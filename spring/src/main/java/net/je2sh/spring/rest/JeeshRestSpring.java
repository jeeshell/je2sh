/*
 * MIT License
 *
 * Copyright (c) 2017 JeeSh
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.je2sh.spring.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import net.je2sh.core.CommandContext;
import net.je2sh.core.plugins.PluginContext;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.ExternalTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@ConditionalOnProperty(prefix = "jeesh.rest", name = "enabled")
@RestController
@RequestMapping("/jeesh")
public class JeeshRestSpring extends PluginContext implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(JeeshRestSpring.class);

    public JeeshRestSpring() throws IOException {
    }

    @RequestMapping("/exec")
    public CommandResponse exec(@RequestBody CommandRequest commandRequest)
            throws IOException
    {
        log.trace("Received request: {}", commandRequest);
        ByteArrayOutputStream resultOutputStream = new ByteArrayOutputStream();

        Terminal cmdTerminal = new ExternalTerminal("Je2sh Terminal", "TERM",
                                                    System.in, resultOutputStream,
                                                    StandardCharsets.UTF_8.name(),
                                                    Terminal.SignalHandler.SIG_DFL);

        List<String> args;
        if (commandRequest.getParams() == null) {
            args = new ArrayList<>();
        }
        else {
            args = new ArrayList<>(commandRequest.getParams());
        }
        args.add(0, commandRequest.getMethod());

        getCommandManager().runCommand(commandRequest.getMethod(),
                                       new CommandContext(this, cmdTerminal),
                                       args.toArray(new String[args.size()]));
        cmdTerminal.writer().flush();
        return new CommandResponse(commandRequest.getId(),
                                   resultOutputStream.toString(StandardCharsets.UTF_8.name()),
                                   null);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        attribute("spring.context", applicationContext);
    }
}

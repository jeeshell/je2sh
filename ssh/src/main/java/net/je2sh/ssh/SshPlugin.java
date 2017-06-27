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

package net.je2sh.ssh;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;

import net.je2sh.core.CommandContext;
import net.je2sh.core.JeeShell;
import net.je2sh.core.plugins.PluginContext;
import net.je2sh.core.plugins.PluginLifeCycle;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.jline.builtins.ssh.ShellCommand;
import org.jline.builtins.ssh.ShellFactoryImpl;
import org.jline.builtins.ssh.Ssh;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Plugin that allows access to the available commands through SSH.
 * <p>
 * This plugin creates a {@link SshServer} and manages its lifecycle.
 */
public class SshPlugin extends PluginLifeCycle implements PasswordAuthenticator {

    private static final Logger log = LoggerFactory.getLogger(SshPlugin.class);
    public static final String PROPERTIES_FILE = "jeesh/jeesh.properties";
    public static final String KEY_FILE = "jeesh/hostkey.pem";
    public static final String PORT_KEY = "jeesh.ssh.port";
    public static final String IP_KEY = "jeesh.ssh.ip";

    private final SshServer server;
    private Principal principal;

    public SshPlugin() throws IOException {
        this(null, null);
    }

    public SshPlugin(Properties properties, Principal principal) throws IOException {
        super(new PluginContext(Executors.newSingleThreadExecutor()));
        getContext().setPrincipal(principal);

        ClassLoader classLoader = this.getClass().getClassLoader();

        if (properties == null) {
            properties = new Properties();
            properties.load(classLoader.getResourceAsStream(PROPERTIES_FILE));
        }

        SimpleGeneratorHostKeyProvider keyPairProvider =
                new SimpleGeneratorHostKeyProvider(new File(
                        classLoader.getResource(KEY_FILE).getPath()));
        Object port = properties.getOrDefault(PORT_KEY, 2022);
        Object ip = properties.getOrDefault(IP_KEY, "127.0.0.1");
        log.debug("Binding SSH Server to {}:{}", ip, port);

        server = SshServer.setUpDefaultServer();
        server.setPort(Integer.valueOf(port.toString()));
        server.setHost((String) ip);
        server.setShellFactory(new ShellFactoryImpl(this::shell));
        server.setCommandFactory(command -> new ShellCommand(this::execute, command));
        server.setKeyPairProvider(keyPairProvider);
        server.setPasswordAuthenticator(this);
    }

    @Override
    public boolean authenticate(String username, String password, ServerSession session)
            throws PasswordChangeRequiredException
    {
        return Objects.equals(username, "admin") && Objects.equals(password, "admin");
    }

    @Override
    public void run() {
        try {
            log.debug("Starting SSH Server");
            server.start();
        }
        catch (IOException e) {
            log.error("Failure running the SSH Server", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            log.debug("Stopping SSH Server");
            server.stop(true);
        }
        catch (IOException e) {
            log.error("Unable to gracefully stop SSH server", e);
        }
        finally {
            super.close();
        }
    }

    private void shell(Ssh.ShellParams params) {
        log.info("Starting shell");
        new JeeShell(getContext(), params.getTerminal(), getContext().getCommandManager())
                .run();
        params.getCloser().run();
    }

    private void execute(Ssh.ExecuteParams params) {
        log.info("Execute received {}", params);
        try {
            Terminal terminal = TerminalBuilder.builder()
                                               .streams(params.getIn(), params.getOut())
                                               .build();
            String[] args = params.getCommand().split(" ");
            getContext().getCommandManager().runCommand(args[0],
                                                        new CommandContext(getContext(), terminal),
                                                        args);
            terminal.flush();
        }
        catch (IOException e) {
            log.error("Unable to execute command", e);
        }
    }

}

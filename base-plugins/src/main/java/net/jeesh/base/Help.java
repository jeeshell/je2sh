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

package net.jeesh.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.beust.jcommander.Parameters;
import net.jeesh.core.AbstractCommand;
import net.jeesh.core.Command;
import net.jeesh.core.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;



@Parameters(commandNames = "help", commandDescription = "Shows the available commands")
public class Help extends AbstractCommand {

    @Override
    public void execute(@NotNull CommandContext context) {
        context.print("List of available commands. ");

        AttributedStringBuilder headerBuilder =
                new AttributedStringBuilder()
                        .append("To find more about a specific command run ")
                        .style(AttributedStyle.BOLD.foreground(AttributedStyle.RED))
                        .append("<command> -h");

        context.getTerminal().writer().println(headerBuilder.toAnsi(context.getTerminal()));

        Map<String, Class<? extends Command>> commandMap =
                context.getPluginContext().getCommandManager().getCommands();

        List<String> commands = new ArrayList<>(commandMap.keySet());
        commands.sort(String::compareTo);

        for (String command : commands) {
            AttributedStringBuilder builder =
                    new AttributedStringBuilder()
                            .append("\t")
                            .style(AttributedStyle.BOLD.foreground(AttributedStyle.RED))
                            .append(command)
                            .append("\t\t")
                            .style(AttributedStyle.DEFAULT.foregroundDefault())
                            .append(getDescription(commandMap.get(command)));
            context.getTerminal().writer().println(builder.toAnsi(context.getTerminal()));
        }
    }

    private static <T extends Command> String getDescription(Class<T> tClass) {
        return Optional.ofNullable(tClass.getAnnotation(Parameters.class))
                       .map(Parameters::commandDescription)
                       .orElse("");

    }
}

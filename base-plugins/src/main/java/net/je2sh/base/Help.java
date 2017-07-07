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

package net.je2sh.base;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.beust.jcommander.Parameters;
import net.je2sh.asciitable.AnsiContentParser;
import net.je2sh.asciitable.JTable;
import net.je2sh.asciitable.style.JPadding;
import net.je2sh.asciitable.style.JTheme;
import net.je2sh.core.AbstractCommand;
import net.je2sh.core.Command;
import net.je2sh.core.CommandContext;
import org.fusesource.jansi.Ansi;
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

        context.println(headerBuilder.toAnsi(context.getTerminal()));

        Map<String, Class<? extends Command>> commandMap =
                context.getPluginContext().getCommandManager().getCommands();

        AnsiContentParser ansiContentParser = new AnsiContentParser();

        JTable table = JTable.of()
                             .width(60)
                             .contentParser(ansiContentParser)
                             .theme(JTheme.NO_LINE);

        Map<String, String> helpMap = new TreeMap<>();
        int maxCommandLength = 1;

        for (Class<? extends Command> command : commandMap.values()) {
            String cmds = getCommandNames(command).stream()
                                                  .map(s -> Ansi.ansi().fgRed().bold().a(s).reset()
                                                                .toString())
                                                  .collect(Collectors.joining(", "));

            helpMap.put(cmds, getDescription(command));
            maxCommandLength = Math.max(maxCommandLength, ansiContentParser.getLength(cmds));
        }

        for (Map.Entry<String, String> entry : helpMap.entrySet()) {
            table.row()
                 .col().width(maxCommandLength + 6)
                 .padding(new JPadding(5, 0, 0, 0, ' '))
                 .content(Ansi.ansi().fgRed().bold().a(entry.getKey()).reset())
                 .done()
                 .col().content(entry.getValue());
        }

        table.render().forEach(context::println);
    }

    private static <T extends Command> List<String> getCommandNames(Class<T> tClass) {
        Parameters paramAnn = tClass.getAnnotation(Parameters.class);
        if (paramAnn.commandNames().length == 0) {
            String simpleName = tClass.getClass().getSimpleName();
            simpleName = Character.toLowerCase(simpleName.charAt(0)) +
                         simpleName.substring(1, simpleName.length());
            return Collections.singletonList(simpleName);
        }
        return Arrays.asList(paramAnn.commandNames());
    }

    private static <T extends Command> String getDescription(Class<T> tClass) {
        return Optional.ofNullable(tClass.getAnnotation(Parameters.class))
                       .map(Parameters::commandDescription)
                       .orElse("");

    }
}

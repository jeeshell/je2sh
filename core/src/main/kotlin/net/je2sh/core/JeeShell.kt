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

package net.je2sh.core

import net.je2sh.core.plugins.PluginContext
import org.jline.reader.EndOfFileException
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import java.lang.Exception

class JeeShell(val context: PluginContext,
               val terminal: Terminal = TerminalBuilder.terminal(),
               val commandManager: CommandManager = context.commandManager) {
    private val EXIT_CMD = "exit"
    private var running = false

    fun run() {
        val prompt: String = "${context.attributes["prompt"]?.toString() ?: context.principal?.name ?: "jeesh"} $ "
        val reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build()
        running = true

        while (running) {

            try {
                val line = reader.readLine(prompt, null, null, null).trim { it <= ' ' }

                if (line.isEmpty()) {
                    continue
                }

                val args = line.split(" ")
                val cmdStr = args[0]

                if (EXIT_CMD == cmdStr) {
                    break
                }

                commandManager.runCommand(cmdStr, CommandContext(context, terminal), *args.toTypedArray())

            } catch (e: UserInterruptException) {
                // Ignore
            } catch (e: EndOfFileException) {
                break
            } catch (e: Exception) {
                terminal.writer().println("Command failed with \"" + e.message + "\"")
            }

        }

        terminal.writer().println("See you next time")
        terminal.writer().flush()
        terminal.close()
    }
}

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

package net.jeesh.core.impl

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import net.jeesh.core.Bootstrap
import net.jeesh.core.Command
import net.jeesh.core.CommandContext
import net.jeesh.core.CommandManager

class DefaultCommandManager(bootstrap: Bootstrap) : CommandManager {

    val commands: Map<String, Class<out Command>>

    init {
        commands = bootstrap.commandProviders.map { it.commands }
                .reduce { acc, mutableMap -> acc + mutableMap }
    }


    override fun runCommand(name: String, context: CommandContext, vararg args: String) {
        val cmdClass = commands[name]
        if (cmdClass != null) {
            val cmdInstance = cmdClass.newInstance()
            try {
                val jCommander = JCommander.newBuilder()
                        .addObject(cmdInstance)
                        .programName(name)
                        .build()

                if (args.size > 1) {
                    jCommander.parse(*args.sliceArray(IntRange(1, args.size - 1)))
                }

                if (cmdInstance.getHelp()) {
                    val usageStr = StringBuilder()
                    jCommander.usage(usageStr)
                    context.println(usageStr)
                } else {
                    cmdInstance.execute(context)
                }
            } catch (e: ParameterException) {
                context.println("Unable to parse command: ${e.message}. Try \"$name -h\" for a list of the available options")
            }
        } else {
            context.println("Command \"$name\" not found. Try \"help\" for a list of available commands")
        }
    }

}

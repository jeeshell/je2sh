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

package net.je2sh.examples.commands

import static net.je2sh.test.TestUtils.mockitoAssert
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.verify

import com.beust.jcommander.ParameterException
import net.je2sh.core.CommandContext
import net.je2sh.core.plugins.PluginContext
import net.je2sh.test.TestUtils
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class HelloTests extends Specification {

    CommandContext commandContext
    Hello cmd

    def setup() {
        commandContext = new CommandContext(Mock(PluginContext), TestUtils.mockedTerminal())
        cmd = new Hello()
    }

    def 'Wrong command "#cmdStr #arguments" should fail'() {
        when:
        TestUtils.parseCommand(cmdStr, cmd, (arguments as String).split(' '))
        cmd.execute(commandContext)

        then:
        thrown(ParameterException)

        where:
        [cmdStr, arguments] << [
                ['hello', 'hi'],
                [
                        '-n',
                        '-a',
                        'something',
                        '-n arg with spaces',
                        '--name arg with spaces',
                ]
        ].combinations()
    }

    def 'No argument should print world'() {
        when:
        cmd.execute(commandContext)

        then:
        mockitoAssert(verify(commandContext.terminal.writer()).println(eq('Hello World')))
    }

    def 'Command "#cmdStr #arguments" should succeed'() {
        given:
        TestUtils.parseCommand(cmdStr, cmd, (arguments as String).split(' '))

        when:
        cmd.execute(commandContext)

        then:
        mockitoAssert(verify(commandContext.terminal.writer()).println(eq("Hello $arg" as String)))

        where:
        [cmdStr, param, arg] << [
                ['hello', 'hi'],
                ['-n', '--name'],
                ['myself']
        ].combinations()
        arguments = "$param $arg"
    }

}


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

package net.je2sh.base

import static net.je2sh.test.TestUtils.mockitoAssert
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.verify

import com.beust.jcommander.ParameterException
import net.je2sh.core.CommandContext
import net.je2sh.core.plugins.PluginContext
import net.je2sh.test.TestUtils
import spock.lang.Specification

class EchoTests extends Specification {

    CommandContext commandContext
    Echo cmd

    def setup() {
        commandContext = new CommandContext(Mock(PluginContext), TestUtils.mockedTerminal())
        cmd = new Echo()
    }

    def 'No arguments should fail'() {
        when:
        cmd.execute(commandContext)

        then:
        thrown(ParameterException)
    }

    def 'Message "#arguments" should echo values'() {
        given:
        TestUtils.parseCommand('echo', cmd, arguments)

        when:
        cmd.execute(commandContext)

        then:
        mockitoAssert(verify(commandContext.terminal.writer()).println(eq(arguments)))

        where:
        arguments << [
                'something',
                'multiple spaces are seen',
                '-o option non-existent'
        ]
    }

}

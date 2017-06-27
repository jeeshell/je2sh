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

import com.beust.jcommander.Parameters
import org.jetbrains.annotations.NotNull
import spock.lang.Specification

class CommandTests extends Specification {

    static class NoParametersCommand extends AbstractCommand {
        @Override
        void execute(@NotNull CommandContext context) {
            // noop
        }
    }

    @Parameters
    static class NoDescriptionCommand extends AbstractCommand {
        @Override
        void execute(@NotNull CommandContext context) {
            // noop
        }
    }

    @Parameters(commandDescription = 'A sample command')
    static class WithDescriptionCommand extends AbstractCommand {
        @Override
        void execute(@NotNull CommandContext context) {
            // noop
        }
    }

    def 'Command without @Parameters should return empty string'() {
        expect:
        new NoParametersCommand().description == ''
    }

    def 'Command without @Parameters description should return empty string'() {
        expect:
        new NoDescriptionCommand().description == ''
    }

    def 'Command with @Parameters description should return description'() {
        expect:
        new WithDescriptionCommand().description == 'A sample command'
    }


}

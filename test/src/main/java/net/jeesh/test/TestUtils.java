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

package net.jeesh.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;

import com.beust.jcommander.JCommander;
import net.jeesh.core.AbstractCommand;
import org.jline.terminal.Terminal;



public class TestUtils {

    public static void parseCommand(String name, AbstractCommand cmd, String... args) {
        JCommander.newBuilder()
                  .addObject(cmd)
                  .programName(name)
                  .build()
                  .parse(args);
    }

    public static Terminal mockedTerminal() {
        return mockedTerminal(mockedWriter());
    }

    public static Terminal mockedTerminal(PrintWriter writer) {
        Terminal mockedTerminal = mock(Terminal.class);
        when(mockedTerminal.writer()).thenReturn(writer);
        return mockedTerminal;
    }

    public static PrintWriter mockedWriter() {
        return mock(PrintWriter.class);
    }

    public static boolean mockitoAssert(Object result) {
        return result == null;
    }

    public static Integer findAvailablePort() {
        while(true) {
            try (ServerSocket socket = new ServerSocket(0)) {
                return socket.getLocalPort();
            }
            catch (IOException e) {
                // noop
            }
        }
    }
}

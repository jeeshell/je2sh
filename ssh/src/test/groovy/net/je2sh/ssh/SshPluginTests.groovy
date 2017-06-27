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

package net.je2sh.ssh

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import groovy.util.logging.Slf4j
import net.je2sh.test.TestUtils
import org.apache.commons.io.IOUtils
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Slf4j
class SshPluginTests extends Specification {

    @Shared
    SshPlugin sshPlugin

    @Shared
    Integer sshdPort

    Session session
    ChannelExec channel

    def setupSpec() {
        sshdPort = TestUtils.findAvailablePort()
        log.info("Starting test server on port {}", sshdPort)
        def props = new Properties()
        props.putAll([(SshPlugin.PORT_KEY): sshdPort])
        sshPlugin = new SshPlugin(props, null)
        sshPlugin.start()
    }

    def cleanupSpec() {
        sshPlugin?.close()
    }

    def setup() {
        String host = '127.0.0.1'
        String user = 'admin'
        JSch jsch = new JSch();
        session = jsch.getSession(user, host, sshdPort)
        session.password = 'admin'
        session.setConfig("StrictHostKeyChecking", "no");

        session.connect()

        channel = session.openChannel('exec')
    }

    def cleanup() {
        session.disconnect()
    }

    @Unroll
    def 'Running "#inputStr" should produce "#outputStr"'() {
        when:
        channel.command = inputStr
        channel.inputStream = null
        channel.connect()

        def lines = IOUtils.readLines(channel.inputStream)

        channel.disconnect()

        then:
        lines[0] == outputStr

        where:
        inputStr           | outputStr
        'hello'            | 'Hello World'
        'hi -n test'       | 'Hello test'
        'echo from a test' | 'from a test'
    }

}

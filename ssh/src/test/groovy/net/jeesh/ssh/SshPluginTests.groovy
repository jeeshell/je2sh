package net.jeesh.ssh

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import groovy.util.logging.Slf4j
import net.jeesh.test.TestUtils
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

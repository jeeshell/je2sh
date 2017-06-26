package net.jeesh.base

import static net.jeesh.test.TestUtils.mockitoAssert
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.verify

import com.beust.jcommander.ParameterException
import net.jeesh.core.CommandContext
import net.jeesh.core.plugins.PluginContext
import net.jeesh.test.TestUtils
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

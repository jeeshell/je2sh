package net.jeesh.base

import static net.jeesh.test.TestUtils.mockitoAssert
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.verify

import com.beust.jcommander.ParameterException
import net.jeesh.core.CommandContext
import net.jeesh.core.plugins.PluginContext
import net.jeesh.test.TestUtils
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


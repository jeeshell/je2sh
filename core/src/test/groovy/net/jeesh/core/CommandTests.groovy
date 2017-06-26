package net.jeesh.core

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

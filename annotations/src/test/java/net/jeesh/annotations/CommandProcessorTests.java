package net.jeesh.annotations;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;



public class CommandProcessorTests {

    @Test
    public void commandWithoutAnnotationShouldFailt() {
        assertAbout(javaSource())
                .that(CLASS_WITHOUT_SUPER)
                .processedWith(new CommandProcessor())
                .failsToCompile()
                .withErrorContaining("Class TestingCommand does not implement");
    }

    @Test
    public void commandWithoutNameUsesClassName() {
        assertAbout(javaSource())
                .that(CLASS_WITHOUT_NAMES)
                .processedWith(new CommandProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects.forSourceString(
                        "net.jeesh.annotations.provider.CommandProviderImpl.java",
                        "package net.jeesh.annotations.provider;\n" +
                        "\n" +
                        "import com.google.auto.service.AutoService;\n" +
                        "import java.lang.Class;\n" +
                        "import java.lang.Override;\n" +
                        "import java.lang.String;\n" +
                        "import java.util.HashMap;\n" +
                        "import java.util.Map;\n" +
                        "import net.jeesh.annotations.TestingCommand;\n" +
                        "import net.jeesh.core.Command;\n" +
                        "import net.jeesh.core.CommandProvider;\n" +
                        "\n" +
                        "@AutoService(CommandProvider.class)\n" +
                        "public final class CommandProviderImpl implements CommandProvider {\n" +
                        "  @Override\n" +
                        "  public Map<String, Class<? extends Command>> getCommands() {\n" +
                        "    Map<String, Class<? extends Command>> knownTypes = new HashMap<>();\n" +
                        "    knownTypes.put(\"testingCommand\", TestingCommand.class);\n" +
                        "    return knownTypes;\n" +
                        "  }\n" +
                        "}"));
    }

    @Test
    public void commandWithAnnotationShoudSucceed() {
        assertAbout(javaSource())
                .that(CLASS_WITH_ANNOTATION)
                .processedWith(new CommandProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects.forSourceString(
                        "net.jeesh.annotations.provider.CommandProviderImpl.java",
                        "package net.jeesh.annotations.provider;\n" +
                        "\n" +
                        "import com.google.auto.service.AutoService;\n" +
                        "import java.lang.Class;\n" +
                        "import java.lang.Override;\n" +
                        "import java.lang.String;\n" +
                        "import java.util.HashMap;\n" +
                        "import java.util.Map;\n" +
                        "import net.jeesh.annotations.TestingCommand;\n" +
                        "import net.jeesh.core.Command;\n" +
                        "import net.jeesh.core.CommandProvider;\n" +
                        "\n" +
                        "@AutoService(CommandProvider.class)\n" +
                        "public final class CommandProviderImpl implements CommandProvider {\n" +
                        "  @Override\n" +
                        "  public Map<String, Class<? extends Command>> getCommands() {\n" +
                        "    Map<String, Class<? extends Command>> knownTypes = new HashMap<>();\n" +
                        "    knownTypes.put(\"testing\", TestingCommand.class);\n" +
                        "    return knownTypes;\n" +
                        "  }\n" +
                        "}"));
    }

    /* ### Utility constants ### */

    private static final JavaFileObject CLASS_WITH_ANNOTATION =
            JavaFileObjects.forSourceLines("net.jeesh.annotations.TestingCommand",
                                           "package net.jeesh.annotations;\n" +
                                           "\n" +
                                           "import com.beust.jcommander.Parameters;\n" +
                                           "import net.jeesh.core.AbstractCommand;\n" +
                                           "import net.jeesh.core.CommandContext;\n" +
                                           "import org.jetbrains.annotations.NotNull;\n" +
                                           "\n" +
                                           "\n" +
                                           "@Parameters(commandNames = \"testing\")\n" +
                                           "public class TestingCommand extends AbstractCommand {\n" +
                                           "\n" +
                                           "    @Override\n" +
                                           "    public void execute(@NotNull CommandContext context) {\n" +
                                           "        // noop\n" +
                                           "    }\n" +
                                           "}");

    private static final JavaFileObject CLASS_WITHOUT_SUPER =
            JavaFileObjects.forSourceLines("net.jeesh.annotations.TestingCommand",
                                           "package net.jeesh.annotations;\n" +
                                           "\n" +
                                           "import com.beust.jcommander.Parameters;\n" +
                                           "import net.jeesh.core.AbstractCommand;\n" +
                                           "import net.jeesh.core.CommandContext;\n" +
                                           "import org.jetbrains.annotations.NotNull;\n" +
                                           "\n" +
                                           "@Parameters(commandNames = \"testing\")\n" +
                                           "public class TestingCommand {\n" +
                                           "\n" +
                                           "    public void execute(@NotNull CommandContext context) {\n" +
                                           "        // noop\n" +
                                           "    }\n" +
                                           "}\n" +
                                           "\n" +
                                           "");

    private static final JavaFileObject CLASS_WITHOUT_NAMES =
            JavaFileObjects.forSourceLines("net.jeesh.annotations.TestingCommand",
                                           "\n" +
                                           "package net.jeesh.annotations;\n" +
                                           "\n" +
                                           "import com.beust.jcommander.Parameters;\n" +
                                           "import net.jeesh.core.AbstractCommand;\n" +
                                           "import net.jeesh.core.CommandContext;\n" +
                                           "import org.jetbrains.annotations.NotNull;\n" +
                                           "\n" +
                                           "@Parameters\n" +
                                           "public class TestingCommand extends AbstractCommand {\n" +
                                           "\n" +
                                           "    @Override\n" +
                                           "    public void execute(@NotNull CommandContext context) {\n" +
                                           "        // noop\n" +
                                           "    }\n" +
                                           "}");
}

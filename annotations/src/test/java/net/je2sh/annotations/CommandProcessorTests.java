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

package net.je2sh.annotations;

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
                        "net.je2sh.annotations.provider.CommandProviderImpl.java",
                        "package net.je2sh.annotations.provider;\n" +
                        "\n" +
                        "import com.google.auto.service.AutoService;\n" +
                        "import java.lang.Class;\n" +
                        "import java.lang.Override;\n" +
                        "import java.lang.String;\n" +
                        "import java.util.HashMap;\n" +
                        "import java.util.Map;\n" +
                        "import net.je2sh.annotations.TestingCommand;\n" +
                        "import net.je2sh.core.Command;\n" +
                        "import net.je2sh.core.CommandProvider;\n" +
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
                        "net.je2sh.annotations.provider.CommandProviderImpl.java",
                        "package net.je2sh.annotations.provider;\n" +
                        "\n" +
                        "import com.google.auto.service.AutoService;\n" +
                        "import java.lang.Class;\n" +
                        "import java.lang.Override;\n" +
                        "import java.lang.String;\n" +
                        "import java.util.HashMap;\n" +
                        "import java.util.Map;\n" +
                        "import net.je2sh.annotations.TestingCommand;\n" +
                        "import net.je2sh.core.Command;\n" +
                        "import net.je2sh.core.CommandProvider;\n" +
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
            JavaFileObjects.forSourceLines("net.je2sh.annotations.TestingCommand",
                                           "package net.je2sh.annotations;\n" +
                                           "\n" +
                                           "import com.beust.jcommander.Parameters;\n" +
                                           "import net.je2sh.core.AbstractCommand;\n" +
                                           "import net.je2sh.core.CommandContext;\n" +
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
            JavaFileObjects.forSourceLines("net.je2sh.annotations.TestingCommand",
                                           "package net.je2sh.annotations;\n" +
                                           "\n" +
                                           "import com.beust.jcommander.Parameters;\n" +
                                           "import net.je2sh.core.AbstractCommand;\n" +
                                           "import net.je2sh.core.CommandContext;\n" +
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
            JavaFileObjects.forSourceLines("net.je2sh.annotations.TestingCommand",
                                           "\n" +
                                           "package net.je2sh.annotations;\n" +
                                           "\n" +
                                           "import com.beust.jcommander.Parameters;\n" +
                                           "import net.je2sh.core.AbstractCommand;\n" +
                                           "import net.je2sh.core.CommandContext;\n" +
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

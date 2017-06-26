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

package net.jeesh.annotations;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.beust.jcommander.Parameters;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import net.jeesh.core.Command;
import net.jeesh.core.CommandProvider;



/**
 * Annotation processor capable of understanding classes annotated with {@link Parameters} and
 * generating a {@link CommandProvider} for such commands.
 * <p>
 * This processor uses <a href="https://github.com/square/javapoet">JavaPoet</a> to build
 * the new class(es).
 */
@SupportedAnnotationTypes("com.beust.jcommander.Parameters")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CommandProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Map<String, TypeMirror> types = new HashMap<>();
        TypeMirror expectedInterfaceType =
                processingEnv.getElementUtils().getTypeElement(Command.class.getCanonicalName())
                             .asType();
        PackageElement pkg = null;
        for (TypeElement a : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(a)) {
                Parameters parameters =
                        Objects.requireNonNull(element.getAnnotation(Parameters.class));

                TypeMirror typeMirror = element.asType();

                if (pkg == null) {
                    pkg = processingEnv.getElementUtils().getPackageOf(element);
                }

                if (!processingEnv.getTypeUtils().isAssignable(typeMirror, expectedInterfaceType)) {
                    processingEnv.getMessager()
                                 .printMessage(Diagnostic.Kind.ERROR,
                                               String.format("Class %s does not implement %s",
                                                             element.getSimpleName(),
                                                             Command.class.getCanonicalName()));
                }

                for (String commandName : extractCommandNames(element.getSimpleName(),
                                                              parameters.commandNames())) {
                    types.put(commandName, typeMirror);
                }
            }
        }

        if (!types.isEmpty()) {
            try {
                writeFile(types, pkg);
            }
            catch (IOException e) {
                processingEnv.getMessager()
                             .printMessage(Diagnostic.Kind.ERROR,
                                           "Unable to process jeesh annotations: " +
                                           e.getMessage());
            }
        }
        return false;
    }

    private void writeFile(Map<String, TypeMirror> types,
                           PackageElement pkg) throws IOException
    {
        TypeName wildcardClass = ParameterizedTypeName.get(ClassName.get(Class.class),
                                                           WildcardTypeName
                                                                   .subtypeOf(Command.class));
        ParameterizedTypeName returnType =
                ParameterizedTypeName.get(ClassName.get(Map.class),
                                          ClassName.get(String.class), wildcardClass);

        MethodSpec.Builder methodBuilder =
                MethodSpec.methodBuilder("getCommands")
                          .addAnnotation(Override.class)
                          .addModifiers(Modifier.PUBLIC)
                          .returns(returnType)
                          .addStatement("$T knownTypes = new $T<>()",
                                        returnType, ClassName.get(HashMap.class));

        for (Map.Entry<String, TypeMirror> entry : types.entrySet()) {
            methodBuilder.addStatement("knownTypes.put($S, $T.class)", entry.getKey(),
                                       entry.getValue());
        }

        MethodSpec getcommands = methodBuilder.addStatement("return knownTypes")
                                              .build();

        AnnotationSpec autoServiceAnnotation =
                AnnotationSpec.builder(AutoService.class)
                              .addMember("value", "$T.class", CommandProvider.class)
                              .build();

        TypeSpec commandProvider = TypeSpec.classBuilder("CommandProviderImpl")
                                           .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                           .addSuperinterface(CommandProvider.class)
                                           .addAnnotation(autoServiceAnnotation)
                                           .addMethod(getcommands)
                                           .build();

        JavaFile javaFile = JavaFile.builder(pkg.getQualifiedName().toString() + ".provider",
                                             commandProvider).build();
        javaFile.writeTo(processingEnv.getFiler());
    }

    private List<String> extractCommandNames(Name elementName, String[] commandNames) {
        if (commandNames.length == 0) {
            String className = elementName.toString();
            StringBuilder builder = new StringBuilder()
                    .append(Character.toLowerCase(className.charAt(0)))
                    .append(className.substring(1, className.length()));

            return Collections.singletonList(builder.toString());
        }
        return Arrays.asList(commandNames);
    }

}

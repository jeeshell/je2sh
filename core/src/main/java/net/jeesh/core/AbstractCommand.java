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

package net.jeesh.core;

import java.util.Optional;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.jetbrains.annotations.NotNull;



/**
 * Abstract implementations of a {@link Command}. Most/All commands should extend this class.
 * <p>
 * Automatically adds the command switches {@code -h} and {@code --help} to extending command.
 * <p>
 * The contents of "help" are the ones specified in {@link Parameters#commandDescription()} or
 * empty string if none.
 *
 * @see Command
 */
public abstract class AbstractCommand implements Command {

    @Parameter(names = { "-h", "--help" }, help = true, description = "Shows this help")
    private boolean help;

    @Override
    public boolean getHelp() {
        return help;
    }

    @NotNull
    @Override
    public String getDescription() {
        return Optional.ofNullable(this.getClass().getAnnotation(Parameters.class))
                       .map(Parameters::commandDescription)
                       .orElse("");
    }
}

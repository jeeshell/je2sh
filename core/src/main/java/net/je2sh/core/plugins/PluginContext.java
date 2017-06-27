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

package net.je2sh.core.plugins;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import net.je2sh.core.Bootstrap;
import net.je2sh.core.CommandManager;
import net.je2sh.core.impl.DefaultCommandManager;



/**
 * Context holder used by plugins.
 * <p>
 * Provides relevant information and objects to the plugins.
 *
 * @see CommandManager
 */
public class PluginContext {

    private final DefaultCommandManager commandManager;
    private final ExecutorService executor;
    private final Map<String, Object> attributes;

    private Principal principal;

    public PluginContext() throws IOException {
        this(new DefaultCommandManager(new Bootstrap()), null);
    }

    public PluginContext(ExecutorService executor) throws IOException {
        this(new DefaultCommandManager(new Bootstrap()), executor);
    }

    public PluginContext(DefaultCommandManager commandManager, ExecutorService executor)
    {
        this.commandManager = commandManager;
        this.executor = executor;
        this.attributes = new HashMap<>();
    }

    public DefaultCommandManager getCommandManager() {
        return commandManager;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void attribute(String name, Object val) {
        attributes.put(name, val);
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }
}

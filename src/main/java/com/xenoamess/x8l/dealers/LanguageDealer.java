/*
 * MIT License
 *
 * Copyright (c) 2019 XenoAmess
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

package com.xenoamess.x8l.dealers;

import com.xenoamess.x8l.AbstractTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * AbstractLanguageDealer
 *
 * @author XenoAmess
 * @version 2.2.2
 */
public class LanguageDealer implements Serializable {

    @SuppressWarnings("rawtypes")
    private final Map<Class, AbstractLanguageDealerHandler> treeNodeHandlerMap = new HashMap<>();

    /**
     * <p>getTreeNodeHandler.</p>
     *
     * @param tClass a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a {@link com.xenoamess.x8l.dealers.AbstractLanguageDealerHandler} object.
     */
    public <T extends AbstractTreeNode> @Nullable AbstractLanguageDealerHandler<T> getTreeNodeHandler(@Nullable Class<T> tClass) {
        //noinspection rawtypes
        AbstractLanguageDealerHandler handler = null;
        //noinspection rawtypes
        Class nowClass = tClass;
        while (handler == null && nowClass != null) {
            handler = treeNodeHandlerMap.get(nowClass);
            nowClass = nowClass.getSuperclass();
        }
        //noinspection unchecked
        return handler;
    }

    /**
     * <p>registerTreeNodeHandler.</p>
     *
     * @param tClass a {@link java.lang.Class} object.
     * @param handler a {@link com.xenoamess.x8l.dealers.AbstractLanguageDealerHandler} object.
     * @param <T> a T object.
     * @return a {@link com.xenoamess.x8l.dealers.AbstractLanguageDealerHandler} object.
     */
    @SuppressWarnings("UnusedReturnValue")
    public <T extends AbstractTreeNode> AbstractLanguageDealerHandler<T> registerTreeNodeHandler(@NotNull Class<T> tClass,
                                                                                                 @NotNull AbstractLanguageDealerHandler<T> handler) {
        //noinspection unchecked
        return treeNodeHandlerMap.put(tClass, handler);
    }

    /**
     * <p>read.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     * @param abstractTreeNode a T object.
     * @param <T> a T object.
     * @return a boolean.
     * @throws java.io.IOException if any.
     */
    @SuppressWarnings("UnusedReturnValue")
    public <T extends AbstractTreeNode> boolean read(@NotNull Reader reader, @NotNull T abstractTreeNode) throws IOException {
        //noinspection unchecked
        AbstractLanguageDealerHandler<T> handler =
                (AbstractLanguageDealerHandler<T>) this.getTreeNodeHandler(abstractTreeNode.getClass());
        if (handler == null) {
            return false;
        }
        return handler.read(reader, abstractTreeNode);
    }

    /**
     * <p>write.</p>
     *
     * @param writer a {@link java.io.Writer} object.
     * @param abstractTreeNode a T object.
     * @param <T> a T object.
     * @return a boolean.
     * @throws java.io.IOException if any.
     */
    @SuppressWarnings("UnusedReturnValue")
    public <T extends AbstractTreeNode> boolean write(@NotNull Writer writer, @NotNull T abstractTreeNode) throws IOException {
        //noinspection unchecked
        AbstractLanguageDealerHandler<T> handler =
                (AbstractLanguageDealerHandler<T>) this.getTreeNodeHandler(abstractTreeNode.getClass());
        if (handler == null) {
            return false;
        }
        return handler.write(writer, abstractTreeNode);
    }

}

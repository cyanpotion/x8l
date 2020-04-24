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

package com.xenoamess.x8l;

import com.xenoamess.x8l.databind.X8lDataBeanFieldScheme;
import com.xenoamess.x8l.databind.x8lpath.X8lPathUtil;
import com.xenoamess.x8l.dealers.LanguageDealer;
import com.xenoamess.x8l.dealers.X8lDealer;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * AbstractTreeNode
 * parent of all TreeNode types.
 *
 * @author XenoAmess
 */
public abstract class AbstractTreeNode implements AutoCloseable {
    private ContentNode parent;

    public AbstractTreeNode(ContentNode parent) {
        this.setParent(parent);
        if (this.getParent() != null) {
            this.getParent().getChildren().add(this);
        }
    }

    public AbstractTreeNode(ContentNode parent, int index) {
        this.setParent(parent);
        if (this.getParent() != null) {
            this.getParent().getChildren().add(index, this);
        }
    }


    public void show() {
        System.out.println();
        System.out.println("show!");
        System.out.println("self : " + this);
        System.out.println("parent : " + this.getParent());
    }

    /**
     * clear the node.
     * <p>
     * close and delete all children.
     * delete all attributes.
     */
    public abstract void clear();

    /**
     * clear the node if need.
     * <p>
     * then, remove this node from parent node.
     */
    @Override
    public void close() {
        this.clear();
        this.removeParent();
    }

    public AbstractTreeNode removeParent() {
        if (this.getParent() != null) {
            this.getParent().removeChild(this);
            this.setParent(null);
        }
        return this;
    }

    /**
     * delete this from old parent's children.
     * then set this node's parent to new parent node.
     *
     * @param contentNode new parent node.
     * @return this
     */
    public AbstractTreeNode changeParent(ContentNode contentNode) {
        this.removeParent();
        this.setParent(contentNode);
        return this;
    }

    /**
     * delete this from old parent's children.
     * then set this node's parent to new parent node.
     * <p>
     * then add this node into new parent node's children's assigned position.
     *
     * @param contentNode new parent node.
     * @param index       insert index of this node into new parent's children
     * @return this
     */
    public AbstractTreeNode changeParentAndRegister(ContentNode contentNode, int index) {
        this.changeParent(contentNode);
        if (index == -1) {
            this.getParent().getChildren().add(this);
        } else {
            this.getParent().getChildren().add(index, this);
        }
        return this;
    }

    public AbstractTreeNode changeParentAndRegister(ContentNode contentNode) {
        return this.changeParentAndRegister(contentNode, -1);
    }

    /**
     * write this AbstractTreeNode's data to a writer.
     *
     * @param writer the writer to write to.
     * @throws IOException IOException
     */
    public void write(Writer writer) throws IOException {
        this.write(writer, X8lDealer.INSTANCE);
    }

    /**
     * write this AbstractTreeNode's data to a writer.
     *
     * @param writer         the writer to write to.
     * @param languageDealer the languageDealer to deal with this.
     * @throws IOException IOException
     */
    public void write(Writer writer, LanguageDealer languageDealer) throws IOException {
        languageDealer.write(writer, this);
    }

    /**
     * format the tree node.
     *
     * @param space spaces before the node.
     *              if space is less than 0 then no space is before the node.
     */
    public abstract void format(int space);

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        String res = "";
        try (
                StringWriter stringWriter = new StringWriter()
        ) {
            this.write(stringWriter);
            res = stringWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * make a deep copy of this node.
     *
     * @return a deep copy of this
     */
    public abstract AbstractTreeNode copy();

    public ContentNode getParent() {
        return parent;
    }

    public void setParent(ContentNode parent) {
        this.parent = parent;
    }

    //---fetch---

    public @NotNull List<Object> fetch(@NotNull String x8lPath) {
        return this.fetch(X8lDataBeanFieldScheme.X8LPATH, x8lPath);
    }

    public <T> @NotNull List<T> fetch(@NotNull String x8lPath, @NotNull Class<T> tClass) {
        return this.fetch(X8lDataBeanFieldScheme.X8LPATH, x8lPath, tClass);
    }

    public @NotNull List<Object> fetch(@NotNull X8lDataBeanFieldScheme x8lDataBeanFieldScheme,
                                       @NotNull String x8lPath) {
        switch (x8lDataBeanFieldScheme) {
            case X8LPATH:
                return X8lPathUtil.fetch(this, x8lPath);
            default:
                throw new NotImplementedException("" + x8lDataBeanFieldScheme.name() + " is not implemented yet");
        }
    }

    public <T> @NotNull List<T> fetch(
            @NotNull X8lDataBeanFieldScheme x8lDataBeanFieldScheme,
            @NotNull String x8lPath,
            @NotNull Class<T> tClass
    ) {
        switch (x8lDataBeanFieldScheme) {
            case X8LPATH:
                return X8lPathUtil.fetch(this, x8lPath, tClass);
            default:
                return null;
        }
    }
}

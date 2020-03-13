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

import com.xenoamess.x8l.X8lTest;
import com.xenoamess.x8l.X8lTree;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class XmlDealerTest {
    @Test
    @Deprecated
    public void naiveWriteTest() throws IOException {
        X8lTree tree = new X8lTest().prepare();
        try (StringWriter writer = new StringWriter()) {
            XmlDealer.INSTANCE.naiveWrite(writer, tree.getRoot());
            System.out.println(writer);
            assertNotEquals(writer.toString(), "");
        }

        tree.setLanguageDealer(X8lDealer.INSTANCE);
        try (Writer writer = new StringWriter()) {
            tree.applyToAllNodes(abstractTreeNode -> {
                try {
                    abstractTreeNode.write(writer, tree.getLanguageDealer());
                } catch (IOException e) {
                }
                return null;
            });
        }
        tree.setLanguageDealer(XmlDealer.INSTANCE);
        try (Writer writer = new StringWriter()) {
            tree.applyToAllNodes(abstractTreeNode -> {
                try {
                    abstractTreeNode.write(writer, tree.getLanguageDealer());
                } catch (IOException e) {
                }
                return null;
            });
        }
        tree.setLanguageDealer(XmlDealer.INSTANCE);
        try (Writer writer = new StringWriter()) {
            tree.applyToAllNodes(abstractTreeNode -> {
                try {
                    XmlDealer.INSTANCE.naiveWrite(writer, abstractTreeNode);
                } catch (IOException e) {
                }
                return null;
            });
        }
        tree.setLanguageDealer(JsonDealer.INSTANCE);
        try (Writer writer = new StringWriter()) {
            tree.applyToAllNodes(abstractTreeNode -> {
                try {
                    abstractTreeNode.write(writer, tree.getLanguageDealer());
                } catch (IOException e) {
                }
                return null;
            });
        }
    }
}

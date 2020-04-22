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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author XenoAmess
 */
public class CommentNodeTest {
    @Test
    public void test() {
        X8lTree tree;
        CommentNode t1;
        CommentNode t2;

        tree = new X8lTree();
        t1 = new CommentNode(tree.getRoot(), "1");
        t2 = new CommentNode(tree.getRoot(), 1, "2");
        assertTrue(tree.getRoot().getChildren().indexOf(t1) < tree.getRoot().getChildren().indexOf(t2));
        assertEquals(tree.toString(), "<<1><<2>");
        System.out.println(tree);

        tree = new X8lTree();
        t1 = new CommentNode(tree.getRoot(), "1");
        t2 = new CommentNode(tree.getRoot(), 0, "2");
        assertTrue(tree.getRoot().getChildren().indexOf(t1) > tree.getRoot().getChildren().indexOf(t2));
        assertEquals(tree.toString(), "<<2><<1>");
        System.out.println(tree);

        try {
            tree = new X8lTree();
            t1 = new CommentNode(tree.getRoot(), "1");
            t2 = new CommentNode(tree.getRoot(), 2, "2");
            assertTrue(tree.getRoot().getChildren().indexOf(t1) > tree.getRoot().getChildren().indexOf(t2));
            System.out.println(tree);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
        }

        assertNotEquals(new CommentNode(null, null), new TextNode(null, null));
        t1 = new CommentNode(tree.getRoot(), "1");
        t2 = new CommentNode(null, "1");
        assertEquals(t1.hashCode(), t2.hashCode());

    }
}

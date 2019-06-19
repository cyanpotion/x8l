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

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author XenoAmess
 */
public class ContentNodeTest {
    @Test
    public void test() throws IOException {
        X8lTree tree = new X8lTest().prepare();
        ContentNode contentNode =
                tree.getRoot().getContentNodesFromChildren().get(tree.getRoot().getContentNodesFromChildren().size()
                        - 1);
        while (!contentNode.getAttributes().isEmpty()) {
            String key = contentNode.getAttributesKeyList().get(0);
            contentNode.removeAttribute(key);
            assert (!contentNode.getAttributes().containsKey(key));
            assert (!contentNode.getAttributesKeyList().contains(key));
        }

        List<TextNode> textNodes = contentNode.getTextNodesFromChildren();
        for (TextNode au : textNodes) {
            contentNode.append(au);
        }

        List<CommentNode> commentNodes = contentNode.getCommentNodesFromChildren();
        for (CommentNode au : commentNodes) {
            contentNode.append(au);
        }

        contentNode.append(contentNode);
        contentNode.append(null);
        contentNode.appendAll(contentNode.getChildren());
        contentNode.appendAll(null);
        assertFalse(contentNode.removeChild(null));
    }
}

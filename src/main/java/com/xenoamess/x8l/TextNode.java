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

/**
 * TextNode
 * TextNode means some nodes with text.
 *
 * @author XenoAmess
 */
public class TextNode extends AbstractTreeNode {
    private String textContent;

    public TextNode(ContentNode parent, String textContent) {
        super(parent);
        if (textContent == null) {
            textContent = "";
        }
        this.setTextContent(textContent);
    }

    public TextNode(ContentNode parent, int index, String textContent) {
        super(parent, index);
        if (textContent == null) {
            textContent = "";
        }
        this.setTextContent(textContent);
    }


    @Override
    public void show() {
        super.show();
        System.out.println("textContent : " + this.getTextContent());
    }

    @Override
    public void clear() {
        this.setTextContent(null);
    }

    @Override
    public void format(int space) {
        this.setTextContent(this.getTextContent().trim());
    }

    @Override
    public TextNode copy() {
        return new TextNode(null, this.textContent);
    }

    @Override
    public boolean equals(Object treeNode) {
        if (treeNode == null) {
            return false;
        }
        if (!treeNode.getClass().equals(this.getClass())) {
            return false;
        }
        return textContent.equals(((TextNode) treeNode).textContent);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}

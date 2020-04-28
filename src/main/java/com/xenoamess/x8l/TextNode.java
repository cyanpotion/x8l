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
 * @version 2.2.3-SNAPSHOT
 */
public class TextNode extends AbstractTreeNode {
    private String textContent;

    /**
     * <p>Constructor for TextNode.</p>
     *
     * @param parent a {@link com.xenoamess.x8l.ContentNode} object.
     * @param textContent a {@link java.lang.String} object.
     */
    public TextNode(ContentNode parent, String textContent) {
        super(parent);
        if (textContent == null) {
            textContent = "";
        }
        this.setTextContent(textContent);
    }

    /**
     * <p>Constructor for TextNode.</p>
     *
     * @param parent a {@link com.xenoamess.x8l.ContentNode} object.
     * @param index a int.
     * @param textContent a {@link java.lang.String} object.
     */
    public TextNode(ContentNode parent, int index, String textContent) {
        super(parent, index);
        if (textContent == null) {
            textContent = "";
        }
        this.setTextContent(textContent);
    }


    /** {@inheritDoc} */
    @Override
    public void show() {
        super.show();
        System.out.println("textContent : " + this.getTextContent());
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        this.setTextContent(null);
    }

    /** {@inheritDoc} */
    @Override
    public void format(int space) {
        this.setTextContent(this.getTextContent().trim());
    }

    /** {@inheritDoc} */
    @Override
    public TextNode copy() {
        return new TextNode(null, this.textContent);
    }

    /** {@inheritDoc} */
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

    /**
     * <p>Getter for the field <code>textContent</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTextContent() {
        return textContent;
    }

    /**
     * <p>Setter for the field <code>textContent</code>.</p>
     *
     * @param textContent a {@link java.lang.String} object.
     */
    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}

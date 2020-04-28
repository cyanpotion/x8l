/*
 * MIT License
 *
 * Copyright (c) 2020 XenoAmess
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
 * <p>RootNode class.</p>
 *
 * @author XenoAmess
 * @version 2.2.2
 */
public class RootNode extends ContentNode {
    /**
     * <p>Constructor for RootNode.</p>
     *
     * @param parent a {@link com.xenoamess.x8l.ContentNode} object.
     */
    public RootNode(ContentNode parent) {
        super(parent);
    }

    /**
     * <p>Constructor for RootNode.</p>
     *
     * @param parent a {@link com.xenoamess.x8l.ContentNode} object.
     * @param index a int.
     */
    public RootNode(ContentNode parent, int index) {
        super(parent, index);
    }

    /** {@inheritDoc} */
    @Override
    public RootNode copy() {
        RootNode res = new RootNode(null);
        for (String attributeKey : this.getAttributesKeyList()) {
            String attributeValue = this.getAttributes().get(attributeKey);
            res.addAttribute(attributeKey, attributeValue);
        }
        for (AbstractTreeNode abstractTreeNode : this.getChildren()) {
            abstractTreeNode.copy().changeParentAndRegister(res);
        }
        return res;
    }

    /**
     * <p>copy.</p>
     *
     * @param original a {@link com.xenoamess.x8l.RootNode} object.
     */
    public void copy(RootNode original) {
        for (String attributeKey : original.getAttributesKeyList()) {
            String attributeValue = original.getAttributes().get(attributeKey);
            this.addAttribute(attributeKey, attributeValue);
        }
        for (AbstractTreeNode abstractTreeNode : original.getChildren()) {
            abstractTreeNode.copy().changeParentAndRegister(this);
        }
    }
}

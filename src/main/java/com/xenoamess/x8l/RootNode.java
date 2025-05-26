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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * <p>RootNode class.</p>
 *
 * @author XenoAmess
 * @version 2.2.3-SNAPSHOT
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
    public RootNode copy(ContentNode parent) {
        // A RootNode's copy should always be a RootNode and typically won't have a parent from another tree.
        // If a parent is provided (e.g. during a recursive copy where a RootNode is unexpectedly a child),
        // it's an anomaly or misuse, but we'll respect it. Normally, for copying a tree, parent will be null.
        if (parent != null) {
            // This case is unusual for RootNode, log a warning or handle as an error if appropriate.
            // For now, we'll proceed as if it's a general ContentNode copy into a RootNode structure.
        }
        RootNode res = new RootNode(null); // RootNode typically has a null parent.

        // Deep copy attributes from this (which is a RootNode, extending ContentNode)
        for (Map.Entry<String, String> entry : this.getAttributes().entrySet()) {
            res.getAttributes().put(entry.getKey(), entry.getValue());
        }
        // Deep copy attributesKeyList
        for (String key : this.getAttributesKeyList()) {
            res.getAttributesKeyList().add(key);
        }
        // Deep copy attributeSegments
        for (String segment : this.getAttributeSegments()) {
            res.getAttributeSegments().add(segment);
        }

        // Deep copy children
        for (AbstractTreeNode abstractTreeNode : this.getChildren()) {
            // Children are added to 'res' (the new RootNode) during their copy(res) call.
            abstractTreeNode.copy(res);
        }
        return res;
    }

    /**
     * Makes this RootNode a deep copy of the original RootNode.
     * Attributes and children are cleared and then populated from the original.
     *
     * @param original The RootNode to copy from.
     */
    public void copyFrom(@NotNull RootNode original) {
        // Clear current state of this RootNode
        this.getAttributes().clear();
        this.getAttributesKeyList().clear();
        this.getAttributeSegments().clear();
        // Clear children, ensuring they are detached from this node first
        List<AbstractTreeNode> childrenToDetach = new ArrayList<>(this.getChildren());
        for (AbstractTreeNode child : childrenToDetach) {
            child.setParent(null); // Detach from this node
        }
        this.getChildren().clear();

        // Deep copy attributes from original
        // Ensure segments are copied correctly, matching the logic in ContentNode's addAttribute
        for (int i = 0; i < original.getAttributesKeyList().size(); i++) {
            String key = original.getAttributesKeyList().get(i);
            String value = original.getAttributes().get(key);
            String segment = "";
            if (i < original.getAttributeSegments().size()) {
                segment = original.getAttributeSegments().get(i);
            }
            // Use the existing addAttribute that handles map, keyList, and segments
            this.addAttribute(key, value, segment);
        }
        // Ensure the last segment is correctly set if it was empty in the original
        if (!original.getAttributesKeyList().isEmpty() && original.getAttributeSegments().size() == original.getAttributesKeyList().size()) {
            if (original.getAttributeSegments().get(original.getAttributeSegments().size() - 1).isEmpty()) {
                 if (!this.getAttributeSegments().isEmpty()) {
                    this.getAttributeSegments().set(this.getAttributeSegments().size() - 1, "");
                 }
            }
        }


        // Deep copy children
        for (AbstractTreeNode abstractTreeNode : original.getChildren()) {
            // The child's copy method will create a new node and
            // its constructor (via super(parent)) will add it to `this` (the new parent).
            abstractTreeNode.copy(this);
        }
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

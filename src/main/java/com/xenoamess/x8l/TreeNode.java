package com.xenoamess.x8l;

import java.io.Writer;

public abstract class TreeNode {
    public static boolean DEBUG = false;
    public ContentNode parent;

    public TreeNode(ContentNode parent) {
        this.parent = parent;
        if (this.parent != null) {
            this.parent.children.add(this);
        }
    }

    public TreeNode(ContentNode parent, int index) {
        this.parent = parent;
        if (this.parent != null) {
            this.parent.children.add(index, this);
        }
    }


    public void show() {
        System.out.println();
        System.out.println("show!");
        System.out.println("self : " + this);
        System.out.println("parent : " + this.parent);
    }

    public void destroy() {
        if (this.parent != null) {
            if (this.parent.children != null) {
                this.parent.children.remove(this);
            }
        }
        this.parent = null;
    }

    public TreeNode removeParent() {
        if (this.parent != null) {
            this.parent.children.remove(this);
            this.parent = null;
        }
        return this;
    }

    public TreeNode changeParent(ContentNode contentNode) {
        this.removeParent();
        this.parent = contentNode;
        return this;
    }

    public TreeNode changeParentAndRegister(ContentNode contentNode, int index) {
        this.changeParent(contentNode);
        this.parent.children.add(index, this);
        return this;
    }

    public TreeNode changeParentAndRegister(ContentNode contentNode) {
        return this.changeParentAndRegister(contentNode, this.parent.children.size());
    }

    public abstract void output(Writer writer);

    public abstract void format(int space);
}

package com.xenoamess.x8l;

public abstract class TreeNode {
    public static boolean DEBUG = false;
    public ContentNode parent;

    public TreeNode(ContentNode parent) {
        this.parent = parent;
        if (this.parent != null) {
            this.parent.children.add(this);
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
}

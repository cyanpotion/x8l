package com.xenoamess.x8l;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author XenoAmess
 */
public abstract class TreeNode implements AutoCloseable {
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

    @Override
    public void close() {
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
        if (index == -1) {
            this.parent.children.add(this);
        } else {
            this.parent.children.add(index, this);
        }
        return this;
    }

    public TreeNode changeParentAndRegister(ContentNode contentNode) {
        return this.changeParentAndRegister(contentNode, -1);
    }

    public abstract void write(Writer writer);

    public abstract void format(int space);

    @Override
    public boolean equals(Object treeNode) {
        if (treeNode.getClass().equals(this.getClass())) {
            return this.toString().equals(treeNode.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        String res = "";
        try (
                StringWriter stringWriter = new StringWriter();
        ) {
            this.write(stringWriter);
            res = stringWriter.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}

package com.xenoamess.x8l;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author XenoAmess
 */
public abstract class AbstractTreeNode implements AutoCloseable {
    public static boolean DEBUG = false;
    public ContentNode parent;

    public AbstractTreeNode(ContentNode parent) {
        this.parent = parent;
        if (this.parent != null) {
            this.parent.children.add(this);
        }
    }

    public AbstractTreeNode(ContentNode parent, int index) {
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

    public AbstractTreeNode removeParent() {
        if (this.parent != null) {
            this.parent.children.remove(this);
            this.parent = null;
        }
        return this;
    }

    public AbstractTreeNode changeParent(ContentNode contentNode) {
        this.removeParent();
        this.parent = contentNode;
        return this;
    }

    public AbstractTreeNode changeParentAndRegister(ContentNode contentNode, int index) {
        this.changeParent(contentNode);
        if (index == -1) {
            this.parent.children.add(this);
        } else {
            this.parent.children.add(index, this);
        }
        return this;
    }

    public AbstractTreeNode changeParentAndRegister(ContentNode contentNode) {
        return this.changeParentAndRegister(contentNode, -1);
    }

    /**
     * write this AbstractTreeNode's data to a writer.
     */
    public abstract void write(Writer writer);

    /**
     * format the tree node.
     *
     * @param space spaces before the node.
     *              if space < 0 then no space is before the node.
     */
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

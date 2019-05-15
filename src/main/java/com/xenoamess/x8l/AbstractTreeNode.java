package com.xenoamess.x8l;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author XenoAmess
 */
public abstract class AbstractTreeNode implements AutoCloseable {
    private ContentNode parent;

    public AbstractTreeNode(ContentNode parent) {
        this.setParent(parent);
        if (this.getParent() != null) {
            this.getParent().getChildren().add(this);
        }
    }

    public AbstractTreeNode(ContentNode parent, int index) {
        this.setParent(parent);
        if (this.getParent() != null) {
            this.getParent().getChildren().add(index, this);
        }
    }


    public void show() {
        System.out.println();
        System.out.println("show!");
        System.out.println("self : " + this);
        System.out.println("parent : " + this.getParent());
    }

    @Override
    public void close() {
        if (this.getParent() != null && this.getParent().getChildren() != null) {
            this.getParent().getChildren().remove(this);
        }
        this.setParent(null);
    }

    public AbstractTreeNode removeParent() {
        if (this.getParent() != null) {
            this.getParent().getChildren().remove(this);
            this.setParent(null);
        }
        return this;
    }

    /**
     * delete this from old parent's children.
     * then set this node's parent to new parent node.
     *
     * @param contentNode new parent node.
     * @return
     */
    public AbstractTreeNode changeParent(ContentNode contentNode) {
        this.removeParent();
        this.setParent(contentNode);
        return this;
    }

    /**
     * delete this from old parent's children.
     * then set this node's parent to new parent node.
     * <p>
     * then add this node into new parent node's children's assigned position.
     *
     * @param contentNode new parent node.
     * @param index       insert index of this node into new parent's children
     * @return
     */
    public AbstractTreeNode changeParentAndRegister(ContentNode contentNode, int index) {
        this.changeParent(contentNode);
        if (index == -1) {
            this.getParent().getChildren().add(this);
        } else {
            this.getParent().getChildren().add(index, this);
        }
        return this;
    }

    public AbstractTreeNode changeParentAndRegister(ContentNode contentNode) {
        return this.changeParentAndRegister(contentNode, -1);
    }

    /**
     * write this AbstractTreeNode's data to a writer.
     *
     * @param writer the writer to write to.
     * @throws IOException If an I/O error occurs
     */
    public abstract void write(Writer writer) throws IOException;

    /**
     * format the tree node.
     *
     * @param space spaces before the node.
     *              if space is less than 0 then no space is before the node.
     */
    public abstract void format(int space);

    @Override
    public boolean equals(Object treeNode) {
        if (treeNode == null) {
            return false;
        }
        if (!treeNode.getClass().equals(this.getClass())) {
            return false;
        }
        return this.toString().equals(treeNode.toString());
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public ContentNode getParent() {
        return parent;
    }

    public void setParent(ContentNode parent) {
        this.parent = parent;
    }
}

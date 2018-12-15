package com.xenoamess.x8l;

public class CommentNode extends TreeNode {
    public String textContent;

    public CommentNode(ContentNode parent, String textContent) {
        super(parent);
        if (textContent == null) {
            textContent = "";
        }
        this.textContent = textContent;
    }

    @Override
    public void show() {
        super.show();
        System.out.println("textContent : " + this.textContent);
    }

    @Override
    public void destroy() {
        super.destroy();
        this.textContent = null;
    }
}
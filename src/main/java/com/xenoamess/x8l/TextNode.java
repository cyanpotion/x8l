package com.xenoamess.x8l;

public class TextNode extends TreeNode {
    public String textContent;

    public TextNode(ContentNode parent, String textContent) {
        super(parent);
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

package com.xenoamess.x8l;

/**
 * @author XenoAmess
 */
public class TextNode extends AbstractTreeNode {
    private String textContent;

    public TextNode(ContentNode parent, String textContent) {
        super(parent);
        if (textContent == null) {
            textContent = "";
        }
        this.setTextContent(textContent);
    }

    public TextNode(ContentNode parent, int index, String textContent) {
        super(parent, index);
        if (textContent == null) {
            textContent = "";
        }
        this.setTextContent(textContent);
    }


    @Override
    public void show() {
        super.show();
        System.out.println("textContent : " + this.getTextContent());
    }

    @Override
    public void clear() {
        this.setTextContent(null);
    }

    @Override
    public void format(int space) {
        this.setTextContent(this.getTextContent().trim());
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}

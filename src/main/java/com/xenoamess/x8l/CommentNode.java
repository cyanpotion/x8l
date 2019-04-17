package com.xenoamess.x8l;

import java.io.IOException;
import java.io.Writer;

/**
 * @author XenoAmess
 */
public class CommentNode extends AbstractTreeNode {
    public String textContent;

    public CommentNode(ContentNode parent, String textContent) {
        super(parent);
        if (textContent == null) {
            textContent = "";
        }
        this.textContent = textContent;
    }

    public CommentNode(ContentNode parent, int index, String textContent) {
        super(parent, index);
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
    public void close() {
        super.close();
        this.textContent = null;
    }

    @Override
    public void write(Writer writer) {
        try {
            writer.append('<');
            writer.append('<');
            writer.append(X8lTree.transcode(textContent));
            writer.append('>');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void format(int space) {
        this.textContent = this.textContent.trim();
    }
}
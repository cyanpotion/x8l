package com.xenoamess.x8l;

import java.io.IOException;
import java.io.Writer;

public class TextNode extends TreeNode {
    public String textContent;

    public TextNode(ContentNode parent, String textContent) {
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

    @Override
    public void output(Writer writer) {
        try {
            writer.append(X8lTree.Transcode(textContent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

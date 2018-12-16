package com.xenoamess.x8l;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class X8lTree {
    public boolean debug;
    public ContentNode root = null;
    public Reader reader;

    public static X8lTree GetX8lTree(Reader reader) {
        X8lTree res = new X8lTree(reader);
        res.parse();
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public X8lTree(Reader reader) {
        this.reader = reader;
    }


    /*
     * destroy this tree.
     */
    public void destroy() {
        this.root.destroy();
        this.root = null;
        if (this.reader != null) {
            try {
                this.reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.reader = null;
        }
    }

    public void parse() {
        if (this.debug) {
            System.out.println("building:");
            System.out.println(this);
            System.out.println();
        }
        this.root = new ContentNode(null);
        int nowInt;
        ContentNode nowNode = this.root;
        boolean inAttributeArea = false;
        boolean inCommentArea = false;
        boolean lastCharIsModulus = false;

        StringBuilder stringBuilder = new StringBuilder();
        char nowChar;
        while (true) {
            try {
                nowInt = this.reader.read();
            } catch (IOException e) {
                e.printStackTrace();
                throw new X8lGrammarException();
            }
            nowChar = (char) nowInt;
            if (nowInt == -1) {
                if (nowNode == this.root && !inAttributeArea && !inCommentArea) {
                    if (stringBuilder.length() != 0) {
                        new TextNode(nowNode, stringBuilder.toString());
//                        stringBuilder = new StringBuilder();
                    }
                    break;
                } else {
                    throw new X8lGrammarException();
                }
            } else if (lastCharIsModulus) {
                stringBuilder.append((char) nowChar);
                lastCharIsModulus = false;
            } else if (nowChar == '%') {
                lastCharIsModulus = true;
            } else if (inCommentArea) {
                if (nowChar == '>') {
                    new CommentNode(nowNode, stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    inCommentArea = false;
                } else {
                    stringBuilder.append((char) nowChar);
                }
            } else if (nowChar == '<') {
                if (inAttributeArea) {
                    if (!nowNode.attributes.isEmpty() || stringBuilder.length() != 0) {
                        throw new X8lGrammarException();
                    } else {
                        ContentNode nowParent = nowNode.parent;
                        nowNode.destroy();
                        nowNode = nowParent;
                        inAttributeArea = false;
                        inCommentArea = true;
                    }
                } else {
                    if (stringBuilder.length() != 0) {
                        new TextNode(nowNode, stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                    }
                    nowNode = new ContentNode(nowNode);
                    inAttributeArea = true;
                }
            } else if (nowChar == '>') {
                if (!inAttributeArea) {
                    if (stringBuilder.length() != 0) {
                        new TextNode(nowNode, stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                    }
                    nowNode = nowNode.parent;
                    if (nowNode == null) {
                        throw new X8lGrammarException();
                    }
                } else {
                    if (stringBuilder.length() != 0) {
                        nowNode.addAttribute(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                    }
                    inAttributeArea = false;
                }
            } else if (nowChar == ' ' || nowChar == '\t' || nowChar == '\r' || nowChar == '\n') {
                if (inAttributeArea) {
                    if (stringBuilder.length() != 0) {
                        nowNode.addAttribute(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                    }
                } else {
                    stringBuilder.append((char) nowChar);
                }
            } else {
                stringBuilder.append((char) nowChar);
            }
        }
    }

    /*
     * print contents to console for debug.
     */
    public void show() {
        root.show();
    }

    /*
     * delete TextNode that only have \s in their textContent.
     */
    public X8lTree trim() {
        this.root.trim();
        return this;
    }

    public void output(Writer writer) {
        this.root.output(writer);
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String Transcode(String originalString) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < originalString.length(); i++) {
            char chr = originalString.charAt(i);
            if (chr == '<' || chr == '>' || chr == '%') {
                stringBuilder.append('%');
            }
            stringBuilder.append(chr);
        }
        return stringBuilder.toString();
    }
}

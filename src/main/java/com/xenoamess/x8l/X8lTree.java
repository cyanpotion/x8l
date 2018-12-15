package com.xenoamess.x8l;

import java.io.IOException;
import java.io.Reader;

public class X8lTree {
    public boolean debug;
    public ContentNode root = null;
    public Reader reader;

    public static X8lTree getX8lTree(Reader reader) {
        X8lTree res = new X8lTree(reader);
        res.parse();
        res.trim();
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
                if (nowNode == this.root) {
                    if (stringBuilder.length() != 0) {
                        new TextNode(nowNode, stringBuilder.toString());
                        stringBuilder = new StringBuilder();
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
            } else if (nowChar == '<') {
                if (inAttributeArea) {
                    throw new X8lGrammarException();
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
    public void trim() {
        this.root.trim();
    }
}

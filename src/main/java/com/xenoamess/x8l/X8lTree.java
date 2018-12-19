package com.xenoamess.x8l;

import java.io.*;

public class X8lTree {
    public boolean debug;
    public ContentNode root = null;
    public Reader reader;

    public static X8lTree LoadFromFile(File file) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new FileNotFoundException();
        }
        X8lTree res = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            res = new X8lTree(fileReader);
            res.parse();
        } catch (FileNotFoundException e) {
            throw e;
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                throw e;
            }
        }
        return res;
    }

    public static void SaveToFile(File file, X8lTree x8lTree) throws IOException {
        if (file == null || !file.isFile()) {
            throw new FileNotFoundException();
        }
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw e;
            }
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            x8lTree.output(fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                throw e;
            }
        }
    }

    public static X8lTree LoadFromString(String string) {
        X8lTree res = null;
        StringReader stringReader = null;
        try {
            stringReader = new StringReader(string);
            res = new X8lTree(stringReader);
            res.parse();
        } finally {
            if (stringReader != null) {
                stringReader.close();
            }
        }
        return res;
    }

    public static String SaveToString(X8lTree x8lTree) {
        StringWriter stringWriter = null;
        try {
            stringWriter = new StringWriter();
            x8lTree.output(stringWriter);
            stringWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stringWriter != null) {
                    stringWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringWriter.toString();
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

                    new TextNode(nowNode, stringBuilder.toString());
//                        stringBuilder = new StringBuilder();

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

                    new TextNode(nowNode, stringBuilder.toString());
                    stringBuilder = new StringBuilder();

                    nowNode = new ContentNode(nowNode);
                    inAttributeArea = true;
                }
            } else if (nowChar == '>') {
                if (!inAttributeArea) {

                    new TextNode(nowNode, stringBuilder.toString());
                    stringBuilder = new StringBuilder();

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

    /*
     * format the tree.
     * notice that format will trim every text content in text nodes and comment nodes.
     * that is designed to do so, in order to notice the user that, when you format the tree,
     * do not forget that text nodes with only space chars have the same right than "normal" text nodes,
     * and you are sure that you be aware the "format" operation changed them ,thus it already changed the whole tree's data.
     * that is important.
     */
    public X8lTree format() {
        this.trim();
        this.root.format(-1);
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

    public static String Untranscode(String transcodedString) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean lastCharIsModulus = false;
        for (int i = 0; i < transcodedString.length(); i++) {
            char chr = transcodedString.charAt(i);
            if (lastCharIsModulus) {
                stringBuilder.append(chr);
                lastCharIsModulus = false;
            } else if (chr == '%') {
                lastCharIsModulus = true;
            } else {
                stringBuilder.append(chr);
            }
        }
        return stringBuilder.toString();
    }
}

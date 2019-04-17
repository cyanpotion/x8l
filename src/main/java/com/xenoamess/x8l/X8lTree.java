package com.xenoamess.x8l;

import java.io.*;

/**
 * @author XenoAmess
 */
public class X8lTree implements AutoCloseable, Serializable {
    protected static class X8lGrammarException extends RuntimeException {
    }

    public boolean debug;
    public ContentNode root = new ContentNode(null);
    public Reader reader;

    public static X8lTree LoadFromFile(File file) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new FileNotFoundException();
        }
        X8lTree res = null;
        try (
                FileReader fileReader = new FileReader(file)
        ) {
            res = new X8lTree(fileReader);
            res.parse();
        } catch (FileNotFoundException e) {
            throw e;
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

        try (
                FileWriter fileWriter = new FileWriter(file);
        ) {
            x8lTree.write(fileWriter);
        } catch (IOException e) {
            throw e;
        }
    }

    public static X8lTree LoadFromString(String string) {
        X8lTree res = null;
        try (
                StringReader stringReader = new StringReader(string);
        ) {
            res = new X8lTree(stringReader);
            res.parse();
        }
        return res;
    }

    public static String SaveToString(X8lTree x8lTree) {
        String res = "";
        try (
                StringWriter stringWriter = new StringWriter();
        ) {
            x8lTree.write(stringWriter);
            res = stringWriter.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public X8lTree() {
    }

    public X8lTree(Reader reader) {
        this.reader = reader;
    }


    /**
     * close this tree.
     */
    @Override
    public void close() {
        this.root.close();
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

    public void read(Reader reader) {
        this.parse(reader);
    }

    public void write(Writer writer) {
        this.root.write(writer);
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parse() {
        this.parse(this.reader);
    }

    public void parse(Reader reader) {
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
                nowInt = reader.read();
            } catch (IOException e) {
                e.printStackTrace();
                throw new X8lGrammarException();
            }
            nowChar = (char) nowInt;
            if (nowInt == -1) {
                if (nowNode == this.root && !inAttributeArea && !inCommentArea) {
                    new TextNode(nowNode, stringBuilder.toString());
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
                        nowNode.close();
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
            } else if (Character.isWhitespace(nowChar)) {
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

    /**
     * print contents to console for debug.
     */
    public void show() {
        root.show();
    }

    /**
     * delete TextNode that only have \s in their textContent.
     */
    public X8lTree trim() {
        this.root.trim();
        return this;
    }

    /**
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

    @Override
    public String toString() {
        return SaveToString(this);
    }

    @Override
    public boolean equals(Object x8lTree) {
        if (x8lTree instanceof X8lTree) {
            return this.toString().equals(x8lTree.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    public void readObject(ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        try (Reader reader = new InputStreamReader(objectInputStream)) {
            this.read(reader);
        }
    }

    public void writeObject(ObjectOutputStream objectOutputStream)
            throws IOException {
        try (Writer writer = new OutputStreamWriter(objectOutputStream)) {
            this.write(writer);
        }
    }
}

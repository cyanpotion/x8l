/*
 * MIT License
 *
 * Copyright (c) 2019 XenoAmess
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xenoamess.x8l;

import com.xenoamess.x8l.dealers.AbstractLanguageDealer;
import com.xenoamess.x8l.dealers.X8lDealer;

import java.io.*;

/**
 * @author XenoAmess
 */
public class X8lTree implements AutoCloseable, Serializable {
    private AbstractLanguageDealer languageDealer = X8lDealer.INSTANCE;
    private final ContentNode root = new ContentNode(null);
    private Reader reader;

    public ContentNode getRoot() {
        return root;
    }

    public static X8lTree loadFromFile(File file) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new FileNotFoundException();
        }
        X8lTree res = null;
        try (
                FileReader fileReader = new FileReader(file)
        ) {
            res = new X8lTree(fileReader);
            res.parse();
        }
        return res;
    }

    public static void saveToFile(File file, X8lTree x8lTree) throws IOException {
        if (file == null || !file.isFile()) {
            throw new FileNotFoundException();
        }
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            if (!file.createNewFile()) {
                System.err.println("File not exist when check but is already there when try to create it. Will " +
                        "use it directly.");
            }
        }

        try (
                FileWriter fileWriter = new FileWriter(file);
        ) {
            x8lTree.write(fileWriter);
        }
    }

    public static X8lTree loadFromString(String string) throws IOException {
        X8lTree res = null;
        try (
                StringReader stringReader = new StringReader(string);
        ) {
            res = new X8lTree(stringReader);
            res.parse();
        }
        return res;
    }

    public static String saveToString(X8lTree x8lTree) {
        String res = "";
        try (
                StringWriter stringWriter = new StringWriter();
        ) {
            x8lTree.write(stringWriter);
            res = stringWriter.toString();
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

    public X8lTree(Reader reader, boolean readItNow) throws IOException {
        this.reader = reader;
        if (readItNow) {
            this.read(reader);
        }
    }

    public X8lTree(Reader reader, AbstractLanguageDealer languageDealer, boolean readItNow) throws IOException {
        this.reader = reader;
        this.languageDealer = languageDealer;
        if (readItNow) {
            this.read(reader, this.languageDealer);
        }
    }

    public X8lTree(X8lTree original) {
        try (
                StringReader stringReader = new StringReader(original.toString());
        ) {
            this.reader = stringReader;
            this.languageDealer = original.languageDealer;
            this.read(this.reader, this.languageDealer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * close this tree.
     */
    @Override
    public void close() {
        this.getRoot().close();
        if (this.reader != null) {
            try {
                this.reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.reader = null;
        }
    }

    public void read(Reader reader) throws IOException {
        read(reader, this.languageDealer);
    }

    public void read(Reader reader, AbstractLanguageDealer languageDealer) throws IOException {
        this.getRoot().read(reader, languageDealer);
    }


    public void write(Writer writer) throws IOException {
        this.write(writer, this.languageDealer);
    }

    public void write(Writer writer, AbstractLanguageDealer languageDealer) throws IOException {
        this.getRoot().write(writer, languageDealer);
        writer.flush();
    }


    public void parse() throws IOException {
        this.read(this.reader);
    }

    /**
     * print contents to console for debug.
     */
    public void show() {
        getRoot().show();
    }

    /**
     * delete TextNode that only have \s in their textContent.
     *
     * @return the original X8lTree is trimmed and then returned.
     */
    public X8lTree trim() {
        this.getRoot().trim();
        return this;
    }

    /**
     * format the tree.
     * notice that format will trim every text content in text nodes and comment nodes.
     * that is designed to do so, in order to notice the user that, when you format the tree,
     * do not forget that text nodes with only space chars have the same right than "normal" text nodes,
     * and you are sure that you be aware the "format" operation changed them ,thus it already changed the whole
     * tree's data.
     * that is important.
     *
     * @return the original X8lTree is formatted and then returned.
     */
    public X8lTree format() {
        this.trim();
        this.getRoot().format(-1);
        return this;
    }


    public static String transcode(String originalString) {
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

    public static String untranscode(String transcodedString) {
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
        return saveToString(this);
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

    /**
     * used by Serializable
     *
     * @param objectInputStream objectInputStream
     * @throws IOException            IOException
     * @throws ClassNotFoundException ClassNotFoundException
     */
    private void readObject(ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        try (Reader objectInputStreamReader = new InputStreamReader(objectInputStream)) {
            this.read(objectInputStreamReader);
        }
    }

    /**
     * used by Serializable
     *
     * @param objectOutputStream objectOutputStream
     * @throws IOException IOException
     */
    private void writeObject(ObjectOutputStream objectOutputStream)
            throws IOException {
        try (Writer writer = new OutputStreamWriter(objectOutputStream)) {
            this.write(writer);
        }
    }

    /**
     * Append an X8lTree upon this X8lTree.
     * Notice that this function will break the original X8lTree.
     *
     * @param patch patch tree.
     * @see ContentNode
     */
    public void append(X8lTree patch) {
        if (patch == null) {
            return;
        }
        this.getRoot().appendAll(patch.getRoot().getChildren());
    }

}

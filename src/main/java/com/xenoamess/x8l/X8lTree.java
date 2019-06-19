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

import com.xenoamess.commons.as_final_field.AsFinalField;
import com.xenoamess.x8l.dealers.AbstractLanguageDealer;
import com.xenoamess.x8l.dealers.X8lDealer;

import java.io.*;

/**
 * @author XenoAmess
 */
public class X8lTree implements AutoCloseable, Serializable {
    private AbstractLanguageDealer languageDealer = X8lDealer.INSTANCE;
    @AsFinalField
    private transient ContentNode root = new ContentNode(null);
    private transient Reader reader;

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
        if (file == null || (file.exists() && !file.isFile())) {
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
                FileWriter fileWriter = new FileWriter(file)
        ) {
            x8lTree.write(fileWriter);
        }
    }

    public static X8lTree loadFromString(String string) {
        X8lTree res = null;
        try (
                StringReader stringReader = new StringReader(string)
        ) {
            res = new X8lTree(stringReader);
            res.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String saveToString(X8lTree x8lTree) {
        String res = "";
        try (
                StringWriter stringWriter = new StringWriter()
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
        this.setReader(reader);
    }

    public X8lTree(Reader reader, boolean readItNow) throws IOException {
        this(reader, X8lDealer.INSTANCE, readItNow);
    }

    public X8lTree(Reader reader, AbstractLanguageDealer languageDealer) throws IOException {
        this(reader, languageDealer, false);
    }

    public X8lTree(Reader reader, AbstractLanguageDealer languageDealer, boolean readItNow) throws IOException {
        this(reader);
        this.setLanguageDealer(languageDealer);
        if (readItNow) {
            this.read(reader, this.getLanguageDealer());
        }
    }

    public X8lTree(X8lTree original) {
        try (
                StringReader stringReader = new StringReader(original.toString())
        ) {
            this.setReader(stringReader);
            this.setLanguageDealer(original.getLanguageDealer());
            this.read(this.getReader(), this.getLanguageDealer());
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
        if (this.getReader() != null) {
            try {
                this.getReader().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.setReader(null);
        }
    }

    public void read() throws IOException {
        read(this.getReader());
    }

    public void read(Reader reader) throws IOException {
        read(reader, this.getLanguageDealer());
    }

    public void read(AbstractLanguageDealer languageDealer) throws IOException {
        read(this.getReader(), languageDealer);
    }

    /**
     * will not close the reader after reading.
     *
     * @param reader         reader
     * @param languageDealer languageDealer
     * @throws IOException IOException
     */
    public void read(Reader reader, AbstractLanguageDealer languageDealer) throws IOException {
        this.getRoot().read(reader, languageDealer);
    }


    public void write(Writer writer) throws IOException {
        this.write(writer, this.getLanguageDealer());
    }

    /**
     * close the writer after writing.
     *
     * @param writer         writer
     * @param languageDealer languageDealer
     * @throws IOException IOException
     */
    public void write(Writer writer, AbstractLanguageDealer languageDealer) throws IOException {
        this.getRoot().write(writer, languageDealer);
        writer.close();
    }


    public void parse() throws IOException {
        this.read(this.getReader());
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

    /**
     * use this function instead of transcode function when you need to transCode even whiteSpace
     * this is used in key / value in attribute.
     *
     * @param originalString originalS tring
     * @return transCoded String
     */
    public static String transcodeWithWhitespace(String originalString) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < originalString.length(); i++) {
            char chr = originalString.charAt(i);
            if (chr == '<' || chr == '>' || chr == '%' || Character.isWhitespace(chr)) {
                stringBuilder.append('%');
            }
            stringBuilder.append(chr);
        }
        return stringBuilder.toString();
    }

    /**
     * use this function instead of transcode function when you need to transCode only &gt;
     * this is used in comment only.
     *
     * @param originalString originalS tring
     * @return transCoded String
     */
    public static String transcodeComment(String originalString) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < originalString.length(); i++) {
            char chr = originalString.charAt(i);
            if (chr == '%' || chr == '>') {
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
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof X8lTree)) {
            return false;
        }
        return this.toString().equals(object.toString());
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
        objectInputStream.defaultReadObject();
        if (this.root == null) {
            this.root = new ContentNode(null);
        }
        this.read(new InputStreamReader(objectInputStream));
    }

    /**
     * used by Serializable
     *
     * @param objectOutputStream objectOutputStream
     * @throws IOException IOException
     */
    private void writeObject(ObjectOutputStream objectOutputStream)
            throws IOException {
        objectOutputStream.defaultWriteObject();
        this.write(new OutputStreamWriter(objectOutputStream));
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
        X8lTree patchClone = new X8lTree(patch);
        this.getRoot().appendAll(patchClone.getRoot().getChildren());
    }

    public AbstractLanguageDealer getLanguageDealer() {
        return languageDealer;
    }

    public void setLanguageDealer(AbstractLanguageDealer languageDealer) {
        this.languageDealer = languageDealer;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }
}

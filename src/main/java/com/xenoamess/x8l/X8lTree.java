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
import com.xenoamess.x8l.dealers.JsonDealer;
import com.xenoamess.x8l.dealers.X8lDealer;
import com.xenoamess.x8l.dealers.XmlDealer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author XenoAmess
 */
public class X8lTree implements AutoCloseable, Serializable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(X8lTree.class);

    private AbstractLanguageDealer languageDealer = X8lDealer.INSTANCE;
    @AsFinalField
    private transient ContentNode root = new ContentNode(null);
    private transient Reader reader;

    private static final List<AbstractLanguageDealer> languageDealerList = new ArrayList<>();

    public static void addToLanguageDealerList(AbstractLanguageDealer languageDealer) {
        languageDealerList.add(languageDealer);
    }

    static {
        addToLanguageDealerList(X8lDealer.INSTANCE);
        addToLanguageDealerList(JsonDealer.INSTANCE);
        addToLanguageDealerList(XmlDealer.INSTANCE);
    }

    public static List<AbstractLanguageDealer> getLanguageDealerListCopy() {
        ArrayList<AbstractLanguageDealer> res = new ArrayList<>();
        res.addAll(languageDealerList);
        return res;
    }

    public static List<AbstractLanguageDealer> suspectDealer(String nameString, List<AbstractLanguageDealer> originalList) {
        List<AbstractLanguageDealer> res = new ArrayList<>(originalList);
        if (nameString.endsWith("json") || nameString.endsWith("json".toUpperCase())) {
            originalList.remove(JsonDealer.INSTANCE);
            originalList.add(0, JsonDealer.INSTANCE);
        } else if (nameString.endsWith("xml") || nameString.endsWith("xml".toUpperCase())) {
            originalList.remove(XmlDealer.INSTANCE);
            originalList.add(0, XmlDealer.INSTANCE);
        } else if (nameString.endsWith("x8l") || nameString.endsWith("x8l".toUpperCase())) {
            originalList.remove(X8lDealer.INSTANCE);
            originalList.add(0, X8lDealer.INSTANCE);
        } else if (nameString.contains("json")) {
            originalList.remove(JsonDealer.INSTANCE);
            originalList.add(0, JsonDealer.INSTANCE);
        } else if (nameString.contains("xml")) {
            originalList.remove(XmlDealer.INSTANCE);
            originalList.add(0, XmlDealer.INSTANCE);
        } else if (nameString.contains("x8l")) {
            originalList.remove(X8lDealer.INSTANCE);
            originalList.add(0, X8lDealer.INSTANCE);
        }
        return res;
    }

    public ContentNode getRoot() {
        return root;
    }

    /*
     * Path
     */

    public static X8lTree load(Path path, AbstractLanguageDealer dealer) throws IOException {
        if (path == null || !Files.isReadable(path)) {
            throw new FileNotFoundException(path == null ? "null" : path.toString());
        }
        X8lTree res = null;
        try (
                Reader reader = Files.newBufferedReader(path)
        ) {
            res = load(reader, dealer);
        }
        return res;
    }

    public static X8lTree load(Path path, List<AbstractLanguageDealer> possibleDealerList) throws IOException {
        if (path == null || !Files.isReadable(path)) {
            throw new FileNotFoundException(path == null ? "null" : path.toString());
        }
        X8lTree res = null;
        try (
                Reader reader = Files.newBufferedReader(path)
        ) {
            res = load(reader, possibleDealerList);
        }
        return res;
    }

    public static X8lTree load(Path path) throws IOException {
        if (path == null || !Files.isReadable(path)) {
            throw new FileNotFoundException(path == null ? "null" : path.toString());
        }
        return load(path, suspectDealer(path.toString(), getLanguageDealerListCopy()));
    }

    public static void save(Path path, X8lTree x8lTree) throws IOException {
        if (path == null || !Files.isReadable(path)) {
            throw new FileNotFoundException(path == null ? "null" : path.toString());
        }
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }

        try (
                Writer writer = Files.newBufferedWriter(path);
        ) {
            x8lTree.write(writer);
        }
    }

    /*
     * FileObject
     */

    public static X8lTree load(FileObject fileObject, AbstractLanguageDealer dealer) throws IOException {
        if (fileObject == null || !fileObject.exists()) {
            throw new FileNotFoundException(fileObject == null ? "null" : fileObject.toString());
        }
        X8lTree res = null;
        try (
                InputStream inputStream = fileObject.getContent().getInputStream()
        ) {
            res = load(inputStream, dealer);
        }
        return res;
    }

    public static X8lTree load(FileObject fileObject, List<AbstractLanguageDealer> possibleDealerList) throws IOException {
        if (fileObject == null || !fileObject.exists()) {
            throw new FileNotFoundException(fileObject == null ? "null" : fileObject.toString());
        }
        X8lTree res = null;
        try (
                InputStream inputStream = fileObject.getContent().getInputStream()
        ) {
            res = load(inputStream, possibleDealerList);
        }
        return res;
    }

    public static X8lTree load(FileObject fileObject) throws IOException {
        if (fileObject == null || !fileObject.exists()) {
            throw new FileNotFoundException(fileObject == null ? "null" : fileObject.toString());
        }
        return load(fileObject, suspectDealer(fileObject.getName().getBaseName(), getLanguageDealerListCopy()));
    }

    public static void save(FileObject fileObject, X8lTree x8lTree) throws IOException {
        if (fileObject == null) {
            throw new FileNotFoundException(fileObject == null ? "null" : fileObject.toString());
        }

        try (
                OutputStream outputStream = fileObject.getContent().getOutputStream();
                Writer writer = new OutputStreamWriter(outputStream);
        ) {
            x8lTree.write(writer);
        }
    }

    /*
     * File
     */

    public static X8lTree load(File file, AbstractLanguageDealer dealer) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new FileNotFoundException(file == null ? "null" : file.getAbsolutePath());
        }
        X8lTree res = null;
        try (
                Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))
        ) {
            res = load(reader, dealer);
        }
        return res;
    }

    public static X8lTree load(File file, List<AbstractLanguageDealer> possibleDealerList) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new FileNotFoundException(file == null ? "null" : file.getAbsolutePath());
        }
        X8lTree res = null;
        try (
                Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))
        ) {
            res = load(reader, possibleDealerList);
        }
        return res;
    }

    public static X8lTree load(File file) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new FileNotFoundException(file == null ? "null" : file.getAbsolutePath());
        }
        return load(file, suspectDealer(file.getName(), getLanguageDealerListCopy()));
    }

    public static void save(File file, X8lTree x8lTree) throws IOException {
        if (file == null || (file.exists() && !file.isFile())) {
            throw new FileNotFoundException(file == null ? "null" : file.getAbsolutePath());
        }
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            if (!file.createNewFile()) {
                LOGGER.error("File not exist when check but is already there when try to create it. Will " +
                        "use it directly.");
            }
        }

        try (
                Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))
        ) {
            x8lTree.write(writer);
        }
    }

    /*
     * String
     */
    public static X8lTree load(String string, AbstractLanguageDealer dealer) {
        X8lTree res = null;
        try (
                StringReader stringReader = new StringReader(string)
        ) {
            res = new X8lTree(stringReader, dealer);
            res.parse();
        } catch (Exception e) {
            throw new X8lGrammarException("X8lTree.save(X8lTree x8lTree) fails. Really dom't know why.", e);
        }
        return res;
    }

    public static X8lTree load(String string, List<AbstractLanguageDealer> possibleDealerList) {
        X8lTree res = null;
        for (AbstractLanguageDealer dealer : possibleDealerList) {
            try (
                    StringReader stringReader = new StringReader(string)
            ) {
                res = new X8lTree(stringReader, dealer);
                res.parse();
                return res;
            } catch (Exception e) {
                LOGGER.debug("Try to use a dealer to load a X8lTree but failed. dealer:{}, treeString:{}", dealer, string, e);
            }
        }
        return res;
    }

    public static X8lTree load(String string) {
        return load(string, getLanguageDealerListCopy());
    }

    public static String save(X8lTree x8lTree) {
        String res = "";
        try (
                StringWriter stringWriter = new StringWriter()
        ) {
            x8lTree.write(stringWriter);
            res = stringWriter.toString();
        } catch (IOException e) {
            throw new X8lGrammarException("X8lTree.save(X8lTree x8lTree) fails. Really dom't know why.", e);
        }
        return res;
    }

    /*
     * Stream
     */

    public static X8lTree load(InputStream inputStream, AbstractLanguageDealer dealer) throws IOException {
        X8lTree x8lTree = null;
        try (
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                Reader reader = new InputStreamReader(bufferedInputStream);
        ) {
            x8lTree = load(reader, dealer);
        }
        return x8lTree;
    }

    public static X8lTree load(InputStream inputStream, List<AbstractLanguageDealer> possibleDealerList) throws IOException {
        return load(IOUtils.toString(inputStream, StandardCharsets.UTF_8), possibleDealerList);
    }

    public static X8lTree load(InputStream inputStream) throws IOException {
        return load(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
    }

    public static void save(OutputStream outputStream, X8lTree x8lTree) throws IOException {
        try (
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                Writer writer = new OutputStreamWriter(bufferedOutputStream);
        ) {
            x8lTree.write(writer);
        }
    }

    /*
     * Reader, Writer
     */

    public static X8lTree load(Reader reader, AbstractLanguageDealer dealer) throws IOException {
        X8lTree x8lTree = new X8lTree(reader, dealer);
        x8lTree.parse();
        return x8lTree;
    }

    public static X8lTree load(Reader reader, List<AbstractLanguageDealer> possibleDealerList) throws IOException {
        return load(IOUtils.toString(reader), possibleDealerList);
    }

    public static X8lTree load(Reader reader) throws IOException {
        return load(IOUtils.toString(reader));
    }

    public static void save(Writer writer, X8lTree x8lTree) throws IOException {
        x8lTree.write(writer);
    }

    public X8lTree() {
        this(null, X8lDealer.INSTANCE);
    }

    public X8lTree(Reader reader, AbstractLanguageDealer languageDealer) {
        this.setReader(reader);
        this.setLanguageDealer(languageDealer);
    }

    public X8lTree(Reader reader, AbstractLanguageDealer languageDealer, boolean readItNow) throws IOException {
        this.setReader(reader);
        this.setLanguageDealer(languageDealer);
        if (readItNow) {
            this.read(reader, this.getLanguageDealer());
        }
    }

    public X8lTree(X8lTree original) {
        if (original != null) {
            try (
                    StringReader stringReader = new StringReader(original.toString())
            ) {
                this.setReader(stringReader);
                this.setLanguageDealer(original.getLanguageDealer());
                this.read(this.getReader(), this.getLanguageDealer());
            } catch (IOException e) {
                throw new X8lGrammarException("X8lTree.save(X8lTree x8lTree) fails. Really dom't know why.", e);
            }
        }
    }


    public void clear() {
        this.getRoot().close();
    }

    /**
     * close this tree.
     */
    @Override
    public void close() throws IOException {
        this.clear();
        if (this.getReader() != null) {
            this.getReader().close();
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
        return save(this);
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
        try (Reader reader = new InputStreamReader(objectInputStream)) {
            this.read(reader);
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
        objectOutputStream.defaultWriteObject();
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

    public <T> List<T> applyToAllNodes(Function<AbstractTreeNode, T> function) {
        ArrayList<T> res = new ArrayList<>();
        this.getRoot().applyToAllNodes(res, function);
        return res;
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

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
import com.xenoamess.x8l.databind.X8lDataBeanFieldScheme;
import com.xenoamess.x8l.dealers.JsonDealer;
import com.xenoamess.x8l.dealers.LanguageDealer;
import com.xenoamess.x8l.dealers.X8lDealer;
import com.xenoamess.x8l.dealers.XmlDealer;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.io.IOUtils.buffer;

/**
 * X8lTree
 * X8lTree struct
 *
 * @author XenoAmess
 * @version 2.2.3-SNAPSHOT
 */
public class X8lTree implements AutoCloseable, Serializable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(X8lTree.class);

    /** Constant <code>STRING_JSON="json"</code> */
    public static final String STRING_JSON = "json";
    /** Constant <code>STRING_XML="xml"</code> */
    public static final String STRING_XML = "xml";
    /** Constant <code>STRING_XSD="xsd"</code> */
    public static final String STRING_XSD = "xsd";
    /** Constant <code>STRING_X8L="x8l"</code> */
    public static final String STRING_X8L = "x8l";

    private @NotNull LanguageDealer languageDealer = X8lDealer.INSTANCE;

    @AsFinalField
    private transient @NotNull RootNode root = new RootNode(null);
    private transient Reader reader;

    private static final List<LanguageDealer> LANGUAGE_DEALER_LIST =
            new ArrayList<>(
                    Arrays.asList(
                            X8lDealer.INSTANCE,
                            JsonDealer.INSTANCE,
                            XmlDealer.INSTANCE
                    )
            );

    /**
     * <p>addToLanguageDealerList.</p>
     *
     * @param languageDealer a {@link com.xenoamess.x8l.dealers.LanguageDealer} object.
     */
    public static void addToLanguageDealerList(@NotNull LanguageDealer languageDealer) {
        LANGUAGE_DEALER_LIST.add(languageDealer);
    }

    /**
     * <p>getLanguageDealerListCopy.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public static @NotNull List<LanguageDealer> getLanguageDealerListCopy() {
        return new LinkedList<>(LANGUAGE_DEALER_LIST);
    }

    /**
     * <p>suspectDealer.</p>
     *
     * @param nameString a {@link java.lang.String} object.
     * @param originalList a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    public static @NotNull List<LanguageDealer> suspectDealer(@NotNull String nameString,
                                                              @NotNull List<LanguageDealer> originalList) {
        List<LanguageDealer> res = new ArrayList<>(originalList);
        if (StringUtils.endsWithIgnoreCase(nameString, STRING_JSON)) {
            res.remove(JsonDealer.INSTANCE);
            res.add(0, JsonDealer.INSTANCE);
        } else if (StringUtils.endsWithIgnoreCase(nameString, STRING_XML) || StringUtils.endsWithIgnoreCase(nameString, STRING_XSD)) {
            res.remove(XmlDealer.INSTANCE);
            res.add(0, XmlDealer.INSTANCE);
        } else if (StringUtils.endsWithIgnoreCase(nameString, STRING_X8L)) {
            res.remove(X8lDealer.INSTANCE);
            res.add(0, X8lDealer.INSTANCE);
        } else if (StringUtils.endsWithIgnoreCase(nameString, STRING_JSON)) {
            res.remove(JsonDealer.INSTANCE);
            res.add(0, JsonDealer.INSTANCE);
        } else if (nameString.contains(STRING_XML) || nameString.contains(STRING_XSD)) {
            res.remove(XmlDealer.INSTANCE);
            res.add(0, XmlDealer.INSTANCE);
        } else if (nameString.contains(STRING_X8L)) {
            res.remove(X8lDealer.INSTANCE);
            res.add(0, X8lDealer.INSTANCE);
        }
        return res;
    }

    /**
     * <p>Getter for the field <code>root</code>.</p>
     *
     * @return a {@link com.xenoamess.x8l.RootNode} object.
     */
    public @NotNull RootNode getRoot() {
        return root;
    }

    /*
     * Path
     */

    /**
     * <p>load.</p>
     *
     * @param path a {@link java.nio.file.Path} object.
     * @param dealer a {@link com.xenoamess.x8l.dealers.LanguageDealer} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(@Nullable Path path, @NotNull LanguageDealer dealer) throws IOException {
        if (path == null || !Files.isReadable(path)) {
            throw new FileNotFoundException(path == null ? "null" : path.toString());
        }
        X8lTree res;
        try (
                Reader reader = Files.newBufferedReader(path)
        ) {
            res = load(reader, dealer);
        }
        return res;
    }

    /**
     * <p>load.</p>
     *
     * @param path a {@link java.nio.file.Path} object.
     * @param possibleDealerList a {@link java.util.List} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(@Nullable Path path, @NotNull List<LanguageDealer> possibleDealerList) throws IOException {
        if (path == null || !Files.isReadable(path)) {
            throw new FileNotFoundException(path == null ? "null" : path.toString());
        }
        X8lTree res;
        try (
                Reader reader = Files.newBufferedReader(path)
        ) {
            res = load(reader, possibleDealerList);
        }
        return res;
    }

    /**
     * <p>load.</p>
     *
     * @param path a {@link java.nio.file.Path} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(@Nullable Path path) throws IOException {
        if (path == null || !Files.isReadable(path)) {
            throw new FileNotFoundException(path == null ? "null" : path.toString());
        }
        return load(path, suspectDealer(path.toString(), getLanguageDealerListCopy()));
    }

    /**
     * <p>save.</p>
     *
     * @param path a {@link java.nio.file.Path} object.
     * @param x8lTree a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static void save(@Nullable Path path, @NotNull X8lTree x8lTree) throws IOException {
        if (path == null || !Files.isReadable(path)) {
            throw new FileNotFoundException(path == null ? "null" : path.toString());
        }
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }

        try (
                Writer writer = Files.newBufferedWriter(path)
        ) {
            x8lTree.write(writer);
        }
    }

    /*
     * FileObject
     */

    /**
     * <p>load.</p>
     *
     * @param fileObject a {@link org.apache.commons.vfs2.FileObject} object.
     * @param dealer a {@link com.xenoamess.x8l.dealers.LanguageDealer} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(
            @Nullable FileObject fileObject,
            @NotNull LanguageDealer dealer
    ) throws IOException {
        if (fileObject == null || !fileObject.exists()) {
            throw new FileNotFoundException(fileObject == null ? "null" : fileObject.toString());
        }
        X8lTree res;
        try (
                InputStream inputStream = fileObject.getContent().getInputStream()
        ) {
            res = load(inputStream, dealer);
        }
        return res;
    }

    /**
     * <p>load.</p>
     *
     * @param fileObject a {@link org.apache.commons.vfs2.FileObject} object.
     * @param possibleDealerList a {@link java.util.List} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(
            @Nullable FileObject fileObject,
            @NotNull List<LanguageDealer> possibleDealerList
    ) throws IOException {
        if (fileObject == null || !fileObject.exists()) {
            throw new FileNotFoundException(fileObject == null ? "null" : fileObject.toString());
        }
        X8lTree res;
        try (
                InputStream inputStream = fileObject.getContent().getInputStream()
        ) {
            res = load(inputStream, possibleDealerList);
        }
        return res;
    }

    /**
     * <p>load.</p>
     *
     * @param fileObject a {@link org.apache.commons.vfs2.FileObject} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(@Nullable FileObject fileObject) throws IOException {
        if (fileObject == null || !fileObject.exists()) {
            throw new FileNotFoundException(fileObject == null ? "null" : fileObject.toString());
        }
        return load(fileObject, suspectDealer(fileObject.getName().getBaseName(), getLanguageDealerListCopy()));
    }

    /**
     * <p>save.</p>
     *
     * @param fileObject a {@link org.apache.commons.vfs2.FileObject} object.
     * @param x8lTree a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static void save(@Nullable FileObject fileObject, @NotNull X8lTree x8lTree) throws IOException {
        if (fileObject == null) {
            throw new FileNotFoundException("null");
        }
        try (
                OutputStream outputStream = fileObject.getContent().getOutputStream();
                Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)
        ) {
            x8lTree.write(writer);
        }
    }

    /*
     * File
     */

    /**
     * <p>load.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param dealer a {@link com.xenoamess.x8l.dealers.LanguageDealer} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(@Nullable File file, @NotNull LanguageDealer dealer) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new FileNotFoundException(file == null ? "null" : file.getAbsolutePath());
        }
        X8lTree res;
        try (Reader reader = buffer(new FileReader(file))) {
            res = load(reader, dealer);
        }
        return res;
    }

    /**
     * <p>load.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param possibleDealerList a {@link java.util.List} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(@Nullable File file, @NotNull List<LanguageDealer> possibleDealerList) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new FileNotFoundException(file == null ? "null" : file.getAbsolutePath());
        }
        X8lTree res;
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            res = load(reader, possibleDealerList);
        }
        return res;
    }

    /**
     * <p>load.</p>
     *
     * @param file a {@link java.io.File} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(@Nullable File file) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new FileNotFoundException(file == null ? "null" : file.getAbsolutePath());
        }
        return load(file, suspectDealer(file.getName(), getLanguageDealerListCopy()));
    }

    /**
     * <p>save.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param x8lTree a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static void save(@Nullable File file, @NotNull X8lTree x8lTree) throws IOException {
        if (file == null || (file.exists() && !file.isFile())) {
            throw new FileNotFoundException(file == null ? "null" : file.getAbsolutePath());
        }
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            if (!file.createNewFile()) {
                LOGGER.error("File not exist when check but is already there when try to create it. Will " +
                        "use it directly.");
            }
        }

        try (Writer writer = buffer(new FileWriter(file))) {
            x8lTree.write(writer);
        }
    }

    /**
     * <p>load.</p>
     *
     * @param string a {@link java.lang.String} object.
     * @param dealer a {@link com.xenoamess.x8l.dealers.LanguageDealer} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     */
    public static @NotNull X8lTree load(@NotNull String string, @NotNull LanguageDealer dealer) {
        X8lTree res;
        try (
                StringReader stringReader = new StringReader(string)
        ) {
            res = new X8lTree(stringReader, dealer);
            res.parse();
        } catch (Exception e) {
            throw new X8lGrammarException("X8lTree.load(X8lTree x8lTree) fails. Really dom't know why.", e);
        }
        return res;
    }

    /**
     * <p>load.</p>
     *
     * @param string a {@link java.lang.String} object.
     * @param possibleDealerList a {@link java.util.List} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     */
    public static @NotNull X8lTree load(@NotNull String string, @NotNull List<LanguageDealer> possibleDealerList) {
        for (LanguageDealer dealer : possibleDealerList) {
            try (
                    StringReader stringReader = new StringReader(string)
            ) {
                X8lTree res = new X8lTree(stringReader, dealer);
                res.parse();
                return res;
            } catch (Exception e) {
                LOGGER.debug("Try to use a dealer to load a X8lTree but failed. dealer:{}, treeString:{}", dealer,
                        string, e);
            }
        }
        throw new X8lGrammarException("None of my dealers can parse this. Sorry. details are in the log.");
    }

    /**
     * <p>load.</p>
     *
     * @param string a {@link java.lang.String} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     */
    public static @NotNull X8lTree load(@NotNull String string) {
        return load(string, getLanguageDealerListCopy());
    }

    /**
     * <p>save.</p>
     *
     * @param x8lTree a {@link com.xenoamess.x8l.X8lTree} object.
     * @return a {@link java.lang.String} object.
     */
    public static @NotNull String save(@NotNull X8lTree x8lTree) {
        String res;
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

    /**
     * <p>load.</p>
     *
     * @param inputStream a {@link java.io.InputStream} object.
     * @param dealer a {@link com.xenoamess.x8l.dealers.LanguageDealer} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(@NotNull InputStream inputStream, @NotNull LanguageDealer dealer) throws IOException {
        X8lTree x8lTree;
        try (Reader reader = buffer(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            x8lTree = load(reader, dealer);
        }
        return x8lTree;
    }

    /**
     * <p>load.</p>
     *
     * @param inputStream a {@link java.io.InputStream} object.
     * @param possibleDealerList a {@link java.util.List} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(@NotNull InputStream inputStream,
                                        @NotNull List<LanguageDealer> possibleDealerList) throws IOException {
        return load(IOUtils.toString(inputStream, StandardCharsets.UTF_8), possibleDealerList);
    }

    /**
     * <p>load.</p>
     *
     * @param inputStream a {@link java.io.InputStream} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(@NotNull InputStream inputStream) throws IOException {
        return load(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
    }

    /**
     * <p>save.</p>
     *
     * @param outputStream a {@link java.io.OutputStream} object.
     * @param x8lTree a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static void save(@NotNull OutputStream outputStream, @NotNull X8lTree x8lTree) throws IOException {
        try (
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                Writer writer = new OutputStreamWriter(bufferedOutputStream, StandardCharsets.UTF_8)
        ) {
            x8lTree.write(writer);
        }
    }

    /*
     * Reader,Writer
     */

    /**
     * <p>load.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     * @param dealer a {@link com.xenoamess.x8l.dealers.LanguageDealer} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(@NotNull Reader reader, @NotNull LanguageDealer dealer) throws IOException {
        X8lTree x8lTree = new X8lTree(reader, dealer);
        x8lTree.parse();
        return x8lTree;
    }

    /**
     * <p>load.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     * @param possibleDealerList a {@link java.util.List} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(@NotNull Reader reader, @NotNull List<LanguageDealer> possibleDealerList) throws IOException {
        return load(IOUtils.toString(reader), possibleDealerList);
    }

    /**
     * <p>load.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static @NotNull X8lTree load(@NotNull Reader reader) throws IOException {
        return load(IOUtils.toString(reader));
    }

    /**
     * <p>save.</p>
     *
     * @param writer a {@link java.io.Writer} object.
     * @param x8lTree a {@link com.xenoamess.x8l.X8lTree} object.
     * @throws java.io.IOException if any.
     */
    public static void save(@NotNull Writer writer, @NotNull X8lTree x8lTree) throws IOException {
        x8lTree.write(writer);
    }

    /**
     * <p>Constructor for X8lTree.</p>
     */
    public X8lTree() {
        this(null, X8lDealer.INSTANCE);
    }

    /**
     * <p>Constructor for X8lTree.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     * @param languageDealer a {@link com.xenoamess.x8l.dealers.LanguageDealer} object.
     */
    public X8lTree(@Nullable Reader reader, @NotNull LanguageDealer languageDealer) {
        this.setReader(reader);
        this.setLanguageDealer(languageDealer);
    }

    /**
     * <p>Constructor for X8lTree.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     * @param languageDealer a {@link com.xenoamess.x8l.dealers.LanguageDealer} object.
     * @param readItNow a boolean.
     * @throws java.io.IOException if any.
     */
    public X8lTree(@Nullable Reader reader, @NotNull LanguageDealer languageDealer, boolean readItNow) throws IOException {
        this.setReader(reader);
        this.setLanguageDealer(languageDealer);
        if (readItNow) {
            if (reader != null) {
                this.read(reader, this.getLanguageDealer());
            } else {
                throw new X8lGrammarException("You pass a null reader and want the tree read it now? No way.");
            }
        }
    }

    /**
     * <p>Constructor for X8lTree.</p>
     *
     * @param original a {@link com.xenoamess.x8l.X8lTree} object.
     */
    public X8lTree(@Nullable X8lTree original) {
        this.setReader(null);
        if (original != null) {
            this.setLanguageDealer(original.getLanguageDealer());
            this.root.copy(original.copy().root);
        } else {
            this.setLanguageDealer(X8lDealer.INSTANCE);
        }
    }


    /**
     * <p>clear.</p>
     */
    public void clear() {
        this.getRoot().close();
    }

    /**
     * {@inheritDoc}
     *
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

    /**
     * <p>read.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void read() throws IOException {
        Reader reader = this.getReader();
        if (reader != null) {
            read(reader);
        } else {
            throw new X8lGrammarException("You cannot read now. Your reader is null.");
        }
    }

    /**
     * <p>read.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     * @throws java.io.IOException if any.
     */
    public void read(@NotNull Reader reader) throws IOException {
        read(reader, this.getLanguageDealer());
    }

    /**
     * <p>read.</p>
     *
     * @param languageDealer a {@link com.xenoamess.x8l.dealers.LanguageDealer} object.
     * @throws java.io.IOException if any.
     */
    public void read(@NotNull LanguageDealer languageDealer) throws IOException {
        Reader reader = this.getReader();
        if (reader != null) {
            read(reader, languageDealer);
        } else {
            throw new X8lGrammarException("You cannot read now. Your reader is null.");
        }
    }

    /**
     * will not close the reader after reading.
     *
     * @param reader         reader
     * @param languageDealer languageDealer
     * @throws java.io.IOException java.io.IOException
     */
    public void read(@NotNull Reader reader, @NotNull LanguageDealer languageDealer) throws IOException {
        this.getRoot().read(reader, languageDealer);
    }


    /**
     * <p>write.</p>
     *
     * @param writer a {@link java.io.Writer} object.
     * @throws java.io.IOException if any.
     */
    public void write(@NotNull Writer writer) throws IOException {
        this.write(writer, this.getLanguageDealer());
    }

    /**
     * close the writer after writing.
     *
     * @param writer         writer
     * @param languageDealer languageDealer
     * @throws java.io.IOException java.io.IOException
     */
    public void write(@NotNull Writer writer, @NotNull LanguageDealer languageDealer) throws IOException {
        this.getRoot().write(writer, languageDealer);
        writer.close();
    }


    /**
     * <p>parse.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void parse() throws IOException {
        Reader reader = this.getReader();
        if (reader != null) {
            this.read(reader);
        } else {
            throw new X8lGrammarException("You cannot read now. Your reader is null.");
        }
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
    @SuppressWarnings("UnusedReturnValue")
    public @NotNull X8lTree trim() {
        this.getRoot().trim();
        return this;
    }

    /**
     * trim every TextNode's text String.
     * (and delete them if it be blank)
     * trim every commentNode's text String.
     *
     * @return the original X8lTree is trimmed and then returned.
     */
    @SuppressWarnings("UnusedReturnValue")
    public @NotNull X8lTree trimForce() {
        this.getRoot().trimForce();
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
    public @NotNull X8lTree format() {
        this.trim();
        this.getRoot().format(-1);
        return this;
    }


    /**
     * <p>transcodeText.</p>
     *
     * @param originalString a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static @NotNull String transcodeText(@NotNull String originalString) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < originalString.length(); i++) {
            char chr = originalString.charAt(i);
            if (chr == '<' || chr == '>' || chr == '%' || chr == '&') {
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
     * @param originalString original String
     * @return transCoded String
     */
    public static @NotNull String transcodeKey(@NotNull String originalString) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < originalString.length(); i++) {
            char chr = originalString.charAt(i);
            if (chr == '=' || chr == '<' || chr == '>' || chr == '%' || Character.isWhitespace(chr)) {
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
     * @param originalString original String
     * @return transCoded String
     */
    public static @NotNull String transcodeValue(@NotNull String originalString) {
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
     * @param originalString original String
     * @return transCoded String
     */
    public static @NotNull String transcodeComment(@NotNull String originalString) {
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


    /**
     * <p>untranscode.</p>
     *
     * @param transcodedString a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static @NotNull String untranscode(@NotNull String transcodedString) {
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

    /** {@inheritDoc} */
    @Override
    public @NotNull String toString() {
        return save(this);
    }


    /**
     * {@inheritDoc}
     * <p>
     * Notice that X8lTree.equals do not compare X8lTree.reader.
     * If two X8lTrees only differ in reader, then return true.
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof X8lTree)) {
            return false;
        }
        X8lTree x8lTree = (X8lTree) object;

        return this.getLanguageDealer().equals(x8lTree.getLanguageDealer())
                && this.getRoot().equals(x8lTree.getRoot());
    }

    /** {@inheritDoc} */
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
    private void readObject(@NotNull ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        //noinspection ConstantConditions
        if (this.root == null) {
            /*
             * this IS actually possible due to some details of serializing.
             * so don't delete this if.
             */
            this.root = new RootNode(null);
        }
        try (Reader reader = buffer(new InputStreamReader(objectInputStream, StandardCharsets.UTF_8))) {
            this.read(reader);
        }
    }

    /**
     * used by Serializable
     *
     * @param objectOutputStream objectOutputStream
     * @throws IOException IOException
     */
    private void writeObject(@NotNull ObjectOutputStream objectOutputStream)
            throws IOException {
        objectOutputStream.defaultWriteObject();
        try (Writer writer = new OutputStreamWriter(objectOutputStream, StandardCharsets.UTF_8)) {
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
    public void append(@Nullable X8lTree patch) {
        if (patch == null) {
            return;
        }
        this.getRoot().appendAll(patch.getRoot().getChildren());
    }

    /**
     * <p>applyToAllNodes.</p>
     *
     * @param function a {@link java.util.function.Function} object.
     * @param <T> a T object.
     * @return a {@link java.util.List} object.
     */
    @SuppressWarnings("UnusedReturnValue")
    public <T> @NotNull List<T> applyToAllNodes(@NotNull Function<AbstractTreeNode, T> function) {
        ArrayList<T> res = new ArrayList<>();
        this.getRoot().applyToAllNodes(res, function);
        return res;
    }

    /**
     * <p>Getter for the field <code>languageDealer</code>.</p>
     *
     * @return a {@link com.xenoamess.x8l.dealers.LanguageDealer} object.
     */
    public @NotNull LanguageDealer getLanguageDealer() {
        return languageDealer;
    }

    /**
     * <p>Setter for the field <code>languageDealer</code>.</p>
     *
     * @param languageDealer a {@link com.xenoamess.x8l.dealers.LanguageDealer} object.
     */
    public void setLanguageDealer(@NotNull LanguageDealer languageDealer) {
        this.languageDealer = languageDealer;
    }

    /**
     * <p>Getter for the field <code>reader</code>.</p>
     *
     * @return a {@link java.io.Reader} object.
     */
    public @Nullable Reader getReader() {
        return reader;
    }

    /**
     * <p>Setter for the field <code>reader</code>.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     */
    public void setReader(@Nullable Reader reader) {
        this.reader = reader;
    }

    /**
     * make a deep copy of this X8lTree.
     *
     * @return a deep copy of this
     */
    public @NotNull X8lTree copy() {
        return X8lTree.load(this.toString(), this.languageDealer);
    }

    //---fetch---

    /**
     * <p>fetch.</p>
     *
     * @param x8lPath a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public @NotNull List<Object> fetch(@NotNull String x8lPath) {
        return this.getRoot().fetch(x8lPath);
    }

    /**
     * <p>fetch.</p>
     *
     * @param x8lPath a {@link java.lang.String} object.
     * @param tClass a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a {@link java.util.List} object.
     */
    public <T> @NotNull List<T> fetch(@NotNull String x8lPath, @NotNull Class<T> tClass) {
        return this.getRoot().fetch(x8lPath, tClass);
    }

    /**
     * <p>fetch.</p>
     *
     * @param x8lDataBeanFieldScheme a {@link com.xenoamess.x8l.databind.X8lDataBeanFieldScheme} object.
     * @param x8lPath a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public @NotNull List<Object> fetch(@NotNull X8lDataBeanFieldScheme x8lDataBeanFieldScheme,
                                       @NotNull String x8lPath) {
        return this.getRoot().fetch(x8lDataBeanFieldScheme, x8lPath);
    }

    /**
     * <p>fetch.</p>
     *
     * @param x8lDataBeanFieldScheme a {@link com.xenoamess.x8l.databind.X8lDataBeanFieldScheme} object.
     * @param x8lPaths an array of x8lPath
     * @return a {@link java.util.List} object.
     */
    public @NotNull List<Object> fetch(@NotNull X8lDataBeanFieldScheme x8lDataBeanFieldScheme,
                                       @NotNull String[] x8lPaths) {
        return this.getRoot().fetch(x8lDataBeanFieldScheme, x8lPaths);
    }

    /**
     * <p>fetch.</p>
     *
     * @param x8lDataBeanFieldScheme a {@link com.xenoamess.x8l.databind.X8lDataBeanFieldScheme} object.
     * @param x8lPaths an array of x8lPath
     * @param tClass a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a {@link java.util.List} object.
     */
    public <T> @NotNull List<T> fetch(
            @NotNull X8lDataBeanFieldScheme x8lDataBeanFieldScheme,
            @NotNull String[] x8lPaths,
            @NotNull Class<T> tClass
    ) {
        return this.getRoot().fetch(x8lDataBeanFieldScheme, x8lPaths, tClass);
    }
}

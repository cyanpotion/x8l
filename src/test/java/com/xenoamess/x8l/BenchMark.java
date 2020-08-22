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

import com.xenoamess.x8l.dealers.JsonDealer;
import com.xenoamess.x8l.dealers.X8lDealer;
import com.xenoamess.x8l.dealers.XmlDealer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.Test;
import static org.apache.commons.io.IOUtils.buffer;

/**
 * @author XenoAmess
 */
public class BenchMark {

    /**
     * test file : src/main/test/java/resources/citylots.json
     * resource uri: https://github.com/zeMirco/sf-city-lots-json
     * License:
     * Copyright (C) 2012 [Mirco Zeiss](mailto: mirco.zeiss@gmail.com)
     * <p>
     * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
     * documentation files (the "Software"), to deal in the Software without restriction, including without
     * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
     * Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
     * <p>
     * The above copyright notice and this permission notice shall be included in all copies or substantial portions
     * of the Software.
     * <p>
     * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
     * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
     * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
     * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
     * DEALINGS IN THE SOFTWARE.
     */
    @Test
    public void testJson_citylots() throws IOException {
        this.testJson("citylots");
    }

    /**
     * test file : src/main/test/java/resources/abstract9.xml
     * resource uri: ftp://ftpmirror.your.org/pub/wikimedia/dumps/20110115/enwiki-20110115-abstract9.xml
     */
    @Test
    public void testXml_enwiki_20110115_abstract9() throws IOException {
        this.testXml("enwiki-20110115-abstract9");
    }

    /**
     * test file : src/main/test/java/resources/abstract10.xml
     * resource uri: ftp://ftpmirror.your.org/pub/wikimedia/dumps/20110115/enwiki-20110115-abstract10.xml
     */
    @Test
    public void testXml_enwiki_20110115_abstract10() throws IOException {
        this.testXml("enwiki-20110115-abstract10");
    }

    /**
     * test file : src/main/test/java/resources/abstract11.xml
     * resource uri: ftp://ftpmirror.your.org/pub/wikimedia/dumps/20110115/enwiki-20110115-abstract11.xml
     */
    @Test
    public void testXml_enwiki_20110115_abstract11() throws IOException {
        this.testXml("enwiki-20110115-abstract11");
    }

    public void testXml(String filePathString) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(
                this.getClass().getResourceAsStream("/" + filePathString + ".zip"));
             BufferedInputStream bufferedInputStream = new BufferedInputStream(zipInputStream);
             Reader reader = new InputStreamReader(bufferedInputStream, StandardCharsets.UTF_8)
        ) {
            zipInputStream.getNextEntry();
            X8lTree tree = new X8lTree(reader, XmlDealer.INSTANCE, true);
            //noinspection ResultOfMethodCallIgnored
            new File("out").mkdirs();
            Writer writer;
            writer = buffer(new FileWriter("out/" + filePathString + ".x8l"));
            tree.write(writer, X8lDealer.INSTANCE);
            writer = buffer(new FileWriter("out/" + filePathString + ".xml"));
            tree.write(writer, XmlDealer.INSTANCE);
        }
    }

    public void testJson(String filePathString) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(
                this.getClass().getResourceAsStream("/" + filePathString + ".zip"));
             BufferedInputStream bufferedInputStream = new BufferedInputStream(zipInputStream);
             Reader reader = new InputStreamReader(bufferedInputStream, StandardCharsets.UTF_8)
        ) {
            zipInputStream.getNextEntry();
            X8lTree tree = new X8lTree(reader, JsonDealer.INSTANCE, true);
            //noinspection ResultOfMethodCallIgnored
            new File("out").mkdirs();
            Writer writer;
            writer = buffer(new FileWriter("out/" + filePathString + ".x8l"));
            tree.write(writer, X8lDealer.INSTANCE);
            writer = buffer(new FileWriter("out/" + filePathString + ".json"));
            tree.write(writer, JsonDealer.INSTANCE);
        }
    }
}

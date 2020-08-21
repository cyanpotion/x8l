/*
 * MIT License
 *
 * Copyright (c) 2020 XenoAmess
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

import com.xenoamess.x8l.dealers.XmlDealer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class XmlToX8lConvertLostCommentNodeTest {
    @Test
    public void test() throws IOException {
        String originalXmlString;
        try (InputStream inputStream = this.getClass().getResource("/testPom.xml").openStream();
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)
        ) {
            originalXmlString = IOUtils.toString(bufferedInputStream, StandardCharsets.UTF_8);
        }
        assertNotNull(originalXmlString);

        X8lTree x8lTree;
        try (InputStream inputStream = this.getClass().getResource("/testPom.xml").openStream();
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)
        ) {
            x8lTree = X8lTree.load(bufferedInputStream, XmlDealer.INSTANCE);
        }
        assertNotNull(x8lTree);

        String getXmlString = x8lTree.toString();
        new File("out").mkdir();
        try (FileOutputStream fileOutputStream = new FileOutputStream("out/testPom.out.xml");
             OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
        ) {
            writer.write(getXmlString);
        }
        assertNotEquals(originalXmlString, getXmlString);
    }
}

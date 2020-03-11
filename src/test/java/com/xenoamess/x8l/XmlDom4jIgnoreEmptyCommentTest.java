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

import org.apache.commons.io.IOUtils;
import org.dom4j.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlDom4jIgnoreEmptyCommentTest {
    @Test
    public void test() throws IOException, DocumentException {
        Document document = null;
        int nowNodeCount = 0;
        try (InputStream inputStream = this.getClass().getResource("/XmlToX8lConvertLostCommentNodeTestXml.xml").openStream();
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        ) {
            document = DocumentHelper.parseText(IOUtils.toString(bufferedInputStream));
        }
        for (int i = 0; i < document.getRootElement().nodeCount(); i++) {
            Node nowNode = document.getRootElement().node(i);
            System.out.println(nowNode.getText());
            if (nowNode instanceof Comment) {
                nowNodeCount++;
            }
        }
        assertEquals(nowNodeCount, 4);
    }
}

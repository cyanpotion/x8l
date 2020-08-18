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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class XmlDom4jICannotParseXSDTest {
    public static final String XML_STRING = "<?xml version=\"1.0\"?>\n" +
            "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" +
            "           targetNamespace=\"http://www.w3school.com.cn\"\n" +
            "           xmlns=\"http://www.w3school.com.cn\"\n" +
            "           elementFormDefault=\"qualified\">\n" +
            "\n" +
            "    <xs:element name=\"note\">\n" +
            "        <xs:complexType>\n" +
            "            <xs:sequence>\n" +
            "                <xs:element name=\"to\" type=\"xs:string\"/>\n" +
            "                <xs:element name=\"from\" type=\"xs:string\"/>\n" +
            "                <xs:element name=\"heading\" type=\"xs:string\"/>\n" +
            "                <xs:element name=\"body\" type=\"xs:string\"/>\n" +
            "            </xs:sequence>\n" +
            "        </xs:complexType>\n" +
            "    </xs:element>\n" +
            "\n" +
            "</xs:schema>";

    @Test
    public void commonTest() throws Exception {
        XmlTester.test(XML_STRING);
    }
}

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

import com.xenoamess.x8l.dealers.X8lDealer;
import com.xenoamess.x8l.dealers.XmlDealer;
import javax.xml.transform.Source;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.jupiter.api.Assertions;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.DOMDifferenceEngine;
import org.xmlunit.diff.DifferenceEngine;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlTester {
    public static void test(String xmlString1) throws Exception {
        int nowNodeCount = 0;
        Document document = DocumentHelper.parseText(xmlString1);

        X8lTree x8lTree2 = X8lTree.load(xmlString1, XmlDealer.INSTANCE);
        x8lTree2.setLanguageDealer(XmlDealer.INSTANCE);
        String xmlString2 = x8lTree2.toString();
        x8lTree2.setLanguageDealer(X8lDealer.INSTANCE);
        String x8lString2 = x8lTree2.toString();

        X8lTree x8lTree3 = X8lTree.load(x8lString2, X8lDealer.INSTANCE);
        x8lTree3.setLanguageDealer(XmlDealer.INSTANCE);
        String xmlString3 = x8lTree3.toString();
        x8lTree3.setLanguageDealer(X8lDealer.INSTANCE);
        String x8lString3 = x8lTree3.toString();

        assertEqualXml(xmlString1, xmlString2);
        assertEqualXml(xmlString1, xmlString3);
        assertEquals(x8lString2, x8lString3);
    }

    private static void assertEqualXml(final String xmlString1, final String xmlString2) {
        Source source1 = Input.fromString(xmlString1).build();
        Source source2 = Input.fromString(xmlString2).build();
        DifferenceEngine diff = new DOMDifferenceEngine();
        diff.addDifferenceListener(new ComparisonListener() {
            public void comparisonPerformed(Comparison comparison, ComparisonResult outcome) {
                final String result = "--------------------\n"
                        + "found a difference:\n"
                        + comparison + "\n"
                        + "outcome:\n"
                        + outcome + "\n"
                        + "source1:\n"
                        + xmlString1 + "\n"
                        + "source2:\n"
                        + xmlString2 + "\n"
                        + "--------------------\n";
                if (outcome != ComparisonResult.EQUAL && outcome != ComparisonResult.SIMILAR) {
                    Assertions.fail(result);
                } else if (outcome == ComparisonResult.SIMILAR) {
                    System.out.println(result);
                }
            }
        });
        diff.compare(source1, source2);
    }
}

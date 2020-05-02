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

package com.xenoamess.x8l.dealers;

import com.xenoamess.x8l.X8lTree;
import org.junit.jupiter.api.Test;

import java.io.*;

public class JsonDealerTest {
    @Test
    public void test() throws IOException {
        try (Reader reader = new StringReader("{\n" +
                "    \"glossary\": {\n" +
                "        \"title\": \"example glossary\",\n" +
                "\t\t\"GlossDiv\": {\n" +
                "            \"title\": 50,\n" +
                "\t\t\t\"GlossList\": {\n" +
                "                \"GlossEntry\": {\n" +
                "                    \"ID\": \"SGML\",\n" +
                "\t\t\t\t\t\"SortAs\": \"SGML\",\n" +
                "\t\t\t\t\t\"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
                "\t\t\t\t\t\"Acronym\": \"SGML\",\n" +
                "\t\t\t\t\t\"Abbrev\": \"ISO 8879:1986\",\n" +
                "\t\t\t\t\t\"GlossDef\": {\n" +
                "                        \"para\": \"A meta-markup language, used to create markup languages such" +
                " as DocBook.\",\n" +
                "\t\t\t\t\t\t\"GlossSeeAlso\": [null, \"XML\", {\"a\":104}]\n" +
                "                    },\n" +
                "\t\t\t\t\t\"GlossSee\": \"markup\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}")) {

            X8lTree tree3 = new X8lTree(reader, JsonDealer.INSTANCE);
            tree3.read();

            try (Writer writer =
                         new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out/jsonDemoOut.x8l")))) {
                tree3.write(writer, X8lDealer.INSTANCE);
            }
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out/jsonDemoOut" +
                    ".json")))) {
                tree3.write(writer, JsonDealer.INSTANCE);
            }

            try (Writer writer = new StringWriter()) {
                tree3.applyToAllNodes(abstractTreeNode -> {
                    try {
                        abstractTreeNode.write(writer, tree3.getLanguageDealer());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                });
            }

        }
    }

    @Test
    public void test2() throws IOException {
        try (Reader reader = new StringReader("{" +
                "\"a\":\"1\"" +
                "/*here*/" +
                "}"
        )) {
            X8lTree tree3 = new X8lTree(reader, JsonDealer.INSTANCE);
            tree3.read();
        }
    }
}

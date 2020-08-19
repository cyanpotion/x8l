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

import com.xenoamess.x8l.dealers.JsonDealer;
import com.xenoamess.x8l.dealers.X8lDealer;
import com.xenoamess.x8l.dealers.XmlDealer;
import java.io.File;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SettingFileTest {
    @Test
    public void testJson() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/RpgModuleDemoSettings.x8l");
        X8lTree originalX8lTree = X8lTree.load(inputStream, X8lDealer.INSTANCE);
        originalX8lTree.trimForce();


        X8lTree jsonTree = originalX8lTree.copy();
        jsonTree.setLanguageDealer(JsonDealer.INSTANCE);
        X8lTree.save(new File("out/RpgModuleDemoSettings.json"), jsonTree);

        X8lTree xmlTree = originalX8lTree.copy();
        xmlTree.setLanguageDealer(XmlDealer.INSTANCE);
        X8lTree.save(new File("out/RpgModuleDemoSettings.xml"), xmlTree);

        X8lTree jsonTree2 = X8lTree.load(new File("out/RpgModuleDemoSettings.json"));
        X8lTree xmlTree2 = X8lTree.load(new File("out/RpgModuleDemoSettings.xml"));
        jsonTree2.setLanguageDealer(X8lDealer.INSTANCE);
        xmlTree2.setLanguageDealer(X8lDealer.INSTANCE);

        Assertions.assertNotEquals(originalX8lTree, xmlTree2);
        Assertions.assertNotEquals(originalX8lTree, jsonTree2);
    }
}

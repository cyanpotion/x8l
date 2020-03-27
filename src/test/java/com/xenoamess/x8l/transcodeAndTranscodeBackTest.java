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
import com.xenoamess.x8l.dealers.LanguageDealer;
import com.xenoamess.x8l.dealers.X8lDealer;
import com.xenoamess.x8l.dealers.XmlDealer;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class transcodeAndTranscodeBackTest {
    private static final String[] strictTests = new String[]{
            "<a>b>"
            , "<< ><a>b>"
            , "a<a>b>"
    };
    private static String[] loseTests;

    static {
        try {
            loseTests = new String[]{
                    IOUtils.toString(transcodeAndTranscodeBackTest.class.getResource("/RpgModuleDemoSettings.x8l"))

            };
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void transcodeAndTranscodeBackTest() {

        for (String tree : strictTests) {
            transcodeAndTranscodeBackTestSingle(tree, XmlDealer.INSTANCE);
            transcodeAndTranscodeBackTestSingle(tree, JsonDealer.INSTANCE);
        }

        for (String tree : loseTests) {
            doLoseTest(tree, JsonDealer.INSTANCE);
        }

    }

    public void transcodeAndTranscodeBackTestSingle(String x8lString, LanguageDealer languageDealer) {
        X8lTree x8lTree = X8lTree.load(x8lString);
        x8lTree.setLanguageDealer(languageDealer);
        String transcodeString = X8lTree.save(x8lTree);
        X8lTree x8lTree2 = X8lTree.load(transcodeString, languageDealer);
        x8lTree2.setLanguageDealer(X8lDealer.INSTANCE);
        String x8lString2 = X8lTree.save(x8lTree2);
        System.out.println("1:");
        System.out.println(x8lString);
        System.out.println("2:");
        System.out.println(transcodeString);
        assertEquals(x8lString, x8lString2);
    }

    public void doLoseTest(String x8lString, LanguageDealer languageDealer) {
        X8lTree x8lTree = X8lTree.load(x8lString);
        x8lTree.setLanguageDealer(languageDealer);
        String transcodeString = X8lTree.save(x8lTree);
        X8lTree x8lTree2 = X8lTree.load(transcodeString, languageDealer);
        x8lTree2.setLanguageDealer(X8lDealer.INSTANCE);
        String x8lString2 = X8lTree.save(x8lTree2);
        X8lTree x8lTree3 = X8lTree.load(x8lString2);
        x8lTree3.setLanguageDealer(languageDealer);
        String transcodeString2 = X8lTree.save(x8lTree3);

        System.out.println("1:");
        System.out.println(x8lString);
        System.out.println("2:");
        System.out.println(transcodeString);
        assertEquals(transcodeString, transcodeString2);
    }


}

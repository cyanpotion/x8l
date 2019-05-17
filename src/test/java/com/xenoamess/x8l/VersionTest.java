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

import org.junit.jupiter.api.Test;

/**
 * @author XenoAmess
 */
public class VersionTest {
    public static final int compareVersionsTest(String va, String vb) {
        System.out.println("compareVersionsTest : " + va + " " + vb);
        int res = Version.compareVersions(va, vb);
        System.out.println("res : " + res);
        return res;
    }

    @Test
    public final void Test() {
        assert (compareVersionsTest("0.1.0-SNAPSHOT", "0.1.0") < 0);
        assert (compareVersionsTest("0.1.0-SNAPSHOT", "0.25.0") < 0);
        assert (compareVersionsTest("1.1.0-SNAPSHOT", "0.25.0") > 0);
        assert (compareVersionsTest("1.33", "5") < 0);
        assert (compareVersionsTest("1.33.0", "0.44.0") > 0);
        assert (compareVersionsTest("0.1.0-SNAPSHOT", "0.1.0-SNAPSHOT") == 0);
    }
}

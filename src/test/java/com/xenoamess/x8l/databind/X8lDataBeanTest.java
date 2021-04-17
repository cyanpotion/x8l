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

package com.xenoamess.x8l.databind;


import com.xenoamess.x8l.X8lTree;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class X8lDataBeanTest {


    @Test
    public void testTestDataBean1() {
        X8lTree x8lTree = X8lTree.load("<a>b>");
        TestDataBean1 testDataBean1 = X8lDataBeanUtil.buildFromX8lTree(TestDataBean1.class, x8lTree);

        assertEquals(
                "<a>b>",
                testDataBean1.nodeA.toString()
        );

        assertEquals(
                "b",
                testDataBean1.value
        );
    }

    //-----

    @Test
    public void testTestDataBean2() {
        X8lTree x8lTree = X8lTree.load("<a>b>");
        TestDataBean2 testDataBean2 = X8lDataBeanUtil.buildFromX8lTree(TestDataBean2.class, x8lTree);

        assertNull(testDataBean2.getNodeA());

        assertNull(testDataBean2.getValue());
    }

    //-----

    @Test
    public void testTestDataBean3() {
        X8lTree x8lTree = X8lTree.load("<a>b>");
        TestDataBean3 testDataBean3 = X8lDataBeanUtil.buildFromX8lTree(TestDataBean3.class, x8lTree);

        assertEquals(
                "<a>b>",
                testDataBean3.getNodeA().toString()
        );

        assertEquals(
                "b",
                testDataBean3.getValue()
        );

        assertNull(
                testDataBean3.getTestEmpty()
        );
    }

    @Test
    public void testTestDataBean4() {
        X8lTree x8lTree = X8lTree.load("<a>b>");
        TestDataBean4 testDataBean4 = X8lDataBeanUtil.buildFromX8lTree(TestDataBean4.class, x8lTree);

        assertEquals(
                "<a>b>",
                testDataBean4.getNodeA().toString()
        );

        assertEquals(
                "b",
                testDataBean4.getValue()
        );

        assertNull(
                testDataBean4.getTestEmpty()
        );
    }
}

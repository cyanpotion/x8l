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

public class TestDataBean4 implements X8lDataBean {
    public TestDataBean4() {

    }

    @X8lDataBeanFieldMark(paths = "CONTENT_NODE(a)")
    private Object nodeA;

    @X8lDataBeanFieldMark(paths = "CONTENT_NODE(a)>TEXT_NODE[0]>TEXT_CONTENT")
    private String value;

    @X8lDataBeanFieldMark(paths = "")
    private Object testEmpty;

    public Object getNodeA() {
        return nodeA;
    }

    public void setNodeA(Object nodeA) {
        this.nodeA = nodeA;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getTestEmpty() {
        return testEmpty;
    }

    public void setTestEmpty(Object testEmpty) {
        this.testEmpty = testEmpty;
    }
}

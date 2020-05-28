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

import java.io.IOException;
import java.util.List;

public class P3cConfigFileTest {
    @Test
    public void test() throws IOException {
        X8lTree x8lTree = X8lTree.load(this.getClass().getResourceAsStream("/p3c_config.default.x8l"));
        x8lTree.append(X8lTree.load(this.getClass().getResourceAsStream("/p3c_config.x8l")));
        List<Object> list = x8lTree.fetch("com.alibaba.p3c.pmd.config>rule_blacklist");
        System.out.println(list);
    }
}

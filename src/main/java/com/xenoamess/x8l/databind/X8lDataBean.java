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
import org.jetbrains.annotations.NotNull;

/**
 * <p>X8lDataBean interface.</p>
 *
 * @author XenoAmess
 * @version 2.2.3-SNAPSHOT
 */
public interface X8lDataBean {
    /**
     * build a X8lDataBean from a X8lTree.
     *
     * @param tClass class of X8lDataBean
     * @param x8lTree X8lTree
     * @param <T> class of X8lDataBean
     * @return the built X8lDataBean.
     */
    static <T extends X8lDataBean> @NotNull T buildFromX8lTree(@NotNull Class<T> tClass, @NotNull X8lTree x8lTree) {
        return X8lDataBeanUtil.buildFromX8lTree(tClass, x8lTree);
    }

    /**
     * load fields from a X8lTree.
     *
     * @param x8lTree X8lTree
     * @param <T> a T object.
     */
    default <T extends X8lDataBean> void loadFromX8lTree(@NotNull X8lTree x8lTree) {
        X8lDataBeanUtil.loadFromX8lTree(this, x8lTree);
    }
}

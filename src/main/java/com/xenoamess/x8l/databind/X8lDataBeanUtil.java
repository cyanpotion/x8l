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

import com.xenoamess.x8l.X8lGrammarException;
import com.xenoamess.x8l.X8lTree;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author XenoAmess
 */
public class X8lDataBeanUtil {
    public static <T extends X8lDataBean> @NotNull T buildFromX8lTree(@NotNull Class<T> tClass,
                                                                      @NotNull X8lTree x8lTree) {
        T t = null;
        try {
            t = tClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new X8lGrammarException("cannot create new instance for class " + tClass.getCanonicalName(), e);
        }
        loadFromX8lTree(t, x8lTree);
        return t;
    }

    @SuppressWarnings("unchecked")
    public static <T extends X8lDataBean> void loadFromX8lTree(T t, @NotNull X8lTree x8lTree) {
        Class<T> tClass = (Class<T>) t.getClass();
        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(tClass, X8lDataBeanFieldMark.class);
        for (Field field : fields) {
            X8lDataBeanFieldMark x8lDataBeanFieldMark = field.getAnnotation(X8lDataBeanFieldMark.class);
            X8lDataBeanFieldScheme x8lDataBeanFieldScheme = x8lDataBeanFieldMark.scheme();
            String path = x8lDataBeanFieldMark.path();

            //noinspection rawtypes
            Class parserClass = x8lDataBeanFieldMark.parser();

            //noinspection rawtypes
            Class fieldClass = field.getType();

            List<Object> list = x8lTree.fetch(x8lDataBeanFieldScheme, path);

            String functionName = x8lDataBeanFieldMark.functionName();
            if (StringUtils.isBlank(functionName)) {
                functionName = "get" + fieldClass.getSimpleName();
            }

            Object result;
            try {
                result = parserClass.getDeclaredMethod(functionName, List.class).invoke(null, list);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new X8lGrammarException("cannot find target function : " + tClass.getCanonicalName() + "." + functionName, e);
            }
            try {
                field.set(t, result);
            } catch (IllegalAccessException e) {
                try {
                    BeanUtils.setProperty(
                            t,
                            field.getName(),
                            result
                    );
                } catch (IllegalAccessException | InvocationTargetException e2) {
                    throw new X8lGrammarException("cannot set value : " + tClass.getCanonicalName() + "." + field.getName(), e2);
                }
            }
        }
    }
}

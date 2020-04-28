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

import com.xenoamess.commonx.java.lang.IllegalArgumentExceptionUtilsx;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * <p>GameManagerConfig class.</p>
 *
 * @author XenoAmess
 * @version 0.162.1-SNAPSHOT
 */
public class X8lDataBeanDefaultParser {

    /**
     * <p>getLastFromList.</p>
     *
     * @param list a {@link java.util.List} object.
     * @param <T> a T object.
     * @return a T object.
     */
    public static <T> @Nullable T getLastFromList(@NotNull List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    /**
     * <p>getLastFromListString.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a {@link java.lang.String} object.
     */
    public static @NotNull String getLastFromListString(@NotNull List<Object> list) {
        Object object = getLastFromList(list);
        return object == null ? "" : object.toString();
    }

    /**
     * <p>getint.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a int.
     */
    public static int getint(@NotNull List<Object> list) {
        return getInteger(getLastFromListString(list));
    }

    /**
     * <p>getInteger.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a {@link java.lang.Integer} object.
     */
    public static @NotNull Integer getInteger(@NotNull List<Object> list) {
        return getint(list);
    }

    /**
     * <p>getlong.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a long.
     */
    public static long getlong(@NotNull List<Object> list) {
        return getLong(getLastFromListString(list));
    }

    /**
     * <p>getLong.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a {@link java.lang.Long} object.
     */
    public static @NotNull Long getLong(@NotNull List<Object> list) {
        return getlong(list);
    }

    /**
     * <p>getboolean.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a boolean.
     */
    public static boolean getboolean(@NotNull List<Object> list) {
        return getBoolean(getLastFromListString(list));
    }

    /**
     * <p>getBoolean.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a {@link java.lang.Boolean} object.
     */
    public static @NotNull Boolean getBoolean(@NotNull List<Object> list) {
        return getboolean(list);
    }

    /**
     * <p>getfloat.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a float.
     */
    public static float getfloat(@NotNull List<Object> list) {
        return getFloat(getLastFromListString(list));
    }

    /**
     * <p>getFloat.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a {@link java.lang.Float} object.
     */
    public static @NotNull Float getFloat(@NotNull List<Object> list) {
        return getfloat(list);
    }

    /**
     * <p>getdouble.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a double.
     */
    public static double getdouble(@NotNull List<Object> list) {
        return getDouble(getLastFromListString(list));
    }

    /**
     * <p>getDouble.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a {@link java.lang.Double} object.
     */
    public static @NotNull Double getDouble(@NotNull List<Object> list) {
        return getdouble(list);
    }

    /**
     * <p>getString.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a {@link java.lang.String} object.
     */
    public static @NotNull String getString(@NotNull List<Object> list) {
        return getLastFromListString(list);
    }

    /**
     * <p>getObject.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a {@link java.lang.Object} object.
     */
    public static @Nullable Object getObject(@NotNull List<Object> list) {
        return getLastFromList(list);
    }

    /**
     * <p>getList.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    public static @NotNull List<Object> getList(@NotNull List<Object> list) {
        return list;
    }

    //-----

    /**
     * parse int from String.
     *
     * @param value string
     * @return int.
     */
    public static int getInteger(String value) {
        /*
         * if value is null or empty, we think that it have no value part,
         * and a key part alone is thought to mean -1.
         */
        if (StringUtils.isBlank(value)) {
            return -1;
        }

        return Integer.parseInt(value);
    }

    /**
     * parse long from String.
     *
     * @param value string
     * @return long.
     */
    public static long getLong(String value) {
        /*
         * if value is null or empty, we think that it have no value part,
         * and a key part alone is thought to mean -1.
         */
        if (StringUtils.isBlank(value)) {
            return -1;
        }

        return Long.parseLong(value);
    }


    /**
     * parse float from String.
     *
     * @param value string
     * @return float.
     */
    public static float getFloat(String value) {
        /*
         * if value is null or empty, we think that it have no value part,
         * and a key part alone is thought to mean NaN.
         */
        if (StringUtils.isBlank(value)) {
            return Float.NaN;
        }

        return Float.parseFloat(value);
    }

    /**
     * parse double from String.
     *
     * @param value string
     * @return double.
     */
    public static double getDouble(String value) {
        /*
         * if value is null or empty, we think that it have no value part,
         * and a key part alone is thought to mean NaN.
         */
        if (StringUtils.isBlank(value)) {
            return Double.NaN;
        }

        return Double.parseDouble(value);
    }

    /**
     * <p>getBoolean.</p>
     *
     * @param value value
     * @return a boolean.
     */
    public static boolean getBoolean(String value) {
        /*
         * if value is null or empty, we think that it have no value part,
         * and a key part alone is thought to mean true.
         */
        if (StringUtils.isBlank(value)) {
            return true;
        }

        switch (value.toLowerCase()) {
            case "1":
            case "true":
            case "yes":
                return true;
            case "0":
            case "false":
            case "no":
                return false;
            default:
                throw new IllegalArgumentException(value);
        }
    }

    //-----

    /**
     * <p>getInteger.</p>
     *
     * @param settingMap settingMap
     * @param key        a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static int getInteger(final Map<String, String> settingMap,
                                 final String key) {
        return getInteger(settingMap, key, -1);
    }

    /**
     * <p>getInteger.</p>
     *
     * @param settingMap   a {@link java.util.Map} object.
     * @param key          a {@link java.lang.String} object.
     * @param defaultValue a boolean.
     * @return a boolean.
     */
    public static int getInteger(final Map<String, String> settingMap,
                                 final String key, int defaultValue) {
        IllegalArgumentExceptionUtilsx.isAnyNullInParamsThenThrowIllegalArgumentException(settingMap, key);
        if (!settingMap.containsKey(key)) {
            return defaultValue;
        }
        return getInteger(settingMap.get(key));
    }

    /**
     * <p>getFloat.</p>
     *
     * @param settingMap settingMap
     * @param key        a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static float getFloat(final Map<String, String> settingMap,
                                 final String key) {
        return getFloat(settingMap, key, Float.NaN);
    }

    /**
     * <p>getFloat.</p>
     *
     * @param settingMap   a {@link java.util.Map} object.
     * @param key          a {@link java.lang.String} object.
     * @param defaultValue a boolean.
     * @return a boolean.
     */
    public static float getFloat(final Map<String, String> settingMap,
                                 final String key, float defaultValue) {
        IllegalArgumentExceptionUtilsx.isAnyNullInParamsThenThrowIllegalArgumentException(settingMap, key);
        if (!settingMap.containsKey(key)) {
            return defaultValue;
        }
        return getFloat(settingMap.get(key));
    }


    /**
     * <p>getBoolean.</p>
     *
     * @param settingMap settingMap
     * @param key        a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean getBoolean(final Map<String, String> settingMap,
                                     final String key) {
        return getBoolean(settingMap, key, false);
    }

    /**
     * <p>getBoolean.</p>
     *
     * @param settingMap   a {@link java.util.Map} object.
     * @param key          a {@link java.lang.String} object.
     * @param defaultValue a boolean.
     * @return a boolean.
     */
    public static boolean getBoolean(final Map<String, String> settingMap,
                                     final String key, boolean defaultValue) {
        IllegalArgumentExceptionUtilsx.isAnyNullInParamsThenThrowIllegalArgumentException(settingMap, key);
        if (!settingMap.containsKey(key)) {
            return defaultValue;
        }
        return getBoolean(settingMap.get(key));
    }

    /**
     * <p>getString.</p>
     *
     * @param settingMap settingMap
     * @param key        a {@link java.lang.String} object.
     * @return return
     */
    public static String getString(final Map<String, String> settingMap,
                                   final String key) {
        return getString(settingMap, key, null);
    }

    /**
     * <p>getString.</p>
     *
     * @param settingMap   a {@link java.util.Map} object.
     * @param key          a {@link java.lang.String} object.
     * @param defaultValue defaultValue
     * @return return
     */
    public static String getString(final Map<String, String> settingMap,
                                   final String key,
                                   final String defaultValue) {
        IllegalArgumentExceptionUtilsx.isAnyNullInParamsThenThrowIllegalArgumentException(settingMap, key);
        if (!settingMap.containsKey(key)) {
            return defaultValue;
        }
        return settingMap.get(key);
    }
}

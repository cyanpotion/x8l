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

package com.xenoamess.x8l.databind.x8lpath;

import com.xenoamess.x8l.AbstractTreeNode;
import com.xenoamess.x8l.CommentNode;
import com.xenoamess.x8l.ContentNode;
import com.xenoamess.x8l.TextNode;
import com.xenoamess.x8l.X8lGrammarException;
import com.xenoamess.x8l.X8lTree;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * CHILD[nth] -:
 *
 * CONTENT_NODE(name)[range] -: range ContentNode from children whose name is name.
 * CONTENT_NODE(name) -: CONTENT_NODE(name)[0]
 * CONTENT_NODE[range] -: CONTENT_NODE(name)[0]
 * CONTENT_NODE(name)[range] -: range ContentNode from children whose name is name.
 *
 * TEXT_NODE[range] -: range TextNode from children.
 * TEXT_NODE -: TEXT_NODE[0,]
 *
 * COMMENT_NODE[range] -: range CommentNode from children
 * COMMENT_NODE -: COMMENT_NODE[0,]
 *
 * ATTRIBUTE(keyName) get attribute keyName's value
 * PARENT parent
 *
 * TEXT_CONTENT if TextNode or CommentNode, get text content.
 *
 * range [a,b]: index from a to b, both inclusive.
 * range [,b]: index from 0 to b, both inclusive.
 * range [a,]: index from a to limit, both inclusive.
 * range [a]: equals to range [a-a].
 *
 * @author XenoAmess
 * @version 2.2.3-SNAPSHOT
 */
public class X8lPathUtil {
    @SuppressWarnings("rawtypes")
    private static final List EMPTY = new LinkedList();

    /**
     * <p>fetch.</p>
     *
     * @param originalNode a {@link com.xenoamess.x8l.AbstractTreeNode} object.
     * @param x8lPath a {@link java.lang.String} object.
     * @param tClass a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a {@link java.util.List} object.
     */
    public static @NotNull <T> List<T> fetch(
            @NotNull AbstractTreeNode originalNode,
            @NotNull String x8lPath,
            @NotNull Class<T> tClass
    ) {
        return fetch(originalNode, new String[]{x8lPath}, tClass);
    }

    /**
     * <p>fetch.</p>
     *
     * @param originalNode a {@link com.xenoamess.x8l.AbstractTreeNode} object.
     * @param x8lPaths an array of x8lPath
     * @param tClass a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a {@link java.util.List} object.
     */
    public static @NotNull <T> List<T> fetch(
            @NotNull AbstractTreeNode originalNode,
            @NotNull String[] x8lPaths,
            @NotNull Class<T> tClass
    ) {
        //noinspection rawtypes
        List fetched = fetch(originalNode, x8lPaths);
        List<T> res = new ArrayList<>(fetched.size());
        for (Object object : fetched) {
            if (tClass.isInstance(object)) {
                //noinspection unchecked
                res.add((T) object);
            }
        }
        return res;
    }

    /**
     * <p>fetch.</p>
     *
     * @param originalNode a {@link com.xenoamess.x8l.AbstractTreeNode} object.
     * @param x8lPath a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public static @NotNull List<Object> fetch(
            @NotNull AbstractTreeNode originalNode,
            @NotNull String x8lPath
    ) {
        List<AbstractTreeNode> list = new LinkedList<>();
        list.add(originalNode);
        return fetch(list, x8lPath);
    }

    /**
     * <p>fetch.</p>
     *
     * @param originalNode a {@link com.xenoamess.x8l.AbstractTreeNode} object.
     * @param x8lPaths an array of x8lPath
     * @return a {@link java.util.List} object.
     */
    public static @NotNull List<Object> fetch(
            @NotNull AbstractTreeNode originalNode,
            @NotNull String[] x8lPaths
    ) {
        List<Object> list = new LinkedList<>();
        for (String x8lPath : x8lPaths) {
            list.addAll(fetch(originalNode, x8lPath));
        }
        return list;
    }

    /**
     * <p>fetch.</p>
     *
     * @param originalNodes a {@link java.util.List} object.
     * @param x8lPath a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public static @NotNull List<Object> fetch(
            @NotNull List<AbstractTreeNode> originalNodes,
            @NotNull String x8lPath
    ) {
        //noinspection rawtypes
        List sourceList = originalNodes;
        //noinspection rawtypes
        List resultList = new ArrayList<>();
        try (StringReader stringReader = new StringReader(x8lPath)) {
            while (true) {
                //noinspection unchecked
                boolean finished = fetch(sourceList, stringReader, resultList);
                if (finished) {
                    break;
                }
                sourceList = resultList;
                resultList = new ArrayList<>();
            }
        } catch (IOException e) {
            throw new X8lGrammarException("fetch fails.", e);
        }
        //noinspection unchecked
        return resultList;
    }

    /**
     * <p>fetch.</p>
     *
     * @param originalNodes a {@link java.util.List} object.
     * @param reader a {@link java.io.Reader} object.
     * @param resultList a {@link java.util.List} object.
     * @return a boolean.
     * @throws java.io.IOException if any.
     */
    public static boolean fetch(
            @NotNull List<AbstractTreeNode> originalNodes,
            @NotNull Reader reader,
            @NotNull List<Object> resultList
    ) throws IOException {
        boolean finished = false;

        StringBuilder stringBuilder = new StringBuilder();
        boolean lastCharIsModulus = false;
        char nowChar;
        int nowInt;
        String operation = null;
        String name = null;
        String range = null;

        int beginIndexInclusive = 0;
        int endIndexInclusive = -1;
        while (true) {
            nowInt = reader.read();
            nowChar = (char) nowInt;
            if (nowInt == -1) {
                finished = true;

                if (operation == null) {
                    operation = stringBuilder.toString();
                }
                break;
            } else if (lastCharIsModulus) {
                stringBuilder.append(nowChar);
                lastCharIsModulus = false;
            } else if (nowChar == '%') {
                lastCharIsModulus = true;
            } else if (nowChar == '>') {
                if (operation == null) {
                    operation = stringBuilder.toString();
                }
                break;
            } else if (nowChar == '(') {
                if (operation == null) {
                    operation = stringBuilder.toString();
                }
                stringBuilder = new StringBuilder();
            } else if (nowChar == ')') {
                name = stringBuilder.toString();
                stringBuilder = new StringBuilder();
            } else if (nowChar == '[') {
                if (operation == null) {
                    operation = stringBuilder.toString();
                }
                stringBuilder = new StringBuilder();
            } else if (nowChar == ']') {
                range = stringBuilder.toString();
                stringBuilder = new StringBuilder();
            } else {
                stringBuilder.append(nowChar);
            }
        }

        operation = X8lTree.untranscode(operation);

        if (name != null) {
            name = X8lTree.untranscode(name);
        }
        if (range != null) {
            int separatorIndex = range.indexOf(',');
            if (separatorIndex == -1) {
                endIndexInclusive = beginIndexInclusive = Integer.parseInt(range.trim());
            } else {
                String beginString = range.substring(0, separatorIndex);
                if (StringUtils.isNotBlank(beginString)) {
                    beginIndexInclusive = Integer.parseInt(beginString);
                }
                String endString = range.substring(separatorIndex + 1);
                if (StringUtils.isNotBlank(endString)) {
                    endIndexInclusive = Integer.parseInt(endString);
                }
            }
        }

        for (AbstractTreeNode originalNode : originalNodes) {
            switch (operation) {
                case "PARENT":
                    resultList.add(originalNode.getParent());
                    break;
                case "CHILD":
                    if (!(originalNode instanceof ContentNode)) {
                        break;
                    }
                    if (name != null) {
                        break;
                    }
                    resultList.addAll(
                            subList(
                                    ((ContentNode) originalNode).getChildren(),
                                    beginIndexInclusive,
                                    endIndexInclusive
                            )
                    );
                    break;
                case "CONTENT_NODE":
                    if (!(originalNode instanceof ContentNode)) {
                        break;
                    }
                    if (name == null) {
                        resultList.addAll(
                                subList(
                                        ((ContentNode) originalNode).getContentNodesFromChildren(endIndexInclusive + 1),
                                        beginIndexInclusive,
                                        endIndexInclusive
                                )
                        );
                    } else {
                        resultList.addAll(
                                subList(
                                        ((ContentNode) originalNode).getContentNodesFromChildrenThatNameIs(
                                                name,
                                                endIndexInclusive + 1
                                        ),
                                        beginIndexInclusive,
                                        endIndexInclusive
                                )
                        );
                    }
                    break;
                case "TEXT_NODE":
                    if (!(originalNode instanceof ContentNode)) {
                        break;
                    }
                    if (name != null) {
                        break;
                    }
                    resultList.addAll(
                            subList(
                                    ((ContentNode) originalNode).getTextNodesFromChildren(endIndexInclusive + 1),
                                    beginIndexInclusive,
                                    endIndexInclusive
                            )
                    );
                    break;
                case "COMMENT_NODE":
                    if (!(originalNode instanceof ContentNode)) {
                        break;
                    }
                    if (name != null) {
                        break;
                    }
                    resultList.addAll(
                            subList(
                                    ((ContentNode) originalNode).getCommentNodesFromChildren(endIndexInclusive + 1),
                                    beginIndexInclusive,
                                    endIndexInclusive
                            )
                    );
                    break;
                case "ATTRIBUTE":
                    if (!(originalNode instanceof ContentNode)) {
                        break;
                    }
                    if (!finished) {
                        break;
                    }
                    resultList.add(
                            ((ContentNode) originalNode).getAttributes().get(name)
                    );
                    break;
                case "TEXT_CONTENT":
                    if (originalNode instanceof TextNode) {
                        resultList.add(((TextNode) originalNode).getTextContent());
                    } else if (originalNode instanceof CommentNode) {
                        resultList.add(((CommentNode) originalNode).getTextContent());
                    }
                    break;
                default:
                    if (!(originalNode instanceof ContentNode)) {
                        break;
                    }
                    if (name != null) {
                        break;
                    }
                    resultList.addAll(
                            subList(
                                    ((ContentNode) originalNode).getContentNodesFromChildrenThatNameIs(operation,
                                            endIndexInclusive + 1),
                                    beginIndexInclusive,
                                    endIndexInclusive
                            )
                    );

            }
        }
        return finished;
    }

    /**
     * <p>subList.</p>
     *
     * @param original a {@link java.util.List} object.
     * @param beginIndexInclusive a int.
     * @param endIndexInclusive a int.
     * @param <T> a T object.
     * @return a {@link java.util.List} object.
     */
    public static @NotNull <T> List<T> subList(
            @NotNull List<T> original,
            int beginIndexInclusive,
            int endIndexInclusive
    ) {
        if (original.isEmpty()) {
            //noinspection unchecked
            return EMPTY;
        }
        if (beginIndexInclusive >= original.size()) {
            //noinspection unchecked
            return EMPTY;
        }
        if (endIndexInclusive == -1 || endIndexInclusive >= original.size()) {
            endIndexInclusive = original.size() - 1;
        }
        return original.subList(beginIndexInclusive, endIndexInclusive + 1);
    }
}

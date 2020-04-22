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

package com.xenoamess.x8l.dealers;

import com.xenoamess.x8l.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Stack;

/**
 * the dealer to deal with x8l format.
 *
 * @author XenoAmess
 */
public final class X8lDealer extends LanguageDealer implements Serializable {
    /**
     * no need to build more X8lDealer instances.
     * please just use X8lDealer.INSTANCE
     * if you want to extend it,
     * please just copy the codes and make your own AbstractLanguageDealer class.
     */
    private X8lDealer() {
        this.registerTreeNodeHandler(
                RootNode.class,
                new AbstractLanguageDealerHandler<RootNode>() {
                    @Override
                    public boolean read(Reader reader, RootNode rootNode) throws IOException, X8lGrammarException {
                        return X8lDealer.this.getTreeNodeHandler(ContentNode.class).read(reader, rootNode);
                    }

                    @Override
                    public boolean write(Writer writer, RootNode rootNode) throws IOException, X8lGrammarException {
                        boolean lastChildIsTextNode = false;
                        boolean nowChildIsTextNode;

                        for (AbstractTreeNode nowChild : rootNode.getChildren()) {

                            nowChildIsTextNode = nowChild instanceof TextNode;
                            if (lastChildIsTextNode && nowChildIsTextNode) {
                                writer.append('&');
                            }
                            lastChildIsTextNode = nowChildIsTextNode;

                            nowChild.write(writer, X8lDealer.this);
                        }
                        return true;
                    }
                }
        );


        this.registerTreeNodeHandler(
                ContentNode.class,
                new AbstractLanguageDealerHandler<ContentNode>() {
                    @SuppressWarnings("AlibabaMethodTooLong")
                    @Override
                    public boolean read(Reader reader, ContentNode contentNode) throws IOException,
                            X8lGrammarException {
                        assert (reader != null);
                        contentNode.close();
                        int nowInt;
                        ContentNode nowNode = contentNode;
                        boolean lastCharIsModulus = false;
                        Stack<X8lStatusEnum> statusEnumStack = new Stack<>();
                        X8lStatusEnum nowStatus = X8lStatusEnum.ContentArea;

                        StringBuilder stringBuilder = new StringBuilder();
                        char nowChar;
                        while (true) {
                            nowInt = reader.read();
                            nowChar = (char) nowInt;
                            if (nowInt == -1) {
                                if (nowNode == contentNode && nowStatus == X8lStatusEnum.ContentArea && statusEnumStack.isEmpty()) {
                                    new TextNode(nowNode, X8lTree.untranscode(stringBuilder.toString()));
                                    break;
                                } else {
                                    throw new X8lGrammarException("Unexpected stop of x8l file.");
                                }
                            } else if (lastCharIsModulus) {
                                stringBuilder.append(nowChar);
                                lastCharIsModulus = false;
                            } else if (nowChar == '%') {
                                lastCharIsModulus = true;
                            } else if (nowStatus == X8lStatusEnum.CommentArea) {
                                if (nowChar == '>') {
                                    new CommentNode(nowNode, X8lTree.untranscode(stringBuilder.toString()));
                                    stringBuilder = new StringBuilder();
                                    nowStatus = statusEnumStack.pop();
                                } else {
                                    stringBuilder.append(nowChar);
                                }
                            } else if (nowChar == '<') {
                                if (nowStatus == X8lStatusEnum.AttributeArea) {
                                    if (!nowNode.getAttributes().isEmpty() || stringBuilder.length() != 0) {
                                        throw new X8lGrammarException("Unexpected < in attribute area of a content " +
                                                "node.");
                                    } else {
                                        ContentNode nowParent = nowNode.getParent();
                                        nowNode.close();
                                        nowNode = nowParent;
                                        nowStatus = X8lStatusEnum.CommentArea;
                                    }
                                } else {
                                    new TextNode(nowNode, X8lTree.untranscode(stringBuilder.toString()));
                                    stringBuilder = new StringBuilder();
                                    nowNode = new ContentNode(nowNode);
                                    statusEnumStack.push(nowStatus);
                                    nowStatus = X8lStatusEnum.AttributeArea;
                                }
                            } else if (nowChar == '>') {
                                if (nowStatus != X8lStatusEnum.AttributeArea) {
                                    new TextNode(nowNode, X8lTree.untranscode(stringBuilder.toString()));
                                    stringBuilder = new StringBuilder();
                                    nowNode = nowNode.getParent();
                                } else {
                                    if (stringBuilder.length() != 0) {
                                        nowNode.addAttributeFromTranscodedExpression(stringBuilder.toString());
                                        stringBuilder = new StringBuilder();
                                    }
                                    nowStatus = statusEnumStack.pop();
                                }
                            } else if (Character.isWhitespace(nowChar)) {
                                if (nowStatus == X8lStatusEnum.AttributeArea) {
                                    if (stringBuilder.length() != 0) {
                                        nowNode.addAttributeFromTranscodedExpression(stringBuilder.toString());
                                        stringBuilder = new StringBuilder();
                                    }
                                } else {
                                    stringBuilder.append(nowChar);
                                }
                            } else if (nowChar == '&') {
                                if (nowStatus == X8lStatusEnum.ContentArea) {
                                    new TextNode(nowNode, X8lTree.untranscode(stringBuilder.toString()));
                                    stringBuilder = new StringBuilder();
                                } else {
                                    stringBuilder.append(nowChar);
                                }
                            } else {
                                stringBuilder.append(nowChar);
                            }
                        }
                        if (!nowNode.getAttributeSegments().isEmpty()) {
                            nowNode.getAttributeSegments().set(nowNode.getAttributeSegments().size() - 1, "");
                        }
                        return true;
                    }

                    @Override
                    public boolean write(Writer writer, ContentNode contentNode) throws IOException,
                            X8lGrammarException {
                        writer.append('<');
                        for (int i = 0; i < contentNode.getAttributesKeyList().size(); i++) {
                            String key = contentNode.getAttributesKeyList().get(i);
                            writer.append(X8lTree.transcodeKey(key));
                            String value = contentNode.getAttributes().get(key);
                            if (!StringUtils.isEmpty(value)) {
                                writer.append("=");
                                writer.append(X8lTree.transcodeValue(value));
                            }
                            writer.append(contentNode.getAttributeSegments().get(i));
                        }
                        writer.append('>');
                        boolean lastChildIsTextNode = false;
                        boolean nowChildIsTextNode;
                        for (AbstractTreeNode nowChild : contentNode.getChildren()) {

                            nowChildIsTextNode = nowChild instanceof TextNode;
                            if (lastChildIsTextNode && nowChildIsTextNode) {
                                writer.append('&');
                            }
                            lastChildIsTextNode = nowChildIsTextNode;

                            nowChild.write(writer, X8lDealer.this);
                        }
                        writer.append('>');
                        return true;
                    }
                }
        );

        this.registerTreeNodeHandler(
                TextNode.class,
                new AbstractLanguageDealerHandler<TextNode>() {
                    @Override
                    public boolean read(Reader reader, TextNode textNode) throws X8lGrammarException {
                        return false;
                    }

                    @Override
                    public boolean write(Writer writer, TextNode textNode) throws IOException, X8lGrammarException {
                        writer.append(X8lTree.transcodeText(textNode.getTextContent()));
                        return true;
                    }
                }
        );

        this.registerTreeNodeHandler(
                CommentNode.class,
                new AbstractLanguageDealerHandler<CommentNode>() {
                    @Override
                    public boolean read(Reader reader, CommentNode commentNode) throws
                            X8lGrammarException {
                        return false;
                    }

                    @Override
                    public boolean write(Writer writer, CommentNode commentNode) throws IOException,
                            X8lGrammarException {
                        writer.append('<');
                        writer.append('<');
                        writer.append(X8lTree.transcodeComment(commentNode.getTextContent()));
                        writer.append('>');
                        return true;
                    }
                }
        );
    }

    public static final X8lDealer INSTANCE = new X8lDealer();

    @Override
    public String toString() {
        return this.getClass().getCanonicalName();
    }

    private Object readResolve() {
        return INSTANCE;
    }
}

enum X8lStatusEnum {
    /**
     * in attribute area of ContentNode.
     */
    AttributeArea,
    /**
     * in CommentNode.
     */
    CommentArea,
    /**
     * in content area of ContentNode.
     */
    ContentArea,
}

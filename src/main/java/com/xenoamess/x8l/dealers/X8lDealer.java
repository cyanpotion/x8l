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
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * the dealer to deal with x8l format.
 *
 * @author XenoAmess
 */
public class X8lDealer implements AbstractLanguageDealer {
    public static final X8lDealer INSTANCE = new X8lDealer();

    @Override
    public void write(Writer writer, AbstractTreeNode treeNode) throws IOException {
        assert (writer != null);
        if (treeNode instanceof ContentNode) {
            ContentNode contentNode = (ContentNode) treeNode;
            if (contentNode.getParent() == null) {
                for (AbstractTreeNode abstractTreeNode : contentNode.getChildren()) {
                    abstractTreeNode.write(writer, this);
                }
            } else {
                writer.append('<');
                boolean firstAttribute = true;
                for (String key : contentNode.getAttributesKeyList()) {
                    if (firstAttribute) {
                        firstAttribute = false;
                    } else {
                        writer.append(' ');
                    }
                    writer.append(X8lTree.transcodeWithWhitespace(key));
                    String value = contentNode.getAttributes().get(key);
                    if (!StringUtils.isEmpty(value)) {
                        writer.append("=");
                        writer.append(X8lTree.transcodeWithWhitespace(value));
                    }
                }
                writer.append('>');
                for (AbstractTreeNode abstractTreeNode : contentNode.getChildren()) {
                    abstractTreeNode.write(writer, this);
                }
                writer.append('>');
            }
        } else if (treeNode instanceof TextNode) {
            TextNode textNode = (TextNode) treeNode;
            writer.append(X8lTree.transcode(textNode.getTextContent()));
        } else if (treeNode instanceof CommentNode) {
            CommentNode commentNode = (CommentNode) treeNode;
            writer.append('<');
            writer.append('<');
            writer.append(X8lTree.transcodeComment(commentNode.getTextContent()));
            writer.append('>');
        } else {
            throw new NotImplementedException("not implemented for this class : " + treeNode.getClass());
        }
    }

    @Override
    public void read(Reader reader, ContentNode contentNode) throws IOException {
        assert (reader != null);
        contentNode.close();
        int nowInt;
        ContentNode nowNode = contentNode;
        boolean inAttributeArea = false;
        boolean inCommentArea = false;
        boolean lastCharIsModulus = false;

        StringBuilder stringBuilder = new StringBuilder();
        char nowChar;
        while (true) {
            nowInt = reader.read();
            nowChar = (char) nowInt;
            if (nowInt == -1) {
                if (nowNode == contentNode && !inAttributeArea && !inCommentArea) {
                    new TextNode(nowNode, stringBuilder.toString());
                    break;
                } else {
                    throw new X8lGrammarException("Unexpected stop of x8l file.");
                }
            } else if (lastCharIsModulus) {
                stringBuilder.append(nowChar);
                lastCharIsModulus = false;
            } else if (nowChar == '%') {
                lastCharIsModulus = true;
            } else if (inCommentArea) {
                if (nowChar == '>') {
                    new CommentNode(nowNode, stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    inCommentArea = false;
                } else {
                    stringBuilder.append(nowChar);
                }
            } else if (nowChar == '<') {
                if (inAttributeArea) {
                    if (!nowNode.getAttributes().isEmpty() || stringBuilder.length() != 0) {
                        throw new X8lGrammarException("Unexpected < in attribute area of a content node.");
                    } else {
                        ContentNode nowParent = nowNode.getParent();
                        nowNode.close();
                        nowNode = nowParent;
                        inAttributeArea = false;
                        inCommentArea = true;
                    }
                } else {
                    new TextNode(nowNode, stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    nowNode = new ContentNode(nowNode);
                    inAttributeArea = true;
                }
            } else if (nowChar == '>') {
                if (!inAttributeArea) {
                    new TextNode(nowNode, stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    nowNode = nowNode.getParent();
                } else {
                    if (stringBuilder.length() != 0) {
                        nowNode.addAttribute(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                    }
                    inAttributeArea = false;
                }
            } else if (Character.isWhitespace(nowChar)) {
                if (inAttributeArea) {
                    if (stringBuilder.length() != 0) {
                        nowNode.addAttribute(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                    }
                } else {
                    stringBuilder.append(nowChar);
                }
            } else {
                stringBuilder.append(nowChar);
            }
        }
    }

}

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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;
import org.dom4j.tree.DefaultComment;
import org.dom4j.tree.DefaultText;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Read and output xml.
 * Notice that I can not ensure the output is really an xml, because x8l has loosen rules on things,
 * And when convert, it might not suit xml's rule.
 * Thus if you want to convert your x8l file into an xml, please does not use some behavior that xml does not allow.
 * like nameless node.
 * <p>
 * We will try to auto fix some of the errors, but thus we cannot ensure output and then input can lead to same tree.
 * <p>
 * But if you convert your xml file into x8l then convert it back, we try to ensure the output is equal to original xml.
 * <p>
 * Also, this dealer can only read in some naive xml, but not complicated one with DTD or XLS or something.
 * <p>
 * I use dom4j for reading xml now. No interest in rewriting it.
 * <p>
 * If anybody want to refine it, pull request is always welcomed.
 *
 * @author XenoAmess
 */
public final class XmlDealer implements AbstractLanguageDealer, Serializable {
    /*
     * no need to build more XmlDealer instances.
     * please just use XmlDealer.INSTANCE
     * if you want to extend it,
     * please just copy the codes and make your own AbstractLanguageDealer class.
     */
    private XmlDealer() {

    }


    public static final String STRING_MAMELESS = "_nameless";

    public static final XmlDealer INSTANCE = new XmlDealer();

    @Override
    public void write(Writer writer, AbstractTreeNode treeNode) throws IOException {
        assert (writer != null);
        Document document = DocumentHelper.createDocument();
        Element element = document.addElement(STRING_MAMELESS);
        if (treeNode instanceof ContentNode) {
            this.write((ContentNode) treeNode, element);
        } else if (treeNode instanceof TextNode) {
            TextNode textNode = (TextNode) treeNode;
            element.addText(textNode.getTextContent());
        } else if (treeNode instanceof CommentNode) {
            CommentNode commentNode = (CommentNode) treeNode;
            element.addComment(commentNode.getTextContent());
        } else {
            throw new NotImplementedException("not implemented for this class : " + treeNode.getClass());
        }
        document.write(writer);
    }

    /**
     * delete attributes which contains illegal char in their key.
     * "illegal" here means illegal to xml, not for x8l.
     * for example, [ is illegal.(JsonDealer.ARRAY_ID_ATTRIBUTE)
     *
     * @param source original Strings to filer
     * @return filtered Strings
     */
    public static List<String> filterIllegalChars(List<String> source) {
        List<String> res = new ArrayList<>();
        for (String au : source) {
            boolean add = !au.contains(JsonDealer.ARRAY_ID_ATTRIBUTE);
            if (add) {
                for (char c : au.toCharArray()) {
                    if ((c >= 0x00 && c <= 0x08)
                            || (c >= 0x0b && c <= 0x0c)
                            || (c >= 0x0e && c <= 0x1f)) {
                        add = false;
                        break;
                    }
                }
            }
            if (add) {
                res.add(au);
            }
        }
        return res;
    }

    private void write(ContentNode contentNode, Element element) {
        boolean firstAttribute = true;
        String nodeName;
        boolean nodeNameless = false;
        List<String> filteredAttributesKeyList = filterIllegalChars(contentNode.getAttributesKeyList());
        if (filteredAttributesKeyList.isEmpty()) {
            //if have no attributes then it is nameless.
            nodeName = STRING_MAMELESS;
            nodeNameless = true;
        } else {
            nodeName = filteredAttributesKeyList.get(0);
            if (!StringUtils.isEmpty(contentNode.getAttributes().get(nodeName))) {
                //if "name" has value then it is nameless.
                nodeName = STRING_MAMELESS;
                nodeNameless = true;
            }
            //else, nodeName be first attribute's key
        }
        if (nodeNameless) {
            //if nameless then give it a name.
            element.setName(nodeName);
            firstAttribute = false;
        }
        if (nodeNameless && contentNode.getChildren().size() == 1) {
            //if nameless and have only one child,
            //then does not output this nameless node, and let the child node be here.
            AbstractTreeNode treeNode = contentNode.getChildren().get(0);
            if (treeNode instanceof ContentNode) {
                this.write((ContentNode) treeNode, element);
            } else if (treeNode instanceof TextNode) {
                TextNode textNode = (TextNode) treeNode;
                element.addText(textNode.getTextContent());
            } else if (treeNode instanceof CommentNode) {
                CommentNode commentNode = (CommentNode) treeNode;
                element.addComment(commentNode.getTextContent());
            } else {
                throw new NotImplementedException("not implemented for this class : " + treeNode.getClass());
            }
            return;
        }

        for (String key : filteredAttributesKeyList) {
            String value = contentNode.getAttributes().get(key);

            if (!StringUtils.isEmpty(value)) {
                element.addAttribute(key, value);
            } else {
                if (!firstAttribute) {
                    element.addAttribute(key, "");
                } else {
                    element.setName(key);
                }
            }

            firstAttribute = false;
        }
        for (AbstractTreeNode au : contentNode.getChildren()) {
            if (au instanceof ContentNode) {
                this.write((ContentNode) au, element.addElement(STRING_MAMELESS));
            } else if (au instanceof TextNode) {
                TextNode textNode = (TextNode) au;
                element.addText(textNode.getTextContent());
            } else if (au instanceof CommentNode) {
                CommentNode commentNode = (CommentNode) au;
                element.addComment(commentNode.getTextContent());
            } else {
                throw new NotImplementedException("not implemented for this class : " + au.getClass());
            }
        }
    }

    @Override
    public void read(Reader reader, ContentNode contentNode) throws IOException {
        assert (reader != null);
        try {
            Document document = DocumentHelper.parseText(IOUtils.toString(reader));
            this.read(new ContentNode(contentNode), document.getRootElement());
        } catch (DocumentException e) {
            throw new IOException(e);
        }
    }

    private void read(ContentNode contentNode, Element element) {
        contentNode.addAttribute(element.getName());
        for (Attribute attribute : element.attributes()) {
            contentNode.addAttribute(attribute.getName(), attribute.getValue());
        }
        for (int i = 0, size = element.nodeCount(); i < size; i++) {
            Node node = element.node(i);
            if (node instanceof Element) {
                ContentNode childContentNode = new ContentNode(contentNode);
                this.read(childContentNode, (Element) node);
            } else if (node instanceof DefaultText) {
                new TextNode(contentNode, node.getText());
            } else if (node instanceof DefaultComment) {
                new CommentNode(contentNode, node.getText());
            }
        }
    }

    /**
     * My naive implement to read xml file.
     * It is outdated and far limited,
     * and should not be used anymore now.
     *
     * @param writer   writer
     * @param treeNode treeNode
     * @throws IOException IOException
     */
    @Deprecated
    public void naiveWrite(Writer writer, AbstractTreeNode treeNode) throws IOException {
        if (treeNode instanceof ContentNode) {
            ContentNode contentNode = (ContentNode) treeNode;
            writer.append('<');
            boolean firstAttribute = true;

            String nodeName;
            boolean nodeNameless = false;

            if (contentNode.getAttributesKeyList().isEmpty()) {
                //if have no attributes then it is nameless.
                nodeName = STRING_MAMELESS;
                nodeNameless = true;
            } else {
                nodeName = contentNode.getAttributesKeyList().get(0);
                if (!StringUtils.isEmpty(contentNode.getAttributes().get(nodeName))) {
                    //if "name" has value then it is nameless.
                    nodeName = STRING_MAMELESS;
                    nodeNameless = true;
                }
                //else, nodeName be first attribute's key
            }
            if (nodeNameless) {
                //if nameless then give it a name.
                writer.append(nodeName);
                if (!contentNode.getAttributesKeyList().isEmpty()) {
                    writer.append(' ');
                }
                firstAttribute = false;
            }

            for (String key : contentNode.getAttributesKeyList()) {
                if (!firstAttribute) {
                    writer.append(' ');
                }
                writer.append(X8lTree.transcodeKeyAndValue(key));
                String value = contentNode.getAttributes().get(key);

                if (!StringUtils.isEmpty(value)) {
                    writer.append('=');
                    writer.append('"');
                    writer.append(X8lTree.transcodeKeyAndValue(value));
                    writer.append('"');
                } else {
                    if (!firstAttribute) {
                        writer.append("=\"\"");
                    }
                }

                firstAttribute = false;
            }
            if (!contentNode.getChildren().isEmpty()) {
                writer.append('>');
            }
            for (AbstractTreeNode abstractTreeNode : contentNode.getChildren()) {
                abstractTreeNode.write(writer, this);
            }
            if (contentNode.getChildren().isEmpty()) {
                writer.append("/>");
            } else {
                writer.append("</");
                writer.append(nodeName);
                writer.append(">");
            }
        } else if (treeNode instanceof TextNode) {
            TextNode textNode = (TextNode) treeNode;
            writer.append(X8lTree.transcodeText(textNode.getTextContent()));
        } else if (treeNode instanceof CommentNode) {
            CommentNode commentNode = (CommentNode) treeNode;
            writer.append("<!--");
            writer.append(X8lTree.transcodeComment(commentNode.getTextContent()));
            writer.append("-->");
        } else {
            throw new NotImplementedException("not implemented for this class : " + treeNode.getClass());
        }
    }

    @Override
    public String toString() {
        return this.getClass().getCanonicalName();
    }

    private Object readResolve() {
        return INSTANCE;
    }
}

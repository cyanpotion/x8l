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
import java.io.Writer;

/**
 * Read and output the "xml like" syntax.
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
 * If anybody want to implement it, pull request is always welcomed.
 *
 * @author XenoAmess
 */
public class XmlDealer implements AbstractLanguageDealer {
    public static String STRING_MAMELESS = "_nameless";

    public static final XmlDealer INSTANCE = new XmlDealer();

    @Override
    public void write(Writer writer, AbstractTreeNode treeNode) throws IOException {
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
                writer.append(X8lTree.transcode(key));
                String value = contentNode.getAttributes().get(key);

                if (!StringUtils.isEmpty(value)) {
                    writer.append('=');
                    writer.append('"');
                    writer.append(X8lTree.transcode(value));
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
            writer.append(X8lTree.transcode(textNode.getTextContent()));
        } else if (treeNode instanceof CommentNode) {
            CommentNode commentNode = (CommentNode) treeNode;
            writer.append("<!--");
            writer.append(X8lTree.transcode(commentNode.getTextContent()));
            writer.append("-->");
        } else {
            throw new NotImplementedException("not implemented for this class : " + treeNode.getClass());
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
}

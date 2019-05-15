package com.xenoamess.x8l;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * @author XenoAmess
 */
public class ContentNode extends AbstractTreeNode {
    private List<AbstractTreeNode> children = new ArrayList<>();
    private Map<String, String> attributes = new HashMap<>();
    private List<String> attributesKeyList = new ArrayList<>();

    public ContentNode(ContentNode parent) {
        super(parent);
    }

    public ContentNode(ContentNode parent, int index) {
        super(parent, index);
    }

    public void addAttribute(String key, String value) {
        if (value == null) {
            value = "";
        }
        if (!this.getAttributes().containsKey(key)) {
            getAttributesKeyList().add(key);
        }
        getAttributes().put(key, value);
    }


    public void addAttribute(String attributeString) {
        int index = attributeString.indexOf('=');
        if (index == -1) {
            this.addAttribute(attributeString, null);
        } else {
            this.addAttribute(attributeString.substring(0, index), attributeString.substring(index + 1));
        }
    }

    public void removeAttribute(String attributeString) {
        int index = attributeString.indexOf('=');
        if (index == -1) {
            //key not exist, thus do nothing here.
        } else {
            attributeString = attributeString.substring(0, index);
        }
        this.getAttributes().remove(attributeString);
        this.getAttributesKeyList().remove(attributeString);
    }

    @Override
    public void show() {
        super.show();
        System.out.println("attributes : ");
        for (Map.Entry<String, String> au : getAttributes().entrySet()) {
            System.out.println(au.getKey() + " = " + au.getValue());
        }
        for (AbstractTreeNode au : this.getChildren()) {
            au.show();
        }
    }

    @Override
    public void close() {
        for (AbstractTreeNode au : this.getChildren()) {
            au.close();
        }
        super.close();
        this.setChildren(null);
        this.setAttributes(null);
        this.setAttributesKeyList(null);
    }

    public void trim() {
        List<AbstractTreeNode> newChildren = new ArrayList<>();
        for (AbstractTreeNode au : this.getChildren()) {
            //it is done in this way to make sure that:
            //  if you extend this library,and make a class extended ContentNode, it will be call trim() here.
            //  but if you make a class extended TextNode, its nodes will not be deleted during trim().
            if (au instanceof ContentNode) {
                ((ContentNode) au).trim();
                newChildren.add(au);
            } else if (au.getClass().equals(TextNode.class)) {
                if (!"".equals(((TextNode) au).getTextContent().trim())) {
                    newChildren.add(au);
                }
            } else {
                newChildren.add(au);
            }
        }
        this.getChildren().clear();
        this.setChildren(newChildren);
    }


    @Override
    public void write(Writer writer) throws IOException {
        if (this.getParent() == null) {
            for (AbstractTreeNode abstractTreeNode : this.getChildren()) {
                abstractTreeNode.write(writer);
            }
        } else {
            writer.append('<');
            boolean firstAttribute = true;
            for (String key : getAttributesKeyList()) {
                if (firstAttribute) {
                    firstAttribute = false;
                } else {
                    writer.append(' ');
                }
                writer.append(X8lTree.transcode(key));
                String value = getAttributes().get(key);
                if (!"".equals(value)) {
                    writer.append("=");
                    writer.append(X8lTree.transcode(value));
                }
            }
            writer.append('>');
            for (AbstractTreeNode abstractTreeNode : this.getChildren()) {
                abstractTreeNode.write(writer);
            }
            writer.append('>');
        }
    }

    @Override
    public void format(int space) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < space; i++) {
            stringBuilder.append("    ");
        }
        String spaceString = stringBuilder.toString();
        String spaceString2 = stringBuilder.toString() + "    ";
        if (space < 0) {
            spaceString2 = "";
        }

        for (AbstractTreeNode abstractTreeNode : this.getChildren()) {
            abstractTreeNode.format(space + 1);
        }

        if (this.getChildren().size() > 1 || (this.getChildren().size() == 1 && !(this.getChildren().get(0) instanceof TextNode))) {
            List<AbstractTreeNode> newChildren = new ArrayList<>();
            for (AbstractTreeNode abstractTreeNode : this.getChildren()) {
                newChildren.add(new TextNode(null, "\n" + spaceString2).changeParent(this));
                newChildren.add(abstractTreeNode);
            }
            this.setChildren(newChildren);
            newChildren.add(new TextNode(null, "\n" + spaceString).changeParent(this));
        }
    }


    public List<TextNode> getTextNodesFromChildren() {
        return this.getTextNodesFromChildren(0);
    }

    public List<TextNode> getTextNodesFromChildren(int maxSize) {
        List<TextNode> res = new ArrayList<>();
        for (AbstractTreeNode au : this.getChildren()) {
            if (au instanceof TextNode) {
                res.add((TextNode) au);
                if (res.size() == maxSize) {
                    return res;
                }
            }
        }
        return res;
    }

    public List<ContentNode> getContentNodesFromChildren() {
        return this.getContentNodesFromChildren(0);
    }

    public List<ContentNode> getContentNodesFromChildren(int maxSize) {
        List<ContentNode> res = new ArrayList<>();
        for (AbstractTreeNode au : this.getChildren()) {
            if (au instanceof ContentNode) {
                res.add((ContentNode) au);
                if (res.size() == maxSize) {
                    return res;
                }
            }
        }
        return res;
    }


    public List<CommentNode> getCommentNodesFromChildren() {
        return this.getCommentNodesFromChildren(0);
    }

    public List<CommentNode> getCommentNodesFromChildren(int maxSize) {
        List<CommentNode> res = new ArrayList<>();
        for (AbstractTreeNode au : this.getChildren()) {
            if (au instanceof CommentNode) {
                res.add((CommentNode) au);
                if (res.size() == maxSize) {
                    return res;
                }
            }
        }
        return res;
    }

    public String getName() {
        if (!this.getAttributesKeyList().isEmpty()) {
            return this.getAttributesKeyList().get(0);
        } else {
            return "";
        }
    }

    public List<ContentNode> getContentNodesFromChildrenThatNameIs(String name) {
        return this.getContentNodesFromChildrenThatNameIs(name, 0);
    }

    public List<ContentNode> getContentNodesFromChildrenThatNameIs(String name, int maxSize) {
        List<ContentNode> res = new ArrayList<>();
        for (AbstractTreeNode au : this.getChildren()) {
            if (au instanceof ContentNode && ((ContentNode) au).getName().equals(name)) {
                res.add((ContentNode) au);
                if (res.size() == maxSize) {
                    return res;
                }
            }
        }
        return res;
    }


    /**
     * Append an AbstractTreeNode as a new child of this node.
     * Notice that this function will change parent of the new added child nodes.
     *
     * @param abstractTreeNode new child node.
     * @see AbstractTreeNode
     */
    public void append(AbstractTreeNode abstractTreeNode) {
        if (abstractTreeNode == null) {
            return;
        }
        abstractTreeNode.changeParent(this);
        this.getChildren().add(abstractTreeNode);
    }

    /**
     * Append an Collection of AbstractTreeNode as new children of this node.
     * Notice that this function will change parent of the new added children nodes.
     *
     * @param abstractTreeNodes new children collection.
     * @see AbstractTreeNode
     */
    public void appendAll(Collection<AbstractTreeNode> abstractTreeNodes) {
        if (abstractTreeNodes == null) {
            return;
        }
        for (AbstractTreeNode abstractTreeNode : abstractTreeNodes) {
            this.append(abstractTreeNode);
        }
    }

    //---getters and setters

    public List<AbstractTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<AbstractTreeNode> children) {
        this.children = children;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<String> getAttributesKeyList() {
        return attributesKeyList;
    }

    public void setAttributesKeyList(List<String> attributesKeyList) {
        this.attributesKeyList = attributesKeyList;
    }
}

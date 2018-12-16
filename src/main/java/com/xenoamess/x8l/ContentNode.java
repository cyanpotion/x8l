package com.xenoamess.x8l;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentNode extends TreeNode {
    public List<TreeNode> children = new ArrayList<TreeNode>();
    public Map<String, String> attributes = new HashMap<String, String>();
    public List<String> attributesKeyList = new ArrayList<String>();

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
        if (!this.attributes.containsKey(key)) {
            attributesKeyList.add(key);
        }
        attributes.put(key, value);
    }


    public void addAttribute(String attributeString) {
        int index = attributeString.indexOf("=");
        if (index == -1) {
            this.addAttribute(attributeString, null);
        } else {
            this.addAttribute(attributeString.substring(0, index), attributeString.substring(index + 1));
        }
    }

    public void removeAttribute(String attributeString) {
        int index = attributeString.indexOf("=");
        if (index == -1) {
        } else {
            attributeString = attributeString.substring(0, index);
        }
        this.attributes.remove(attributeString);
        this.attributesKeyList.remove(attributeString);
    }

    @Override
    public void show() {
        super.show();
        System.out.println("attributes : ");
        for (Map.Entry<String, String> au : attributes.entrySet()) {
            System.out.println(au.getKey() + " = " + au.getValue());
        }
        for (TreeNode au : this.children) {
            au.show();
        }
    }

    @Override
    public void destroy() {
        for (TreeNode au : this.children) {
            au.destroy();
        }
        super.destroy();
        this.children = null;
        this.attributes = null;
        this.attributesKeyList = null;
    }

    public void trim() {
        List<TreeNode> newChildren = new ArrayList<TreeNode>();
        for (TreeNode au : this.children) {
            //it is done in this way to make sure that:
            //  if you extend this library,and make a class extended ContentNode, it will be call trim() here.
            //  but if you make a class extended TextNode, its nodes will not be deleted during trim().
            if (au instanceof ContentNode) {
                ((ContentNode) au).trim();
                newChildren.add(au);
            } else if (au.getClass().equals(TextNode.class)) {
                if (!((TextNode) au).textContent.trim().equals("")) {
                    newChildren.add(au);
                }
            } else {
                newChildren.add(au);
            }
        }
        this.children.clear();
        this.children = newChildren;
    }


    @Override
    public void output(Writer writer) {
        try {
            if (this.parent == null) {
                for (TreeNode treeNode : this.children) {
                    treeNode.output(writer);
                }
            } else {
                writer.append('<');
                boolean firstAttribute = true;
                for (String key : attributesKeyList) {
                    if (firstAttribute) {
                        firstAttribute = false;
                    } else {
                        writer.append(' ');
                    }
                    writer.append(X8lTree.Transcode(key));
                    String value = attributes.get(key);
                    if (!value.equals("")) {
                        writer.append("=");
                        writer.append(X8lTree.Transcode(value));
                    }
                }
                writer.append('>');
                for (TreeNode treeNode : this.children) {
                    treeNode.output(writer);
                }
                writer.append('>');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

//        if (this.parent == null) {
//            for (TreeNode treeNode : this.children) {
//                treeNode.format(0);
//            }
//        } else {
        for (TreeNode treeNode : this.children) {
            treeNode.format(space + 1);
        }

        if (this.children.size() > 1 || (this.children.size() == 1 && !(this.children.get(0) instanceof TextNode))) {
            List<TreeNode> newChildren = new ArrayList<TreeNode>();
//        newChildren.add(new TextNode(null, "\n" + spaceString).changeParent(this));
//            newChildren.add(new TextNode(null, "\n" + spaceString).changeParent(this));
            for (TreeNode treeNode : this.children) {
//            newChildren.add(new TextNode(null, "\n" + spaceString + "    ").changeParent(this));
                newChildren.add(new TextNode(null, "\n" + spaceString2).changeParent(this));
                newChildren.add(treeNode);
                this.children = newChildren;
            }
            newChildren.add(new TextNode(null, "\n" + spaceString).changeParent(this));
        }
    }


    public List<TextNode> getTextNodesFromChildren() {
        return this.getTextNodesFromChildren(0);
    }

    public List<TextNode> getTextNodesFromChildren(int maxSize) {
        List<TextNode> res = new ArrayList<TextNode>();
        for (TreeNode au : this.children) {
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
        List<ContentNode> res = new ArrayList<ContentNode>();
        for (TreeNode au : this.children) {
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
        List<CommentNode> res = new ArrayList<CommentNode>();
        for (TreeNode au : this.children) {
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
        if (!this.attributesKeyList.isEmpty()) {
            return this.attributesKeyList.get(0);
        } else {
            return "";
        }
    }

    public List<ContentNode> getContentNodesFromChildrenThatNameIs(String name) {
        return this.getContentNodesFromChildrenThatNameIs(name, 0);
    }

    public List<ContentNode> getContentNodesFromChildrenThatNameIs(String name, int maxSize) {
        List<ContentNode> res = new ArrayList<ContentNode>();
        for (TreeNode au : this.children) {
            if (au instanceof CommentNode && ((ContentNode) au).getName().equals(name)) {
                res.add((ContentNode) au);
                if (res.size() == maxSize) {
                    return res;
                }
            }
        }
        return res;
    }

}

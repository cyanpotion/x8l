package com.xenoamess.x8l;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContentNode extends TreeNode {
    public ArrayList<TreeNode> children = null;
    public Map<String, String> attributes = null;
    public ArrayList<String> attributesKeyList = null;

    public ContentNode(ContentNode parent) {
        super(parent);
        this.children = new ArrayList<TreeNode>();
        this.attributes = new HashMap<String, String>();
        this.attributesKeyList = new ArrayList<String>();
    }

    public void addAttribute(String key, String value) {
        if (value == null) {
            value = "";
        }
        attributes.put(key, value);
        attributesKeyList.add(key);
    }


    public void addAttribute(String attributeString) {
        int index = attributeString.indexOf("=");
        if (index == -1) {
            this.addAttribute(attributeString, null);
        } else {
            this.addAttribute(attributeString.substring(0, index), attributeString.substring(index + 1));
        }
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
        ArrayList<TreeNode> newChildren = new ArrayList<TreeNode>();
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
            }
        }
        this.children.clear();
        this.children = newChildren;
    }
}

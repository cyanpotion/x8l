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

package com.xenoamess.x8l;

import com.xenoamess.x8l.dealers.LanguageDealer;
import com.xenoamess.x8l.dealers.X8lDealer;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

/**
 * ContentNode
 * ContentNode means some nodes with content.
 *
 * @author XenoAmess
 */
public class ContentNode extends AbstractTreeNode {

    public static final String DEFAULT_ATTRIBUTE_VALUE = "";

    public static final String DEFAULT_SEGMENT_VALUE = " ";

    public static final String EMPTY_SEGMENT_VALUE = "";

    private final List<AbstractTreeNode> children = new ArrayList<>();
    private final Map<String, String> attributes = new HashMap<>();
    private final List<String> attributesKeyList = new ArrayList<>();

    private final List<String> attributeSegments = new ArrayList<>();

    public ContentNode(ContentNode parent) {
        super(parent);
    }

    public ContentNode(ContentNode parent, int index) {
        super(parent, index);
    }

    public void addAttribute(String key, String value, String segment) {
        if (value == null) {
            value = DEFAULT_ATTRIBUTE_VALUE;
        }
        if (segment == null) {
            segment = EMPTY_SEGMENT_VALUE;
        }
        boolean addKey = !this.getAttributes().containsKey(key);
        if (addKey) {
            getAttributesKeyList().add(key);
        }
        getAttributes().put(key, value);

        if (addKey) {
            if (!getAttributeSegments().isEmpty()) {
                int size = getAttributeSegments().size();
                if (getAttributeSegments().get(size - 1).equals(EMPTY_SEGMENT_VALUE)) {
                    getAttributeSegments().set(size - 1, DEFAULT_SEGMENT_VALUE);
                }
            }
            getAttributeSegments().add(segment);
        }
    }

    public void addAttribute(String key, String value) {
        this.addAttribute(key, value, null);
    }


    public void addAttribute(String attributeString) {
        this.addAttribute(attributeString, null);
    }

    public void addAttributeFromTranscodedExpression(String attributeExpressionString) {
        int index1 = 0;
        final int len = attributeExpressionString.length();

        out1:
        for (; index1 < len; index1++) {
            char nowChar = attributeExpressionString.charAt(index1);
            switch (nowChar) {
                case '%':
                    index1++;
                    break;
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                case '=':
                    break out1;
                default:
                    break;
            }
        }

        int index2 = index1 + 1;

        out2:
        for (; index2 < len; index2++) {
            char nowChar = attributeExpressionString.charAt(index2);
            switch (nowChar) {
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    break;
                default:
                    break out2;
            }
        }

        int index3 = index2 + 1;

        out3:
        for (; index3 < len; index3++) {
            char nowChar = attributeExpressionString.charAt(index3);
            switch (nowChar) {
                case '%':
                    index3++;
                    break;
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    break out3;
                default:
                    break;
            }
        }

        this.addAttribute(
                X8lTree.transcodeKey(StringUtils.substring(attributeExpressionString, 0, index1)),
                X8lTree.transcodeValue(StringUtils.substring(attributeExpressionString, index2, index3))
        );
    }

    public void removeAttribute(String attributeString) {
        int index = this.getAttributesKeyList().indexOf(attributeString);
        getAttributeSegments().remove(index);
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
        boolean lastChildIsTextNode = false;
        boolean nowChildIsTextNode;
        for (AbstractTreeNode nowChild : this.getChildren()) {
            nowChildIsTextNode = nowChild instanceof TextNode;
            if (lastChildIsTextNode && nowChildIsTextNode) {
                System.out.println('&');
            }
            lastChildIsTextNode = nowChildIsTextNode;
            nowChild.show();
        }
    }

    @Override
    public void clear() {
        List<AbstractTreeNode> tmpChildren = new ArrayList<>(this.getChildren());
        for (AbstractTreeNode au : tmpChildren) {
            au.close();
        }
        this.getChildren().clear();
        this.getAttributes().clear();
        this.getAttributesKeyList().clear();
    }

    public void trimAttributeSegments() {
        for (int i = 0; i < this.getAttributeSegments().size(); i++) {
            this.getAttributeSegments().replaceAll(
                    s -> DEFAULT_SEGMENT_VALUE
            );
        }
        if (!this.getAttributeSegments().isEmpty()) {
            this.getAttributeSegments().set(
                    this.getAttributeSegments().size() - 1
                    , ""
            );
        }
    }

    public void trim() {
        trimAttributeSegments();

        List<AbstractTreeNode> newChildren = new ArrayList<>();
        for (AbstractTreeNode au : this.getChildren()) {
            //it is done in this way to make sure that:
            //  if you extend this library,and make a class extended ContentNode, it will be call trim() here.
            //  but if you make a class extended TextNode, its nodes will not be deleted during trim().
            if (au instanceof ContentNode) {
                ((ContentNode) au).trim();
                newChildren.add(au);
            } else if (au.getClass().equals(TextNode.class)) {
                if (!StringUtils.isBlank(((TextNode) au).getTextContent())) {
                    newChildren.add(au);
                }
            } else {
                newChildren.add(au);
            }
        }
        this.getChildren().clear();
        this.getChildren().addAll(newChildren);
    }

    public void trimForce() {
        trimAttributeSegments();

        List<AbstractTreeNode> newChildren = new ArrayList<>();
        for (AbstractTreeNode au : this.getChildren()) {
            //it is done in this way to make sure that:
            //  if you extend this library,and make a class extended ContentNode, it will be call trimForce() here.
            //  but if you make a class extended TextNode, its nodes will not be deleted during trimForce().
            if (au instanceof ContentNode) {
                ((ContentNode) au).trimForce();
                newChildren.add(au);
            } else if (au.getClass().equals(TextNode.class)) {
                TextNode textNode = (TextNode) au;
                String newContent = textNode.getTextContent().trim();
                if (!StringUtils.isBlank(newContent)) {
                    textNode.setTextContent(newContent);
                    newChildren.add(au);
                }
            } else if (au.getClass().equals(CommentNode.class)) {
                CommentNode textNode = (CommentNode) au;
                String newContent = textNode.getTextContent().trim();
                if (!StringUtils.isBlank(newContent)) {
                    textNode.setTextContent(newContent);
                    newChildren.add(au);
                }
            } else {
                newChildren.add(au);
            }
        }
        this.getChildren().clear();
        this.getChildren().addAll(newChildren);
    }

    private static final int MAXIMAL_IGNORE_FORMAT_ATTRIBUTE_NUM = 3;

    public void formatAttributeSegments(int space) {
        this.trimAttributeSegments();
        if (this.getAttributesKeyList().size() <= MAXIMAL_IGNORE_FORMAT_ATTRIBUTE_NUM) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < space; i++) {
            stringBuilder.append("    ");
        }
        String segment1 = "\n" + stringBuilder.toString() + "    ";
        for (int i = 0; i < this.getAttributesKeyList().size() - 1; i++) {
            this.getAttributeSegments().set(i, segment1);
        }
        String segment2 = "\n" + stringBuilder.toString();
        this.getAttributeSegments().set(this.getAttributesKeyList().size() - 1, segment2);
    }

    @Override
    public void format(int space) {
        formatAttributeSegments(space);
        //notice that space can less than 0 here.
        //thus this loop can never be changed to String.repeat().
        //please ignore the warning about that.
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < space; i++) {
            stringBuilder.append("    ");
        }
        String spaceString1 = stringBuilder.toString();
        String spaceString2 = stringBuilder.toString() + "    ";
        if (space < 0) {
            spaceString2 = "";
        }

        for (AbstractTreeNode abstractTreeNode : this.getChildren()) {
            abstractTreeNode.format(space + 1);
        }

        if (
                this.getChildren().size() > 1
                        || (
                        this.getChildren().size() == 1
                                && !(this.getChildren().get(0) instanceof TextNode)
                )
        ) {
            List<AbstractTreeNode> newChildren = new ArrayList<>();
            for (AbstractTreeNode abstractTreeNode : this.getChildren()) {
                if (space != -1) {
                    if (abstractTreeNode instanceof TextNode) {
                        ((TextNode) abstractTreeNode).setTextContent(
                                "\n" + spaceString2 + ((TextNode) abstractTreeNode).getTextContent()
                        );
                    } else {
                        newChildren.add(new TextNode(null, "\n" + spaceString2).changeParent(this));
                    }
                } else {
                    space = 0;
                }
                newChildren.add(abstractTreeNode);
            }
            this.getChildren().clear();
            this.getChildren().addAll(newChildren);
            new TextNode(this, "\n" + spaceString1);
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
        abstractTreeNode.setParent(this);
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

        Collection<AbstractTreeNode> tmpCollection = abstractTreeNodes;
        if (abstractTreeNodes == this.getChildren()) {
            tmpCollection = new ArrayList<>(abstractTreeNodes);
        }

        for (AbstractTreeNode abstractTreeNode : tmpCollection) {
            this.append(abstractTreeNode);
        }
    }


    public void read(@NotNull Reader reader) throws IOException {
        this.read(reader, X8lDealer.INSTANCE);
    }

    public void read(@NotNull Reader reader, @NotNull LanguageDealer languageDealer) throws IOException {
        languageDealer.read(reader, this);
    }


    @SuppressWarnings("UnusedReturnValue")
    public <T> List<T> applyToAllNodes(Function<AbstractTreeNode, T> function) {
        ArrayList<T> res = new ArrayList<>();
        this.applyToAllNodes(res, function);
        return res;
    }

    public <T> void applyToAllNodes(List<T> results, Function<AbstractTreeNode, T> function) {
        results.add(function.apply(this));
        for (AbstractTreeNode au : this.getChildren()) {
            if (au instanceof ContentNode) {
                ((ContentNode) au).applyToAllNodes(results, function);
            } else {
                results.add(function.apply(au));
            }
        }
    }

    //---getters and setters

    public List<AbstractTreeNode> getChildren() {
        return children;
    }

    /**
     * if find child in this.children then remove it and return true;
     * otherwise return false.
     *
     * @param child the child node to remove.
     * @return if remove success.
     */
    public boolean removeChild(AbstractTreeNode child) {
        Iterator<AbstractTreeNode> iterator = this.getChildren().iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == child) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public ContentNode copy() {
        ContentNode res = new ContentNode(null);
        for (String attributeKey : this.getAttributesKeyList()) {
            String attributeValue = this.getAttributes().get(attributeKey);
            res.addAttribute(attributeKey, attributeValue);
        }
        for (AbstractTreeNode abstractTreeNode : this.getChildren()) {
            abstractTreeNode.copy().changeParentAndRegister(res);
        }
        return res;
    }

    @Override
    public boolean equals(Object treeNode) {
        if (treeNode == null) {
            return false;
        }
        if (!treeNode.getClass().equals(this.getClass())) {
            return false;
        }
        ContentNode contentNode = (ContentNode) treeNode;

        return this.getAttributesKeyList().equals(contentNode.getAttributesKeyList())
                && this.getAttributes().equals(contentNode.getAttributes())
                && this.getChildren().equals(contentNode.getChildren());
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public List<String> getAttributesKeyList() {
        return attributesKeyList;
    }

    public List<String> getAttributeSegments() {
        return attributeSegments;
    }

    /**
     * treat this node as an array who contains only TextNodes, and return their text content.
     * @return String array list of TextNode's text content
     */
    public List<String> asStringList() {
//          example:
//          <a>1&2&3>
//          ->
//          string array [1,2,3]
        return this.asStringCollectionFill(new ArrayList<>(this.getChildren().size()));
    }

    /**
     * treat this node as an array who contains only TextNodes, and return their text content, trimmed.
     * @return String array list of TextNode's text content, trimmed.
     */
    public List<String> asStringListTrimmed() {
        return this.asStringCollectionTrimmedFill(new ArrayList<>(this.getChildren().size()));
    }

    /**
     * treat this node as an array who contains only TextNodes, and return their text content.
     * @return String set of TextNode's text content
     */
    public HashSet<String> asStringSet() {
//          example:
//          <a>1&2&3>
//          ->
//          string array [1,2,3]
        return this.asStringCollectionFill(new HashSet<>(this.getChildren().size()));
    }

    /**
     * treat this node as an array who contains only TextNodes, and return their text content, trimmed.
     * @return String set of TextNode's text content, trimmed.
     */
    public HashSet<String> asStringSetTrimmed() {
        return this.asStringCollectionTrimmedFill(new HashSet<>(this.getChildren().size()));
    }

    public <T extends Collection<String>> T asStringCollection(Class<T> collectionClass) {
        try {
            try {
                return asStringCollectionFill(collectionClass.getConstructor(Integer.TYPE).newInstance(this.getChildren().size()));
            } catch (Exception e) {
                return asStringCollectionFill(collectionClass.getConstructor().newInstance());
            }
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new X8lGrammarException("asStringCollection fails!", e);
        }
    }

    public <T extends Collection<String>> T asStringCollectionTrimmed(Class<T> collectionClass) {
        try {
            try {
                return asStringCollectionTrimmedFill(collectionClass.getConstructor(Integer.TYPE).newInstance(this.getChildren().size()));
            } catch (Exception e) {
                return asStringCollectionTrimmedFill(collectionClass.getConstructor().newInstance());
            }
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new X8lGrammarException("asStringCollection fails!", e);
        }
    }

    public <T extends Collection<String>> T asStringCollectionFill(T collection) {
        List<TextNode> textNodes = this.getTextNodesFromChildren();
        for (TextNode au : textNodes) {
            collection.add(au.getTextContent());
        }
        return collection;
    }

    public <T extends Collection<String>> T asStringCollectionTrimmedFill(T collection) {
        List<TextNode> textNodes = this.getTextNodesFromChildren();
        for (TextNode au : textNodes) {
            collection.add(au.getTextContent().trim());
        }
        return collection;
    }

    /**
     * treat this node as a String Map.
     * @return String map
     */
    public Map<String, String> asStringMap() {
//         example:
//         <a>
//           <he>1>
//           <she>0>
//           <it>-1>
//         >
//         ->
//         string map [he=1,she=2,it=-1]
//        /

        return this.asStringMapFill(new HashMap<>(this.getChildren().size()));
    }

    /**
     * treat this node as a String Map, trimmed.
     * @return String map, trimmed.
     */
    public Map<String, String> asStringMapTrimmed() {
        return this.asStringMapTrimmedFill(new HashMap<>(this.getChildren().size()));
    }

    public <T extends Map<String, String>> T asStringMapFill(T map) {
        List<ContentNode> contentNodes = this.getContentNodesFromChildren();
        for (ContentNode au : contentNodes) {
            map.put(au.getName(), au.getTextNodesFromChildren(1).get(0).getTextContent());
        }
        return map;
    }

    public <T extends Map<String, String>> T asStringMapTrimmedFill(T map) {
        List<ContentNode> contentNodes = this.getContentNodesFromChildren();
        for (ContentNode au : contentNodes) {
            map.put(au.getName().trim(), au.getTextNodesFromChildren(1).get(0).getTextContent().trim());
        }
        return map;
    }

}

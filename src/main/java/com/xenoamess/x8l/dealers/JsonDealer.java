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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xenoamess.x8l.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

/**
 * Read and output json.
 * Notice that I can not ensure the output is really a json, because x8l has loosen rules on things,
 * And when convert, it might not suit json's rule.
 * Thus if you want to convert your x8l file into a json, please does not use some behavior that json does not allow.
 * like attributes / commentNodes.
 * <p>
 * We will try to auto fix some of the errors, but thus we cannot ensure output and then input can lead to same tree.
 * <p>
 * If you convert your json file into x8l then convert it back, we try to ensure the output is equal to original
 * json.
 * But As there is no type information in x8l, all numbers, null, text or other things will be treated as string,
 * and the type information will be permanently lost when convert from json to x8l tree.
 * <p>
 * Also, this dealer can only read in some naive json, but not complicated one with some special rules.
 * <p>
 * I use jackson for reading json now. No interest in rewriting it.
 * <p>
 * If anybody want to refine it, pull request is always welcomed.
 *
 * @author XenoAmess
 */
public class JsonDealer implements AbstractLanguageDealer {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JsonDealer.class);

    public static final String ARRAY_ID_ATTRIBUTE = "[";
    public static final String TEXT_KEY = "_text";
    public static final String COMMENT_KEY = "_comment";
    public static final String ATTRIBUTES_KEY = "_attributes";
    public static final JsonDealer INSTANCE = new JsonDealer();
    private static ObjectMapper objectMapper;

    private static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            ObjectMapper localObjectMapper = new ObjectMapper();
            localObjectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
            objectMapper = localObjectMapper;
        }
        return objectMapper;
    }

    public static boolean isSingleNameTextPair(ContentNode contentNode) {
        if (contentNode.getAttributes().size() == 1) {
            String name = contentNode.getName();
            return !StringUtils.isBlank(name)
                    && StringUtils.isBlank(contentNode.getAttributes().get(name))
                    && contentNode.getChildren().size() == 1
                    && (contentNode.getChildren().get(0) instanceof TextNode);
        }
        return false;
    }

    @Override
    public void write(Writer writer, AbstractTreeNode treeNode) throws IOException {
        assert (writer != null);
        int nowIndex = 0;
        if (treeNode instanceof ContentNode) {
            ContentNode contentNode = (ContentNode) treeNode;
            if (contentNode.getAttributes().containsKey(ARRAY_ID_ATTRIBUTE)) {
                ArrayNode arrayNode = getObjectMapper().createArrayNode();
                nowIndex = this.write(contentNode, arrayNode, nowIndex);
                getObjectMapper().writeValue(writer, arrayNode);
            } else {
                ObjectNode objectNode = getObjectMapper().createObjectNode();
                nowIndex = this.write(contentNode, objectNode, nowIndex);
                getObjectMapper().writeValue(writer, objectNode);
            }
        } else if (treeNode instanceof TextNode) {
            TextNode textNode = (TextNode) treeNode;
            writer.append(textNode.getTextContent());
        } else if (treeNode instanceof CommentNode) {
            CommentNode commentNode = (CommentNode) treeNode;
            writer.append("/*");
            writer.append(commentNode.getTextContent());
            writer.append("*/");
        } else {
            LOGGER.error("Json does not allow this here.we just delete it : {}", treeNode.toString());
        }
    }


    private int write(ContentNode contentNode, ObjectNode objectNode, int nowIndex) throws IOException {
        if (contentNode.getAttributes().containsKey(ARRAY_ID_ATTRIBUTE)) {
            LOGGER.error("Json does not allow this here.we just delete it : {}", contentNode.toString());
            return nowIndex;
        }
        if (!contentNode.getAttributes().isEmpty()) {
            ObjectNode attributeNode = getObjectMapper().createObjectNode();
            for (Map.Entry<String, String> entry : contentNode.getAttributes().entrySet()) {
                attributeNode.put(entry.getKey(), entry.getValue());
            }
            objectNode.put(ATTRIBUTES_KEY, attributeNode);
        }

        for (AbstractTreeNode treeNode : contentNode.getChildren()) {
            if (treeNode instanceof ContentNode) {
                ContentNode contentNode2 = (ContentNode) treeNode;
                if (contentNode2.getAttributes().containsKey(ARRAY_ID_ATTRIBUTE)) {
                    ArrayNode arrayNode2 = getObjectMapper().createArrayNode();
                    objectNode.set(contentNode2.getName(), arrayNode2);
                    nowIndex = this.write(contentNode2, arrayNode2, nowIndex);
                }
//                else if (isSingleNameTextPair(contentNode2)) {
//                    objectNode.put(
//                            contentNode2.getName(),
//                            ((TextNode) contentNode2.getChildren().get(0)).getTextContent()
//                    );
//                }
                else {
                    ObjectNode objectNode2 = getObjectMapper().createObjectNode();
                    objectNode.set(contentNode2.getName(), objectNode2);
                    nowIndex = this.write(contentNode2, objectNode2, nowIndex);
                }
            } else if (treeNode instanceof TextNode) {
                objectNode.put(TEXT_KEY + nowIndex, ((TextNode) treeNode).getTextContent());
                nowIndex++;
            } else if (treeNode instanceof CommentNode) {
                objectNode.put(COMMENT_KEY + nowIndex, ((CommentNode) treeNode).getTextContent());
                nowIndex++;
            } else {
                LOGGER.error("Json does not allow this here.we just delete it : {}", treeNode.toString());
            }
        }

        return nowIndex;
    }

    private int write(ContentNode contentNode, ArrayNode arrayNode, int nowIndex) throws IOException {
        if (!contentNode.getAttributes().containsKey(ARRAY_ID_ATTRIBUTE)) {
            LOGGER.error("Json does not allow this here.we just delete it : {}", contentNode.toString());
            return nowIndex;
        }

        for (AbstractTreeNode treeNode : contentNode.getChildren()) {
            if (treeNode instanceof ContentNode) {
                ContentNode contentNode2 = (ContentNode) treeNode;
                if (contentNode2.getAttributes().containsKey(ARRAY_ID_ATTRIBUTE)) {
                    ArrayNode arrayNode2 = getObjectMapper().createArrayNode();
                    arrayNode.add(arrayNode2);
                    nowIndex = this.write(contentNode2, arrayNode2, nowIndex);
                }
//                else if (isSingleNameTextPair(contentNode2)) {
//                    arrayNode.add(((TextNode) contentNode2.getChildren().get(0)).getTextContent());
//                }
                else {
                    ObjectNode objectNode2 = getObjectMapper().createObjectNode();
                    arrayNode.add(objectNode2);
                    nowIndex = this.write(contentNode2, objectNode2, nowIndex);
                }
            } else if (treeNode instanceof TextNode) {
                arrayNode.add(((TextNode) treeNode).getTextContent());
            } else {
                LOGGER.error("Json does not allow this here.we just delete it : {}", treeNode.toString());
            }
        }

        return nowIndex;
    }


    @Override
    public void read(Reader reader, ContentNode contentNode) throws IOException {
        assert (reader != null);
        JsonNode jsonNode = getObjectMapper().readTree(reader);
        if (jsonNode instanceof ObjectNode) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            this.read(contentNode, objectNode);
        } else if (jsonNode instanceof ArrayNode) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            this.read(contentNode, arrayNode);
        } else {
            throw new X8lGrammarException("JsonDealer can not deal with json who represent this type." + jsonNode.getClass());
        }
    }

    private void read(ContentNode contentNode, ObjectNode objectNode) {
        for (Iterator<Map.Entry<String, JsonNode>> it = objectNode.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            if (ATTRIBUTES_KEY.equals(entry.getKey())) {
                JsonNode attributeJsonNode = entry.getValue();
                for (Iterator<Map.Entry<String, JsonNode>> attributeJsonNodeIterator = attributeJsonNode.fields(); attributeJsonNodeIterator.hasNext(); ) {
                    Map.Entry<String, JsonNode> attributeJsonNodeEntry = attributeJsonNodeIterator.next();
                    contentNode.addAttribute(attributeJsonNodeEntry.getKey(), attributeJsonNodeEntry.getValue().asText());
                }
                continue;
            } else if (entry.getKey().startsWith(TEXT_KEY)) {
                new TextNode(contentNode, entry.getValue().asText());
                continue;
            } else if (entry.getKey().startsWith(COMMENT_KEY)) {
                new CommentNode(contentNode, entry.getValue().asText());
                continue;
            }
            ContentNode childContentNode = new ContentNode(contentNode);
            childContentNode.addAttribute(entry.getKey());
            if (entry.getValue() instanceof ObjectNode) {
                this.read(childContentNode, (ObjectNode) entry.getValue());
            } else if (entry.getValue() instanceof ArrayNode) {
                ArrayNode arrayNode = (ArrayNode) entry.getValue();
                this.read(childContentNode, arrayNode);
            } else {
                new TextNode(childContentNode, entry.getValue().asText());
            }
        }
    }

    private void read(ContentNode contentNode, ArrayNode arrayNode) {
        contentNode.addAttribute(ARRAY_ID_ATTRIBUTE);
        for (Iterator<JsonNode> it = arrayNode.elements(); it.hasNext(); ) {
            JsonNode childNode = it.next();
            ContentNode childContentNode = new ContentNode(contentNode);


            if (childNode instanceof ObjectNode) {
                this.read(childContentNode, (ObjectNode) childNode);
            } else if (childNode instanceof ArrayNode) {
                this.read(childContentNode, (ArrayNode) childNode);
            } else {
                new TextNode(childContentNode, childNode.asText());
            }
        }
    }

    @Override
    public String toString() {
        return this.getClass().getCanonicalName();
    }
}

package com.xenoamess.x8l.dealers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xenoamess.x8l.AbstractTreeNode;
import com.xenoamess.x8l.ContentNode;
import com.xenoamess.x8l.TextNode;
import com.xenoamess.x8l.X8lGrammarException;

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
    public static final String ARRAY_ID_ATTRIBUTE = "[";
    public static final JsonDealer INSTANCE = new JsonDealer();
    private static ObjectMapper objectMapper;

    private static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    @Override
    public void write(Writer writer, AbstractTreeNode treeNode) throws IOException {
        assert (writer != null);

        if (treeNode instanceof ContentNode) {
            ContentNode contentNode = (ContentNode) treeNode;
            if (contentNode.getAttributes().containsKey(ARRAY_ID_ATTRIBUTE)) {
                ArrayNode arrayNode = getObjectMapper().createArrayNode();
                this.write(contentNode, arrayNode);
                getObjectMapper().writeValue(writer, arrayNode);
            } else {
                ObjectNode objectNode = getObjectMapper().createObjectNode();
                this.write(contentNode, objectNode);
                getObjectMapper().writeValue(writer, objectNode);
            }
        } else if (treeNode instanceof TextNode) {
            TextNode textNode = (TextNode) treeNode;
            writer.append(textNode.getTextContent());
        } else {
            System.err.println("Json does not allow this here.we just delete it." + treeNode.toString());
        }
    }


    private void write(ContentNode contentNode, ObjectNode objectNode) throws IOException {
        if (contentNode.getAttributes().containsKey(ARRAY_ID_ATTRIBUTE)) {
            System.err.println("Json does not allow this here.we just delete it." + contentNode.toString());
            return;
        }

        for (AbstractTreeNode treeNode : contentNode.getChildren()) {
            if (treeNode instanceof ContentNode) {
                ContentNode contentNode2 = (ContentNode) treeNode;
                if (contentNode2.getAttributes().containsKey(ARRAY_ID_ATTRIBUTE)) {
                    ArrayNode arrayNode2 = getObjectMapper().createArrayNode();
                    objectNode.set(contentNode2.getName(), arrayNode2);
                    this.write(contentNode2, arrayNode2);
                } else if (contentNode2.getChildren().size() == 1 && contentNode2.getChildren().get(0) instanceof TextNode) {
                    objectNode.put(
                            contentNode2.getName(),
                            ((TextNode) contentNode2.getChildren().get(0)).getTextContent()
                    );
                } else {
                    ObjectNode objectNode2 = getObjectMapper().createObjectNode();
                    objectNode.set(contentNode2.getName(), objectNode2);
                    this.write(contentNode2, objectNode2);
                }
            } else {
                System.err.println("Json does not allow this here.we just delete it." + treeNode.toString());
            }
        }
    }

    private void write(ContentNode contentNode, ArrayNode arrayNode) throws IOException {
        if (!contentNode.getAttributes().containsKey(ARRAY_ID_ATTRIBUTE)) {
            System.err.println("Json does not allow this here.we just delete it." + contentNode.toString());
            return;
        }

        for (AbstractTreeNode treeNode : contentNode.getChildren()) {
            if (treeNode instanceof ContentNode) {
                ContentNode contentNode2 = (ContentNode) treeNode;
                if (contentNode2.getAttributes().containsKey(ARRAY_ID_ATTRIBUTE)) {
                    ArrayNode arrayNode2 = getObjectMapper().createArrayNode();
                    arrayNode.add(arrayNode2);
                    this.write(contentNode2, arrayNode2);
                } else if (contentNode2.getChildren().size() == 1 && contentNode2.getChildren().get(0) instanceof TextNode) {
                    arrayNode.add(((TextNode) contentNode2.getChildren().get(0)).getTextContent());
                } else {
                    ObjectNode objectNode2 = getObjectMapper().createObjectNode();
                    arrayNode.add(objectNode2);
                    this.write(contentNode2, objectNode2);
                }
            } else {
                System.err.println("Json does not allow this here.we just delete it." + treeNode.toString());
            }
        }
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

}

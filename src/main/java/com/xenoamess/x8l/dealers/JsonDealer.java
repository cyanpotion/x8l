package com.xenoamess.x8l.dealers;

import com.xenoamess.x8l.AbstractTreeNode;
import com.xenoamess.x8l.ContentNode;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * @author XenoAmess
 */
public class JsonDealer implements AbstractLanguageDealer {
    public static final JsonDealer INSTANCE = new JsonDealer();

    @Override
    public void write(Writer writer, AbstractTreeNode treeNode) throws IOException {

    }

    @Override
    public void read(Reader reader, ContentNode contentNode) throws IOException {

    }
}

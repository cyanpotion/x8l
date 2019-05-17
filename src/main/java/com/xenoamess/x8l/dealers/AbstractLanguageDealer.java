package com.xenoamess.x8l.dealers;

import com.xenoamess.x8l.AbstractTreeNode;
import com.xenoamess.x8l.ContentNode;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * @author XenoAmess
 */
public interface AbstractLanguageDealer {

    void write(Writer writer, AbstractTreeNode treeNode) throws IOException;

    /**
     * notice that only ContentNode can read.
     * notice that
     *
     * @param reader      reader to read
     * @param contentNode contentNode to read to
     * @throws IOException IOException
     */
    void read(Reader reader, ContentNode contentNode) throws IOException;
}

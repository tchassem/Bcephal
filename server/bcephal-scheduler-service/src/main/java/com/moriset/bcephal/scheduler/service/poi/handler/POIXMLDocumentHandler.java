package com.moriset.bcephal.scheduler.service.poi.handler;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

public final class POIXMLDocumentHandler {
	protected void handlePOIXMLDocument(POIXMLDocument doc) throws Exception {
//        assertNotNull(doc.getAllEmbeddedParts());
//        assertNotNull(doc.getPackage());
//        assertNotNull(doc.getPackagePart());
//        assertNotNull(doc.getProperties());
//        assertNotNull(doc.getRelations());
    }

    protected static boolean isEncrypted(InputStream stream) throws IOException {
        if (FileMagic.valueOf(stream) == FileMagic.OLE2) {
            try (POIFSFileSystem poifs = new POIFSFileSystem(stream)) {
                if (poifs.getRoot().hasEntryCaseInsensitive(Decryptor.DEFAULT_POIFS_ENTRY)) {
                    return true;
                }
            }
            throw new IOException("Wrong file format or file extension for OO XML file");
        }
        return false;
    }

    /**
     * Recurse through the document and convert all elements so they are available in the ooxml-lite jar.
     * This method only makes sense for hierarchical documents like .docx.
     * If the document is split up in different parts like in .pptx, each part needs to be provided.
     *
     * @param base the entry point
     */
    public static void cursorRecursive(XmlObject base) {
        try (XmlCursor cur = base.newCursor()) {
            cursorRecursive(cur);
        }
    }

    private static void cursorRecursive(XmlCursor cur) {
        do {
//            assertNotNull(cur.getObject());
            cur.push();
            for (boolean b = cur.toFirstAttribute(); b; b = cur.toNextAttribute()) {
//                assertNotNull(cur.getObject());
            }
            cur.pop();
            cur.push();

            if (cur.toFirstChild()) {
                cursorRecursive(cur);
            }
            cur.pop();
        } while (cur.toNextSibling());
    }
}

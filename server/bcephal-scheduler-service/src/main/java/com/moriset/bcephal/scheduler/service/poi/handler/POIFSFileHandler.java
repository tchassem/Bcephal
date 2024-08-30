package com.moriset.bcephal.scheduler.service.poi.handler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.POIDocument;
import org.apache.poi.hpsf.extractor.HPSFPropertiesExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class POIFSFileHandler extends AbstractFileHandler {

	 @Override
	    public void handleFile(File stream, String path) throws Exception {
	        try (POIFSFileSystem fs = new POIFSFileSystem(stream)) {
	            handlePOIFSFileSystem(fs);
	            handleHPSFProperties(fs);
	        }
	    }

	    private void handleHPSFProperties(POIFSFileSystem fs) throws IOException {
	        try (HPSFPropertiesExtractor ext = new HPSFPropertiesExtractor(fs)) {
	            // can be null
	            ext.getDocSummaryInformation();
	            ext.getSummaryInformation();

//	            assertNotNull(ext.getDocumentSummaryInformationText());
//	            assertNotNull(ext.getSummaryInformationText());
//	            assertNotNull(ext.getText());
	        }
	    }

	    private void handlePOIFSFileSystem(POIFSFileSystem fs) {
//	        assertNotNull(fs);
//	        assertNotNull(fs.getRoot());
	    }

	    protected void handlePOIDocument(POIDocument doc) throws Exception {
	        try (UnsynchronizedByteArrayOutputStream out = UnsynchronizedByteArrayOutputStream.builder().get()) {
	            doc.write(out);

	            try (InputStream in = out.toInputStream();
	                POIFSFileSystem fs = new POIFSFileSystem(in)) {
	                handlePOIFSFileSystem(fs);
	            }
	        }
	    }

	    // a test-case to test this locally without executing the full TestAllFiles
//	    @Test
	    void test() throws Exception {
	        File file = new File("test-data/poifs/Notes.ole2");

	        //try (InputStream stream = new FileInputStream(file)) {
	            handleFile(file, file.getPath());
	        //}

	        //handleExtracting(file);
	    }

}

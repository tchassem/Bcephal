package com.moriset.bcephal.scheduler.service.poi.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.usermodel.HSSFOptimiser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class HSSFFileHandler extends SpreadsheetHandler {

	 private final POIFSFileHandler delegate = new POIFSFileHandler();
	    @Override
	    public void handleFile(File stream, String path) throws Exception {
	        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(stream));
	        handleWorkbook(wb);

	        // TODO: some documents fail currently...
	        // Note - as of Bugzilla 48036 (svn r828244, r828247) POI is capable of evaluating
	        // IntersectionPtg.  However it is still not capable of parsing it.
	        // So FormulaEvalTestData.xls now contains a few formulas that produce errors here.
	        //HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(wb);
	        //evaluator.evaluateAll();

	        delegate.handlePOIDocument(wb);

	        // also try to see if some of the Records behave incorrectly
	        // TODO: still fails on some records... RecordsStresser.handleWorkbook(wb);

	        HSSFOptimiser.optimiseCellStyles(wb);
//	        for(Sheet sheet : wb) {
//	            for (Row row : sheet) {
////	                for (Cell cell : row) {
//////	                    assertNotNull(cell.getCellStyle());
////	                }
//	            }
//	        }

	        HSSFOptimiser.optimiseFonts(wb);
	    }

	    private static final Set<String> EXPECTED_ADDITIONAL_FAILURES = new HashSet<>();
	    static {
	        // encrypted
	        EXPECTED_ADDITIONAL_FAILURES.add("spreadsheet/35897-type4.xls");
	        EXPECTED_ADDITIONAL_FAILURES.add("spreadsheet/xor-encryption-abc.xls");
	        EXPECTED_ADDITIONAL_FAILURES.add("spreadsheet/password.xls");
	        // broken files
	        EXPECTED_ADDITIONAL_FAILURES.add("spreadsheet/43493.xls");
	        // TODO: ok to ignore?
	        EXPECTED_ADDITIONAL_FAILURES.add("spreadsheet/50833.xls");
	        EXPECTED_ADDITIONAL_FAILURES.add("spreadsheet/51832.xls");
	        EXPECTED_ADDITIONAL_FAILURES.add("spreadsheet/XRefCalc.xls");
	        EXPECTED_ADDITIONAL_FAILURES.add("spreadsheet/61300.xls");
	    }

	    @Override
	    public void handleAdditional(File file) throws Exception {
	        // redirect stdout as the examples often write lots of text
	        PrintStream oldOut = System.out;
	        String fileWithParent = file.getParentFile().getName() + "/" + file.getName();
	        try {
	           // BiffViewer bv = new BiffViewer();
	            //bv.parse(file, null);
	            //assertFalse( EXPECTED_ADDITIONAL_FAILURES.contains(fileWithParent), "Expected Extraction to fail for file " + file + " and handler " + this + ", but did not fail!" );
	        } catch (OldExcelFormatException e) {
	            // old excel formats are not supported here
	        } catch (RuntimeException e) {
	            if(!EXPECTED_ADDITIONAL_FAILURES.contains(fileWithParent)) {
	                throw e;
	            }
	        } finally {
	            System.setOut(oldOut);
	        }
	    }

	    // a test-case to test this locally without executing the full TestAllFiles
	   // @Test
	    void test() throws Exception {
	        File file = new File("../test-data/spreadsheet/59074.xls");

	        //try (InputStream stream = new FileInputStream(file)) {
	            handleFile(file, file.getPath());
	        //}

	        handleExtracting(file);

	        handleAdditional(file);
	    }

	    // a test-case to test this locally without executing the full TestAllFiles
	   // @Test
	    void testExtractor() throws Exception {
	        handleExtracting(new File("../test-data/spreadsheet/59074.xls"));
	    }

}

package com.moriset.bcephal.scheduler.service.poi.handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.hssf.usermodel.HSSFObjectData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.sl.usermodel.GroupShape;
import org.apache.poi.sl.usermodel.ObjectMetaData.Application;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFObjectShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.dashboard.service.PresentationChartService;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class XSLFFileHandler extends SlideShowHandler {
	
	
	private PresentationChartService presentationChartService;
	
	@Override
    public void handleFile(File pack, String path) throws Exception {
		 try {
			 	 setOutPath(path); 
				 BufferedInputStream in = new BufferedInputStream(new FileInputStream(pack));
				 setPowerPoint(getXSLFSlideShowFactory().create(in));
				 setPowerPointInner(new XSLFSlideShow(getPowerPoint().getPackage()));
				 in.close();
	           // new POIXMLDocumentHandler().handlePOIXMLDocument(slide);
	            POIXMLDocumentHandler.cursorRecursive(getPowerPoint().getCTPresentation());
	            XSLFSlide destSlide = getPowerPoint().createSlide(getPowerPoint().getSlides().getFirst().getSlideLayout());
	            loadEmbeddedWithSlide(destSlide);	            
	            handleSlideShow();
	        } catch (POIXMLException e) {
	            Exception cause = (Exception)e.getCause();
	            throw cause == null ? e : cause;
	        }
    }

//	private  void copyEmbeddedObject(XSLFObjectShape objectShape) {
//        XSLFObjectData objectData = objectShape.getObjectData();
//        if (objectData != null && objectData.getOLE2ClassName().equals("Excel.Sheet.12")) {
//            try {
//                InputStream is = objectData.getInputStream();
//                XSSFWorkbook workbook = new XSSFWorkbook(is);
//                // Process the embedded Excel data as needed
//                // For example, you can iterate over sheets and cells:
//                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
//                    // Process each sheet
//                }
//                workbook.close();
//                is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        // Handle other types of embedded objects as needed
//    }
	
	private void readShapes(Shape<?,?> shape) {
        // recursively walk group-shapes
        if(shape instanceof GroupShape) {
            GroupShape<? extends Shape<?,?>, ?> shapes = (GroupShape<? extends Shape<?,?>, ?>) shape;
            for (Shape<? extends Shape<?,?>, ?> shape_ : shapes) {
                readShapes(shape_);
            }
        }

        if(shape instanceof SimpleShape) {
            SimpleShape<?, ?> simpleShape = (SimpleShape<?, ?>) shape;
            simpleShape.getFillColor();
            simpleShape.getFillStyle();
            simpleShape.getStrokeStyle();
            simpleShape.getLineDecoration();
        }

        if(shape instanceof XSLFGraphicFrame) {
//			XSLFGraphicFrame frame = (XSLFGraphicFrame)shape;
//			if(shape instanceof XSLFObjectShape){
//				XSLFObjectShape objectShape = (XSLFObjectShape)shape;
//				//loadEmbedded(objectShape);
//			}
		}
        
        readText(shape);
    }

	private void readText(Shape<?,?> s) {
        if (s instanceof TextShape) {
            for (TextParagraph<?,?,?> tp : (TextShape<?,?>)s) {
                for (TextRun tr : tp) {
                    tr.getRawText();
                }
            }
        }
    }

    private void loadEmbeddedWithSlide(XSLFSlide slide) throws Exception {
    	for(XSLFShape shape : slide.getShapes()) {
    		readShapes(shape);
    	}
	}



	@Override
    public void handleExtracting(File file) throws Exception {
        super.handleExtracting(file);


        // additionally try the other getText() methods
        try (SlideShowExtractor<?,?> extractor = (SlideShowExtractor<?, ?>) ExtractorFactory.createExtractor(file)) {
           // assertNotNull(extractor);
            extractor.setSlidesByDefault(true);
            extractor.setNotesByDefault(true);
            extractor.setMasterByDefault(true);

           // assertNotNull(extractor.getText());

            extractor.setSlidesByDefault(false);
            extractor.setNotesByDefault(false);
            extractor.setMasterByDefault(false);

           // assertEquals("", extractor.getText(), "With all options disabled we should not get text");
        }
    }

    // a test-case to test this locally without executing the full TestAllFiles
    @Override
    //@Test
    void test() throws Exception {
        File file = new File("test-data/slideshow/ca.ubc.cs.people_~emhill_presentations_HowWeRefactor.pptx");
       // try (File stream = new File(file)) {
            handleFile(file, file.getPath());
       // }

        handleExtracting(file);
    }
    

    
    
    
    public void loadEmbedded(Workbook wb) throws Exception {
        if (wb instanceof HSSFWorkbook) {
            loadEmbedded((HSSFWorkbook)wb);
        }
        else if (wb instanceof XSSFWorkbook) {
            loadEmbedded((XSSFWorkbook)wb);
        }
        else {
            throw new IllegalArgumentException(wb.getClass().getName());
        }
    }

    public void loadEmbedded(HSSFWorkbook workbook) throws IOException {
        for (HSSFObjectData obj : workbook.getAllEmbeddedObjects()) {
            //the OLE2 Class Name of the object
            String oleName = obj.getOLE2ClassName();
            switch (oleName) {
                case "Worksheet": {
                    DirectoryNode dn = (DirectoryNode) obj.getDirectory();
                    HSSFWorkbook embeddedWorkbook = new HSSFWorkbook(dn, false);
                    embeddedWorkbook.close();
                    break;
                }
                case "Document": {
//                    DirectoryNode dn = (DirectoryNode) obj.getDirectory();
                    //HWPFDocument embeddedWordDocument = new HWPFDocument(dn);
                    //embeddedWordDocument.close();
                    break;
                }
                case "Presentation": {
//                    DirectoryNode dn = (DirectoryNode) obj.getDirectory();
                    //SlideShow<?, ?> embeddedSlieShow = new HSLFSlideShow(dn);
                    //embeddedSlieShow.close();
                    break;
                }
                default:
                    if (obj.hasDirectoryEntry()) {
                        // The DirectoryEntry is a DocumentNode. Examine its entries to find out what it is
//                        DirectoryNode dn = (DirectoryNode) obj.getDirectory();
//                        for (org.apache.poi.poifs.filesystem.Entry entry : dn) {
//                            //System.out.println(oleName + "." + entry.getName());
//                        }
                    } else {
                        // There is no DirectoryEntry
                        // Recover the object's data from the HSSFObjectData instance.
//                        byte[] objectData = obj.getObjectData();
                    }
                    break;
            }
        }
    }

    public void loadEmbedded(XSLFObjectShape shape) throws Exception {
        for (PackagePart pPart : getPowerPointInner().getAllEmbeddedParts()) {
            String contentType = pPart.getContentType();
            switch (contentType) {
                case "application/vnd.ms-excel":
                    // Excel Workbook - either binary or OpenXML
                    try (InputStream stream = pPart.getInputStream()) {
                        HSSFWorkbook embeddedWorkbook = new HSSFWorkbook(stream);
                        embeddedWorkbook.close();
                    }
                    break;
                case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                    // Excel Workbook - OpenXML file format
                    try (Workbook wb = WorkbookFactory.create(pPart.getInputStream());) {
                        XSSFWorkbook embeddedWorkbook = (XSSFWorkbook) wb;
//                        PackagePart value = pPart;
                        XSSFSheet sheet = embeddedWorkbook.getSheetAt(0);
    					if(sheet != null) {
    						Iterator<Row> rows = sheet.rowIterator();
    						if(rows.hasNext()) {
    							Row row = rows.next();
    							Cell cell1 = row.getCell(0);
//    							Cell cell2 = row.getCell(1);
    							String category = cell1 != null ? cell1.getStringCellValue() : null;
    							String name = cell1 != null ? cell1.getStringCellValue() : null;
    							if(StringUtils.hasText(category) || StringUtils.hasText(name)) {
    								Application app = Application.lookup(shape.getCTOleObject().getProgId());
    								try (OutputStream os = shape.updateObjectData(app, app != null ? app.getMetaData() : Application.EXCEL_V12.getMetaData())) {
    					            
    								List<String> path = presentationChartService.ExtractReportData(category, embeddedWorkbook, os);
    								if(path.size() > 0) {
    									embeddedWorkbook.close();
//    									try ( FileInputStream out = new FileInputStream(path.get(0));
//    									         /*XSSFWorkbook outWorkbook = new XSSFWorkbook(out);*/) {
//    											  //OutputStream os = getOutputStream(value, oleRel);
//    											  //outWorkbook.write(os);
//    											  //outWorkbook.close();
//    											  //os.close();
//    												OutputStream os = value.getOutputStream();
//    												out.transferTo(os);
//    												out.close();
//    												os.close();
//    											 // shape.getCTOleObject().save(os);
//    									    }catch (Exception e) {
//    											throw new BcephalException(String.format("Unable to read embedded object: " + e.getLocalizedMessage()));
//    										}
    									}
    								}
    							}
    						}
    					}
                        embeddedWorkbook.close();
                    }
                    break;
                case "application/msword":
                    // Word Document - binary (OLE2CDF) file format
                    try (InputStream stream = pPart.getInputStream()) {
                        ///HWPFDocument document = new HWPFDocument(stream);
                        //document.close();
                    }
                    break;
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                    // Word Document - OpenXML file format
                    try (InputStream stream = pPart.getInputStream()) {
                        XWPFDocument document = new XWPFDocument(stream);
                        document.close();
                    }
                    break;
                case "application/vnd.ms-powerpoint":
                    // PowerPoint Document - binary file format
                    try (InputStream stream = pPart.getInputStream()) {
                       // HSLFSlideShow slideShow = new HSLFSlideShow(stream);
                        //slideShow.close();
                    }
                    break;
                case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                    // PowerPoint Document - OpenXML file format
                    
                    try (XMLSlideShow slide = new XMLSlideShow(pPart.getInputStream());
                            XSLFSlideShow slideInner2 = new XSLFSlideShow(slide.getPackage())) {
//                           assertNotNull(slideInner.getPresentation());
//                           assertNotNull(slideInner.getSlideMasterReferences());
//                           assertNotNull(slideInner.getSlideReferences());

                           new POIXMLDocumentHandler().handlePOIXMLDocument(slide);
                           POIXMLDocumentHandler.cursorRecursive(slide.getCTPresentation());

                           handleSlideShow();
                           slide.close();
                       } catch (POIXMLException e) {
                           Exception cause = (Exception)e.getCause();
                           throw cause == null ? e : cause;
                       }
                    break;
                default:
                    // Any other type of embedded object.
                    System.out.println("Unknown Embedded Document: " + contentType);
                    try (InputStream inputStream = pPart.getInputStream()) {

                    }
                    break;
            }
        }
    }
}

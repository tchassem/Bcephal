package com.moriset.bcephal.scheduler.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLDocumentPart.RelationPart;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.ZipPackagePart;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.usermodel.ObjectMetaData;
import org.apache.poi.sl.usermodel.ObjectMetaData.Application;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFChart;
import org.apache.poi.xslf.usermodel.XSLFFactory;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFObjectShape;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFRelation;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSheet;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideShowFactory;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.dashboard.service.PresentationChartService;
import com.moriset.bcephal.domain.filters.VariableIntervalPeriod;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.scheduler.domain.Presentation;
import com.moriset.bcephal.scheduler.domain.PresentationTemplate;
import com.moriset.bcephal.scheduler.domain.PresentationVariables;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerItem;
import com.moriset.bcephal.scheduler.service.poi.handler.XSLFFileHandler;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PresentationRunner {

	PresentationTemplate template;
	
	Presentation presentation;
	
	SchedulerPlannerItem item;
	
	TaskProgressListener listener;
	
	String operationCode;
	
	String presentationsDir;
	
	String projectCode;
	
	PresentationChartService presentationChartService;
	
	Map<String, Object> variableValues;
	
	XSLFSlide templateSlide;
	XSLFSlide footerSlide;
	XMLSlideShow powerPoint;
	
	
	public void run(boolean first) {
		
		if(template == null) {
			throw new BcephalException("Presentation template is null!");
		}
		if(!StringUtils.hasText(template.getRepository())) {
			throw new BcephalException("Presentation template file is null!");
		}
		if(!new File(template.getRepository()).exists()) {
			throw new BcephalException("Presentation template file not found: " + template.getRepository());
		}
		
		try {
			FileInputStream in = new FileInputStream(template.getRepository());
			try (XMLSlideShow templatePpt = new XMLSlideShow(in)) {
				powerPoint = null;	
				
				if(first) {					
					presentation = new Presentation();
					presentation.setCode(operationCode);
					presentation.setName(template.getName());
					
					powerPoint = new XMLSlideShow(templatePpt.getPackage());
					int count = powerPoint.getSlides().size() - 1;
					while(count >= 0) {
						powerPoint.removeSlide(count);
						count--;
					}	
					savePresentation(first);					
				}
				
				templateSlide = template.isHasHeader() ? templatePpt.getSlides().get(1) : templatePpt.getSlides().get(0);
				powerPoint = new XMLSlideShow(new FileInputStream(presentation.getRepository()));
				
				
				
				if(template.isHasHeader() && first) {
					XSLFSlide headerSlide = templatePpt.getSlides().getFirst();
					if(headerSlide != null) {
						XSLFSlide slide = powerPoint.createSlide(headerSlide.getSlideLayout());
						slide.importContent(headerSlide);						
						formatSlide(slide);
					}
				}			
				if(template.isHasFooter() && first) {
					footerSlide = templatePpt.getSlides().getLast();
					if(footerSlide != null) {
						XSLFSlide slide = powerPoint.createSlide(footerSlide.getSlideLayout());
						slide.importContent(footerSlide);
						formatSlide(slide);
					}
				}
				
				first = false;
				createSlide();
				savePresentation(first);
				powerPoint.close();
			} 
		} catch (BcephalException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw new BcephalException("Presentation template file not found: " + template.getRepository());
		} catch (IOException e) {
			throw new BcephalException("Unable to read presentation template file: " + template.getRepository());
		}
	}
	
	
	
public void run0(boolean first) {
		
		if(template == null) {
			throw new BcephalException("Presentation template is null!");
		}
		if(!StringUtils.hasText(template.getRepository())) {
			throw new BcephalException("Presentation template file is null!");
		}
		if(!new File(template.getRepository()).exists()) {
			throw new BcephalException("Presentation template file not found: " + template.getRepository());
		}
		
		try {
			
			if(first && (  presentation == null || !StringUtils.hasText(presentation.getRepository()) ||  !Paths.get(presentation.getRepository()).toFile().exists())) {
				
				if(first) {					
					presentation = new Presentation();
					presentation.setCode(operationCode);
					presentation.setName(template.getName());
					
					powerPoint = new XMLSlideShow();
					savePresentation2(first);
				}
				
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(template.getRepository()));
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(presentation.getRepository()));
				
				IOUtils.copy(in, out);
				out.close();
				in.close();
			}
			BufferedInputStream pack = new BufferedInputStream(new FileInputStream(presentation.getRepository()));
			try (XMLSlideShow templatePpt = new XSLFSlideShowFactory().create(pack)) {
				powerPoint = null;	
				powerPoint = templatePpt;
				
				templateSlide = template.isHasHeader() ? templatePpt.getSlides().get(1) : templatePpt.getSlides().get(0);
				
				
				if(template.isHasHeader() && first) {
					XSLFSlide headerSlide = templatePpt.getSlides().getFirst();
					if(headerSlide != null) {
						XSLFSlide slide = powerPoint.createSlide(headerSlide.getSlideLayout());
						slide.importContent(headerSlide);						
						formatSlide(slide);
					}
				}			
				if(template.isHasFooter() && first) {
					footerSlide = templatePpt.getSlides().getLast();
					if(footerSlide != null) {
						XSLFSlide slide = powerPoint.createSlide(footerSlide.getSlideLayout());
						slide.importContent(footerSlide);
						formatSlide(slide);
					}
				}
				
				first = false;
				createSlide2();				
				savePresentation2(first);	
			} 
		} catch (BcephalException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw new BcephalException("Presentation template file not found: " + template.getRepository());
		} catch (Exception e) {
			throw new BcephalException("Unable to read presentation template file: " + template.getRepository());
		}
	}




public void run1(boolean first) {
	
	if(template == null) {
		throw new BcephalException("Presentation template is null!");
	}
	if(!StringUtils.hasText(template.getRepository())) {
		throw new BcephalException("Presentation template file is null!");
	}
	if(!new File(template.getRepository()).exists()) {
		throw new BcephalException("Presentation template file not found: " + template.getRepository());
	}
	
	try {
		if(first && (  presentation == null || !StringUtils.hasText(presentation.getRepository()) ||  !Paths.get(presentation.getRepository()).toFile().exists())) {
			
			if(first) {					
				presentation = new Presentation();
				presentation.setCode(operationCode);
				presentation.setName(template.getName());				
				powerPoint = new XMLSlideShow();
				
				savePresentation2(first);
			}
			
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(template.getRepository()));
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(presentation.getRepository()));
			
			IOUtils.copy(in, out);
			out.close();
			in.close();
		}
		//BufferedInputStream pack = new BufferedInputStream(new FileInputStream(presentation.getRepository()));
		File pack = new File(presentation.getRepository());
		
		XSLFFileHandler handler = new XSLFFileHandler();
		handler.handleFile(pack, presentation.getRepository());
		handler.handleExtracting(new File(presentation.getRepository()));
		
//			powerPoint = null;	
//			powerPoint = templatePpt;
//			
//			templateSlide = template.isHasHeader() ? templatePpt.getSlides().get(1) : templatePpt.getSlides().get(0);
//			
//			
//			if(template.isHasHeader() && first) {
//				XSLFSlide headerSlide = templatePpt.getSlides().getFirst();
//				if(headerSlide != null) {
//					XSLFSlide slide = powerPoint.createSlide(headerSlide.getSlideLayout());
//					slide.importContent(headerSlide);						
//					formatSlide(slide);
//				}
//			}			
//			if(template.isHasFooter() && first) {
//				footerSlide = templatePpt.getSlides().getLast();
//				if(footerSlide != null) {
//					XSLFSlide slide = powerPoint.createSlide(footerSlide.getSlideLayout());
//					slide.importContent(footerSlide);
//					formatSlide(slide);
//				}
//			}
			first = false;
			
			//buildXSSFWorkbook(Arrays.asList(presentation.getRepository()).toArray(new String[1]));
			
		
	} catch (BcephalException e) {
		log.error(template.getRepository(),e);
		throw e;
	} catch (FileNotFoundException e) {
		log.error(template.getRepository(),e);
		throw new BcephalException("Presentation template file not found: " + template.getRepository());
	} catch (Exception e) {
		log.error(template.getRepository(),e);
		throw new BcephalException("Unable to read presentation template file: " + template.getRepository());
	}
}

	protected void savePresentation(boolean first) {
		if(first) {
			Path path = buildPath();
			if(!path.toFile().getParentFile().exists()) {
				path.toFile().getParentFile().mkdirs();
			}
			presentation.setRepository(path.toString());
		}				
		try {
			FileOutputStream out = new FileOutputStream(presentation.getRepository());
			powerPoint.write(out);
			out.close();
		}
		catch (org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException e) {
			throw new BcephalException(e.getMessage(), e);
		}		
		catch (FileNotFoundException e) {
			throw new BcephalException("Presentation output repository not found: " + item.getRepository());
		} catch (IOException  e) {
			throw new BcephalException("Unable to write presentation file: " + presentation.getRepository());
		}
	}
	
	protected void savePresentation2(boolean first) {
		if(first) {
			Path path = buildPath();
			if(!path.toFile().getParentFile().exists()) {
				path.toFile().getParentFile().mkdirs();
			}
			presentation.setRepository(path.toString());
		}				
		try {
			 
			BufferedOutputStream out0 = new BufferedOutputStream(new FileOutputStream(presentation.getRepository()));
			UnsynchronizedByteArrayOutputStream out = UnsynchronizedByteArrayOutputStream.builder()
					 .setOutputStream(out0).get();
			powerPoint.write(out);
			out.close();
			powerPoint.close();
		}
		catch (org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException e) {
			throw new BcephalException(e.getMessage(), e);
		}		
		catch (FileNotFoundException e) {
			throw new BcephalException("Presentation output repository not found: " + item.getRepository());
		} catch (IOException  e) {
			throw new BcephalException("Unable to write presentation file: " + presentation.getRepository());
		}
	}
	
	public XSLFSlide createSlideCopy() throws IOException {
		Path tmpFile = Files.createTempFile(operationCode + "_template" + System.currentTimeMillis(), ".pptx");
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(template.getRepository()));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tmpFile.toFile()));

		IOUtils.copy(in, out);
		out.close();
		in.close();
		XMLSlideShow templatePpt = new XSLFSlideShowFactory().create(tmpFile.toFile(), null, true);
		XSLFSlide headerSlide = null;
		List<XSLFSlide> slides = templatePpt.getSlides();
		if (!template.isHasHeader() && !template.isHasFooter()) {
			if(slides.size() > 0) {
				headerSlide = templatePpt.getSlides().getFirst();
			}
		} else if (!template.isHasHeader() && template.isHasFooter()) {
			if(slides.size() > 1) {
				headerSlide = templatePpt.getSlides().getFirst();
			}
		}else if (template.isHasHeader() && !template.isHasFooter()) {
			if(slides.size() > 1) {
				headerSlide = templatePpt.getSlides().getLast();
			}
		}else if (template.isHasHeader() && template.isHasFooter()) {
			if(slides.size() > 2) {
				headerSlide = templatePpt.getSlides().get(1);
			}
		}
		
		in.close();
		templatePpt.close();

		return headerSlide;
	}
	
	public void createSlide() {
		if(templateSlide != null) {
			XSLFSlide slide = powerPoint.createSlide(templateSlide.getSlideLayout());
			slide.importContent(templateSlide);
			
			formatSlide(slide);
			
			if(template.isHasFooter() && footerSlide != null) {
				powerPoint.setSlideOrder(slide, slide.getSlideNumber() - 2);
			}
		}
	}
		
	public void createSlide2() throws SecurityException, IllegalAccessException, InvocationTargetException, IOException, OpenXML4JException, XmlException  {
		if(templateSlide != null) {
			//convertSmartArt();
			
			//XSLFSlide copy =  createSlideCopy();
			
			
//			XSLFSlideShow slideInner = new XSLFSlideShow(powerPoint.getPackage());
			XSLFSlide newSlide = powerPoint.createSlide(templateSlide.getSlideLayout());

			//byte[] excelData = IOUtils.toByteArray(new FileInputStream("chemin_vers_fichier_excel.xlsx"));
			
			for (XSLFShape shape : templateSlide.getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    // Si la forme est une zone de texte, la copier
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    XSLFTextShape newShape = newSlide.createTextBox();
                    newShape.setText(textShape.getText());
                    newShape.setAnchor(textShape.getAnchor());
                } else if (shape instanceof XSLFPictureShape) {
                    // Si la forme est une image, la copier
                    XSLFPictureShape picShape = (XSLFPictureShape) shape;
                    XSLFPictureShape newShape = newSlide.createPicture(picShape.getPictureData());
                    newShape.setAnchor(picShape.getAnchor());
                } else if (shape instanceof XSLFObjectShape) {
                    // Si la forme est une image, la copier
//                	XSLFObjectShape picShape = (XSLFObjectShape) shape;
                	
//                    // Ajouter les données de l'objet Excel au PowerPoint
//                      excelObject = powerPoint.addRelation(excelData, "application/vnd.ms-excel");
//
//                    // Ajouter l'objet Excel à la diapositive
//                    XSLFObjectShape excelShape = newSlide.createObject();
//                    excelShape.setObjectData(excelObject);
//                    excelShape.setAnchor(picShape.getAnchor()); // Définir la position et la taille de l'objet Excel
//
//                    picShape.getCTOleObject().setEmbed(excelData);
//                    
//                    
//                    XSLFObjectData objectData = powerPoint.addEmbed(excelData, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                    // Ajouter l'objet Excel à la diapositive sous forme de forme XSLFObjectShape
                	
//                	XSLFPictureData objectShape = powerPoint.addPicture(excelData, PictureType.PNG);
                	
//                	final int iconId = wb.addPicture(firefoxIcon.toByteArray(), XSSFWorkbook.PICTURE_TYPE_PNG);
//
//        			final int oleIdx = wb.addOlePackage(content.getBytes(), objectName, objectName + fileExtension, objectName + fileExtension);
//        			final Drawing<?> pat = sheet.createDrawingPatriarch();
                	
//                	XSLFPictureData detachedData = powerPoint.addPicture(new byte[0], PictureType.PNG);
//                	XSLFPictureShape pict = new XSLFPictureShape(detachedData,newSlide);
                	
                	
//                	XSLFObjectShape targetObjectShape = createOleShape(newSlide, getObjectData(picShape));
//                	targetObjectShape.setAnchor(picShape.getAnchor());
//                    
                } else if (shape instanceof XSLFGroupShape) {
                    // Si la forme est un groupe de formes, la copier
                    XSLFGroupShape groupShape = (XSLFGroupShape) shape;
                    XSLFGroupShape newGroupShape = newSlide.createGroup();
                    for (XSLFShape subShape : groupShape.getShapes()) {
                        if (subShape instanceof XSLFTextShape) {
                            // Copier les zones de texte dans le groupe
                            XSLFTextShape textShape = (XSLFTextShape) subShape;
                            XSLFTextShape newTextShape = newGroupShape.createTextBox();
                            newTextShape.setText(textShape.getText());
                            newTextShape.setAnchor(textShape.getAnchor());
                        } else if (subShape instanceof XSLFPictureShape) {
                            // Copier les images dans le groupe
                            XSLFPictureShape picShape = (XSLFPictureShape) subShape;
                            XSLFPictureShape newPicShape = newGroupShape.createPicture(picShape.getPictureData());
                            newPicShape.setAnchor(picShape.getAnchor());
                        }
                    }
                    // Copier les autres propriétés du groupe
//                    Shadow shadow = groupShape.getShadow();
//                    newGroupShape.setShadow(shadow);
                }
            }
		}
	}
	
	
	public  void buildXSSFWorkbook(String[] args) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(args[0])) {
            for (PackagePart pPart : workbook.getAllEmbeddedParts()) {
                String contentType = pPart.getContentType();
                try (InputStream is = pPart.getInputStream()) {
                    Closeable document = null;
                    switch (contentType) {
                        case "application/vnd.ms-excel":
                            // Excel Workbook - either binary or OpenXML
                            document = new HSSFWorkbook(is);
                            break;
                        case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                            // Excel Workbook - OpenXML file format
                            document = new XSSFWorkbook(is);
                            
                            break;
                        case "application/msword":
                            // Word Document - binary (OLE2CDF) file format
                          //  document = new HWPFDocument(is);
                            break;
                        case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                            // Word Document - OpenXML file format
                            document = new XWPFDocument(is);
                            break;
                        case "application/vnd.ms-powerpoint":
                            // PowerPoint Document - binary file format
                          //  document = new HSLFSlideShow(is);
                            break;
                        case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                            // PowerPoint Document - OpenXML file format
                            document = new XMLSlideShow(is);
                            break;
                        default:
                            // Any other type of embedded object.
                            document = is;
                            break;
                    }
                    if(document != null) {
                    	document.close();
                    }
                }
            }
        }
    }
		


	public void formatSlide(XSLFSlide slide) {
		if(slide != null) {
			for(XSLFShape shape : slide.getShapes().toArray(new XSLFShape[slide.getShapes().size()])) {
				if(shape instanceof XSLFAutoShape) {
					XSLFAutoShape textShape = (XSLFAutoShape)shape;	
					 Iterator<XSLFTextParagraph> it = textShape.iterator();
					 while(it.hasNext()) {
						 XSLFTextParagraph paragraph = it.next();
						 List<XSLFTextRun> runs = paragraph.getTextRuns();
						 if(runs.size() == 1) {
							 runs.get(0).setText(format(runs.get(0).getRawText()));
						 }
						 else if(runs.size() > 1) {
							 String text = paragraph.getText();
							 for(XSLFTextRun run : runs) {
								 run.setText("");
							 }	
							 runs.get(0).setText(format(text));
						 }						 					
					 }			
				}
				else if(shape instanceof XSLFGraphicFrame) {
					XSLFGraphicFrame frame = (XSLFGraphicFrame)shape;
					
					if(frame.getChart() != null) {
						XSLFChart chart = frame.getChart();
						String name = chart.getTitle().getBody().getParagraph(0).getText();
						if(StringUtils.hasText(name) && name.startsWith("{")) {
							name = name.replace("{", "");
							name = name.replace("}", "");
						}						
						presentationChartService.ReplaceChartWorkbook(powerPoint,chart, name, variableValues);
					}
					else if(shape instanceof XSLFObjectShape){
//						XSLFObjectShape objectShape = (XSLFObjectShape)shape;
						
					  //  buildExcelObject(slide,index, objectShape);
						
//						CTOleObject object = objectShape.getCTOleObject();
//						
//						List<RelationPart> parts = objectShape.getSheet().getRelationParts();
//						for(RelationPart part : parts) {
//							POIXMLDocumentPart doc = part.getDocumentPart();
//							if(doc instanceof XSLFObjectData) {
//								//((XSLFObjectData)doc).getDirectory();
//								((XSLFObjectData)doc).getFileName();
//							}
//						}
//						object.
//						objectShape.getCTOleObject()
//						objectShape.getSheet().getRelationPartById(oleRel).getDocumentPart();
//						
//						objectShape.getObjectData();
						
						
						
						
//						XSLFObjectShape newShape = slide.createOleShape(null);
						
						
//						slide.removeShape(shape);		
						
//						if(objectShape.getCTOleObject() != null) {
//							String oleRel = objectShape.getCTOleObject().getId();
//							//XSLFObjectData odata = objectShape.getObjectData();
//							XSLFChart data = objectShape.getSheet().getRelationPartById(oleRel).getDocumentPart();
//							if(data != null) {
//								try {
//									XSSFWorkbook workbook = data.getWorkbook();
//									if(workbook != null) {
//										XSSFSheet sheet = data.getWorkbook().getSheetAt(0);
//										Iterator<Row> rows = sheet.rowIterator();
//										if(rows.hasNext()) {
//											Row row = rows.next();
//											Cell cell1 = row.getCell(0);
//											Cell cell2 = row.getCell(1);
//											String category = cell1 != null ? cell1.getStringCellValue() : null;
//											String name = cell1 != null ? cell1.getStringCellValue() : null;
//											if(StringUtils.hasText(category) && StringUtils.hasText(name)) {
//												
//											}
//											//cell1.setCellValue("Joseph");
//										}
//									}									
//								}
//								catch (Exception e) {
//									// TODO: handle exception
//								}
//							}
//						}
					}
				}
			}
		}
	}
	
	public OutputStream updateObjectData(final PackagePart rp_, final XSLFObjectShape shape,final Application application, final ObjectMetaData metaData) throws IOException {
        final ObjectMetaData md = (application != null) ? application.getMetaData() : metaData;
        if (md == null || md.getClassID() == null) {
            throw new IllegalArgumentException("either application and/or metaData needs to be set.");
        }


        final XSLFSheet sheet = shape.getSheet();

        final PackagePart rp;
        if (shape.getCTOleObject().isSetId()) {
            // object data was already set
            //rp = sheet.getRelationPartById(shape.getCTOleObject().getId());
        	rp = rp_;
        } else {
            // object data needs to be initialized
            try {
                final XSLFRelation descriptor = XSLFRelation.OLE_OBJECT;
                final OPCPackage pack = sheet.getPackagePart().getPackage();
                int nextIdx = pack.getUnusedPartIndex(descriptor.getDefaultFileName());
                RelationPart rp1 = sheet.createRelationship(descriptor, XSLFFactory.getInstance(), nextIdx, false);
                shape.getCTOleObject().setId(rp1.getRelationship().getId());
                rp = rp1.getDocumentPart().getPackagePart();
            } catch (InvalidFormatException e) {
                throw new IOException("Unable to add new ole embedding", e);
            }

            // setting spid only works with a vml drawing object
            // oleObj.setSpid("_x0000_s"+(1025+objectIdx));
        }

        shape.getCTOleObject().setProgId(md.getProgId());
        shape.getCTOleObject().setName(md.getObjectName());

        return new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                addUpdatedData(rp, md, this);
            }
        };
    }

    private void addUpdatedData(PackagePart objectPart, ObjectMetaData metaData, ByteArrayOutputStream baos) throws IOException {
        objectPart.clear();
        try (InputStream bis = FileMagic.prepareToCheckMagic(baos.toInputStream());
             final OutputStream os = objectPart.getOutputStream()) {
            final FileMagic fm = FileMagic.valueOf(bis);

            if (fm == FileMagic.OLE2) {
                try (final POIFSFileSystem poifs = new POIFSFileSystem(bis)) {
                    poifs.getRoot().setStorageClsid(metaData.getClassID());
                    poifs.writeFilesystem(os);
                }
            } else if (metaData.getOleEntry() == null) {
                // OLE Name hasn't been specified, pass the input through
                baos.writeTo(os);
            } else {
                try (final POIFSFileSystem poifs = new POIFSFileSystem()) {
                    final ClassID clsId = metaData.getClassID();
                    if (clsId != null) {
                        poifs.getRoot().setStorageClsid(clsId);
                    }
                    poifs.createDocument(bis, metaData.getOleEntry());
                    Ole10Native.createOleMarkerEntry(poifs);
                    poifs.writeFilesystem(os);
                }
            }
        }
    }
    
	public void RemaneExelFile(PackagePart part_) throws IOException {
		if (part_ instanceof ZipPackagePart) {
            // Create a memory part
            PackagePartName partName = part_.getPartName();
            try {
                Field m = PackagePartName.class.getDeclaredField("partNameURI");
                m.setAccessible(true);
                String sheetName = String.format("/ppt/embeddings/Microsoft_Excel_Worksheet%s.xlsx", System.currentTimeMillis());
                m.set(partName, new URI(sheetName));
            } catch (Exception e) {
                throw new RuntimeException("_relationships", e);
            }
		}
	}
	
	public OutputStream getOutputStream(PackagePart part_,String rid) throws Exception {
        OutputStream outStream;
        // If this part is a zip package part (read only by design) we convert
        // this part into a MemoryPackagePart instance for write purpose.
        
        
     // remove old media file and reference
        part_.getPackage().removePart(part_.getPartName());
        part_.getPackage().removeRelationship(rid);
        //part_.getPackage().removePart(part_.getPartName());
        //part_.getPackage().removePart(PackagingURIHelper.getRelationshipPartName(part_.getPartName()));

        // add something new
        String sheetName = String.format("/ppt/embeddings/Microsoft_Excel_Worksheet%s.xlsx", System.currentTimeMillis());
        PackagePartName partName = PackagingURIHelper.createPartName(sheetName);
        PackagePart part = part_.getPackage().createPart(partName, part_.getContentType());
        
        outStream = part.getOutputStream();
        
        
//        if (part_ instanceof ZipPackagePart) {
//            // Delete logically this part
//        	part_.getPackage().removePart(part_.getPartName());
//
//            // Create a memory part
//        	OPCPackage pack = part_.getPackage();
//            PackagePart part = null;
//            PackagePartName partName = part_.getPartName();
//           
//            RemaneExelFile(part_);
//            
//            try {
//                Method m = OPCPackage.class.getDeclaredMethod("createPart", PackagePartName.class, String.class, boolean.class);
//                m.setAccessible(true);
//                part = (PackagePart) m.invoke(pack, partName, part_.getContentType().toString(), false);
//            } catch (Exception e) {
//                throw new RuntimeException("getOutputStreamImpl", e);
//            }
//            
//            if (part == null) {
//                throw new InvalidOperationException(
//                        "Can't create a temporary part !");
//            }
//            try {
//                Field m = PackagePart.class.getDeclaredField("_relationships");
//                m.setAccessible(true);
//                m.set(part_, part_.getRelationships());
//            } catch (Exception e) {
//                throw new RuntimeException("_relationships", e);
//            }
//            try {
//                Method m = PackagePart.class.getDeclaredMethod("getOutputStreamImpl");
//                m.setAccessible(true);
//                outStream = (OutputStream) m.invoke(part);
//            } catch (Exception e) {
//                throw new RuntimeException("getOutputStreamImpl", e);
//            }
//        } else {
//        	try {
//                Method m = PackagePart.class.getDeclaredMethod("getOutputStreamImpl");
//                m.setAccessible(true);
//                outStream = (OutputStream) m.invoke(part_);
//            } catch (Exception e) {
//                throw new RuntimeException("getOutputStreamImpl", e);
//            }
//        }
        return outStream;
    }
	
	private String format(String source) {
		if(StringUtils.hasText(source)) {
			for(String key : variableValues.keySet()) {
				Object obj = variableValues.get(key);
				String variable = "{"+ key + "}";
				String value = "";
				if(obj != null) {
					if(obj instanceof String) {
						value = (String)obj;
					}
					else if(obj instanceof Date) {
						value = new SimpleDateFormat("dd/MM/yyyy").format((Date)obj);
					}
					else if(obj instanceof Number) {
						value = new DecimalFormat().format((Number)obj);
					}
					else if(obj instanceof VariableIntervalPeriod) {
						VariableIntervalPeriod val = (VariableIntervalPeriod)obj;
						SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
						value = val.isOneDay() ? formatter.format(val.getStart()) 
								: formatter.format(val.getStart()) + " - " + formatter.format(val.getEnd());
					}
				}
				source = source.replace(variable, value);
			}			
		}
		return source;
	}
	
	public Path buildPath() {		
		String name = buildFileName();
		if(!name.toUpperCase().endsWith(".PPTX") || !name.toUpperCase().endsWith(".PPT")) {
			name += ".pptx";
		}		
		if(!StringUtils.hasText(item.getRepository())) {
			item.setRepository("");
		}
		Path path = Paths.get(presentationsDir, item.getRepository(), name);	
		return path;
	}
	
	private String buildFileName() {
		String description = item.getObjectName();
		if(!StringUtils.hasText(description)) {
			description = template.getName();
		}
		if(org.springframework.util.StringUtils.hasText(description)) {
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			PresentationVariables variables = new PresentationVariables();
			description = description.replace(variables.CURRENT_DATE, format.format(date));
			description = description.replace(variables.CURRENT_DATE_TIME, new SimpleDateFormat("yyyyMMddHHmmss").format(date));
			description = description.replace(variables.DAY, new SimpleDateFormat("dd").format(date));
			description = description.replace(variables.MONTH, new SimpleDateFormat("MM").format(date));
			description = description.replace(variables.MONTH_NAME, new SimpleDateFormat("MMMM").format(date));
			description = description.replace(variables.YEAR, new SimpleDateFormat("yyyy").format(date));
		}	
		return description;
	}
	
}

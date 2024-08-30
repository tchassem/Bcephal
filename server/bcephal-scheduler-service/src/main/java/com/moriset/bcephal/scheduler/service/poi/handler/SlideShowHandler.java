package com.moriset.bcephal.scheduler.service.poi.handler;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.sl.usermodel.GroupShape;
import org.apache.poi.sl.usermodel.Notes;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlideShowFactory;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
public abstract class SlideShowHandler extends POIFSFileHandler {
	
	private XSLFSlideShowFactory xSLFSlideShowFactory = new XSLFSlideShowFactory();
	private XMLSlideShow powerPoint;
	private XSLFSlideShow powerPointInner;
	private String outPath;
	
	public void handleSlideShow() throws IOException {
        renderSlides();
        readContent(powerPoint);
        readPictures();

        Path path = Files.createTempFile("Presentation", ".pptx");
        // write out the file
        BufferedOutputStream out0 = new BufferedOutputStream(new FileOutputStream(path.toFile()));
		//UnsynchronizedByteArrayOutputStream out = UnsynchronizedByteArrayOutputStream.builder().setPath(path).get();
		
		log.info(path.toFile().getCanonicalPath());
        /*XSLFSlide destSlide = */powerPoint.createSlide(powerPoint.getSlides().getFirst().getSlideLayout());
        //slide.importContent(powerPoint.getSlides().getFirst());
        
        powerPoint.write(out0);
        
        readContent(powerPoint);
        
        
        
         //read in the written file
//        try (SlideShow<?, ?> read = SlideShowFactory.create(out.toInputStream())) {
//            readContent(read);
//        }
        out0.close();
        powerPoint.close();
        
        
    }
	
    
	public SlideShow<?, ?> copy(){
		
		return null;
	}

    private void readContent(SlideShow<?, ?> ss) {
        for (Slide<?,?> s : ss.getSlides()) {
            s.getTitle();

            for (Shape<?,?> shape : s) {
                readShapes(shape);
            }

            Notes<?, ?> notes = s.getNotes();
            if(notes != null) {
                for (Shape<?, ?> shape : notes) {
                    readShapes(shape);
                }
            }

            for (Shape<?,?> shape : s.getMasterSheet()) {
                readShapes(shape);
            }
        }
    }

    private void readShapes(Shape<?,?> s) {
        // recursively walk group-shapes
        if(s instanceof GroupShape) {
            GroupShape<? extends Shape<?,?>, ?> shapes = (GroupShape<? extends Shape<?,?>, ?>) s;
            for (Shape<? extends Shape<?,?>, ?> shape : shapes) {
                readShapes(shape);
            }
        }

        if(s instanceof SimpleShape) {
            SimpleShape<?, ?> simpleShape = (SimpleShape<?, ?>) s;
            simpleShape.getFillColor();
            simpleShape.getFillStyle();
            simpleShape.getStrokeStyle();
            simpleShape.getLineDecoration();
        }

        readText(s);
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

    private void readPictures() {
//        for (PictureData pd : powerPoint.getPictureData()) {
//            Dimension dim = pd.getImageDimension();
//            assertTrue( dim.getHeight() >= 0, "Expecting a valid height, but had an image with height: " + dim.getHeight() );
//            assertTrue( dim.getWidth() >= 0, "Expecting a valid width, but had an image with width: " + dim.getWidth() );
//        }
    }

    private void renderSlides() {
        Dimension pgSize = powerPoint.getPageSize();

        for (Slide<?,?> s : powerPoint.getSlides()) {
            BufferedImage img = new BufferedImage(pgSize.width, pgSize.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = img.createGraphics();

            // default rendering options
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHint(Drawable.BUFFERED_IMAGE, new WeakReference<>(img));

            try {
                // draw stuff
            	s.draw(graphics);
            } catch (ArrayIndexOutOfBoundsException e) {
                // We saw exceptions with JDK 8 on Windows in the Jenkins CI which
                // seem to only be triggered by some font (maybe Calibri?!)
                // We cannot avoid this, so let's try to not make the tests fail in this case
                if (!"-1".equals(e.getMessage()) /*||
                    !ExceptionUtils.readStackTrace(e).contains("ExtendedTextSourceLabel.getJustificationInfos")*/) {
                    throw e;
                }
            }

            graphics.dispose();
            img.flush();
        }
    }
    
}

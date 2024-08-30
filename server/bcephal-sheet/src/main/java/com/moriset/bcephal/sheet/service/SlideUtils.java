package com.moriset.bcephal.sheet.service;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.util.Units;
import org.apache.poi.xddf.usermodel.chart.AxisCrossBetween;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFChart;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

public class SlideUtils {
	
	
	public void createPresentationFromLayout () throws Exception {
		FileInputStream in = new FileInputStream("D:\\layout.pptx");
		XMLSlideShow layoutPpt = new XMLSlideShow(in);
		XSLFSlide layoutSlide = layoutPpt.getSlides().getFirst();
		
		
		XMLSlideShow ppt = new XMLSlideShow();
		//XMLSlideShow ppt = new XMLSlideShow(new FileInputStream("D:\\powerpoint.pptx"));
		XSLFSlide slide = ppt.createSlide(layoutSlide.getSlideLayout());
		slide.importContent(layoutSlide);
		
		createChart(ppt, slide);
		
		layoutPpt.close();
		
		FileOutputStream out = new FileOutputStream("D:\\powerpoint.pptx");
		ppt.write(out);
		out.close();
		
	}
	
	
	public void createChart(XMLSlideShow slideShow, XSLFSlide slide) throws Exception {
		// create a new empty slide
		//XSLFSlide slide = slideShow.createSlide();

		// create chart
		XSLFChart chart = slideShow.createChart();
		chart.setTitleText("Test Chart");

		// set axis
		XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
		XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
		leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
		leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);

		// define chart data for bar chart
		XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);

		// add chart categories (x-axis data)
		String[] categories = new String[] { "Category 1", "Category 2", "Category 3" };
		String categoryDataRange = chart
				.formatRange(new org.apache.poi.ss.util.CellRangeAddress(1, categories.length, 0, 0));
		XDDFCategoryDataSource categoryData = XDDFDataSourcesFactory.fromArray(categories, categoryDataRange, 0);

		// add chart values (y-axis data)
		Double[] values = new Double[] { 10.0, 20.0, 15.0 };
		String valuesDataRange = chart.formatRange(new org.apache.poi.ss.util.CellRangeAddress(1, values.length, 1, 1));
		XDDFNumericalDataSource<Double> valueData = XDDFDataSourcesFactory.fromArray(values, valuesDataRange, 1);
		XDDFBarChartData bar = (XDDFBarChartData) data;
		bar.setBarDirection(BarDirection.BAR);

		// add series
		XDDFChartData.Series series = data.addSeries(categoryData, valueData);
		series.setTitle("Series 1", chart.setSheetTitle("Series 1", 0));

		chart.getOrAddLegend().addEntry();
		chart.getOrAddLegend().getEntries().getLast().setTextBody(new XDDFTextBody(chart));
		chart.getOrAddLegend().getEntries().getLast().getTextBody().setText("S");
		
		// plot chart
		chart.plot(data);

		// set chart dimensions !!Units are EMU (English Metric Units)!!
		Rectangle chartDimensions = new Rectangle(100 * Units.EMU_PER_POINT, 50 * Units.EMU_PER_POINT,
				400 * Units.EMU_PER_POINT, 400 * Units.EMU_PER_POINT);
		// add chart to slide
		slide.addChart(chart, chartDimensions);

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public void createPresentation () throws Exception {
		XMLSlideShow ppt = new XMLSlideShow();
		//ppt.createSlide();
		
		for(XSLFSlideMaster master : ppt.getSlideMasters()){
		    for(XSLFSlideLayout layout : master.getSlideLayouts()){
		        System.out.println(layout.getType());
		    }
		}
		
		XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
		
		XSLFSlideLayout layout 
		  = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);
		XSLFSlide slide = ppt.createSlide(layout);
		
		XSLFTextShape titleShape = slide.getPlaceholder(0);
//		XSLFTextShape contentShape = slide.getPlaceholder(1);
		
		for (XSLFShape s : slide.getShapes()) {
			if (s instanceof XSLFAutoShape) {
				XSLFAutoShape shape = (XSLFAutoShape)s;
				shape.clearText();
				XSLFTextRun text = shape.addNewTextParagraph().addNewTextRun();
				text.setText(shape.getShapeName());
				text.setFontColor(Color.green);
				text.setFontSize(24.);
		    }
		}
		
		titleShape.clearText();
		titleShape.addNewTextParagraph().addNewTextRun().setText("Joseph");
		
		
//		XSLFTextBox shape = slide.createTextBox();
//		XSLFTextParagraph p = shape.addNewTextParagraph();
//		XSLFTextRun r = p.addNewTextRun();
//		r.setText("Baeldung");
//		r.setFontColor(Color.green);
//		r.setFontSize(24.);
		
		ppt.close();
		
		FileOutputStream out = new FileOutputStream("D:\\powerpoint.pptx");
		ppt.write(out);
		out.close();
		
	}
	
}

/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinColumn;


/**
 * @author B-Cephal Team
 *
 *         16 juin 2016
 */
public class JoinExporter {

	protected Logger logger;
	protected SXSSFWorkbook sworkbook;
	protected Sheet ssheet;
	protected XSSFWorkbook workbook;
	protected XSSFSheet sheet;
	protected int colCount;
	protected List<Integer> datePositions;
	CellStyle style;
	DataFormat format;
	private List<Long> ids;
	private Join join;
	private boolean exportAllColumns;
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public JoinExporter() {
		logger = LoggerFactory.getLogger(getClass().getName());
		datePositions = new ArrayList<>(0);

	}

	public JoinExporter(Join join, List<Long> ids, boolean exportAllColumns) {
		this();
		sworkbook = new SXSSFWorkbook(-1);
		ssheet = sworkbook.createSheet("Data");
		style = sworkbook.createCellStyle();
		format = sworkbook.createDataFormat();
		this.ids = ids;
		this.join = join;
		this.exportAllColumns = exportAllColumns;
		writeHeaderss(join);
	}

	public void export(String path, Join join, List<Object[]> objects, boolean ignoreLastColumn) throws IOException {
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet("Data");
		style = workbook.createCellStyle();
		format = workbook.createDataFormat();
		writeHeaders(join);
		writeRows(objects, 1, ignoreLastColumn);
		writeFile(path);
	}

	protected void writeHeaders(Join join) {
		XSSFRow rowhead = sheet.createRow(0);
		colCount = 0;
		Font font = workbook.createFont();
		font.setColor(IndexedColors.BLACK.getIndex());
		font.setBold(true);
		style.setFont(font);
		style.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		List<JoinColumn> columns = join.getColumnListChangeHandler().getItems();
		Collections.sort(columns, new Comparator<JoinColumn>() {
			@Override
			public int compare(JoinColumn value1, JoinColumn value2) {
				return value1.getPosition() - value2.getPosition();
			}
		});
		for (JoinColumn column : columns) {
			if(ids != null && ids.contains(column.getId()) || exportAllColumns){
				XSSFCell cell = rowhead.createCell(colCount++);
				cell.setCellValue(column.getName());
				cell.setCellStyle(style);
				if (column.getType() == DimensionType.PERIOD) {
					datePositions.add(colCount);
				}
			}
		}
	}
	
	private Long getIdByPosition(int position) {
		List<JoinColumn> columns = join.getColumnListChangeHandler().getItems();
		Collections.sort(columns, new Comparator<JoinColumn>() {
			@Override
			public int compare(JoinColumn value1, JoinColumn value2) {
				return value1.getPosition() - value2.getPosition();
			}
		});
		for(JoinColumn column : columns) {
			if(column.getPosition() == position) {
				return column.getId();
			}
		}
		return (long)-1;
	}

	public void writeRows(List<Object[]> objects, int rowCount, boolean ignoreLastColumn) {
		// int i = 1;
		for (Object[] values : objects) {
			XSSFRow row = sheet.createRow(rowCount++);
			int j = 0;
			int position = 0;
			for (Object value : values) {
				if (ids != null && ids.contains(getIdByPosition(position))|| exportAllColumns) {
					XSSFCell cell = row.createCell(j++);
					if (value == null)
						cell.setCellValue("");
					else if (value instanceof String)
						cell.setCellValue((String) value);
					else if (value instanceof BigDecimal)
						cell.setCellValue(((BigDecimal) value).doubleValue());
					else if (value instanceof Double)
						cell.setCellValue((Double) value);
					else if (value instanceof Date) {
						try {
							Date date = SIMPLE_DATE_FORMAT.parse(value.toString());
							style.setDataFormat(format.getFormat("dd/MM/yyyy"));
							cell.setCellStyle(style);
							cell.setCellValue(date);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else if (value instanceof Long || value instanceof BigInteger) {
						if (datePositions.contains(position)) {
							try {
								Date date = SIMPLE_DATE_FORMAT.parse(value.toString());
								style.setDataFormat(format.getFormat("dd/MM/yyyy"));
								cell.setCellStyle(style);
								cell.setCellValue(date);
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					} else
						cell.setCellValue(value.toString());
					if (ignoreLastColumn) {
						if (position == (colCount - 1))
							break;
					} else {
						if (position == colCount)
							break;
					}
				}
				position++;
			}
		}
	}

	protected void writeHeaderss(Join join) {
		Row rowhead = ssheet.createRow(0);
		colCount = 0;
		CellStyle style = sworkbook.createCellStyle();
		Font font = sworkbook.createFont();
		font.setColor(IndexedColors.BLACK.getIndex());
		font.setBold(true);
		style.setFont(font);
		style.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		List<JoinColumn> columns = join.getColumnListChangeHandler().getItems();
		Collections.sort(columns, new Comparator<JoinColumn>() {
			@Override
			public int compare(JoinColumn value1, JoinColumn value2) {
				return value1.getPosition() - value2.getPosition();
			}
		});
		for (JoinColumn column : columns) {
			if (ids != null && ids.contains(column.getId())|| exportAllColumns) {
				Cell cell = rowhead.createCell(colCount++);
				cell.setCellValue(column.getName());
				cell.setCellStyle(style);
				if (column.getType() == DimensionType.PERIOD) {
					datePositions.add(colCount);
				}
			}
		}
	}

	public void writeRowss(List<Object[]> objects, boolean ignoreLastColumn) throws IOException {
		int rowCount = ssheet.getPhysicalNumberOfRows();
		for (Object[] values : objects) {
			Row row = ssheet.createRow(rowCount++);
			int j = 0;
			int position = 0;
			for (Object value : values) {
				if (ids != null && ids.contains(getIdByPosition(position))|| exportAllColumns) {
					Cell cell = row.createCell(j++);
					if (value == null)
						cell.setCellValue("");
					else if (value instanceof String)
						cell.setCellValue((String) value);
					else if (value instanceof BigDecimal)
						cell.setCellValue(((BigDecimal) value).doubleValue());
					else if (value instanceof Double)
						cell.setCellValue((Double) value);
					else if (value instanceof Date) {
						try {
							Date date = SIMPLE_DATE_FORMAT.parse(value.toString());
							style.setDataFormat(format.getFormat("dd/MM/yyyy"));
							cell.setCellStyle(style);
							cell.setCellValue(date);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else if (value instanceof Long || value instanceof BigInteger) {
						if (datePositions.contains(position)) {
							try {
								Date date = SIMPLE_DATE_FORMAT.parse(value.toString());
								DataFormat format = sworkbook.createDataFormat();
								style.setDataFormat(format.getFormat("dd/MM/yyyy"));
								cell.setCellStyle(style);
								cell.setCellValue(date);
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					} else
						cell.setCellValue(value.toString());
					if (ignoreLastColumn) {
						if (position == (colCount))
							break;
					} else {
						if (position == colCount)
							break;
					}
				}
				position++;
			}
		}
		((SXSSFSheet) ssheet).flushRows();
	}

	public void writeFile(String path) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(path);
		workbook.write(fileOut);
		fileOut.close();
	}

	public void writeFiles(String path) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(path);
		sworkbook.write(fileOut);
		fileOut.close();
		sworkbook.close();
	}

}

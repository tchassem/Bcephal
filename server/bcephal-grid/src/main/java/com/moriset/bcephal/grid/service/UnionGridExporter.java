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
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.UnionGrid;
import com.moriset.bcephal.grid.domain.UnionGridColumn;


/**
 * @author B-Cephal Team
 *
 *         16 juin 2016
 */
public class UnionGridExporter {

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
	private UnionGrid unionGrid;
	private boolean exportAllColumns;
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public UnionGridExporter() {
		logger = LoggerFactory.getLogger(getClass().getName());
		datePositions = new ArrayList<>(0);

	}

	public UnionGridExporter(UnionGrid unionGrid, List<Long> ids, boolean exportAllColumns) {
		this();
		sworkbook = new SXSSFWorkbook(-1);
		ssheet = sworkbook.createSheet("Data");
		style = sworkbook.createCellStyle();
		format = sworkbook.createDataFormat();
		this.ids = ids;
		this.unionGrid = unionGrid;
		this.exportAllColumns = exportAllColumns;
		writeHeaderss(unionGrid);
	}

	public void export(String path, UnionGrid unionGrid, List<Object[]> objects, boolean ignoreLastColumn) throws IOException {
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet("Data");
		style = workbook.createCellStyle();
		format = workbook.createDataFormat();
		writeHeaders(unionGrid);
		writeRows(objects, 1, ignoreLastColumn);
		writeFile(path);
	}

	protected void writeHeaders(UnionGrid unionGrid) {
		XSSFRow rowhead = sheet.createRow(0);
		colCount = 0;
		Font font = workbook.createFont();
		font.setColor(IndexedColors.BLACK.getIndex());
		font.setBold(true);
		style.setFont(font);
		style.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		List<UnionGridColumn> columns = unionGrid.getColumnListChangeHandler().getItems();
		Collections.sort(columns, new Comparator<UnionGridColumn>() {
			@Override
			public int compare(UnionGridColumn value1, UnionGridColumn value2) {
				return value1.getPosition() - value2.getPosition();
			}
		});
		for (UnionGridColumn column : columns) {
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
		List<UnionGridColumn> columns = unionGrid.getColumnListChangeHandler().getItems();
		Collections.sort(columns, new Comparator<UnionGridColumn>() {
			@Override
			public int compare(UnionGridColumn value1, UnionGridColumn value2) {
				return value1.getPosition() - value2.getPosition();
			}
		});
		for(UnionGridColumn column : columns) {
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

	protected void writeHeaderss(UnionGrid unionGrid) {
		Row rowhead = ssheet.createRow(0);
		colCount = 0;
		CellStyle style = sworkbook.createCellStyle();
		Font font = sworkbook.createFont();
		font.setColor(IndexedColors.BLACK.getIndex());
		font.setBold(true);
		style.setFont(font);
		style.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		List<UnionGridColumn> columns = unionGrid.getColumnListChangeHandler().getItems();
		Collections.sort(columns, new Comparator<UnionGridColumn>() {
			@Override
			public int compare(UnionGridColumn value1, UnionGridColumn value2) {
				return value1.getPosition() - value2.getPosition();
			}
		});
		for (UnionGridColumn column : columns) {
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

	public void writeRowss(List<GridItem> objects, boolean ignoreLastColumn) throws IOException {
		int rowCount = ssheet.getPhysicalNumberOfRows();
		for (GridItem item : objects) {
			Object[] values = item.datas;
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
/**
 * 
 */
package com.moriset.bcephal.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author B-Cephal Team
 *
 *         16 oct. 2014
 */
public class ExcelLoader {

	/**
	 * Excel file to read
	 */
	public String excelFile;
	File file;

	/**
	 * The workbook
	 */
	public Workbook workbook;

	public OPCPackage pkg;

	public boolean canOpenfile;

	/**
	 * Build a new instance of ExcelLoader with excelFile
	 */
	public ExcelLoader() {

	}

	/**
	 * Load the Workbook.
	 * 
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws BiffException
	 */
	public void loadWorkbook() throws IOException, InvalidFormatException {
		if (excelFile == null || excelFile.isEmpty()) {
			throw new IllegalArgumentException("Excel file path is empty!");
		}
		try {
			file = new File(excelFile);
			if (!file.exists()) {
				workbook = new XSSFWorkbook(pkg);
			} else {
				pkg = OPCPackage.open(file);
				workbook = new XSSFWorkbook(pkg);				
				//workbook = WorkbookFactory..create(pkg);
				canOpenfile = true;
			}
		} catch (Exception exce) {
			canOpenfile = false;
		}
	}

	/**
	 * Load the Workbook.
	 * 
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws BiffException
	 */
	public void loadWorkbookInWriteMode() throws IOException, InvalidFormatException {
		if (excelFile == null || excelFile.isEmpty()) {
			throw new IllegalArgumentException("Excel file path is empty!");
		}
		try {
			file = new File(excelFile);
			if (!file.exists()) {
				workbook = new XSSFWorkbook(pkg);
			} else {
				pkg = OPCPackage.open(file, PackageAccess.WRITE);
				workbook = new XSSFWorkbook(pkg);				
				//workbook = WorkbookFactory.create(pkg);
				canOpenfile = true;
			}
		} catch (Exception exce) {
			canOpenfile = false;
		}
	}

	/**
	 * Load the Workbook.
	 * 
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws BiffException
	 */
	public void loadWorkbookInReadMode() throws IOException, InvalidFormatException {
		if (excelFile == null || excelFile.isEmpty()) {
			throw new IllegalArgumentException("Excel file path is empty!");
		}
		try {
			file = new File(excelFile);
			if (!file.exists()) {
				workbook = new XSSFWorkbook(pkg);
			} else {
				pkg = OPCPackage.open(file, PackageAccess.READ);
				workbook = new XSSFWorkbook(pkg);				
				//workbook = WorkbookFactory.create(pkg);
				canOpenfile = true;
			}
		} catch (Exception exce) {
			canOpenfile = false;
		}
	}

	public void load() {
		try {
			// this.document = new POIFSFileSystem(new
			// FileInputStream(this.excelFile));
			this.workbook = new XSSFWorkbook(new FileInputStream(this.excelFile));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Sheet getSheet(int index) {
		if (workbook == null)
			return null;
		try {
			return workbook.getSheetAt(index);
		} catch (Exception e) {
		}
		return null;
	}

	public Sheet getSheet(String name) {
		if (workbook == null)
			return null;
		try {
			return workbook.getSheet(name);
		} catch (Exception e) {
		}
		return null;
	}

	public void closeWorkBook() throws IOException {
		try {
			if (workbook != null)
				workbook.close();
			else
				return;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void saveAndCloseWorkBook() throws IOException {
		try {
			if (workbook != null) {
				workbook.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// try{
		// if(workbook != null) workbook.close();
		// }catch(Exception e){
		// e.printStackTrace();
		// }

	}

	public void closeFile() {
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(excelFile);
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeValue(String sheetName, int row, int col, BigDecimal value) {
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet != null) {
			org.apache.poi.ss.usermodel.Row line = sheet.getRow(row - 1);
			if (line != null) {
				Cell cell = line.getCell(col - 1);
				cell.setCellValue(value != null ? value.doubleValue() : 0);
			}
		}
	}

	public String readValue(String sheetName, int col, int row) {
		if (workbook == null) {
			return null;
		}
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet != null) {
			org.apache.poi.ss.usermodel.Row line = sheet.getRow(row - 1);
			if (line != null) {
				Cell cell = line.getCell(col - 1);
				if (cell != null) {
					switch (cell.getCellType()) {
					case NUMERIC:
						double value = cell.getNumericCellValue();
						int invalue = ((Double) value).intValue();
						if ((value - invalue) == 0) {
							return String.valueOf(invalue);
						} else {
							return String.valueOf(value);
						}
					default:
						return cell.getStringCellValue();
					}
				}
			}
		}
		return null;
	}
	

	/**
	 * Get the value of a cell in an Ms Excel File Possible values : Formula,
	 * String, Numeric
	 */
	public Object getCellValue(Cell cell) {
		try {
			if (cell == null)
				return null;
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			CellValue cellValue = evaluator.evaluate(cell);
			if (cellValue == null) {
				return null;
			}
			switch (cellValue.getCellType()) {
				case BOOLEAN: return cellValue.getBooleanValue();
				case NUMERIC: {
					try {
						if (DateUtil.isCellDateFormatted(cell))
							return cell.getDateCellValue();
					} catch (Exception e) {
					}
					return cellValue.getNumberValue();
				}
				case STRING: return cellValue.getStringValue();
				case BLANK: return null;
				case _NONE: return null;
				case ERROR: return null;
				case FORMULA: break;
			}

			if (cell.getCellType() == CellType.ERROR) {
				return null;
			}
			if (cell.getCellType() == CellType.FORMULA) {
				return getCellValue(cell.getCachedFormulaResultType(), cell);
			}
			return getCellValue(cell.getCellType(), cell);
		} catch (Exception e) {
			System.out.println();
			System.out.println(excelFile);
			System.out.println(cell.getColumnIndex() + " : " + cell.getRowIndex());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * retrieve the basic type of a cell
	 */
	private Object getCellValue(CellType cellType, Cell cell) {
		switch (cellType) {
			case NUMERIC: {
				if (DateUtil.isCellDateFormatted(cell)) {
					return cell.getDateCellValue();
				}
				return cell.getNumericCellValue();
			}
			default: {
				try {
					return cell.getStringCellValue();
				} catch (Exception e) {
					return null;
				}
			}
		}
	}

}

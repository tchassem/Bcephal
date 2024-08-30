
package com.moriset.bcephal.utils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author joel
 */
public class DataFormater {

	public enum Type {
		ROW, COL;
	}

	public static String WORKBOOK_END = "]";
	public static String SHEET_SEPARATOR = ";";
	public static String SHEET_DELIMITOR = "!";
	public static String RANGE_SEPARATOR = ":";
	public static String CELL_SEPARATOR = "$";

	/**
	 *
	 * @param dataRange
	 *            Example : "Sheet1!$A$1:$F$2;Sheet2!$A$4:$F$5;Sheet3!$A$7:$F$7"
	 * @return
	 */
	public static Map<String, List<String>> getSheetMap(String dataRange) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		if (dataRange != null && !dataRange.isEmpty()) {
			String[] sheetTab = dataRange.split("\\" + SHEET_SEPARATOR);
			if (sheetTab.length == 0) {
				sheetTab = new String[] { dataRange };
			}
			for (String sheetData : sheetTab) {
				String[] tab = sheetData.split("\\" + SHEET_DELIMITOR);
				String sheet = tab[0];
				if (!map.containsKey(sheet)) {
					map.put(sheet, new ArrayList<String>());
				}
				List<String> cells = map.get(sheet);
				try {
					// addDistinctCells(tab[1], cells);
					for (String cell : getCells(tab[1])) {
						if (!cells.contains(cell)) {
							cells.add(cell);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				map.put(sheet, cells);
			}
		}
		return map;
	}

	/**
	 *
	 * @param cellRange
	 *            Example : "$A$1:$F$2;
	 * @return
	 */
	public static List<String> getCells(String cellRange) {
		List<String> result = new ArrayList<String>();
		String[] rangeTab = cellRange.split("\\" + RANGE_SEPARATOR);
		if (rangeTab.length <= 1) {
			if (cellRange.startsWith(CELL_SEPARATOR)) {
				cellRange = cellRange.substring(1);
			}
			result.add(cellRange);
			return result;
		}
		String col1 = getColumn(rangeTab[0]);
		int row1 = getRow(rangeTab[0]);
		String col2 = getColumn(rangeTab[1]);
		int row2 = getRow(rangeTab[1]);
		for (int i = getColumnIndex(col1); i <= getColumnIndex(col2); i++) {
			for (int j = row1; j <= row2; j++) {
				String cell = getColumnName(i) + CELL_SEPARATOR + j;
				result.add(cell);
			}
		}
		return result;
	}

	/**
	 *
	 * @param cellRange
	 *            Example : "$A$1:$F$2;
	 * @return
	 */
	public static List<Point> getCellsCoord(String cellRange) {
		List<Point> result = new ArrayList<Point>();
		String[] rangeTab = cellRange.split("\\" + RANGE_SEPARATOR);
		if (rangeTab.length <= 1) {
			if (cellRange.startsWith(CELL_SEPARATOR)) {
				cellRange = cellRange.substring(1);
			}
			Point borneUnique = getCoord(cellRange.toCharArray());
			result.add(borneUnique);
			result.add(borneUnique);
			return result;
		}

		Point borneInf = getCoord(rangeTab[0].toCharArray());
		Point borneSup = getCoord(rangeTab[1].toCharArray());

		result.add(borneInf);
		result.add(borneSup);

		return result;
	}

	private static Point getCoord(char[] rangeTab) {
		String colValue = "";

		int j = 0;
		for (int i = rangeTab.length - 1; i >= 0; i--) {
			try {
				Integer.parseInt("" + rangeTab[j]);
				break;
			} catch (Exception exce) {
				colValue += rangeTab[j];
			}
			j++;
		}

		String rowValue = "";
		for (int p = j; p <= rangeTab.length - 1; p++) {
			rowValue += rangeTab[p];
		}
		Point cellule1 = new Point(getColumnIndex(colValue.toUpperCase()), Integer.parseInt(rowValue));

		return cellule1;
	}

	/**
	 * 
	 * @param cellRange
	 * @param cells
	 */
	public static void addDistinctCells(String cellRange, List<String> cells) {
		String[] rangeTab = cellRange.split("\\" + RANGE_SEPARATOR);
		if (rangeTab.length <= 1) {
			if (cellRange.startsWith(CELL_SEPARATOR)) {
				cellRange = cellRange.substring(1);
				cells.add(cellRange);
			}
			return;
		}
		String col1 = getColumn(rangeTab[0]);
		int row1 = getRow(rangeTab[0]);
		String col2 = getColumn(rangeTab[1]);
		int row2 = getRow(rangeTab[1]);
		for (int i = getColumnIndex(col1); i <= getColumnIndex(col2); i++) {
			for (int j = row1; j <= row2; j++) {
				String cell = getColumnName(i) + CELL_SEPARATOR + j;
				cells.add(cell);
			}
		}
		return;
	}

	/**
	 * Returns the list of the cells such that cell.row == row
	 * 
	 * @param cells
	 * @param row
	 * @return
	 */
	public static List<String> getCellsByRow(List<String> cells, int row) {
		List<String> result = new ArrayList<String>();
		for (String cell : cells) {
			if (getRow(cell) == row) {
				result.add(cell);
			}
		}
		return result;
	}

	/**
	 * Returns the list of the cells such that cell.col == col
	 * 
	 * @param cells
	 * @param col
	 * @return
	 */
	public static List<String> getCellsByColumn(List<String> cells, String col) {
		List<String> result = new ArrayList<String>();
		for (String cell : cells) {
			if (getColumn(cell).equals(col)) {
				result.add(cell);
			}
		}
		return result;
	}

	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public static String getCellName(int row, int col) {
		return getColumnName(col) + row;
	}

	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public static String getCellName(int row, int col, boolean dollarOnRow, boolean dollarOnCol) {
		return (dollarOnCol ? "$" : "") + getColumnName(col) + (dollarOnRow ? "$" : "") + row;
	}

	/**
	 * Calculate and returns the row of the given cell.
	 * <P>
	 * The cell looks like: "A$1"
	 * 
	 * @param cell
	 *            The cell.
	 * @return
	 */
	public static int getRow(String cell) {
		String[] tab = cell.split("\\" + CELL_SEPARATOR);
		return Integer.valueOf(tab[tab.length - 1]);
	}

	/**
	 * Calculate and returns the column of the given cell.
	 * <P>
	 * The cell looks like: "A$1"
	 * 
	 * @param cell
	 *            The cell.
	 * @return
	 */
	public static String getColumn(String cell) {
		if (cell.startsWith(CELL_SEPARATOR)) {
			cell = cell.substring(1);
		}
		String[] tab = cell.split("\\" + CELL_SEPARATOR);
		return tab[0];
	}

	/**
	 *
	 * @param cells
	 * @return
	 */
	public static int getMinRow(List<String> cells) {
		sortCellsByRow(cells);
		return getRow(cells.get(0));
	}

	/**
	 *
	 * @param cells
	 * @return
	 */
	public static String getMinCol(List<String> cells) {
		sortCellsByColumn(cells);
		return getColumn(cells.get(0));
	}

	/**
	 * Sort the given cells by row
	 * 
	 * @param cells
	 */
	public static void sortCellsByRow(List<String> cells) {
		Comparator<String> comparator = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int row1 = getRow((String) o1);
				String col1 = getColumn((String) o1);
				int row2 = getRow((String) o2);
				String col2 = getColumn((String) o2);
				if (row1 > row2) {
					return 1;
				} else if (row1 < row2) {
					return -1;
				} else {
					return col1.compareTo(col2);
				}
			}
		};
		Collections.sort(cells, comparator);
	}

	/**
	 * Sort the given cells by column
	 * 
	 * @param cells
	 */
	public static void sortCellsByColumn(List<String> cells) {
		Comparator<String> comparator = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int row1 = getRow((String) o1);
				String col1 = getColumn((String) o1);
				int row2 = getRow((String) o2);
				String col2 = getColumn((String) o2);
				int n = col1.compareTo(col2);
				if (n != 0) {
					return n;
				} else {
					if (row1 > row2) {
						return 1;
					} else {
						return -1;
					}
				}
			}
		};
		Collections.sort(cells, comparator);
	}

	/**
	 * Column name
	 * 
	 * @param value
	 *            Column value
	 * @return
	 */
	public static String getColumnName(int value) {
		return getName(value);
	}

	protected static String getName(int value) {
		String[] tab = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
				"T", "U", "V", "W", "X", "Y", "Z" };
		if (value <= 26) {
			return tab[value > 0 ? value - 1 : value];
		}
		int r = value % 26;
		r = r == 0 ? tab.length : r;
		int d = (int) (value - r) / 26;
		String lastName = r > 0 ? tab[r - 1] : "";
		String name = getName(d);

		return name + lastName;
	}

	/**
	 * Column index
	 * 
	 * @param name
	 *            Column name
	 * @return
	 */
	public static int getColumnIndex(String name) {
		char[] chars = name.toUpperCase().toCharArray();
		int length = chars.length;
		if (length == 1) {
			return getPosition(chars[0]);
		}
		return getPosition(chars, length - 1);
	}

	protected static int getPosition(char[] chars, int index) {
		if (index == 0) {
			return getPosition(chars[0]);
		}
		int precPos = getPosition(chars, index - 1);
		int pos = getPosition(chars[index]);
		return (26 * precPos) + pos;
	}

	/**
	 * Position dans l'aphabet
	 * 
	 * @param c
	 * @return
	 */
	protected static int getPosition(char c) {
		char[] tab = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
				'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		int length = 26;
		for (int i = 1; i <= length; i++) {
			if (tab[i - 1] == c) {
				return i;
			}
		}
		return 0;
	}

	/**
	 *
	 * @param categoryRange
	 * @return
	 */
	public static Type getTypeByCategory(String categoryRange) {
		Type result = Type.ROW;
		Map<String, List<String>> map = getSheetMap(categoryRange);
		for (String sheet : map.keySet()) {
			List<String> categories = map.get(sheet);
			if (haveSameRow(categories)) {
				result = Type.COL;
			}
		}
		return result;
	}

	/**
	 *
	 * @param cells
	 * @return
	 */
	public static boolean haveSameRow(List<String> cells) {
		int row = getRow(cells.get(0));
		for (String cell : cells) {
			if (getRow(cell) != row) {
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 * @param cells
	 * @return
	 */
	public static boolean haveSameColumn(List<String> cells) {
		String col = getColumn(cells.get(0));
		for (String cell : cells) {
			if (!getColumn(cell).equals(col)) {
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 * @param map1
	 * @param map2
	 */
	protected static void mergeMap(Map<String, List<String>> map1, Map<String, List<String>> map2) {
		for (String sheet : map2.keySet()) {
			if (!map1.containsKey(sheet)) {
				map1.put(sheet, map2.get(sheet));
			} else {
				List<String> cells = map1.get(sheet);
				for (String cell : map2.get(sheet)) {
					if (!cells.contains(cell)) {
						cells.add(cell);
					}
				}
				map1.put(sheet, cells);
			}
		}
	}

	protected static boolean areInTheSameGroup(String cell1, String cell2, int minRow, int minCol, Type type) {
		if (type == Type.ROW) {
			return (getColumn(cell1).equals(getColumn(cell2)) && getRow(cell1) == getRow(cell2) - 1)
					|| (getColumnIndex(getColumn(cell1)) == getColumnIndex(getColumn(cell2)) - 1
							&& getRow(cell2) == minRow);
		}
		return (getColumnIndex(getColumn(cell1)) == getColumnIndex(getColumn(cell2)) - 1
				&& getRow(cell1) == getRow(cell2))
				|| (getColumnIndex(getColumn(cell2)) == minCol && getRow(cell1) == getRow(cell2) - 1);
	}

}

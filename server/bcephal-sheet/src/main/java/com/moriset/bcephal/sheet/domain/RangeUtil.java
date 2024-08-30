/**
 * 
 */
package com.moriset.bcephal.sheet.domain;

/**
 * @author Joseph Wambo
 *
 */
public class RangeUtil {

	public enum Type {
		ROW, COL
	}

	public static String WORKBOOK_END = "]";
	public static String SHEET_SEPARATOR = ";";
	public static String SHEET_DELIMITOR = "!";
	public static String RANGE_SEPARATOR = ":";
	public static String CELL_SEPARATOR = "$";
	public static String FORMUALA_SIGN = "=";
	public static String ERROR = "REFERENCE NOT FOUND";

	static String[] alphabet = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
			"R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	static String[] numeric = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

	public static String buildRefFormula(SpreadSheetCell cell, SpreadSheetCell targetCell, String refFormula) {
		String formula = refFormula;
		if (isFromula(formula)) {
			formula = formula.replace(FORMUALA_SIGN, "");
			int first = formula.indexOf(CELL_SEPARATOR);
			int last = formula.lastIndexOf(CELL_SEPARATOR);
			if (first == 0 && last > 0) // $A$1
			{
				return FORMUALA_SIGN + formula;
			}

			int fr = getRowIndex(formula);
			int fc = getColumnIndex(formula);
			int r = fr;
			int c = fc;
			if (first == 0 && last == 0) // $A1
			{
				r = targetCell.getRow() - cell.getRow() + fr + 1;
			}
			if (first > 0)// A$1
			{
				c = targetCell.getCol() - cell.getCol() + fc;
			}
			if (first < 0)// A1
			{
				r = targetCell.getRow() - cell.getRow() + fr + 1;
				c = targetCell.getCol() - cell.getCol() + fc;
			}
			String col = GetColumnName(c);
			formula = FORMUALA_SIGN + col + r;
		}
		return formula;
	}

	public static String GetCellName(String formula) {
		String name = null;
		if (isFromula(formula)) {
			name = formula.trim().substring(1).trim();
		}
		return name;
	}

	public static boolean isFromula(String formula) {
		return formula != null && formula.startsWith(FORMUALA_SIGN);
	}

	public static String getCellName(int row, int col) {
		return GetColumnName(col) + (row + 1);
	}

	public static String GetColumnName(int value) {
		return getName(value);
	}

	public static int getColumnIndex(String name) {
		String col = "";
		for (char c : name.replace(CELL_SEPARATOR, "").toUpperCase().toCharArray()) {
			if (!Character.isDigit(c)) {
				col += c;
			}
		}
		char[] chars = col.toCharArray();
		int length = chars.length;
		if (length == 1) {
			return getPosition(chars[0]);
		}
		return getPosition(chars, length - 1);
	}

	public static int getRowIndex(String name) {
		String row = "";
		for (char c : name.replace(CELL_SEPARATOR, "").toUpperCase().toCharArray()) {
			if (Character.isDigit(c)) {
				row += c;
			}
		}
		return Integer.parseInt(row) - 1;
	}

	public static String getName(int value) {
		String[] tab = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
				"T", "U", "V", "W", "X", "Y", "Z" };
		if (value <= 25) {
			return tab[value];
		}
		int r = value % 25;
		String lastName = tab[r - 1];
		int d = (int) (value - r) / 25;
		String name = getName(d - 1);

		return name + lastName;
	}

	protected static int getPosition(char[] chars, int index) {
		if (index == 0) {
			return getPosition(chars[0]);
		}
		int precPos = getPosition(chars, index - 1);
		int pos = getPosition(chars[index]);
		return (26 * precPos) + pos;
	}

	protected static int getPosition(char c) {
		char[] tab = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
				'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		int length = 26;
		for (int i = 0; i < length; i++) {
			if (tab[i] == c) {
				return i;
			}
		}
		return 0;
	}

}

package com.moriset.bcephal.utils;

public class TagFormulaUtil {
	public enum Type {
		ROWREF, COLREF, CELLREF, CELL
	}

	public static String CELL_REF_SEPARATOR = "$";

	public static String ERROR_CELL = "REF_NOT_FOUND";

	public static String SIGN_EQUAL = "=";

	private static String MIN_CHAR = "A";

	private static String MAX_CHAR = "Z";

	private static String MIN_CHAR_NUM = "0";

	private static String MAX_CHAR_NUM = "9";

	/// <summary>
	/// la forme generale d'une formule est de la forme:
	/// - =A1
	/// - =$A1
	/// - =A$1
	/// - =$A$1
	/// </summary>
	/// <param name="formula"></param>
	/// <returns>true si c'est une formule et false sinon</returns>
	public static boolean isFormula(String formula) {
		return formula != null && formula.startsWith(SIGN_EQUAL);
	}

	public static String getFormulaWithoutEqualSign(String formula) {
		return formula.startsWith(SIGN_EQUAL) ? formula.substring(1) : formula;
	}

	public static boolean isSyntaxeFormulaCorrectly(String form) {
		String formula = getFormulaWithoutEqualSign(form);
		int length = formula.length();
		if (length < 2)
			return false;
		String first = formula.substring(0, 1);
		if (first.compareTo(CELL_REF_SEPARATOR) != 0
				&& (first.compareTo(MIN_CHAR) < 0 || first.compareTo(MAX_CHAR) > 0))
			return false;

		String last = formula.substring(length - 1, 1);
		if (last.compareTo(MIN_CHAR_NUM) < 0 || last.compareTo(MAX_CHAR_NUM) > 0)
			return false;

		if (first.compareTo(CELL_REF_SEPARATOR) == 0)
			formula = formula.substring(1, formula.length() - 1);
		if (formula.length() < 2)
			return false;

		for (int i = 0; i < formula.length(); i++) {
			String car = formula.substring(i, 1);
			if (car.compareTo(MIN_CHAR) >= 0 && car.compareTo(MAX_CHAR) <= 0)
				formula = formula.substring(1, formula.length() - 1);
			else
				break;
		}

		if (formula.length() < 1)
			return false;
		first = formula.substring(0, 1);
		if (first.compareTo(CELL_REF_SEPARATOR) == 0)
			formula = formula.substring(1, formula.length() - 1);
		if (formula.length() < 1)
			return false;
		for (int i = 0; i < formula.length(); i++) {
			String car = formula.substring(i, 1);
			if (car.compareTo(MIN_CHAR_NUM) >= 0 && car.compareTo(MAX_CHAR_NUM) <= 0)
				formula = formula.substring(1, formula.length() - 1);
			else
				return false;
		}
		return true;
	}

	/**
	 * les different type de format sont : - CELL : A1 - CELLREF : $A$1 - COLREF
	 * : $A1 - ROWREF : A$1
	 * 
	 * @param formula
	 * @return le type de la cellule suivant un format.
	 */
	public static Type gettType(String formula) {
		int firstOccurence = formula.indexOf(CELL_REF_SEPARATOR);
		int lastOccurence = formula.lastIndexOf(CELL_REF_SEPARATOR);
		if (firstOccurence < 0 && lastOccurence < 0) {
			return Type.CELLREF;
		} else if (firstOccurence == 0 && lastOccurence > 0) {
			return Type.CELL;
		}
		if (firstOccurence == 0 && lastOccurence == 0) {
			return Type.COLREF;
		}
		if (firstOccurence > 0 && lastOccurence > 0) {
			return Type.ROWREF;
		}
		return Type.CELL;
	}

	/**
	 * 
	 * @param formulRef
	 * @param rowRef
	 * @param colRef
	 * @param row
	 * @param col
	 * @return
	 */
	public static String getFormula(String formulRef, int rowRef, int colRef, int row, int col) {

		formulRef = !formulRef.startsWith(SIGN_EQUAL) ? formulRef : getFormulaWithoutEqualSign(formulRef);
		Type type = gettType(formulRef);
		java.awt.Point formul = getCoordonne(formulRef);
		if (formul == null)
			return formulRef;
		if (type.equals(Type.CELL)) {
			return SIGN_EQUAL + formulRef;
		} else if (type.equals(Type.CELLREF)) {
			java.awt.Point ecart = new java.awt.Point(col - colRef, row - rowRef);
			if ((ecart.x + formul.x) > 0 && (ecart.y + formul.y) > 0) {
				return SIGN_EQUAL + DataFormater.getColumnName((int) ecart.x + (int) formul.x)
						+ ((int) ecart.y + (int) formul.y);
			} else {
				return ERROR_CELL;
			}
		} else if (type.equals(Type.COLREF)) {
			java.awt.Point ecart = new java.awt.Point(0, row - rowRef);
			if ((ecart.x + formul.x) > 0 && (ecart.y + formul.y) > 0) {
				return SIGN_EQUAL + CELL_REF_SEPARATOR + DataFormater.getColumnName((int) ecart.x + (int) formul.x)
						+ ((int) ecart.y + (int) formul.y);
			} else {
				return ERROR_CELL;
			}
		} else if (type.equals(Type.ROWREF)) {
			java.awt.Point ecart = new java.awt.Point(col - colRef, 0);
			if ((ecart.x + formul.x) > 0 && (ecart.y + formul.y) > 0) {
				return SIGN_EQUAL + DataFormater.getColumnName((int) ecart.x + (int) formul.x) + CELL_REF_SEPARATOR
						+ ((int) ecart.y + (int) formul.y);
			} else {
				return ERROR_CELL;
			}
		}
		return null;

	}

	/**
	 * 
	 * @param formula
	 * @return
	 */
	public static java.awt.Point getCoordonne(String form) {
		java.awt.Point point = new java.awt.Point();
		String formula = getFormulaWithoutEqualSign(form).toUpperCase();
		Type type = gettType(formula);
		if (type.equals(Type.CELL) || type.equals(Type.ROWREF)) {
			point.x = DataFormater.getColumnIndex(DataFormater.getColumn(formula));
			point.y = DataFormater.getRow(formula);
		} else if (type.equals(Type.COLREF) || type.equals(Type.CELLREF)) {
			if (formula.startsWith(CELL_REF_SEPARATOR)) {
				formula = formula.substring(1);
			}

			String row = "";
			String col = "";
			for (int i = 0; i < formula.toCharArray().length; i++) {
				String car = "" + formula.toCharArray()[i];
				if ((car.toUpperCase().compareTo(MIN_CHAR) >= 0 && car.toUpperCase().compareTo(MAX_CHAR) <= 0))
					col += car;
				else
					row += car;
			}
			if (col.trim().isEmpty())
				return null;
			point.x = DataFormater.getColumnIndex(col.toUpperCase());
			point.y = Integer.parseInt(row);
		}
		return point;
	}

}

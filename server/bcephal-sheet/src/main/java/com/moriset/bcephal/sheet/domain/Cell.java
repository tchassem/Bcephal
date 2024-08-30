/**
 * 
 */
package com.moriset.bcephal.sheet.domain;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class Cell {

	private int row;

	private int column;

	private int sheet;

	private String name;
//    public string Name 
//    {
//        get
//        {
//            if (string.IsNullOrWhiteSpace(name))
//            {
//                name = RangeUtil.GetCellName(Row, Column);
//            }
//            return name;
//        }
//        set { name = value; }
//    }

	public Cell() {
	}

	public Cell(int row, int column, int sheet, String name) {
		this.row = row;
		this.column = column;
		this.sheet = sheet;
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}

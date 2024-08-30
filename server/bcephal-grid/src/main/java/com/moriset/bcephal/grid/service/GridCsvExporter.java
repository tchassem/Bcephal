/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;


public class GridCsvExporter {

	protected String separator = ";";
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	private PrintWriter writer;
	
	private Grille grid;
	
	private List<Long> ids;
	private boolean exportAllColumns;
	
	public GridCsvExporter(String path, Grille grid, boolean ignoreLastColumn, List<Long> ids, boolean exportAllColumns) throws IOException {
		 writer = new PrintWriter(path, StandardCharsets.UTF_8);
		 this.grid = grid;
		 this.ids = ids;
		 this.exportAllColumns = exportAllColumns;
		 writeHeaders(grid, writer,ignoreLastColumn);
	}

	public void export(List<Object[]> objects, boolean ignoreLastColumn) {	
		formatData(objects);
		writeRows(objects, writer,ignoreLastColumn);	
	}
	
	private void formatData(List<Object[]> items) {		
		for (GrilleColumn col : grid.getColumns()) {
			if (ids != null &&  ids.contains(col.getId()) || exportAllColumns) {
				if (col.isMeasure()) {
					int count = col.getFormat().getNbrOfDecimal();
					String pattern = "#0.";
					for (int i = 0; i < count; i++) {
						pattern += "0";
					}
					NumberFormat formatter = new DecimalFormat(pattern);
					for (Object[] row : items) {
						Object value = row[col.getPosition()];
						if (value != null && value instanceof Number) {
							row[col.getPosition()] = formatter.format(value);
						}
					}
				}
			}
		}
	}

	protected void writeHeaders(Grille grid, PrintWriter writer, boolean ignoreLastColumn) {
		List<GrilleColumn> columns = grid.getColumnListChangeHandler().getItems();
		Collections.sort(columns, new Comparator<GrilleColumn>() {
			@Override
			public int compare(GrilleColumn value1, GrilleColumn value2) {
				return value1.getPosition() - value2.getPosition();
			}
		});
		String line = "";
		String coma = "";
		for (GrilleColumn column : columns) {
			if (ids != null && ids.contains(column.getId()) || exportAllColumns) {
				line += coma + column.getName();
				coma = separator;
			}
		}
		writer.println(line);
	}

	private Long getIdByPosition(int position) {
		List<GrilleColumn> columns = grid.getColumnListChangeHandler().getItems();
		Collections.sort(columns, new Comparator<GrilleColumn>() {
			@Override
			public int compare(GrilleColumn value1, GrilleColumn value2) {
				return value1.getPosition() - value2.getPosition();
			}
		});
		for(GrilleColumn column : columns) {
			if(column.getPosition() == position) {
				return column.getId();
			}
		}
		return (long)-1;
	}
	
	protected void writeRows(List<Object[]> objects, PrintWriter writer, boolean ignoreLastColumn) {
		for (Object[] values : objects) {
			String line = "";
			String coma = "";
			int col = 0;
			int position = 0;
			for (Object obj : values) {
				if (ids != null &&  ids.contains(getIdByPosition(position)) || exportAllColumns) {
					String value = "";
					if (obj == null) {
						value = "";
					} else if (obj instanceof String) {
						value = (String) obj;
						value = "\"" + ((String)value) + "\"";
					} else if (obj instanceof BigDecimal) {
						value = "" + ((BigDecimal) obj).toPlainString();
					} else if (obj instanceof Double) {
						value = "" + (Double) obj;
					} else if (obj instanceof Date) {
						try {
							Date date = (Date) obj;
							value = SIMPLE_DATE_FORMAT.format(date);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						value = obj.toString();
					}

					line += coma + value;
					coma = separator;
				}
				if (col == values.length - 2) {
					break;
				}
				col++;
				position ++;
			}
			writer.println(line);
		}
	}

	public void close() {
		writer.close();
	}

}

/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.UnionGrid;
import com.moriset.bcephal.grid.domain.UnionGridColumn;


public class UnionGridJsonExporter {

	protected String separator = ";";
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	private ObjectMapper mapper;
	private PrintWriter writer;
	private List<Long> ids;
	private UnionGrid unionGrid;
	boolean exportAllColumns;
	
	public UnionGridJsonExporter(String path, UnionGrid unionGrid,boolean ignoreLastColumn, List<Long> ids, boolean exportAllColumns) throws Exception {
		mapper = new ObjectMapper();
		writer = new PrintWriter(path, StandardCharsets.UTF_8);
		this.ids = ids;
		this.unionGrid = unionGrid;
		this.exportAllColumns = exportAllColumns;
		writeHeaders(unionGrid, mapper, writer,ignoreLastColumn);
	}
	
	public void export( List<GridItem> objects,boolean ignoreLastColumn)  throws Exception{
		writeRows(objects, mapper, writer,ignoreLastColumn);
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
	

	protected void writeHeaders(UnionGrid unionGrid, ObjectMapper mapper, PrintWriter writer, boolean ignoreLastColumn) {
		List<UnionGridColumn> columns = unionGrid.getColumnListChangeHandler().getItems();
		Collections.sort(columns, new Comparator<UnionGridColumn>() {
			@Override
			public int compare(UnionGridColumn value1, UnionGridColumn value2) {
				return value1.getPosition() - value2.getPosition();
			}
		});
		@SuppressWarnings("unused")
		String line = "";
		String coma = "";
		@SuppressWarnings("unused")
		int offset = 0;
		for (UnionGridColumn column : columns) {
			if (ids != null && ids.contains(column.getId()) || exportAllColumns) {
				line += coma + column.getName();
				coma = separator;
			}
		}
	}

	protected void writeRows(List<GridItem> objects, ObjectMapper mapper, PrintWriter writer, boolean ignoreLastColumn) throws JsonProcessingException {		
		if(ignoreLastColumn) {
			List<Object[]> objects_ = new ArrayList<>();
			objects.forEach(line ->{ 
					Object[]  objec = new Object[line.datas.length];
					int offset = 0;
					int position = 0;
					while(position < line.datas.length - 1) {
						if (ids != null && ids.contains(getIdByPosition(position)) || exportAllColumns) {
							objec[offset] = line.datas[position];
							offset++;
						}
						
						position++;
					}
					objects_.add(objec);
				});
			writer.println(mapper.writeValueAsString(objects_));
		}else {
			writer.println(mapper.writeValueAsString(objects));
		}
	}

	public void close() {
		writer.close();
	}
}

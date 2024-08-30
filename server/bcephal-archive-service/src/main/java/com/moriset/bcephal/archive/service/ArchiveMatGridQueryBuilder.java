package com.moriset.bcephal.archive.service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import com.moriset.bcephal.archive.domain.Archive;
import com.moriset.bcephal.archive.domain.ArchiveConfigurationEnrichmentItem;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.service.ReportGridQueryBuilder;

public class ArchiveMatGridQueryBuilder extends ReportGridQueryBuilder {

	MaterializedGrid matGrid;
	Archive archive;
	String userName;
	RunModes mode;
	Map<String, Object> parameters;
	
	public ArchiveMatGridQueryBuilder(GrilleDataFilter filter, Archive archive, MaterializedGrid matGrid, String userName, RunModes mode) {
		super(filter);
		this.archive = archive;
		this.matGrid = matGrid;
		this.userName = userName;
		this.mode = mode;
	}
	
	
	@SuppressWarnings("unused")
	protected String buildInsertQuery(GrilleDataFilter filter, List<ArchiveConfigurationEnrichmentItem> enrichmentItems,
			Object[] row) {
		String sql = "INSERT INTO " + matGrid.getMaterializationTableName()
				+ " (";	
		String values = " VALUES(";
		String coma = "";
		parameters = new HashedMap<>();
		for (GrilleColumn column : filter.getGrid().getColumns()) {
			MaterializedGridColumn matgGridColumn = matGrid.getColumnByDimension(column.getType(), column.getDimensionId());
			if(matgGridColumn == null) continue;
			String col = matgGridColumn.getDbColumnName();			
			if (col == null) continue;
			Object value = getValue(column, row); 
			if (value == null) continue;
			String param = getParameterName(col);			
			sql += coma + col;
			values += coma + param; 
			coma = ", ";
			parameters.put(col, value);
		}	
		
		MaterializedGridColumn matgGridColumn = matGrid.getColumnByCategory(GrilleColumnCategory.LOAD_NBR);
		if(matgGridColumn != null) {
			String col = matgGridColumn.getDbColumnName();			
			String param = getParameterName(col);	
			Object value = "" + archive.getId(); 
			sql += coma + col;
			values += coma + param; 
			coma = ", ";
			parameters.put(col, value);
		}
		matgGridColumn = matGrid.getColumnByCategory(GrilleColumnCategory.LOAD_SOURCE_NAME);
		if(matgGridColumn != null) {
			String col = matgGridColumn.getDbColumnName();			
			String param = getParameterName(col);	
			Object value = "" + archive.getName(); 
			sql += coma + col;
			values += coma + param; 
			coma = ", ";
			parameters.put(col, value);
		}
		matgGridColumn = matGrid.getColumnByCategory(GrilleColumnCategory.LOAD_MODE);
		if(matgGridColumn != null) {
			String col = matgGridColumn.getDbColumnName();			
			String param = getParameterName(col);	
			Object value = mode.name(); 
			sql += coma + col;
			values += coma + param; 
			coma = ", ";
			parameters.put(col, value);
		}
		matgGridColumn = matGrid.getColumnByCategory(GrilleColumnCategory.LOAD_USER);
		if(matgGridColumn != null) {
			String col = matgGridColumn.getDbColumnName();			
			String param = getParameterName(col);	
			Object value = userName; 
			sql += coma + col;
			values += coma + param; 
			coma = ", ";
			parameters.put(col, value);
		}
		matgGridColumn = matGrid.getColumnByCategory(GrilleColumnCategory.LOAD_DATE);
		if(matgGridColumn != null) {
			String col = matgGridColumn.getDbColumnName();			
			String param = getParameterName(col);	
			Object value = archive.getModificationDate(); 
			sql += coma + col;
			values += coma + param; 
			coma = ", ";
			parameters.put(col, value);
		}
		matgGridColumn = matGrid.getColumnByCategory(GrilleColumnCategory.LOAD_TIME);
		if(matgGridColumn != null) {
			String col = matgGridColumn.getDbColumnName();			
			String param = getParameterName(col);	
			Object value = new SimpleDateFormat("HH:mm:ss").format(archive.getModificationDate()); 
			sql += coma + col;
			values += coma + param; 
			coma = ", ";
			parameters.put(col, value);
		}
		
		matgGridColumn = matGrid.getColumnByCategory(GrilleColumnCategory.OPERATION_CODE);
		if(matgGridColumn != null) {
			String col = matgGridColumn.getDbColumnName();			
			String param = getParameterName(col);	
			Object value = "" + archive.getId(); 
			sql += coma + col;
			values += coma + param; 
			coma = ", ";
			parameters.put(col, value);
		}
		
		for(ArchiveConfigurationEnrichmentItem item : enrichmentItems) {
//			String col = item.getUniverseColumnName();			
//			if (col == null) continue;
//			Object value = item.getValue();
//			if (value == null) continue;
//			String param = getParameterName(col);
//			if (!parameters.containsKey(col)) {
//				sql += coma + col;
//				values += coma + param; 
//				coma = ", ";
//			}			
//			parameters.put(col, value);
		}
		
		sql += ") ";
		values += ")";		
		sql += values;		
		return sql;
	}

	private Object getValue(GrilleColumn column, Object[] row) {
		int position = column.getPosition();
		if(position > -1 && row.length > 0 && row.length > position) {
			Object obj = row[position];
			if(obj != null) {
				return obj;
			}
		}
		return null;
	}
	
	private String getParameterName(String col) {		
		return ":" + col;
	}
	
}

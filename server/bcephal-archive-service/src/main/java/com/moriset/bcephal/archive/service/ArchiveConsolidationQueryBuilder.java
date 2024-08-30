package com.moriset.bcephal.archive.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.archive.domain.ArchiveConfigurationEnrichmentItem;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.service.ReportGridQueryBuilder;

public class ArchiveConsolidationQueryBuilder extends ReportGridQueryBuilder {

	Long archiveId;
	String UserName;
	Map<String, Object> parameters;

	public ArchiveConsolidationQueryBuilder(GrilleDataFilter filter, Long archiveId, String UserName) {
		super(filter);
		this.archiveId = archiveId;
		this.UserName = UserName;
	}

	protected String buildInsertQuery(GrilleDataFilter filter, List<ArchiveConfigurationEnrichmentItem> enrichmentItems,
			Object[] row) {
		String sql = "INSERT INTO " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME + " (";
		String values = " VALUES(";
		String coma = "";
		parameters = new HashedMap<>();
		for (GrilleColumn column : filter.getGrid().getColumns()) {
			String col = column.getUniverseTableColumnName();
			if (col == null)
				continue;
			Object value = getValue(column, row);
			if (value == null)
				continue;
			String param = getParameterName(col);
			sql += coma + col;
			values += coma + param;
			coma = ", ";
			parameters.put(col, value);
		}

		for (ArchiveConfigurationEnrichmentItem item : enrichmentItems) {
			String col = item.getUniverseColumnName();
			if (col == null)
				continue;
			Object value = item.getValue();
			if (value == null)
				continue;
			String param = getParameterName(col);
			if (!parameters.containsKey(col)) {
				sql += coma + col;
				values += coma + param;
				coma = ", ";
			}
			parameters.put(col, value);
		}

		sql += coma + UniverseParameters.SOURCE_TYPE + ", " + UniverseParameters.SOURCE_ID + ", "
				+ UniverseParameters.USERNAME + ") ";
		String param = getParameterName(UniverseParameters.SOURCE_TYPE);
		values += coma + param;
		coma = ", ";
		parameters.put(UniverseParameters.SOURCE_TYPE, UniverseSourceType.ARCHIVE.toString());

		param = getParameterName(UniverseParameters.SOURCE_ID);
		values += coma + param;
		parameters.put(UniverseParameters.SOURCE_ID, archiveId);

		param = getParameterName(UniverseParameters.USERNAME);
		values += coma + param + ")";
		parameters.put(UniverseParameters.USERNAME, StringUtils.hasText(UserName) ? UserName : "BCEPHAL");

		sql += values;
		return sql;
	}

	private Object getValue(GrilleColumn column, Object[] row) {
		int position = column.getPosition();
		if (position > -1 && row.length > 0 && row.length > position) {
			Object obj = row[position];
			if (obj != null) {
				return obj;
			}
		}
		return null;
	}

	private String getParameterName(String col) {
		return ":" + col;
	}
}

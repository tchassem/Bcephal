package com.moriset.bcephal.grid.service.form;

import java.util.List;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.form.FormModel;
import com.moriset.bcephal.grid.domain.form.FormModelField;
import com.moriset.bcephal.grid.service.InputGridQueryBuilder;

public class FormQueryBuilder extends InputGridQueryBuilder {

	protected FormModel formModel;
	protected boolean addId;
	protected List<Long> hidedObjectId;
	
	
	public FormQueryBuilder(GrilleDataFilter filter, FormModel formModel, List<Long> hidedObjectId) {
		super(filter);
		this.setSourceType(null);
		if (filter.getDataSourceType() != null && filter.getDataSourceType().isInputGrid()) {
			this.setSourceType(UniverseSourceType.INPUT_GRID);
			this.setSourceId(filter.getDataSourceId());
		}
		this.formModel = formModel;
		this.hidedObjectId =hidedObjectId;
	}

	@Override
	public String buildCountQuery() {
		String sql = "SELECT 1 ";
		if (StringUtils.hasText(sql)) {
			sql += buildFromPart();
			String wherePart = buildWherePart();
			if (StringUtils.hasText(wherePart)) {
				sql += wherePart;
			}
			String groupBy = buildGroupByPart();
			if (StringUtils.hasText(groupBy)) {
				sql += groupBy;
			}
		}
		return "SELECT COUNT(1) FROM (" + sql + ") AS A";
	}

	@Override
	public String buildQuery() {
		String sql = buildSelectPart();
		if (StringUtils.hasText(sql)) {
			sql += buildFromPart();
			String wherePart = buildWherePart();
			if (StringUtils.hasText(wherePart)) {
				sql += wherePart;
			}
			if(!addId) {
				String groupBy = buildGroupByPart();
				if (StringUtils.hasText(groupBy)) {
					sql += groupBy;
				}
			}
			String orderBy = buildOrderPart();
			if (!StringUtils.hasText(orderBy)) {
				orderBy = " ORDER BY id ";
			}
			if (StringUtils.hasText(orderBy)) {
				sql += orderBy;
			}
		}
		return sql;

	}

	@Override
	protected String buildSelectPart() {
		String selectPart = "SELECT ";
		String coma = "";
		for (GrilleColumn column : this.getFilter().getGrid().getColumns()) {
			String col = buildDbColName(column);
			if (col == null) {
				continue;
			}
			selectPart += coma + col;
			coma = ", ";
		}
		if(addId) {
			selectPart += coma + UniverseParameters.ID;
		}
		else {
			if(formModel.hasDetails()) {
				selectPart += coma + "min(id) as id";
			}
			else {
				selectPart += coma + UniverseParameters.ID;
			}
			//selectPart += coma + (formModel.hasDetails() ? "row_number() over () as id" : "id");
		}
		return selectPart;
	}
	
	@Override
	protected String buildDbColName(GrilleColumn column) {
		if(column.getDimensionId() == null || column.getDimensionId().equals(Long.valueOf(0))) {
			return "null as col" + column.getPosition();
		}
		else {
			return column.getUniverseTableColumnName();
		}
	}
	
	@Override
	protected String getColumnNameForDetails(GrilleColumn column) {
		if(column.getDimensionId() == null || column.getDimensionId().equals(Long.valueOf(0))) {
			return "null";
		}
		else {
			return column.getUniverseTableColumnName();
		}
	}
	
	protected String buildDbColName(FormModelField field) {
		if(field.getDimensionId() == null || field.getDimensionId().equals(Long.valueOf(0))) {
			return null;
		}
		else {
			field.setDataSourceId(this.getFilter().getGrid().getDataSourceId());
			field.setDataSourceType(this.getFilter().getGrid().getDataSourceType());
			return field.getUniverseTableColumnName();
		}
	}

	protected String buildGroupByPart() {
		if (formModel.hasDetails()) {
			String groupBy = "";
			String coma = "";
			for (FormModelField field : formModel.getFields()) {
				if (field.isMain()) {
					String col = buildDbColName(field);
					if (col == null) {
						continue;
					}
					groupBy += coma + col;
					coma = ", ";
				}
			}
			if (!groupBy.isEmpty()) {
				groupBy = " GROUP BY " + groupBy;
			}
			return groupBy;
		}
		return null;
	}

	
	@Override
	protected String buildWherePart() {
		String sql = super.buildWherePart();
		String where = " WHERE ";
		if(StringUtils.hasText(sql)) {
			where = " AND ";
		}
		
		if(this.hidedObjectId != null && this.hidedObjectId.size() > 0){
			sql += where + " ( ";
			String coma = "";
			for(Long id : this.hidedObjectId){
				sql += coma + UniverseParameters.ID + " != " + id.toString();
				coma = " AND ";
			}
			sql += " ) ";
			where = " AND ";
		}
		return sql;
	}

}

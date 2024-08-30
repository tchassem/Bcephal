package com.moriset.bcephal.grid.service.form;

import java.util.List;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.form.Form;
import com.moriset.bcephal.grid.domain.form.FormModel;
import com.moriset.bcephal.grid.domain.form.FormModelField;
import com.moriset.bcephal.grid.domain.form.FormModelFieldCategory;

public class FormRowQueryBuilder extends FormQueryBuilder {

	protected Object[] keys;
	
	public FormRowQueryBuilder(GrilleDataFilter filter, FormModel formModel, List<Long> hidedObjectId) {
		super(filter, formModel,hidedObjectId);
	}

	@Override
	public String buildQuery() {
		String sql = buildSelectPart();
		if(StringUtils.hasText(sql)){
			sql += buildFromPart();
			String wherePart = buildWherePart();
			if(StringUtils.hasText(wherePart)){
				sql += wherePart;
			}			
			String orderBy = buildOrderPart();
			if(StringUtils.hasText(orderBy)){
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
		selectPart += coma + "id";
		return selectPart;
	}
	
	@Override
	protected String buildWherePart() {			
		if(!formModel.hasDetails() && getFilter().getIds().size() > 0) {
			return " WHERE id = " + getFilter().getIds().get(0);
		}
		
		String sql = "";
		String base = super.buildWherePart();
		String where = " ";
		if (StringUtils.hasText(base)) {
			sql += where + base;
			where = " AND ";
		}
		else {
			where = " WHERE ";
		}
		for(FormModelField field : formModel.getFields()){
			if(field.getCategory() == FormModelFieldCategory.MAIN) {
				String col = buildDbColName(field);
				Object value = keys[field.getPosition()];
				if(value != null) {
					if(field.getDimensionType() == DimensionType.ATTRIBUTE) {
						sql += where + col + " = '" + value + "'";
					}
					else if(field.getDimensionType() == DimensionType.MEASURE) {
						sql += where + col + " = " + value;
					}
					else if(field.getDimensionType() == DimensionType.PERIOD) {
						String date = Form.getDateAsString(value);			
						sql += where + col + " = " + date;
					}
				}
				else {
					sql += where + col + " IS NULL";
				}	
				where = " AND ";
			}			
		}	
		return sql;
	}
	
	
}

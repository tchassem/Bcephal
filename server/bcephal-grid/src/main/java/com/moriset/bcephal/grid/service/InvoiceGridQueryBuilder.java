/**
 * 
 */
package com.moriset.bcephal.grid.service;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleRowType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class InvoiceGridQueryBuilder extends ReportGridQueryBuilder {

	
	public InvoiceGridQueryBuilder(GrilleDataFilter filter) {
		super(filter);
		setSourceType(filter.getGrid().isCreditNoteRepo() ? UniverseSourceType.CREDIT_NOTE : UniverseSourceType.INVOICE);
		setSourceId(null);
	}
		
	@Override
	protected String buildWhereSourcePart() {
		String sql = "";
		if (StringUtils.hasText(getFilter().getReportType())) {
			if(UniverseSourceType.INVOICE.name().equalsIgnoreCase(getFilter().getReportType())) {
				sql = UniverseParameters.SOURCE_TYPE + " = '" + UniverseSourceType.INVOICE.name() + "'";
			}
			else if(UniverseSourceType.CREDIT_NOTE.name().equalsIgnoreCase(getFilter().getReportType()) 
					|| "CREDIT NOTE".equalsIgnoreCase(getFilter().getReportType())) {
				sql = UniverseParameters.SOURCE_TYPE + " = '" + UniverseSourceType.CREDIT_NOTE.name() + "'";
			}
		}
		else {
			sql = "(" + UniverseParameters.SOURCE_TYPE + " = '" + UniverseSourceType.INVOICE.name() + "' OR "
					+ UniverseParameters.SOURCE_TYPE + " = '" + UniverseSourceType.CREDIT_NOTE.name() + "')";
		}
		return sql;
	}
	
	@Override
	protected String buildWhereStatusPart() {
		String sql = "";
		if(getFilter().getInvoiceStatusAttributeId()!= null 
				&& getFilter().getRowType() != null
				&& getFilter().getRowType() != GrilleRowType.ALL) {				
			String statusCol = new Attribute(getFilter().getInvoiceStatusAttributeId()).getUniverseTableColumnName();
			String value = getFilter().getRowType() == GrilleRowType.VALIDATED ? getFilter().getInvoiceStatusValidatedValue() : getFilter().getInvoiceStatusDraftValue();
			if (StringUtils.hasText(value)) {
				sql = "UPPER(" + statusCol + ") = '" + value.toUpperCase() + "'";
				this.attributes.put(getFilter().getInvoiceStatusAttributeId(), new Attribute(getFilter().getInvoiceStatusAttributeId()));
			}
		}
		
		if(getFilter().getInvoiceMailStatusAttributeId()!= null && StringUtils.hasText(getFilter().getSubjectType())) {
			String statusCol = new Attribute(getFilter().getInvoiceMailStatusAttributeId()).getUniverseTableColumnName();
			String value = null;
			if(getFilter().getSubjectType().equalsIgnoreCase(getFilter().getInvoiceMailStatusSentValue())) {
				value = getFilter().getInvoiceMailStatusSentValue();
			}
			else if(getFilter().getSubjectType().equalsIgnoreCase(getFilter().getInvoiceMailStatusNotSentValue())) {
				value = getFilter().getInvoiceMailStatusNotSentValue();
			}
			else if(getFilter().getSubjectType().equalsIgnoreCase("Error")) {
				value = "Error";
			}
			if(StringUtils.hasText(value)) {
				if(StringUtils.hasText(sql)) {
					sql += " AND ";
				}
				sql += "UPPER(" + statusCol + ") = '" + value.toUpperCase() + "'";
				this.attributes.put(getFilter().getInvoiceMailStatusAttributeId(), new Attribute(getFilter().getInvoiceMailStatusAttributeId()));
			}
		}
		if(StringUtils.hasText(sql)) {
			sql = "(" + sql + ")";
		}
		return sql;
	}
	
	@Override
	protected boolean isReport() {
		return false;
	}

}

/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.grid.service.DimensionDataFilter;
import com.moriset.bcephal.utils.BcephalException;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GrilleDataFilter extends BrowserDataFilter {

	private Grille grid;
	private List<GrilleColumn> columns;
	
	private List<Long> ids;
	private List<Long> exportColumnIds;
	
	private Long recoAttributeId;
	
	private Long freezeAttributeId;
	
	private Long neutralizationAttributeId;
	
	private Long noteAttributeId;
	
	private GrilleRowType rowType;
	
	private boolean conterpart;
	
	private boolean credit;
	
	private boolean debit;
	
	private boolean exportAllColumns;
	
	private String data;

	
	private List<String> recoValues;	
	
	
	@JsonIgnore
	private Long billingStatusAttributeId;	
	@JsonIgnore
	private String billingStatusBilledValue;	
	@JsonIgnore
	private String billingStatusDraftValue;
	
	
	@JsonIgnore
	private Long invoiceStatusAttributeId;	
	@JsonIgnore
	private String invoiceStatusValidatedValue;	
	@JsonIgnore
	private String invoiceStatusDraftValue;
	
	
	@JsonIgnore
	private Long invoiceMailStatusAttributeId;	
	@JsonIgnore
	private String invoiceMailStatusSentValue;	
	@JsonIgnore
	private String invoiceMailStatusNotSentValue;
	
		
	@JsonIgnore
	private String creditValue;
	
	@JsonIgnore
	private String debitValue;
	
	@JsonIgnore
	private Attribute debitCreditAttribute;
	
	@JsonIgnore
	private UniverseFilter filter;
		
	public GrilleDataFilter() {
		exportAllColumns = true;
	}
	
	public GrilleDataFilter(DimensionDataFilter filter){
		this();
		this.grid = filter.getGrid();
		this.columns = filter.getColumns();
		this.ids = filter.getIds();
		this.exportColumnIds = filter.getExportColumnIds();
		this.recoAttributeId = filter.getRecoAttributeId();
		this.freezeAttributeId = filter.getFreezeAttributeId();		
		this.neutralizationAttributeId = filter.getNeutralizationAttributeId();
		this.noteAttributeId = filter.getNoteAttributeId();
		this.rowType = filter.getRowType();
		this.conterpart = filter.isConterpart();		
		this.credit = filter.isCredit();
		this.debit = filter.isDebit();
		this.exportAllColumns = filter.isExportAllColumns();
		this.setAllowRowCounting(filter.isAllowRowCounting());
		this.setShowAll(filter.isShowAll());
		this.setSelectAllRows(filter.isSelectAllRows());
		this.data = filter.getData();	
		this.setColumnFilters(filter.getColumnFilters());
	}
	
	
	public void loadFilterRecoDataForGrid()  {
		if(getGrid() != null) {
			if(getRecoAttributeId() != null) {
				GrilleColumn column = getGrid().getColumnById(getRecoAttributeId());
				if(column != null && column.getDimensionId() != null) {
					setRecoAttributeId(column.getDimensionId());
				}
				else {
					throw new BcephalException("Reco type column not found!");
				}
			}	
			if(getNoteAttributeId()!= null) {
				GrilleColumn column = getGrid().getColumnById(getNoteAttributeId());
				if(column != null && column.getDimensionId() != null) {
					setNoteAttributeId(column.getDimensionId());
				}
				else {
					throw new BcephalException("Note column not found!");
				}
			}
			if(getFreezeAttributeId() != null) {
				GrilleColumn column = getGrid().getColumnById(getFreezeAttributeId());
				if(column != null && column.getDimensionId() != null) {
					setFreezeAttributeId(column.getDimensionId());
				}
				else {
					throw new BcephalException("Freeze column not found!");
				}
			}
			if(getNeutralizationAttributeId() != null) {
				GrilleColumn column = getGrid().getColumnById(getNeutralizationAttributeId());
				if(column != null && column.getDimensionId() != null) {
					setNeutralizationAttributeId(column.getDimensionId());
				}
				else {
					throw new BcephalException("Neutralization column not found!");
				}
			}
			
			if(getRecoData() != null) {
				getRecoData().setRecoAttributeId(getRecoAttributeId());
				getRecoData().setNoteAttributeId(getNoteAttributeId());
				getRecoData().setFreezeAttributeId(getFreezeAttributeId());
				getRecoData().setNeutralizationAttributeId(getNeutralizationAttributeId());
				
				if(getRecoData().getPartialRecoAttributeId() != null) {
					GrilleColumn column = getGrid().getColumnById(getRecoData().getPartialRecoAttributeId());
					if(column != null && column.getDimensionId() != null) {
						getRecoData().setPartialRecoAttributeId(column.getDimensionId());
					}
					else {
						throw new BcephalException("Partial reco type column not found!");
					}
				}
				
				if(getRecoData().getAmountMeasureId() != null) {
					GrilleColumn column = getGrid().getColumnById(getRecoData().getAmountMeasureId());
					if(column != null && column.getDimensionId() != null) {
						getRecoData().setAmountMeasureId(column.getDimensionId());
					}
					else {
						throw new BcephalException("Amount column not found!");
					}
				}	
				if(getRecoData().getReconciliatedMeasureId() != null) {
					GrilleColumn column = getGrid().getColumnById(getRecoData().getReconciliatedMeasureId());
					if(column != null && column.getDimensionId() != null) {
						getRecoData().setReconciliatedMeasureId(column.getDimensionId());
					}
					else {
						throw new BcephalException("Reconciliated amount column not found!");
					}
				}	
				if(getRecoData().getRemainningMeasureId() != null) {
					GrilleColumn column = getGrid().getColumnById(getRecoData().getRemainningMeasureId());
					if(column != null && column.getDimensionId() != null) {
						getRecoData().setRemainningMeasureId(column.getDimensionId());
					}
					else {
						throw new BcephalException("Remainning amount column not found!");
					}
				}
				
				if(getRecoData().getDebitCreditAttributeId() != null) {
					GrilleColumn column = getGrid().getColumnById(getRecoData().getDebitCreditAttributeId());
					if(column != null && column.getDimensionId() != null) {
						getRecoData().setDebitCreditAttributeId(column.getDimensionId());
					}
					else {
						throw new BcephalException("D/C column not found!");
					}
				}
				
				
			}
		}	
		
	}
	
	
	
}

package com.moriset.bcephal.billing.service.batch;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.moriset.bcephal.billing.domain.BillingModel;
import com.moriset.bcephal.billing.domain.BillingModelDriverGroup;
import com.moriset.bcephal.billing.domain.BillingModelEnrichmentItem;
import com.moriset.bcephal.billing.domain.BillingModelPivot;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.Join;

public class BillingContext {
	
	public String PIVOT_VALUE_SEPARATOR = "-;:;-";
	
	public Join billingJoin;
	
	public Long clientIdId;
	public Integer clientIdPosition;
	public String clientIdColumnName;
	
	public Long clientNameId;
	public Integer clientNamePosition;
	
	public Long clientDepartmentNumberId;
	public Integer clientDepartmentNumberPosition;
	
	public Long clientDepartmentNameId;
	public Integer clientDepartmentNamePosition;
	
	public Long clientLanguageId;
	public Integer clientLanguagePosition;
	
	
	public Long clientAdressStreetId;
	public Integer clientAdressStreetPosition;
	
	public Long clientAdressPostalCodeId;
	public Integer clientAdressPostalCodePosition;
	
	public Long clientAdressCityId;
	public Integer clientAdressCityPosition;
	
	public Long clientAdressCountryId;
	public Integer clientAdressCountryPosition;
	
	public Long clientVatNumberId;
	public Integer clientVatNumberPosition;
	
	public Long clientLegalFormId;
	public Integer clientLegalFormPosition;
	
	public Long clientPhoneId;
	public Integer clientPhonePosition;
	
	public Long clientEmailId;
	public Integer clientEmailPosition;
	
	public Long clientEmailCcId;
	public Integer clientEmailCcPosition;
	
	public Long clientStatusId;
	public Integer clientStatusPosition;
	public String clientStatusACTIVEValue;
	
	public Long clientInternalNumberId;
	public Integer clientInternalNumberPosition;
	
	public Long clientDoingBusinessAsId;
	public Integer clientDoingBusinessAsPosition;
	
	public Long clientDepartmentInternalNumberId;
	public Integer clientDepartmentInternalNumberPosition;
	
	public Long clientContactLastnameId;
	public Integer clientContactLastNamePosition;
	
	public Long clientContactFirstnameId;
	public Integer clientContactFirstnamePosition;
	
	public Long clientContactTitleId;
	public Integer clientContactTitlePosition;
		
	
	public Long billingEventTypeId;
	public Integer billingEventTypePosition;
	public String billingEventTypeInvoiceValue;
	public String billingEventTypeCreditNoteValue;
	
	public Long billingEventStatusId;
	public String billingEventStatusDraftValue;
	public String billingEventStatusBilledValue;
	public String billingEventStatusFrozenValue;
	
	public Long billingEventDateId;
	public Integer billingEventDatePosition;
	
	public Long unitCostId;
	public Integer unitCostPosition;
	
	public Long billingAmountId;
	public Integer billingAmountPosition;
	
	public Long driverId;
	public Integer driverPosition;
	
	public Long driverNameId;
	public Integer driverNamePosition;
	
	public Long vatRateId;
	public Integer vatRatePosition;
	
	public Long descriptionId;
	public Integer descriptionPosition;
	
	
	public Long billingRunId;
	public Long billingRunTypeId;
	public Long billingRunDateId;
	
	public Long invoiceRefId;
	public Long subInvoiceRefId;
	public Integer subInvoiceRefPosition;
	
	public Long invoiceAmountWithoutVatId;
	public Long invoiceVatAmountId;
	public Long invoiceTotalAmountId;
	public Long invoiceValidationDateId;
	public Long invoiceDateId;
	public Long invoiceDueDateId;
	public Long invoiceDueDateCalculationId;
		
	public Long invoiceStatusId;
	public String invoiceStatusDraftValue;
	public String invoiceStatusValidatedValue;
	
	public Long invoiceMailStatusId;
	public String invoiceMailStatusSentValue;;
	public String invoiceMailStatusNotSendValue;
		
	public Long invoiceTypeId;
	public String invoiceValue;
	public String creditNoteValue;
	
	public Long invoiceCommunicationMessageId;
	public Long invoiceDescriptionId;
	public Integer invoiceDescriptionPosition;
	
	public Long invoiceNumberGeneratorId;
	public Long creditNoteNumberGeneratorId;
	
	
	
	
	
	public Map<BillingModelPivot, Integer> pivotColumnPositions = new HashMap<BillingModelPivot, Integer>(0);
	public Map<Long, Integer> groupingColumnPositions = new HashMap<Long, Integer>(0);
	public Map<String, Integer> parameterColumnPositions = new HashMap<String, Integer>(0);
	public Map<Long, BillingModelDriverGroup> driverGroupColumnPositions = new HashMap<Long, BillingModelDriverGroup>(0);
	
	
	
	
	public String getInsertInvoiceSQL(BillingModel billingModel) {
		String table = UniverseParameters.SCHEMA_NAME.concat(UniverseParameters.UNIVERSE_TABLE_NAME);
		String invoiceNbrCol = new Attribute(this.invoiceRefId, "").getUniverseTableColumnName();
		String clientNbrCol = new Attribute(this.clientIdId, "").getUniverseTableColumnName();		
		String amountWithoutVatCol = new Measure(this.invoiceAmountWithoutVatId, "").getUniverseTableColumnName();
		String vatAmountCol = new Measure(this.invoiceVatAmountId, "").getUniverseTableColumnName();
		String totalAmountCol = new Measure(this.invoiceTotalAmountId, "").getUniverseTableColumnName();
		String invoiceStatusCol = new Attribute(this.invoiceStatusId, "").getUniverseTableColumnName();
		String invoiceDateCol = new Period(invoiceDateId).getUniverseTableColumnName();
		String invoiceDueDateCol = new Period(invoiceDueDateId).getUniverseTableColumnName();	
		String invoiceCreditNoteCol = new Attribute(this.invoiceTypeId, "").getUniverseTableColumnName();
		String runNbrCol = this.billingRunId != null ? new Attribute(this.billingRunId, "").getUniverseTableColumnName() : null;
		String runDateCol = this.billingRunDateId != null ? new Period(this.billingRunDateId, "").getUniverseTableColumnName() : null;
		String runTypeCol = this.billingRunTypeId != null ? new Attribute(this.billingRunTypeId, "").getUniverseTableColumnName() : null;
		String invoiceMailStatusCol = this.invoiceMailStatusId != null ? new Attribute(this.invoiceMailStatusId, "").getUniverseTableColumnName() : null;
		String invoiceCommunicationMessageCol = invoiceCommunicationMessageId != null ? 
				new Attribute(this.invoiceCommunicationMessageId, "").getUniverseTableColumnName() : null;
		
		String invoiceDescriptionCol = invoiceDescriptionId != null ? 
				new Attribute(this.invoiceDescriptionId, "").getUniverseTableColumnName() : null;
					
		String sql = "INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}, {12}, {13}"
				+ (invoiceCommunicationMessageId != null ? ", {14}" : "")
				+ (invoiceDescriptionId != null ? ", {15}" : "")
				+ (billingRunId != null ? ", {16}" : "")
				+ (billingRunTypeId != null ? ", {17}" : "")
				+ (billingRunDateId != null ? ", {18}" : "")
				+ (invoiceMailStatusId != null ? ", {19}" : "");
				

		String cols = "";
		String values = "";
		String coma = ",";
		for(BillingModelEnrichmentItem item : billingModel.getEnrichmentItems()) {
			String col = item.getUniverseTableColumnName();
			if(col != null) {				
				cols += coma + col;
				values += coma + "?";				
			}
		}
		
		if(clientNameId != null) {
			String col = new Attribute(this.clientNameId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}
		if(clientDepartmentNumberId != null) {
			String col = new Attribute(this.clientDepartmentNumberId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}
		if(clientLanguageId != null) {
			String col = new Attribute(this.clientLanguageId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}
		
		if(clientAdressStreetId != null) {
			String col = new Attribute(this.clientAdressStreetId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}		
		if(clientAdressPostalCodeId != null) {
			String col = new Attribute(this.clientAdressPostalCodeId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}		
		if(clientAdressCityId != null) {
			String col = new Attribute(this.clientAdressCityId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}		
		if(clientAdressCountryId != null) {
			String col = new Attribute(this.clientAdressCountryId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}		
		if(clientVatNumberId != null) {
			String col = new Attribute(this.clientVatNumberId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}		
		if(clientLegalFormId != null) {
			String col = new Attribute(this.clientLegalFormId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}		
		if(clientPhoneId != null) {
			String col = new Attribute(this.clientPhoneId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}		
		if(clientEmailId != null) {
			String col = new Attribute(this.clientEmailId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}
		if(clientEmailCcId != null) {
			String col = new Attribute(this.clientEmailCcId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}
		if(clientInternalNumberId != null) {
			String col = new Attribute(this.clientInternalNumberId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}		
		if(clientDoingBusinessAsId != null) {
			String col = new Attribute(this.clientDoingBusinessAsId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}
		
		if(clientContactTitleId != null) {
			String col = new Attribute(this.clientContactTitleId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}
		if(clientContactFirstnameId != null) {
			String col = new Attribute(this.clientContactFirstnameId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}
		if(clientContactLastnameId != null) {
			String col = new Attribute(this.clientContactLastnameId, "").getUniverseTableColumnName();
			cols += coma + col;
			values += coma + "?";	
		}

				
		sql += cols + ")"
		+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
		+ (invoiceCommunicationMessageId != null ? ", ?" : "")
		+ (invoiceDescriptionId != null ? ", ?" : "")
		+ (billingRunId != null ? ", ?" : "")
		+ (billingRunTypeId != null ? ", ?" : "")
		+ (billingRunDateId != null ? ", ?" : "")
		+ (invoiceMailStatusId != null ? ", ?" : "");
		
		sql += values + ")";
				
		sql = MessageFormat.format(sql,
			table, 
			UniverseParameters.SOURCE_ID, 
			UniverseParameters.SOURCE_NAME,
			UniverseParameters.SOURCE_TYPE,
			UniverseParameters.ISREADY,
			invoiceNbrCol,
			clientNbrCol,
			amountWithoutVatCol,
			vatAmountCol,
			totalAmountCol,
			invoiceDateCol,
			invoiceDueDateCol,
			invoiceStatusCol,
			invoiceCreditNoteCol,
			invoiceCommunicationMessageCol,
			invoiceDescriptionCol,
			runNbrCol,
			runTypeCol,
			runDateCol,
			invoiceMailStatusCol			
			);
		return sql;
	}
	
	public String getUpdateInvoiceSQL() {
		String table = UniverseParameters.SCHEMA_NAME.concat(UniverseParameters.UNIVERSE_TABLE_NAME);
		String invoiceNbrCol = new Attribute(this.invoiceRefId, "").getUniverseTableColumnName();
		String clientNbrCol = new Attribute(this.clientIdId, "").getUniverseTableColumnName();		
		String amountWithoutVatCol = new Measure(this.invoiceAmountWithoutVatId, "").getUniverseTableColumnName();
		String vatAmountCol = new Measure(this.invoiceVatAmountId, "").getUniverseTableColumnName();
		String totalAmountCol = new Measure(this.invoiceTotalAmountId, "").getUniverseTableColumnName();
		String invoiceDueDateCol = new Period(invoiceDueDateId).getUniverseTableColumnName();	
		String runNbrCol = this.billingRunId != null ? new Attribute(this.billingRunId, "").getUniverseTableColumnName() : null;
		String invoiceCommunicationMessageCol = invoiceCommunicationMessageId != null ? 
				new Attribute(this.invoiceCommunicationMessageId, "").getUniverseTableColumnName() : null;
				
		String invoiceDescriptionCol = invoiceDescriptionId != null ? 
				new Attribute(this.invoiceDescriptionId, "").getUniverseTableColumnName() : null;
					
		String sql = "UPDATE {0} SET {1} = ?, {2} = ?, {3} = ?, {4} = ?, {5} = ?"
				+ (invoiceCommunicationMessageId != null ? ", {6} = ?" : "")
				+ (invoiceDescriptionId != null ? ", {7} = ?" : "");
		

		String cols = "";
		String coma = ",";	
		
		if(clientNameId != null) {
			String col = new Attribute(this.clientNameId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}
		if(clientDepartmentNumberId != null) {
			String col = new Attribute(this.clientDepartmentNumberId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}
		if(clientLanguageId != null) {
			String col = new Attribute(this.clientLanguageId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}
		
		if(clientAdressStreetId != null) {
			String col = new Attribute(this.clientAdressStreetId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}		
		if(clientAdressPostalCodeId != null) {
			String col = new Attribute(this.clientAdressPostalCodeId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}		
		if(clientAdressCityId != null) {
			String col = new Attribute(this.clientAdressCityId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}		
		if(clientAdressCountryId != null) {
			String col = new Attribute(this.clientAdressCountryId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}		
		if(clientVatNumberId != null) {
			String col = new Attribute(this.clientVatNumberId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}		
		if(clientLegalFormId != null) {
			String col = new Attribute(this.clientLegalFormId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}		
		if(clientPhoneId != null) {
			String col = new Attribute(this.clientPhoneId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";	
		}		
		if(clientEmailId != null) {
			String col = new Attribute(this.clientEmailId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}	
		if(clientEmailCcId != null) {
			String col = new Attribute(this.clientEmailCcId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}	
		if(clientInternalNumberId != null) {
			String col = new Attribute(this.clientInternalNumberId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}		
		if(clientDoingBusinessAsId != null) {
			String col = new Attribute(this.clientDoingBusinessAsId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}
		
		if(clientContactTitleId != null) {
			String col = new Attribute(this.clientContactTitleId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}
		if(clientContactFirstnameId != null) {
			String col = new Attribute(this.clientContactFirstnameId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}
		if(clientContactLastnameId != null) {
			String col = new Attribute(this.clientContactLastnameId, "").getUniverseTableColumnName();
			cols += coma + col + " = ?";
		}

		sql += cols;
		
		String where = " WHERE {8} = ? AND {9} = ? AND {10} = ? AND {11} = ? "		
			+ (billingRunId != null ? "AND {12} = ? " : "");
		
		sql += where;	
				
		sql = MessageFormat.format(sql,
			table, 			
			clientNbrCol,
			amountWithoutVatCol,
			vatAmountCol,
			totalAmountCol,
			invoiceDueDateCol,
			invoiceCommunicationMessageCol,
			invoiceDescriptionCol,			
			UniverseParameters.SOURCE_ID, 
			UniverseParameters.SOURCE_NAME,
			UniverseParameters.SOURCE_TYPE,	
			invoiceNbrCol,
			runNbrCol
			);
		return sql;
	}
	
	
	public String getSaveInvoiceSQL() {
		String table = UniverseParameters.SCHEMA_NAME.concat(UniverseParameters.UNIVERSE_TABLE_NAME);
		String statusCol = new Attribute(this.billingEventStatusId, "").getUniverseTableColumnName();
		String invoiceNbrCol = new Attribute(this.invoiceRefId, "").getUniverseTableColumnName();
		String runNbrCol = new Attribute(this.billingRunId, "").getUniverseTableColumnName();
		
		String sql = MessageFormat.format("UPDATE {0} SET {1} = ?, {2} = ?, {3} = ? ", 
				table, statusCol, invoiceNbrCol, runNbrCol);
		return sql;
	}
	
	public String getResetInvoiceSQL() {
		String table = UniverseParameters.SCHEMA_NAME.concat(UniverseParameters.UNIVERSE_TABLE_NAME);
		String statusCol = new Attribute(this.billingEventStatusId, "").getUniverseTableColumnName();
		String invoiceNbrCol = new Attribute(this.invoiceRefId, "").getUniverseTableColumnName();
		String runNbrCol = new Attribute(this.billingRunId, "").getUniverseTableColumnName();
		
		String sql = MessageFormat.format("UPDATE {0} SET {1} = ?, {2} = null, {3} = null WHERE {2} = ? AND {3} = ?", 
				table, statusCol, invoiceNbrCol, runNbrCol);
		return sql;
	}
	
	public String getDeleteInvoiceSQL() {
		String table = UniverseParameters.SCHEMA_NAME.concat(UniverseParameters.UNIVERSE_TABLE_NAME);				
		String sql = MessageFormat.format("DELETE FROM {0} WHERE {1} = ? AND {2} = ? AND {3} = ?", 
				table, 
				UniverseParameters.SOURCE_ID, 
				UniverseParameters.SOURCE_NAME,
				UniverseParameters.SOURCE_TYPE
				);
		return sql;
	}
	
	public String getValidateInvoiceSQL() {
		String table = UniverseParameters.SCHEMA_NAME.concat(UniverseParameters.UNIVERSE_TABLE_NAME);	
		String invoiceStatusCol = new Attribute(this.invoiceStatusId, "").getUniverseTableColumnName();
		String validationDateCol =  invoiceValidationDateId != null ? new Period(invoiceValidationDateId, "").getUniverseTableColumnName() : null;
		String invoiceDateCol =  invoiceDateId != null ? new Period(invoiceDateId, "").getUniverseTableColumnName() : null;
		String invoiceReferenceCol = new Attribute(this.invoiceRefId, "").getUniverseTableColumnName();
		
		String query = "UPDATE {0} SET {4} = ?, {1} = ?, {2} = ? ";		
		if(validationDateCol != null) {
			query += ", {6} = ? ";
		}
		if(invoiceDateCol != null) {
			query += ", {7} = ? ";
		}
		query += " WHERE {3} = ? AND {4} = ? AND {5} = ?";
		
		String sql = MessageFormat.format(query, 
				table, 
				invoiceStatusCol,
				invoiceReferenceCol,
				UniverseParameters.SOURCE_ID, 
				UniverseParameters.SOURCE_NAME,
				UniverseParameters.SOURCE_TYPE,
				validationDateCol,
				invoiceDateCol
				);
		return sql;
	}
	
	public String getValidateInvoiceEventsSQL() {
		String table = UniverseParameters.SCHEMA_NAME.concat(UniverseParameters.UNIVERSE_TABLE_NAME);
		String invoiceNbrCol = new Attribute(this.invoiceRefId, "").getUniverseTableColumnName();
		String runNbrCol = new Attribute(this.billingRunId, "").getUniverseTableColumnName();
		
		String sql = MessageFormat.format("UPDATE {0} SET {1} = ? WHERE {2} = ? AND {3} = ? ", 
				table, invoiceNbrCol, invoiceNbrCol, runNbrCol);
		return sql;
	}


	public String getResetInvoiceValidationSQL() {		
		String table = UniverseParameters.SCHEMA_NAME.concat(UniverseParameters.UNIVERSE_TABLE_NAME);	
		String invoiceStatusCol = new Attribute(this.invoiceStatusId, "").getUniverseTableColumnName();
		String validationDateCol =  invoiceValidationDateId != null ? new Period(invoiceValidationDateId, "").getUniverseTableColumnName() : null;
		
		String query = "UPDATE {0} SET {1} = ? ";		
		if(validationDateCol != null) {
			query += ", {5} = null ";
		}
		query += " WHERE {2} = ? AND {3} = ? AND {4} = ?";
		
		String sql = MessageFormat.format(query, 
				table, 
				invoiceStatusCol,
				UniverseParameters.SOURCE_ID, 
				UniverseParameters.SOURCE_NAME,
				UniverseParameters.SOURCE_TYPE,
				validationDateCol
				);
		return sql;
	}
	
	public String getSendingInvoiceSQL() {
		String table = UniverseParameters.SCHEMA_NAME.concat(UniverseParameters.UNIVERSE_TABLE_NAME);	
		String invoiceNbrCol = new Attribute(this.invoiceRefId, "").getUniverseTableColumnName();		
		String invoiceSendingStatusCol = new Attribute(this.invoiceMailStatusId, "").getUniverseTableColumnName();
		String sql = MessageFormat.format("UPDATE {0} SET {1} = ? WHERE {2} = ? AND {3} = ? AND {4} = ?", 
				table, 
				invoiceSendingStatusCol,
				UniverseParameters.SOURCE_NAME,
				UniverseParameters.SOURCE_TYPE,
				invoiceNbrCol
				);
		return sql;
	}
	
	
	
	
}

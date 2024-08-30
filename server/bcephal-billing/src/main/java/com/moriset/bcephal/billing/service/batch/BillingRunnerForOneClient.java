package com.moriset.bcephal.billing.service.batch;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.billing.domain.BillTemplate;
import com.moriset.bcephal.billing.domain.BillingDescription;
import com.moriset.bcephal.billing.domain.BillingModel;
import com.moriset.bcephal.billing.domain.BillingModelAppendicyType;
import com.moriset.bcephal.billing.domain.BillingModelDriverGroup;
import com.moriset.bcephal.billing.domain.BillingModelDriverGroupItem;
import com.moriset.bcephal.billing.domain.BillingModelGroupingItem;
import com.moriset.bcephal.billing.domain.BillingModelInvoiceGranularityLevel;
import com.moriset.bcephal.billing.domain.BillingModelParameter;
import com.moriset.bcephal.billing.domain.BillingModelPivot;
import com.moriset.bcephal.billing.domain.BillingModelTemplate;
import com.moriset.bcephal.billing.domain.CalculateBillingItem;
import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.billing.domain.InvoiceDetail;
import com.moriset.bcephal.billing.domain.InvoiceItem;
import com.moriset.bcephal.billing.domain.InvoiceItemBillingElements;
import com.moriset.bcephal.billing.domain.InvoiceItemInfo;
import com.moriset.bcephal.billing.domain.InvoiceStatus;
import com.moriset.bcephal.billing.domain.InvoiceType;
import com.moriset.bcephal.billing.repository.BillTemplateRepository;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.PeriodFilterItem;
import com.moriset.bcephal.domain.filters.PeriodGranularity;
import com.moriset.bcephal.domain.filters.PeriodOperator;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.DateUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class BillingRunnerForOneClient {

	String clientId;
	BillingContext context;
	BillingModel billingModel;
	TaskProgressListener listener;	
	Party billingCompany;
	Party client;
	EntityManager session;
	BillingRunnerUtils billingRunnerUtils;
	String repositoryView;
	BillTemplateRepository billTemplateRepository;
	
	HashMap<String, List<Long>> events = new HashMap<String, List<Long>>(0);
    HashMap<String, Invoice> invoices = new HashMap<String, Invoice>(0);
    HashMap<String, Invoice> subinvoices = new HashMap<String, Invoice>(0);
    
    String dueDateCalculationView;
	
	
	public void run(List<Object[]> events) throws Exception {		
		BillingModelInvoiceGranularityLevel consolidation = billingModel.getInvoiceGranularityLevel();
        if(consolidation == null) {
        	consolidation = BillingModelInvoiceGranularityLevel.NO_CONSOLIDATION;
        }
    	if(context.groupingColumnPositions.size() > 0) {
    		consolidation = BillingModelInvoiceGranularityLevel.CUSTOM;
    	}		
		
		int set = 0;
		int count = events.size();
		int page = count <= 1000 ? 50 
        		: count <= 10000 ? 100
        		: count <= 100000 ? 200
        		: count <= 1000000 ? 300
        		: 500;
		page = 1;
		for(Object[] row : events) {	
			run(row, consolidation);
			set++;
	    	if(listener != null) {
	    		if(set == page) {
	    			listener.nextSubInfoStep(set);
	    			set = 0;
	    		}
	        }			
		}
		if(set > 0 && listener != null) {
			listener.nextSubInfoStep(set);
		}
	}
	
	private void run(Object[] row, BillingModelInvoiceGranularityLevel consolidation) throws Exception {
		log.trace("Billing event : {}", row);
		boolean isAddSubInvoiceAnnexe = billingModel.isAddAppendicies() && billingModel.getAppendicyType() == BillingModelAppendicyType.SUB_INVOICE;
		
    	String merchantNbr = clientId;
    	String merchantName = "";
    	log.debug("Client ID : {}    Client name : {}", merchantNbr, merchantName);
    	if(context.clientDepartmentNumberPosition != null) {
    		merchantNbr = (String)row[context.clientDepartmentNumberPosition];
    	}
    	if(context.clientDepartmentNamePosition != null) {
    		merchantName = (String)row[context.clientDepartmentNamePosition];
    	}
    	log.debug("Client ID : {}    Client name : {}", merchantNbr, merchantName);
    	Date billingEventDate = context.billingEventDatePosition != null ? (Date)row[context.billingEventDatePosition] : null;			        	
    	if(billingEventDate == null) {
    		log.debug("Event with wrong billing event date");
    		return;
    	}
    				        	
    	String eventType = context.billingEventTypePosition != null ? (String)row[context.billingEventTypePosition] : null;
    	if(!StringUtils.hasText(eventType)) {
    		log.debug("Event without type.");
    		return;
    	}    
    	log.trace("Event type: {}", eventType);
    	boolean isInvoice = eventType.equals(context.billingEventTypeInvoiceValue);
    	boolean isCreditNote = eventType.equals(context.billingEventTypeCreditNoteValue);
    	if(!isInvoice && !isCreditNote) {
    		log.debug("Event with unknown type: {}", eventType);
    		return;
    	}
    	InvoiceType invoiceType = isCreditNote ? InvoiceType.CREDIT_NOTE : InvoiceType.INVOICE;
    	    	
    	String key = clientId;
    	Date invoiceDate = new Date();
    	Date invoiceDate2 = new Date();
    	if(billingModel.isSeparateInvoicePerPeriod() && (billingModel.getPeriodSide().isAll() || billingModel.getPeriodSide().isInterval())) {    		
    		DateUtil util = new DateUtil();
    		Date date = null;
    		Date date2 = null;
    		if(billingModel.getPeriodGranularity().isWeek()) {
    			date = util.getFirstDayOfWeek(billingEventDate);	
    			date2 = util.getLastDayOfWeek(billingEventDate);	
			}
			else if(billingModel.getPeriodGranularity().isMonth()) {
				date = util.getFirstDayOfMonth(billingEventDate);	
				date2 = util.getLastDayOfMonth(billingEventDate);	
			}
			else if(billingModel.getPeriodGranularity().isQuarter()) {
				date = util.getFirstDayOfQuarter(billingEventDate);		
				date2 = util.getLastDayOfQuarter(billingEventDate);	
			}
			else if(billingModel.getPeriodGranularity().isYear()) {
				date = util.getFirstDayOfYear(billingEventDate);	
				date2 = util.getLastDayOfYear(billingEventDate);	
			}
    		if(date != null) {
    			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    			key = clientId + "_" + formatter.format(date2);
    			invoiceDate = date;
    			invoiceDate2 = date2;
    		}
    	}
    	
    	if(isInvoice) {
    		key += "_Invoice";
    	}
    	else {
    		key += "_CreditNote";
    	}		        		        	
    	 
    	String pivotValues = "";
    	String sepa = "";
    	String pivotKey = "";			        	
    	for(BillingModelPivot pivot : billingModel.getPivots()) {
    		Integer position = context.pivotColumnPositions.get(pivot);		        		
			if(position != null) {
				String value = (String)row[position];
				if(StringUtils.hasText(value)) {
					pivotKey += value + "_";
					pivotValues += sepa + value;
					sepa = context.PIVOT_VALUE_SEPARATOR;
				}
			}
		}
    	
    	key = pivotKey + key;
    	
    	BigDecimal unitCost = context.unitCostPosition != null ? (BigDecimal)row[context.unitCostPosition] : null;
    	BigDecimal amountToBill = context.billingAmountPosition != null ? (BigDecimal)row[context.billingAmountPosition] : null;
    	BigDecimal quantity = context.driverPosition != null ? (BigDecimal)row[context.driverPosition] : null;		
    	String unit = context.driverNamePosition != null ? (String)row[context.driverNamePosition] : null;
    	BigDecimal vatRate = context.vatRatePosition != null ? (BigDecimal)row[context.vatRatePosition] : BigDecimal.ZERO;
    	if(vatRate == null) {
    		vatRate = BigDecimal.ZERO;
    	}
    	
    	String category = "";
    	String description = context.descriptionPosition >= 0 ? (String)row[context.descriptionPosition] : "";    		        	
    	
    	if(context.groupingColumnPositions.size() > 0) {
    		Object[] groupRow = getGroupingRow(row);			        		
    		String value = (String) groupRow[groupRow.length - 1];
    		description = StringUtils.hasText(value) ? value : "";
    		category = "";
    		String coma = "";
    		for(int i = 0; i < groupRow.length-1; i++){
    			String val = (String) groupRow[i];
    			category += coma.concat(StringUtils.hasText(val) ? val : "");
    			coma = "$||$";
    		}
    	}
    	
    	
    	if(!this.billingModel.isUseVat()) {
    		vatRate = BigDecimal.ZERO;
    	}
    	
    	boolean useUnitCostToComputeAmount = this.billingModel.isUseUnitCostToComputeAmount();			        	
    	boolean[] exclude = computeExcludeDriverAndUnitCost(row);
    	boolean includeDriver = this.billingModel.isIncludeUnitCost() && !exclude[0];
    	boolean includeUnitCost = this.billingModel.isIncludeUnitCost() && !exclude[1];
    	CalculateBillingItem element = null;
    	if(useUnitCostToComputeAmount) {
    		log.debug("Compute billing elements...");
//    		quantity = quantity == null ? BigDecimal.ONE : quantity;
//    		unitCost = unitCost != null ? unitCost : BigDecimal.ZERO;
//    		amountToBill = quantity.multiply(unitCost);
    		
    		element = getCalculateBillingItem(row);
    		if(element != null && element.getType() != null) {
    			log.debug("Billing elements : {}", element.getType());
    			if(element.getType().isBillingAmount()) {
    				quantity = quantity == null ? BigDecimal.ONE : quantity;
            		unitCost = unitCost != null ? unitCost : BigDecimal.ZERO;
    			}
    			else if(element.getType().isUnitCost()) {
    				quantity = quantity == null ? BigDecimal.ONE : quantity;
            		amountToBill = amountToBill != null ? amountToBill : BigDecimal.ZERO;
    			}
    			else if(element.getType().isDriver()) {
    				unitCost = unitCost != null ? unitCost : BigDecimal.ZERO;
    				amountToBill = amountToBill != null ? amountToBill : BigDecimal.ZERO;
    			}
    			else if(element.getType().isUnitCostEqualsBillingAmount()) {
    				quantity = BigDecimal.ONE;
    				amountToBill = amountToBill != null ? amountToBill : BigDecimal.ZERO;
    				unitCost = amountToBill;
    			}
    		}
    		else {
    			quantity = quantity == null ? BigDecimal.ONE : quantity;
        		unitCost = unitCost != null ? unitCost : BigDecimal.ZERO;
        		amountToBill = quantity.multiply(unitCost);
    		}    		
    	}
    	else {
    		if(!includeDriver) {
    			quantity = null;				        				 
    		}
    		if(!includeUnitCost) {
        		unitCost = null;				        				 
    		}
    		if(amountToBill == null) {
    			amountToBill = BigDecimal.ZERO;
    		}      		
    	}
    	String billTemplateCode = getBillTemplateCode(row);	
    	key += "_" + billTemplateCode;
    	
    	if(amountToBill.compareTo(BigDecimal.ZERO) != 0 || this.billingModel.isIncludeZeroAmountEvents()) {
        	List<Long> items = events.get(key);
        	if(items == null) {
        		items = new ArrayList<Long>();
        		events.put(key, items);
        	}
        	
        	String subKey = key + "_" + merchantNbr;				        	
        	Invoice invoice = invoices.get(key);	
        	Invoice subInvoice = subinvoices.get(subKey);	
        	
        	
        	
        	if(invoice == null) {
        		try {
        			invoice = new Invoice();
        			invoice.setType(invoiceType);
	        		if(isInvoice) {
	        			invoice = builInvoice(row, invoice, key, clientId, merchantNbr, invoiceDate, invoiceDate2, billingEventDate, billingCompany, pivotValues, false, null);
	        		}
	        		else {
	        			String invoiceRef = context.subInvoiceRefPosition >= 0 ? (String)row[context.subInvoiceRefPosition] : "";		      
	        			invoice = builInvoice(row, invoice, key, clientId, merchantNbr, invoiceDate, invoiceDate2, billingEventDate, billingCompany, pivotValues, false, invoiceRef);					        			
	        		}
	        		
	        		BillingDescription billingDescription = billingModel.getBillingDescription(invoice.getClientLanguage());
	        		invoice.setBillingDescription(billingDescription);
	        		invoice.setDescriptionDescription(billingDescription != null ? billingDescription.getDescription() : null);
	        		invoice.setBillTemplateCode(billTemplateCode);
	        		if(!StringUtils.hasText(invoice.getDescription())) {
	        			String invoiceDescription = context.invoiceDescriptionPosition != null ? (String)row[context.invoiceDescriptionPosition] : null;	        			
	        			invoice.setDescription(invoiceDescription);
	        			if(StringUtils.hasText(invoiceDescription)) {
	        				invoice.setBillingDescription(null);
	        				invoice.setDescriptionDescription(null);
	        			}
	        		}
	        		
		        	invoices.put(key, invoice);
		        	if(listener != null) {
		        		listener.getInfo().getCurrentSubTask().setStepCount(listener.getInfo().getCurrentSubTask().getStepCount() + 1);
			        }	
        		}
        		catch (Exception e) {
					log.error("Error while building invoice: ", e);
					return;
				}
        	}
        	
        	if(isAddSubInvoiceAnnexe && subInvoice == null) {
        		subInvoice = new Invoice();
        		subInvoice.setType(invoiceType);
        		subInvoice = builInvoice(row, subInvoice, key, merchantNbr, merchantName, invoiceDate, invoiceDate2, billingEventDate, billingCompany, pivotValues, true, null);
    			subinvoices.put(subKey, subInvoice);
    			invoice.getChildren().add((Invoice)subInvoice);
        	}
        	
        	Long eventOid = ((Number)row[row.length-1]).longValue();
        	items.add(eventOid);
        	       
        	
        	InvoiceItem invoiceItem = buildInvoiceItem(invoice, eventOid, billingEventDate, category, description, includeDriver, quantity, unit, includeUnitCost, unitCost, vatRate, amountToBill, consolidation, element);
    		updateInvoiceItemInfos(invoice, invoiceItem, row);
    		if(isAddSubInvoiceAnnexe && subInvoice != null) {
    			invoiceItem = buildInvoiceItem(subInvoice, eventOid, billingEventDate, category, description, includeDriver, quantity, unit, includeUnitCost, unitCost, vatRate, amountToBill, consolidation, element);				        			
    			updateInvoiceItemInfos(subInvoice, invoiceItem, row);
    		}		
        	
    	}    	    	
	}
		
	private String getBillTemplateCode(Object[] row) {
		String code = billingModel.getBillTemplateCode();
		Long eventOid = ((Number)row[row.length-1]).longValue();
		for(BillingModelTemplate template : billingModel.getBillingModelTemplates()) {
			if(template.getTemplateId() != null) {
				boolean matchs = billingRunnerUtils.templateMatchs(template, eventOid, repositoryView);
				if(matchs) {
					if(template.getCode() == null) {
						Optional<BillTemplate> billTemplate = billTemplateRepository.findById(template.getTemplateId());
						if(billTemplate.isPresent()) {
							template.setCode(billTemplate.get().getCode());
						}
					}					
					code = template.getCode();
					break;
				}
			}
		}
		if(!StringUtils.hasText(code)) {
			code = billingModel.getBillTemplateCode();
		}
		return code;
	}

	private Invoice builInvoice(Object[] row, Invoice invoice, String key, String clientNbr, String merchantNbr, Date invoiceDate, Date invoiceDate2, Date billingEventDate, Party company, String pivotValues, boolean subInvoice, String parentref) throws Exception {
		invoice.setVisibleInShortcut(true);
		invoice.setInvoiceDate(invoiceDate);
		invoice.setInvoiceDate2(invoiceDate2);				
		invoice.setClientNumber(clientNbr);		
    	invoice.setDepartmentNumber(merchantNbr);
    	invoice.setOrderReference(parentref);
    	
    	invoice.setDriverDecimalNumber(billingModel.getDriverDecimalNumber());
    	invoice.setUnitCostDecimalNumber(billingModel.getUnitCostDecimalNumber());
    	invoice.setBillingAmountDecimalNumber(billingModel.getBillingAmountDecimalNumber());
    	
    	invoice.setUseValidationDateAsInvoiceDate(billingModel.isUseValidationDateAsInvoiceDate());
    	invoice.setReprintPdfAtValidation(billingModel.isReprintPdfAtValidation());
    	invoice.setInvoiceValidationSequenceId(billingModel.getInvoiceValidationSequenceId());
    	invoice.setFileNameDescription(billingModel.getFileNameDescription());
    	
    	invoice.setUseVat(billingModel.isUseVat());
		
		if(billingModel.isDueDateCalculation()) {
			calculateDueDate(invoice);
		}
		else {
			invoice.setDueDate(new DateUtil().getPlusOneMonth(invoiceDate2 != null ? invoiceDate2 : invoiceDate));
		}
    	invoice.setStatus(InvoiceStatus.DRAFT);
    	invoice.setVatAmount(BigDecimal.ZERO);
    	invoice.setAmountWithoutVat(BigDecimal.ZERO);
    	    	
    	invoice.setClientName("");
    	invoice.setBillingDate(invoiceDate);
    	if(billingModel.isInvoiceDateCalculation() && billingModel.getInvoiceDateValue() != null) {
    		Date date = billingModel.getInvoiceDateValue().buildDynamicDate();
			invoice.setBillingDate(date);
		}
    	
    	//invoice.setBillTemplateCode(billingModel.getBillTemplateCode());
    	
    	invoice.setBillingCompanyNumber(company.number);
    	invoice.setBillingCompanyName(company.name);
    	invoice.setBillingCompanyVatNumber(company.vatNumber);
    	invoice.setBillingCompanyAdressStreet(company.street);
    	invoice.setBillingCompanyAdressPostalCode(company.postalcode);
    	invoice.setBillingCompanyAdressCity(company.city);
    	invoice.setBillingCompanyAdressCountry(company.country);
    	invoice.setBillingCompanyPhone(company.phone);
    	invoice.setBillingCompanyEmail(company.email);
    	invoice.setBillingCompanyLegalForm(company.legalForm);
    	    
//    	Party client = new Party();
//    	if(subInvoice) {
//    		log.debug("Build sub invoice client : {}     {}", clientNbr, merchantNbr);
//    		client.name = merchantNbr;
//    		client.number = merchantNbr;
//    		invoice.setDepartmentNumber(clientNbr);	
//    	}
//    	else {    		
//        	if(StringUtils.hasText(client.departmentNumber)) {
//        		invoice.setDepartmentNumber(client.departmentNumber);
//        	}
////        	invoice.setMerchantInternalNumber(client.departmentInternalNumber);
////        	invoice.setClientInternalNumber(client.internalNumber);        	
//    	}  
    	
    	log.debug(" ***  Client \nNbr: {} \nName: {}", client.number, client.name);
    	
    	invoice.setClientName(subInvoice ? merchantNbr : client.getName());
    	invoice.setDepartmentNumber(subInvoice ? clientNbr : client.departmentNumber);	
    	//invoice.setClientDoingBusinessAs(client.doingBusinessAs);
    	invoice.setClientVatNumber(client.vatNumber);
    	invoice.setClientAdressStreet(client.street);
    	invoice.setClientAdressPostalCode(client.postalcode);
    	invoice.setClientAdressCity(client.city);
    	invoice.setClientAdressCountry(client.country);
    	invoice.setClientPhone(client.phone);
    	invoice.setClientEmail(client.email);
    	invoice.setClientEmailCc(client.emailCc);
    	invoice.setClientLegalForm(client.legalForm);
    	invoice.setClientLanguage(client.language);
    	invoice.setClientContact(client.contactLastname);
    	invoice.setContactTitle(client.contactTitle);
    	invoice.setContactFirstname(client.contactFirstname);
    	invoice.setContactLastname(client.contactLastname);
    	    	
    	invoice.setName("".concat(invoice.getClientNumber() != null ? invoice.getClientNumber() : "")
    			.concat("_").concat(invoice.getClientName() != null ? invoice.getClientName() : "")
    			.concat("_").concat(key).concat("_").concat(new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())));
    	
    	log.debug(" ***  Invoice name: {}", invoice.getName());
    	
    	invoice.setAmountUnit(billingModel.getCurrency());    	
    	invoice.setUseUnitCost(this.billingModel.isUseUnitCostToComputeAmount());
    	invoice.setSubjectToVat(this.billingModel.isUseVat());    	
    	invoice.setPivotValues(pivotValues);    	
    	invoice.setOrderItems(billingModel.isOrderItems());
    	invoice.setOrderItemsAsc(billingModel.isOrderItemsAsc());
    	
		return invoice;
	}
	
	private InvoiceItem buildInvoiceItem(Invoice invoice, Long eventId, Date date, String category, String description, boolean includeDriver, BigDecimal quantity, String unit, boolean includeUnitCost,
			BigDecimal unitCost, BigDecimal vatRate, BigDecimal amountToBill, BillingModelInvoiceGranularityLevel consolidation, CalculateBillingItem element) {		
				
		InvoiceItem item = new InvoiceItem();		
		item.setIncludeQuantity(includeDriver);
		item.setQuantity(quantity);
		item.setUnit(unit);
		item.setCategory(category);
		item.setDescription(description);
		item.setPosition(invoice.getItemListChangeHandler().getItems().size() + 1);
		item.setIncludeUnitCost(includeUnitCost);
		item.setUnitCost(unitCost);
		item.setAmount(amountToBill);
		item.setCurrency(invoice.getAmountUnit());
		item.setVatRate(vatRate);		
		
		item.setUseUnitCost(this.billingModel.isUseUnitCostToComputeAmount());
		item.setSubjectToVat(this.billingModel.isUseVat());
		invoice.setAmountWithoutVat(invoice.getAmountWithoutVat().add(amountToBill));	
		
		item.getEventIds().add(eventId);
		item.setBillingElements(new InvoiceItemBillingElements(element));
		item.getBillingElements().incrementBillingEventCount();
		item.getBillingElements().getAmounts().add(item.getAmount());
		item.getBillingElements().getDrivers().add(item.getQuantity());
		item.getBillingElements().getUnitCosts().add(item.getUnitCost());
		if(element != null) {
			item.setDriverDecimalNumber(element.getDriverDecimalNumber());
			item.setUnitCostDecimalNumber(element.getUnitCostDecimalNumber());
			item.setBillingAmountDecimalNumber(element.getBillingAmountDecimalNumber());
		}
		
		if(consolidation.isNoConsolidation()) {
			invoice.getItemListChangeHandler().addNew(item);
		}
		else {
			InvoiceItem found = invoice.getItemByDescription(category, description, vatRate);
			if(found == null) {
				invoice.getItemListChangeHandler().addNew(item);
			}
			else {
				found.setAmount(found.getAmount().add(item.getAmount()));				
				if(this.billingModel.isIncludeUnitCost()) {
					found.setQuantity(found.getQuantity().add(item.getQuantity()));	
					if(element == null && this.billingModel.isUseUnitCostToComputeAmount()) {
						if(found.getAmount().compareTo(BigDecimal.ZERO) != 0) {
							BigDecimal cost = found.getAmount().divide(found.getQuantity(), new MathContext(14));
							found.setUnitCost(cost);
						}
					}
					else if(unitCost != null){
						found.setUnitCost(found.getUnitCost().add(item.getUnitCost()));	
					}			
				}
				found.getEventIds().addAll(item.getEventIds());
				found.getBillingElements().merge(item.getBillingElements());
				item = (InvoiceItem)found;
				
//				if(found.getUseUnitCost()) {
//					found.setQuantity(found.getQuantity().add(item.getQuantity()));					
//					if(found.getAmount().compareTo(BigDecimal.ZERO) != 0) {
//						BigDecimal cost = found.getAmount().divide(found.getQuantity(), new MathContext(14));
//						found.setUnitCost(cost);
//					}
//				}
			}
		}
		
		InvoiceDetail detail = new InvoiceDetail();
		detail.setEventId(eventId);
		detail.setDate(date);
		detail.setQuantity(quantity);
		detail.setUnit(unit);
		detail.setDescription(description);
		detail.setPosition(invoice.getDetails().size() + 1);
		detail.setUnitCost(unitCost);
		detail.setAmountWithoutVat(amountToBill);
		detail.setCurrency(invoice.getAmountUnit());
		detail.setVatRate(vatRate);
		invoice.getDetails().add(detail);
				
		return item;
	}
	
	private void updateInvoiceItemInfos(Invoice invoice, InvoiceItem item, Object[] row) throws BcephalException {
		try {
			Object[] values = getParameterRow(row);
			log.trace("Building parameters values...");			
			log.trace("ITEM: " + item);			
			int i = 0;
			for(BillingModelParameter parameter : this.billingModel.getParameters()) {					
				Object value = values[i++];
				log.trace("Parameter (Global : {}) : {} = {}", parameter.isGlobalParameter(), parameter.getName(), value);				
				if(value != null) {	
					InvoiceItemInfo info = parameter.isGlobalParameter() ? invoice.getInfo(parameter) :  item.getInfo(parameter);
					if(info == null) {
						info = new InvoiceItemInfo();
						info.setDimensionType(parameter.getDimensionType());
						info.setPosition(parameter.getPosition());
						info.setName(parameter.getName());
						info.setGlobalParameter(parameter.isGlobalParameter());
						if(parameter.isGlobalParameter()) {
							invoice.getInfoListChangeHandler().addNew(info);
						}
						else {
							item.getInfoListChangeHandler().addNew(info);
						}
					}
					updateInfos(info, parameter, value);
				}			
			}
		}
		catch (Exception ex) {
			log.error("Unable build parameters values.", ex);			
//				String message = MessageFormat.format("Unable build parameters values");
//				throw new BcephalException(message);
		}
	}
	
	
	private void updateInfos(InvoiceItemInfo info, BillingModelParameter parameter, Object value) throws BcephalException {
		if(value != null) {	
			if(parameter.getDimensionType() == DimensionType.MEASURE) {
				BigDecimal decimal = value instanceof BigDecimal ? (BigDecimal)value : new BigDecimal(value.toString());
				BigDecimal old = info.getDecimalValue();
				if(old == null) {
					info.setDecimalValue(decimal);
				}
				else {
					String function = StringUtils.hasText(parameter.getFunctions()) ? parameter.getFunctions().toUpperCase() : "SUM";
					if(function.equalsIgnoreCase("SUM")) {
						info.setDecimalValue(old.add(decimal));
					}
					else if(function.equalsIgnoreCase("MAX")) {
						info.setDecimalValue(old.compareTo(decimal) > 0 ? old : decimal);
					}
					else if(function.equalsIgnoreCase("MIN")) {
						info.setDecimalValue(old.compareTo(decimal) < 0 ? old : decimal);
					}
				}						
			}
			else if(parameter.getDimensionType() == DimensionType.PERIOD) {						
				Date date = null;
				try {
					date = value instanceof Date ? (Date)value : new SimpleDateFormat().parse(value.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}						
				Date old = info.getDateValue1();
				if(old == null) {
					info.setDateValue1(date);
				}
				else {
					String function = StringUtils.hasText(parameter.getFunctions()) ? parameter.getFunctions().toUpperCase() : "MIN";
					if(function.equalsIgnoreCase("MIN")) {
						info.setDateValue1(old.compareTo(date) < 0 ? old : date);
					}
					else if(function.equalsIgnoreCase("MAX")) {
						info.setDateValue1(old.compareTo(date) > 0 ? old : date);
					}
				}		
			}
			else if(parameter.getDimensionType() == DimensionType.ATTRIBUTE && !StringUtils.hasText(info.getStringValue())) {
				String string = value instanceof String ? (String)value : value.toString();
				info.setStringValue(string);
			}
		}			
	}
	
	
	public Date buildPeriod(PeriodOperator operator, Integer operationNumber, PeriodGranularity operationGranularity, String sign, Date staticDate){
		if(operator != null) {
			PeriodFilterItem item = new PeriodFilterItem();
			item.setOperator(operator);
			item.setNumber(operationNumber != null ? operationNumber : 0);
			item.setSign(sign);
			item.setGranularity(operationGranularity);
			item.setValue(staticDate);			
			return item.builDynamicDate(staticDate, sign, item.getNumber(), operationGranularity);
		}
		return null;
	}
	
	protected Integer getDueDateCalculation(String clientNumber) {			
		try{	
			//String col = new Attribute(context.clientIdId, null).getUniverseTableColumnName();
			String countSql = "SELECT measure FROM ".concat(dueDateCalculationView).concat(" WHERE client = ?");
			Query query = session.createNativeQuery(countSql);
			query.setParameter(1, clientNumber);
			query.setMaxResults(1);
			Number num = (Number) query.getSingleResult();
			return num.intValue();			
		}
		catch (Exception e) {
			return 0;
		}
	}
	
	private void calculateDueDate(Invoice invoice) {
		if(!StringUtils.hasText(dueDateCalculationView)){
			if(billingModel.getDueDateValue() != null) {
				Date date = billingModel.getDueDateValue().buildDynamicDate();
				invoice.setDueDate(date);
			}
		}
		else {	
			Integer calculation = getDueDateCalculation(invoice.getClientNumber());
			Date date = invoice.getInvoiceDate2() != null ? invoice.getInvoiceDate2() : invoice.getInvoiceDate();
			if(calculation != null && calculation != 0){	
				 date = buildPeriod(null, calculation, PeriodGranularity.DAY, "+", date);
				//date = billingModel.buildPeriod("INVOICE", calculation, Granularity.DAY.getLabel(), Operation.ADD.getLabel(), date);
			}
			else {
				if(billingModel.getDueDateValue() != null) {
					date = billingModel.getDueDateValue().buildDynamicDate();
				}
			}				
			invoice.setDueDate(date);
		}
	}
	
	private Object[] getGroupingRow(Object[] row) {		
		Object[] groupRow = new Object[context.groupingColumnPositions.size()];
		int i = 0;
		List<Integer> values = new ArrayList<Integer>(context.groupingColumnPositions.values());
		Collections.sort(values, new java.util.Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1 - o2;
			}
		});		
		for(Integer position : values) {
			groupRow[i++] = row[position];
		}
		return groupRow;
	}
	
	private boolean[] computeExcludeDriverAndUnitCost(Object[] row) {		
		boolean[] result = new boolean[2];
		for(Long key : context.groupingColumnPositions.keySet()) {
			BillingModelDriverGroup group = context.driverGroupColumnPositions.get(key);
			if(group != null) {
				Object value = row[context.groupingColumnPositions.get(key)];
				BillingModelDriverGroupItem item = group.getItemByValue((String)value);
				result[0] = (result[0] || (item != null && item.isExcludeDriver()));
				result[1] = (result[1] || (item != null && item.isExcludeUnitCost()));
			}			
		}		
		return result;
	}
	
	private CalculateBillingItem getCalculateBillingItem(Object[] row) {		
		Map<Long, String> parameters = new HashMap<>(0);
		for(Long key : context.groupingColumnPositions.keySet()) {
			Object value = row[context.groupingColumnPositions.get(key)];
			
			BillingModelGroupingItem item = this.billingModel.getBillingModelGroupingItem(key);
			if(item != null) {
				parameters.put(item.getId(), (String)value);	
			}
			else {
				throw new BcephalException("Wrong Calculate billing element filter!");
			}				
		}	
		CalculateBillingItem result = this.billingModel.getCalculateBillingItem(parameters);				
		return result;
	}
	
	private Object[] getParameterRow(Object[] row) {
		Object[] parameterRow = new Object[this.billingModel.getParameters().size()];		
		int i = 0;
		for(BillingModelParameter parameter : this.billingModel.getParameters()) {
			parameterRow[i++] = row[parameter.columnPosition];
		}		
		return parameterRow;
	}
	
	
	@Data
	@AllArgsConstructor
	public class Client {
		
		String number;
		String name;
		
		String email;
	}
	
}

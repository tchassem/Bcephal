/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.ModelPeriodGranularity;
import com.moriset.bcephal.domain.ModelPeriodSide;
import com.moriset.bcephal.domain.SchedulableObject;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.domain.filters.UniverseFilter;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Moriset
 *
 */
@Entity(name = "BillingModel")
@Table(name = "BCP_BILLING_MODEL")
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingModel extends SchedulableObject {
	
	private static final long serialVersionUID = 8879146216395236873L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_model_seq")
	@SequenceGenerator(name = "billing_model_seq", sequenceName = "billing_model_seq", initialValue = 1,  allocationSize = 1)
	private Long id;			 
    
    private String currency;
    
    @ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "filter")
	private UniverseFilter filter;	
    
    private Long invoiceSequenceId;
    
    private Long invoiceValidationSequenceId;
    
    private boolean selectPeriodAtRuntime;
	
	private boolean useUnitCostToComputeAmount;
	
	private boolean includeUnitCost;
	
	private boolean useVat;
	
	@Transient
	//@ManyToOne @JoinColumn(name = "vatFilter")
	private UniverseFilter vatFilter;
	
	private boolean orderItems;
	
	private boolean orderItemsAsc;
    
    private boolean separateInvoicePerPeriod;
	
	private boolean includeZeroAmountEvents;
	
	private boolean refreshRepositoriesBeforeRun;	
	
	private boolean refreshRepositoriesAfterRun;	
	
	private boolean buildCommunicationMessage;
    
	private boolean dueDateCalculation;	
	
	private boolean invoiceDateCalculation;	
	
	private String fileNameDescription;
	
	private Integer driverDecimalNumber;
	
	private Integer unitCostDecimalNumber;
	
	private Integer billingAmountDecimalNumber;	
	
	private boolean validateGeneratedInvoices;
	
	private boolean useValidationDateAsInvoiceDate;
	
	private boolean reprintPdfAtValidation;	
	
	private boolean confirmAction;
	
		
	@AttributeOverrides({
	    @AttributeOverride(name="dateOperator", 	column = @Column(name="dueDateOperator")),
	    @AttributeOverride(name="dateValue", 		column = @Column(name="dueDateValue")),
	    @AttributeOverride(name="variableName", 	column = @Column(name="dueDateVariableName")),
	    @AttributeOverride(name="dateSign", 		column = @Column(name="dueDateSign")),
	    @AttributeOverride(name="dateNumber", 		column = @Column(name="dueDateNumber")),
	    @AttributeOverride(name="dateGranularity",	column = @Column(name="dueDateGranularity"))
	})
	@Embedded	
	private PeriodValue dueDateValue;
	
	@AttributeOverrides({
	    @AttributeOverride(name="dateOperator", 	column = @Column(name="invoiceDateOperator")),
	    @AttributeOverride(name="dateValue", 		column = @Column(name="invoiceDateValue")),
	    @AttributeOverride(name="variableName", 	column = @Column(name="invoiceVariableName")),
	    @AttributeOverride(name="dateSign", 		column = @Column(name="invoiceDateSign")),
	    @AttributeOverride(name="dateNumber", 		column = @Column(name="invoiceDateNumber")),
	    @AttributeOverride(name="dateGranularity",	column = @Column(name="invoiceDateGranularity"))
	})
	@Embedded	
	private PeriodValue invoiceDateValue;	
	
			
	@Enumerated(EnumType.STRING)
	private ModelPeriodSide periodSide;
	
	@AttributeOverrides({
	    @AttributeOverride(name="dateOperator", 	column = @Column(name="fromDateOperator")),
	    @AttributeOverride(name="dateValue", 		column = @Column(name="fromDateValue")),
	    @AttributeOverride(name="variableName", 	column = @Column(name="fromVariableName")),
	    @AttributeOverride(name="dateSign", 		column = @Column(name="fromDateSign")),
	    @AttributeOverride(name="dateNumber", 		column = @Column(name="fromDateNumber")),
	    @AttributeOverride(name="dateGranularity",	column = @Column(name="fromDateGranularity"))
	})
	@Embedded	
	private PeriodValue fromDateValue;
	
	@AttributeOverrides({
	    @AttributeOverride(name="dateOperator", 	column = @Column(name="toDateOperator")),
	    @AttributeOverride(name="dateValue", 		column = @Column(name="toDateValue")),
	    @AttributeOverride(name="variableName", 	column = @Column(name="toVariableName")),
	    @AttributeOverride(name="dateSign", 		column = @Column(name="toDateSign")),
	    @AttributeOverride(name="dateNumber", 		column = @Column(name="toDateNumber")),
	    @AttributeOverride(name="dateGranularity",	column = @Column(name="toDateGranularity"))
	})
	@Embedded	
	private PeriodValue toDateValue;	
	
	
	private boolean addAppendicies;
	
	@Enumerated(EnumType.STRING)
	private BillingModelAppendicyType appendicyType;
	
	private DataSourceType appendicyGridType;
	
	private Long appendicyGridId;
	
	private String billTemplateCode;
	
	private String billingCompanyCode;		
	
	@Enumerated(EnumType.STRING)
	private ModelPeriodGranularity periodGranularity;
	
	@Enumerated(EnumType.STRING)
	private BillingModelInvoiceGranularityLevel invoiceGranularityLevel;
		
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "billing")
	private List<BillingModelItem> items;
	@Transient 
	private ListChangeHandler<BillingModelItem> itemListChangeHandler;
		
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "billing")
	private List<BillingModelPivot> pivots;
	@Transient
	private ListChangeHandler<BillingModelPivot> pivotListChangeHandler;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "billing")
	private List<BillingModelGroupingItem> groupingItems;
	@Transient
	private ListChangeHandler<BillingModelGroupingItem> groupingItemListChangeHandler;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "billing")
	private List<BillingModelParameter> parameters;
	@Transient
	private ListChangeHandler<BillingModelParameter> parameterListChangeHandler;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "billing")
	private List<BillingModelDriverGroup> driverGroups;
	@Transient
	private ListChangeHandler<BillingModelDriverGroup> driverGroupListChangeHandler;
		
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "billing")
	private List<BillingModelEnrichmentItem> enrichmentItems;
	@Transient
	private ListChangeHandler<BillingModelEnrichmentItem> enrichmentItemListChangeHandler;

	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "billing")
	private List<BillingDescription> billingDescriptions;
	@Transient 
	private ListChangeHandler<BillingDescription> billingDescriptionsListChangeHandler;

	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "modelId")
	private List<BillingModelTemplate> billingModelTemplates;
	@Transient 
	private ListChangeHandler<BillingModelTemplate> billingModelTemplatesListChangeHandler;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "modelId")
	private List<CalculateBillingItem> calculateBillingItems;
	@Transient 
	private ListChangeHandler<CalculateBillingItem> calculateBillingItemListChangeHandler;
	
	
	public BillingModel() {
		this.itemListChangeHandler = new ListChangeHandler<BillingModelItem>();
		this.pivotListChangeHandler = new ListChangeHandler<BillingModelPivot>();
		this.groupingItemListChangeHandler = new ListChangeHandler<BillingModelGroupingItem>();
		this.parameterListChangeHandler = new ListChangeHandler<BillingModelParameter>();
		this.driverGroupListChangeHandler = new ListChangeHandler<BillingModelDriverGroup>();
		this.enrichmentItemListChangeHandler = new ListChangeHandler<BillingModelEnrichmentItem>();
		this.billingDescriptionsListChangeHandler = new ListChangeHandler<BillingDescription>();
		this.billingModelTemplatesListChangeHandler = new ListChangeHandler<BillingModelTemplate>();
		this.calculateBillingItemListChangeHandler = new ListChangeHandler<CalculateBillingItem>();
		this.periodSide = ModelPeriodSide.CURRENT;
		this.periodGranularity = ModelPeriodGranularity.MONTH;
		this.invoiceGranularityLevel = BillingModelInvoiceGranularityLevel.CATEGORY;
		this.addAppendicies = false;
		this.includeZeroAmountEvents = true;
		this.fromDateValue = new PeriodValue();
		this.toDateValue = new PeriodValue();
		this.dueDateValue = new PeriodValue();
		this.invoiceDateValue = new PeriodValue();
		this.refreshRepositoriesAfterRun = true;
		this.refreshRepositoriesBeforeRun = true;
		this.driverDecimalNumber = 2;
		this.unitCostDecimalNumber = 2;
		this.billingAmountDecimalNumber = 2;
		this.fileNameDescription = "$INVOICE YEAR$$INVOICE MONTH$$INVOICE DAY$ - $CLIENT ID$ - $INVOICE NBR$";
	}
	
	public DataSourceType getAppendicyGridType() {
		if(appendicyGridType == null) {
			appendicyGridType = DataSourceType.REPORT_GRID;
		}
		return appendicyGridType;
	}

	public void setItems(List<BillingModelItem> items) {
		this.items = items;
		itemListChangeHandler.setOriginalList(items);
	}
	
	public void setPivots(List<BillingModelPivot> items) {
		this.pivots = items;
		pivotListChangeHandler.setOriginalList(items);
	}
	
	public void setGroupingItems(List<BillingModelGroupingItem> items) {
		this.groupingItems = items;
		groupingItemListChangeHandler.setOriginalList(items);
	}
	
	public void setParameters(List<BillingModelParameter> items) {
		this.parameters = items;
		parameterListChangeHandler.setOriginalList(items);
	}
	
	public void setDriverGroups(List<BillingModelDriverGroup> items) {
		this.driverGroups = items;
		driverGroupListChangeHandler.setOriginalList(items);
	}
	
	public void setEnrichmentItems(List<BillingModelEnrichmentItem> items) {
		this.enrichmentItems = items;
		enrichmentItemListChangeHandler.setOriginalList(items);
	}
	
	public void setBillingDescriptions(List<BillingDescription> items) {
		this.billingDescriptions = items;
		billingDescriptionsListChangeHandler.setOriginalList(items);
	}
	
	public void setBillingModelTemplates(List<BillingModelTemplate> items) {
		this.billingModelTemplates = items;
		billingModelTemplatesListChangeHandler.setOriginalList(items);
	}
	
	public void setCalculateBillingItems(List<CalculateBillingItem> calculateBillingItems) {
		this.calculateBillingItems = calculateBillingItems;
		calculateBillingItemListChangeHandler.setOriginalList(calculateBillingItems);
	}

	public BillingDescription getBillingDescription(String clientLanguage) {
		if(StringUtils.hasText(clientLanguage)) {
			for(BillingDescription description : getBillingDescriptionsListChangeHandler().getItems()) {
				if(StringUtils.hasText(description.getLocale()) && description.getLocale().equalsIgnoreCase(clientLanguage)) {
					return description;
				}
			}
		}		
		return null;
	}
	
	public BillingModelGroupingItem getBillingModelGroupingItem(Long attributeId) {
		if(attributeId != null) {
			for(BillingModelGroupingItem item : getGroupingItemListChangeHandler().getItems()) {
				if(item.getAttributeId() == attributeId || item.getAttributeId().equals(attributeId)) {
					return item;
				}
			}
		}		
		return null;
	}
	
	@JsonIgnore
	public CalculateBillingItem getCalculateBillingItem(Map<Long, String> parameters) {
		for(CalculateBillingItem item : getCalculateBillingItemListChangeHandler().getItems()) {
			if(item.matchs(parameters)) {
				return item;
			}
		}
		return null;
	}
	
	@PostLoad
	public void initListChangeHandler() {
		items.size();
		pivots.size();
		groupingItems.size();
		parameters.size();
		driverGroups.size();
		enrichmentItems.size();
		billingDescriptions.size();
		billingModelTemplates.size();
		calculateBillingItems.size();
		this.itemListChangeHandler.setOriginalList(items);		
		this.pivotListChangeHandler.setOriginalList(pivots);
		this.groupingItemListChangeHandler.setOriginalList(groupingItems);
		this.parameterListChangeHandler.setOriginalList(parameters);
		this.driverGroupListChangeHandler.setOriginalList(driverGroups);
		this.enrichmentItemListChangeHandler.setOriginalList(enrichmentItems);
		this.billingDescriptionsListChangeHandler.setOriginalList(billingDescriptions);
		this.billingModelTemplatesListChangeHandler.setOriginalList(billingModelTemplates);
		this.calculateBillingItemListChangeHandler.setOriginalList(calculateBillingItems);
		
		if(this.driverDecimalNumber== null)this.driverDecimalNumber = 2;
		if(this.unitCostDecimalNumber== null)this.unitCostDecimalNumber = 2;
		if(this.billingAmountDecimalNumber == null)this.billingAmountDecimalNumber = 2;
	}
	
	
	@Override
	public BillingModel copy() {
		BillingModel copy = new BillingModel();
		copy.setName(this.getName() + System.currentTimeMillis());
		copy.setGroup(this.getGroup());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		copy.setDescription(getDescription());
		copy.setActive(isActive());
		copy.setScheduled(isScheduled());
		copy.setCronExpression(getCronExpression());
		copy.setCurrency(getCurrency());
		copy.setFilter(filter != null ? getFilter().copy() : null);
		copy.setInvoiceSequenceId(getInvoiceSequenceId());
		copy.setInvoiceValidationSequenceId(getInvoiceValidationSequenceId());
		copy.setSelectPeriodAtRuntime(isSelectPeriodAtRuntime());
		copy.setUseUnitCostToComputeAmount(isUseUnitCostToComputeAmount());
		copy.setIncludeUnitCost(isIncludeUnitCost());
		copy.setUseVat(isUseVat());
		copy.setVatFilter(vatFilter != null ? getVatFilter().copy() : null);
		copy.setOrderItems(isOrderItems());
		copy.setOrderItemsAsc(isOrderItemsAsc());
		copy.setSeparateInvoicePerPeriod(isSeparateInvoicePerPeriod());
		copy.setIncludeZeroAmountEvents(isIncludeZeroAmountEvents());
		copy.setRefreshRepositoriesBeforeRun(isRefreshRepositoriesBeforeRun());
		copy.setRefreshRepositoriesAfterRun(isRefreshRepositoriesAfterRun());
		copy.setBuildCommunicationMessage(isBuildCommunicationMessage());
		copy.setDueDateCalculation(isDueDateCalculation());
		copy.setInvoiceDateCalculation(isInvoiceDateCalculation());
		copy.setFileNameDescription(getFileNameDescription());
		copy.setValidateGeneratedInvoices(isValidateGeneratedInvoices());
		copy.setUseValidationDateAsInvoiceDate(isUseValidationDateAsInvoiceDate());
		copy.setReprintPdfAtValidation(isReprintPdfAtValidation());
		copy.setDueDateValue(dueDateValue != null ? getDueDateValue().copy() : null);
		copy.setInvoiceDateValue(invoiceDateValue != null ? getInvoiceDateValue().copy() : null);
		copy.setPeriodSide(getPeriodSide());
		copy.setFromDateValue(fromDateValue != null ? getFromDateValue().copy() : null);
		copy.setToDateValue(toDateValue != null ? getToDateValue().copy() : null);
		copy.setAddAppendicies(isAddAppendicies());
		copy.setAppendicyType(getAppendicyType());
		copy.setBillTemplateCode(getBillTemplateCode());
		copy.setBillingCompanyCode(getBillingCompanyCode());
		copy.setPeriodGranularity(getPeriodGranularity());
		copy.setInvoiceGranularityLevel(getInvoiceGranularityLevel());
		copy.setDriverDecimalNumber(getDriverDecimalNumber());
		copy.setUnitCostDecimalNumber(getUnitCostDecimalNumber());
		copy.setBillingAmountDecimalNumber(getBillingAmountDecimalNumber());
		for(BillingModelItem item : getItemListChangeHandler().getItems()) {
			copy.getItemListChangeHandler().addNew(item.copy());
		}
		for(BillingModelPivot item : getPivotListChangeHandler().getItems()) {
			copy.getPivotListChangeHandler().addNew(item.copy());
		}
		for(BillingModelGroupingItem item : getGroupingItemListChangeHandler().getItems()) {
			copy.getGroupingItemListChangeHandler().addNew(item.copy());
		}
		for(BillingModelParameter item : getParameterListChangeHandler().getItems()) {
			copy.getParameterListChangeHandler().addNew(item.copy());
		}
		for(BillingModelDriverGroup item : getDriverGroupListChangeHandler().getItems()) {
			copy.getDriverGroupListChangeHandler().addNew(item.copy());
		}
		for(BillingModelEnrichmentItem item : getEnrichmentItemListChangeHandler().getItems()) {
			copy.getEnrichmentItemListChangeHandler().addNew(item.copy());
		}
		for(BillingDescription item : getBillingDescriptionsListChangeHandler().getItems()) {
			copy.getBillingDescriptionsListChangeHandler().addNew(item.copy());
		}
		for(BillingModelTemplate item : getBillingModelTemplatesListChangeHandler().getItems()) {
			copy.getBillingModelTemplatesListChangeHandler().addNew(item.copy());
		}
		for(CalculateBillingItem item : getCalculateBillingItemListChangeHandler().getItems()) {
			copy.getCalculateBillingItemListChangeHandler().addNew(item.copy());
		}
		return copy;
	}


			
}

/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MailSendingStatus;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

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
 * @author Joseph Wambo
 *
 */
@Entity(name = "Invoice")
@Table(name = "BCP_BILLING_INVOICE")
@Data
@EqualsAndHashCode(callSuper = false)
public class Invoice extends MainObject {

	private static final long serialVersionUID = -4397935903325399563L;
	
	public final static String FILE_SEPARATOR = " ; ";
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_invoice_seq")
	@SequenceGenerator(name = "billing_invoice_seq", sequenceName = "billing_invoice_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@Enumerated(EnumType.STRING) 
	private InvoiceType type;
	
	private Long invoiceValidationSequenceId;
	
	private boolean useValidationDateAsInvoiceDate;	
	
	private boolean reprintPdfAtValidation;	
		
	private String reference;
	
	private String draftReference;
		
	private String validationReference;
	
	private Date validationDate;
	
	private String orderReference;	

	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	private Invoice parent;
	
	
	private String runNumber;
	
    private String merchantNumber;
    private String clientDoingBusinessAs;
   	private String clientInternalNumber;
   	
   	
   	private Boolean useVat;
   	
   	private String contactTitle;
   	
   	private String contactFirstname;
   	
   	private String contactLastname;
			
	private int version;
	
	private String billTemplateCode;
	
	private boolean manuallyModified;
	
	private String otherFiles;
		
	private String file;
	
	private String fileName;
		
	private String fileNameDescription;
	
	private String descriptionDescription;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date billingDate;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date invoiceDate;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date invoiceDate2;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dueDate;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
    private Date creationDateBilling;
	
	private String communicationMessage;
	
	private String departmentNumber;
	
	
	private String clientContact;
	
	private String clientNumber;
	
	private String clientName;
	
	private String clientLegalForm;
	
	private String clientAdressStreet;
	
	private String clientAdressPostalCode;
	
	private String clientAdressCity;
	
	private String clientAdressCountry;
	
	private String clientVatNumber;
	
	private String clientEmail;
	
	private String clientEmailCc;
	
	private String clientPhone;
	
	private String clientLanguage;
	
	
	private String billingCompanyNumber;
	
	private String billingCompanyName;
	
	private String billingCompanyLegalForm;
	
	private String billingCompanyAdressStreet;
	
	private String billingCompanyAdressPostalCode;
	
	private String billingCompanyAdressCity;
	
	private String billingCompanyAdressCountry;
	
	private String billingCompanyVatNumber;
	
	private String billingCompanyEmail;
	
	private String billingCompanyPhone;
	
		
	private BigDecimal amountWithoutVat;
	
	private BigDecimal vatAmount;		
	
	private BigDecimal billingAmountWithoutVat;
	
	private BigDecimal billingVatAmount;
			
	private String amountUnit;
	
	
	private boolean subjectToVat;
	
	private boolean useUnitCost;
	
	private String pivotValues;
	
	private boolean orderItems;
	
	private boolean orderItemsAsc;
	
	private Integer driverDecimalNumber;
	
	private Integer unitCostDecimalNumber;
	
	private Integer billingAmountDecimalNumber;	
	
	private boolean confirmAction;
	
	@Enumerated(EnumType.STRING) 
	private InvoiceStatus status;
	
	@Enumerated(EnumType.STRING) 
	private MailSendingStatus mailStatus;
			
	
	@JsonIgnore
	@Transient
	private List<InvoiceVatItem> vatItems;
			
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "invoice")
	private List<InvoiceItem> items;

	@Transient
	private ListChangeHandler<InvoiceItem> itemListChangeHandler;
	
	@Transient
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "parent")
	private List<Invoice> children;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "invoice")
	private List<InvoiceItemInfo> infos;

	@Transient
	private ListChangeHandler<InvoiceItemInfo> infoListChangeHandler;
			
	@JsonIgnore @Transient
	private List<InvoiceDetail> details;
	
	@Transient @JsonIgnore
	private BillingDescription billingDescription;
	
	
	public Invoice() {
		super();
		useVat = false;
		this.driverDecimalNumber = 2;
		this.unitCostDecimalNumber = 2;
		this.billingAmountDecimalNumber = 2;
		vatItems = new ArrayList<InvoiceVatItem>(0);
		items = new ArrayList<InvoiceItem>(0);
		itemListChangeHandler = new ListChangeHandler<InvoiceItem>();
		details = new ArrayList<InvoiceDetail>(0);
		children = new ArrayList<Invoice>(0);
		infos = new ArrayList<InvoiceItemInfo>(0);
		infoListChangeHandler = new ListChangeHandler<InvoiceItemInfo>();
	}
	
	public boolean isUseVat() {
		if(useVat == null) {
			useVat = false;
		}
		return useVat;
	}
		
	/**
	 * @return the amountWithoutVat
	 */
	public BigDecimal getAmountWithoutVat() {
		if(amountWithoutVat == null) {
			amountWithoutVat = BigDecimal.ZERO;
		}
		return amountWithoutVat;
	}
	
	/**
	 * @return the vatAmount
	 */
	public BigDecimal getVatAmount() {
		if(vatAmount == null) {
			vatAmount = BigDecimal.ZERO;
		}
		return vatAmount;
	}
	
	@JsonIgnore
	public BigDecimal getBillingTotalAmount() {
		return getBillingAmountWithoutVat().add(getBillingVatAmount());
	}
	
	/**
	 * @return the totalAmount
	 */
	public BigDecimal getTotalAmount() {
		return getAmountWithoutVat().add(getVatAmount());
	}
	
	public void setItems(List<InvoiceItem> items) {
		this.items = items;
		this.itemListChangeHandler.setOriginalList(items);
	}
	
	public List<InvoiceItem> getItems() {
		if(this.isOrderItems()) {
			sortItems(this.isOrderItemsAsc());
		}
		return items;
	}


	private void sortItems(boolean asc) {
		Collections.sort(items, new Comparator<InvoiceItem>() {
			@Override
			public int compare(InvoiceItem item1, InvoiceItem item2) {
				if(item1.getDescription() == null) {
					return asc ? -1 : 1;
				}
				if(item2.getDescription() == null) {
					return asc ? -1 : 1;
				}
				if(asc) {
					return item1.getDescription().compareTo(item2.getDescription());
				}
				else {
					return item2.getDescription().compareTo(item1.getDescription());
				}
			}
		});
	}
	
	public void setInfos(List<InvoiceItemInfo> infos) {
		this.infos = infos;
		this.infoListChangeHandler.setOriginalList(infos);
	}
		
	@PostLoad
	public void initListChangeHandler() {
		items.size();
		this.itemListChangeHandler.setOriginalList(items);
		children.size();
		infos.size();
		this.infoListChangeHandler.setOriginalList(infos);
		if(this.driverDecimalNumber== null)this.driverDecimalNumber = 2;
		if(this.unitCostDecimalNumber== null)this.unitCostDecimalNumber = 2;
		if(this.billingAmountDecimalNumber == null)this.billingAmountDecimalNumber = 2;
	}
	
	@JsonIgnore
	public boolean isInvoice() {
		return getType() != null && getType() == InvoiceType.INVOICE;
	}
	
	@JsonIgnore
	public boolean isCreditNote() {
		return getType() != null && getType() == InvoiceType.CREDIT_NOTE;
	}
	
	public InvoiceItemInfo getInfo(BillingModelParameter parameter) {
		for(InvoiceItemInfo info : getInfoListChangeHandler().getItems()) {
			if(info.getDimensionType() == parameter.getDimensionType() && info.getName() .equals(parameter.getName())) {
				return info;
			}
		}
		return null;
	}		
	
	public String buildFileName() {
		String description = getFileNameDescription();
		if(org.springframework.util.StringUtils.hasText(description)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			InvoiceVariables variables = new InvoiceVariables();
			description = description.replace(variables.CLIENT_ID, getClientNumber());
			description = description.replace(variables.CLIENT_NAME, getClientName());
			description = description.replace(variables.CURRENT_DATE, format.format(new Date()));
			description = description.replace(variables.CURRENT_DATE_TIME, new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			description = description.replace(variables.INVOICE_DATE, format.format(getBillingDate()));
			description = description.replace(variables.INVOICE_DAY, new SimpleDateFormat("dd").format(getBillingDate()));
			description = description.replace(variables.INVOICE_MONTH, new SimpleDateFormat("MM").format(getBillingDate()));
			description = description.replace(variables.INVOICE_MONTH_NAME, new SimpleDateFormat("MMMM").format(getBillingDate()));
			description = description.replace(variables.INVOICE_YEAR, new SimpleDateFormat("yyyy").format(getBillingDate()));			
			description = description.replace(variables.INVOICE_FROM, format.format(getInvoiceDate()));
			description = description.replace(variables.INVOICE_TO, format.format(getInvoiceDate2()));	
			description = description.replace(variables.INVOICE_NBR, getReference());
			if(org.springframework.util.StringUtils.hasText(getDescription())) {
				description = description.replace(variables.INVOICE_DESCRIPTION, getDescription());	
			}
		}	
		return description;
	}
	
	public String buildDescription() {
		String description = billingDescription != null ? billingDescription.getDescription() : getDescription();
		if(description != null) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			InvoiceVariables variables = new InvoiceVariables();
			description = description.replace(variables.CLIENT_ID, getClientNumber());
			description = description.replace(variables.CLIENT_NAME, getClientName());
			description = description.replace(variables.CURRENT_DATE, format.format(new Date()));
			description = description.replace(variables.CURRENT_DATE_TIME, new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			description = description.replace(variables.INVOICE_DATE, format.format(getBillingDate()));
			description = description.replace(variables.INVOICE_DAY, new SimpleDateFormat("dd").format(getBillingDate()));
			description = description.replace(variables.INVOICE_MONTH, new SimpleDateFormat("MM").format(getBillingDate()));
			description = description.replace(variables.INVOICE_MONTH_NAME, new SimpleDateFormat("MMMM").format(getBillingDate()));
			description = description.replace(variables.INVOICE_YEAR, new SimpleDateFormat("yyyy").format(getBillingDate()));			
			description = description.replace(variables.INVOICE_FROM, format.format(getInvoiceDate()));
			description = description.replace(variables.INVOICE_TO, format.format(getInvoiceDate2()));	
			description = description.replace(variables.INVOICE_NBR, getReference());
			if(StringUtils.hasText(getDescription())) {
				description = description.replace(variables.INVOICE_DESCRIPTION, getDescription());	
			}
		}
		setDescription(description);
		return description;
	}
		
	public InvoiceItem getItemByDescription(String category, String description, BigDecimal rate) {
		for(InvoiceItem item : getItemListChangeHandler().getItems()) {
			if((item.getCategory() == null && category == null) || (item.getCategory() != null && item.getCategory().equals(category))) {
				if(item.getDescription() != null && item.getDescription().equals(description)) {
					if(rate.equals(item.getVatRate())) {
						return item;
					}
				}
			}
		}
		return null;
	}
	
	public void buildBillingElements() {
		for(InvoiceItem item : itemListChangeHandler.getItems()) {
			if(item.getBillingElements() != null && item.getBillingElements().getCalculateBillingItem() != null) {
				item.getBillingElements().buildElements();
				BigDecimal amount = item.getBillingElements().getAmount();
				BigDecimal unitCost = item.getBillingElements().getUnitCost();
				BigDecimal driver = item.getBillingElements().getDriver();
				item.setAmount(amount);
				item.setQuantity(driver);
				item.setUnitCost(unitCost);
			};
		}
	}

	public void buildVatItems() {
		this.vatItems = new ArrayList<InvoiceVatItem>(0);
		HashMap<BigDecimal, BigDecimal> vats = new HashMap<BigDecimal, BigDecimal>();
		BigDecimal totalAountWithoutVat = BigDecimal.ZERO;
		for(InvoiceItem item : itemListChangeHandler.getItems()) {
			if(item.getVatRate() != null && item.getVatRate().compareTo(BigDecimal.ZERO) > 0 && item.getAmount() != null) {
				if(!vats.containsKey(item.getVatRate())) {
					vats.put(item.getVatRate(), BigDecimal.ZERO);
				}
				BigDecimal value = vats.get(item.getVatRate()).add(item.getAmount());
				vats.put(item.getVatRate(), value);
			}
			BigDecimal amount = item.isUseUnitCost() ? item.getUnitCost().multiply(item.getQuantity()) : item.getAmount();
			if(amount.equals(BigDecimal.ZERO)) {
				amount = item.getAmount();
			}
			totalAountWithoutVat = totalAountWithoutVat.add(amount);
		}
		
		List<BigDecimal> rates = new ArrayList<BigDecimal>(vats.keySet());
		Collections.sort(rates);
		BigDecimal totalVatAount = BigDecimal.ZERO;
		for(BigDecimal rate : rates) {
			InvoiceVatItem item = new InvoiceVatItem(rate, vats.get(rate), getAmountUnit());
			this.vatItems.add(item);
			totalVatAount = totalVatAount.add(item.getVatAmount());
		}		
					
		if(totalVatAount != null && totalVatAount != BigDecimal.ZERO) {
			totalVatAount = totalVatAount.setScale(2, RoundingMode.HALF_UP);
		}
		if(totalAountWithoutVat != null && totalAountWithoutVat != BigDecimal.ZERO) {
			totalAountWithoutVat =  totalAountWithoutVat.setScale(2, RoundingMode.HALF_UP);
		}
		
		setVatAmount(totalVatAount);
		setAmountWithoutVat(totalAountWithoutVat);	
		
		for(Invoice invoice : children) {
			invoice.buildVatItems();
		}
	}
		
	public String getContact() {
		String contact = "";
		if (StringUtils.hasText(contactTitle)) {
			contact += contactTitle + " ";
		}
		if (StringUtils.hasText(contactFirstname)) {
			contact += contactFirstname + " ";
		}
		if (StringUtils.hasText(contactLastname)) {
			contact += contactLastname;
		}
		return StringUtils.hasText(contact.trim()) ? contact.trim() : null; 
	}
	
	@Override
	public Persistent copy() {
		return null;
	}
	
	
	
	@JsonIgnore
	private Map<String, List<InvoiceItem>> getItemsByCategory() {
		Map<String, List<InvoiceItem>> map = new HashMap<String, List<InvoiceItem>>(0);
		for(InvoiceItem item : this.itemListChangeHandler.getItems()) {
			List<InvoiceItem> list = map.get(item.getCategory());
			if(list == null) {
				list = new ArrayList<InvoiceItem>(0);
				map.put(item.getCategory(), list);
			}
			list.add(item);
		}
		return map;
	}
	
	@JsonIgnore
	public List<InvoiceItemBean> getInvoiceItemDataSource(){
		Map<String, List<InvoiceItem>> map = getItemsByCategory();
		List<InvoiceItemBean> list = new ArrayList<InvoiceItemBean>(0);
		for(String key : map.keySet()) {
			String[] categories = null;
			String separetor = "$||$";
			if(key != null && key.contains(separetor)) {
				String separetor2 = "\\$\\|\\|\\$";
				categories = key.split(separetor2);	
				if(categories != null) {
					int offset = 0;
					InvoiceItemBean firstBean = null;
					while (offset < categories.length) {	
						String category = categories[offset];
						if(offset == 0) {
							InvoiceItemBean bean = findBeans(category, list);
							if(bean == null) {
								bean = 	new InvoiceItemBean(findItems(map,key,category,offset, categories.length - 1), category, null, null, 0);	
								list.add(bean);
							}		
							firstBean = bean;
						}else {
							InvoiceItemBean bean = findBeans(category, firstBean.getBeans());
							if(bean == null) {
								bean = 	new InvoiceItemBean(findItems(map,key,category,offset, categories.length - 1), category, null, "  ", 0);	
								firstBean.getBeans().add(bean);
							}else {
								bean.getBeans().add(new InvoiceItemBean(findItems(map,key,category,offset, categories.length - 1), category, null, bean.getSpace() + "  ", 0));
							}
						}
						offset++;
					}
				}
			}else {			
				createdBeanItem(key, list, map);
			}
		}
		
		Collections.sort(list, new Comparator<InvoiceItemBean>() {
			@Override
			public int compare(InvoiceItemBean o1, InvoiceItemBean o2) {
				return o1.position - o2.position;
			}
		});
		
		return list;
	}
	
	@JsonIgnore
	public Invoice getThis(){
		return this;
	}
	
	@JsonIgnore
	public BigDecimal getInfosVatAmount(String parameterCode) {
		BigDecimal value = BigDecimal.ZERO;
		List<InvoiceItem> items = this.itemListChangeHandler.getItems();
		for (InvoiceItem invoiceItem : items) {
			if(invoiceItem != null && parameterCode != null) {
				value = value.add(invoiceItem.getInfosVatAmount(parameterCode));
			}
		}
		return value;
	}
		
	@JsonIgnore
	public Object getGlobalInfos(String parameterCode, String type) {
		if(type == null) {
			type = DimensionType.ATTRIBUTE.name();
		}
		DimensionType pType = DimensionType.valueOf(type);
		if(DimensionType.MEASURE.equals(pType)) {
			BigDecimal value = BigDecimal.ZERO;
			List<InvoiceItemInfo> items = this.infoListChangeHandler.getItems();
			for (InvoiceItemInfo invoiceItemInfo : items) {
				if(invoiceItemInfo != null && parameterCode != null && parameterCode.equals(invoiceItemInfo.getName())) {
					if(invoiceItemInfo.getDecimalValue() != null) {
						value = value.add(invoiceItemInfo.getDecimalValue());
					}
				}
			}
			return value;
		}
		else
			if(DimensionType.PERIOD.equals(pType)) {
				List<InvoiceItemInfo> items = this.infoListChangeHandler.getItems();
				for (InvoiceItemInfo invoiceItemInfo : items) {
					if(invoiceItemInfo != null && parameterCode != null && parameterCode.equals(invoiceItemInfo.getName())) {
						return invoiceItemInfo.getDateValue1();
					}
				}
			}
			else
				if(DimensionType.ATTRIBUTE.equals(pType)) {
					List<InvoiceItemInfo> items = this.infoListChangeHandler.getItems();
					for (InvoiceItemInfo invoiceItemInfo : items) {
						if(invoiceItemInfo != null && parameterCode != null && parameterCode.equals(invoiceItemInfo.getName())) {
							return invoiceItemInfo.getStringValue();
						}
					}
				}
//				else
//					if(DimensionType.INTERVAL.equals(pType)) {
//						List<InvoiceItemInfo> items = this.infoListChangeHandler.getItems();
//						for (InvoiceItemInfo invoiceItemInfo : items) {
//							if(invoiceItemInfo != null && parameterCode != null && parameterCode.equals(invoiceItemInfo.getName())) {
//								return invoiceItemInfo.getDateValue1() + " - " + invoiceItemInfo.getDateValue2();
//							}
//						}
//					}
		return null;
	}
	
	@JsonIgnore
	public Object getInfos(String parameterCode, String type) {
		Object value = null;
		List<InvoiceItem> items = this.itemListChangeHandler.getItems();
		if (type == null) {
			type = DimensionType.ATTRIBUTE.name();
		}
		DimensionType pType = DimensionType.valueOf(type);
		if (DimensionType.MEASURE.equals(pType)) {
			BigDecimal value2 = BigDecimal.ZERO;
			for (InvoiceItem invoiceItem : items) {
				if (invoiceItem != null && parameterCode != null) {
					value2 = value2.add((BigDecimal) invoiceItem.getInfos(parameterCode, type));
				}
			}
			value = value2;
		} else if (DimensionType.PERIOD.equals(pType)) {
			for (InvoiceItem invoiceItem : items) {
				if (invoiceItem != null && parameterCode != null) {
					value = invoiceItem.getInfos(parameterCode, type);
					if(value != null) {
						return value;
					}
				}
			}
		} else if (DimensionType.ATTRIBUTE.equals(pType)) {
			value = null;
			for (InvoiceItem invoiceItem : items) {
				if (invoiceItem != null && parameterCode != null) {
					value = invoiceItem.getInfos(parameterCode, type);
					if(value != null) {
						return value;
					}
				}
			}
		} 
//		else if (DimensionType.INTERVAL.equals(pType)) {
//			for (InvoiceItem invoiceItem : items) {
//				if (invoiceItem != null && parameterCode != null) {
//					value = invoiceItem.getInfos(parameterCode, type);
//					if(value != null) {
//						return value;
//					}
//				}
//			}
//		}
		return value;
	}
	
	private List<InvoiceItem> findItems(Map<String, List<InvoiceItem>> map, String key, String category, int offset, int length) {
		if(offset == length) {
			return map.get(key);
		}else {
			return map.get(category);
		}
	}



	private InvoiceItemBean findBeans(String category, List<InvoiceItemBean> list) {
		for (InvoiceItemBean invoiceItemBean : list) {
			if(invoiceItemBean.getCategory() != null && invoiceItemBean.getCategory().equals(category)) {
				return invoiceItemBean;
			}
		}
		return null;
	}
	
	private void createdBeanItem(String key, List<InvoiceItemBean> list, Map<String, List<InvoiceItem>> map) {
		if(key != null) {
			if(key.equalsIgnoreCase("RETAIL")) {
				list.add(new InvoiceItemBean(map.get(key), "transaction.fees", null, null, 2));
			}
			else if(key.equalsIgnoreCase("Other fees")) {
				list.add(new InvoiceItemBean(map.get(key), "other.fees", null, null, 3));
			}
			else if(key.equalsIgnoreCase("acquirer fees")) {
				list.add(new InvoiceItemBean(map.get(key), "acquirer.fees", null, null, 1));
			}
			else if(!StringUtils.hasText(key)) {
				list.add(new InvoiceItemBean(map.get(key), key, null, null, 0));
			}
			else {
				list.add(new InvoiceItemBean(map.get(key), key, null, null, 0));
			}
		}
	}
		
	
	
	
	public class InvoiceItemBean {

		private List<InvoiceItemBean> beans;
	    private List<InvoiceItem> items;
	    private String category;
	    private String space = "";
	    public int position;

	    public InvoiceItemBean(List<InvoiceItem> items, String category, List<InvoiceItemBean> beans, String space, int position) {
	    	if(items == null) {
	    		items = new ArrayList<>(0);
	        }
	    	Collections.sort(items, new Comparator<InvoiceItem>() {
				@Override
				public int compare(InvoiceItem o1, InvoiceItem o2) {
					return o1.getDescription().compareToIgnoreCase(o2.getDescription());
				}
			});
	        this.items = items;
	        this.category = category;
	        if(beans == null) {
	        	beans = new ArrayList<>(0);
	        }
	        this.beans = beans;
	        if(space != null) {
	        	this.space = space;
	        }
	        this.position = position;
	    }

		public List<InvoiceItem> getItems() {
			return items;
		}
		public String getCategory() {
			return category;
		}
		
		public List<InvoiceItemBean> getBeans() {
			return beans;
		}
		
		public String getSpace() {
			return space;
		}
	}
	
	@JsonIgnore
	public List<?> getActualItems() {
		return this.itemListChangeHandler.getItems();
	}


	@Override
	public String toString(){
		return getName();
	}
	

}

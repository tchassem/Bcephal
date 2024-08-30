/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;

import jakarta.persistence.Entity;
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
@Entity(name = "InvoiceItem")
@Table(name = "BCP_BILLING_INVOICE_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class InvoiceItem extends AbstractInvoiceItem {

	private static final long serialVersionUID = -7613008455371220958L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_invoice_item_seq")
	@SequenceGenerator(name = "billing_invoice_item_seq", sequenceName = "billing_invoice_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice")
	private Invoice invoice;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "item")
	private List<InvoiceItemInfo> infos;

	@Transient
	private ListChangeHandler<InvoiceItemInfo> infoListChangeHandler;
	
	public InvoiceItem() {
		infos = new ArrayList<InvoiceItemInfo>(0);
		infoListChangeHandler = new ListChangeHandler<InvoiceItemInfo>();
	}
	
	
	public InvoiceItemInfo getInfo(BillingModelParameter parameter) {
		for(InvoiceItemInfo info : getInfoListChangeHandler().getItems()) {
			if(info.getDimensionType() == parameter.getDimensionType() && info.getName() .equals(parameter.getName())) {
				return info;
			}
		}
		return null;
	}		
	
	@JsonIgnore
	public BigDecimal getInfosVatAmount(String parameterCode) {
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

	/**
	 * @param infos the infos to set
	 */
	public void setInfos(List<InvoiceItemInfo> infos) {
		this.infos = infos;
		this.infoListChangeHandler.setOriginalList(infos);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		infos.size();
		this.infoListChangeHandler.setOriginalList(infos);
	}

	@Override
	public Persistent copy() {
		return null;
	}
	
	
	@JsonIgnore
	public List<InvoiceItemInfo> getInfosDataSource() {
		return this.infoListChangeHandler.getItems();
	}
	
	
	
	@JsonIgnore
	public Object getInfos(String parameterCode, String type) {
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
		}else
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
	public InvoiceItem getThis(){
		return this;
	}
	
}

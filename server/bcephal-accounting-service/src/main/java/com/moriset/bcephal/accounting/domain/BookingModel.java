/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.ModelPeriodGranularity;
import com.moriset.bcephal.domain.ModelPeriodSide;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;
import com.moriset.bcephal.utils.SchedulerOption;

import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 
 * @author MORISET-004
 *
 */
@Entity(name = "BookingModel")
@Table(name = "BCP_ACCOUNTING_BOOKING_MODEL")
@Data
@EqualsAndHashCode(callSuper = false)
public class BookingModel extends MainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1216758096297882047L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_model_seq")
	@SequenceGenerator(name = "booking_model_seq", sequenceName = "booking_model_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private boolean active;
	
	@Enumerated(EnumType.STRING) 
	private SchedulerOption schedulerOption;
	
	private String cronExpression;
	
	@Enumerated(EnumType.STRING)
	private ModelPeriodSide periodSide;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date periodFrom;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date periodTo;
	
	@Enumerated(EnumType.STRING)
	private ModelPeriodGranularity periodGranularity;
	
	private boolean includeZeroAmountEntries;
	
	private boolean selectPeriodAtRuntime;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "filter")
	private UniverseFilter filter;
	
	private String fromDynamicPeriod;
	
	private Long fromOperationNumber;
	
	private String fromOperationGranularity;
	
	private String fromOperation;

	private String toDynamicPeriod;
	
	private Long toOperationNumber;
	
	private String toOperationGranularity;
	
	private String toOperation;
			
	private BigDecimal minDeltaAmount;
	
	private BigDecimal maxDeltaAmount;

	
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "booking")
	private List<BookingModelPivot> pivots;

	@Transient
	private ListChangeHandler<BookingModelPivot> pivotListChangeHandler;
	
	
	@Transient
    public SchedulerOption oldSchedulingOption;
    @Transient
    public boolean oldActive;
    @Transient
    public String oldCronExpression;
    
    
    public BookingModel() {
		pivots = new ArrayList<BookingModelPivot>(0);
		pivotListChangeHandler = new ListChangeHandler<BookingModelPivot>();
		setVisibleInShortcut(true);
		minDeltaAmount = BigDecimal.ZERO;
		maxDeltaAmount = BigDecimal.ZERO;
	}

	/**
	 * @param pivots the pivots to set
	 */
	public void setPivots(List<BookingModelPivot> pivots) {
		this.pivots = pivots;
		pivotListChangeHandler.setOriginalList(pivots);
	}
	
	@JsonIgnore
	public List<BookingModelPivot> getActualPivots() {
		return getPivotListChangeHandler().getItems();
	}
    
    
    @PostLoad
	public void initListChangeHandler() {
		pivots.size();
		this.pivotListChangeHandler.setOriginalList(pivots);
	}
    
	@JsonIgnore
	public BookingModel copy() {
		BookingModel copy = new BookingModel();
		copy.setGroup(this.getGroup());
		copy.setVisibleInShortcut(this.isVisibleInShortcut());
		copy.setActive(false);
		copy.setCronExpression(this.getCronExpression());
		copy.setSchedulerOption(this.getSchedulerOption());	
		
		copy.setFromDynamicPeriod(this.getFromDynamicPeriod());		
		copy.setFromOperationNumber(this.getFromOperationNumber());		
		copy.setFromOperationGranularity(this.getFromOperationGranularity());		
		copy.setFromOperation(this.getFromOperation());
		copy.setToDynamicPeriod(this.getToDynamicPeriod());		
		copy.setToOperationNumber(this.getToOperationNumber());		
		copy.setToOperationGranularity(this.getToOperationGranularity());		
		copy.setToOperation(this.getToOperation());	
		
		for (BookingModelPivot pivot : getPivotListChangeHandler().getItems()) {
			BookingModelPivot item = pivot.copy();
			copy.getPivotListChangeHandler().addNew(item);
		}
		
		return copy;
	}
    
}

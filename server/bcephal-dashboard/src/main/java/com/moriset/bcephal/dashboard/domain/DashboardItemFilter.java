/**
 * 
 */
package com.moriset.bcephal.dashboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.filters.AttributeOperator;
import com.moriset.bcephal.domain.filters.FilterVerb;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "DashboardItemFilter")
@Table(name = "BCP_DASHBOARD_ITEM_FILTER")
@Data
@EqualsAndHashCode(callSuper = false)
public class DashboardItemFilter extends Persistent {
	
	private static final long serialVersionUID = -7650081799817325536L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_item_filter_seq")
	@SequenceGenerator(name = "dashboard_item_filter_seq", sequenceName = "dashboard_item_filter_seq	", initialValue = 1,  allocationSize = 1)
	private Long id;
		
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "itemId")
	private DashboardItem itemId;
    
    @Enumerated(EnumType.STRING) 
	private DashboardItemFilterType type;
    
    @Enumerated(EnumType.STRING) 
	private AttributeOperator operator;
        
	private String value;
	
	private int position;
	
	private FilterVerb filterVerb;

	private String openBrackets;

	private String closeBrackets;
    
    
    public DashboardItemFilter(){
    	operator = AttributeOperator.NOT_NULL;
    	filterVerb = FilterVerb.AND;
    	type = DashboardItemFilterType.GROUP;
    }
        
   
	@JsonIgnore
	public DashboardItemFilter copy() {
		DashboardItemFilter copy = new DashboardItemFilter();
		copy.setPosition(this.getPosition());
		copy.setValue(value);
		copy.setOperator(operator);
		copy.setType(type);
		return copy;
	}
	
}

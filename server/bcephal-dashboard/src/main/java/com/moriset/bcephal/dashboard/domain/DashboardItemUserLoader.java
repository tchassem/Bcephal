/**
 * 
 */
package com.moriset.bcephal.dashboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
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

@Entity(name = "DashboardItemUserLoader")
@Table(name = "BCP_DASHBOARD_ITEM_USER_LOADER")
@Data
@EqualsAndHashCode(callSuper = false)
public class DashboardItemUserLoader extends Persistent {
	
	private static final long serialVersionUID = -7650081799817325536L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_item_user_loader_seq")
	@SequenceGenerator(name = "dashboard_item_user_loader_seq", sequenceName = "dashboard_item_user_loader_seq	", initialValue = 1,  allocationSize = 1)
	private Long id;
		
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "itemId")
	private DashboardItem itemId;
        
	private String description;
	
	private Integer position;
	
	private Long userLoaderId;

	private String icon;
    
        
   
	@JsonIgnore
	public DashboardItemUserLoader copy() {
		DashboardItemUserLoader copy = new DashboardItemUserLoader();
		copy.setPosition(this.getPosition());
		copy.setDescription(description);
		copy.setIcon(icon);
		copy.setUserLoaderId(userLoaderId);
		return copy;
	}
	
}

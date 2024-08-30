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

@Entity(name =  "DashboardItemVariable")
@Table(name = "BCP_DASHBOARD_ITEM_VARIABLE")
@Data
@EqualsAndHashCode(callSuper=false)
public class DashboardItemVariable extends Persistent {

	private static final long serialVersionUID = -8093110676540453246L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_item_var_seq")
	@SequenceGenerator(name = "dashboard_item_var_seq", sequenceName = "dashboard_item_var_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "itemId")
	private DashboardItem itemId;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@ManyToOne 
	@JoinColumn(name = "reference")
	private DashboardItemVariableReference reference;
	
	private int position;		
	
	private String name;		
	
	private boolean active;	
	
	public DashboardItemVariable() {
		this.active = true;
    }
		
	@Override
	public DashboardItemVariable copy() {
		DashboardItemVariable p = new DashboardItemVariable();
		p.setName(name);
		p.setActive(active);		
		p.setPosition(position);
		p.setReference(reference != null ? reference.copy() : null);
		return p;
	}	
	
}

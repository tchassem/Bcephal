/**
 * 
 */
package com.moriset.bcephal.dashboard.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;

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
@Entity(name = "DashboardItem")
@Table(name = "BCP_DASHBOARD_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class DashboardItem extends Persistent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2236610489650939610L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_item_seq")
	@SequenceGenerator(name = "dashboard_item_seq", sequenceName = "dashboard_item_seq	", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String name;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dashboard")
	private Dashboard dashboard;
    
    @Enumerated(EnumType.STRING) 
	private DashboardItemType itemType;
    
	private Long itemId;
    
	private String itemName;
	
	private int position;
	
	private String background;
    
	private String foreground;
	
	private String backgroundTitle;
    
	private String foregroundTitle;
	
	private Integer height;
    
	private Integer width;
    
	private boolean showTitleBar;
    
	private boolean visible;
	
    private boolean showBorder;
    
    private boolean newTab;
    
    private String description;
    
    private String linkedLabel;    
    private String linkedFunctionality;    
    private Long linkedFunctionalityId;
    
    
    @JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "itemId")
	private List<DashboardItemFilter> filters;

	@Transient
	private ListChangeHandler<DashboardItemFilter> filterListChangeHandler;
	

    @JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "itemId")
	private List<DashboardItemUserLoader> userLoaders;

	@Transient
	private ListChangeHandler<DashboardItemUserLoader> userLoaderListChangeHandler;
	
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "itemId")
	private List<DashboardItemVariable> variables;

	@Transient
	private ListChangeHandler<DashboardItemVariable> variableListChangeHandler;
        
    
    public DashboardItem(){
    	this.filterListChangeHandler = new ListChangeHandler<>();
    	this.variableListChangeHandler = new ListChangeHandler<>();
    	this.userLoaderListChangeHandler = new ListChangeHandler<>();
    }
    
    
    public void setFilters(List<DashboardItemFilter> filters) {
		this.filters = filters;
		this.filterListChangeHandler.setOriginalList(this.filters);
	}
    
    public void setVariables(List<DashboardItemVariable> variables) {
		this.variables = variables;
		this.variableListChangeHandler.setOriginalList(this.variables);
	}
    
    public void setUserLoaders(List<DashboardItemUserLoader> userLoaders) {
		this.userLoaders = userLoaders;
		this.userLoaderListChangeHandler.setOriginalList(this.userLoaders);
	}

	@PostLoad
	public void initListChangeHandler() {
		this.filters.forEach(x -> { });
		this.filterListChangeHandler.setOriginalList(this.filters);
		
		this.variables.forEach(x -> { });
		this.variableListChangeHandler.setOriginalList(this.variables);
		
		this.userLoaders.forEach(x -> { });
		this.userLoaderListChangeHandler.setOriginalList(this.userLoaders);
	}

    
   
	@JsonIgnore
	public DashboardItem copy() {
		DashboardItem copy = new DashboardItem();
		copy.setName(this.getName());
		copy.setPosition(this.getPosition());
		copy.setItemName(this.getItemName());
		copy.setItemId(this.getItemId());
		copy.setItemType(this.getItemType());	
		copy.setBackground(this.getBackground());
		copy.setForeground(this.getForeground());
		copy.setBackgroundTitle(this.getBackgroundTitle());
		copy.setForegroundTitle(this.getForegroundTitle());
		copy.setWidth(this.getWidth());
		copy.setHeight(this.getHeight());
		copy.setVisible(this.isVisible());
		copy.setShowTitleBar(this.isShowTitleBar());
		copy.setShowBorder(this.isShowBorder());
		copy.setLinkedLabel(this.getLinkedLabel());
		copy.setLinkedFunctionality(this.getLinkedFunctionality());
		copy.setLinkedFunctionalityId(this.getLinkedFunctionalityId());
		for (DashboardItemFilter item : this.filterListChangeHandler.getItems()) {
			DashboardItemFilter child = item.copy();
			copy.filterListChangeHandler.addNew(child);
		}
		for (DashboardItemVariable item : this.variableListChangeHandler.getItems()) {
			DashboardItemVariable child = item.copy();
			copy.variableListChangeHandler.addNew(child);
		}

		for (DashboardItemUserLoader item : this.userLoaderListChangeHandler.getItems()) {
			DashboardItemUserLoader child = item.copy();
			copy.userLoaderListChangeHandler.addNew(child);
		}
		return copy;
	}
	
}

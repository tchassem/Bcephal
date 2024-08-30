/**
 * 
 */
package com.moriset.bcephal.dashboard.domain;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MainObject;

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

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "Dashboard")
@Table(name = "BCP_DASHBOARD")
@Data
@EqualsAndHashCode(callSuper = false)
public class Dashboard extends MainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5280592212491559076L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_seq")
	@SequenceGenerator(name = "dashboard_seq", sequenceName = "dashboard_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private String ProfilId;

	@Enumerated(EnumType.STRING)
	private DashboardLayout layout;

	private boolean published;

	private boolean AllowRefreshFrequency;

	private int RefreshFrequency;

	@Enumerated(EnumType.STRING)
	private TimeUnit RefreshFrequencyUnit;
	
	@ManyToOne
	@JoinColumn(name = "defaultItem")
	private DashboardItem defaultItem;

	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "dashboard")
	private List<DashboardItem> items;

	@Transient
	private ListChangeHandler<DashboardItem> itemsListChangeHandler;

	public Dashboard() {
		this.itemsListChangeHandler = new ListChangeHandler<>();
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<DashboardItem> items) {
		this.items = items;
		this.itemsListChangeHandler.setOriginalList(this.items);
	}

	@PostLoad
	public void initListChangeHandler() {
		this.items.forEach(x -> {
		});
		this.itemsListChangeHandler.setOriginalList(this.items);
	}

	@JsonIgnore
	public Dashboard copy() {
		Dashboard copy = new Dashboard();
		copy.setGroup(this.getGroup());
		copy.setName(this.getName());
		copy.setProfilId(this.getProfilId());
		copy.setLayout(this.getLayout());
		copy.setAllowRefreshFrequency(isAllowRefreshFrequency());
		copy.setRefreshFrequency(getRefreshFrequency());
		copy.setRefreshFrequencyUnit(getRefreshFrequencyUnit());
		for (DashboardItem item : this.itemsListChangeHandler.getItems()) {
			DashboardItem child = item.copy();
			copy.itemsListChangeHandler.addNew(child);
		}
		return copy;
	}

}

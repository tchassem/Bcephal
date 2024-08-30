package com.moriset.bcephal.loader.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import lombok.ToString;

@Entity(name = "UserLoader")
@Table(name = "BCP_USER_LOADER")
@Data
@EqualsAndHashCode(callSuper = false)
public class UserLoader extends MainObject {

	private static final long serialVersionUID = 8966032649263846234L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_loader_seq")
	@SequenceGenerator(name = "user_loader_seq", sequenceName = "user_loader_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private UserLoaderTreatment treatment;
	
	private String repository;
	
	private Long fileLoaderId;
	
	@Transient
	private String fileLoaderName;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name="menu")
	private UserLoaderMenu menu;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "loader")
	private List<UserLoaderScheduler> schedulers;

	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
	private ListChangeHandler<UserLoaderScheduler> schedulerListChangeHandler;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "loader")
	private List<UserLoaderCondition> conditions;

	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
	private ListChangeHandler<UserLoaderCondition> conditionListChangeHandler;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "loader")
	private List<UserLoaderController> controllers;

	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
	private ListChangeHandler<UserLoaderController> controllerListChangeHandler;
	
	
	public UserLoader() {
		this.treatment = UserLoaderTreatment.LOADER;
		this.schedulers = new ArrayList<>();
		this.schedulerListChangeHandler = new ListChangeHandler<>();
		this.conditions = new ArrayList<>();
		this.conditionListChangeHandler = new ListChangeHandler<>();
		this.controllers = new ArrayList<>();
		this.controllerListChangeHandler = new ListChangeHandler<>();
	}
	
	public void setSchedulers(List<UserLoaderScheduler> schedulers) {
		this.schedulers = schedulers;
		this.schedulerListChangeHandler.setOriginalList(schedulers);
	}
	
	public void setConditions(List<UserLoaderCondition> conditions) {
		this.conditions = conditions;
		this.conditionListChangeHandler.setOriginalList(conditions);
	}
	
	public void setControllers(List<UserLoaderController> controllers) {
		this.controllers = controllers;
		this.controllerListChangeHandler.setOriginalList(controllers);
	}
	
	public void sortConditions() {
		List<UserLoaderCondition> conditions = this.conditionListChangeHandler.getItems();
		Collections.sort(conditions, new Comparator<UserLoaderCondition>() {
			@Override
			public int compare(UserLoaderCondition o1, UserLoaderCondition o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		setConditions(conditions);
	}
	
	public void sortSchedulers() {
		List<UserLoaderScheduler> schedulers = this.schedulerListChangeHandler.getItems();
		Collections.sort(schedulers, new Comparator<UserLoaderScheduler>() {
			@Override
			public int compare(UserLoaderScheduler o1, UserLoaderScheduler o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		setSchedulers(schedulers);
	}
	
	public void sortControllers() {
		List<UserLoaderController> controllers = this.controllerListChangeHandler.getItems();
		Collections.sort(controllers, new Comparator<UserLoaderController>() {
			@Override
			public int compare(UserLoaderController o1, UserLoaderController o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		setControllers(controllers);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		this.schedulers.size();
		this.schedulerListChangeHandler.setOriginalList(schedulers);
		this.conditions.size();
		this.conditionListChangeHandler.setOriginalList(conditions);
		this.controllers.size();
		this.controllerListChangeHandler.setOriginalList(controllers);
	}
	
	@Override
	public UserLoader copy() {
		UserLoader copy = new UserLoader();
		copy.setTreatment(treatment);
		copy.setMenu(menu != null ? menu.copy() : null);		
		
		for(UserLoaderScheduler item : schedulerListChangeHandler.getItems()) {
			copy.getSchedulerListChangeHandler().addNew(item.copy());
		}
		for(UserLoaderCondition item : conditionListChangeHandler.getItems()) {
			copy.getConditionListChangeHandler().addNew(item.copy());
		}
		for(UserLoaderController item : controllerListChangeHandler.getItems()) {
			copy.getControllerListChangeHandler().addNew(item.copy());
		}
		
		return copy;
	}
	
	
	
}

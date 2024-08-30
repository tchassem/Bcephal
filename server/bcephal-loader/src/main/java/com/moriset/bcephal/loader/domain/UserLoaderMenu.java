package com.moriset.bcephal.loader.domain;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "UserLoaderMenu")
@Table(name = "BCP_USER_LOADER_MENU")
@Data
@EqualsAndHashCode(callSuper = false)
public class UserLoaderMenu extends Persistent {
	
	private static final long serialVersionUID = -7463135907878928640L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_loader_menu_seq")
	@SequenceGenerator(name = "user_loader_menu_seq", sequenceName = "user_loader_menu_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
		
	private Long loaderId;
	
	private String parent;
	
	private String icon;
	
	private String parentIcon;
	private String newMenuIcon;
	private String listMenuIcon;
	
	private String name;
	
	private int position;
	
	private boolean active;
	
	private boolean hasNewMenu;
	private String newMenuName;
	
	private boolean hasListMenu;
	private String listMenuName;
	
	public UserLoaderMenu() {
		this.active = true;
		this.hasNewMenu = true;
		this.hasListMenu = true;
		this.newMenuIcon = "bi-plus";
		this.listMenuIcon = "bi-list";
	}
	
	@Override
	public UserLoaderMenu copy() {
		UserLoaderMenu copy = new UserLoaderMenu();
		copy.setName(getName());
		copy.setActive(isActive());
		copy.setParent(parent);
		copy.setPosition(position);
		copy.setHasListMenu(hasListMenu);
		copy.setHasNewMenu(hasNewMenu);
		copy.setIcon(icon);
		copy.setParentIcon(parentIcon);
		copy.setNewMenuIcon(newMenuIcon);
		copy.setListMenuIcon(listMenuIcon);
		copy.setListMenuName(listMenuName);
		copy.setNewMenuName(newMenuName);
		return copy;
	}
	
	public void initialize(String name) {
		this.active = true;		
		this.parent = null;	
		this.position = 50;
		this.hasNewMenu = true;
		this.hasListMenu = true;
		this.newMenuName = "New " + name;
		this.listMenuName = "List " + name;	
	}
	
	
	
	@JsonIgnore
	public void setCaption(String caption) {
		
	}
	
	
	
	@JsonIgnore
	public Set<String> getLabelCodes() {
		Set<String> labels = new HashSet<>();
		if(StringUtils.hasText(getName())) {
			labels.add(getName());
		}
		if(StringUtils.hasText(getNewMenuName())) {
			labels.add(getNewMenuName());
		}
		if(StringUtils.hasText(getListMenuName())) {
			labels.add(getListMenuName());
		}
		return labels;
	}
	
	public void setLabels(Map<String, String> labels) {
		if(StringUtils.hasText(getName())) {
			String value = labels.get(getName());
			if(StringUtils.hasText(value)) {
				setCaption(value);
			}
		}
		if(StringUtils.hasText(getNewMenuName())) {
			String value = labels.get(getNewMenuName());
			if(StringUtils.hasText(value)) {
				setNewMenuName(value);
			}
		}
		if(StringUtils.hasText(getListMenuName())) {
			String value = labels.get(getListMenuName());
			if(StringUtils.hasText(value)) {
				setListMenuName(value);
			}
		}
	}

	public void initializeName(String name) {
		setName(name);
		setNewMenuName("New");
		setListMenuName("List");
	}
	
}

package com.moriset.bcephal.grid.domain.form;

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

@Entity(name = "FormModelMenu")
@Table(name = "BCP_FORM_MODEL_MENU")
@Data
@EqualsAndHashCode(callSuper = false)
public class FormModelMenu extends Persistent {
	
	private static final long serialVersionUID = -2734236572805120964L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "form_model_menu_seq")
	@SequenceGenerator(name = "form_model_menu_seq", sequenceName = "form_model_menu_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
		
	private Long modelId;
	
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
	
	public FormModelMenu() {
		this.active = true;
		this.hasNewMenu = true;
		this.hasListMenu = true;
		this.newMenuIcon = "bi-plus";
		this.listMenuIcon = "bi-list";
	}
	
	@Override
	public FormModelMenu copy() {
		FormModelMenu copy = new FormModelMenu();
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

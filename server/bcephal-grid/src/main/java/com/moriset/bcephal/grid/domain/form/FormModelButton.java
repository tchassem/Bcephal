package com.moriset.bcephal.grid.domain.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;

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

@Entity(name = "FormModelButton")
@Table(name = "BCP_FORM_MODEL_BUTTON")
@Data
@EqualsAndHashCode(callSuper = false)
public class FormModelButton extends Persistent {
	
	private static final long serialVersionUID = 1892547605374506924L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "form_model_button_seq")
	@SequenceGenerator(name = "form_model_button_seq", sequenceName = "form_model_button_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude		
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "modelId")
	private FormModel modelId;	
		
	private String icon;
	
	private String style;
	
	private int position;
	
	private boolean active;	
	
	private boolean allowUserConfirmation;
	
	private String userConfirmationMessage;

	private String name;
	
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "buttonId")
	private List<FormModelButtonAction> actions;

	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
	private ListChangeHandler<FormModelButtonAction> actionListChangeHandler;
	
	
	public FormModelButton() {
		this.active = true;
		this.actions = new ArrayList<>();
		this.actionListChangeHandler = new ListChangeHandler<>();
	}
	
	public void setActions(List<FormModelButtonAction> actions) {
		this.actions = actions;
		this.actionListChangeHandler.setOriginalList(actions);
	}
	
	public void sortActions() {
		Collections.sort(this.actions, new Comparator<FormModelButtonAction>() {
			@Override
			public int compare(FormModelButtonAction value1, FormModelButtonAction value2) {
				return value1.getPosition() - value2.getPosition();
			}
		});
	}

	@PostLoad
	public void initListChangeHandler() {
		this.actions.size();
		this.actionListChangeHandler.setOriginalList(actions);
	}
		
	@JsonIgnore
	public Set<String> getLabelCodes() {
		Set<String> labels = new HashSet<>();
		if(StringUtils.hasText(getName())) {
			labels.add(getName());
		}
		return labels;
	}
	
	public void setLabels(Map<String, String> labels) {
		if(StringUtils.hasText(getName())) {
			String value = labels.get(getName());
			if(StringUtils.hasText(value)) {
				setName(value);
			}
		}
	}

	@Override
	public FormModelButton copy() {
		FormModelButton copy = new FormModelButton();
		copy.setIcon(icon);
		copy.setStyle(style);
		copy.setPosition(position);
		copy.setActive(active);
		copy.setAllowUserConfirmation(allowUserConfirmation);
		copy.setUserConfirmationMessage(userConfirmationMessage);
		copy.setName(name);
		for(FormModelButtonAction action : actionListChangeHandler.getItems()) {
			copy.getActionListChangeHandler().addNew(action.copy());
		}
		return copy;
	}
}

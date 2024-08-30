package com.moriset.bcephal.grid.domain.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.filters.UniverseFilter;

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

@Entity(name = "FormModel")
@Table(name = "BCP_FORM_MODEL")
@Data
@EqualsAndHashCode(callSuper = false)
public class FormModel extends MainObject {
	
	private static final long serialVersionUID = 882605371749154813L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "form_model_seq")
	@SequenceGenerator(name = "form_model_seq", sequenceName = "form_model_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private DataSourceType dataSourceType;
	
	private Long dataSourceId;
	
	@Transient
	private String dataSourceName;
	
	private boolean published;
	
	public boolean allowEditor;
	private String editorTitle;
	
	public boolean allowBrowser;
	private String browserTitle;
		
	public boolean showFieldLabel;
	public int fieldLabelSize;
	public int fieldEditorSize;
	
	public int maxFieldPerRow;
	public int maxFieldPerCol;
	
	public Boolean allowValidation;
	
	private boolean detailsInSeparatedTab;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name="menu")
	private FormModelMenu menu;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name="userFilter")
	private UniverseFilter userFilter;	
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name="adminFilter")
	private UniverseFilter adminFilter;
	
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "modelId")
	private List<FormModelField> fields;

	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
	private ListChangeHandler<FormModelField> mainFieldListChangeHandler;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
	private ListChangeHandler<FormModelField> detailsFieldListChangeHandler;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "modelId")
	private List<FormModelButton> buttons;

	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
	private ListChangeHandler<FormModelButton> buttonListChangeHandler;

	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
	private String Code;
	
	public FormModel (){
		this.dataSourceType = DataSourceType.UNIVERSE;
		this.fields = new ArrayList<>();
		this.buttons = new ArrayList<>();
		this.mainFieldListChangeHandler = new ListChangeHandler<>();
		this.detailsFieldListChangeHandler = new ListChangeHandler<>();
		this.buttonListChangeHandler = new ListChangeHandler<>();
	}
	
	public void setFields(List<FormModelField> fields) {
		this.fields = fields;
		initFields();
	}
	
	public void setButtons(List<FormModelButton> buttons) {
		this.buttons = buttons;
		this.buttonListChangeHandler.setOriginalList(buttons);
	}
	
	public boolean isAllowValidation() {
		if(allowValidation == null) {
			allowValidation = false;
		}
		return allowValidation;
	}
	
	@PostLoad
	public void initListChangeHandler() {
		initFields();
		this.buttons.size();
		this.buttonListChangeHandler.setOriginalList(buttons);
	}
	
	private void initFields() {
		this.mainFieldListChangeHandler.setOriginalList(new ArrayList<>());
		this.detailsFieldListChangeHandler.setOriginalList(new ArrayList<>());
		this.fields.forEach(field -> {
			if(field.isMain()) {
				this.mainFieldListChangeHandler.getOriginalList().add(field);
			}
			else {
				this.detailsFieldListChangeHandler.getOriginalList().add(field);
			}
		});
	}
	
	@JsonIgnore
	public List<FormModelField> getMainFieldSortedFields() {
		List<FormModelField> fields = this.getMainFieldListChangeHandler().getItems();
		Collections.sort(fields, new Comparator<FormModelField>() {
			@Override
			public int compare(FormModelField o1, FormModelField o2) {
				return o1.getColPosition() - o2.getColPosition();
			}
		});
		return fields;
	}
	
	@JsonIgnore
	public List<FormModelField> getDetailsFieldSortedFields() {
		List<FormModelField> fields = this.getDetailsFieldListChangeHandler().getItems();
		Collections.sort(fields, new Comparator<FormModelField>() {
			@Override
			public int compare(FormModelField o1, FormModelField o2) {
				return o1.getColPosition() - o2.getColPosition();
			}
		});
		return fields;
	}
	
	public void sortFields() {
		Collections.sort(this.fields, new Comparator<FormModelField>() {
			@Override
			public int compare(FormModelField value1, FormModelField value2) {
				return value1.getPosition() - value2.getPosition();
			}
		});
	}
	
	public void sortButtons() {
		Collections.sort(this.buttons, new Comparator<FormModelButton>() {
			@Override
			public int compare(FormModelButton value1, FormModelButton value2) {
				return value1.getPosition() - value2.getPosition();
			}
		});
	}
	
	@JsonIgnore
	public boolean hasDetails() {
		return getDetailsFieldListChangeHandler().getItems().size() > 0;
	}
	
	@JsonIgnore
	public Set<String> getLabelCodes() {
		Set<String> labels = new HashSet<>();
		if(menu != null) {
			labels.addAll(menu.getLabelCodes());
		}
		for(FormModelButton button : getButtonListChangeHandler().getItems()) {
			labels.addAll(button.getLabelCodes());
		}
		for(FormModelField field : getFields()) {
			labels.addAll(field.getLabelCodes());
		}
		return labels;
	}
	
	public void setLabels(Map<String, String> labels) {
		if(menu != null) {
			menu.setLabels(labels);
		}
		for(FormModelButton button : getButtonListChangeHandler().getItems()) {
			button.setLabels(labels);
		}
		for(FormModelField field : getFields()) {
			field.setLabels(labels);
		}
	}
	
	@Override
	public FormModel copy() {
		FormModel copy = new FormModel();
		
		copy.setDataSourceType(dataSourceType);
		copy.setDataSourceId(dataSourceId);
		copy.setDataSourceName(dataSourceName);
		copy.setPublished(published);		
		copy.setAllowEditor(allowEditor);
		copy.setEditorTitle(editorTitle);		
		copy.setAllowBrowser(allowBrowser);
		copy.setBrowserTitle(browserTitle);			
		copy.setShowFieldLabel(showFieldLabel);
		copy.setFieldLabelSize(fieldLabelSize);
		copy.setFieldEditorSize(fieldEditorSize);		
		copy.setMaxFieldPerRow(maxFieldPerRow);
		copy.setMaxFieldPerCol(maxFieldPerCol);		
		copy.setAllowValidation(allowValidation);		
		copy.setDetailsInSeparatedTab(detailsInSeparatedTab);
		copy.setMenu(menu != null ? menu.copy() : null);
		copy.setUserFilter(userFilter != null ? userFilter.copy() : null);
		copy.setAdminFilter(adminFilter != null ? adminFilter.copy() : null);
		for(FormModelField field : mainFieldListChangeHandler.getItems()) {
			copy.getMainFieldListChangeHandler().addNew(field.copy());
		}
		for(FormModelField field : detailsFieldListChangeHandler.getItems()) {
			copy.getDetailsFieldListChangeHandler().addNew(field.copy());
		}
		for(FormModelButton button : buttonListChangeHandler.getItems()) {
			copy.getButtonListChangeHandler().addNew(button.copy());
		}		
		return copy;
	}

	

}

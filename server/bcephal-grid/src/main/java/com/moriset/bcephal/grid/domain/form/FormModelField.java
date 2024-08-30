package com.moriset.bcephal.grid.domain.form;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.HorizontalAlignment;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionFormat;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.PeriodGrouping;
import com.moriset.bcephal.domain.filters.PeriodValue;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "FormModelField")
@Table(name = "BCP_FORM_MODEL_FIELD")
@Data
@EqualsAndHashCode(callSuper = false)
public class FormModelField extends Persistent {

	private static final long serialVersionUID = 1238556081762045075L;
	
	private static final String VALUE_SEPARATOR = "_-;-_";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "form_model_field_seq")
	@SequenceGenerator(name = "form_model_field_seq", sequenceName = "form_model_field_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "modelId")
	private FormModel modelId;
	
	@Enumerated(EnumType.STRING)
	private FormModelFieldCategory category;
	
	@Enumerated(EnumType.STRING)
	private FormModelFieldType type;
	
	@Enumerated(EnumType.STRING)
	private FormModelFieldNature nature;
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	
	@Enumerated(EnumType.STRING)
	private HorizontalAlignment alignment;
	
	private Long dimensionId;
	
	private String dimensionFunction;
	
	private String name;
		
	private int rowPosition;
	
	private int colPosition;
	
	private int position;

	private boolean showLabel;
	
	private String label;
	
	private String description;
	
	private int labelSize;
	
	private int editorSize;
	
	private boolean mandatory;
		
	private boolean visible;
	
	private boolean readOnly;
	
	private boolean valueGeneratedBySystem;
	
	private boolean key;
	
	private boolean showColumnInBrowser;
	
	private Integer backgroundColor;

	private Integer foregroundColor;
	
	private Integer width;
	
	private String fixedType;
		
	private String defaultStringValue;
	
	private BigDecimal defaultDecimalValue;
	
	@Embedded	
	private PeriodValue defaultDateValue;
	
	private boolean applyDefaultValueIfCellEmpty;
	
	private boolean applyDefaultValueToFutureLine;
	
	private Boolean allowDuplication;
	
	@Enumerated(EnumType.STRING) 
	private FormModelFieldDuplicationValue duplicationValue;
		
	@Embedded	
	private DimensionFormat format;
	
	@Enumerated(EnumType.STRING) 
	private PeriodGrouping groupBy;
	
	@Column(name="spot")
	private Long objectId;
	
	@JsonIgnore
	private String valuesImpl;
	
	private boolean selectAllValues;
	
	@Transient
	private List<String> values;	
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@ManyToOne 
	@JoinColumn(name = "reference")
	private FormModelFieldReference reference;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
//	@ManyToOne 
//	@JoinColumn(name = "spot_id")
	private FormModelSpot formModelSpot;
	
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "fieldId")
	private List<FormModelFieldValidationItem> validationItems;

	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient 
	private ListChangeHandler<FormModelFieldValidationItem> validationItemListChangeHandler;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "parentId")
	private List<FormModelFieldCalculateItem> calculateItems;

	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient 
	private ListChangeHandler<FormModelFieldCalculateItem> calculateItemListChangeHandler;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "parentId")
	private List<FormModelFieldConcatenateItem> concatenateItems;

	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient 
	private ListChangeHandler<FormModelFieldConcatenateItem> concatenateItemListChangeHandler;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient @JsonIgnore
	private DataSourceType dataSourceType;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient @JsonIgnore
	private Long dataSourceId;
	
	
	public FormModelField() {
		this.visible = true;	
		this.mandatory = false;
		this.format = new DimensionFormat();
		this.validationItems = new ArrayList<>();
		this.validationItemListChangeHandler = new ListChangeHandler<>();
		this.calculateItems = new ArrayList<>();
		this.calculateItemListChangeHandler = new ListChangeHandler<>();
		this.concatenateItems = new ArrayList<>();
		this.concatenateItemListChangeHandler = new ListChangeHandler<>();
		defaultDateValue = new PeriodValue();
		dataSourceType = DataSourceType.UNIVERSE;
		values= new ArrayList<>();
		selectAllValues = true;
		allowDuplication = true;
		duplicationValue = FormModelFieldDuplicationValue.CURRENT_VALUE;
	}
	
	public boolean isAllowDuplication() {
		if(allowDuplication == null) {
			allowDuplication = true;
		}
		return allowDuplication;
	}
	
	public boolean getAllowDuplication() {
		return isAllowDuplication();
	}
	
	public FormModelFieldDuplicationValue getDuplicationValue() {
		if(duplicationValue == null) {
			duplicationValue = FormModelFieldDuplicationValue.CURRENT_VALUE;
		}
		return duplicationValue;
	}
	
	
	public void setValidationItems(List<FormModelFieldValidationItem> validationItems) {
		this.validationItems = validationItems;
		this.validationItemListChangeHandler.setOriginalList(validationItems);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		this.validationItems.size();
		this.validationItemListChangeHandler.setOriginalList(validationItems);
		if(this.defaultDateValue == null) {
			this.defaultDateValue = new PeriodValue();
		}
		this.calculateItems.size();
		this.calculateItemListChangeHandler.setOriginalList(calculateItems);
		this.concatenateItems.size();
		this.concatenateItemListChangeHandler.setOriginalList(concatenateItems);
		
		this.values = new ArrayList<>();
		if(StringUtils.hasText(this.valuesImpl)) {
			String[] vals = this.valuesImpl.split(VALUE_SEPARATOR);
			for(String val : vals) {
				this.values.add(val);
			}
			this.values.sort(new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
		}		
	}
	
	@PrePersist
	public void initValueImpl() {
		this.valuesImpl = String.join(VALUE_SEPARATOR, this.values);
//		String coma = "";
//		for(String val : this.values) {
//			this.valuesImpl += coma + val;
//			coma = VALUE_SEPARATOR;
//		}
	}
	
	public void seCalculateItems(List<FormModelFieldCalculateItem> calculateItems) {
		this.calculateItems = calculateItems;
		this.calculateItemListChangeHandler.setOriginalList(calculateItems);
	}
	
		
	public void setConcatenateItems(List<FormModelFieldConcatenateItem> concatenateItems) {
		this.concatenateItems = concatenateItems;
		this.concatenateItemListChangeHandler.setOriginalList(concatenateItems);
	}
		
	@JsonIgnore
	public boolean isDetails() {
		return getCategory() != null && getCategory().isDetails();
	}
	
	@JsonIgnore
	public boolean isMain() {
		return getCategory() == null || getCategory().isMain();
	}
	
	@JsonIgnore
	public boolean isEdition() {
		return getType() == null || getType().isEdition();
	}
	
	@JsonIgnore
	public boolean isHasLabel() {
		return getType() != null && getType().isLabel();
	}
	
	@JsonIgnore
	public boolean isReference_() {
		return getType() != null && getType().isReference();
	}
	
	@JsonIgnore
	public boolean isSelection() {
		return getType() != null && getType().isSelection();
	}
	
	@JsonIgnore
	public boolean isSelectionEdition() {
		return getType() != null && getType().isSelectionEdition();
	}
	
	@JsonIgnore
	public boolean isInput() {
		return getNature() == null || getNature().isInput();
	}
	
	@JsonIgnore
	public boolean isReport() {
		return getNature() != null && getNature().isReport();
	}
	
	@JsonIgnore
	public boolean isCalculated() {
		return getNature() != null && getNature().isCalculated();
	}
	
	@JsonIgnore
	public boolean isConcatenated() {
		return getNature() != null && getNature().isConcatenated();
	}
	
	@JsonIgnore
	public boolean isSpot() {
		return getNature() != null && getNature().isSpot();
	}
	
	@JsonIgnore
	public boolean isTypeSpot() {
		return getType() != null && getType().isSpot();
	}
	
	@JsonIgnore
	public boolean isAttribute() {
		return getDimensionType() == null || getDimensionType().isAttribute();
	}

	@JsonIgnore
	public boolean isPeriod() {
		return getDimensionType() != null && getDimensionType().isPeriod();
	}
	
	@JsonIgnore
	public boolean isMeasure() {
		return getDimensionType() != null && getDimensionType().isMeasure();
	}
	
	@JsonIgnore
	public boolean isCalculatedMeasure() {
		return getDimensionType() != null && getDimensionType().isCalculatedMeasure();
	}
	
	

	@JsonIgnore
	public String getUniverseTableColumnName() {
		if (isAttribute()) {
			return new Attribute(getDimensionId(), getName(), getDataSourceType(), getDataSourceId()).getUniverseTableColumnName();
		} else if (isMeasure()) {
			return new Measure(getDimensionId(), getName(), getDataSourceType(), getDataSourceId()).getUniverseTableColumnName();
		} else if (isPeriod()) {
			return new Period(getDimensionId(), getName(), getDataSourceType(), getDataSourceId()).getUniverseTableColumnName();
		}
		return null;
	}

	@JsonIgnore
	public String getUniverseTableColumnType() {
		if (isAttribute()) {
			return new Attribute().getUniverseTableColumnType();
		} else if (isMeasure()) {
			return new Measure().getUniverseTableColumnType();
		} else if (isPeriod()) {
			return new Period().getUniverseTableColumnType();
		}
		return null;
	}
	
	
	public void setDefaultDateValue(PeriodValue dateValue) {
		this.defaultDateValue = dateValue;
		if(this.defaultDateValue == null) {
			this.defaultDateValue = new PeriodValue();
		}
	}
	
	public Object buildDefaultValue() {
		if(isAttribute()) {
			return getDefaultStringValue();
		}
		if(isMeasure()) {
			return getDefaultDecimalValue();
		}
		if(isPeriod() && getDefaultDateValue() != null) {
			Date date = getDefaultDateValue().buildDynamicDate();
			return date != null ? getDefaultDateValue().asDbString(date) : null;
		}
		return null;
	}

	@JsonIgnore
	public Set<String> getLabelCodes() {
		Set<String> labels = new HashSet<>();
		if(StringUtils.hasText(getLabel())) {
			labels.add(getLabel());
		}
		if(StringUtils.hasText(getDescription())) {
			labels.add(getDescription());
		}
		for(FormModelFieldValidationItem item : getValidationItemListChangeHandler().getItems()) {
			labels.addAll(item.getLabelCodes());
		}
		return labels;
	}
	
	public void setLabels(Map<String, String> labels) {
		if(StringUtils.hasText(getLabel())) {
			String value = labels.get(getLabel());
			if(StringUtils.hasText(value)) {
				setLabel(value);
			}
		}
		if(StringUtils.hasText(getDescription())) {
			String value = labels.get(getDescription());
			if(StringUtils.hasText(value)) {
				setDescription(value);
			}
		}
		for(FormModelFieldValidationItem item : getValidationItemListChangeHandler().getItems()) {
			item.setLabels(labels);
		}
	}


	@Override
	public FormModelField copy() {
		FormModelField copy = new FormModelField();
		copy.setCategory(category);
		copy.setType(type);
		copy.setNature(nature);
		copy.setDimensionType(dimensionType);
		copy.setDimensionId(dimensionId);
		copy.setDimensionFunction(dimensionFunction);
		copy.setName(name);
		copy.setRowPosition(rowPosition);
		copy.setColPosition(colPosition);
		copy.setPosition(position);
		copy.setShowLabel(showLabel);
		copy.setLabel(label);
		copy.setDescription(description);
		copy.setLabelSize(labelSize);
		copy.setEditorSize(editorSize);
		copy.setMandatory(mandatory);
		copy.setVisible(visible);
		copy.setReadOnly(readOnly);
		copy.setValueGeneratedBySystem(valueGeneratedBySystem);
		copy.setKey(key);
		copy.setShowColumnInBrowser(showColumnInBrowser);
		copy.setBackgroundColor(backgroundColor);
		copy.setForegroundColor(foregroundColor);
		copy.setWidth(width);
		copy.setFixedType(fixedType);
		copy.setDefaultStringValue(defaultStringValue);
		copy.setDefaultDecimalValue(defaultDecimalValue);		
		copy.setDefaultDateValue(defaultDateValue.copy());
		copy.setApplyDefaultValueIfCellEmpty(applyDefaultValueIfCellEmpty);
		copy.setApplyDefaultValueToFutureLine(applyDefaultValueToFutureLine);
		copy.setAllowDuplication(allowDuplication);		
		copy.setDuplicationValue(duplicationValue);
		copy.setFormat(format.copy());		
		copy.setGroupBy(groupBy);		
		copy.setObjectId(objectId);	
		copy.setFormModelSpot(getFormModelSpot() != null ? getFormModelSpot().copy() : null);
		copy.setValuesImpl(valuesImpl);		
		copy.setSelectAllValues(selectAllValues);
		copy.setValues(values);
		
//		@ToString.Exclude @EqualsAndHashCode.Exclude	
//		@ManyToOne 
//		@JoinColumn(name = "reference")
//		private FormModelFieldReference reference;
//		
//		@ToString.Exclude @EqualsAndHashCode.Exclude	
//		@JsonIgnore
//		@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "fieldId")
//		private List<FormModelFieldValidationItem> validationItems;
//
//		@ToString.Exclude @EqualsAndHashCode.Exclude	
//		@Transient 
//		private ListChangeHandler<FormModelFieldValidationItem> validationItemListChangeHandler;
//		
//		@ToString.Exclude @EqualsAndHashCode.Exclude	
//		@JsonIgnore
//		@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "parentId")
//		private List<FormModelFieldCalculateItem> calculateItems;
//
//		@ToString.Exclude @EqualsAndHashCode.Exclude	
//		@Transient 
//		private ListChangeHandler<FormModelFieldCalculateItem> calculateItemListChangeHandler;
//		
//		@JsonIgnore
//		@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "parentId")
//		private List<FormModelFieldConcatenateItem> concatenateItems;
//
//		@ToString.Exclude @EqualsAndHashCode.Exclude	
//		@Transient 
//		private ListChangeHandler<FormModelFieldConcatenateItem> concatenateItemListChangeHandler;
		
		return copy;
	}
	
}

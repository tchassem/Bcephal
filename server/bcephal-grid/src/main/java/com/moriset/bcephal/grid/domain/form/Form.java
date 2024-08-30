package com.moriset.bcephal.grid.domain.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Form extends MainObject {

	private static final long serialVersionUID = -7641882946022582059L;

	private Long id;

	private Long modelId;

	private DataSourceType dataSourceType;

	private Long dataSourceId;

	private Object[] mainData;

	private List<Object[]> detailsData;

	private List<Long> deleteddRows;

	@Transient
	private String dataSourceName;

	public Form() {
		this.dataSourceType = DataSourceType.UNIVERSE;
		this.detailsData = new ArrayList<>();
		this.deleteddRows = new ArrayList<>();
		setCreationDate(new Timestamp(System.currentTimeMillis()));
		setModificationDate(getCreationDate());
	}

	@Override
	public Form copy() {
		Form copy = new Form();
		copy.setId(null);
		copy.setModelId(modelId);
		copy.setDataSourceType(dataSourceType);
		copy.setDataSourceId(dataSourceId);	
		
		if(mainData != null && mainData.length > 0) {
			Object[] result = Arrays.copyOf(mainData, mainData.length);
			if(result != null && result.length > 0) {
				result[result.length-1] = null;
			}
			copy.setMainData(result);
		}
		if(detailsData != null) {
			for(Object[] data : detailsData) {
				Object[] result = Arrays.copyOf(data, data.length);
				if(result != null && result.length > 0) {
					result[result.length-1] = null;
				}
				copy.getDetailsData().add(result);
			}
		}
		copy.setDataSourceName(dataSourceName);
		return copy;
	}
	
	public Form builCopy(FormModel model) {
		Form copy = copy();		
		if(copy.getMainData() != null && copy.getMainData().length > 0) {
			for (FormModelField field : model.getFields()) {
				if(field.getCategory() == FormModelFieldCategory.MAIN) {
					Object value = buildDuplicationValue(field, copy.getMainData()[field.getPosition()]);
					copy.getMainData()[field.getPosition()] = value;
				}				
			}
		}
		for(Object[] data : copy.getDetailsData()) {
			if(data!= null && data.length > 0) {
				for (FormModelField field : model.getFields()) {
					if(field.getCategory() == FormModelFieldCategory.DETAILS) {
						Object value = buildDuplicationValue(field, data[field.getPosition()]);
						data[field.getPosition()] = value;
					}				
				}
			}
		}
		return copy;
	}
	
	private Object buildDuplicationValue(FormModelField field, Object currentValue) {
		if(field.isAllowDuplication()) {
			if(field.getDuplicationValue() == FormModelFieldDuplicationValue.DEFAULT_VALUE) {
				return field.buildDefaultValue();
			}
			return currentValue;
		}
		return null;
	}
	
	public List<Long> buildIds() {
		List<Long> ids = new ArrayList<>();
		if(detailsData != null && detailsData.size() > 0) {
			for(Object[] datas : detailsData) {
				Long id = buildId(datas);
				if(id != null) {
					ids.add(id);
				}
			}
		}
		else if(mainData != null) {
			Long id = buildId(mainData);
			if(id != null) {
				ids.add(id);
			}
		}
		return ids;
	}
	
	private Long buildId(Object[] datas) {
		if(datas == null || datas.length == 0 || datas[datas.length - 1] == null) {
			return null;
		}
		Long id = Long.valueOf(datas[datas.length - 1].toString());
		return id;
	}

	public String buildDeleteSql(FormModel formModel, List<Long> ids) {
		String sql = "DELETE FROM " + UniverseParameters.UNIVERSE_TABLE_NAME + " WHERE ID IN (";
		if (formModel.getDataSourceType() == DataSourceType.MATERIALIZED_GRID) {
			sql = "DELETE FROM " + new MaterializedGrid(formModel.getDataSourceId()).getMaterializationTableName() + " WHERE ID IN (";
		}
		String coma = "";
		for (Long id : ids) {
			sql += coma + id;
			coma = ",";
		}
		sql += ")";
		return sql;
	}
	

	public String buildInsertSql(Object[] mainDatas, Object[] detailDatas, FormModel formModel, MaterializedGrid grid,
			String username) {
		if (formModel.getDataSourceType() == DataSourceType.MATERIALIZED_GRID) {
			String sql = "INSERT INTO " + grid.getMaterializationTableName() + " (";
			String values = " VALUES(";
			String coma = "";
			for (FormModelField field : formModel.getFields()) {
				String col = buildDbColName(field);
				if (StringUtils.hasText(col)) {
					Object[] data = field.getCategory() == FormModelFieldCategory.DETAILS ? detailDatas : mainDatas;
					Object value = data[field.getPosition()];
					if (value != null) {
						sql += coma + col;
						values += coma + getValueAsString(field, value);
						coma = ",";
					}
				}
			}
			sql += ")" + values + ") returning ID";
			return sql;
		} else {
			String sql = "INSERT INTO " + UniverseParameters.UNIVERSE_TABLE_NAME + " ("
					+ UniverseParameters.EXTERNAL_SOURCE_TYPE + "," + UniverseParameters.EXTERNAL_SOURCE_ID + ","
					+ UniverseParameters.USERNAME + "," + UniverseParameters.ISREADY;
			String values = " VALUES('" + UniverseSourceType.FORM + "'" + "," + formModel.getId() + ",'" + username
					+ "'" + ",true";

			String coma = ",";
			if (formModel.getDataSourceType().isInputGrid() && formModel.getDataSourceId() != null) {
				sql += coma + UniverseParameters.SOURCE_TYPE + coma + UniverseParameters.SOURCE_ID;
				values += ",'" + UniverseSourceType.INPUT_GRID + "'," + formModel.getDataSourceId();
			}

			for (FormModelField field : formModel.getFields()) {
				String col = buildDbColName(field);
				if (StringUtils.hasText(col)) {
					Object[] data = field.getCategory() == FormModelFieldCategory.DETAILS ? detailDatas : mainDatas;
					Object value = data[field.getPosition()];
					if (value != null) {
						sql += coma + col;
						values += coma + getValueAsString(field, value);
						coma = ",";
					}
				}
			}
			sql += ")" + values + ") returning ID";
			return sql;
		}
	}

	public String buildUpdateSql(Object[] mainDatas, Object[] detailDatas, FormModel formModel, MaterializedGrid grid,
			Long id) {
		if (formModel.getDataSourceType() == DataSourceType.MATERIALIZED_GRID) {
			String sql = "UPDATE " + grid.getMaterializationTableName() + " SET ";
			String coma = "";
			for (FormModelField field : formModel.getFields()) {
				String col = buildDbColName(field);
				if (StringUtils.hasText(col)) {
					Object[] data = field.getCategory() == FormModelFieldCategory.DETAILS ? detailDatas : mainDatas;
					Object value = data[field.getPosition()];
					if (value != null) {
						sql += coma + col + " = " + getValueAsString(field, value);
						coma = ",";
					}
				}
			}
			sql += " WHERE ID = " + id;
			return sql;
		} else {
			String sql = "UPDATE " + UniverseParameters.UNIVERSE_TABLE_NAME + " SET ";
			String coma = "";
			for (FormModelField field : formModel.getFields()) {
				String col = buildDbColName(field);
				if (StringUtils.hasText(col)) {
					Object[] data = field.getCategory() == FormModelFieldCategory.DETAILS ? detailDatas : mainDatas;
					Object value = data[field.getPosition()];
					if (value != null) {
						sql += coma + col + " = " + getValueAsString(field, value);
						coma = ",";
					}
				}
			}
			sql += " WHERE ID = " + id;
			return sql;
		}
	}

	public String buildChangeValueSql(FormModelButtonAction action, FormModel formModel, MaterializedGrid grid,
			List<Long> ids) {
		if (action.getFieldId() != null) {
			FormModelField field = null;
			for (FormModelField f : formModel.getFields()) {
				if (action.getFieldId().equals(f.getId())) {
					field = f;
					break;
				}
			}
			if (field != null) {
				String col = buildDbColName(field);
				if (StringUtils.hasText(col)) {
					String value = action.buildValue(field);
					String sql = "UPDATE " + UniverseParameters.UNIVERSE_TABLE_NAME + " SET " + col + " = " + value;
					if (formModel.getDataSourceType() == DataSourceType.MATERIALIZED_GRID) {
						sql = "UPDATE " + grid.getMaterializationTableName() + " SET " + col + " = " + value;
					}

					sql += " WHERE ID IN (";
					String coma = "";
					for (Long id : ids) {
						sql += coma + id;
						coma = ",";
					}
					sql += ")";
					return sql;
				}
			}
		}
		return null;
	}

	private String getValueAsString(FormModelField field, Object value) {
		if (value != null) {
			if (field.getDimensionType() == DimensionType.ATTRIBUTE) {
				return "'" + value + "'";
			} else if (field.getDimensionType() == DimensionType.MEASURE) {
				if (value instanceof BigDecimal) {
					return ((BigDecimal) value).toPlainString();
				}
				return value.toString();
			} else if (field.getDimensionType() == DimensionType.PERIOD) {
				return getDateAsString(value);
			}
		}
		return "NULL";
	}

	protected String buildDbColName(FormModelField field) {
		if (field.getDimensionId() == null || field.getDimensionId().equals(Long.valueOf(0))) {
			return null;
		} else {
			return field.getUniverseTableColumnName();
		}
	}

	public static String getDateAsString(Object value) {
		Date date = null;
		List<String> formats = Arrays.asList("yyyy-MM-dd", "dd/MM/yyyy", "yyyy-MM-ddTHH:mm:ss");
		int index = 0;
		while (date == null && index < formats.size()) {
			try {
				date = new SimpleDateFormat(formats.get(index++)).parse(value.toString());
			} catch (Exception e) {

			}
		}
		if (date != null) {
			return "'" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "'";
		} else {
			throw new BcephalException("Unknow data format: " + value);
		}
	}

}

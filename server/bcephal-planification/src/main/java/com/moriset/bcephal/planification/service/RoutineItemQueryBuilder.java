/**
 * 
 */
package com.moriset.bcephal.planification.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.LoaderData;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.MeasureFunctions;
import com.moriset.bcephal.domain.universe.UniverseExternalSourceType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.AbstractSmartGridColumn;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.service.ReportGridQueryBuilder;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineCalculateItem;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineConcatenateItem;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineItem;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineMappingCondition;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineSourceType;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineSpot;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineSpotCondition;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper=false)
public class RoutineItemQueryBuilder  extends ReportGridQueryBuilder {

	TransformationRoutineItem item;
	Map<String, Object> parameters;
	LoaderData loaderData;
	
	public RoutineItemQueryBuilder(GrilleDataFilter filter, TransformationRoutineItem item) {
		super(filter);
		this.item = item;
		setIgnoreUserFilter(true);
	}
	
	@Override
	protected boolean isReport() {
		return false;
	}
	
	@Override
	public String buildQuery() {
		this.parameters = new HashMap<String, Object>();
		String column = item.getUniverseTableTargetColumnName();
		String sql = null;
		if(item.getSourceField().getSourceType() == TransformationRoutineSourceType.MAPPING) {
			sql = buildMappingQuery(column);
			if(StringUtils.hasText(sql)){
				String wherePart = buildWherePart(column);
				if(StringUtils.hasText(wherePart)){
					sql += wherePart;
				}
			}
		}
		else if(item.getSourceField().getSourceType() == TransformationRoutineSourceType.SPOT) {
			sql = buildSpotQuery(column);
			if(StringUtils.hasText(sql)){
				String wherePart = buildWherePart(column);				
//				if(item.getRanking() != null && item.getRanking().getDimensionId() != null) {
//					String targetTable = UniverseParameters.UNIVERSE_TABLE_NAME;
//					String rankingSql = " FROM (SELECT id FROM " +  targetTable + " item2 ";
//					String coma = " WHERE ";
//					if(StringUtils.hasText(wherePart)){
//						rankingSql += wherePart;
//						coma = " AND ";
//					}
//					//rankingSql += coma + "item.id = item2.id";
//					rankingSql += " ORDER BY " + item.getRanking().getUniverseTableColumnName(DataSourceType.UNIVERSE)
//							+ " " + (item.getRanking().isAscending() ? "ASC" : "DESC");
//					rankingSql += ") AS item2";
//					sql += rankingSql;
//				}				
				if(StringUtils.hasText(wherePart)){
					sql += wherePart;
				}
			}
		}
		else {
			String setpart = buildSetPart(column);
			if(StringUtils.hasText(setpart)){
				sql = "UPDATE " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME;
				sql += setpart;
				String wherePart = buildWherePart(column);
				if(StringUtils.hasText(wherePart)){
					sql += wherePart;
				}	
			}	
		}
		return sql;
	}
	
	
	public String buildSpotQuery(Long id) {
		this.parameters = new HashMap<String, Object>();
		String column = item.getUniverseTableTargetColumnName();
		String sql = null;
		if(item.getSourceField().getSourceType() == TransformationRoutineSourceType.SPOT) {
			sql = buildSpotQuery(column);
			if(StringUtils.hasText(sql)){
				sql += " WHERE id = :id";	
				//this.parameters.put("id", id);
			}
		}
		return sql;
	}
	
	public String buildCalculateQuery(Long id) {
		this.parameters = new HashMap<String, Object>();
		String column = item.getUniverseTableTargetColumnName();
		String sql = null;
		if(item.getSourceField().getSourceType() == TransformationRoutineSourceType.CALCULATE) {
			sql = buildSetCalculatePart(column);
			if(StringUtils.hasText(sql)){
				String targetTable = UniverseParameters.UNIVERSE_TABLE_NAME;
				sql = "UPDATE " + targetTable + " item " + sql + " WHERE id = :id";	
			}
		}
		return sql;
	}
	
	public String buildSelectIdsQuery() {
		this.parameters = new HashMap<String, Object>();
		String column = item.getUniverseTableTargetColumnName();
		String sql = null;
		//if(item.getSourceField().getSourceType() == TransformationRoutineSourceType.SPOT) {
			String targetTable = UniverseParameters.UNIVERSE_TABLE_NAME;
			sql = "SELECT id FROM " + targetTable;
			String wherePart = buildWherePart(column);	
			if(StringUtils.hasText(wherePart)){
				sql += wherePart;
			}
		//}
		return sql;
	}
	
	
	protected String buildSetPart(String col) {		
		String sql = null;
		if(item.getSourceField().getSourceType() == TransformationRoutineSourceType.FREE) {
			Object value = item.getFreeValue();
			if(value != null) {
				sql = " SET " + col + " = :value ";
				parameters.put("value", value);
			}
			else {
				sql = " SET " + col + " = null ";
			}						
		}
		else if(item.getSourceField().getSourceType() == TransformationRoutineSourceType.NULL) {
			sql = " SET " + col + " = null ";				
		}
		else if(item.getSourceField().getSourceType() == TransformationRoutineSourceType.DIMENSION) {
			String fieldCol = item.getSourceField().getUniverseColumnName(item.getType());			
			if(fieldCol != null) {
				sql = " SET " + col + " = " + fieldCol;
			}					
		}
		else if(item.getSourceField().getSourceType() == TransformationRoutineSourceType.CONCATENATE) {	
			sql = buildSetConcatenatePart(col);
		}
		else if(item.getSourceField().getSourceType() == TransformationRoutineSourceType.CALCULATE) {	
			sql = buildSetCalculatePart(col);
		}
		else if(item.getSourceField().getSourceType() == TransformationRoutineSourceType.MAPPING) {	
			sql = buildSetMappingPart(col);
		}
		else if(item.getSourceField().getSourceType() == TransformationRoutineSourceType.POSITION) {	
			sql = buildSetPositionPart(col);
		}
		else if(item.getSourceField().getSourceType() == TransformationRoutineSourceType.REPLACE) {	
			sql = buildSetReplacePart(col);
		}
		return sql;
	}
		
	private String buildSetConcatenatePart(String col) {
		String sql = null;
		String set = " SET ";
		for(TransformationRoutineConcatenateItem concatenateItem : item.getConcatenateItems()) {
			if(concatenateItem.getField() != null) {
				if(concatenateItem.getField().getSourceType() == TransformationRoutineSourceType.FREE
						&& concatenateItem.getField().getStringValue() != null) {
					String param = "value" + concatenateItem.getId();
					if(sql == null) {
						sql = set + col + " = (:" + param;
						parameters.put(param, concatenateItem.getField().getStringValue());
						set = " || ";
					}
					else {
						sql += set + ":" + param;
						parameters.put(param, concatenateItem.getField().getStringValue());
						set = " || ";
					}
				}
				else if(concatenateItem.getField().getSourceType() == TransformationRoutineSourceType.DIMENSION) {
					String fieldCol = concatenateItem.getField().getUniverseColumnName(item.getType());			
					if(fieldCol != null) {
						fieldCol = "CASE WHEN " + fieldCol + " IS NULL THEN '' ELSE " + fieldCol + " END";
						if(sql == null) {
							sql = set + col + " = (" + fieldCol;
							set = " || ";
						}
						else {
							sql += set + fieldCol;
							set = " || ";
						}
					}
				}
			}
		}
		if(sql != null) {
			sql += ")";
		}
		return sql;
	}
	
	private String buildSetCalculatePart(String col) {
		String sql = null;
		String set = " SET ";
		for(TransformationRoutineCalculateItem calculateItem : item.getCalculateItems()) {
			if(calculateItem.getField() != null) {
				String sign = calculateItem.getSign();
				if(sign == null) {
					sign = "";
				}
				if(calculateItem.getOpeningBracket() == null) {
					calculateItem.setOpeningBracket("");
				}
				if(calculateItem.getClosingBracket() == null) {
					calculateItem.setClosingBracket("");
				}
				if(calculateItem.getField().getSourceType() == TransformationRoutineSourceType.FREE
						&& calculateItem.getField().getDecimalValue() != null) {
					String param = "value" + calculateItem.getId();
					if(sql == null) {
						sql = set + col + " = " + sign + " " +  calculateItem.getOpeningBracket() + " :" + param + " " + calculateItem.getClosingBracket();
						parameters.put(param, calculateItem.getField().getDecimalValue());
					}
					else {
						sql += sign + " " + calculateItem.getOpeningBracket() + " :" + param + " " + calculateItem.getClosingBracket();
						parameters.put(param, calculateItem.getField().getDecimalValue());
					}
				}
				else if(calculateItem.getField().getSourceType() == TransformationRoutineSourceType.DIMENSION) {
					String fieldCol = calculateItem.getField().getUniverseColumnName(item.getType());			
					if(fieldCol != null) {
						fieldCol = "CASE WHEN " + fieldCol + " IS NULL THEN 0 ELSE " + fieldCol + " END";
						if(sql == null) {
							sql = set + col + " = " + sign + " " +  calculateItem.getOpeningBracket() + " " + fieldCol + " " + calculateItem.getClosingBracket();
						}
						else {
							sql += sign + " " + calculateItem.getOpeningBracket() + " " + fieldCol + " " + calculateItem.getClosingBracket();							
						}
					}
				}
				else if(calculateItem.getField().getSourceType() == TransformationRoutineSourceType.SPOT && calculateItem.getSpot() != null) {
					String fieldCol = buildSpotQuery(calculateItem.getSpot());		
					if(fieldCol != null) {
						if(sql == null) {
							sql = set + col + " = " + sign + " " + calculateItem.getOpeningBracket() + " (" + fieldCol + ") " + calculateItem.getClosingBracket();
						}
						else {
							sql += sign + " " + calculateItem.getOpeningBracket() + " (" + fieldCol + ") " + calculateItem.getClosingBracket();							
						}
					}
				}
			}
		}
		return sql;
	}
	
	private String buildSetReplacePart(String col) {
		String sql = item.buildSetReplacePart(col, parameters);
		return sql;
	}

	private String buildSetPositionPart(String col) {
		String sql = item.buildSetPositionPart(col, parameters);
		return sql;
	}	
	
	@SuppressWarnings("unused")
	private String buildSpotQuery(String col) {
		String sql = null;
		if(item.getSpot() != null && item.getSpot().getGrid() != null && item.getSpot().getMeasureId() != null) {
			
			String sourceCol = item.getSpot().getUniverseTableColumnName();	
			if(item.getSpot().getDataSourceType().isInputGrid() || item.getSpot().getDataSourceType().isReportGrid()) {
				AbstractSmartGridColumn column = item.getSpot().getGrid().getColumnById(item.getSpot().getMeasureId());
				sourceCol = column.getDbColumnName();
			}
			String sourceVar = "spot.";
			String sourceTable = item.getSpot().getGrid().getDbTableName();
			
			String targetVar = "item.";
			String targetTable = UniverseParameters.UNIVERSE_TABLE_NAME;
			
			MeasureFunctions func = item.getSpot().getMeasureFunction() != null ? item.getSpot().getMeasureFunction() : MeasureFunctions.SUM;
			String spotSql = "SELECT " + func.code + "(" + sourceCol + ") FROM " + sourceTable + "  AS spot";
			String coma = " WHERE ";
			
			if(item.getSpot().getGrid().getGridType().isInputGrid()) {		
				spotSql += coma + UniverseParameters.SOURCE_TYPE + " = :sourceType AND " + UniverseParameters.SOURCE_ID + " = :sourceId";
				this.parameters.put("sourceType", UniverseSourceType.INPUT_GRID.name());
				this.parameters.put("sourceId", item.getSpot().getGrid().getId());
				coma = " AND ";
			}
			
			item.getSpot().sortConditions();
			String conditionSql = "";
			
			for(TransformationRoutineSpotCondition condition : item.getSpot().getConditions()) {
				
				String col1 = null;
				String col2 = null;
				
				DataSourceType col1DataSourceType = DataSourceType.UNIVERSE;
				DataSourceType col2DataSourceType = DataSourceType.UNIVERSE;
				
				if(condition.getSide1().isReferenceGrid()) {
					AbstractSmartGridColumn column = item.getSpot().getGrid().getColumnById(condition.getColumnId1());						
					col1 = column.getDbColumnName();
					col1 = sourceVar + col1;
				}
				else if(condition.getSide1().isTargetGrid()) {
					AbstractSmartGridColumn column = item.getGrid().getColumnById(condition.getColumnId1());					
					col1 = column.getDbColumnName();
					col1 = targetVar + col1;
				}
				
				if(condition.getSide2() != null) {
					if(condition.getSide2().isReferenceGrid() && condition.getColumnId2() != null) {
						AbstractSmartGridColumn column = item.getSpot().getGrid().getColumnById(condition.getColumnId2());
						col2 = column.getDbColumnName();
						col2 = sourceVar + col2;
						if(column.isPeriod() && condition.getPeriodValue() != null) {
							col2 = condition.getPeriodValue().buildDynamicDate(col2);
						}
					}
					else if(condition.getSide2().isTargetGrid() && condition.getColumnId2() != null) {
						AbstractSmartGridColumn column = item.getGrid().getColumnById(condition.getColumnId2());
						col2 = column.getDbColumnName();
						col2 = targetVar + col2;
						if(column.isPeriod() && condition.getPeriodValue() != null) {
							col2 = condition.getPeriodValue().buildDynamicDate(col2);
						}
					}
					else if(condition.getSide2().isFree()) {
						
					}
				}
				
				String part = condition.getSql(col1, col2);
				if(StringUtils.hasText(part)) {
					boolean isFirst = condition.getPosition() == 0;
					String verb = condition.getVerb() != null ? condition.getVerb() : FilterVerb.AND.name();
					boolean isAndNot = FilterVerb.ANDNO.name().equals(verb);
					boolean isOrNot = FilterVerb.ORNO.name().equals(verb);
					String verbString = " " + verb + " ";
					
					if(isAndNot) {
						verbString = isFirst ? " NOT " : " AND NOT ";
					}
					else if(isOrNot) {
						verbString = isFirst ? " NOT " : " OR NOT ";
					}
					else if(isFirst){
						verbString = coma;
						coma = " AND ";	
					}
					
					if(isAndNot || isOrNot) {
						part = "(" + part + ")";
					}	
					conditionSql += verbString + part;		
				}				
			}
			if(StringUtils.hasText(conditionSql)) {
				spotSql += conditionSql;		
			}
			
			
			sql = "UPDATE " + targetTable + " item SET " + col 
					+ " = (" + spotSql + ") ";	
		}
		return sql;
	}
	
	private String buildSpotQuery(TransformationRoutineSpot spot) {
		String sql = null;
		if(spot != null && spot.getGrid() != null && spot.getMeasureId() != null) {
			
			String sourceCol = spot.getUniverseTableColumnName();		
			if(spot.getDataSourceType().isInputGrid() || spot.getDataSourceType().isReportGrid()) {
				AbstractSmartGridColumn column = spot.getGrid().getColumnById(spot.getMeasureId());
				sourceCol = column.getDbColumnName();
			}
			String sourceVar = "spot.";
			String sourceTable = spot.getGrid().getDbTableName();
			
			String targetVar = "item.";
//			String targetTable = UniverseParameters.UNIVERSE_TABLE_NAME;
			
			MeasureFunctions func = spot.getMeasureFunction() != null ? spot.getMeasureFunction() : MeasureFunctions.SUM;
			String spotSql = "SELECT " + func.code + "(" + sourceCol + ") FROM " + sourceTable + "  AS spot";
			String coma = " WHERE ";	
			
			if(spot.getGrid().getGridType().isInputGrid()) {		
				spotSql += coma + UniverseParameters.SOURCE_TYPE + " = :sourceType AND " + UniverseParameters.SOURCE_ID + " = :sourceId";
				this.parameters.put("sourceType", UniverseSourceType.INPUT_GRID.name());
				this.parameters.put("sourceId", spot.getGrid().getId());
				coma = " AND ";
			}
			
			spot.sortConditions();
			String conditionSql = "";
			
			for(TransformationRoutineSpotCondition condition : spot.getConditions()) {
				
				String col1 = null;
				String col2 = null;
				
//				DataSourceType col1DataSourceType = DataSourceType.UNIVERSE;
//				DataSourceType col2DataSourceType = DataSourceType.UNIVERSE;
				
				if(condition.getSide1().isReferenceGrid()) {
					AbstractSmartGridColumn column = spot.getGrid().getColumnById(condition.getColumnId1());						
					col1 = column.getDbColumnName();
					col1 = sourceVar + col1;
				}
				else if(condition.getSide1().isTargetGrid()) {
					AbstractSmartGridColumn column = item.getGrid().getColumnById(condition.getColumnId1());					
					col1 = column.getDbColumnName();
					col1 = targetVar + col1;
				}
				
				if(condition.getSide2() != null) {
					if(condition.getSide2().isReferenceGrid() && condition.getColumnId2() != null) {
						AbstractSmartGridColumn column = spot.getGrid().getColumnById(condition.getColumnId2());
						col2 = column.getDbColumnName();
						col2 = sourceVar + col2;
						if(column.isPeriod() && condition.getPeriodValue() != null) {
							col2 = condition.getPeriodValue().buildDynamicDate(col2);
						}
					}
					else if(condition.getSide2().isTargetGrid() && condition.getColumnId2() != null) {
						AbstractSmartGridColumn column = item.getGrid().getColumnById(condition.getColumnId2());
						col2 = column.getDbColumnName();
						col2 = targetVar + col2;
						if(column.isPeriod() && condition.getPeriodValue() != null) {
							col2 = condition.getPeriodValue().buildDynamicDate(col2);
						}
					}
					else if(condition.getSide2().isFree()) {
						
					}
				}
				
				String part = condition.getSql(col1, col2);
				if(StringUtils.hasText(part)) {
					boolean isFirst = condition.getPosition() == 0;
					String verb = condition.getVerb() != null ? condition.getVerb() : FilterVerb.AND.name();
					boolean isAndNot = FilterVerb.ANDNO.name().equals(verb);
					boolean isOrNot = FilterVerb.ORNO.name().equals(verb);
					String verbString = " " + verb + " ";
					
					if(isAndNot) {
						verbString = isFirst ? " NOT " : " AND NOT ";
					}
					else if(isOrNot) {
						verbString = isFirst ? " NOT " : " OR NOT ";
					}
					else if(isFirst){
						verbString = coma;
						coma = " AND ";	
					}
					
					if(isAndNot || isOrNot) {
						part = "(" + part + ")";
					}	
					conditionSql += verbString + part;		
				}				
			}
			
			if(StringUtils.hasText(conditionSql)) {
				spotSql += conditionSql;		
			}	
			
			sql = spotSql;	
		}
		return sql;
	}
	

	@SuppressWarnings("unused")
	private String buildMappingQuery(String col) {
		String sql = null;
		if(item.getSourceField() != null && item.getSourceField().getMapping() != null 
				&& item.getSourceField().getMapping().getGrid() != null
				 && item.getSourceField().getMapping().getValueColumnId() != null) {
			
			AbstractSmartGridColumn sourceColumn = item.getSourceField().getMapping().getGrid().getColumnById(item.getSourceField().getMapping().getValueColumnId());
			String sourceCol = sourceColumn.getDbColumnName();			
			String sourceVar = "mapp.";
			String sourceTable = item.getSourceField().getMapping().getGrid().getDbTableName();
						
			Long dimensionId = item.getDataSourceType().isJoin() || item.getDataSourceType().isMaterializedGrid() ? item.getTargetDimensionId() : sourceColumn.getDimensionId();			
			GrilleColumn targetGrilleColumn = new GrilleColumn(null, "", item.getType(), item.getTargetDimensionId(), "");			
			targetGrilleColumn.setDataSourceId(item.getTargetGridId());
			targetGrilleColumn.setDataSourceType(item.getDataSourceType());
			String targetCol = targetGrilleColumn.getUniverseTableColumnName();			
			String targetVar = "item.";
			String targetTable = UniverseParameters.UNIVERSE_TABLE_NAME;
			
			
			sql = "UPDATE " + UniverseParameters.UNIVERSE_TABLE_NAME 
					+ " item SET " + col 
					+ " = (SELECT " + sourceVar + sourceCol + " FROM " + sourceTable + " mapp ";
			String coma = " WHERE ";	
			if(item.getSourceField().getMapping().getGrid().getGridType() == JoinGridType.GRID) {
				sql += coma + sourceVar + UniverseParameters.SOURCE_TYPE + " = '" + UniverseSourceType.INPUT_GRID.name() + "' AND " + sourceVar
						+ UniverseParameters.SOURCE_ID + " = " + item.getSourceField().getMapping().getGrid().getId();
				coma = " AND ";
			}
			sql += coma + sourceVar + sourceCol + " IS NOT NULL";
			coma = " AND ";
			for(Long id : item.getSourceField().getMapping().getMappingColumnIds()) {
				AbstractSmartGridColumn column = item.getSourceField().getMapping().getGrid().getColumnById(id);
				if(column != null) {
					String c = column.getDbColumnName();
					sql += " AND " + sourceVar + c + " = " + targetVar + c;					
				}
			}
			
			item.getSourceField().getMapping().sortConditions();
			String conditionSql = "";
			
			for(TransformationRoutineMappingCondition condition : item.getSourceField().getMapping().getConditions()) {
				
				String col1 = null;
				String col2 = null;
				
				DataSourceType col1DataSourceType = DataSourceType.UNIVERSE;
				DataSourceType col2DataSourceType = DataSourceType.UNIVERSE;
				
				if(condition.getSide1().isReferenceGrid()) {
					AbstractSmartGridColumn column = item.getSourceField().getMapping().getGrid().getColumnById(condition.getColumnId1());						
					col1 = column.getDbColumnName();
					col1 = sourceVar + col1;
				}
				else if(condition.getSide1().isTargetGrid()) {
					AbstractSmartGridColumn column = item.getGrid().getColumnById(condition.getColumnId1());					
					col1 = column.getDbColumnName();
					col1 = targetVar + col1;
				}
				
				if(condition.getSide2() != null) {
					if(condition.getSide2().isReferenceGrid() && condition.getColumnId2() != null) {
						AbstractSmartGridColumn column = item.getSourceField().getMapping().getGrid().getColumnById(condition.getColumnId2());
						col2 = column.getDbColumnName();
						col2 = sourceVar + col2;
					}
					else if(condition.getSide2().isTargetGrid() && condition.getColumnId2() != null) {
						AbstractSmartGridColumn column = item.getGrid().getColumnById(condition.getColumnId2());
						col2 = column.getDbColumnName();
						col2 = targetVar + col2;
					}
					else if(condition.getSide2().isFree()) {
						
					}
				}
				
				String part = condition.getSql(col1, col2);
				if(StringUtils.hasText(part)) {
					boolean isFirst = condition.getPosition() == 0;
					String verb = condition.getVerb() != null ? condition.getVerb() : FilterVerb.AND.name();
					boolean isAndNot = FilterVerb.ANDNO.name().equals(verb);
					boolean isOrNot = FilterVerb.ORNO.name().equals(verb);
					String verbString = " " + verb + " ";
					
					if(isAndNot) {
						verbString = isFirst ? " NOT " : " AND NOT ";
					}
					else if(isOrNot) {
						verbString = isFirst ? " NOT " : " OR NOT ";
					}
					else if(isFirst){
						verbString = coma;
						coma = " AND ";	
					}
					
					if(isAndNot || isOrNot) {
						part = "(" + part + ")";
					}	
					conditionSql += verbString + part;		
				}				
			}
			
			if(StringUtils.hasText(conditionSql)) {
				sql += conditionSql;		
			}
			
			sql += " LIMIT 1)";
		}
		return sql;
	}

	private String buildSetMappingPart(String col) {	
		log.debug("Build mapping set part...");
		String sql = null;
		
		log.debug("Mapping set part : {}", sql);
		return sql;
	}
	
	
	
	protected String buildWherePart(String col) {
		String sql = buildWherePart();
		String where = StringUtils.hasText(sql) ? " AND " : " WHERE ";			
		
		if(item.isApplyOnlyIfEmpty()) {
			if(item.isAttribute()) {
				sql += where + "(" + col + " IS NULL OR " + col + " = '')";
				where = " AND ";
			}
			else {
				sql += where + "(" + col + " IS NULL)";
				where = " AND ";
			}
		}	
		
		String loadSql = buildLoadDataWherePart();
		if(StringUtils.hasText(loadSql)) {
			sql += where + "(" + loadSql + ")";
			where = " AND ";
		}
		
		return sql;
	}

	private String buildLoadDataWherePart() {
		String sql = "";
		String coma = "";
		if(loaderData != null) {
			if(loaderData.getLoaderId() != null) {
				String param1 = "param_src_" + System.currentTimeMillis();
				parameters.put(param1, loaderData.getLoaderId());				
				
				String param2 = "param_srct_" + System.currentTimeMillis();
				parameters.put(param2, UniverseExternalSourceType.FILE_LOADER.name());				
				
				sql += coma + UniverseParameters.EXTERNAL_SOURCE_TYPE + " = :" + param2 + " AND "
						+ UniverseParameters.EXTERNAL_SOURCE_ID + " = :" + param1;
				coma = " AND ";
			}
			
			if (StringUtils.hasText(loaderData.getLoaderFileColumn()) && loaderData.getFiles() != null && loaderData.getFiles().size() > 0) {
				String param = "param_file_" + System.currentTimeMillis();
				parameters.put(param, loaderData.getFiles());				
				sql += coma + loaderData.getLoaderFileColumn() + " in :" + param;
				coma = " AND ";
			}
			if (StringUtils.hasText(loaderData.getLoaderNbrColumn()) && StringUtils.hasText(loaderData.getLoadNbr())) {
				String param = "param_nbr_" + System.currentTimeMillis();
				parameters.put(param, loaderData.getLoadNbr());				
				sql += coma + loaderData.getLoaderNbrColumn() + " = :" + param;
				coma = " AND ";
			}
			
			if(StringUtils.hasText(loaderData.getUsername())) {		
				String param = "param_user_" + System.currentTimeMillis();
				parameters.put(param, loaderData.getUsername());				
				sql += coma + UniverseParameters.USERNAME + " = :" + param;
				coma = " AND ";
			}
			if(loaderData.getFiles() != null && loaderData.getFiles().size() > 0) {
				String param = "param_ref_" + System.currentTimeMillis();
				parameters.put(param, loaderData.getFiles());				
				sql += coma + UniverseParameters.EXTERNAL_SOURCE_REF + " in :" + param;
				coma = " AND ";
			}			
		}
		return sql;
	}

}

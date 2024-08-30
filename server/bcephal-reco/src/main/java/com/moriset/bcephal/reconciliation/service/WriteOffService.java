/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.reconciliation.domain.ReconciliationData;
import com.moriset.bcephal.reconciliation.domain.WriteOffField;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WriteOffService {

	private EntityManager entityManager;
	
	private String username;
	
	public void writeOff(ReconciliationData data, Attribute recoAttribute, String recoNumber, Date recoDate, RunModes mode) throws Exception {
		log.debug("Try to edit writeoff");
		if (data != null && data.getWriteOffMeasureId() == null && data.getWriteOffAmount().compareTo(BigDecimal.ZERO) != 0) {
			throw new BcephalException("Writeoff measure is NULL!");
		}
		if (data == null || data.getWriteOffMeasureId() == null || data.getWriteOffAmount() == null || data.getWriteOffAmount().compareTo(BigDecimal.ZERO) == 0) {
			return;
		}
		String universe = UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME;
		String coma = "";
		String stringQuery = "INSERT INTO " + universe + " (ID";
		String valueQuery = "values(DEFAULT";
		List<Object> parameters = new ArrayList<>(0);
		coma = ", ";	
		String[] result = addToQuery(UniverseParameters.SOURCE_TYPE, UniverseSourceType.WRITEOFF.toString(), coma, stringQuery, valueQuery, parameters);
		stringQuery = result[0];	
		valueQuery = result[1];	
		
		result = addToQuery(UniverseParameters.ISREADY, true, coma, stringQuery, valueQuery, parameters);
		stringQuery = result[0];	
		valueQuery = result[1];
		result = addToQuery(UniverseParameters.USERNAME, username, coma, stringQuery, valueQuery, parameters);
		stringQuery = result[0];	
		valueQuery = result[1];
		result = addToQuery(recoAttribute.getUniverseTableColumnName(), recoNumber, coma, stringQuery, valueQuery, parameters);	
		stringQuery = result[0];	
		valueQuery = result[1];
		Measure measure = new Measure(data.getWriteOffMeasureId());
		result = addToQuery(measure.getUniverseTableColumnName(), data.getWriteOffAmount().doubleValue(), coma, stringQuery, valueQuery, parameters);
		stringQuery = result[0];	
		valueQuery = result[1];
		
		if(data.isAddUser() && data.getUserAttribute() != null) {
			result = addToQuery(data.getUserAttribute().getUniverseTableColumnName(), username, coma, stringQuery, valueQuery, parameters);
			stringQuery = result[0];	
			valueQuery = result[1];
		}
		if(data.isAddRecoDate() && data.getRecoDateId() != null) {
			String col = new Period(data.getRecoDateId()).getUniverseTableColumnName();	
			log.debug("*********** Add reco date: {} = {}", col, recoDate);
			result = addToQuery(col, recoDate, coma, stringQuery, valueQuery, parameters);
			stringQuery = result[0];	
			valueQuery = result[1];
		}
		if(data.isAddNote() && data.getNoteAttributeId() != null && StringUtils.hasText(data.getNote())) {
			result = addToQuery(new Attribute(data.getNoteAttributeId()).getUniverseTableColumnName(), data.getNote(), coma, stringQuery, valueQuery, parameters);
			stringQuery = result[0];	
			valueQuery = result[1];
		}
		if(data.isAddAutomaticManual() && data.getAutoManualAttribute() != null) {
			if(mode == RunModes.A) {
				if(data.getAutomaticValue() != null) {
					result = addToQuery(data.getAutoManualAttribute().getUniverseTableColumnName(), data.getAutomaticValue().getName(), coma, stringQuery, valueQuery, parameters);
					stringQuery = result[0];	
					valueQuery = result[1];
				}
				else {
					result = addToQuery(data.getAutoManualAttribute().getUniverseTableColumnName(), RunModes.A.name(), coma, stringQuery, valueQuery, parameters);
					stringQuery = result[0];	
					valueQuery = result[1];
				}
			}
			else {
				if(data.getManualValue() != null) {
					result = addToQuery(data.getAutoManualAttribute().getUniverseTableColumnName(), data.getManualValue().getName(), coma, stringQuery, valueQuery, parameters);
					stringQuery = result[0];	
					valueQuery = result[1];
				}
				else {
					result = addToQuery(data.getAutoManualAttribute().getUniverseTableColumnName(), RunModes.M.name(), coma, stringQuery, valueQuery, parameters);
					stringQuery = result[0];	
					valueQuery = result[1];
				}
			}
			
		}
		
//		linkedAttributeDatas = new HashMap<>(0);		
		for(WriteOffField field : data.getWriteOffFields()){
			if(field.getDimensionId() != null) {
				String col = field.getDimensionType().isAttribute() ? new Attribute(field.getDimensionId()).getUniverseTableColumnName()
						: field.getDimensionType().isMeasure() ? new Measure(field.getDimensionId()).getUniverseTableColumnName() 
								: field.getDimensionType().isPeriod() ? new Period(field.getDimensionId()).getUniverseTableColumnName() : null;
				if(field.getDimensionType().isAttribute()){
					result = addToQuery(col, field.getStringValue(), coma, stringQuery, valueQuery, parameters);
					stringQuery = result[0];	
					valueQuery = result[1];
				}
				else if(field.getDimensionType().isMeasure()){	
					result = addToQuery(col, field.getDecimalValue(), coma, stringQuery, valueQuery, parameters);
					stringQuery = result[0];	
					valueQuery = result[1];
				}
				else if(field.getDimensionType().isPeriod()){	
					result = addToQuery(col, field.getDateValue().buildDynamicDate(), coma, stringQuery, valueQuery, parameters);
					stringQuery = result[0];	
					valueQuery = result[1];
				}
			}
			
			
//			if(field.getAttributeField() != null){		
//				if(field.value != null){
//					addToQuery(field.getAttributeField().getUniverseTableColumnName(), field.value.getName(), coma, stringQuery, valueQuery, parameters);
//				}				
//			}
//			else if(field.getPeriodField() != null && field.date != null){
//				logger.debug("*********** Add Period: {} = {}", field.getPeriodField().getUniverseTableColumnName(), field.date);
//				Calendar calendar = Calendar.getInstance();
//				calendar.setTime(field.date);
//				Date date = calendar.getTime();
//				logger.debug("*********** Original: {} --- Calendar: {}", field.date, date);
//				addToQuery(field.getPeriodField().getUniverseTableColumnName(), date, coma, stringQuery, valueQuery, parameters);
//			}			
		}
		
		stringQuery += ")" + " " + valueQuery + ")";
		
		Query query = entityManager.createNativeQuery(stringQuery);
		int n = 1;
		for(Object parameter : parameters){
			query.setParameter(n++, parameter);
		}
		query.executeUpdate();
		
//		if (linkedAttributeDatas.size() > 0) {
//			AttributeService service = new AttributeService(getUserSession());
//			service.saveLinkedAttributeDatasWithoutCommit(linkedAttributeDatas.values());
//		}
		
	}
	
	
	protected String[] addToQuery(String colName, Object value, String coma, String stringQuery, String valueQuery, List<Object> parameters) {
		if (value != null) {
			stringQuery += coma + colName;			
			valueQuery += coma + "?";
			parameters.add(value);
		}
		return new String[] {stringQuery, valueQuery};
	}
	
}

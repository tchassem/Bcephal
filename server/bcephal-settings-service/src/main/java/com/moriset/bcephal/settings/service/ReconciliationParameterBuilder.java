/**
 * 
 */
package com.moriset.bcephal.settings.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.InitiationParameterCodes;
import com.moriset.bcephal.domain.ReconciliationParameterCodes;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Entity;
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.grid.domain.GrilleCategory;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.service.MaterializedGridService;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class ReconciliationParameterBuilder extends ParameterBuilder {

	protected String MODEL_NAME = "Default Model";
	protected String ENTITY_NAME = "Reconciliation";
	protected String RECO_ENTITY_NAME = "Reco Type";
	protected Model model;
	protected Entity entity;
	protected Entity typeEntity;
	private String RECO_MAT_GRID_REPOSITOPRY_NAME = "Reco logs";
	

	@Autowired
	protected MaterializedGridService matGridService;

	@Transactional
	public void buildParameters(HttpSession session, Locale locale) {
		HashMap<Parameter, IPersistent> parameters = new HashMap<Parameter, IPersistent>(0);
		try {
			buildReconciliationParameters(parameters, session, locale);
			saveParameters(parameters, locale);
		} catch (Exception ex) {
			log.error("Unable to save billing role parameters ", ex);
		}
	}

	private void buildReconciliationParameters(HashMap<Parameter, IPersistent> parametersMap, HttpSession session, Locale locale) throws Exception {
		this.model = (Model) buildParameterIfNotExist(MODEL_NAME, InitiationParameterCodes.INITIATION_DEFAULT_MODEL, ParameterType.MODEL, parametersMap, null, null, null, session, locale);
//		this.entity = (Entity) buildParameterIfNotExist(ENTITY_NAME, ReconciliationParameterCodes.RECONCILIATION_RECO_DATA_BLOCK, ParameterType.ENTITY, parametersMap, null, null, this.model, session, locale);
//		this.typeEntity = (Entity) buildParameterIfNotExist(RECO_ENTITY_NAME, ReconciliationParameterCodes.RECONCILIATION_RECO_TYPE_BLOCK, ParameterType.ENTITY, parametersMap, null, null, this.model, session, locale);
		Attribute attribute = (Attribute) buildParameterIfNotExist("D/C", ReconciliationParameterCodes.RECONCILIATION_DC_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap, null, ENTITY_NAME, this.model, session, locale);
		buildParameterIfNotExist("D", ReconciliationParameterCodes.RECONCILIATION_DEBIT_VALUE, ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
		buildParameterIfNotExist("C", ReconciliationParameterCodes.RECONCILIATION_CREDIT_VALUE, ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
		attribute = (Attribute) buildParameterIfNotExist("M/A", ReconciliationParameterCodes.RECONCILIATION_AUTO_MANUAL_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap, null, ENTITY_NAME, this.model, session, locale);
		buildParameterIfNotExist("M", ReconciliationParameterCodes.RECONCILIATION_MANUAL_VALUE, ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
		buildParameterIfNotExist("A", ReconciliationParameterCodes.RECONCILIATION_AUTOMATIC_VALUE, ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
		buildParameterIfNotExist("User", ReconciliationParameterCodes.RECONCILIATION_USER_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap, null, ENTITY_NAME, this.model, session, locale);
		
		
		MaterializedGrid recoLogMatGrid = null;
		Parameter parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_MAT_GRID_REPO, ParameterType.MAT_GRID);
		if (parameter != null && parameter.getLongValue() != null) {
			Optional<MaterializedGrid> result = matGridService.getRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				recoLogMatGrid = result.get();
			}
		}
		if (recoLogMatGrid == null) {
			recoLogMatGrid = buildMaterializedRepositoryGrid();
			if (recoLogMatGrid != null) {
				recoLogMatGrid = matGridService.save(recoLogMatGrid, locale);
				matGridService.publish(recoLogMatGrid, locale);
				if (parameter == null) {
					parameter = new Parameter(ReconciliationParameterCodes.RECONCILIATION_MAT_GRID_REPO, ParameterType.MAT_GRID);
				}
				parameter.setLongValue(recoLogMatGrid.getId());
				parameterRepository.save(parameter);
			}
		}
	}

	private MaterializedGrid buildMaterializedRepositoryGrid() {
		MaterializedGrid grid = new MaterializedGrid();
		grid.setCategory(GrilleCategory.SYSTEM);
		grid.setName(RECO_MAT_GRID_REPOSITOPRY_NAME);
		grid.setVisibleInShortcut(false);

		addColum(grid, "Ation", DimensionType.ATTRIBUTE, GrilleColumnCategory.RECO_ACTION);
		addColum(grid, "Filter", DimensionType.ATTRIBUTE, GrilleColumnCategory.RECO_FILTER);
		addColum(grid, "Reco type", DimensionType.ATTRIBUTE, GrilleColumnCategory.RECO_TYPE);
		addColum(grid, "Partial type", DimensionType.ATTRIBUTE, GrilleColumnCategory.RECO_PARTIAL_TYPE);
		addColum(grid, "Reco nbr", DimensionType.ATTRIBUTE, GrilleColumnCategory.RECO_NBR);
		addColum(grid, "Partial nbr",DimensionType.ATTRIBUTE, GrilleColumnCategory.RECO_PARTIAL_NBR);
		addColum(grid, "Reco date", DimensionType.PERIOD, GrilleColumnCategory.RECO_DATE);	
		addColum(grid, "A/M", DimensionType.ATTRIBUTE, GrilleColumnCategory.RECO_AM);
		addColum(grid, "User", DimensionType.ATTRIBUTE, GrilleColumnCategory.RECO_USER);
		addColum(grid, "Left amount", DimensionType.MEASURE, GrilleColumnCategory.RECO_LEFT_AMOUNT);
		addColum(grid, "Rigth amount", DimensionType.MEASURE, GrilleColumnCategory.RECO_RIGTH_AMOUNT);
		addColum(grid, "Reco amount", DimensionType.MEASURE, GrilleColumnCategory.RECO_AMOUNT);
		addColum(grid, "Balance amount", DimensionType.MEASURE, GrilleColumnCategory.RECO_BALANCE_AMOUNT);
		addColum(grid, "WO", DimensionType.MEASURE, GrilleColumnCategory.RECO_WRITEOFF);

		return grid;
	}

	protected void addColum(MaterializedGrid grid, String name, DimensionType type, GrilleColumnCategory role) {
		if (type != null) {
			int position = grid.getColumnListChangeHandler().getItems().size();			
			MaterializedGridColumn column = new MaterializedGridColumn();
			column.setName(name);
			column.setRole(role);
			column.setType(type);
			column.setPosition(position);
			column.setCategory(GrilleColumnCategory.USER);
			grid.getColumnListChangeHandler().addNew(column);
		}
		
	}

}

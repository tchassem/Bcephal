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
import com.moriset.bcephal.domain.dimension.DimensionType;
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

@Service
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class LogsParameterBuilder extends ParameterBuilder {

	protected String MODEL_NAME = "Default Model";
	protected String ENTITY_NAME = "Logs";
	protected String LOADER_GRID_NAME = "File loader logs";
	protected String ARCHIVE_GRID_NAME = "Archive logs";
	protected String RECO_GRID_NAME = "Reconciliation logs";
	protected String SCHEDULER_GRID_NAME = "Scheduler logs";
	protected String JOIN_GRID_NAME = "Join publication logs";
	
	
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
		buildRecoLogsGrid(locale);
		buildLogsGrid(InitiationParameterCodes.LOGS_ARCHIVE_MAT_GRID, ARCHIVE_GRID_NAME, locale);
		buildLogsGrid(InitiationParameterCodes.LOGS_JOIN_MAT_GRID, JOIN_GRID_NAME, locale);
		buildLogsGrid(InitiationParameterCodes.LOGS_FILE_LOADER_MAT_GRID, LOADER_GRID_NAME, locale);
		buildLogsGrid(InitiationParameterCodes.LOGS_RECO_MAT_GRID, RECO_GRID_NAME, locale);
		buildLogsGrid(InitiationParameterCodes.LOGS_SCHEDULER_MAT_GRID, SCHEDULER_GRID_NAME, locale);		
	}
	
	private void buildLogsGrid(String code, String name, Locale locale) {
		MaterializedGrid grid = null;
		Parameter parameter = parameterRepository.findByCodeAndParameterType(code, ParameterType.MAT_GRID);
		if (parameter != null && parameter.getLongValue() != null) {
			Optional<MaterializedGrid> result = matGridService.getRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				grid = result.get();
			}
		}
		if (grid == null) {
			grid = matGridService.getByName(name);
			if(grid == null) {
				grid = buildLogsGrid(name);
				grid = matGridService.save(grid, locale);
			}
			if (grid != null) {				
				matGridService.publish(grid, locale);
				if (parameter == null) {
					parameter = new Parameter(code, ParameterType.MAT_GRID);
				}
				parameter.setLongValue(grid.getId());
				parameterRepository.save(parameter);
			}
		}
	}
	
	private MaterializedGrid buildLogsGrid(String name) {
		MaterializedGrid grid = new MaterializedGrid();
		grid.setCategory(GrilleCategory.SYSTEM);
		grid.setName(name);
		grid.setVisibleInShortcut(false);

		addColum(grid, "Name", DimensionType.ATTRIBUTE, GrilleColumnCategory.LOG_NAME);
		addColum(grid, "Category", DimensionType.ATTRIBUTE, GrilleColumnCategory.LOG_CATEGORY);
		addColum(grid, "Date", DimensionType.PERIOD, GrilleColumnCategory.LOG_DATE);
		addColum(grid, "Time", DimensionType.ATTRIBUTE, GrilleColumnCategory.LOG_TIME);
		addColum(grid, "Log nbr", DimensionType.ATTRIBUTE, GrilleColumnCategory.LOG_NBR);
		addColum(grid, "Run nbr", DimensionType.ATTRIBUTE, GrilleColumnCategory.LOG_RUN_NBR);
		addColum(grid, "Action", DimensionType.ATTRIBUTE, GrilleColumnCategory.LOG_ACTION);
		addColum(grid, "User",DimensionType.ATTRIBUTE, GrilleColumnCategory.LOG_USER);
		addColum(grid, "A/M", DimensionType.ATTRIBUTE, GrilleColumnCategory.LOG_AM);
		addColum(grid, "Status", DimensionType.ATTRIBUTE, GrilleColumnCategory.LOG_STATUS);
		addColum(grid, "Log count", DimensionType.MEASURE, GrilleColumnCategory.LOG_COUNT);
		addColum(grid, "Error count", DimensionType.MEASURE, GrilleColumnCategory.LOG_ERROR_COUNT);
		addColum(grid, "Message", DimensionType.ATTRIBUTE, GrilleColumnCategory.LOG_MESSAGE);

		return grid;
	}
	

	private void buildRecoLogsGrid(Locale locale) {
		MaterializedGrid grid = null;
		Parameter parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_MAT_GRID_REPO, ParameterType.MAT_GRID);
		if (parameter != null && parameter.getLongValue() != null) {
			Optional<MaterializedGrid> result = matGridService.getRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				grid = result.get();
			}
		}
		if (grid == null) {
			grid = buildRecoLogsGrid();
			if (grid != null) {
				grid = matGridService.save(grid, locale);
				matGridService.publish(grid, locale);
				if (parameter == null) {
					parameter = new Parameter(ReconciliationParameterCodes.RECONCILIATION_MAT_GRID_REPO, ParameterType.MAT_GRID);
				}
				parameter.setLongValue(grid.getId());
				parameterRepository.save(parameter);
			}
		}
	}
	
	
	

	private MaterializedGrid buildRecoLogsGrid() {
		MaterializedGrid grid = new MaterializedGrid();
		grid.setCategory(GrilleCategory.SYSTEM);
		grid.setName(RECO_GRID_NAME);
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

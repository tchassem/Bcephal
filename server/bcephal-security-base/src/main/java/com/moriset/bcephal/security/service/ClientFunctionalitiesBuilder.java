/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.security.domain.ClientBaseObject;
import com.moriset.bcephal.security.domain.ClientFunctionality;
import com.moriset.bcephal.security.domain.RightLevel;
import com.moriset.bcephal.utils.FunctionalityCodes;

/**
 * @author Moriset
 *
 */
public class ClientFunctionalitiesBuilder {

	
	public void build(ClientBaseObject client) {
		int position = 0;
		for(String code : FunctionalityCodes.GetAll()) {
			client.getFunctionalityListChangeHandler().getNewItems().add(new ClientFunctionality(code, position++, true, getActions(code).get(0), getActions(code).get(1)));
		}
	}
	
	public void buildUpdate(ClientBaseObject client) {
		int position = client.getFunctionalityListChangeHandler().getItems().size();
		for(String code : FunctionalityCodes.GetAll()) {
			if(!contains(client.getFunctionalityListChangeHandler().getItems(), code)) {
				client.getFunctionalityListChangeHandler().getNewItems().add(new ClientFunctionality(code, position++, false, getActions(code).get(0), getActions(code).get(1)));
			}
		}
	}
	
	public void buildDeleteIfEmptyCode(ClientBaseObject client) {
		for(ClientFunctionality item : client.getFunctionalityListChangeHandler().getItems()) {
			if(item != null && item.isPersistent() && !StringUtils.hasText(item.getCode())) {
				client.getFunctionalityListChangeHandler().addDeleted(item);
			}
		}
	}
	
	private boolean contains(List<ClientFunctionality> fonctionalities, String code) {
		for(ClientFunctionality functionality : fonctionalities) {
			if(functionality != null && StringUtils.hasText(functionality.getCode()) && functionality.getCode().equals(code)) {
				return true;
			}
		}
		return false;
	}
	
	
	private static List<List<RightLevel>> getActions(String code) {
		List<RightLevel> actions = new ArrayList<>();	
		List<RightLevel> lowLevelActions = new ArrayList<>();
		if(FunctionalityCodes.SOURCING_INPUT_GRID.equals(code)
				|| FunctionalityCodes.REPORTING_REPORT_GRID.equals(code)
				|| FunctionalityCodes.REPORTING_REPORT_JOIN_GRID.equals(code)
				|| FunctionalityCodes.SOURCING_MATERIALIZED_GRID.equals(code)) {
			actions.add(RightLevel.VIEW);
			actions.add(RightLevel.EXPORT);
			actions.add(RightLevel.EDIT);
			actions.add(RightLevel.CREATE);
		}
		else if(FunctionalityCodes.SOURCING_INPUT_SPREADSHEET.equals(code)) {
			actions.add(RightLevel.VIEW);
			actions.add(RightLevel.LOAD);
			actions.add(RightLevel.EDIT);
			actions.add(RightLevel.CREATE);
		}
		else if(FunctionalityCodes.REPORTING_REPORT_SPREADSHEET.equals(code)) {
			actions.add(RightLevel.VIEW);
			actions.add(RightLevel.RUN);
			actions.add(RightLevel.EDIT);
			actions.add(RightLevel.CREATE);
		}
		else if(FunctionalityCodes.TRANSFORMATION_TREE.equals(code)) {
			actions.add(RightLevel.VIEW);
			actions.add(RightLevel.RUN);
			actions.add(RightLevel.CLEAR);
			actions.add(RightLevel.EDIT);
			actions.add(RightLevel.CREATE);
		}
		
		else if(FunctionalityCodes.BILLING_EVENT_REPOSITORY.equals(code)||
				FunctionalityCodes.BILLING_CLIENT_EVENT_REPOSITORY.equals(code)||
				FunctionalityCodes.BILLING_JOIN_EVENT_REPOSITORY.equals(code)) {
			actions.add(RightLevel.VIEW);
			actions.add(RightLevel.EDIT);
		}
		
		
		else if(FunctionalityCodes.PROJECT_BACKUP_SCHEDULER_LOG.equals(code)
				|| FunctionalityCodes.SOURCING_FILE_LOADER_SCHEDULER_LOG.equals(code)
				//|| FunctionalityCodes.SCHEDULER_PLANNER_SCHEDULER_LOG.equals(code)
//				|| FunctionalityCodes.SOURCING_FILE_LOADER_SCHEDULER_LOG.equals(code)
//				|| FunctionalityCodes.TRANSFORMATION_ROUTINE_SCHEDULER_LOG.equals(code)
//				|| FunctionalityCodes.REPORTING_REPORT_JOIN_GRID_SCHEDULER_LOG.equals(code)
//				|| FunctionalityCodes.DASHBOARDING_ALARM_SCHEDULER_LOG.equals(code)
//				|| FunctionalityCodes.RECONCILIATION_AUTO_RECO_SCHEDULER_LOG.equals(code)
//				|| FunctionalityCodes.ACCOUNTING_BOOKING_MODEL_SCHEDULER_LOG.equals(code)
//				|| FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER_LOG.equals(code)
				) {
			actions.add(RightLevel.VIEW);
			actions.add(RightLevel.EDIT);
		}
		else {
			actions.add(RightLevel.VIEW);
			actions.add(RightLevel.EDIT);
			actions.add(RightLevel.CREATE);
		}
		List<List<RightLevel>> result = new ArrayList<>();
		result.add(actions);
		result.add(lowLevelActions);
		return result;
	}
	
}

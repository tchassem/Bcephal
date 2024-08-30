/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.security.domain.Profile;
import com.moriset.bcephal.security.domain.Right;
import com.moriset.bcephal.security.domain.RightLevel;
import com.moriset.bcephal.security.domain.User;
import com.moriset.bcephal.utils.FunctionalityCodes;

import lombok.Data;

/**
 * @author Moriset
 *
 */
@Data
public class SubPrivilegeObserver {
	
	@JsonIgnore
	private User user;
	
	@JsonIgnore
	private Profile profile;
	
	@JsonIgnore
	private List<Right> rights;
	
	@JsonIgnore
	private String functionalityCode;
	
	@JsonIgnore
	private List<String> clientFunctionalities;
	
	public List<RightLevel> getRightLevelAvailable(){
		List<RightLevel> level = new ArrayList<>(
				Arrays.asList(RightLevel.NONE,RightLevel.VIEW,RightLevel.EDIT,RightLevel.DELETE,RightLevel.ALL));
		
		if (functionalityCode.equals(FunctionalityCodes.SOURCING_INPUT_GRID)){
			level.add(RightLevel.EXPORT);
			level.add(RightLevel.PUBLISH);
			level.add(RightLevel.DELETE_ROWS);
			level.add(RightLevel.FIND_REPLACE);
		}
		else if (functionalityCode.equals(FunctionalityCodes.SOURCING_MATERIALIZED_GRID)){
			level.add(RightLevel.EXPORT);
			level.add(RightLevel.PUBLISH);
			level.add(RightLevel.DELETE_ROWS);
			level.add(RightLevel.FIND_REPLACE);
		}
		else if (functionalityCode.equals(FunctionalityCodes.SOURCING_FILE_LOADER)){
			level.add(RightLevel.LOAD);
		}
		else if (functionalityCode.equals(FunctionalityCodes.TRANSFORMATION_ROUTINE)){
			level.add(RightLevel.RUN);
		}
		else if (functionalityCode.equals(FunctionalityCodes.REPORTING_REPORT_GRID)){
			level.add(RightLevel.EXPORT);
			level.add(RightLevel.PUBLISH);
		}
		else if (functionalityCode.equals(FunctionalityCodes.REPORTING_CHART) || functionalityCode.equals(FunctionalityCodes.REPORTING_PIVOT_TABLE)){
			level.add(RightLevel.EXPORT);
			level.add(RightLevel.PUBLISH);
		}
		else if (functionalityCode.equals(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID)){
			level.add(RightLevel.EXPORT);
			level.add(RightLevel.PUBLISH);
			level.add(RightLevel.MATERIALIZE);
		}		
		else if (functionalityCode.equals(FunctionalityCodes.DASHBOARDING_DASHBOARD)){
			level.add(RightLevel.PUBLISH);
		}
		else if (functionalityCode.equals(FunctionalityCodes.DASHBOARDING_ALARM)){
			level.add(RightLevel.RUN);
		}	
		else if (functionalityCode.equals(FunctionalityCodes.SETTINGS_INTEGRATION_SERVICE)){
			level.add(RightLevel.RUN);
		}	
		else if (functionalityCode.equals(FunctionalityCodes.RECONCILIATION_FILTER)){
			level.add(RightLevel.RUN);
			level.add(RightLevel.RESET);
			level.add(RightLevel.EXPORT);
			level.add(RightLevel.PUBLISH);
		}	
		else if (functionalityCode.equals(FunctionalityCodes.RECONCILIATION_MODEL)){
			level.add(RightLevel.RUN);
			level.add(RightLevel.RESET);
			level.add(RightLevel.EXPORT);
			level.add(RightLevel.PUBLISH);
		}	
		else if (functionalityCode.equals(FunctionalityCodes.RECONCILIATION_UNION)) {
			level.add(RightLevel.RUN);
			level.add(RightLevel.EXPORT);
		}
		else if (functionalityCode.equals(FunctionalityCodes.RECONCILIATION_AUTO_RECO)){
			level.add(RightLevel.RUN);
		}
		else if (functionalityCode.equals(FunctionalityCodes.SCHEDULER_PLANNER) || functionalityCode.equals(FunctionalityCodes.SCHEDULER_PLANNER_SCHEDULER)){
			level.add(RightLevel.RUN);
		}
		
		else if (functionalityCode.equals(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG)){
			level.add(RightLevel.RUN);
		}
		else if (functionalityCode.equals(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE)){
			level.add(RightLevel.LOAD);
		}
		
		
		return level;
	}
	
	private Boolean hasRole(String code, RightLevel level) {
		if(isAdministrator()) {
			return true;
		}
		if(!isClientFunctionalityAllowed(code)) {
			return false;
		}		
		if(profile != null && profile.getType() != null && profile.getType().isSuperUser()) {
			if(FunctionalityCodes.ADMINISTRATION_CLIENT.equals(code)) {
				return false;
			}
			return true;
		}
		for(Right right : rights) {					
			if( level != null && level.equals(right.getLevel())) {
				if(level.equals(RightLevel.NONE)) {
					return false;
				}else {
					return true;
				}
			}
		}
		return rights.size() > 0 ? false : null;
	}
	
	private boolean isClientFunctionalityAllowed(String code) {		
		return this.clientFunctionalities != null && this.clientFunctionalities.contains(code);		
	}
	
	public boolean isAdministrator() {
		return (profile == null && user != null && user.getType() != null && user.getType().isAdministrator())
				|| (profile != null && profile.getType() != null && profile.getType().isAdministrator());
	}
	
	public Boolean isView() {		
		Boolean role =  hasRole(functionalityCode, RightLevel.VIEW);
		if(role == null) {
			return isEdit();
		}
		return role || isEdit();
	}
	
	public Boolean isEdit() {
		Boolean role =  hasRole(functionalityCode, RightLevel.EDIT);
		if(role == null) {
			return isCreate();
		}
		return role || isCreate();
	}
	
	public Boolean isCreate() {	
		Boolean role =  hasRole(functionalityCode, RightLevel.CREATE);
		if(role == null) {
			return isAll();
		}
		return role || isAll();
	}
	
	public Boolean isAll() {		
		return hasRole(functionalityCode, RightLevel.ALL);	
	}
	
	public Boolean isValidate() {		
		return hasRole(functionalityCode, RightLevel.VALIDATE);	
	}
	
	public Boolean isAction() {		
		return hasRole(functionalityCode, RightLevel.ACTION);	
	}
	
	public Boolean isRun() {		
		return hasRole(functionalityCode, RightLevel.RUN);	
	}
	
	public Boolean isClear() {		
		return hasRole(functionalityCode, RightLevel.CLEAR);	
	}
	
	public Boolean isExport() {		
		return hasRole(functionalityCode, RightLevel.EXPORT);	
	}
	
	public Boolean isLoad() {		
		return hasRole(functionalityCode, RightLevel.LOAD);	
	}
	
	public Boolean isReset() {		
		return hasRole(functionalityCode, RightLevel.RESET);	
	}
	
	public Boolean isPublish() {		
		return hasRole(functionalityCode, RightLevel.PUBLISH);	
	}
	
	public Boolean isFindReplace() {		
		return hasRole(functionalityCode, RightLevel.FIND_REPLACE);	
	}
	
	public Boolean isDeleteRows() {		
		return hasRole(functionalityCode, RightLevel.DELETE_ROWS);	
	}
	
	public Boolean isMaterialize() {		
		return hasRole(functionalityCode, RightLevel.MATERIALIZE);	
	}
	
	public Boolean isNone() {		
		return hasRole(functionalityCode, RightLevel.NONE);	
	}
}

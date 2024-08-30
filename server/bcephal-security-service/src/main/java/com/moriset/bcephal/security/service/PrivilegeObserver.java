/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class PrivilegeObserver {
	
	@JsonIgnore
	private User user;
	
	@JsonIgnore
	private Profile profile;
	
	@JsonIgnore
	private List<Right> rights;
	
	@JsonIgnore
	private List<String> clientFunctionalities;
	
	
	
	
	public boolean isProjectAllowed() {
		return hasRole(FunctionalityCodes.PROJECT);
	}
	
	public boolean isProjectViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT,RightLevel.VIEW);
	}
	
	public boolean isProjectActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT,RightLevel.VIEW);
	}
	
	public boolean isProjectEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT,RightLevel.EDIT);
	}
	
	public boolean isProjectCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT,RightLevel.CREATE);
	}
	
	public boolean isProjectBackupAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_BACKUP);
	}
	
	public boolean isProjectBackupViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_BACKUP,RightLevel.VIEW);
	}
	
	public boolean isProjectBackupActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_BACKUP,RightLevel.ACTION);
	}
	
	public boolean isProjectBackupEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_BACKUP,RightLevel.EDIT);
	}
	
	public boolean isProjectBackupCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_BACKUP,RightLevel.CREATE);
	}
	
	public boolean isProjectBackupSchedulerAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_BACKUP_SCHEDULER);
	}
	
	public boolean isProjectBackupSchedulerViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_BACKUP_SCHEDULER,RightLevel.VIEW);
	}
	
	public boolean isProjectBackupSchedulerActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_BACKUP_SCHEDULER,RightLevel.ACTION);
	}
	
	public boolean isProjectBackupSchedulerEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_BACKUP_SCHEDULER,RightLevel.EDIT);
	}
	
	public boolean isProjectBackupSchedulerCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_BACKUP_SCHEDULER,RightLevel.CREATE);
	}
	
	public boolean isProjectImportAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_IMPORT);
	}
	
	public boolean isProjectImportViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_IMPORT,RightLevel.VIEW);
	}
	
	public boolean isProjectImportActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_IMPORT,RightLevel.ACTION);
	}
	
	public boolean isProjectImportEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_IMPORT,RightLevel.EDIT);
	}
	
	public boolean isProjectImportCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_IMPORT,RightLevel.CREATE);
	}
	
	public boolean isProjectAccessRightAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_ACCESS_RIGHT);
	}
	
	public boolean isProjectAccessRightViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_ACCESS_RIGHT,RightLevel.VIEW);
	}
	
	public boolean isProjectAccessRightActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_ACCESS_RIGHT,RightLevel.ACTION);
	}
	
	public boolean isProjectAccessRightEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_ACCESS_RIGHT,RightLevel.EDIT);
	}
	
	public boolean isProjectAccessRightCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_ACCESS_RIGHT,RightLevel.CREATE);
	}
	
	
	public boolean isInitiationAllowed() {
		return hasRole(FunctionalityCodes.INITIATION);
	}
	
	public boolean isInitiationViewAllowed() {
		return hasRole(FunctionalityCodes.INITIATION,RightLevel.VIEW);
	}
	
	public boolean isInitiationActionAllowed() {
		return hasRole(FunctionalityCodes.INITIATION,RightLevel.ACTION);
	}
	
	public boolean isInitiationEditAllowed() {
		return hasRole(FunctionalityCodes.INITIATION,RightLevel.EDIT);
	}
	
	public boolean isInitiationCreateAllowed() {
		return hasRole(FunctionalityCodes.INITIATION,RightLevel.CREATE);
	}
	
	public boolean isInitiationModelAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_MODEL);
	}
	
	public boolean isInitiationModelViewAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_MODEL,RightLevel.VIEW);
	}
	
	public boolean isInitiationModelActionAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_MODEL,RightLevel.ACTION);
	}
	
	public boolean isInitiationModelEditAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_MODEL,RightLevel.EDIT);
	}
	
	public boolean isInitiationModelCreateAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_MODEL,RightLevel.CREATE);
	}
	
	public boolean isInitiationMeasureAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_MEASURE);
	}
	
	public boolean isInitiationMeasureViewAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_MEASURE,RightLevel.VIEW);
	}
	
	public boolean isInitiationMeasureActionAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_MEASURE,RightLevel.ACTION);
	}
	
	public boolean isInitiationMeasureEditAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_MEASURE,RightLevel.EDIT);
	}
	
	public boolean isInitiationMeasureCreateAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_MEASURE,RightLevel.CREATE);
	}
		
	
	public boolean isInitiationCalculatedMeasureAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_CALCULATED_MEASURE);
	}
	
	public boolean isInitiationCalculatedMeasureViewAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_CALCULATED_MEASURE,RightLevel.VIEW);
	}
	
	public boolean isInitiationCalculatedMeasureActionAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_CALCULATED_MEASURE,RightLevel.ACTION);
	}
	
	public boolean isInitiationCalculatedMeasureEditAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_CALCULATED_MEASURE,RightLevel.EDIT);
	}
	
	public boolean isInitiationCalculatedMeasureCreateAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_CALCULATED_MEASURE,RightLevel.CREATE);
	}
	
	
	public boolean isInitiationPeriodAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_PERIOD);
	}
	
	public boolean isInitiationPeriodViewAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_PERIOD,RightLevel.VIEW);
	}
	
	public boolean isInitiationPeriodActionAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_PERIOD,RightLevel.ACTION);
	}
	
	public boolean isInitiationPeriodEditAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_PERIOD,RightLevel.EDIT);
	}
	
	public boolean isInitiationPeriodCreateAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_PERIOD,RightLevel.CREATE);
	}
	
	public boolean isInitiationCalendarAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_CALENDAR);
	}
	
	public boolean isInitiationCalendarViewAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_CALENDAR,RightLevel.VIEW);
	}
	
	public boolean isInitiationCalendarActionAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_CALENDAR,RightLevel.ACTION);
	}
	
	public boolean isInitiationCalendarEditAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_CALENDAR,RightLevel.EDIT);
	}
	
	public boolean isInitiationCalendarCreateAllowed() {
		return hasRole(FunctionalityCodes.INITIATION_CALENDAR,RightLevel.CREATE);
	}
	
	
	public boolean isSourcingAllowed() {
		return hasRole(FunctionalityCodes.SOURCING);
	}
	
	public boolean isSourcingViewAllowed() {
		return hasRole(FunctionalityCodes.SOURCING,RightLevel.VIEW);
	}
	

	
	public boolean isSourcingActionAllowed() {
		return hasRole(FunctionalityCodes.SOURCING,RightLevel.ACTION);
	}
	
	public boolean isSourcingEditAllowed() {
		return hasRole(FunctionalityCodes.SOURCING,RightLevel.EDIT);
	}
	
	public boolean isSourcingCreateAllowed() {
		return hasRole(FunctionalityCodes.SOURCING,RightLevel.CREATE);
	}
		
	public boolean isSourcingInputGridAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_INPUT_GRID);
	}
	
	public boolean isSourcingInputGridViewAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_INPUT_GRID,RightLevel.VIEW);
	}
	
	public boolean isSourcingInputGridActionAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_INPUT_GRID,RightLevel.ACTION);
	}
	
	public boolean isSourcingInputGridExportAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_INPUT_GRID,RightLevel.EXPORT);
	}
	
	public boolean isSourcingInputGridEditAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_INPUT_GRID,RightLevel.EDIT);
	}
	
	public boolean isSourcingInputGridCreateAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_INPUT_GRID,RightLevel.CREATE);
	}
	
	public boolean isSourcingMaterializedGridAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_MATERIALIZED_GRID);
	}
	
	public boolean isSourcingMaterializedGridViewAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_MATERIALIZED_GRID,RightLevel.VIEW);
	}
	
	public boolean isSourcingMaterializedGridActionAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_MATERIALIZED_GRID,RightLevel.ACTION);
	}
	
	public boolean isSourcingMaterializedGridExportAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_MATERIALIZED_GRID,RightLevel.EXPORT);
	}
	
	public boolean isSourcingMaterializedGridEditAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_MATERIALIZED_GRID,RightLevel.EDIT);
	}
	
	public boolean isSourcingMaterializedGridCreateAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_MATERIALIZED_GRID,RightLevel.CREATE);
	}
	
	public boolean isSourcingInputSpreadsheetAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_INPUT_SPREADSHEET);
	}
	
	public boolean isSourcingInputSpreadsheetViewAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_INPUT_SPREADSHEET,RightLevel.VIEW);
	}
	
	public boolean isSourcingInputSpreadsheetActionAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_INPUT_SPREADSHEET,RightLevel.ACTION);
	}
	
	public boolean isSourcingInputSpreadsheetLoadAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_INPUT_SPREADSHEET,RightLevel.LOAD);
	}
	
	public boolean isSourcingInputSpreadsheetEditAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_INPUT_SPREADSHEET,RightLevel.EDIT);
	}
	
	public boolean isSourcingInputSpreadsheetCreateAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_INPUT_SPREADSHEET,RightLevel.CREATE);
	}
	
	public boolean isSourcingFileLoaderAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_FILE_LOADER);
	}

	public boolean isSourcingFileLoaderViewAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_FILE_LOADER,RightLevel.VIEW);
	}

	public boolean isSourcingFileLoaderActionAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_FILE_LOADER,RightLevel.ACTION);
	}
	
	public boolean isSourcingFileLoaderEditAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_FILE_LOADER,RightLevel.EDIT);
	}
	
	public boolean isSourcingFileLoaderCreateAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_FILE_LOADER,RightLevel.CREATE);
	}
	
	
	
	public boolean isSourcingUserLoaderAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_USER_LOADER);
	}

	public boolean isSourcingUserLoaderViewAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_USER_LOADER,RightLevel.VIEW);
	}

	public boolean isSourcingUserLoaderActionAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_USER_LOADER,RightLevel.ACTION);
	}
	
	public boolean isSourcingUserLoaderEditAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_USER_LOADER,RightLevel.EDIT);
	}
	
	public boolean isSourcingUserLoaderCreateAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_USER_LOADER,RightLevel.CREATE);
	}
	
	
	public boolean isSourcingUserLoaderConfigAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_USER_LOADER_CONFIG);
	}

	public boolean isSourcingUserLoaderConfigViewAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_USER_LOADER_CONFIG,RightLevel.VIEW);
	}

	public boolean isSourcingUserLoaderConfigActionAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_USER_LOADER_CONFIG,RightLevel.ACTION);
	}
	
	public boolean isSourcingUserLoaderConfigEditAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_USER_LOADER_CONFIG,RightLevel.EDIT);
	}
	
	public boolean isSourcingUserLoaderConfigCreateAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_USER_LOADER_CONFIG,RightLevel.CREATE);
	}

	
	
	
	public boolean isSourcingFileLoaderSchedulerAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_FILE_LOADER_SCHEDULER);
	}
	
	public boolean isSourcingFileLoaderSchedulerViewAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_FILE_LOADER_SCHEDULER,RightLevel.VIEW);
	}
	
	public boolean isSourcingFileLoaderSchedulerActionAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_FILE_LOADER_SCHEDULER,RightLevel.ACTION);
	}
	
	public boolean isSourcingFileLoaderSchedulerEditAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_FILE_LOADER_SCHEDULER,RightLevel.EDIT);
	}
	
	public boolean isSourcingFileLoaderSchedulerCreateAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_FILE_LOADER_SCHEDULER,RightLevel.CREATE);
	}
	
	public boolean isSourcingFileLoaderSchedulerLogAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_FILE_LOADER_SCHEDULER_LOG);
	}
	
	public boolean isSourcingFileLoaderSchedulerLogViewAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_FILE_LOADER_SCHEDULER_LOG,RightLevel.VIEW);
	}
	
	public boolean isSourcingFileLoaderSchedulerLogActionAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_FILE_LOADER_SCHEDULER_LOG,RightLevel.ACTION);
	}
	
	public boolean isSourcingFileLoaderSchedulerLogEditAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_FILE_LOADER_SCHEDULER_LOG,RightLevel.EDIT);
	}
	
	public boolean isSourcingSpotAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_SPOT);
	}
	
	public boolean isSourcingSpotViewAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_SPOT,RightLevel.VIEW);
	}
	
	public boolean isSourcingSpotActionAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_SPOT,RightLevel.ACTION);
	}
	
	public boolean isSourcingSpotEditAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_SPOT,RightLevel.EDIT);
	}
	
	public boolean isSourcingSpotCreateAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_SPOT,RightLevel.CREATE);
	}
	
	public boolean isSourcingLingAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_LINK);
	}
	
	public boolean isSourcingLingViewAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_LINK,RightLevel.VIEW);
	}
	
	public boolean isSourcingLingActionAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_LINK,RightLevel.ACTION);
	}
	
	public boolean isSourcingLingEditAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_LINK,RightLevel.EDIT);
	}
	
	public boolean isSourcingLingCreateAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_LINK,RightLevel.CREATE);
	}
	
	
	public boolean isSourcingLoaderTemplateAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_LOADER_TEMPLATE);
	}

	public boolean isSourcingLoaderTemplateViewAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_LOADER_TEMPLATE,RightLevel.VIEW);
	}

	public boolean isSourcingLoaderTemplateActionAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_LOADER_TEMPLATE,RightLevel.ACTION);
	}
	
	public boolean isSourcingLoaderTemplateEditAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_LOADER_TEMPLATE,RightLevel.EDIT);
	}
	
	public boolean isSourcingLoaderTemplateCreateAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_LOADER_TEMPLATE,RightLevel.CREATE);
	}

	
	public boolean isTransformationAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION);
	}

	public boolean isTransformationViewAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION,RightLevel.VIEW);
	}

	public boolean isTransformationActionAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION,RightLevel.ACTION);
	}

	public boolean isTransformationEditAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION,RightLevel.EDIT);
	}

	public boolean isTransformationCreateAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION,RightLevel.CREATE);
	}

	public boolean isTransformationTreeAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE);
	}

	public boolean isTransformationTreeViewAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE,RightLevel.VIEW);
	}

	public boolean isTransformationTreeActionAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE,RightLevel.ACTION);
	}

	public boolean isTransformationTreeRunAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE,RightLevel.RUN);
	}

	public boolean isTransformationTreeClearAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE,RightLevel.CLEAR);
	}

	public boolean isTransformationTreeEditAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE,RightLevel.EDIT);
	}

	public boolean isTransformationTreeCreateAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE,RightLevel.CREATE);
	}
	
	public boolean isTransformationTreeSchedulerAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE_SCHEDULER);
	}
	
	public boolean isTransformationTreeSchedulerViewAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE_SCHEDULER,RightLevel.VIEW);
	}
	
	public boolean isTransformationTreeSchedulerActionAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE_SCHEDULER,RightLevel.ACTION);
	}
	
	public boolean isTransformationTreeSchedulerEditAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE_SCHEDULER,RightLevel.EDIT);
	}
	
	public boolean isTransformationTreeSchedulerCreateAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE_SCHEDULER,RightLevel.CREATE);
	}

	public boolean isTransformationTreeSchedulerLogAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE_SCHEDULER_LOG);
	}
	
	public boolean isTransformationTreeSchedulerLogViewAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE_SCHEDULER_LOG,RightLevel.VIEW);
	}

	
	public boolean isTransformationTreeSchedulerLogActionAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE_SCHEDULER_LOG,RightLevel.ACTION);
	}
	
	public boolean isTransformationTreeSchedulerLogEditAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_TREE_SCHEDULER_LOG,RightLevel.EDIT);
	}

	public boolean isTransformationRoutineAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE);
	}
	
	public boolean isTransformationRoutineViewAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE,RightLevel.VIEW);
	}
	
	public boolean isTransformationRoutineActionAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE,RightLevel.ACTION);
	}
	
	public boolean isTransformationRoutineEditAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE,RightLevel.EDIT);
	}
	
	public boolean isTransformationRoutineCreateAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE,RightLevel.CREATE);
	}
	
	public boolean isTransformationRoutineSchedulerAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE_SCHEDULER);
	}
	
	public boolean isTransformationRoutineSchedulerViewAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE_SCHEDULER,RightLevel.VIEW);
	}
	
	public boolean isTransformationRoutineSchedulerActionAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE_SCHEDULER,RightLevel.ACTION);
	}
	
	public boolean isTransformationRoutineSchedulerEditAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE_SCHEDULER,RightLevel.EDIT);
	}
	
	public boolean isTransformationRoutineSchedulerCreateAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE_SCHEDULER,RightLevel.CREATE);
	}
	
	public boolean isTransformationRoutineSchedulerLogAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE_SCHEDULER_LOG);
	}
	
	public boolean isTransformationRoutineSchedulerLogViewAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE_SCHEDULER_LOG,RightLevel.VIEW);
	}
	
	public boolean isTransformationRoutineSchedulerLogActionAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE_SCHEDULER_LOG,RightLevel.ACTION);
	}
	
	public boolean isTransformationRoutineSchedulerLogEditAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE_SCHEDULER_LOG,RightLevel.EDIT);
	}
	
	public boolean isTransformationRoutineSchedulerLogCreateAllowed() {
		return hasRole(FunctionalityCodes.TRANSFORMATION_ROUTINE_SCHEDULER_LOG,RightLevel.CREATE);
	}
	
	
	public boolean isReportingAllowed() {
		return hasRole(FunctionalityCodes.REPORTING);
	}
	
	public boolean isReportingViewAllowed() {
		return hasRole(FunctionalityCodes.REPORTING,RightLevel.VIEW);
	}
	
	public boolean isReportingActionAllowed() {
		return hasRole(FunctionalityCodes.REPORTING,RightLevel.ACTION);
	}
	
	public boolean isReportingEditAllowed() {
		return hasRole(FunctionalityCodes.REPORTING,RightLevel.EDIT);
	}
	
	public boolean isReportingCreateAllowed() {
		return hasRole(FunctionalityCodes.REPORTING,RightLevel.CREATE);
	}
	
	public boolean isReportingReportGridAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_GRID);
	}
	
	public boolean isReportingReportGridViewAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_GRID,RightLevel.VIEW);
	}
	
	public boolean isReportingReportGridActionAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_GRID,RightLevel.ACTION);
	}
	
	public boolean isReportingReportGridExportAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_GRID,RightLevel.EXPORT);
	}
	
	public boolean isReportingReportGridEditAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_GRID,RightLevel.EDIT);
	}
	
	public boolean isReportingReportGridCreateAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_GRID,RightLevel.CREATE);
	}
	
	
	public boolean isReportingUnionGridAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_UNION_GRID);
	}
	
	public boolean isReportingUnionGridViewAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_UNION_GRID,RightLevel.VIEW);
	}
	
	public boolean isReportingUnionGridActionAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_UNION_GRID,RightLevel.ACTION);
	}
	
	public boolean isReportingUnionGridExportAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_UNION_GRID,RightLevel.EXPORT);
	}
	
	public boolean isReportingUnionGridEditAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_UNION_GRID,RightLevel.EDIT);
	}
	
	public boolean isReportingUnionGridCreateAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_UNION_GRID,RightLevel.CREATE);
	}
	
	
	public boolean isReportingReportSpreadsheetAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_SPREADSHEET);
	}
	
	public boolean isReportingReportSpreadsheetViewAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_SPREADSHEET,RightLevel.VIEW);
	}
	
	public boolean isReportingReportSpreadsheetActionAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_SPREADSHEET,RightLevel.ACTION);
	}
	
	public boolean isReportingReportSpreadsheetEditAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_SPREADSHEET,RightLevel.EDIT);
	}
	
	public boolean isReportingReportSpreadsheetCreateAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_SPREADSHEET,RightLevel.CREATE);
	}

	public boolean isReportingPivotTableAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_PIVOT_TABLE);
	}

	public boolean isReportingPivotTableViewAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_PIVOT_TABLE,RightLevel.VIEW);
	}

	public boolean isReportingPivotTableActionAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_PIVOT_TABLE,RightLevel.ACTION);
	}

	public boolean isReportingPivotTableEditAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_PIVOT_TABLE,RightLevel.EDIT);
	}

	public boolean isReportingPivotTableCreateAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_PIVOT_TABLE,RightLevel.CREATE);
	}
	
	public boolean isReportingChartAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_CHART);
	}
	
	public boolean isReportingChartViewAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_CHART,RightLevel.VIEW);
	}

	public boolean isReportingChartActionAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_CHART,RightLevel.ACTION);
	}
	
	public boolean isReportingChartEditAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_CHART,RightLevel.EDIT);
	}
	
	public boolean isReportingChartCreateAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_CHART,RightLevel.CREATE);
	}

	
	public boolean isDashboardingAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING);
	}

	public boolean isDashboardingViewAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING,RightLevel.VIEW);
	}
	


	public boolean isDashboardingActionAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING,RightLevel.ACTION);
	}

	public boolean isDashboardingEditAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING,RightLevel.EDIT);
	}

	public boolean isDashboardingCreateAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING,RightLevel.CREATE);
	}
	
	public boolean isDashboardingDashboardAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_DASHBOARD);
	}
	
	public boolean isDashboardingDashboardViewAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_DASHBOARD,RightLevel.VIEW);
	}
	
	public boolean isDashboardingDashboardActionAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_DASHBOARD,RightLevel.ACTION);
	}
	
	
	public boolean isDashboardingDashboardEditAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_DASHBOARD,RightLevel.EDIT);
	}
	
	public boolean isDashboardingDashboardCreateAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_DASHBOARD,RightLevel.CREATE);
	}

	public boolean isDashboardingAlarmAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM);
	}

	public boolean isDashboardingAlarmViewAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM,RightLevel.VIEW);
	}

	public boolean isDashboardingAlarmActionAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM,RightLevel.ACTION);
	}

	public boolean isDashboardingAlarmEditAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM,RightLevel.EDIT);
	}

	public boolean isDashboardingAlarmCreateAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM,RightLevel.CREATE);
	}

	public boolean isDashboardingAlarmSchedulerAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM_SCHEDULER);
	}

	public boolean isDashboardingAlarmSchedulerViewAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM_SCHEDULER,RightLevel.VIEW);
	}

	public boolean isDashboardingAlarmSchedulerActionAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM_SCHEDULER,RightLevel.ACTION);
	}

	public boolean isDashboardingAlarmSchedulerEditAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM_SCHEDULER,RightLevel.EDIT);
	}

	public boolean isDashboardingAlarmSchedulerCreateAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM_SCHEDULER,RightLevel.CREATE);
	}

	public boolean isDashboardingAlarmSchedulerLogAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM_SCHEDULER_LOG);
	}

	public boolean isDashboardingAlarmSchedulerLogViewAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM_SCHEDULER_LOG,RightLevel.VIEW);
	}

	public boolean isDashboardingAlarmSchedulerLogActionAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM_SCHEDULER_LOG,RightLevel.ACTION);
	}

	public boolean isDashboardingAlarmSchedulerLogEditAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM_SCHEDULER_LOG,RightLevel.EDIT);
	}

	public boolean isDashboardingAlarmSchedulerLogCreateAllowed() {
		return hasRole(FunctionalityCodes.DASHBOARDING_ALARM_SCHEDULER_LOG,RightLevel.CREATE);
	}
	

	public boolean isReconciliationAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION);
	}

	public boolean isReconciliationViewAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION,RightLevel.VIEW);
	}

	public boolean isReconciliationActionAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION,RightLevel.ACTION);
	}

	public boolean isReconciliationEditAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION,RightLevel.EDIT);
	}

	public boolean isReconciliationCreateAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION,RightLevel.CREATE);
	}

	public boolean isReconciliationFilterAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_FILTER);
	}

	public boolean isReconciliationFilterViewAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_FILTER,RightLevel.VIEW);
	}

	public boolean isReconciliationFilterActionAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_FILTER,RightLevel.ACTION);
	}

	public boolean isReconciliationFilterEditAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_FILTER,RightLevel.EDIT);
	}

	public boolean isReconciliationFilterCreateAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_FILTER,RightLevel.CREATE);
	}

	public boolean isReconciliationModelAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_MODEL);
	}

	public boolean isReconciliationModelViewAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_MODEL,RightLevel.VIEW);
	}

	public boolean isReconciliationModelActionAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_MODEL,RightLevel.ACTION);
	}

	public boolean isReconciliationModelEditAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_MODEL,RightLevel.EDIT);
	}

	public boolean isReconciliationModelCreateAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_MODEL,RightLevel.CREATE);
	}

	public boolean isReconciliationUnionAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_UNION);
	}

	public boolean isReconciliationUnionViewAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_UNION, RightLevel.VIEW);
	}

	public boolean isReconciliationUnionActionAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_UNION, RightLevel.ACTION);
	}

	public boolean isReconciliationUnionEditAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_UNION, RightLevel.EDIT);
	}

	public boolean isReconciliationUnionCreateAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_UNION, RightLevel.CREATE);
	}	

	public boolean isReconciliationAutoRecoAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO);
	}

	public boolean isReconciliationAutoRecoViewAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO,RightLevel.VIEW);
	}

	public boolean isReconciliationAutoRecoActionAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO,RightLevel.ACTION);
	}

	public boolean isReconciliationAutoRecoEditAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO,RightLevel.EDIT);
	}

	public boolean isReconciliationAutoRecoCreateAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO,RightLevel.CREATE);
	}

	public boolean isReconciliationAutoRecoSchedulerAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO_SCHEDULER);
	}

	public boolean isReconciliationAutoRecoSchedulerViewAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO_SCHEDULER,RightLevel.VIEW);
	}

	public boolean isReconciliationAutoRecoSchedulerActionAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO_SCHEDULER,RightLevel.ACTION);
	}

	public boolean isReconciliationAutoRecoSchedulerEditAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO_SCHEDULER,RightLevel.EDIT);
	}

	public boolean isReconciliationAutoRecoSchedulerCreateAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO_SCHEDULER,RightLevel.CREATE);
	}

	public boolean isReconciliationAutoRecoSchedulerLogAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO_SCHEDULER_LOG);
	}

	public boolean isReconciliationAutoRecoSchedulerLogViewAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO_SCHEDULER_LOG,RightLevel.VIEW);
	}

	public boolean isReconciliationAutoRecoSchedulerLogActionAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO_SCHEDULER_LOG,RightLevel.ACTION);
	}

	public boolean isReconciliationAutoRecoSchedulerLogEditAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO_SCHEDULER_LOG,RightLevel.EDIT);
	}

	public boolean isReconciliationAutoRecoSchedulerLogCreateAllowed() {
		return hasRole(FunctionalityCodes.RECONCILIATION_AUTO_RECO_SCHEDULER_LOG,RightLevel.CREATE);
	}

	
	public boolean isDataManagementAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT);
	}

	public boolean isDataManagementViewAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT,RightLevel.VIEW);
	}

	public boolean isDataManagementActionAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT,RightLevel.ACTION);
	}

	public boolean isDataManagementEditAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT,RightLevel.EDIT);
	}

	public boolean isDataManagementCreateAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT,RightLevel.CREATE);
	}

	public boolean isDataManagementArchiveAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE);
	}

	public boolean isDataManagementArchiveViewAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE,RightLevel.VIEW);
	}

	public boolean isDataManagementArchiveActionAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE,RightLevel.ACTION);
	}

	public boolean isDataManagementArchiveEditAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE,RightLevel.EDIT);
	}

	public boolean isDataManagementArchiveCreateAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE,RightLevel.CREATE);
	}

	public boolean isDataManagementArchiveLogAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_LOG);
	}

	public boolean isDataManagementArchiveLogViewAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_LOG,RightLevel.VIEW);
	}

	public boolean isDataManagementArchiveLogActionAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_LOG,RightLevel.ACTION);
	}

	public boolean isDataManagementArchiveLogEditAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_LOG,RightLevel.EDIT);
	}

	public boolean isDataManagementArchiveLogCreateAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_LOG,RightLevel.CREATE);
	}

	public boolean isDataManagementArchiveConfigAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG);
	}

	public boolean isDataManagementArchiveConfigViewAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG,RightLevel.VIEW);
	}

	public boolean isDataManagementArchiveConfigActionAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG,RightLevel.ACTION);
	}

	public boolean isDataManagementArchiveConfigEditAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG,RightLevel.EDIT);
	}

	public boolean isDataManagementArchiveConfigCreateAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG,RightLevel.CREATE);
	}

	public boolean isDataManagementArchiveConfigSchedulerAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER);
	}

	public boolean isDataManagementArchiveConfigSchedulerViewAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER,RightLevel.VIEW);
	}

	public boolean isDataManagementArchiveConfigSchedulerActionAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER,RightLevel.ACTION);
	}

	public boolean isDataManagementArchiveConfigSchedulerEditAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER,RightLevel.EDIT);
	}

	public boolean isDataManagementArchiveConfigSchedulerCreateAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER,RightLevel.CREATE);
	}
	
	public boolean isDataManagementArchiveConfigSchedulerLogAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER_LOG);
	}
	
	public boolean isDataManagementArchiveConfigSchedulerLogViewAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER_LOG,RightLevel.VIEW);
	}
	
	public boolean isDataManagementArchiveConfigSchedulerLogActionAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER_LOG,RightLevel.ACTION);
	}
	
	public boolean isDataManagementArchiveConfigSchedulerLogEditAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER_LOG,RightLevel.EDIT);
	}
	
	public boolean isDataManagementArchiveConfigSchedulerLogCreateAllowed() {
		return hasRole(FunctionalityCodes.DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER_LOG,RightLevel.CREATE);
	}


	public boolean isSettingsAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS);
	}

	public boolean isSettingsViewAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS,RightLevel.VIEW);
	}

	public boolean isSettingsActionAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS,RightLevel.ACTION);
	}

	public boolean isSettingsEditAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS,RightLevel.EDIT);
	}

	public boolean isSettingsCreateAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS,RightLevel.CREATE);
	}

	public boolean isSettingsIncrementalNumberAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_INCREMENTAL_NUMBER);
	}

	public boolean isSettingsIncrementalNumberViewAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_INCREMENTAL_NUMBER,RightLevel.VIEW);
	}

	public boolean isSettingsIncrementalNumberActionAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_INCREMENTAL_NUMBER,RightLevel.ACTION);
	}

	public boolean isSettingsIncrementalNumberEditAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_INCREMENTAL_NUMBER,RightLevel.EDIT);
	}

	public boolean isSettingsIncrementalNumberCreateAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_INCREMENTAL_NUMBER,RightLevel.CREATE);
	}

	public boolean isSettingsGroupAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_GROUP);
	}

	public boolean isSettingsGroupViewAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_GROUP,RightLevel.VIEW);
	}

	public boolean isSettingsGroupActionAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_GROUP,RightLevel.ACTION);
	}

	public boolean isSettingsGroupEditAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_GROUP,RightLevel.EDIT);
	}

	public boolean isSettingsGroupCreateAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_GROUP,RightLevel.CREATE);
	}

	
	public boolean isAdministrationAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION);
	}
	
	public boolean isAdministrationViewAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION,RightLevel.VIEW);
	}
	
	public boolean isAdministrationActionAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION,RightLevel.ACTION);
	}
	
	public boolean isAdministrationEditAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION,RightLevel.EDIT);
	}
	
	public boolean isAdministrationCreateAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION,RightLevel.CREATE);
	}

	public boolean isAdministrationClientAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_CLIENT);
	}

	public boolean isAdministrationClientViewAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_CLIENT,RightLevel.VIEW);
	}

	public boolean isAdministrationClientActionAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_CLIENT,RightLevel.ACTION);
	}

	public boolean isAdministrationClientEditAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_CLIENT,RightLevel.EDIT);
	}

	public boolean isAdministrationClientCreateAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_CLIENT,RightLevel.CREATE);
	}

	public boolean isAdministrationProfileAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_PROFILE);
	}

	public boolean isAdministrationProfileViewAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_PROFILE,RightLevel.VIEW);
	}

	public boolean isAdministrationProfileActionAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_PROFILE,RightLevel.ACTION);
	}

	public boolean isAdministrationProfileEditAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_PROFILE,RightLevel.EDIT);
	}

	public boolean isAdministrationProfileCreateAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_PROFILE,RightLevel.CREATE);
	}

	public boolean isAdministrationUserAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_USER);
	}

	public boolean isAdministrationUserViewAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_USER,RightLevel.VIEW);
	}

	public boolean isAdministrationUserActionAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_USER,RightLevel.ACTION);
	}

	public boolean isAdministrationUserEditAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_USER,RightLevel.EDIT);
	}

	public boolean isAdministrationUserCreateAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_USER,RightLevel.CREATE);
	}

	public boolean isAdministrationConnectedUserAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_CONNECTED_USER);
	}

	public boolean isAdministrationConnectedUserViewAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_CONNECTED_USER,RightLevel.VIEW);
	}

	public boolean isAdministrationConnectedUserActionAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_CONNECTED_USER,RightLevel.ACTION);
	}

	public boolean isAdministrationConnectedUserEditAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_CONNECTED_USER,RightLevel.EDIT);
	}

	public boolean isAdministrationConnectedUserCreateAllowed() {
		return hasRole(FunctionalityCodes.ADMINISTRATION_CONNECTED_USER,RightLevel.CREATE);
	}
	
		
	public boolean isBillingAllowed() {
		return hasRole(FunctionalityCodes.BILLING);
	}
	
	public boolean isBillingViewAllowed() {
		return hasRole(FunctionalityCodes.BILLING,RightLevel.VIEW);
	}
	
	public boolean isBillingActionAllowed() {
		return hasRole(FunctionalityCodes.BILLING,RightLevel.ACTION);
	}
	
	public boolean isBillingEditAllowed() {
		return hasRole(FunctionalityCodes.BILLING,RightLevel.EDIT);
	}
	
	public boolean isBillingCreateAllowed() {
		return hasRole(FunctionalityCodes.BILLING,RightLevel.CREATE);
	}
	
	public boolean isBillingEventAllowed() {
		return hasRole(FunctionalityCodes.BILLING_EVENT);
	}
	
	public boolean isBillingEventViewAllowed() {
		return hasRole(FunctionalityCodes.BILLING_EVENT,RightLevel.VIEW);
	}
	
	public boolean isBillingEventActionAllowed() {
		return hasRole(FunctionalityCodes.BILLING_EVENT,RightLevel.ACTION);
	}
	
	public boolean isBillingEventEditAllowed() {
		return hasRole(FunctionalityCodes.BILLING_EVENT,RightLevel.EDIT);
	}
	
	public boolean isBillingEventCreateAllowed() {
		return hasRole(FunctionalityCodes.BILLING_EVENT,RightLevel.CREATE);
	}
	
	public boolean isBillingEventRepositoryAllowed() {
		return hasRole(FunctionalityCodes.BILLING_EVENT_REPOSITORY);
	}
	
	public boolean isBillingEventRepositoryViewAllowed() {
		return hasRole(FunctionalityCodes.BILLING_EVENT_REPOSITORY,RightLevel.VIEW);
	}
	
	public boolean isBillingEventRepositoryActionAllowed() {
		return hasRole(FunctionalityCodes.BILLING_EVENT_REPOSITORY,RightLevel.ACTION);
	}
	
	public boolean isBillingEventRepositoryEditAllowed() {
		return hasRole(FunctionalityCodes.BILLING_EVENT_REPOSITORY,RightLevel.EDIT);
	}
	
	public boolean isBillingEventRepositoryCreateAllowed() {
		return hasRole(FunctionalityCodes.BILLING_EVENT_REPOSITORY,RightLevel.CREATE);
	}
	
	public boolean isBillingModelAllowed() {
		return hasRole(FunctionalityCodes.BILLING_MODEL);
	}
	
	public boolean isBillingModelViewAllowed() {
		return hasRole(FunctionalityCodes.BILLING_MODEL,RightLevel.VIEW);
	}
	
	public boolean isBillingModelActionAllowed() {
		return hasRole(FunctionalityCodes.BILLING_MODEL,RightLevel.ACTION);
	}
	
	public boolean isBillingModelEditAllowed() {
		return hasRole(FunctionalityCodes.BILLING_MODEL,RightLevel.EDIT);
	}
	
	public boolean isBillingModelCreateAllowed() {
		return hasRole(FunctionalityCodes.BILLING_MODEL,RightLevel.CREATE);
	}
	
	public boolean isBillingModelSchedulerAllowed() {
		return hasRole(FunctionalityCodes.BILLING_MODEL_SCHEDULER);
	}
	
	public boolean isBillingModelSchedulerViewAllowed() {
		return hasRole(FunctionalityCodes.BILLING_MODEL_SCHEDULER,RightLevel.VIEW);
	}
	
	public boolean isBillingModelSchedulerActionAllowed() {
		return hasRole(FunctionalityCodes.BILLING_MODEL_SCHEDULER,RightLevel.ACTION);
	}
	
	public boolean isBillingModelSchedulerEditAllowed() {
		return hasRole(FunctionalityCodes.BILLING_MODEL_SCHEDULER,RightLevel.EDIT);
	}
	
	public boolean isBillingModelSchedulerCreateAllowed() {
		return hasRole(FunctionalityCodes.BILLING_MODEL_SCHEDULER,RightLevel.CREATE);
	}
	
	public boolean isBillingRunAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN);
	}
	
	public boolean isBillingRunViewAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN,RightLevel.VIEW);
	}
	
	public boolean isBillingRunActionAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN,RightLevel.ACTION);
	}
	
	public boolean isBillingRunEditAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN,RightLevel.EDIT);
	}
	
	public boolean isBillingRunCreateAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN,RightLevel.CREATE);
	}
	
	public boolean isBillingRunOutcomeAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_OUTCOME);
	}
	
	public boolean isBillingRunOutcomeViewAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_OUTCOME,RightLevel.VIEW);
	}
	
	public boolean isBillingRunOutcomeActionAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_OUTCOME,RightLevel.ACTION);
	}
	
	public boolean isBillingRunOutcomeEditAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_OUTCOME,RightLevel.EDIT);
	}
	
	public boolean isBillingRunOutcomeCreateAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_OUTCOME,RightLevel.CREATE);
	}
	
	public boolean isBillingRunInvoiceAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_INVOICE);
	}
	
	public boolean isBillingRunInvoiceViewAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_INVOICE,RightLevel.VIEW);
	}
	
	public boolean isBillingRunInvoiceActionAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_INVOICE,RightLevel.ACTION);
	}
	
	public boolean isBillingRunInvoiceEditAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_INVOICE,RightLevel.EDIT);
	}
	
	public boolean isBillingRunInvoiceCreateAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_INVOICE,RightLevel.CREATE);
	}
	
	public boolean isBillingRunCreditNoteAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_CREDIT_NOTE);
	}
	
	public boolean isBillingRunCreditNoteViewAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_CREDIT_NOTE,RightLevel.VIEW);
	}
	
	public boolean isBillingRunCreditNoteActionAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_CREDIT_NOTE,RightLevel.ACTION);
	}
	
	public boolean isBillingRunCreditNoteEditAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_CREDIT_NOTE,RightLevel.EDIT);
	}
	
	public boolean isBillingRunCreditNoteCreateAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_CREDIT_NOTE,RightLevel.CREATE);
	}
	
	public boolean isBillingRunStatusAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_STATUS);
	}
	
	public boolean isBillingRunStatusViewAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_STATUS,RightLevel.VIEW);
	}
	
	public boolean isBillingRunStatusActionAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_STATUS,RightLevel.ACTION);
	}
	
	public boolean isBillingRunStatusEditAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_STATUS,RightLevel.EDIT);
	}
	
	public boolean isBillingRunStatusCreateAllowed() {
		return hasRole(FunctionalityCodes.BILLING_RUN_STATUS,RightLevel.CREATE);
	}
	
	public boolean isBillingTemplateViewAllowed() {
		return hasRole(FunctionalityCodes.BILLING_TEMPLATE,RightLevel.VIEW);
	}
	
	public boolean isBillingTemplateActionAllowed() {
		return hasRole(FunctionalityCodes.BILLING_TEMPLATE,RightLevel.ACTION);
	}
	
	public boolean isBillingTemplateEditAllowed() {
		return hasRole(FunctionalityCodes.BILLING_TEMPLATE,RightLevel.EDIT);
	}
	
	public boolean isBillingTemplateCreateAllowed() {
		return hasRole(FunctionalityCodes.BILLING_TEMPLATE,RightLevel.CREATE);
	}
	
	public boolean isBillingJoinViewAllowed() {
		return hasRole(FunctionalityCodes.BILLING_JOIN_EVENT_REPOSITORY,RightLevel.VIEW);
	}
	
	public boolean isBillingJoinActionAllowed() {
		return hasRole(FunctionalityCodes.BILLING_JOIN_EVENT_REPOSITORY,RightLevel.ACTION);
	}
	
	public boolean isBillingJoinEditAllowed() {
		return hasRole(FunctionalityCodes.BILLING_JOIN_EVENT_REPOSITORY,RightLevel.EDIT);
	}
	
	public boolean isBillingJoinCreateAllowed() {
		return hasRole(FunctionalityCodes.BILLING_JOIN_EVENT_REPOSITORY,RightLevel.CREATE);
	}
	
	public boolean isClientRepositoryViewAllowed() {
		return hasRole(FunctionalityCodes.BILLING_CLIENT_EVENT_REPOSITORY,RightLevel.VIEW);
	}
	
	public boolean isClientRepositoryActionAllowed() {
		return hasRole(FunctionalityCodes.BILLING_CLIENT_EVENT_REPOSITORY,RightLevel.ACTION);
	}
	
	public boolean isClientRepositoryEditAllowed() {
		return hasRole(FunctionalityCodes.BILLING_CLIENT_EVENT_REPOSITORY,RightLevel.EDIT);
	}
	
	public boolean isClientRepositoryCreatedAllowed() {
		return hasRole(FunctionalityCodes.BILLING_CLIENT_EVENT_REPOSITORY,RightLevel.CREATE);
	}
	
	public boolean isCompanyRepositoryViewAllowed() {
		return hasRole(FunctionalityCodes.BILLING_COMPANY_EVENT_REPOSITORY,RightLevel.VIEW);
	}
	
	public boolean isCompanyRepositoryActionAllowed() {
		return hasRole(FunctionalityCodes.BILLING_COMPANY_EVENT_REPOSITORY,RightLevel.ACTION);
	}
	
	public boolean isCompanyRepositoryEditAllowed() {
		return hasRole(FunctionalityCodes.BILLING_COMPANY_EVENT_REPOSITORY,RightLevel.EDIT);
	}
	
	public boolean isCompanyRepositoryCreatedAllowed() {
		return hasRole(FunctionalityCodes.BILLING_COMPANY_EVENT_REPOSITORY,RightLevel.CREATE);
	}
	
	
	public boolean isAccountingAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING);
	}
	
	public boolean isAccountingViewAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING,RightLevel.VIEW);
	}
	
	public boolean isAccountingActionAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING,RightLevel.ACTION);
	}
	
	public boolean isAccountingEditAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING,RightLevel.EDIT);
	}
	
	public boolean isAccountingCreateAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING,RightLevel.CREATE);
	}
	
	public boolean isAccountingPosingAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_POSTING);
	}
	
	public boolean isAccountingPosingViewAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_POSTING,RightLevel.VIEW);
	}
	
	public boolean isAccountingPosingActionAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_POSTING,RightLevel.ACTION);
	}
	
	public boolean isAccountingPosingEditAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_POSTING,RightLevel.EDIT);
	}
	
	public boolean isAccountingPosingCreateAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_POSTING,RightLevel.CREATE);
	}
	
	public boolean isAccountingPosingEntryRepositoryAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_POSTING_ENTRY_REPOSITORY);
	}
	
	public boolean isAccountingPosingEntryRepositoryViewAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_POSTING_ENTRY_REPOSITORY,RightLevel.VIEW);
	}
	
	public boolean isAccountingPosingEntryRepositoryActionAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_POSTING_ENTRY_REPOSITORY,RightLevel.ACTION);
	}
	
	public boolean isAccountingPosingEntryRepositoryEditAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_POSTING_ENTRY_REPOSITORY,RightLevel.EDIT);
	}
	
	public boolean isAccountingPosingEntryRepositoryCreateAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_POSTING_ENTRY_REPOSITORY,RightLevel.CREATE);
	}
	
	public boolean isAccountingPosingEditionAllowed() {
		return hasRole(FunctionalityCodes. ACCOUNTING_POSTING_EDITION);
	}
	
	public boolean isAccountingPosingEditionViewAllowed() {
		return hasRole(FunctionalityCodes. ACCOUNTING_POSTING_EDITION,RightLevel.VIEW);
	}
	
	public boolean isAccountingPosingEditionActionAllowed() {
		return hasRole(FunctionalityCodes. ACCOUNTING_POSTING_EDITION,RightLevel.ACTION);
	}
	
	public boolean isAccountingPosingEditionEditAllowed() {
		return hasRole(FunctionalityCodes. ACCOUNTING_POSTING_EDITION,RightLevel.EDIT);
	}
	
	public boolean isAccountingPosingEditionCreateAllowed() {
		return hasRole(FunctionalityCodes. ACCOUNTING_POSTING_EDITION,RightLevel.CREATE);
	}
	
	public boolean isAccountingBookingAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING);
	}
	
	public boolean isAccountingBookingViewAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING,RightLevel.VIEW);
	}
	
	public boolean isAccountingBookingActionAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING,RightLevel.ACTION);
	}
	
	public boolean isAccountingBookingEditAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING,RightLevel.EDIT);
	}
	
	public boolean isAccountingBookingCreateAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING,RightLevel.CREATE);
	}
	
	public boolean isAccountingBookingModelAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL);
	}
	
	public boolean isAccountingBookingModelViewAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL,RightLevel.VIEW);
	}
	
	public boolean isAccountingBookingModelActionAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL,RightLevel.ACTION);
	}
	
	public boolean isAccountingBookingModelEditAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL,RightLevel.EDIT);
	}
	
	public boolean isAccountingBookingModelCreateAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL,RightLevel.CREATE);
	}
	
	public boolean isBillingBookingModelSchedulerAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL_SCHEDULER);
	}
	
	public boolean isBillingBookingModelSchedulerViewAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL_SCHEDULER,RightLevel.VIEW);
	}
	
	public boolean isBillingBookingModelSchedulerActionAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL_SCHEDULER,RightLevel.ACTION);
	}
	
	public boolean isBillingBookingModelSchedulerEditAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL_SCHEDULER,RightLevel.EDIT);
	}
	
	public boolean isBillingBookingModelSchedulerCreateAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL_SCHEDULER,RightLevel.CREATE);
	}
	
	public boolean isAccountingBookingModelSchedulerLogAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL_SCHEDULER_LOG);
	}
	
	public boolean isAccountingBookingModelSchedulerLogViewAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL_SCHEDULER_LOG,RightLevel.VIEW);
	}
	
	public boolean isAccountingBookingModelSchedulerLogActionAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL_SCHEDULER_LOG,RightLevel.ACTION);
	}
	
	public boolean isAccountingBookingModelSchedulerLogEditAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL_SCHEDULER_LOG,RightLevel.EDIT);
	}
	
	public boolean isAccountingBookingModelSchedulerLogCreateAllowed() {
		return hasRole(FunctionalityCodes.ACCOUNTING_BOOKING_MODEL_SCHEDULER_LOG,RightLevel.CREATE);
	}
	
	
	public boolean isMessengerAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER);
	}
	
	public boolean isMessengerViewAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER,RightLevel.VIEW);
	}
	
	public boolean isMessengerActionAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER,RightLevel.ACTION);
	}
	
	public boolean isMessengerEditAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER,RightLevel.EDIT);
	}
	
	public boolean isMessengerCreateAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER,RightLevel.CREATE);
	}
	
	public boolean isMessengerEmailAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_EMAIL);
	}
	
	public boolean isMessengerEmailViewAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_EMAIL,RightLevel.VIEW);
	}
	
	public boolean isMessengerEmailActionAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_EMAIL,RightLevel.ACTION);
	}
	
	public boolean isMessengerEmailEditAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_EMAIL,RightLevel.EDIT);
	}
	
	public boolean isMessengerEmailCreateAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_EMAIL,RightLevel.CREATE);
	}
	
	public boolean isMessengerSmsAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_SMS);
	}
	
	public boolean isMessengerSmsViewAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_SMS,RightLevel.VIEW);
	}
	
	public boolean isMessengerSmsActionAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_SMS,RightLevel.ACTION);
	}
	
	public boolean isMessengerSmsEditAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_SMS,RightLevel.EDIT);
	}
	
	public boolean isMessengerSmsCreateAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_SMS,RightLevel.CREATE);
	}
	
	public boolean isMessengerLogAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_LOG);
	}
	
	public boolean isMessengerLogViewAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_LOG,RightLevel.VIEW);
	}
	
	public boolean isMessengerLogActionAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_LOG,RightLevel.ACTION);
	}
	
	public boolean isMessengerLogEditAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_LOG,RightLevel.EDIT);
	}
	
	public boolean isMessengerLogCreateAllowed() {
		return hasRole(FunctionalityCodes.MESSENGER_LOG,RightLevel.CREATE);
	}
	
	public boolean isReportingJoinGridViewAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID,RightLevel.VIEW);
	}
	
	public boolean isReportingJoinGridActionAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID,RightLevel.ACTION);
	}
	
	public boolean isReportingJoinGridEditAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID,RightLevel.EDIT);
	}
	
	public boolean isReportingJoinGridCreateAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID,RightLevel.CREATE);
	}
	
	

	public boolean isReportingJoinGridSchedulerViewAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID_SCHEDULER,RightLevel.VIEW);
	}

	public boolean isReportingJoinGridSchedulerActionAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID_SCHEDULER,RightLevel.ACTION);
	}
	
	public boolean isReportingJoinGridSchedulerEditAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID_SCHEDULER,RightLevel.EDIT);
	}
	
	public boolean isReportingJoinGridSchedulerCreateAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID_SCHEDULER,RightLevel.CREATE);
	}
	
	

	public boolean isReportingJoinGridSchedulerLogViewAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID_SCHEDULER_LOG,RightLevel.VIEW);
	}

	public boolean isReportingJoinGridSchedulerLogActionAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID_SCHEDULER_LOG,RightLevel.ACTION);
	}
	
	public boolean isReportingJoinGridSchedulerLogEditAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID_SCHEDULER_LOG,RightLevel.EDIT);
	}
	
	public boolean isReportingJoinGridSchedulerLogCreateAllowed() {
		return hasRole(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID_SCHEDULER_LOG,RightLevel.CREATE);
	}
	
	
	
	

	public boolean isSchedulerPlannerViewAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER,RightLevel.VIEW);
	}

	public boolean isSchedulerPlannerActionAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER,RightLevel.ACTION);
	}
	
	public boolean isSchedulerPlannerEditAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER,RightLevel.EDIT);
	}
	
	public boolean isSchedulerPlannerCreateAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER,RightLevel.CREATE);
	}
	
	

	public boolean isSchedulerPlannerSchedulerViewAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_SCHEDULER,RightLevel.VIEW);
	}

	public boolean isSchedulerPlannerSchedulerActionAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_SCHEDULER,RightLevel.ACTION);
	}
	
	public boolean isSchedulerPlannerSchedulerEditAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_SCHEDULER,RightLevel.EDIT);
	}
	
	public boolean isSchedulerPlannerSchedulerCreateAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_SCHEDULER,RightLevel.CREATE);
	}
	
	public boolean isSchedulerPlannerSchedulerLogViewAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_SCHEDULER_LOG,RightLevel.VIEW);
	}
	
	public boolean isSchedulerPlannerSchedulerLogActionAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_SCHEDULER_LOG,RightLevel.ACTION);
	}
	
	public boolean isSchedulerPlannerSchedulerLogEditAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_SCHEDULER_LOG,RightLevel.EDIT);
	}	
	
	
	
	public boolean isSchedulerPlannerPresentationViewAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_PRESENTATION,RightLevel.VIEW);
	}
	
	public boolean isSchedulerPlannerPresentationActionAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_PRESENTATION,RightLevel.ACTION);
	}
	
	public boolean isSchedulerPlannerPresentationEditAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_PRESENTATION,RightLevel.EDIT);
	}
	
	public boolean isSchedulerPlannerPresentationCreateAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_PRESENTATION,RightLevel.CREATE);
	}
	
	
	public boolean isSchedulerPlannerPresentationTemplateViewAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_PRESENTATION_TEMPLATE,RightLevel.VIEW);
	}
	
	public boolean isSchedulerPlannerPresentationTemplateActionAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_PRESENTATION_TEMPLATE,RightLevel.ACTION);
	}
	
	public boolean isSchedulerPlannerPresentationTemplateEditAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_PRESENTATION_TEMPLATE,RightLevel.EDIT);
	}
	
	public boolean isSchedulerPlannerPresentationTemplateCreateAllowed() {
		return hasRole(FunctionalityCodes.SCHEDULER_PLANNER_PRESENTATION_TEMPLATE,RightLevel.CREATE);
	}
	
	
	public boolean isIntegrationViewAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_INTEGRATION_SERVICE,RightLevel.VIEW);
	}
	
	public boolean isIntegrationActionAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_INTEGRATION_SERVICE,RightLevel.ACTION);
	}
	
	public boolean isIntegrationrEditAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_INTEGRATION_SERVICE,RightLevel.EDIT);
	}
	
	public boolean isIntegrationCreateAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_INTEGRATION_SERVICE,RightLevel.CREATE);
	}
	
	public boolean isIntegrationLogViewAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_INTEGRATION_SERVICE,RightLevel.VIEW);
	}
	
	public boolean isIntegrationLogActionAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_INTEGRATION_SERVICE,RightLevel.ACTION);
	}
	
	public boolean isIntegrationSchedulerViewAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_INTEGRATION_SERVICE,RightLevel.VIEW);
	}
	
	public boolean isIntegrationSchedulerActionAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_INTEGRATION_SERVICE,RightLevel.ACTION);
	}	
	
	public boolean isTaskViewAllowed() {
		return hasRole(FunctionalityCodes.TASK,RightLevel.VIEW);
	}	
	
	public boolean isTaskActionAllowed() {
		return hasRole(FunctionalityCodes.TASK,RightLevel.ACTION);
	}
	
	public boolean isTaskEditAllowed() {
		return hasRole(FunctionalityCodes.TASK,RightLevel.EDIT);
	}
	
	public boolean isTaskCreateAllowed() {
		return hasRole(FunctionalityCodes.TASK,RightLevel.CREATE);
	}
	
	
	public boolean isSourcingFormModelViewAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_MODEL_FORM,RightLevel.VIEW);
	}
	
	public boolean isSourcingFormModelActionAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_MODEL_FORM,RightLevel.ACTION);
	}
	
	public boolean isSourcingFormModelEditAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_MODEL_FORM,RightLevel.EDIT);
	}
	
	public boolean isSourcingFormModelCreateAllowed() {
		return hasRole(FunctionalityCodes.SOURCING_MODEL_FORM,RightLevel.CREATE);
	}
	
	
	public boolean isDynamicBrowserViewAllowed() {
		return hasRole(FunctionalityCodes.DYNAMIC_FORM,RightLevel.VIEW);
	}
	
	public boolean isDynamicBrowserActionAllowed() {
		return hasRole(FunctionalityCodes.DYNAMIC_FORM,RightLevel.ACTION);
	}
	
	public boolean isDynamicBrowserEditAllowed() {
		return hasRole(FunctionalityCodes.DYNAMIC_FORM,RightLevel.EDIT);
	}
	
	public boolean isDynamicBrowserCreateAllowed() {
		return hasRole(FunctionalityCodes.DYNAMIC_FORM,RightLevel.CREATE);
	}
	

	public boolean isFileManagerViewAllowed() {
		return hasRole(FunctionalityCodes.FILE_MANAGER,RightLevel.VIEW);
	}

	public boolean isFileManagerActionAllowed() {
		return hasRole(FunctionalityCodes.FILE_MANAGER,RightLevel.ACTION);
	}
	
	public boolean isFileManagerEditAllowed() {
		return hasRole(FunctionalityCodes.FILE_MANAGER,RightLevel.EDIT);
	}
	

	
	public boolean isDocumentBrowserViewAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_DOCUMENT,RightLevel.VIEW);
	}
	
	public boolean isDocumentBrowserActionAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_DOCUMENT,RightLevel.ACTION);
	}
	
	public boolean isDocumentBrowserEditAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_DOCUMENT,RightLevel.EDIT);
	}
	
	public boolean isDocumentBrowserCreateAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_DOCUMENT,RightLevel.CREATE);
	}
	
	public boolean isLabelBrowserViewAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_LABEL ,RightLevel.VIEW);
	}
	
	public boolean isLabelBrowserActionAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_LABEL ,RightLevel.ACTION);
	}
	
	public boolean isLabelBrowserEditAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_LABEL, RightLevel.EDIT);
	}
	
	public boolean isLabelBrowserCreateAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_LABEL, RightLevel.CREATE);
	}


	public boolean isSettingsVariableAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_VARIABLE);
	}

	public boolean isSettingsVariableViewAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_VARIABLE, RightLevel.VIEW);
	}

	public boolean isSettingsVariableActionAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_VARIABLE, RightLevel.ACTION);
	}

	public boolean isSettingsVariableEditAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_VARIABLE, RightLevel.EDIT);
	}

	public boolean isSettingsVariableCreateAllowed() {
		return hasRole(FunctionalityCodes.SETTINGS_VARIABLE, RightLevel.CREATE);
	}	
	
	
	public boolean isFileManagerBrowserViewAllowed() {
		return hasRole(FunctionalityCodes.FILE_MANAGER,RightLevel.VIEW);
	}
	
	public boolean isFileManagerBrowserActionAllowed() {
		return hasRole(FunctionalityCodes.FILE_MANAGER,RightLevel.ACTION);
	}
	
	public boolean isFileManagerBrowserEditAllowed() {
		return hasRole(FunctionalityCodes.FILE_MANAGER,RightLevel.EDIT);
	}
	
	public boolean isFileManagerBrowserCreateAllowed() {
		return hasRole(FunctionalityCodes.FILE_MANAGER,RightLevel.CREATE);
	}
	
	public boolean isTaskTemplateViewAllowed() {
		return hasRole(FunctionalityCodes.TASK_TEMPLATE,RightLevel.VIEW);
	}
	
	public boolean isTaskTemplateActionAllowed() {
		return hasRole(FunctionalityCodes.TASK_TEMPLATE,RightLevel.ACTION);
	}
	
	public boolean isTaskTemplateEditAllowed() {
		return hasRole(FunctionalityCodes.TASK_TEMPLATE,RightLevel.EDIT);
	}
	
	public boolean isTaskTemplateCreateAllowed() {
		return hasRole(FunctionalityCodes.TASK_TEMPLATE,RightLevel.CREATE);
	}
	
	
	public boolean isProjectVersionViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_PROJECT_VERSION,RightLevel.VIEW);
	}
	
	public boolean isProjectVersionActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_PROJECT_VERSION,RightLevel.ACTION);
	}
	
	public boolean isProjectVersionEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_PROJECT_VERSION,RightLevel.EDIT);
	}
	
	public boolean isProjectVersionCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_PROJECT_VERSION,RightLevel.CREATE);
	}
	
	public boolean isProjectManagerViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER,RightLevel.VIEW);
	}
	
	public boolean isProjectManagerActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER,RightLevel.ACTION);
	}
	
	public boolean isProjectManagerEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER,RightLevel.EDIT);
	}
	
	public boolean isProjectManagerCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER,RightLevel.CREATE);
	}
	
	public boolean isProjectManagerTestViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST,RightLevel.VIEW);
	}
	
	public boolean isProjectManagerTestActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST,RightLevel.ACTION);
	}
	
	public boolean isProjectManagerTestEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST,RightLevel.EDIT);
	}
	
	public boolean isProjectManagerTestCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST,RightLevel.CREATE);
	}

	public boolean isProjectManagerTestStepViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_STEP,RightLevel.VIEW);
	}

	public boolean isProjectManagerTestStepActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_STEP,RightLevel.ACTION);
	}
	
	public boolean isProjectManagerTestStepEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_STEP,RightLevel.EDIT);
	}
	
	public boolean isProjectManagerTestStepCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_STEP,RightLevel.CREATE);
	}
	

	public boolean isProjectManagerTestExpectedResultViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_EXPECTED_RESULT,RightLevel.VIEW);
	}
	
	public boolean isProjectManagerTestExpectedResultActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_EXPECTED_RESULT,RightLevel.ACTION);
	}
	
	public boolean isProjectManagerTestExpectedResultEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_EXPECTED_RESULT,RightLevel.EDIT);
	}
	
	public boolean isProjectManagerTestExpectedResultCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_EXPECTED_RESULT,RightLevel.CREATE);
	}
	


	public boolean isProjectManagerIssueViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_ISSUE,RightLevel.VIEW);
	}

	public boolean isProjectManagerIssueActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_ISSUE,RightLevel.ACTION);
	}
	
	public boolean isProjectManagerIssueEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_ISSUE,RightLevel.EDIT);
	}
	
	public boolean isProjectManagerIssueCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_ISSUE,RightLevel.CREATE);
	}
	


	public boolean isProjectManagerFunctionalityViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_FUNCTIONNALITY,RightLevel.VIEW);
	}
	
	public boolean isProjectManagerFunctionalityActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_FUNCTIONNALITY,RightLevel.ACTION);
	}
	
	public boolean isProjectManagerFunctionalityEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_FUNCTIONNALITY,RightLevel.EDIT);
	}
	
	public boolean isProjectManagerFunctionalityCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_FUNCTIONNALITY,RightLevel.CREATE);
	}


	public boolean isProjectManagerModuleViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_MODULE,RightLevel.VIEW);
	}

	public boolean isProjectManagerModuleActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_MODULE,RightLevel.ACTION);
	}
	
	public boolean isProjectManagerModuleEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_MODULE,RightLevel.EDIT);
	}
	
	public boolean isProjectManagerModuleCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_MODULE,RightLevel.CREATE);
	}
	
	
	public boolean isTestCaseViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_CASE,RightLevel.VIEW);
	}

	public boolean isTestCaseActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_CASE,RightLevel.ACTION);
	}
	
	public boolean isTestCaseEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_CASE,RightLevel.EDIT);
	}
	
	public boolean isTestCaseCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_CASE,RightLevel.CREATE);
	}
	
	
	public boolean isTestScenarioViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_SCENARIO,RightLevel.VIEW);
	}

	public boolean isTestScenarioActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_SCENARIO,RightLevel.ACTION);
	}
	public boolean isTestScenarioEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_SCENARIO,RightLevel.EDIT);
	}
	
	public boolean isTestScenarioCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_SCENARIO,RightLevel.CREATE);
	}
	
	
	public boolean isTestCampaignViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_CAMPAIGN,RightLevel.VIEW);
	}
	
	public boolean isTestCampaignActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_CAMPAIGN,RightLevel.ACTION);
	}
		
	public boolean isTestCampaignEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_CAMPAIGN,RightLevel.EDIT);
	}
	
	public boolean isTestCampaignCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_CAMPAIGN,RightLevel.CREATE);
	}
	
	
	public boolean isTestCampaignResultViewAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_CAMPAIGN_RESULT,RightLevel.VIEW);
	}
	
	public boolean isTestCampaignResultActionAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_CAMPAIGN_RESULT,RightLevel.ACTION);
	}
	
	public boolean isTestCampaignResultEditAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_CAMPAIGN_RESULT,RightLevel.EDIT);
	}
	
	public boolean isTestCampaignResultCreateAllowed() {
		return hasRole(FunctionalityCodes.PROJECT_MANAGER_TEST_CAMPAIGN_RESULT,RightLevel.CREATE);
	}
	
	
	
	
	private boolean hasRole(String code) {
		return hasRole(code, null);
	}

	private boolean hasRole(String code, RightLevel level) {		
		if(!isClientFunctionalityAllowed(code)) {
			if(isAdministrator()) {
				return isClientAdminBasicFunctionalityAllowed(code);
			}
			return false;
		}
		if(isAdministrator()) {
			return true;
		}
		if(profile != null && profile.getType() != null && profile.getType().isSuperUser()) {
			if(FunctionalityCodes.ADMINISTRATION_CLIENT.equals(code)) {
				return false;
			}
			return true;
		}
		if(rights == null) {
			return false;
		}
		List<Right> rightsItems = this.rights.stream()
				.filter(item -> code.equals(item.getFunctionality()))
				.collect(Collectors.toList());
		if(rightsItems.size() == 0) {
			return false;
		}
		if(level == null) {
			return true;
		}
		List<Right> rightsItems2 = rightsItems.stream()
				.filter(item -> level == item.getLevel())
				.collect(Collectors.toList());
		return rightsItems2.size() > 0;
	}
	
	private boolean isClientFunctionalityAllowed(String code) {	
		if(getForm().equals(code) && this.clientFunctionalities != null) {
			List<String> items = this.clientFunctionalities.stream()
					.filter(item -> item.startsWith(getForm()))
					.collect(Collectors.toList());
			return items!= null && items.size() > 0;
		}
		return this.clientFunctionalities != null && this.clientFunctionalities.contains(code);		
	}
	
	private boolean isClientAdminBasicFunctionalityAllowed(String code) {	
		return Arrays.asList(FunctionalityCodes.ADMINISTRATION,
				FunctionalityCodes.ADMINISTRATION_CLIENT, 
				FunctionalityCodes.ADMINISTRATION_CONNECTED_USER, 
				FunctionalityCodes.ADMINISTRATION_PROFILE, 
				FunctionalityCodes.ADMINISTRATION_USER).contains(code);		
	}
	
	public boolean isAdministrator() {
		return (profile == null && user != null && user.getType() != null && user.getType().isAdministrator())
				|| (profile != null && profile.getType() != null && profile.getType().isAdministrator());
	}
	
	private String getForm() {
		String form = "Form_";
		if(profile != null && profile.getClientId() != null) {
			form += profile.getClientId();
		}else 
		if(user != null && user.getClientId() != null) {
			//form += user.getClientId();
		}
		return form;
	}
	
	private String getUserLoad() {
		String form = "USER_LOAD_";
		if(profile != null && profile.getClientId() != null) {
			form += profile.getClientId();
		}else 
		if(user != null && user.getClientId() != null) {
			//form += user.getClientId();
		}
		return form;
	}
	
	public Map<String, List<RightLevel>> getFormRights(){
		Map<String, List<RightLevel>> formRights = new HashMap<>();	
		if(this.clientFunctionalities != null) {
			List<String> items = this.clientFunctionalities.stream()
					.filter(item -> item.startsWith(getForm()) || item.startsWith(getUserLoad()))
					.collect(Collectors.toList());
			for(String code : items) {
				formRights.put(code, getlevels(code));
			}
		}
		return formRights;
	}
	
	private List<RightLevel> getlevels(String code){
		List<RightLevel> levels = new ArrayList<>(0);
		for(RightLevel value : RightLevel.values()) {
			boolean b = hasRole(code, value);
			if(b) {
				levels.add(value);
			}
		}
		return levels;
	}
}

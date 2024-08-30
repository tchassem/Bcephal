/**
 * 
 */
package com.moriset.bcephal.utils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Moriset
 *
 */
public class FunctionalityCodes {
	
	public static String VARIABLE = "variable";
	public static String PROJECT 						= "project";	
	public static String PROJECT_BACKUP 				= PROJECT + ".backup";
	public static String PROJECT_BACKUP_SCHEDULER 		= PROJECT_BACKUP + ".scheduler";
	public static String PROJECT_BACKUP_SCHEDULER_LOG 	= PROJECT_BACKUP_SCHEDULER + ".log";
	public static String PROJECT_IMPORT 				= PROJECT + ".import";
	public static String PROJECT_ACCESS_RIGHT			= PROJECT + ".access.right";
		
	public static String INITIATION 			= "initiation";
	public static String INITIATION_MODEL 		= INITIATION + ".model";
	public static String INITIATION_MEASURE 	= INITIATION + ".measure";
	public static String INITIATION_PERIOD 		= INITIATION + ".period";
	public static String INITIATION_CALENDAR 	= INITIATION + ".calendar";
	public static String INITIATION_CALCULATED_MEASURE 	= INITIATION + ".calculated.measure";
	
	public static String SOURCING 							= "sourcing";
	public static String SOURCING_INPUT_GRID 				= SOURCING + ".input.grid";
	public static String SOURCING_INPUT_SPREADSHEET 		= SOURCING + ".input.spreadsheet";
	public static String SOURCING_MATERIALIZED_GRID 		= SOURCING + ".materialized.grid";
	public static String SOURCING_FILE_LOADER 				= SOURCING + ".file.loader";
	public static String SOURCING_FILE_LOADER_SCHEDULER 	= SOURCING_FILE_LOADER + ".scheduler";
	public static String SOURCING_FILE_LOADER_SCHEDULER_LOG = SOURCING_FILE_LOADER_SCHEDULER + ".log";
	public static String SOURCING_SPOT 						= SOURCING + ".spot";
	public static String SOURCING_LINK 						= SOURCING + ".link";
	public static String SOURCING_MODEL_FORM 				= SOURCING + ".model.form";
	public static String SOURCING_USER_LOAD					= SOURCING + ".user.load";
	public static String SOURCING_USER_LOADER				= SOURCING + ".user.loader";
	public static String SOURCING_USER_LOADER_CONFIG		= SOURCING + ".user.loader.config";
	public static String SOURCING_LOADER_TEMPLATE			= SOURCING + ".loader.template";
	
			
	public static String TRANSFORMATION 					= "transformation";
	public static String TRANSFORMATION_TREE 				= TRANSFORMATION + ".tree";
	public static String TRANSFORMATION_TREE_SCHEDULER 		= TRANSFORMATION_TREE + ".scheduler";
	public static String TRANSFORMATION_TREE_SCHEDULER_LOG 	= TRANSFORMATION_TREE_SCHEDULER + ".log";
	
	public static String TRANSFORMATION_ROUTINE 				= TRANSFORMATION + ".routine";
	public static String TRANSFORMATION_ROUTINE_SCHEDULER 		= TRANSFORMATION_ROUTINE + ".scheduler";
	public static String TRANSFORMATION_ROUTINE_SCHEDULER_LOG 	= TRANSFORMATION_ROUTINE_SCHEDULER + ".log";
	
	public static String SCRIPT 				= TRANSFORMATION + ".script";
	
	public static String REPORTING 							= "reporting";
	public static String REPORTING_REPORT_GRID 				= REPORTING + ".report.grid";
	public static String REPORTING_REPORT_JOIN_GRID         = REPORTING + ".report.join.grid";
	public static String REPORTING_REPORT_JOIN_GRID_SCHEDULER         = REPORTING_REPORT_JOIN_GRID + ".scheduler";
	public static String REPORTING_REPORT_JOIN_GRID_SCHEDULER_LOG         = REPORTING_REPORT_JOIN_GRID_SCHEDULER + ".log";
	public static String REPORTING_REPORT_SPREADSHEET 		= REPORTING + ".report.spreadsheet";
	public static String REPORTING_PIVOT_TABLE 				= REPORTING + ".pivot.table";
	public static String REPORTING_CHART 					= REPORTING + ".chart";
	public static String REPORTING_UNION_GRID				= REPORTING + ".union.grid";
	
	public static String DASHBOARDING 						= "dashboarding";
	public static String DASHBOARDING_DASHBOARD 			= DASHBOARDING + ".dashboard";
	public static String DASHBOARDING_ALARM 				= DASHBOARDING + ".alarm";
	public static String DASHBOARDING_ALARM_SCHEDULER 		= DASHBOARDING_ALARM + ".scheduler";
	public static String DASHBOARDING_ALARM_SCHEDULER_LOG 	= DASHBOARDING_ALARM_SCHEDULER + ".log";
	
	
	public static String RECONCILIATION 						= "reconciliation";
	public static String RECONCILIATION_FILTER 					= RECONCILIATION + ".filter";
	public static String RECONCILIATION_MODEL 					= RECONCILIATION + ".model";
	public static String RECONCILIATION_UNION 					= RECONCILIATION + ".union";
	public static String RECONCILIATION_AUTO_RECO 				= RECONCILIATION + ".auto.reco";
	public static String RECONCILIATION_AUTO_RECO_SCHEDULER 	= RECONCILIATION_AUTO_RECO + ".scheduler";
	public static String RECONCILIATION_AUTO_RECO_SCHEDULER_LOG = RECONCILIATION_AUTO_RECO_SCHEDULER + ".log";
	
	
	public static String BILLING 							= "billing";
	public static String BILLING_EVENT						= BILLING + ".event";
	public static String BILLING_EVENT_REPOSITORY			= BILLING_EVENT + ".repository";
	public static String BILLING_CLIENT_EVENT_REPOSITORY	= BILLING_EVENT + ".client.repository";
	public static String BILLING_COMPANY_EVENT_REPOSITORY	= BILLING_EVENT + ".company.repository";	
	public static String BILLING_JOIN_EVENT_REPOSITORY	    = BILLING_EVENT + ".join.repository";
	public static String BILLING_TEMPLATE 					= BILLING + ".template";
	public static String BILLING_MODEL 						= BILLING + ".model";
	public static String BILLING_MODEL_SCHEDULER 			= BILLING_MODEL + ".scheduler";
	public static String BILLING_RUN 						= BILLING + ".run";
	public static String BILLING_RUN_OUTCOME 				= BILLING_RUN + ".outcome";
	public static String BILLING_RUN_INVOICE				= BILLING_RUN + ".invoice";
	public static String BILLING_RUN_CREDIT_NOTE 			= BILLING_RUN + ".credit.note";
	public static String BILLING_RUN_STATUS					= BILLING_RUN + ".status";
	
	
	public static String PAYMENT 								= "payment";
	public static String PAYMENT_EVENT							= PAYMENT + ".event";
	public static String PAYMENT_EVENT_REPOSITORY				= PAYMENT_EVENT + ".repository";
	public static String PAYMENT_EVENT_COUNTERPARTY_REPOSITORY	= PAYMENT_EVENT + ".counterparty.repository";
	public static String PAYMENT_EVENT_INSRUCTOR_REPOSITORY		= PAYMENT_EVENT + ".instructor.repository";	
	public static String PAYMENT_EVENT_JOIN_REPOSITORY	   		= PAYMENT_EVENT + ".join.repository";
	public static String PAYMENT_MEAN 							= PAYMENT + ".mean";
	public static String PAYMENT_MEAN_ROUTINE 					= PAYMENT_MEAN + ".routine";
	public static String PAYMENT_MODEL 						= PAYMENT + ".model";
	public static String PAYMENT_RUN 						= PAYMENT + ".run";
	public static String PAYMENT_RUN_OUTCOME 				= PAYMENT_RUN + ".outcome";
	public static String PAYMENT_INSTRUCTION				= PAYMENT + ".instruction";
	public static String PAYMENT_RUN_STATUS					= PAYMENT_RUN + ".status";
	
	
	public static String ACCOUNTING 							= "accounting";
	public static String ACCOUNTING_POSTING						= ACCOUNTING + ".posting";
	public static String ACCOUNTING_POSTING_ENTRY_REPOSITORY	= ACCOUNTING_POSTING + ".entry.repository";
	public static String ACCOUNTING_POSTING_EDITION				= ACCOUNTING_POSTING + ".edition";
	public static String ACCOUNTING_BOOKING						= ACCOUNTING + ".booking";
	public static String ACCOUNTING_BOOKING_MODEL				= ACCOUNTING_BOOKING + ".model";	
	public static String ACCOUNTING_BOOKING_MODEL_SCHEDULER 		= ACCOUNTING_BOOKING_MODEL + ".scheduler";
	public static String ACCOUNTING_BOOKING_MODEL_SCHEDULER_LOG 	= ACCOUNTING_BOOKING_MODEL_SCHEDULER + ".log";
	
	public static String MESSENGER 			= "messenger";
	public static String MESSENGER_EMAIL	= MESSENGER + ".email";
	public static String MESSENGER_SMS		= MESSENGER + ".sms";
	public static String MESSENGER_LOG		= MESSENGER + ".log";
	
	
	
	public static String DATA_MANAGEMENT 				= "data.management";
	public static String DATA_MANAGEMENT_ARCHIVE 			= DATA_MANAGEMENT + ".archive";
	public static String DATA_MANAGEMENT_ARCHIVE_LOG 		= DATA_MANAGEMENT + ".log";
	public static String DATA_MANAGEMENT_ARCHIVE_CONFIG 		= DATA_MANAGEMENT + ".archive.config";
	public static String DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER 	= DATA_MANAGEMENT_ARCHIVE_CONFIG + ".scheduler";
	public static String DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER_LOG = DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER + ".log";
	
	public static String SETTINGS 						= "settings";
	public static String SETTINGS_INCREMENTAL_NUMBER 	= SETTINGS + ".incremental.number";
	public static String SETTINGS_GROUP 				= SETTINGS + ".group";
	public static String SETTINGS_INTEGRATION_SERVICE = SETTINGS + ".integartion.service";
	public static String SETTINGS_DOCUMENT = SETTINGS + ".document";
	public static String SETTINGS_LABEL = SETTINGS + ".label";
	public static String SETTINGS_VARIABLE = SETTINGS + ".variable";
	
	public static String ADMINISTRATION 				= "administration";
	public static String ADMINISTRATION_CLIENT 			= ADMINISTRATION + ".client";
	public static String ADMINISTRATION_PROFILE 		= ADMINISTRATION + ".profile";
	public static String ADMINISTRATION_USER 			= ADMINISTRATION + ".user";
	public static String ADMINISTRATION_CONNECTED_USER 	= ADMINISTRATION + ".connected.user";
	
	public static String SCHEDULER_PLANNER 				  = "scheduler.planner";
	public static String SCHEDULER_PLANNER_SCHEDULER 	  = SCHEDULER_PLANNER + ".scheduler";
	public static String SCHEDULER_PLANNER_SCHEDULER_LOG  = SCHEDULER_PLANNER_SCHEDULER + ".log";
	public static String SCHEDULER_PLANNER_PRESENTATION 	  = SCHEDULER_PLANNER + ".presentation";
	public static String SCHEDULER_PLANNER_PRESENTATION_TEMPLATE 	  = SCHEDULER_PLANNER_PRESENTATION + ".template";
	
	public static String DYNAMIC_FORM = "dynamic.form";
	
	public static String FILE_MANAGER = "file.manager";
	
	public static String TASK 		= "task";
	public static String TASK_TEMPLATE = TASK  + ".template";
	public static String TASK_LOG 	=  TASK  + ".log";
	public static String TASK_LOG_ITEM 	=  TASK  + ".log.item";
	
	public static String CHAT 		= "chat";
	public static String DOCUMENT 		= "document";
	
	
	public static String LICENSE_MANAGER 			= "license.manager";
	public static String LICENSE_MANAGER_PRODUCT 	= LICENSE_MANAGER + ".product";
	public static String LICENSE_MANAGER_LICENSE 	= LICENSE_MANAGER + ".license";
	
	public static String PROJECT_MANAGER 	= "project.manager";
	public static String PROJECT_MANAGER_TEST 	= PROJECT_MANAGER + ".test";
	public static String PROJECT_MANAGER_TEST_CASE 	= PROJECT_MANAGER_TEST + ".case";
	public static String PROJECT_MANAGER_PROJECT_VERSION 	= PROJECT_MANAGER + ".project.version";
	public static String PROJECT_MANAGER_MODULE 	= PROJECT_MANAGER + ".module";
	public static String PROJECT_MANAGER_FUNCTIONNALITY 	= PROJECT_MANAGER + ".functionnality";
	public static String PROJECT_MANAGER_ISSUE 	= PROJECT_MANAGER + ".issue";
	public static String PROJECT_MANAGER_TEST_STEP 	= PROJECT_MANAGER_TEST + ".step";
	public static String PROJECT_MANAGER_TEST_EXPECTED_RESULT 	= PROJECT_MANAGER_TEST + ".expected.result";
	public static String PROJECT_MANAGER_TEST_SCENARIO 	= PROJECT_MANAGER_TEST + ".test.scenario";
	public static String PROJECT_MANAGER_TEST_CAMPAIGN 	= PROJECT_MANAGER + ".test.campaign";
	public static String PROJECT_MANAGER_TEST_CAMPAIGN_RESULT 	= PROJECT_MANAGER + ".test.campaign.result";

	
	public static List<String> GetAll() {
		List<String> codes = new ArrayList<String>();
		codes.add(PROJECT);
		codes.add(PROJECT_BACKUP);
		codes.add(PROJECT_BACKUP_SCHEDULER);
		codes.add(PROJECT_BACKUP_SCHEDULER_LOG);
		codes.add(PROJECT_IMPORT);
		codes.add(PROJECT_ACCESS_RIGHT);
		
		codes.add(INITIATION);
		codes.add(INITIATION_MODEL);
		codes.add(INITIATION_MEASURE);
		codes.add(INITIATION_CALCULATED_MEASURE);
		codes.add(INITIATION_PERIOD);
		codes.add(INITIATION_CALENDAR);
		
		codes.add(SOURCING);
		codes.add(SOURCING_INPUT_GRID);
		// codes.add(SOURCING_INPUT_SPREADSHEET);
		codes.add(SOURCING_FILE_LOADER);
		codes.add(SOURCING_FILE_LOADER_SCHEDULER);
		codes.add(SOURCING_FILE_LOADER_SCHEDULER_LOG);	
		codes.add(SOURCING_MATERIALIZED_GRID);	
		codes.add(SOURCING_USER_LOADER);
		codes.add(SOURCING_USER_LOADER_CONFIG);
		codes.add(SOURCING_USER_LOAD);
		codes.add(SOURCING_SPOT);
		// codes.add(SOURCING_LINK);
		codes.add(SOURCING_MODEL_FORM);
		codes.add(SOURCING_LOADER_TEMPLATE);
		
		codes.add(TRANSFORMATION);
		// codes.add(TRANSFORMATION_TREE);
		// codes.add(TRANSFORMATION_TREE_SCHEDULER);
		// codes.add(TRANSFORMATION_TREE_SCHEDULER_LOG);
		codes.add(TRANSFORMATION_ROUTINE);
		codes.add(TRANSFORMATION_ROUTINE_SCHEDULER);
		codes.add(TRANSFORMATION_ROUTINE_SCHEDULER_LOG);
		
		codes.add(SCRIPT);
		
		codes.add(REPORTING);
		codes.add(REPORTING_REPORT_GRID);
		codes.add(REPORTING_REPORT_JOIN_GRID);
		codes.add(REPORTING_UNION_GRID);
		// codes.add(REPORTING_REPORT_JOIN_GRID_SCHEDULER);
		// codes.add(REPORTING_REPORT_JOIN_GRID_SCHEDULER_LOG);
		// codes.add(REPORTING_REPORT_SPREADSHEET);
		codes.add(REPORTING_PIVOT_TABLE);
		codes.add(REPORTING_CHART);
		
		codes.add(DASHBOARDING);
		codes.add(DASHBOARDING_DASHBOARD);
		codes.add(DASHBOARDING_ALARM);
		codes.add(DASHBOARDING_ALARM_SCHEDULER);
		codes.add(DASHBOARDING_ALARM_SCHEDULER_LOG);
		
		codes.add(RECONCILIATION);
		codes.add(RECONCILIATION_FILTER);
		codes.add(RECONCILIATION_MODEL);
		codes.add(RECONCILIATION_UNION);
		codes.add(RECONCILIATION_AUTO_RECO);
		codes.add(RECONCILIATION_AUTO_RECO_SCHEDULER);
		// codes.add(RECONCILIATION_AUTO_RECO_SCHEDULER_LOG);
		
		
		codes.add(BILLING);
		codes.add(BILLING_EVENT);
		codes.add(BILLING_EVENT_REPOSITORY);
		codes.add(BILLING_TEMPLATE);
		codes.add(BILLING_CLIENT_EVENT_REPOSITORY);
		codes.add(BILLING_COMPANY_EVENT_REPOSITORY);
		codes.add(BILLING_JOIN_EVENT_REPOSITORY);
		codes.add(BILLING_MODEL);
		codes.add(BILLING_MODEL_SCHEDULER);
		codes.add(BILLING_RUN);
		codes.add(BILLING_RUN_OUTCOME);
		codes.add(BILLING_RUN_INVOICE);
		codes.add(BILLING_RUN_CREDIT_NOTE);
		codes.add(BILLING_RUN_STATUS);
		
		codes.add(SCHEDULER_PLANNER);
		codes.add(SCHEDULER_PLANNER_SCHEDULER);
		codes.add(SCHEDULER_PLANNER_SCHEDULER_LOG);
		codes.add(SCHEDULER_PLANNER_PRESENTATION);
		codes.add(SCHEDULER_PLANNER_PRESENTATION_TEMPLATE);
		
		codes.add(DATA_MANAGEMENT);
		codes.add(DATA_MANAGEMENT_ARCHIVE);
		codes.add(DATA_MANAGEMENT_ARCHIVE_LOG);
		codes.add(DATA_MANAGEMENT_ARCHIVE_CONFIG);
		codes.add(DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER);
		codes.add(DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER_LOG);
		
		codes.add(SETTINGS);
		codes.add(SETTINGS_INCREMENTAL_NUMBER);
		codes.add(SETTINGS_GROUP);
		codes.add(SETTINGS_INTEGRATION_SERVICE);
		codes.add(SETTINGS_DOCUMENT);
		codes.add(SETTINGS_LABEL);
		codes.add(SETTINGS_VARIABLE);
				
		codes.add(MESSENGER);
		codes.add(MESSENGER_EMAIL);
		codes.add(MESSENGER_SMS);
		codes.add(MESSENGER_LOG);
		
		codes.add(ADMINISTRATION);
		codes.add(ADMINISTRATION_PROFILE);
		codes.add(ADMINISTRATION_USER);
		codes.add(ADMINISTRATION_CONNECTED_USER);
		
		codes.add(FILE_MANAGER);
		
		codes.add(DYNAMIC_FORM);
		codes.add(TASK);
		codes.add(TASK_TEMPLATE);
		codes.add(CHAT);
		codes.add(DOCUMENT);
		
		codes.add(LICENSE_MANAGER);
		codes.add(LICENSE_MANAGER_PRODUCT);
		codes.add(LICENSE_MANAGER_LICENSE);
		
		codes.add(PROJECT_MANAGER);
		codes.add(PROJECT_MANAGER_TEST);
		codes.add(PROJECT_MANAGER_TEST_CASE);
		codes.add(PROJECT_MANAGER_PROJECT_VERSION);
		codes.add(PROJECT_MANAGER_MODULE);
		codes.add(PROJECT_MANAGER_FUNCTIONNALITY);
		codes.add(PROJECT_MANAGER_ISSUE);
		codes.add(PROJECT_MANAGER_TEST_STEP);
		codes.add(PROJECT_MANAGER_TEST_EXPECTED_RESULT);
		codes.add(PROJECT_MANAGER_TEST_SCENARIO);
		codes.add(PROJECT_MANAGER_TEST_CAMPAIGN);
		codes.add(PROJECT_MANAGER_TEST_CAMPAIGN_RESULT);
		
		return codes;
	}



}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Base
{
    public class FunctionalityCodes
    {

		public static string PROJECT = "project";
		public static string PROJECT_BACKUP = PROJECT + ".backup";
		public static string PROJECT_BACKUP_SCHEDULER = PROJECT_BACKUP + ".scheduler";
		public static string PROJECT_BACKUP_SCHEDULER_LOG = PROJECT_BACKUP_SCHEDULER + ".log";
		public static string PROJECT_IMPORT = PROJECT + ".import";

		public static string INITIATION = "initiation";
		public static string INITIATION_MODEL = INITIATION + ".model";
		public static string INITIATION_MEASURE = INITIATION + ".measure";
		public static string INITIATION_PERIOD = INITIATION + ".period";
		public static string INITIATION_CALENDAR = INITIATION + ".calendar";

		public static string SOURCING = "sourcing";
		public static string SOURCING_INPUT_GRID = SOURCING + ".input.grid";
		public static string SOURCING_INPUT_SPREADSHEET = SOURCING + ".input.spreadsheet";
		public static string SOURCING_FILE_LOADER = SOURCING + ".file.loader";
		public static string SOURCING_FILE_LOADER_SCHEDULER = SOURCING_FILE_LOADER + ".scheduler";
		public static string SOURCING_FILE_LOADER_SCHEDULER_LOG = SOURCING_FILE_LOADER_SCHEDULER + ".log";
		public static string SOURCING_SPOT = SOURCING + ".spot";
		public static string SOURCING_LINK = SOURCING + ".link";

		public static string TRANSFORMATION = "transformation";
		public static string TRANSFORMATION_TREE = TRANSFORMATION + ".tree";
		public static string TRANSFORMATION_TREE_SCHEDULER = TRANSFORMATION_TREE + ".scheduler";
		public static string TRANSFORMATION_TREE_SCHEDULER_LOG = TRANSFORMATION_TREE_SCHEDULER + ".log";

		public static string TRANSFORMATION_ROUTINE = TRANSFORMATION + ".routine";
		public static string TRANSFORMATION_ROUTINE_SCHEDULER = TRANSFORMATION_ROUTINE + ".scheduler";
		public static string TRANSFORMATION_ROUTINE_SCHEDULER_LOG = TRANSFORMATION_ROUTINE_SCHEDULER + ".log";

		public static string REPORTING = "reporting";
		public static string REPORTING_REPORT_GRID = REPORTING + ".report.grid";
		public static string REPORTING_REPORT_SPREADSHEET = REPORTING + ".report.spreadsheet";
		public static string REPORTING_PIVOT_TABLE = REPORTING + ".pivot.table";
		public static string REPORTING_CHART = REPORTING + ".chart";


		public static string DASHBOARDING = "dashboarding";
		public static string DASHBOARDING_DASHBOARD = DASHBOARDING + ".dashboard";
		public static string DASHBOARDING_ALARM = DASHBOARDING + ".alarm";
		public static string DASHBOARDING_ALARM_SCHEDULER = DASHBOARDING_ALARM + ".scheduler";
		public static string DASHBOARDING_ALARM_SCHEDULER_LOG = DASHBOARDING_ALARM_SCHEDULER + ".log";


		public static string RECONCILIATION = "reconciliation";
		public static string RECONCILIATION_FILTER = RECONCILIATION + ".filter";
		public static string RECONCILIATION_AUTO_RECO = RECONCILIATION + ".auto.reco";
		public static string RECONCILIATION_AUTO_RECO_SCHEDULER = RECONCILIATION_AUTO_RECO + ".scheduler";
		public static string RECONCILIATION_AUTO_RECO_SCHEDULER_LOG = RECONCILIATION_AUTO_RECO_SCHEDULER + ".log";



		public static string DATA_MANAGEMENT = "data.management";
		public static string DATA_MANAGEMENT_ARCHIVE = DATA_MANAGEMENT + ".archive";
		public static string DATA_MANAGEMENT_ARCHIVE_LOG = DATA_MANAGEMENT + ".log";
		public static string DATA_MANAGEMENT_ARCHIVE_CONFIG = DATA_MANAGEMENT + ".archive.config";
		public static string DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER = DATA_MANAGEMENT_ARCHIVE_CONFIG + ".scheduler";
		public static string DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER_LOG = DATA_MANAGEMENT_ARCHIVE_CONFIG_SCHEDULER + ".log";

		public static string SETTINGS = "settings";
		public static string SETTINGS_INCREMENTAL_NUMBER = SETTINGS + ".incremental.number";
		public static string SETTINGS_GROUP = SETTINGS + ".group";

		public static string ADMINISTRATION = "administration";
		public static string ADMINISTRATION_CLIENT = ADMINISTRATION + ".client";
		public static string ADMINISTRATION_PROFILE = ADMINISTRATION + ".profile";
		public static string ADMINISTRATION_USER = ADMINISTRATION + ".user";
		public static string ADMINISTRATION_CONNECTED_USER = ADMINISTRATION + ".connected.user";

	}
}

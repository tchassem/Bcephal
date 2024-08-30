using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Base
{
    public class SubjectType
    {

        public static SubjectType ALL = new SubjectType("All categories");

        public static SubjectType DEFAULT = new SubjectType("Default");

        public static SubjectType PROJECT = new SubjectType("Project");

        public static SubjectType USER = new SubjectType("User");
        public static SubjectType PROFIL = new SubjectType("Profil");
        public static SubjectType SUBSCRIPTION = new SubjectType("Subscription");
        public static SubjectType ADMIN_DASHBOARD = new SubjectType("Administration Dashboard");

        public static SubjectType PARAMETER = new SubjectType("Parameter");
        public static SubjectType GROUP = new SubjectType("Group");
        public static SubjectType INCREMENTAL_NUMBER = new SubjectType("Incremental Number");

        public static SubjectType MODEL = new SubjectType("Model");
        public static SubjectType MEASURE = new SubjectType("Measure");
        public static SubjectType PERIOD = new SubjectType("Period");
        public static SubjectType ATTRIBUTE = new SubjectType("Attribute");
        public static SubjectType ATTRIBUTE_VALUE = new SubjectType("Attribute Value");
        public static SubjectType ENTITY = new SubjectType("Entity");


        public static SubjectType INPUT_TABLE = new SubjectType("Input Table", "Table");
        public static SubjectType INPUT_GRID = new SubjectType("Input Grid", "Grid");
        public static SubjectType AUTOMATIC_TABLE = new SubjectType("Automatic Table");
        public static SubjectType AUTOMATIC_GRID = new SubjectType("Automatic Grid");
        public static SubjectType LINK = new SubjectType("Link");

        public static SubjectType FILE_LOADER = new SubjectType("File Loader");
        public static SubjectType FILE_LOADER_RUN = new SubjectType("Run File Loader");
        public static SubjectType FILE_LOADER_LOG = new SubjectType("File Loader Log");
        public static SubjectType FILE_LOADER_LOG_ITEM = new SubjectType("File Loader Log Item");

        public static SubjectType REPORT_TABLE = new SubjectType("Report Table", "Table");
        public static SubjectType REPORT_GRID = new SubjectType("Report Grid", "Grid");
        public static SubjectType STRUCTURED_REPORT = new SubjectType("Structured Report");
        public static SubjectType CALCULATED_MEASURE = new SubjectType("Calculated Measure");

        public static SubjectType TRANSFORMATION_TREE = new SubjectType("Transformation Tree");
        public static SubjectType TRANSFORMATION_TREE_RUN = new SubjectType("Transformation Tree Run");
        public static SubjectType COMBINED_TRANSFORMATION_TREE = new SubjectType("Combined Transformation Tree");
        public static SubjectType LOOP = new SubjectType("Loop");
        public static SubjectType PRESENTATION = new SubjectType("Presentation");

        public static SubjectType RECONCILIATION = new SubjectType("Reconciliation");
        public static SubjectType RECONCILIATION_FILTER = new SubjectType("Reconciliation Filter");
        public static SubjectType AUTOMATIC_RECONCILIATION = new SubjectType("Automatic Reconciliation");
        public static SubjectType AUTOMATIC_RECONCILIATION_LOG = new SubjectType("Automatic Reconciliation Log");
        public static SubjectType AUTOMATIC_RECONCILIATION_LOG_ITEM = new SubjectType("Automatic Reconciliation Log Item");
        public static SubjectType AUTOMATIC_RECONCILIATION_RUN = new SubjectType("Run Automatic Reconciliations");

        public static SubjectType REPORT_PUBLICATION = new SubjectType("Report Publication");
        public static SubjectType DASHBOARD_REPORT = new SubjectType("Dashboard Report");
        public static SubjectType DASHBOARD_REPORT_PIVOT_TABLE = new SubjectType("Pivot Table");
        public static SubjectType DASHBOARD_REPORT_CHART = new SubjectType("Chart");
        public static SubjectType DASHBOARD_REPORT_GRID = new SubjectType("Drill Down Grid");

        public static SubjectType DASHBOARD = new SubjectType("Dashboard");
        public static SubjectType ALARM = new SubjectType("Alarm");
        public static SubjectType ALARM_RUN = new SubjectType("Alarm Run");

        public static SubjectType BILLING_EVENT = new SubjectType("Billing event");
        public static SubjectType BILLING_EVENT_REPOSITORY = new SubjectType("Billing event repository");
        public static SubjectType BILLING_MODELS = new SubjectType("Billing models");
        public static SubjectType BILLING_MODEL = new SubjectType("Billing model");
        public static SubjectType BILLING_MODEL_SCHEDULED = new SubjectType("Billing model scheduled");
        public static SubjectType BILLING_MODEL_TEMPLATE = new SubjectType("Billing model template");
        public static SubjectType BILLING_RUN = new SubjectType("Billing run");
        public static SubjectType INVOICE = new SubjectType("Invoice");
        public static SubjectType CREDIT_NOTE = new SubjectType("Credit note");
        public static SubjectType BILLING_RUN_OUTCOME = new SubjectType("Billing run outcome");
        public static SubjectType BILLING_RUN_STATUS = new SubjectType("Billing run status");

        public static SubjectType DYNAMIC_FORM = new SubjectType("Dynamic form");
        public static SubjectType DATA_ARCHIVE = new SubjectType("Data Archive");
        public static SubjectType DATA_ARCHIVE_LOG = new SubjectType("Data Archive Log");
        public static SubjectType DATA_ARCHIVE_CONFIG = new SubjectType("Data Archive Config");

        


        public string Label { get; set; }

        [JsonIgnore]
        public string ShortName { get; set; }

        private SubjectType(string label)
        {
            this.Label = label;
        }

        private SubjectType(string label, string shortName) : this(label)
        {
            this.ShortName = shortName;
        }

        public override string ToString()
        {
            return Label;
        }

    }
}


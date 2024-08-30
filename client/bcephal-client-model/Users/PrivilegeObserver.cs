using Bcephal.Models.Base;
using Bcephal.Models.Profiles;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Users
{
    public class PrivilegeObserver
    {

        #region project Privillege
        [JsonIgnore]
        public bool ProjectAllowed { get => ProjectViewAllowed || ProjectEditAllowed || ProjectCreateAllowed; }
       // public bool ProjectAllowed { get; set; }
		public bool ProjectViewAllowed { get; set; }
		public bool ProjectEditAllowed { get; set; }
		public bool ProjectCreateAllowed { get; set; }

        [JsonIgnore]
        public bool ProjectBackupAllowed { get => ProjectBackupViewAllowed || ProjectBackupEditAllowed || ProjectBackupCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedProjectBackup { get => ProjectCreateAllowed && ProjectBackupCreateAllowed; }
        public bool CanEditProjectBackup<P>(P item) where P : IPersistent => (ProjectEditAllowed || ProjectCreateAllowed) && ProjectBackupEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewProjectBackup { get => ProjectViewAllowed && ProjectBackupViewAllowed; }

        public bool ProjectBackupViewAllowed { get; set; }
		public bool ProjectBackupEditAllowed { get; set; }
		public bool ProjectBackupCreateAllowed { get; set; }

        [JsonIgnore]
        public bool ProjectBackupSchedulerAllowed { get => ProjectBackupSchedulerViewAllowed || ProjectBackupSchedulerEditAllowed || ProjectBackupSchedulerCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedProjectBackupScheduler { get => ProjectCreateAllowed && ProjectBackupSchedulerCreateAllowed; }
        public bool CanEditProjectBackupScheduler<P>(P item) where P : IPersistent => (ProjectEditAllowed || ProjectCreateAllowed) && ProjectBackupSchedulerEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewProjectBackupScheduler { get => ProjectViewAllowed && ProjectBackupSchedulerViewAllowed; }

        public bool ProjectBackupSchedulerViewAllowed { get; set; }
		public bool ProjectBackupSchedulerEditAllowed { get; set; }
		public bool ProjectBackupSchedulerCreateAllowed { get; set; }

        [JsonIgnore]
        public bool ProjectImportAllowed { get => ProjectImportViewAllowed || ProjectImportEditAllowed || ProjectImportCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedProjectImport { get => ProjectCreateAllowed && ProjectImportCreateAllowed; }
        public bool CanEditProjectImport<P>(P item) where P : IPersistent => (ProjectEditAllowed || ProjectCreateAllowed) && ProjectImportEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewProjectImport { get => ProjectViewAllowed && ProjectImportViewAllowed; }

        public bool ProjectImportViewAllowed { get; set; }
		public bool ProjectImportEditAllowed { get; set; }
		public bool ProjectImportCreateAllowed { get; set; }

        [JsonIgnore]
        public bool ProjectAccessRightAllowed { get => ProjectAccessRightViewAllowed || ProjectAccessRightEditAllowed || ProjectAccessRightCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedProjectAccessRight { get => ProjectCreateAllowed && ProjectAccessRightCreateAllowed; }
        public bool CanEditProjectAccessRight<P>(P item) where P : IPersistent => (ProjectEditAllowed || ProjectCreateAllowed) && ProjectAccessRightEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewProjectAccessRight { get => ProjectViewAllowed && ProjectAccessRightViewAllowed; }

        public bool ProjectAccessRightViewAllowed { get; set; }
		public bool ProjectAccessRightEditAllowed { get; set; }
		public bool ProjectAccessRightCreateAllowed { get; set; }
        #endregion


        #region Initiation privillege
        [JsonIgnore]
        public bool InitiationAllowed { get => InitiationViewAllowed || InitiationEditAllowed || InitiationCreateAllowed; }
        //public bool InitiationAllowed { get; set; }
        public bool InitiationViewAllowed { get; set; }
		public bool InitiationEditAllowed { get; set; }
		public bool InitiationCreateAllowed { get; set; }

        [JsonIgnore]
        public bool InitiationModelAllowed { get => InitiationModelViewAllowed || InitiationModelEditAllowed || InitiationModelCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedInitiationModel { get => InitiationCreateAllowed && InitiationModelCreateAllowed; }
        public bool CanEditInitiationModel<P>(P item) where P : IPersistent => (InitiationEditAllowed || InitiationCreateAllowed) && InitiationModelEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewInitiationModel { get => InitiationViewAllowed && InitiationModelViewAllowed; }

        public bool InitiationModelViewAllowed { get; set; }
		public bool InitiationModelEditAllowed { get; set; }
		public bool InitiationModelCreateAllowed { get; set; }

        [JsonIgnore]
        public bool InitiationMeasureAllowed { get => InitiationMeasureViewAllowed || InitiationMeasureEditAllowed || InitiationMeasureCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedInitiationMeasure { get => InitiationCreateAllowed && InitiationMeasureCreateAllowed; }
        public bool CanEditInitiationMeasure<P>(P item) where P : IPersistent => (InitiationEditAllowed || InitiationCreateAllowed) && InitiationMeasureEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewInitiationMeasure { get => InitiationViewAllowed && InitiationMeasureViewAllowed; }

        public bool InitiationMeasureViewAllowed { get; set; }
		public bool InitiationMeasureEditAllowed { get; set; }
		public bool InitiationMeasureCreateAllowed { get; set; }

        [JsonIgnore]
        public bool InitiationPeriodAllowed { get => InitiationPeriodViewAllowed || InitiationPeriodEditAllowed || InitiationPeriodCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedInitiationPeriod { get => InitiationCreateAllowed && InitiationPeriodCreateAllowed; }
        public bool CanEditInitiationPeriod<P>(P item) where P : IPersistent => (InitiationEditAllowed || InitiationCreateAllowed) && InitiationPeriodEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewInitiationPeriod { get => InitiationViewAllowed && InitiationPeriodViewAllowed; }

        public bool InitiationPeriodViewAllowed { get; set; }
		public bool InitiationPeriodEditAllowed { get; set; }
		public bool InitiationPeriodCreateAllowed { get; set; }

        
        //public bool InitiationCalendarAllowed { get; set; }
        public bool InitiationCalendarViewAllowed { get; set; }
		public bool InitiationCalendarEditAllowed { get; set; }
		public bool InitiationCalendarCreateAllowed { get; set; }

        #endregion

        #region Sourcing privillege

        //public bool SourcingAllowed { get; set; }
        [JsonIgnore]
        public bool SourcingAllowed { get => SourcingViewAllowed || SourcingEditAllowed || SourcingCreateAllowed; }
        public bool SourcingViewAllowed { get; set; }
		public bool SourcingEditAllowed { get; set; }
		public bool SourcingCreateAllowed { get; set; }

        [JsonIgnore]
        public bool SourcingInputGridAllowed { get => SourcingInputGridViewAllowed || SourcingInputGridExportAllowed || SourcingInputGridEditAllowed || SourcingInputGridCreateAllowed; }


        [JsonIgnore]
        public bool CanCreatedSourcingInputGrid { get => SourcingCreateAllowed && SourcingInputGridCreateAllowed; }
        public bool CanEditSourcingInputGrid<P>(P item) where P: IPersistent  => (SourcingEditAllowed || SourcingCreateAllowed) && SourcingInputGridEditAllowed && item.Id.HasValue; 
        [JsonIgnore]
        public bool CanViewSourcingInputGrid { get => SourcingViewAllowed && SourcingInputGridViewAllowed; }

        public bool SourcingInputGridViewAllowed { get; set; }
		public bool SourcingInputGridExportAllowed { get; set; }
		public bool SourcingInputGridEditAllowed { get; set; }
		public bool SourcingInputGridCreateAllowed { get; set; }

        [JsonIgnore]
        public bool SourcingInputSpreadsheetAllowed { get => SourcingInputSpreadsheetViewAllowed || SourcingInputSpreadsheetLoadAllowed || SourcingInputSpreadsheetEditAllowed || SourcingInputSpreadsheetCreateAllowed; }

        [JsonIgnore]
        public bool CanCreatedSourcingInputSpreadsheet { get => SourcingCreateAllowed && SourcingInputSpreadsheetCreateAllowed; }
        public bool CanEditSourcingInputSpreadsheet<P>(P item) where P : IPersistent => (SourcingEditAllowed || SourcingCreateAllowed) && SourcingInputSpreadsheetEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewSourcingInputSpreadsheet { get => SourcingViewAllowed && SourcingInputSpreadsheetViewAllowed; }

        public bool SourcingInputSpreadsheetViewAllowed { get; set; }
		public bool SourcingInputSpreadsheetLoadAllowed { get; set; }
		public bool SourcingInputSpreadsheetEditAllowed { get; set; }
		public bool SourcingInputSpreadsheetCreateAllowed { get; set; }

        [JsonIgnore]
        public bool SourcingFileLoaderAllowed { get => SourcingFileLoaderViewAllowed || SourcingFileLoaderEditAllowed || SourcingFileLoaderCreateAllowed; }

        [JsonIgnore]
        public bool CanCreatedSourcingFileLoader { get => SourcingCreateAllowed && SourcingFileLoaderCreateAllowed; }
        public bool CanEditSourcingFileLoader<P>(P item) where P : IPersistent => (SourcingEditAllowed || SourcingCreateAllowed) && SourcingFileLoaderEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewSourcingFileLoader { get => SourcingViewAllowed && SourcingFileLoaderViewAllowed; }

        public bool SourcingFileLoaderViewAllowed { get; set; }
		public bool SourcingFileLoaderEditAllowed { get; set; }
		public bool SourcingFileLoaderCreateAllowed { get; set; }



        [JsonIgnore]
        public bool SourcingFileLoaderSchedulerAllowed { get => SourcingFileLoaderSchedulerViewAllowed || SourcingFileLoaderSchedulerEditAllowed || SourcingFileLoaderSchedulerCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedSourcingFileLoaderScheduler { get => SourcingCreateAllowed && SourcingFileLoaderSchedulerCreateAllowed; }
        public bool CanEditSourcingFileLoaderScheduler<P>(P item) where P : IPersistent => (SourcingEditAllowed || SourcingCreateAllowed) && SourcingFileLoaderSchedulerEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewSourcingFileLoaderScheduler { get => SourcingViewAllowed && SourcingFileLoaderSchedulerViewAllowed; }

        public bool SourcingFileLoaderSchedulerViewAllowed { get; set; }
		public bool SourcingFileLoaderSchedulerEditAllowed { get; set; }
		public bool SourcingFileLoaderSchedulerCreateAllowed { get; set; }



        [JsonIgnore]
        public bool SourcingFileLoaderSchedulerLogAllowed { get => SourcingFileLoaderSchedulerLogViewAllowed || SourcingFileLoaderSchedulerLogEditAllowed; }
       
        //public bool CanEditSourcingFileLoaderSchedulerLog<P>(P item) where P : IPersistent => (SourcingEditAllowed || SourcingCreateAllowed) && SourcingFileLoaderSchedulerLogEditAllowed && item.Id.HasValue;
        //[JsonIgnore]
        //public bool CanViewSourcingFileLoaderSchedulerLog { get => SourcingViewAllowed && SourcingFileLoaderSchedulerLogViewAllowed; }

        public bool SourcingFileLoaderSchedulerLogViewAllowed { get; set; }
		public bool SourcingFileLoaderSchedulerLogEditAllowed { get; set; }



        [JsonIgnore]
        public bool SourcingSpotAllowed { get => SourcingSpotViewAllowed || SourcingSpotEditAllowed || SourcingSpotCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedSourcingSpot { get => SourcingCreateAllowed && SourcingSpotCreateAllowed; }
        public bool CanEditSourcingSpot<P>(P item) where P : IPersistent => (SourcingEditAllowed || SourcingCreateAllowed) && SourcingSpotEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewSourcingSpot { get => SourcingViewAllowed && SourcingSpotViewAllowed; }

        public bool SourcingSpotViewAllowed { get; set; }
		public bool SourcingSpotEditAllowed { get; set; }
		public bool SourcingSpotCreateAllowed { get; set; }



        [JsonIgnore]
        public bool SourcingLingAllowed { get => SourcingLingViewAllowed || SourcingLingEditAllowed || SourcingLingCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedSourcingLing { get => SourcingCreateAllowed && SourcingLingCreateAllowed; }
        public bool CanEditSourcingLing<P>(P item) where P : IPersistent => (SourcingEditAllowed || SourcingCreateAllowed) && SourcingLingEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewSourcingLing { get => SourcingViewAllowed && SourcingLingViewAllowed; }

        public bool SourcingLingViewAllowed { get; set; }
		public bool SourcingLingEditAllowed { get; set; }
		public bool SourcingLingCreateAllowed { get; set; }

        #endregion


        #region Transformation Privillege
        [JsonIgnore]
        public bool TransformationAllowed { get => TransformationViewAllowed || TransformationEditAllowed || TransformationCreateAllowed; }
        [JsonIgnore]
        //public bool CanCreatedTransformation { get => TransformationCreateAllowed && TransformationCreateAllowed; }
        //public bool CanEditTransformation<P>(P item) where P : IPersistent => (TransformationEditAllowed || TransformationCreateAllowed) && TransformationEditAllowed && item.Id.HasValue;
        //[JsonIgnore]
        //public bool CanViewTransformation { get => TransformationViewAllowed && TransformationViewAllowed; }
        public bool TransformationViewAllowed { get; set; }
        public bool TransformationEditAllowed { get; set; }
        public bool TransformationCreateAllowed { get; set; }



        [JsonIgnore]
        public bool TransformationTreeAllowed { get => TransformationTreeViewAllowed || TransformationTreeRunAllowed || TransformationTreeClearAllowed || TransformationTreeEditAllowed || TransformationTreeCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedTransformationTree { get => TransformationCreateAllowed && TransformationTreeCreateAllowed; }
        public bool CanEditTransformationTree<P>(P item) where P : IPersistent => (TransformationEditAllowed || TransformationCreateAllowed) && TransformationTreeEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewTransformationTree { get => TransformationViewAllowed && TransformationTreeViewAllowed; }

        public bool TransformationTreeViewAllowed { get; set; }
		public bool TransformationTreeRunAllowed { get; set; }
		public bool TransformationTreeClearAllowed { get; set; }
		public bool TransformationTreeEditAllowed { get; set; }
		public bool TransformationTreeCreateAllowed { get; set; }

        [JsonIgnore]
        public bool TransformationTreeSchedulerAllowed { get => TransformationTreeSchedulerViewAllowed || TransformationTreeSchedulerEditAllowed || TransformationTreeSchedulerCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedTransformationTreeScheduler { get => TransformationCreateAllowed && TransformationTreeSchedulerCreateAllowed; }
        public bool CanEditTransformationTreeScheduler<P>(P item) where P : IPersistent => (TransformationEditAllowed || TransformationCreateAllowed) && TransformationTreeSchedulerEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewTransformationTreeScheduler { get => TransformationViewAllowed && TransformationTreeSchedulerViewAllowed; }

        public bool TransformationTreeSchedulerViewAllowed { get; set; }
		public bool TransformationTreeSchedulerEditAllowed { get; set; }
		public bool TransformationTreeSchedulerCreateAllowed { get; set; }

        [JsonIgnore]
        public bool TransformationTreeSchedulerLogAllowed { get => TransformationTreeSchedulerLogViewAllowed || TransformationTreeSchedulerLogEditAllowed; }
      
        //public bool CanEditTransformationTreeSchedulerLog<P>(P item) where P : IPersistent => (TransformationEditAllowed || TransformationCreateAllowed) && TransformationTreeSchedulerLogEditAllowed && item.Id.HasValue;
        //[JsonIgnore]
        //public bool CanViewTransformationTreeSchedulerLog { get => TransformationViewAllowed && TransformationTreeSchedulerLogViewAllowed; }

        public bool TransformationTreeSchedulerLogViewAllowed { get; set; }
		public bool TransformationTreeSchedulerLogEditAllowed { get; set; }



        [JsonIgnore]
        public bool TransformationRoutineAllowed { get => TransformationRoutineViewAllowed || TransformationRoutineEditAllowed || TransformationRoutineCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedTransformationRoutine { get => TransformationCreateAllowed && TransformationRoutineCreateAllowed; }
        public bool CanEditTransformationRoutine<P>(P item) where P : IPersistent => (TransformationEditAllowed || TransformationCreateAllowed) && TransformationRoutineEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewTransformationRoutine { get => TransformationViewAllowed && TransformationRoutineViewAllowed; }

        public bool TransformationRoutineViewAllowed { get; set; }
		public bool TransformationRoutineEditAllowed { get; set; }
		public bool TransformationRoutineCreateAllowed { get; set; }

        [JsonIgnore]
        public bool TransformationRoutineSchedulerAllowed { get => TransformationRoutineSchedulerViewAllowed || TransformationRoutineSchedulerEditAllowed || TransformationRoutineSchedulerCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedTransformationRoutineScheduler { get => TransformationCreateAllowed && TransformationRoutineSchedulerCreateAllowed; }
        public bool CanEditTransformationRoutineScheduler<P>(P item) where P : IPersistent => (TransformationEditAllowed || TransformationCreateAllowed) && TransformationRoutineSchedulerEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewTransformationRoutineScheduler { get => TransformationViewAllowed && TransformationRoutineSchedulerViewAllowed; }

        public bool TransformationRoutineSchedulerViewAllowed { get; set; }
		public bool TransformationRoutineSchedulerEditAllowed { get; set; }
		public bool TransformationRoutineSchedulerCreateAllowed { get; set; }

        [JsonIgnore]
        public bool TransformationRoutineSchedulerLogAllowed { get => TransformationRoutineSchedulerLogViewAllowed || TransformationRoutineSchedulerLogEditAllowed || TransformationRoutineSchedulerLogCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedTransformationRoutineSchedulerLog { get => TransformationCreateAllowed && TransformationRoutineSchedulerLogCreateAllowed; }
        public bool CanEditTransformationRoutineLogScheduler<P>(P item) where P : IPersistent => (TransformationEditAllowed || TransformationCreateAllowed) && TransformationRoutineSchedulerLogEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewTransformationRoutineSchedulerLog { get => TransformationViewAllowed && TransformationRoutineSchedulerLogViewAllowed; }

        public bool TransformationRoutineSchedulerLogViewAllowed { get; set; }
		public bool TransformationRoutineSchedulerLogEditAllowed { get; set; }
		public bool TransformationRoutineSchedulerLogCreateAllowed { get; set; }

        #endregion

        #region Reporting Privillege
        [JsonIgnore]
        public bool ReportingAllowed { get => ReportingViewAllowed || ReportingEditAllowed || ReportingCreateAllowed; }
        //public bool ReportingAllowed { get; set; }
        public bool ReportingViewAllowed { get; set; }
		public bool ReportingEditAllowed { get; set; }
		public bool ReportingCreateAllowed { get; set; }

        [JsonIgnore]
        public bool ReportingReportGridAllowed { get => ReportingReportGridViewAllowed || ReportingReportGridExportAllowed || ReportingReportGridEditAllowed || ReportingReportGridCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedReportingReportGrid { get => ReportingCreateAllowed && ReportingReportGridCreateAllowed; }
        public bool CanEditReportingReportGrid<P>(P item) where P : IPersistent => (ReportingEditAllowed || ReportingCreateAllowed) && ReportingReportGridEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewReportingReportGrid { get => ReportingViewAllowed && ReportingReportGridViewAllowed; }

        public bool ReportingReportGridViewAllowed { get; set; }
		public bool ReportingReportGridExportAllowed { get; set; }
		public bool ReportingReportGridEditAllowed { get; set; }
		public bool ReportingReportGridCreateAllowed { get; set; }

        [JsonIgnore]
        public bool ReportingPivotTableAllowed { get => ReportingPivotTableViewAllowed || ReportingPivotTableEditAllowed || ReportingPivotTableCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedReportingPivotTable { get => ReportingCreateAllowed && ReportingPivotTableCreateAllowed; }
        public bool CanEditReportingPivotTable<P>(P item) where P : IPersistent => (ReportingEditAllowed || ReportingCreateAllowed) && ReportingPivotTableEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewReportingPivotTable { get => ReportingViewAllowed && ReportingPivotTableViewAllowed; }

        public bool ReportingPivotTableViewAllowed { get; set; }
		public bool ReportingPivotTableEditAllowed { get; set; }
		public bool ReportingPivotTableCreateAllowed { get; set; }

        [JsonIgnore]
        public bool ReportingChartAllowed { get => ReportingChartViewAllowed || ReportingChartEditAllowed || ReportingChartCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedReportingChart { get => ReportingCreateAllowed && ReportingChartCreateAllowed; }
        public bool CanEditReportingChart<P>(P item) where P : IPersistent => (ReportingEditAllowed || ReportingCreateAllowed) && ReportingChartEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewReportingChart { get => ReportingViewAllowed && ReportingChartViewAllowed; }

        public bool ReportingChartViewAllowed { get; set; }
		public bool ReportingChartEditAllowed { get; set; }
		public bool ReportingChartCreateAllowed { get; set; }
        #endregion


        #region Dashbording Privillege
        [JsonIgnore]
        public bool DashboardingAllowed { get => DashboardingViewAllowed || DashboardingEditAllowed || DashboardingCreateAllowed; }
        //public bool DashboardingAllowed { get; set; }
        public bool DashboardingViewAllowed { get; set; }
		public bool DashboardingEditAllowed { get; set; }
		public bool DashboardingCreateAllowed { get; set; }

        [JsonIgnore]
        public bool DashboardingDashboardAllowed { get => DashboardingDashboardViewAllowed || DashboardingDashboardEditAllowed || DashboardingDashboardCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedDashboardingDashboard { get => DashboardingCreateAllowed && DashboardingDashboardCreateAllowed; }
        public bool CanEditDashboardingDashboard<P>(P item) where P : IPersistent => (DashboardingEditAllowed || DashboardingCreateAllowed) && DashboardingDashboardEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewDashboardingDashboard { get => DashboardingViewAllowed && DashboardingDashboardViewAllowed; }

        public bool DashboardingDashboardViewAllowed { get; set; }
		public bool DashboardingDashboardEditAllowed { get; set; }
		public bool DashboardingDashboardCreateAllowed { get; set; }

        [JsonIgnore]
        public bool DashboardingAlarmAllowed { get => DashboardingAlarmViewAllowed || DashboardingAlarmEditAllowed || DashboardingAlarmCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedDashboardingAlarm { get => DashboardingCreateAllowed && DashboardingAlarmCreateAllowed; }
        public bool CanEditDashboardingAlarm<P>(P item) where P : IPersistent => (DashboardingEditAllowed || DashboardingCreateAllowed) && DashboardingAlarmEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewDashboardingAlarm { get => DashboardingViewAllowed && DashboardingAlarmViewAllowed; }

        public bool DashboardingAlarmViewAllowed { get; set; }
		public bool DashboardingAlarmEditAllowed { get; set; }
		public bool DashboardingAlarmCreateAllowed { get; set; }

        [JsonIgnore]
        public bool DashboardingAlarmSchedulerAllowed { get => DashboardingAlarmSchedulerViewAllowed || DashboardingAlarmSchedulerEditAllowed || DashboardingAlarmSchedulerCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedDashboardingAlarmScheduler { get => DashboardingCreateAllowed && DashboardingAlarmSchedulerCreateAllowed; }
        public bool CanEditDashboardingAlarmScheduler<P>(P item) where P : IPersistent => (DashboardingEditAllowed || DashboardingCreateAllowed) && DashboardingAlarmSchedulerEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewDashboardingAlarmScheduler { get => DashboardingViewAllowed && DashboardingAlarmSchedulerViewAllowed; }

        public bool DashboardingAlarmSchedulerViewAllowed { get; set; }
		public bool DashboardingAlarmSchedulerEditAllowed { get; set; }
		public bool DashboardingAlarmSchedulerCreateAllowed { get; set; }

        [JsonIgnore]
        public bool DashboardingAlarmSchedulerLogAllowed { get => DashboardingAlarmSchedulerLogViewAllowed || DashboardingAlarmSchedulerLogEditAllowed || DashboardingAlarmSchedulerLogCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedDashboardingAlarmSchedulerLog { get => DashboardingCreateAllowed && DashboardingAlarmSchedulerLogCreateAllowed; }
        public bool CanEditDashboardingAlarmSchedulerLog<P>(P item) where P : IPersistent => (DashboardingEditAllowed || DashboardingCreateAllowed) && DashboardingAlarmSchedulerLogEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewDashboardingAlarmSchedulerLog { get => DashboardingViewAllowed && DashboardingAlarmSchedulerLogViewAllowed; }

        public bool DashboardingAlarmSchedulerLogViewAllowed { get; set; }
		public bool DashboardingAlarmSchedulerLogEditAllowed { get; set; }
		public bool DashboardingAlarmSchedulerLogCreateAllowed { get; set; }

        #endregion

        #region Reconcilliation privillege

        [JsonIgnore]
        public bool ReconciliationAllowed { get => ReconciliationViewAllowed || ReconciliationEditAllowed || ReconciliationCreateAllowed; }
        //public bool ReconciliationAllowed { get; set; }
        public bool ReconciliationViewAllowed { get; set; }
		public bool ReconciliationEditAllowed { get; set; }
		public bool ReconciliationCreateAllowed { get; set; }

        [JsonIgnore]
        public bool ReconciliationFilterAllowed { get => ReconciliationFilterViewAllowed || ReconciliationFilterEditAllowed || ReconciliationFilterCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedReconciliationFilter { get => ReconciliationCreateAllowed && ReconciliationFilterCreateAllowed; }
        public bool CanEditReconciliationFilter<P>(P item) where P : IPersistent => (ReconciliationEditAllowed || ReconciliationCreateAllowed) && ReconciliationFilterEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewReconciliationFilter { get => ReconciliationViewAllowed && ReconciliationFilterViewAllowed; }

        public bool ReconciliationFilterViewAllowed { get; set; }
		public bool ReconciliationFilterEditAllowed { get; set; }
		public bool ReconciliationFilterCreateAllowed { get; set; }

        [JsonIgnore]
        public bool ReconciliationAutoRecoAllowed { get => ReconciliationAutoRecoViewAllowed || ReconciliationAutoRecoEditAllowed || ReconciliationAutoRecoCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedReconciliationAutoReco { get => ReconciliationCreateAllowed && ReconciliationAutoRecoCreateAllowed; }
        public bool CanEditReconciliationAutoReco<P>(P item) where P : IPersistent => (ReconciliationEditAllowed || ReconciliationCreateAllowed) && ReconciliationAutoRecoEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewReconciliationAutoReco { get => ReconciliationViewAllowed && ReconciliationAutoRecoViewAllowed; }

        public bool ReconciliationAutoRecoViewAllowed { get; set; }
		public bool ReconciliationAutoRecoEditAllowed { get; set; }
		public bool ReconciliationAutoRecoCreateAllowed { get; set; }

        [JsonIgnore]
        public bool ReconciliationAutoRecoSchedulerAllowed { get => ReconciliationAutoRecoSchedulerViewAllowed || ReconciliationAutoRecoSchedulerEditAllowed || ReconciliationAutoRecoSchedulerCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedReconciliationAutoRecoScheduler { get => ReconciliationCreateAllowed && ReconciliationAutoRecoSchedulerCreateAllowed; }
        public bool CanEditReconciliationAutoRecoScheduler<P>(P item) where P : IPersistent => (ReconciliationEditAllowed || ReconciliationCreateAllowed) && ReconciliationAutoRecoSchedulerEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewReconciliationAutoRecoScheduler { get => ReconciliationViewAllowed && ReconciliationAutoRecoSchedulerViewAllowed; }

        public bool ReconciliationAutoRecoSchedulerViewAllowed { get; set; }
		public bool ReconciliationAutoRecoSchedulerEditAllowed { get; set; }
		public bool ReconciliationAutoRecoSchedulerCreateAllowed { get; set; }

        [JsonIgnore]
        public bool ReconciliationAutoRecoSchedulerLogAllowed { get => ReconciliationAutoRecoSchedulerLogViewAllowed || ReconciliationAutoRecoSchedulerLogEditAllowed || ReconciliationAutoRecoSchedulerLogCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedReconciliationAutoRecoSchedulerLog { get => ReconciliationCreateAllowed && ReconciliationAutoRecoSchedulerLogCreateAllowed; }
        public bool CanEditReconciliationAutoRecoSchedulerLog<P>(P item) where P : IPersistent => (ReconciliationEditAllowed || ReconciliationCreateAllowed) && ReconciliationAutoRecoSchedulerLogEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewReconciliationAutoRecoSchedulerLog { get => ReconciliationViewAllowed && ReconciliationAutoRecoSchedulerLogViewAllowed; }

        public bool ReconciliationAutoRecoSchedulerLogViewAllowed { get; set; }
		public bool ReconciliationAutoRecoSchedulerLogEditAllowed { get; set; }
		public bool ReconciliationAutoRecoSchedulerLogCreateAllowed { get; set; }
        #endregion

        #region DataManager Privillege

        [JsonIgnore]
        public bool DataManagementAllowed { get => DataManagementViewAllowed || DataManagementEditAllowed || DataManagementCreateAllowed; }
        //public bool DataManagementAllowed { get; set; }
		public bool DataManagementViewAllowed { get; set; }
		public bool DataManagementEditAllowed { get; set; }
		public bool DataManagementCreateAllowed { get; set; }

        [JsonIgnore]
        public bool DataManagementArchiveAllowed { get => DataManagementArchiveViewAllowed || DataManagementArchiveEditAllowed || DataManagementArchiveCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedDataManagementArchive { get => DataManagementCreateAllowed && DataManagementArchiveCreateAllowed; }
        public bool CanEditDataManagementArchive<P>(P item) where P : IPersistent => (DataManagementEditAllowed || DataManagementCreateAllowed) && DataManagementArchiveEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewDataManagementArchive { get => DataManagementViewAllowed && DataManagementArchiveViewAllowed; }

        public bool DataManagementArchiveViewAllowed { get; set; }
		public bool DataManagementArchiveEditAllowed { get; set; }
		public bool DataManagementArchiveCreateAllowed { get; set; }

        [JsonIgnore]
        public bool DataManagementArchiveLogAllowed { get => DataManagementArchiveLogViewAllowed || DataManagementArchiveLogEditAllowed || DataManagementArchiveLogCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedDataManagementArchiveLog { get => DataManagementCreateAllowed && DataManagementArchiveLogCreateAllowed; }
        public bool CanEditDataManagementArchiveLog<P>(P item) where P : IPersistent => (DataManagementEditAllowed || DataManagementCreateAllowed) && DataManagementArchiveLogEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewDataManagementArchiveLog { get => DataManagementViewAllowed && DataManagementArchiveLogViewAllowed; }

        public bool DataManagementArchiveLogViewAllowed { get; set; }
		public bool DataManagementArchiveLogEditAllowed { get; set; }
		public bool DataManagementArchiveLogCreateAllowed { get; set; }

        [JsonIgnore]
        public bool DataManagementArchiveConfigAllowed { get => DataManagementArchiveConfigViewAllowed || DataManagementArchiveConfigEditAllowed || DataManagementArchiveConfigCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedDataManagementArchiveConfig { get => DataManagementCreateAllowed && DataManagementArchiveConfigCreateAllowed; }
        public bool CanEditDataManagementArchiveConfig<P>(P item) where P : IPersistent => (DataManagementEditAllowed || DataManagementCreateAllowed) && DataManagementArchiveConfigEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewDataManagementArchiveConfig { get => DataManagementViewAllowed && DataManagementArchiveConfigViewAllowed; }

        public bool DataManagementArchiveConfigViewAllowed { get; set; }
		public bool DataManagementArchiveConfigEditAllowed { get; set; }
		public bool DataManagementArchiveConfigCreateAllowed { get; set; }

        [JsonIgnore]
        public bool DataManagementArchiveConfigSchedulerAllowed { get => DataManagementArchiveConfigSchedulerViewAllowed || DataManagementArchiveConfigSchedulerEditAllowed || DataManagementArchiveConfigSchedulerCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedDataManagementArchiveConfigScheduler { get => DataManagementCreateAllowed && DataManagementArchiveConfigSchedulerCreateAllowed; }
        public bool CanEditDataManagementArchiveConfigScheduler<P>(P item) where P : IPersistent => (DataManagementEditAllowed || DataManagementCreateAllowed) && DataManagementArchiveConfigSchedulerEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewDataManagementArchiveConfigScheduler { get => DataManagementViewAllowed && DataManagementArchiveConfigSchedulerViewAllowed; }

        public bool DataManagementArchiveConfigSchedulerViewAllowed { get; set; }
		public bool DataManagementArchiveConfigSchedulerEditAllowed { get; set; }
		public bool DataManagementArchiveConfigSchedulerCreateAllowed { get; set; }

        [JsonIgnore]
        public bool DataManagementArchiveConfigSchedulerLogAllowed { get => DataManagementArchiveConfigSchedulerLogViewAllowed || DataManagementArchiveConfigSchedulerLogEditAllowed || DataManagementArchiveConfigSchedulerLogCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedDataManagementArchiveConfigSchedulerLog { get => DataManagementCreateAllowed && DataManagementArchiveConfigSchedulerLogCreateAllowed; }
        public bool CanEditDataManagementArchiveConfigSchedulerLog<P>(P item) where P : IPersistent => (DataManagementEditAllowed || DataManagementCreateAllowed) && DataManagementArchiveConfigSchedulerLogEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewDataManagementArchiveConfigSchedulerLog { get => DataManagementViewAllowed && DataManagementArchiveConfigSchedulerLogViewAllowed; }

        public bool DataManagementArchiveConfigSchedulerLogViewAllowed { get; set; }
		public bool DataManagementArchiveConfigSchedulerLogEditAllowed { get; set; }
		public bool DataManagementArchiveConfigSchedulerLogCreateAllowed { get; set; }
        #endregion


        #region Setting privillege
        [JsonIgnore]
        public bool SettingsAllowed { get => SettingsViewAllowed || SettingsEditAllowed || SettingsCreateAllowed; }
        //public bool SettingsAllowed { get; set; }
        public bool SettingsViewAllowed { get; set; }
		public bool SettingsEditAllowed { get; set; }
		public bool SettingsCreateAllowed { get; set; }

        [JsonIgnore]
        public bool SettingsIncrementalNumberAllowed { get => SettingsIncrementalNumberViewAllowed || SettingsIncrementalNumberEditAllowed || SettingsIncrementalNumberCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedSettingsIncrementalNumber { get => SettingsCreateAllowed && SettingsIncrementalNumberCreateAllowed; }
        public bool CanEditSettingsIncrementalNumber<P>(P item) where P : IPersistent => (SettingsEditAllowed || SettingsCreateAllowed) && SettingsIncrementalNumberEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewSettingsIncrementalNumber { get => SettingsViewAllowed && SettingsIncrementalNumberViewAllowed; }

        public bool SettingsIncrementalNumberViewAllowed { get; set; }
		public bool SettingsIncrementalNumberEditAllowed { get; set; }
		public bool SettingsIncrementalNumberCreateAllowed { get; set; }

        [JsonIgnore]
        public bool SettingsGroupAllowed { get => SettingsGroupViewAllowed || SettingsGroupEditAllowed || SettingsGroupCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedSettingsGroup { get => SettingsCreateAllowed && SettingsGroupCreateAllowed; }
        public bool CanEditSettingsGroup<P>(P item) where P : IPersistent => (SettingsEditAllowed || SettingsCreateAllowed) && SettingsGroupEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewSettingsGroup { get => SettingsViewAllowed && SettingsGroupViewAllowed; }

        public bool SettingsGroupViewAllowed { get; set; }
		public bool SettingsGroupEditAllowed { get; set; }
		public bool SettingsGroupCreateAllowed { get; set; }

        #endregion

        #region Administration Privillege
        [JsonIgnore]
        public bool AdministrationAllowed { get => AdministrationViewAllowed || AdministrationEditAllowed || AdministrationCreateAllowed; }
        //public bool AdministrationAllowed { get; set; }
        public bool AdministrationViewAllowed { get; set; }
		public bool AdministrationEditAllowed { get; set; }
		public bool AdministrationCreateAllowed { get; set; }

        [JsonIgnore]
        public bool AdministrationClientAllowed { get => AdministrationClientViewAllowed || AdministrationClientEditAllowed || AdministrationClientCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedAdministrationClient { get => AdministrationCreateAllowed && AdministrationClientCreateAllowed; }
        public bool CanEditAdministrationClient<P>(P item) where P : IPersistent => (AdministrationEditAllowed || AdministrationCreateAllowed) && AdministrationClientEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewAdministrationClient { get => AdministrationViewAllowed && AdministrationClientViewAllowed; }

        public bool AdministrationClientViewAllowed { get; set; }
		public bool AdministrationClientEditAllowed { get; set; }
		public bool AdministrationClientCreateAllowed { get; set; }

        [JsonIgnore]
        public bool AdministrationProfileAllowed { get => AdministrationProfileViewAllowed || AdministrationProfileEditAllowed || AdministrationProfileCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedAdministrationProfile { get => AdministrationCreateAllowed && AdministrationProfileCreateAllowed; }
        public bool CanEditAdministrationProfile<P>(P item) where P : IPersistent => (AdministrationEditAllowed || AdministrationCreateAllowed) && AdministrationProfileEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewAdministrationProfile { get => AdministrationViewAllowed && AdministrationProfileViewAllowed; }

        public bool AdministrationProfileViewAllowed { get; set; }
		public bool AdministrationProfileEditAllowed { get; set; }
		public bool AdministrationProfileCreateAllowed { get; set; }

        [JsonIgnore]
        public bool AdministrationUserAllowed { get => AdministrationUserViewAllowed || AdministrationUserEditAllowed || AdministrationUserCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedAdministrationUser { get => AdministrationCreateAllowed && AdministrationUserCreateAllowed; }
        public bool CanEditAdministrationUser<P>(P item) where P : IPersistent => (AdministrationEditAllowed || AdministrationCreateAllowed) && AdministrationUserEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewAdministrationUser { get => AdministrationViewAllowed && AdministrationUserViewAllowed; }

        public bool AdministrationUserViewAllowed { get; set; }
		public bool AdministrationUserEditAllowed { get; set; }
		public bool AdministrationUserCreateAllowed { get; set; }

        [JsonIgnore]
        public bool AdministrationConnectedUserAllowed { get => AdministrationConnectedUserViewAllowed || AdministrationConnectedUserEditAllowed || AdministrationConnectedUserCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedAdministrationConnectedUser { get => AdministrationCreateAllowed && AdministrationConnectedUserCreateAllowed; }
        public bool CanEditAdministrationConnectedUser<P>(P item) where P : IPersistent => (AdministrationEditAllowed || AdministrationCreateAllowed) && AdministrationConnectedUserEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewAdministrationConnectedUser { get => AdministrationViewAllowed && AdministrationConnectedUserViewAllowed; }

        public bool AdministrationConnectedUserViewAllowed { get; set; }
		public bool AdministrationConnectedUserEditAllowed { get; set; }
		public bool AdministrationConnectedUserCreateAllowed { get; set; }

        [JsonIgnore]
        public bool AdministrationSchedulerAllowed { get => AdministrationSchedulerViewAllowed || AdministrationSchedulerEditAllowed || AdministrationSchedulerCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedAdministrationScheduler { get => AdministrationCreateAllowed && AdministrationSchedulerCreateAllowed; }
        public bool CanEditAdministrationScheduler<P>(P item) where P : IPersistent => (AdministrationEditAllowed || AdministrationCreateAllowed) && AdministrationSchedulerEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewAdministrationScheduler { get => AdministrationViewAllowed && AdministrationSchedulerViewAllowed; }

        public bool AdministrationSchedulerViewAllowed { get; set; }
		public bool AdministrationSchedulerEditAllowed { get; set; }
		public bool AdministrationSchedulerCreateAllowed { get; set; }

        #endregion

        #region Billing Privillege
        [JsonIgnore]
        public bool BillingAllowed { get => BillingViewAllowed || BillingEditAllowed || BillingCreateAllowed; }
        //public bool BillingAllowed { get; set; }
        public bool BillingViewAllowed { get; set; }
		public bool BillingEditAllowed { get; set; }
		public bool BillingCreateAllowed { get; set; }

        [JsonIgnore]
        public bool BillingEventAllowed { get => BillingEventViewAllowed || BillingEventEditAllowed || BillingEventCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedBillingEvent { get => BillingCreateAllowed && BillingEventCreateAllowed; }
        public bool CanEditBillingEvent<P>(P item) where P : IPersistent => (BillingEditAllowed || BillingCreateAllowed) && BillingEventEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewBillingEvent { get => BillingViewAllowed && BillingEventViewAllowed; }

        public bool BillingEventViewAllowed { get; set; }
		public bool BillingEventEditAllowed { get; set; }
		public bool BillingEventCreateAllowed { get; set; }

        [JsonIgnore]
        public bool BillingEventRepositoryAllowed { get => BillingEventRepositoryViewAllowed || BillingEventRepositoryEditAllowed || BillingEventRepositoryCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedBillingEventRepository { get => BillingCreateAllowed && BillingEventRepositoryCreateAllowed; }
        public bool CanEditBillingEventRepository<P>(P item) where P : IPersistent => (BillingEditAllowed || BillingCreateAllowed) && BillingEventRepositoryEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewBillingEventRepository { get => BillingViewAllowed && BillingEventRepositoryViewAllowed; }

        public bool BillingEventRepositoryViewAllowed { get; set; }
		public bool BillingEventRepositoryEditAllowed { get; set; }
		public bool BillingEventRepositoryCreateAllowed { get; set; }


         [JsonIgnore]
        public bool ClientRepositoryAllowed { get => ClientRepositoryViewAllowed || ClientRepositoryEditAllowed || ClientRepositoryCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedClientRepository { get => BillingCreateAllowed && ClientRepositoryCreateAllowed; }
        public bool CanEditClientRepository<P>(P item) where P : IPersistent => (BillingEditAllowed || BillingCreateAllowed) && ClientRepositoryEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewClientRepository { get => BillingViewAllowed && ClientRepositoryViewAllowed; }

        public bool ClientRepositoryViewAllowed { get; set; }
		public bool ClientRepositoryEditAllowed { get; set; }
		public bool ClientRepositoryCreateAllowed { get; set; }



        [JsonIgnore]
        public bool BillingJoinAllowed { get => BillingJoinViewAllowed || BillingJoinEditAllowed || BillingJoinCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedBillingJoinRepository { get => BillingCreateAllowed && BillingJoinCreateAllowed; }
        public bool CanEditBillingJoin<P>(P item) where P : IPersistent => (BillingEditAllowed || BillingCreateAllowed) && BillingJoinEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewBillingJoin { get => BillingViewAllowed && BillingJoinViewAllowed; }

        public bool BillingJoinViewAllowed { get; set; }
		public bool BillingJoinEditAllowed { get; set; }
		public bool BillingJoinCreateAllowed { get; set; }



        [JsonIgnore]
        public bool BillingModelAllowed { get => BillingModelViewAllowed || BillingModelEditAllowed || BillingModelCreateAllowed; }
        
        [JsonIgnore]
        public bool CanCreatedBillingModel { get => BillingCreateAllowed && BillingModelCreateAllowed; }
        public bool CanEditBillingModel<P>(P item) where P : IPersistent => (BillingEditAllowed || BillingCreateAllowed) && BillingModelEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewBillingModel { get => BillingViewAllowed && BillingModelViewAllowed; }

        public bool BillingModelViewAllowed { get; set; }
		public bool BillingModelEditAllowed { get; set; }
		public bool BillingModelCreateAllowed { get; set; }


         [JsonIgnore]
        public bool BillingTemplateAllowed { get => BillingTemplateViewAllowed || BillingTemplateEditAllowed || BillingTemplateCreateAllowed; }
        
        [JsonIgnore]
        public bool CanCreatedBillingTemplate { get => BillingCreateAllowed && BillingTemplateCreateAllowed; }
        public bool CanEditBillingTemplate<P>(P item) where P : IPersistent => (BillingEditAllowed || BillingCreateAllowed) && BillingTemplateEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewBillingTemplate { get => BillingViewAllowed && BillingTemplateViewAllowed; }

        public bool BillingTemplateViewAllowed { get; set; }
		public bool BillingTemplateEditAllowed { get; set; }
		public bool BillingTemplateCreateAllowed { get; set; }

        [JsonIgnore]
        public bool BillingModelSchedulerAllowed { get => BillingModelSchedulerViewAllowed || BillingModelSchedulerEditAllowed || BillingModelSchedulerCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedBillingModelScheduler { get => BillingCreateAllowed && BillingModelSchedulerCreateAllowed; }
        public bool CanEditBillingModelScheduler<P>(P item) where P : IPersistent => (BillingEditAllowed || BillingCreateAllowed) && BillingModelSchedulerEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewBillingModelScheduler { get => BillingViewAllowed && BillingModelSchedulerViewAllowed; }

        public bool BillingModelSchedulerViewAllowed { get; set; }
		public bool BillingModelSchedulerEditAllowed { get; set; }
		public bool BillingModelSchedulerCreateAllowed { get; set; }

        [JsonIgnore]
        public bool BillingRunAllowed { get => BillingRunViewAllowed || BillingRunEditAllowed || BillingRunCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedBillingRun { get => BillingCreateAllowed && BillingRunCreateAllowed; }
        public bool CanEditBillingRun<P>(P item) where P : IPersistent => (BillingEditAllowed || BillingCreateAllowed) && BillingRunEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewBillingRun { get => BillingViewAllowed && BillingRunViewAllowed; }

        public bool BillingRunViewAllowed { get; set; }
		public bool BillingRunEditAllowed { get; set; }
		public bool BillingRunCreateAllowed { get; set; }

        [JsonIgnore]
        public bool BillingRunOutcomeAllowed { get => BillingRunOutcomeViewAllowed || BillingRunOutcomeEditAllowed || BillingRunOutcomeCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedBillingRunOutcome { get => BillingCreateAllowed && BillingRunOutcomeCreateAllowed; }
        public bool CanEditBillingRunOutcome<P>(P item) where P : IPersistent => (BillingEditAllowed || BillingCreateAllowed) && BillingRunOutcomeEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewBillingRunOutcome { get => BillingViewAllowed && BillingRunOutcomeViewAllowed; }

        public bool BillingRunOutcomeViewAllowed { get; set; }
		public bool BillingRunOutcomeEditAllowed { get; set; }
		public bool BillingRunOutcomeCreateAllowed { get; set; }

        [JsonIgnore]
        public bool BillingRunInvoiceAllowed { get => BillingRunInvoiceViewAllowed || BillingRunInvoiceEditAllowed || BillingRunInvoiceCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedBillingRunInvoice { get => BillingCreateAllowed && BillingRunInvoiceCreateAllowed; }
        public bool CanEditBillingRunInvoice<P>(P item) where P : IPersistent => (BillingEditAllowed || BillingCreateAllowed) && BillingRunInvoiceEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewBillingRunInvoice { get => BillingViewAllowed && BillingRunInvoiceViewAllowed; }

        public bool BillingRunInvoiceViewAllowed { get; set; }
		public bool BillingRunInvoiceEditAllowed { get; set; }
		public bool BillingRunInvoiceCreateAllowed { get; set; }

        [JsonIgnore]
        public bool BillingRunCreditNoteAllowed { get => BillingRunCreditNoteViewAllowed || BillingRunCreditNoteEditAllowed || BillingRunCreditNoteCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedBillingRunCreditNote { get => BillingCreateAllowed && BillingRunCreditNoteCreateAllowed; }
        public bool CanEditBillingRunCreditNote<P>(P item) where P : IPersistent => (BillingEditAllowed || BillingCreateAllowed) && BillingRunCreditNoteEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewBillingRunCreditNote { get => BillingViewAllowed && BillingRunCreditNoteViewAllowed; }

        public bool BillingRunCreditNoteViewAllowed { get; set; }
		public bool BillingRunCreditNoteEditAllowed { get; set; }
		public bool BillingRunCreditNoteCreateAllowed { get; set; }

        [JsonIgnore]
        public bool BillingRunStatusAllowed { get => BillingRunStatusViewAllowed || BillingRunStatusEditAllowed || BillingRunStatusCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedBillingRunStatus { get => BillingCreateAllowed && BillingRunStatusCreateAllowed; }
        public bool CanEditBillingRunStatus<P>(P item) where P : IPersistent => (BillingEditAllowed || BillingCreateAllowed) && BillingRunStatusEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewBillingRunStatus { get => BillingViewAllowed && BillingRunStatusViewAllowed; }

        public bool BillingRunStatusViewAllowed { get; set; }
		public bool BillingRunStatusEditAllowed { get; set; }
		public bool BillingRunStatusCreateAllowed { get; set; }
        #endregion


        #region Accounting Privillege
        [JsonIgnore]
        public bool AccountingAllowed { get => AccountingViewAllowed || AccountingEditAllowed || AccountingCreateAllowed; }
        //public bool AccountingAllowed { get; set; }
        public bool AccountingViewAllowed { get; set; }
		public bool AccountingEditAllowed { get; set; }
		public bool AccountingCreateAllowed { get; set; }

        [JsonIgnore]
        public bool AccountingPosingAllowed { get => AccountingPosingViewAllowed || AccountingPosingEditAllowed || AccountingPosingCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedAccountingPosing { get => AccountingCreateAllowed && AccountingPosingCreateAllowed; }
        public bool CanEditAccountingPosing<P>(P item) where P : IPersistent => (AccountingEditAllowed || AccountingCreateAllowed) && AccountingPosingEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewAccountingPosing { get => AccountingViewAllowed && AccountingPosingViewAllowed; }

        public bool AccountingPosingViewAllowed { get; set; }
		public bool AccountingPosingEditAllowed { get; set; }
		public bool AccountingPosingCreateAllowed { get; set; }

        [JsonIgnore]
        public bool AccountingPosingEntryRepositoryAllowed { get => AccountingPosingEntryRepositoryViewAllowed || AccountingPosingEntryRepositoryEditAllowed || AccountingPosingEntryRepositoryCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedAccountingPosingEntryRepository { get => AccountingCreateAllowed && AccountingPosingEntryRepositoryCreateAllowed; }
        public bool CanEditAccountingPosingEntryRepository<P>(P item) where P : IPersistent => (AccountingEditAllowed || AccountingCreateAllowed) && AccountingPosingEntryRepositoryEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewAccountingPosingEntryRepository { get => AccountingViewAllowed && AccountingPosingEntryRepositoryViewAllowed; }

        public bool AccountingPosingEntryRepositoryViewAllowed { get; set; }
		public bool AccountingPosingEntryRepositoryEditAllowed { get; set; }
		public bool AccountingPosingEntryRepositoryCreateAllowed { get; set; }

        [JsonIgnore]
        public bool AccountingPosingEditionAllowed { get => AccountingPosingEditionViewAllowed || AccountingPosingEditionEditAllowed || AccountingPosingEditionCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedAccountingPosingEdition { get => AccountingCreateAllowed && AccountingPosingEditionCreateAllowed; }
        public bool CanEditAccountingPosingEdition<P>(P item) where P : IPersistent => (AccountingEditAllowed || AccountingCreateAllowed) && AccountingPosingEditionEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewAccountingPosingEdition { get => AccountingViewAllowed && AccountingPosingEditionViewAllowed; }

        public bool AccountingPosingEditionViewAllowed { get; set; }
		public bool AccountingPosingEditionEditAllowed { get; set; }
		public bool AccountingPosingEditionCreateAllowed { get; set; }

        [JsonIgnore]
        public bool AccountingBookingAllowed { get => AccountingBookingViewAllowed || AccountingBookingEditAllowed || AccountingBookingCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedAccountingBooking { get => AccountingCreateAllowed && AccountingBookingCreateAllowed; }
        public bool CanEditAccountingBooking<P>(P item) where P : IPersistent => (AccountingEditAllowed || AccountingCreateAllowed) && AccountingBookingEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewAccountingBooking { get => AccountingViewAllowed && AccountingBookingViewAllowed; }

        public bool AccountingBookingViewAllowed { get; set; }
		public bool AccountingBookingEditAllowed { get; set; }
		public bool AccountingBookingCreateAllowed { get; set; }

        [JsonIgnore]
        public bool AccountingBookingModelAllowed { get => AccountingBookingModelViewAllowed || AccountingBookingModelEditAllowed || AccountingBookingModelCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedAccountingBookingModel { get => AccountingCreateAllowed && AccountingBookingModelCreateAllowed; }
        public bool CanEditAccountingBookingModel<P>(P item) where P : IPersistent => (AccountingEditAllowed || AccountingCreateAllowed) && AccountingBookingModelEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewAccountingBookingModel { get => AccountingViewAllowed && AccountingBookingModelViewAllowed; }

        public bool AccountingBookingModelViewAllowed { get; set; }
		public bool AccountingBookingModelEditAllowed { get; set; }
		public bool AccountingBookingModelCreateAllowed { get; set; }

        [JsonIgnore]
        public bool BillingBookingModelSchedulerAllowed { get => BillingBookingModelSchedulerViewAllowed || BillingBookingModelSchedulerEditAllowed || BillingBookingModelSchedulerCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedBillingBookingModelScheduler { get => BillingCreateAllowed && BillingBookingModelSchedulerCreateAllowed; }
        public bool CanEditBillingBookingModelScheduler<P>(P item) where P : IPersistent => (BillingEditAllowed|| BillingCreateAllowed) && BillingBookingModelSchedulerEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewBillingBookingModelScheduler { get => BillingViewAllowed && BillingBookingModelSchedulerViewAllowed; }

        public bool BillingBookingModelSchedulerViewAllowed { get; set; }
		public bool BillingBookingModelSchedulerEditAllowed { get; set; }
		public bool BillingBookingModelSchedulerCreateAllowed { get; set; }

        [JsonIgnore]
        public bool AccountingBookingModelSchedulerLogAllowed { get => AccountingBookingModelSchedulerLogViewAllowed || AccountingBookingModelSchedulerLogEditAllowed || AccountingBookingModelSchedulerLogCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedBookingModelSchedulerLog { get => AccountingCreateAllowed && AccountingBookingModelSchedulerLogCreateAllowed; }
        public bool CanEditBookingModelSchedulerLog<P>(P item) where P : IPersistent => (AccountingEditAllowed || AccountingCreateAllowed) && AccountingBookingModelSchedulerLogEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewBookingModelSchedulerLog { get => AccountingViewAllowed && AccountingBookingModelSchedulerLogViewAllowed; }

        public bool AccountingBookingModelSchedulerLogViewAllowed { get; set; }
		public bool AccountingBookingModelSchedulerLogEditAllowed { get; set; }
		public bool AccountingBookingModelSchedulerLogCreateAllowed { get; set; }
        #endregion

        #region Messenger Privillege
        [JsonIgnore]
        public bool MessengerAllowed { get => MessengerViewAllowed || MessengerEditAllowed || MessengerCreateAllowed; }
        //public bool MessengerAllowed { get; set; }
        public bool MessengerViewAllowed { get; set; }
		public bool MessengerEditAllowed { get; set; }
		public bool MessengerCreateAllowed { get; set; }

        [JsonIgnore]
        public bool MessengerEmailAllowed { get => MessengerEmailViewAllowed || MessengerEmailEditAllowed || MessengerEmailCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedMessengerEmail { get => MessengerCreateAllowed && MessengerEmailCreateAllowed; }
        public bool CanEditMessengerEmail<P>(P item) where P : IPersistent => (MessengerEditAllowed || MessengerCreateAllowed) && MessengerEmailEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewMessengerEmail { get => MessengerViewAllowed && MessengerEmailViewAllowed; }

        public bool MessengerEmailViewAllowed { get; set; }
		public bool MessengerEmailEditAllowed { get; set; }
		public bool MessengerEmailCreateAllowed { get; set; }

        [JsonIgnore]
        public bool MessengerSmsAllowed { get => MessengerSmsViewAllowed || MessengerSmsEditAllowed || MessengerSmsCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedMessengerSms { get => MessengerCreateAllowed && MessengerSmsCreateAllowed; }
        public bool CanEditMessengerSms<P>(P item) where P : IPersistent => (MessengerEditAllowed || MessengerCreateAllowed) && MessengerSmsEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewMessengerSms { get => MessengerViewAllowed && MessengerSmsViewAllowed; }

        public bool MessengerSmsViewAllowed { get; set; }
		public bool MessengerSmsEditAllowed { get; set; }
		public bool MessengerSmsCreateAllowed { get; set; }

        [JsonIgnore]
        public bool MessengerLogAllowed { get => MessengerLogViewAllowed || MessengerLogEditAllowed || MessengerLogCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedMessengerLog { get => MessengerCreateAllowed && MessengerLogCreateAllowed; }
        public bool CanEditMessengerLog<P>(P item) where P : IPersistent => (MessengerEditAllowed || MessengerCreateAllowed) && MessengerLogEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewMessengerLog { get => MessengerViewAllowed && MessengerLogViewAllowed; }

        public bool MessengerLogViewAllowed { get; set; }
		public bool MessengerLogEditAllowed { get; set; }
		public bool MessengerLogCreateAllowed { get; set; }

        #endregion

        #region Reporting privillege
        [JsonIgnore]
        public bool ReportingJoinGridAllowed { get => ReportingJoinGridViewAllowed || ReportingJoinGridEditAllowed || ReportingJoinGridCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedReportingJoinGrid { get => ReportingCreateAllowed && ReportingJoinGridCreateAllowed; }
        public bool CanEditReportingJoinGrid<P>(P item) where P : IPersistent => (ReportingEditAllowed || ReportingCreateAllowed) && ReportingJoinGridEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewReportingJoinGrid { get => ReportingAllowed && ReportingJoinGridViewAllowed; }
        public bool ReportingJoinGridViewAllowed { get; set; }
		public bool ReportingJoinGridEditAllowed { get; set; }
		public bool ReportingJoinGridCreateAllowed { get; set; }


        [JsonIgnore]
        public bool ReportingJoinGridSchedulerAllowed { get => ReportingJoinGridSchedulerViewAllowed || ReportingJoinGridSchedulerEditAllowed || ReportingJoinGridSchedulerCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedReportingJoinGridScheduler { get => ReportingCreateAllowed && ReportingJoinGridSchedulerCreateAllowed; }
        public bool CanEditReportingJoinGridScheduler<P>(P item) where P : IPersistent => (ReportingEditAllowed || ReportingCreateAllowed) && ReportingJoinGridSchedulerEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewReportingJoinGridScheduler { get => ReportingAllowed && ReportingJoinGridSchedulerViewAllowed; }


        public bool ReportingJoinGridSchedulerViewAllowed { get; set; }
        public bool ReportingJoinGridSchedulerEditAllowed { get; set; }
        public bool ReportingJoinGridSchedulerCreateAllowed { get; set; }

        [JsonIgnore]
        public bool ReportingJoinGridSchedulerLogAllowed { get => ReportingJoinGridSchedulerLogViewAllowed || ReportingJoinGridSchedulerLogEditAllowed || ReportingJoinGridSchedulerLogCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedReportingJoinGridSchedulerLog { get => ReportingCreateAllowed && ReportingJoinGridSchedulerLogCreateAllowed; }
        public bool CanEditReportingJoinGridSchedulerLog<P>(P item) where P : IPersistent => (ReportingEditAllowed || ReportingCreateAllowed) && ReportingJoinGridSchedulerLogEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewReportingJoinGridSchedulerLog { get => ReportingAllowed && ReportingJoinGridSchedulerLogViewAllowed; }


        public bool ReportingJoinGridSchedulerLogViewAllowed { get; set; }
        public bool ReportingJoinGridSchedulerLogEditAllowed { get; set; }
        public bool ReportingJoinGridSchedulerLogCreateAllowed { get; set; }

        #endregion

        #region Scheduler privillege
        public bool SchedulerPlannerViewAllowed { get; set; }
        public bool SchedulerPlannerEditAllowed { get; set; }
        public bool SchedulerPlannerCreateAllowed { get; set; }
        [JsonIgnore]
        public bool SchedulerPlannerAllowed { get => SchedulerPlannerViewAllowed || SchedulerPlannerEditAllowed || SchedulerPlannerCreateAllowed; }

        public bool SchedulerPlannerSchedulerViewAllowed { get; set; }
        public bool SchedulerPlannerSchedulerEditAllowed { get; set; }
        public bool SchedulerPlannerSchedulerCreateAllowed { get; set; }

        [JsonIgnore]
        public bool SchedulerPlannerSchedulerAllowed { get => SchedulerPlannerSchedulerViewAllowed || SchedulerPlannerSchedulerEditAllowed || SchedulerPlannerSchedulerCreateAllowed; }
        [JsonIgnore]
        public bool CanCreatedSchedulerPlannerScheduler { get => SchedulerPlannerAllowed && SchedulerPlannerSchedulerCreateAllowed; }
        public bool CanEditSchedulerPlannerScheduler<P>(P item) where P : IPersistent => (SchedulerPlannerEditAllowed || SchedulerPlannerCreateAllowed) && SchedulerPlannerSchedulerEditAllowed && item.Id.HasValue;
        [JsonIgnore]
        public bool CanViewSchedulerPlannerScheduler { get => SchedulerPlannerAllowed && SchedulerPlannerSchedulerViewAllowed; }


        public bool SchedulerPlannerSchedulerLogViewAllowed { get; set; }
        public bool SchedulerPlannerSchedulerLogEditAllowed { get; set; }

        #endregion
    }
}

using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Functionalities;
using Bcephal.Models.Grids;
using Bcephal.Models.Grids.Filters;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Dashboard
{
    public partial class DashboardConfigDesign_ : ComponentBase
    {
        [CascadingParameter]
        public Error Error { get; set; }
        [Inject]
        public AppState AppState { get; set; }

        [Inject]
        ReportGridService ReportGridService { get; set; }
        [Inject]
        DashboardReportService DashboardReportService { get; set; }

        [Parameter]
        public EditorData<Models.Dashboards.Dashboard> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Models.Dashboards.Dashboard>> EditorDataChanged { get; set; }
        public NodeElement SelectedItem { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;
        public List<NodeElement> Data { get; set; } = new();

        public EditorData<Models.Dashboards.Dashboard> EditorDataBinding
        {
            get
            {
                return EditorData;
            }
            set
            {
                EditorData = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }


        public List<Models.Dashboards.DashboardItemType> DashboardItemTypes = new List<Models.Dashboards.DashboardItemType>()
        {
            Models.Dashboards.DashboardItemType.CHART,
            Models.Dashboards.DashboardItemType.DRILL_DOWN_GRID,
            Models.Dashboards.DashboardItemType.PIVOT_TABLE,
            Models.Dashboards.DashboardItemType.BROWSER_REPORT_PUBLICATION,
            Models.Dashboards.DashboardItemType.REPORT_GRID
        };

        public async Task<ObservableCollection<GrilleBrowserData>> GetReportGrid()
        {
            BrowserDataFilter filter = new();
            BrowserDataPage<GrilleBrowserData> page = await ReportGridService.Search<GrilleBrowserData>(filter);
            return page.Items;
        }
        
        public async Task<ObservableCollection<BrowserData>> GetReportCharts()
        {
            BrowserDataFilter filter = new();
            filter.ReportType = Models.Dashboards.DashboardItemType.CHART.Code;
            filter.published = true;
            BrowserDataPage<BrowserData> page = await DashboardReportService.Search<BrowserData>(filter);
            return page.Items;
        }

        public async Task<ObservableCollection<BrowserData>> GetReportPivotTables()
        {
            BrowserDataFilter filter = new();
            filter.ReportType = Models.Dashboards.DashboardItemType.PIVOT_TABLE.Code;
            filter.published = true;
            BrowserDataPage<BrowserData> page = await DashboardReportService.Search<BrowserData>(filter);
            return page.Items;
        }

        protected async override Task OnInitializedAsync()
        {
            ObservableCollection<GrilleBrowserData> reportGrids;
            ObservableCollection<BrowserData> reportCharts;
            ObservableCollection<BrowserData> reportpivottables;
            try
            {
                 reportGrids = await GetReportGrid();
            }
            catch (Exception ex)
            {
                reportGrids = new();                
                Error.ProcessError(ex);
            }
            try
            {
                reportCharts = await GetReportCharts();
            }
            catch (Exception ex)
            {
                reportCharts = new();
                Error.ProcessError(ex);
            }
            try
            {
                reportpivottables = await GetReportPivotTables();
            }
            catch (Exception ex)
            {
                reportpivottables = new();
                Error.ProcessError(ex);
            }
            if (Data.Count > 0)
            {
                Data.Clear();
            }
            int i = 0;
            foreach (Models.Dashboards.DashboardItemType ite in DashboardItemTypes)
            {
                NodeElement elt = new() { Name = ite.Label, Id = i , ItemType = ite.Code };
                List<NodeElement> sublist = new();

                if (ite == Models.Dashboards.DashboardItemType.REPORT_GRID)
                {
                    if (reportGrids.Count > 0)
                    {
                        sublist.Clear();
                        foreach (GrilleBrowserData gd in reportGrids)
                        {
                            sublist.Add(new NodeElement() { Name = gd.Name, Id = gd.Id, IsLeaf = true, ItemType = ite.Code });
                        }
                        elt.Children.AddRange(sublist);
                    }
                }else if (ite == Models.Dashboards.DashboardItemType.CHART)
                {
                    if (reportCharts.Count > 0)
                    {
                        sublist.Clear();
                        foreach (BrowserData gd in reportCharts)
                        {
                            sublist.Add(new NodeElement() { Name = gd.Name, Id = gd.Id, IsLeaf = true, ItemType = ite.Code });
                        }
                        elt.Children.AddRange(sublist);
                    }

                }
                else if(ite == Models.Dashboards.DashboardItemType.PIVOT_TABLE)
                {
                    if (reportpivottables.Count > 0)
                    {
                        sublist.Clear();
                        foreach (BrowserData gd in reportpivottables)
                        {
                            sublist.Add(new NodeElement() { Name = gd.Name, Id = gd.Id, IsLeaf = true, ItemType = ite.Code });
                        }
                        elt.Children.AddRange(sublist);
                    }

                }
                Data.Add(elt);
                i++;
            }           
            Data.Add(GETStandardComponentNode());
           
            await base.OnInitializedAsync();
        }

        private NodeElement GETStandardComponentNode()
        {
            NodeElement Node = new NodeElement() { Name = AppState["List.screen"] };
            NodeElement Project = new NodeElement() { Name = AppState["project"] };
            NodeElement Sourcing = new NodeElement() { Name = AppState["sourcing"] };
            NodeElement TransformationData = new NodeElement() { Name = AppState["TransformationData"] };
            NodeElement Reporting = new NodeElement() { Name = AppState["reporting"] };
            NodeElement Dashboarding = new NodeElement() { Name = AppState["dashboarding"] };
            NodeElement Reconciliation = new NodeElement() { Name = AppState["reconciliation"] };
            NodeElement Billing = new NodeElement() { Name = AppState["billing"] };
            NodeElement Accounting = new NodeElement() { Name = AppState["Accounting"] };
            NodeElement DataManager = new NodeElement() { Name = AppState["data.management"] };
            NodeElement Messenger = new NodeElement() { Name = AppState["data.messenger"] };
            NodeElement Administration = new NodeElement() { Name = AppState["administration"] };

            Project.Children.Add(new NodeElement() { Name = AppState["ProjectsList"], ItemType = Models.Dashboards.DashboardItemType.PROJECT_BROWSER.Code });
            Project.Children.Add(new NodeElement() { Name = AppState["Project.List.Backup"], ItemType = Models.Dashboards.DashboardItemType.PROJECT_BROWSER.Code });
            Node.Children.Add(Project);
           
            //Sourcing.Children.Add(new NodeElement() { Name = AppState["List.Input.Tables"] });
            Sourcing.Children.Add(new NodeElement() { Name = AppState["List.Input.Grid"], ItemType = Models.Dashboards.DashboardItemType.INPUT_GRID_BROWSER.Code });
            Sourcing.Children.Add(new NodeElement() { Name = AppState["List.Files.Loader"], ItemType = Models.Dashboards.DashboardItemType.LOADER_BROWSER.Code });
            Sourcing.Children.Add(new NodeElement() { Name = AppState["List.Spot"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_SPOT.Code });
            Sourcing.Children.Add(new NodeElement() { Name = AppState["List.Dynamic.Forms"], ItemType = Models.Dashboards.DashboardItemType.FORM_BROWSER.Code });
            Node.Children.Add(Sourcing);

           // TransformationData.Children.Add(new NodeElement() { Name = AppState["TransformationData.ListTransformationTrees"], ItemType = Models.Dashboards.DashboardItemType.TRANSFORMATION_TREE_BROWSER.Code });
          //  TransformationData.Children.Add(new NodeElement() { Name = AppState["TransformationData.ScheduledTransformationTrees"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_SCHEDULED_TRANSFORMATION_TREE.Code });
            TransformationData.Children.Add(new NodeElement() { Name = AppState["List.trans.rout"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_TRANSFORMATION_ROUTINE.Code });
          //  TransformationData.Children.Add(new NodeElement() { Name = AppState["list.scheduled.tranformation.routine"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_SCHEDULED_ROUTINE.Code });
            Node.Children.Add(TransformationData);

            Reporting.Children.Add(new NodeElement() { Name = AppState["List.Reports"], ItemType = Models.Dashboards.DashboardItemType.REPORT_GRID_BROWSER.Code });
            Reporting.Children.Add(new NodeElement() { Name = AppState["List.Pivot.Table"], ItemType = Models.Dashboards.DashboardItemType.PIVOT_TABLE_BROWSER.Code });
            Reporting.Children.Add(new NodeElement() { Name = AppState["List.Report.Publication"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_REPORT_PUBLICATION.Code });
            Reporting.Children.Add(new NodeElement() { Name = AppState["List.Chart"], ItemType = Models.Dashboards.DashboardItemType.CHART_BROWSER.Code });
            Reporting.Children.Add(new NodeElement() { Name = AppState["List.Joins"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_REPORTING_JOIN.Code });
            Reporting.Children.Add(new NodeElement() { Name = AppState["List.Join.Log"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_REPORTING_JOIN_LOG.Code });
            Node.Children.Add(Reporting);

            Dashboarding.Children.Add(new NodeElement() { Name = AppState["List.Dashboard"], ItemType = Models.Dashboards.DashboardItemType.DASHBOARD_BROWSER.Code});
            Dashboarding.Children.Add(new NodeElement() { Name = AppState["List.Alarms"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_REPORT_ALARM.Code });
            Dashboarding.Children.Add(new NodeElement() { Name = AppState["Scheduled.Alarms"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_REPORT_SCHEDULED_ALARM.Code });
            Node.Children.Add(Dashboarding);

            Reconciliation.Children.Add(new NodeElement() { Name = AppState["List.Automatic.Reconciliations"], ItemType = Models.Dashboards.DashboardItemType.AUTO_RECO_BROWSER.Code });
            Reconciliation.Children.Add(new NodeElement() { Name = AppState["List.Reconciliation.Filter"], ItemType = Models.Dashboards.DashboardItemType.MANUAL_RECO_BROWSER.Code });
            Node.Children.Add(Reconciliation);

            Billing.Children.Add(new NodeElement() { Name = AppState["List.model"], ItemType = Models.Dashboards.DashboardItemType.BILLING_MODEL_BROWSER.Code });
            Billing.Children.Add(new NodeElement() { Name = AppState["billing.model.scheduler"], ItemType = Models.Dashboards.DashboardItemType.SCHEDULED_MODEL_BROWSER.Code });
            Billing.Children.Add(new NodeElement() { Name = AppState["List.template"], ItemType = Models.Dashboards.DashboardItemType.BILLING_TEMPLATE_LIST.Code });
            Billing.Children.Add(new NodeElement() { Name = AppState["billing.run.invoice"], ItemType = Models.Dashboards.DashboardItemType.LIST_INVOICE.Code });
            Billing.Children.Add(new NodeElement() { Name = AppState["billing.run.credit.note"], ItemType = Models.Dashboards.DashboardItemType.LIST_CREDIT_NOTE.Code });
            Node.Children.Add(Billing);

            Accounting.Children.Add(new NodeElement() { Name = AppState["Posting.List"], ItemType = Models.Dashboards.DashboardItemType.POSTING_LIST.Code });
            Accounting.Children.Add(new NodeElement() { Name = AppState["List.Booking.Model"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_BOOKING_MODEL.Code });
            Accounting.Children.Add(new NodeElement() { Name = AppState["Scheduled.booking"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_SCHEDULED_BOOKING.Code });
            Accounting.Children.Add(new NodeElement() { Name = AppState["List.Booking.Logs"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_BOOKING_MODEL_LOG.Code });
            Node.Children.Add(Accounting);

            DataManager.Children.Add(new NodeElement() { Name = AppState["List.Archives"], ItemType = Models.Dashboards.DashboardItemType.ARCHIVE_BROWSER.Code });
            DataManager.Children.Add(new NodeElement() { Name = AppState["List.Archive.Logs"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_ARCHIVE_LOG.Code });
            DataManager.Children.Add(new NodeElement() { Name = AppState["List.configurations"], ItemType = Models.Dashboards.DashboardItemType.ARCHIVE_CONFIGURATION_BROWSER.Code });
            Node.Children.Add(DataManager);

            Messenger.Children.Add(new NodeElement() { Name = AppState["List.Message.Logs"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_MESSAGE_LOG.Code });
            Node.Children.Add(Messenger);

            Administration.Children.Add(new NodeElement() { Name = AppState["List.Customer"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_CLIENT.Code });
            Administration.Children.Add(new NodeElement() { Name = AppState["List.User"], ItemType = Models.Dashboards.DashboardItemType.BROWSER_USER.Code });
            Administration.Children.Add(new NodeElement() { Name = AppState["Profil.List"], ItemType = Models.Dashboards.DashboardItemType.PROFIL_LIST.Code });
           Administration.Children.Add(new NodeElement() { Name = AppState["Scheduler"], ItemType = Models.Dashboards.DashboardItemType.ADMIN_SCHEDULER_BROWSER.Code });
            Node.Children.Add(Administration);

            Node.Children.Add(new NodeElement() { Name = AppState["HomePage"], ItemType = Models.Dashboards.DashboardItemType.DASHBOARD_HOME_PAGE.Code });
            Node.Children.Add(new NodeElement() { Name = AppState["Tiles"], ItemType = Models.Dashboards.DashboardItemType.HOME_PAGE.Code });
            return Node;
        }
        private void GetSelectedItem(NodeElement e)
        {

            SelectedItem = e;
            StateHasChanged();
        }


    }

  
}

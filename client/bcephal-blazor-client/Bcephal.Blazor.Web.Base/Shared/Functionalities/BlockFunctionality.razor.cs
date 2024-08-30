using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Functionalities;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Functionalities
{
    public partial class BlockFunctionality : ComponentBase
    {

        [Inject]
        private AppState AppState { get; set; }
        //[Inject]
        //private IJSRuntime JSRuntime { get; set; }

        [Parameter]
        public FunctionalityBlock Block { get; set; }

        [Parameter]
        public EventCallback<FunctionalityBlock> BlockChanged { get; set; }

        private FunctionalityBlock BoundBlock
        {
            get => Block;
            set
            {
                Block = value;
                BlockChanged.InvokeAsync(value);
            }
        }

        [Parameter]
        public EventCallback<Models.Functionalities.FunctionalityBlock> DeleteFunctionalityBlock { get; set; }

        [Parameter]
        public EventCallback<Models.Functionalities.FunctionalityBlock> UpdateFunctionalityBlock { get; set; }

        [Parameter]
        public FunctionalityWorkspace FunctionalityWorkspace { get; set; }

        public bool ModalAction { get; set; } = false;

        public string ActionModal { get; set; }
        public string DefaultRoute { get; set; } = "";
        public string Title { get; set; }

        protected async override Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            // await JSRuntime.InvokeVoidAsync("console.log", " f block => ", Block );
            LoadContextualItems();
        }

        DxContextMenu ContextMenu { get; set; }
        dynamic[] ContextualItems;

        public void CallChangeColorModal()
        {
            Title = "Properties";
            ActionModal = "Properties";
            ModalAction = true;
            StateHasChanged();
        }

        public void LoadContextualItems()
        {
            if (Block.Code.Equals(FunctionalityCode.FILE_LOADER_CODE))
            {
                DefaultRoute = Route.LIST_FILES_LOADER;
                ContextualItems = new[] {
                        new {ItemText = AppState["functionality.createFileLoader"] , Route = Route.NEW_LOAD_FILE, IconClass = "ml-1 bi-plus" },
                        new {ItemText = AppState["functionality.listFileLoaders"] , Route = Route.LIST_FILES_LOADER, IconClass = "ml-1 bi-card-list" }
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.AUTOMATIC_INPUT_GRID))
            {
                DefaultRoute = Route.BROWSER_GRID;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.inputgrid"] , Route = Route.EDIT_GRID, IconClass = "ml-1 bi-plus" },
                        new {ItemText = AppState["Functionality.list.inputgrid"] , Route = Route.BROWSER_GRID, IconClass = "ml-1 bi-card-list" }
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.INPUT_GRID))
            {
                DefaultRoute = Route.BROWSER_GRID;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.inputgrid"] , Route = Route.EDIT_GRID, IconClass = "ml-1 bi-plus" },
                        new {ItemText = AppState["Functionality.list.inputgrid"], Route = Route.BROWSER_GRID, IconClass = "ml-1 bi-card-list" }
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.BILLING_TEMPLATE))
            {
                DefaultRoute = Route.BILLING_TEMPLATE_LIST;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.billingtemplate"] , Route = Route.BILLING_TEMPLATE, IconClass = "ml-1 bi-plus" },
                        new {ItemText = AppState["Functionality.list.billingtemplate"] , Route = Route.BILLING_TEMPLATE_LIST, IconClass = "ml-1 bi-card-list" }
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.RECONCILIATION_LOG))
            {
                DefaultRoute = Route.RECONCILIATION_LOG;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.reconciliationlog"] , Route = Route.RECONCILIATION_LOG, IconClass = "ml-1 bi-plus" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.DYNAMIC_FORM))
            {
                DefaultRoute = Route.NEW_DYNAMIC_FORM;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.dynamicform"] , Route = Route.NEW_DYNAMIC_FORM, IconClass = "ml-1 bi-plus" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.BILLING_RUN_OUTCOME))
            {
                DefaultRoute = Route.BILLING_RUN_OUTCOME;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.billingrunoutcome"] , Route = Route.BILLING_RUN_OUTCOME, IconClass = "ml-1 bi-plus" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.BILLING_LIST_INVOICE))
            {
                DefaultRoute = Route.LIST_INVOICE;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.list.billingListInvoice"] , Route = Route.LIST_INVOICE, IconClass = "ml-1 bi-card-list" }
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.BILLING_MODEL) || Block.Code.Equals(FunctionalityCode.BILLING_MODELS))
            {
                DefaultRoute = Route.BILLING_MODEL_BROWSER;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.billingModel"] , Route = Route.BILLING_MODEL_FORM, IconClass = "ml-1 bi-plus" },
                        new {ItemText = AppState["Functionality.list.billingModel"] , Route = Route.BILLING_MODEL_BROWSER, IconClass = "ml-1 bi-card-list" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.BILLING_RUN_STATUS))
            {
                DefaultRoute = Route.CURRENT_BILLING_RUN_STATUS;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.list.billingRunStatus"] , Route = Route.CURRENT_BILLING_RUN_STATUS, IconClass = "ml-1 bi-plus" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.BILLING_EVENT_REPOSITORY))
            {
                DefaultRoute = Route.BILLING_EVENT_REPOSITORY;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.list.billingEvent"] , Route = Route.BILLING_EVENT_REPOSITORY, IconClass = "ml-1 bi-plus" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.SETTINGS_CONFIGURATION))
            {
                DefaultRoute = Route.SETTINGS_CONFIGURATION;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.settings.configuration"] , Route = Route.SETTINGS_CONFIGURATION, IconClass = "ml-1 bi-plus" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.REPORTING_GRID))
            {
                DefaultRoute = Route.BROWSER_REPORTING_GRID;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.reportingGrid"] , Route = Route.EDIT_REPORTING_GRID, IconClass = "ml-1 bi-plus" },
                        new {ItemText = AppState["Functionality.list.reportingGrid"] , Route = Route.BROWSER_REPORTING_GRID, IconClass = "ml-1 bi-card-list" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.REPORT_PIVOT_TABLE))
            {
                DefaultRoute = Route.BROWSER_REPORT_PIVOT_TABLE;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.reportPivotTable"] , Route = Route.EDIT_REPORT_PIVOT_TABLE, IconClass = "ml-1 bi-plus" },
                        new {ItemText = AppState["Functionality.list.reportPivotTable"], Route = Route.BROWSER_REPORT_PIVOT_TABLE, IconClass = "ml-1 bi-card-list" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.REPORTING_REPORT))
            {
                DefaultRoute = Route.BROWSER_REPORT;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.list.reportingReport"] , Route = Route.BROWSER_REPORT, IconClass = "ml-1 bi-plus" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.REPORTING_REPORT_PUBLICATION))
            {
                DefaultRoute = Route.BROWSER_REPORT_PUBLICATION;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.list.reportPublication"], Route = Route.BROWSER_REPORT_PUBLICATION, IconClass = "ml-1 bi-plus" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.RECONCILIATION_FILTER))
            {
                DefaultRoute = Route.RECONCILIATION_FILTER_BROWSER;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.reconciliationFilter"] , Route = Route.RECONCILIATION_FILTER_FORM, IconClass = "ml-1 bi-plus" },
                        new {ItemText = AppState["Functionality.list.reconciliationFilter"] , Route = Route.RECONCILIATION_FILTER_BROWSER, IconClass = "ml-1 bi-card-list" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.REPORTING_CHART))
            {
                DefaultRoute = Route.BROWSER_REPORTING_CHART;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.reportingChart"] , Route = Route.EDIT_REPORTING_CHART, IconClass = "ml-1 bi-plus" },
                        new {ItemText = AppState["Functionality.list.reportingChart"] , Route = Route.BROWSER_REPORTING_CHART, IconClass = "ml-1 bi-card-list" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.REPORT_DASHBOARD))
            {
                DefaultRoute = Route.BROWSER_REPORT_DASHBOARD;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.reportDashboard"] , Route = Route.EDIT_REPORT_DASHBOARD, IconClass = "ml-1 bi-plus" },
                        new {ItemText = AppState["Functionality.list.reportDashboard"] , Route = Route.BROWSER_REPORT_DASHBOARD, IconClass = "ml-1 bi-card-list" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.DASHBOARD_ALARM))
            {
                DefaultRoute = Route.BROWSER_REPORT_ALARM;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.dashboardAlarm"] , Route = Route.EDIT_REPORT_ALARM, IconClass = "ml-1 bi-plus" },
                        new {ItemText = AppState["Functionality.list.dashboardAlarm"] , Route = Route.BROWSER_REPORT_ALARM, IconClass = "ml-1 bi-card-list" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.DASHBOARD_ALARM_SCHEDULED))
            {
                DefaultRoute = Route.BROWSER_REPORT_SCHEDULED_ALARM;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.list.dashboardAlarmScheduled"] , Route = Route.BROWSER_REPORT_SCHEDULED_ALARM, IconClass = "ml-1 bi-plus" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.SOURCING_FILE_LOADER_LOG))
            {
                DefaultRoute = Route.BROWSER_FILES_LOADER_LOGS;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.list.fileLoaderLog"] , Route = Route.BROWSER_FILES_LOADER_LOGS, IconClass = "ml-1 bi-plus" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.SOURCING_SCHEDULED_FILE_LOADER))
            {
                DefaultRoute = Route.BROWSER_SCHEDULED_FILES_LOADER;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.list.fileLoaderScheduling"] , Route = Route.BROWSER_SCHEDULED_FILES_LOADER, IconClass = "ml-1 bi-plus" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.RECONCILIATION_AUTO_RECO))
            {
                DefaultRoute = Route.RECONCILIATION_AUTO_BROWSER;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.autoReco"] , Route = Route.RECONCILIATION_AUTO_FORM, IconClass = "ml-1 bi-plus" },
                        new {ItemText = AppState["Functionality.list.autoReco"] , Route = Route.RECONCILIATION_AUTO_BROWSER, IconClass = "ml-1 bi-card-list" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.DASHBOARDING_DASHBOARD))
            {
                DefaultRoute = Route.BROWSER_REPORT_DASHBOARD;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.dashboard"], Route = Route.EDIT_REPORT_DASHBOARD, IconClass = "ml-1 bi-plus" },
                        new {ItemText = AppState["Functionality.list.dashboard"], Route = Route.BROWSER_REPORT_DASHBOARD, IconClass = "ml-1 bi-card-list" },
                };
            }
            else if (Block.Code.Equals(FunctionalityCode.REPORT_PIVOT_TABLE))
            {
                DefaultRoute = Route.BROWSER_REPORT_PIVOT_TABLE;
                ContextualItems = new[] {
                        new {ItemText = AppState["Functionality.new.reportPivotTable"] , Route = Route.EDIT_REPORT_PIVOT_TABLE, IconClass = "ml-1 bi-plus" },
                        new {ItemText = AppState["Functionality.list.reportPivotTable"] , Route = Route.BROWSER_REPORT_PIVOT_TABLE, IconClass = "ml-1 bi-card-list" },
                };
            }
        }

        private async void Navigate(string route)
        {
            if (string.IsNullOrWhiteSpace(route))
                return;
            await  AppState.NavigateTo(route);
        }

        public void CloseModalAction()
        {
            ModalAction = false;
            StateHasChanged();
        }
    }
}

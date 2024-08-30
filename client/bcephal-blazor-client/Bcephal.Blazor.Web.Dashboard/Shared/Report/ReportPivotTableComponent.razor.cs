using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Report
{
    public partial class ReportPivotTableComponent:ComponentBase
    {
        
        public DxPivotGridDataProvider<JObject> PivotGridDataProvider { get; set; }

        [Inject]
        DashboardReportService DashboardReportService { get; set; }


        [CascadingParameter]
        public Error Error { get; set; }
        DxPivotGrid PivotGrid { get; set; }

        [Parameter]
        public DashboardReport Report { get; set; }

        [Parameter]
        public long? Id { get; set; }


        protected override async Task OnParametersSetAsync()
        {
            await base.OnParametersSetAsync();
            await InitPivotTable();
        }

        private async Task InitPivotTable()
        {
            try
            {
                await LoadPivotTable();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }

        EditorData<DashboardReport> EditorData;

        protected virtual async Task LoadPivotTable()
        {
            try
            {

                EditorDataFilter filter = new EditorDataFilter();
                filter.NewData = true;
                if (Id.HasValue)
                {
                    filter.NewData = false;
                    filter.Id = Id;
                }
                EditorData = await DashboardReportService.GetEditorData(filter);
                Report = EditorData.Item;
                StateHasChanged();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }

    }
}

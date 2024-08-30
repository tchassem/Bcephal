using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Reporting.Pages.Reporting.Charts.components;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Report
{
    public partial class ReportChartComponent : ComponentBase
    {

        [Parameter]
        public DashboardReport Chart { get; set; }

        [Parameter]
        public EditorData<DashboardReport> EditorData { get; set; }

        ChartComponent ChartComponent { get; set; }


        [Parameter]
        public ObservableCollection<Dictionary<string, object>> ChartData_ { get; set; }

    }
}

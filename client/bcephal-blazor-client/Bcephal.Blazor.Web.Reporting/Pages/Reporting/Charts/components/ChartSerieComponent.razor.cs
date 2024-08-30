using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Dashboards;
using Bcephal.Models.Filters;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.Charts.components
{
    public partial class ChartSerieComponent: ComponentBase
    {
        #region  :: Properties section ::

        [Inject]
        public AppState AppState { get; set; }

        [Inject]
        public IJSRuntime JSRuntime { get; set; }

        [Parameter]
        public ChartSerie Serie { get; set; }

        [Parameter]
        public string CssClass { get; set; }

        [Parameter]
        public string ColorClass { get; set; }

        [Parameter]
        public string VisibleClass { get; set; }

        public bool IsXSmallScreen { get; set; }

        [Parameter]
        public IEnumerable<string> ChartSeriesTypes { get; set; }

        [Parameter]
        public IEnumerable<DashboardReportField> DashboardReportFields { get; set; }

        [Parameter]
        public EventCallback<ChartSerie> SerieChanged { get; set; }

        [Parameter]
        public EventCallback<ChartSerie> SerieDeleted { get; set; }

        [Parameter]
        public ObservableCollection<DashboardReportField> ArgumentAxisFields { get; set; }

        [Parameter]
        public ObservableCollection<DashboardReportField> ValueAxisFields { get; set; }

        private bool showConfigModal;

        #endregion


        #region :: Methods section ::

        public string GetSpacing(bool isHorizontal)
        {
            return isHorizontal ? "4px" : "0px";
        }

        private Task AddFilterElement(DashboardReportField field)
        {
            Serie.SerieFilter.AddItem(new SerieFilterItem { Field = field });

            return Task.CompletedTask;
        }
        private Task DeleteFilterElement(SerieFilterItem item)
        {
            Serie.SerieFilter.DeleteItem(item);

            return Task.CompletedTask;
        }
        #endregion
    }
}

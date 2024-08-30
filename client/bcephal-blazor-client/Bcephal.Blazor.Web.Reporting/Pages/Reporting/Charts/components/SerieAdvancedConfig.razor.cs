using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Dashboards;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.Charts.components
{
    public partial class SerieAdvancedConfig: ComponentBase
    {
        #region :: Parameters and properties section
        [Inject]
        public AppState AppState { get; set; }

        [Inject]
        public IJSRuntime JSRuntime { get; set; }

        [Parameter]
        public ChartSerie Serie { get; set; }
        [Parameter]
        public IEnumerable<DashboardReportField> Fields { get; set; }

        public bool IsXSmallScreen { get; set; }
        string formCaptionWidth = "0.2fr";
        string formInputWidth = "0.8fr";
        private IEnumerable<string> ChartAxisAlignments;
        #endregion


        #region :: Methods section ::
        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            ChartAxisAlignments = Enum.GetValues(typeof(ChartAxisAlignment)).Cast<ChartAxisAlignment>().Select((a) => a.ToString());
            Serie.CustomValueAxisAlignment = ChartAxisAlignment.Near.ToString();
        }

        public string GetSpacing(bool isHorizontal)
        {
            return isHorizontal ? "4px" : "0px";
        }

        private void AddFilterElement(DashboardReportField field)
        {
            Serie.SerieFilter.AddItem(new SerieFilterItem { Field = field });
        }
        private void DeleteFilterElement(SerieFilterItem item)
        {
            Serie.SerieFilter.DeleteItem(item);
        }
        #endregion
    }
}

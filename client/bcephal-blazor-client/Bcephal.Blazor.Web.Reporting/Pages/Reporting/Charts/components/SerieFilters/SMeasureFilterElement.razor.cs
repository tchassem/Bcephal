using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Dashboards;
using Microsoft.AspNetCore.Components;
using Bcephal.Models.Filters;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.Charts.components.SerieFilters
{
    public partial class SMeasureFilterElement : ComponentBase
    {
        #region Parameters and properties region
        [Inject]
        public AppState AppState { get; set; }
        [Parameter]
        public SerieFilterItem FilterItem { get; set; }
        [Parameter]
        public EventCallback<SerieFilterItem> FilterItemChanged { get; set; }
        [Parameter]
        public EventCallback<SerieFilterItem> OnClickRemove { get; set; }
        [Parameter]
        public IEnumerable<DashboardReportField> Fields { get; set; }
        IEnumerable<string> FilterVerbs { get; set; }
        public decimal DecimalValue
        {
            get
            {
                try
                {
                    return Convert.ToDecimal(FilterItem.Value);
                }
                catch
                {
                    return decimal.Zero;
                }
            }
            set
            {
                FilterItem.Value = value.ToString();
            }
        }
        public string SelectedFilterVerb
        {
            get
            {
                return FilterItem.FilterVerb.GetText(text => AppState[text]);
            }
            set
            {
                FilterItem.FilterVerb = FilterItem.FilterVerb.GetFilterVerb(value, text => AppState[text]);
            }
        }
        #endregion

        #region Methods region
        protected override void OnInitialized()
        {
            FilterVerbs = FilterItem.FilterVerb.GetAll(text => AppState[text]);
        }

        #endregion
    }
}
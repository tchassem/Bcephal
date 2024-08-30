using Bcephal.Models.Dashboards;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bcephal.Models.Filters;
using Bcephal.Blazor.Web.Base.Services;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.Charts.components.SerieFilters
{
    public partial class SAttributeFilterElement: ComponentBase
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
        IEnumerable<string> AttributeOperators { get; set; }
        IEnumerable<string> FilterVerbs { get; set; }
        bool showAttributeModal = false;
        public String SelectedFilterVerb
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
        AttributeOperator? Operator { get; set; }
        public string SelectedOperator
        {
            get
            {
                try
                {
                    Operator = string.IsNullOrWhiteSpace(FilterItem.Operator) ?
                       null
                       : ((AttributeOperator?)Enum.Parse(typeof(AttributeOperator), FilterItem.Operator));
                    return Operator.GetText(text => AppState[text]);
                }
                catch
                {
                    return null;
                }
                
            }
            set
            {
                FilterItem.Operator = Operator.GetAttributeOperator(value, text => AppState[text]).ToString();
            }
        }

        #endregion

        #region Methods region
        protected override void OnInitialized()
        {
            AttributeOperators = Operator.GetAll(text => AppState[text]);
            FilterVerbs = FilterItem.FilterVerb.GetAll(text => AppState[text]);
        }

        public void ValueSelected(string newValue)
        {
            FilterItem.Value = newValue;
        }
        #endregion
    }
}

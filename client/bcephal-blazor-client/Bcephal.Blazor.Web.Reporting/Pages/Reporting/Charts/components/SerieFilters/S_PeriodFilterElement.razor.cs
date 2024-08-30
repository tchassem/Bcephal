using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Dashboards;
using Bcephal.Models.Filters;
using Microsoft.AspNetCore.Components;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.Charts.components.SerieFilters
{
    public partial class S_PeriodFilterElement: ComponentBase
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
        DateTime? DateValue
        {
            get
            {
                return string.IsNullOrWhiteSpace(FilterItem.Value) 
                    ? DateTime.Today 
                    : DateTime.Parse(FilterItem.Value);
            }
            set
            {
                FilterItem.Value = value.ToString();
            }
        }

        //bool IsSpecific => Operator == PeriodOperator.SPECIFIC;
        bool IsSpecific;
        IEnumerable<string> PeriodGranularities;
        IEnumerable<string> PeriodOperators;
        private IEnumerable<PeriodOperator> Operators = new List<PeriodOperator>(){
            PeriodOperator.TODAY,
            PeriodOperator.BEGIN_WEEK,
            PeriodOperator.END_WEEK,
            PeriodOperator.BEGIN_MONTH,
            PeriodOperator.END_MONTH,
            PeriodOperator.BEGIN_YEAR,
            PeriodOperator.END_YEAR,
            PeriodOperator.SPECIFIC
        };
        IEnumerable<string> FilterVerbs { get; set; }
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
        public string SelectedComparator
        {
            get
            {
                try
                {
                    return string.IsNullOrWhiteSpace(FilterItem.Comparator) ?
                        null
                        : ((PeriodOperator)Enum.Parse(typeof(PeriodOperator), FilterItem.Comparator))
                                                .GetText(text => AppState[text]);
                }
                catch
                {
                    return null;
                }
            }
            set
            {
                FilterItem.Comparator = PeriodOperator.BEGIN_MONTH.GetPeriodOperator(value, text => AppState[text]).ToString();
            }
        }
        public string SelectedGranularity
        {
            get
            {
                try
                {
                    return FilterItem.Granularity.GetText(text => AppState[text]);
                }
                catch
                {
                    return null;
                }
            }
            set
            {
                if (!string.IsNullOrWhiteSpace(value))
                {
                    FilterItem.Granularity = FilterItem.Granularity.GetPeriodGranularity(value, text => AppState[text]);
                }
            }
        }

        #endregion

        #region Methods region
        protected override void OnInitialized()
        {
            IsSpecific = FilterItem.Operator == PeriodOperator.SPECIFIC.ToString();
            PeriodOperators = Operators.Select(v => AppState[v.ToString()].ToString());
            PeriodGranularities = FilterItem.Granularity.GetAll(text => AppState[text]);
            FilterVerbs = FilterItem.FilterVerb.GetAll(text => AppState[text]);
        }

        /*protected void PeriodOperatorSelectionChanged(PeriodOperator value)
        {
            if (value == PeriodOperator.BEGIN_MONTH)
            {
                DateValue = new DateTime(DateTime.Today.Year, DateTime.Today.Month, 1);
            }
            else if (value == PeriodOperator.END_MONTH)
            {
                DateValue = new DateTime(DateTime.Today.Year, DateTime.Today.Month, DateTime.DaysInMonth(DateTime.Today.Year, DateTime.Today.Month));
            }
            else if (value == PeriodOperator.BEGIN_WEEK)
            {
                var culture = CultureInfo.CurrentCulture;
                // si le 1er jour c'est dimanche
                var diff = culture.DateTimeFormat.FirstDayOfWeek == 0 ? DateTime.Today.DayOfWeek - culture.DateTimeFormat.FirstDayOfWeek - 1 : DateTime.Today.DayOfWeek - culture.DateTimeFormat.FirstDayOfWeek;
                if (diff < 0)
                    diff += 7;
                DateValue = DateTime.Today.AddDays(-diff).Date;
            }
            else if (value == PeriodOperator.BEGIN_YEAR)
            {
                DateValue = new DateTime(DateTime.Today.Year, 1, 1);
            }
            else if (value == PeriodOperator.END_WEEK)
            {
                var culture = CultureInfo.CurrentCulture;
                var diff = culture.DateTimeFormat.FirstDayOfWeek == 0 ? DateTime.Today.DayOfWeek - culture.DateTimeFormat.FirstDayOfWeek - 1 : DateTime.Today.DayOfWeek - culture.DateTimeFormat.FirstDayOfWeek;
                if (diff < 0)
                    diff += 7;

                DateValue = DateTime.Today.AddDays(6 - diff);
            }
            else if (value == PeriodOperator.END_YEAR)
            {
                DateValue = new DateTime(DateTime.Today.Year, 12, 31);
            }
            else if (value == PeriodOperator.TODAY)
            {
                DateValue = DateTime.Today;
            }

        }*/
        #endregion
    }
}

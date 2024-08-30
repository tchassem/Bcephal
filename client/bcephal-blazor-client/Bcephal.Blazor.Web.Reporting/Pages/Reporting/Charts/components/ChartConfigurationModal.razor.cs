using Microsoft.AspNetCore.Components;
using System;
using Bcephal.Models.Dashboards;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using DevExpress.Blazor;
using Microsoft.JSInterop;
using Bcephal.Models.Base;
using System.Collections.ObjectModel;
using Bcephal.Blazor.Web.Base.Services;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.Charts.components
{
    public partial class ChartConfigurationModal : ComponentBase
    {
        #region  :: Properties section ::
        [Parameter]
        public DashboardReport Chart { get; set; }

        [Inject]
        public AppState AppState { get; set; }

        [Inject]
        public IJSRuntime JSRuntime { get; set; }

        bool IsXSmallScreen;
        string formCaptionWidth = "0.2fr";
        string formInputWidth = "0.8fr";
        public string cssClass = "p-1 min-w-160 bc-w-20";
        public string visibleClass = "p-1 min-w-82";
        public string colorClass = "p-1 min-w-60";

        public DashboardReportChartType ChartType
        {
            get { return Chart.ChartProperties.DashboardReportChartType; }
            set
            {
                Chart.ChartProperties.DashboardReportChartType = value;
                AppState.Update = true;
            }
        }

        public string Subtitle
        {
            get { return Chart.ChartProperties.WebChartData.Subtitle; }
            set
            {
                Chart.ChartProperties.WebChartData.Subtitle = value;
                AppState.Update = true;
            }
        }
        public string Title
        {
            get 
            {
                if (string.IsNullOrWhiteSpace(Chart.ChartProperties.WebChartData.Title))
                {
                    Chart.ChartProperties.WebChartData.Title = Chart.Name;
                }
                return Chart.ChartProperties.WebChartData.Title; 
            }
            set
            {
                Chart.ChartProperties.WebChartData.Title = value;
                AppState.Update = true;
            }
        }
        public ChartSerie DefaultSerie
        {
            get
            {
                if (Chart.ChartProperties.WebChartData.DefaultSerie == null )
                {
                    Chart.ChartProperties.WebChartData.DefaultSerie = new ChartSerie() { IsVisible = true, IsDefault = true };
                }
                return Chart.ChartProperties.WebChartData.DefaultSerie;
            }
        }

        public bool Rotated
        {
            get { return Chart.ChartProperties.WebChartData.Rotated; }
            set { Chart.ChartProperties.WebChartData.Rotated = value;
                AppState.Update = true;
            }
        }

        public ChartLabelOverlap? LabelOverlap
        {
            get
            {
                return string.IsNullOrWhiteSpace(Chart.ChartProperties.WebChartData.LabelOverlap) ?
                   null
                  : (ChartLabelOverlap?)Enum.Parse(typeof(ChartLabelOverlap), Chart.ChartProperties.WebChartData.LabelOverlap);
            }
            set { 
                Chart.ChartProperties.WebChartData.LabelOverlap = value.Value.ToString();
                AppState.Update = true;
            }
        }

        public Orientation? LegendOrientation
        {
            get
            {
                return string.IsNullOrWhiteSpace(Chart.ChartProperties.WebChartData.LegendOrientation) ?
                  null
                  : (Orientation?)Enum.Parse(typeof(Orientation), Chart.ChartProperties.WebChartData.LegendOrientation);
            }
            set { 
                Chart.ChartProperties.WebChartData.LegendOrientation = value.Value.ToString();
                AppState.Update = true;
            }
        }

        public bool ShowLegend
        {
            get { return Chart.ChartProperties.WebChartData.ShowLegend; }
            set { Chart.ChartProperties.WebChartData.ShowLegend = value;
                AppState.Update = true;
            }
        }

        public bool ShowLegendBorder
        {
            get { return Chart.ChartProperties.WebChartData.ShowLegendBorder; }
            set { 
                Chart.ChartProperties.WebChartData.ShowLegendBorder = value;
                AppState.Update = true;
            }
        }

        public bool ShowAxisTitles
        {
            get { return Chart.ChartProperties.WebChartData.ShowAxisTitles; }
            set 
            { 
                Chart.ChartProperties.WebChartData.ShowAxisTitles = value;
                AppState.Update = true;
            }
        }

        public RelativePosition? LegendPosition
        {
            get
            {
                return string.IsNullOrWhiteSpace(Chart.ChartProperties.WebChartData.LegendPosition) ?
                   null
                  : (RelativePosition?)Enum.Parse(typeof(RelativePosition), Chart.ChartProperties.WebChartData.LegendPosition);
            }
            set
            {
                Chart.ChartProperties.WebChartData.LegendPosition = value.Value.ToString();
                AppState.Update = true;
            }
        }

        public HorizontalAlignment? LegendHorizontalAlignment
        {
            get
            {
                return string.IsNullOrWhiteSpace(Chart.ChartProperties.WebChartData.LegendHorizontalAlignment) ?
                  null
                  : (HorizontalAlignment?)Enum.Parse(typeof(HorizontalAlignment), Chart.ChartProperties.WebChartData.LegendHorizontalAlignment);
            }
            set { 
                Chart.ChartProperties.WebChartData.LegendHorizontalAlignment = value.Value.ToString();
                AppState.Update = true;
            }
        }

        public bool LegendAllowToggleSeries
        {
            get { return Chart.ChartProperties.WebChartData.LegendAllowToggleSeries; }
            set { 
                Chart.ChartProperties.WebChartData.LegendAllowToggleSeries = value;
                AppState.Update = true;
            }
        }

        public string LegendTitle
        {
            get { return Chart.ChartProperties.WebChartData.LegendTitle; }
            set { 
                Chart.ChartProperties.WebChartData.LegendTitle = value;
                AppState.Update = true;
            }
        }

        public string LegendSubtitle
        {
            get { return Chart.ChartProperties.WebChartData.LegendSubtitle; }
            set { 
                Chart.ChartProperties.WebChartData.LegendSubtitle = value;
                AppState.Update = true;
            }
        }

        public string ValueAxisTitle
        {
            get { return Chart.ChartProperties.WebChartData.ValueAxisTitle; }
            set { 
                Chart.ChartProperties.WebChartData.ValueAxisTitle = value;
                AppState.Update = true;
            }
        }

        public string ArgumentAxisTitle
        {
            get { return Chart.ChartProperties.WebChartData.ArgumentAxisTitle; }
            set { 
                Chart.ChartProperties.WebChartData.ArgumentAxisTitle = value;
                AppState.Update = true;
            }
        }

        public List<ChartSerie> ChartSerieList
        {
            get { return Chart.ChartProperties.WebChartData.ChartSerieList; }
        }

        public ObservableCollection<DashboardReportField> DashboardReportFields
        {
            get { return Chart.FieldListChangeHandler.GetItems(); }
        }

        private IEnumerable<Orientation?> Orientations;
        private IEnumerable<RelativePosition?> RelativePositions;
        private IEnumerable<HorizontalAlignment?> HorizontalAlignments;
        private IEnumerable<string> ChartSeriesTypes;

        #endregion


        #region :: Methods section ::

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            Orientations = Enum.GetValues(typeof(Orientation)).Cast<Orientation?>();
            RelativePositions = Enum.GetValues(typeof(RelativePosition)).Cast<RelativePosition?>();
            HorizontalAlignments = Enum.GetValues(typeof(HorizontalAlignment)).Cast<HorizontalAlignment?>();
            ChartSeriesTypes = Enum.GetValues(typeof(ChartSeriesType)).Cast<ChartSeriesType?>().Select((t) => t.Value.ToString());
        }

        public void AddChartSerie()
        {
            Chart.ChartProperties.WebChartData.AddSerie(new ChartSerie() { IsVisible = true, IsDefault = false, Type = ChartSeriesType.Bar.ToString() });
            AppState.Update = true;
        }

        public void DeleteChartSerie(ChartSerie serie)
        {
            Chart.ChartProperties.WebChartData.RemoveOrForgetItem(serie);
            AppState.Update = true;
        }

        public string GetSpacing(bool isHorizontal)
        {
            return isHorizontal ? "4px" : "0px";
        }
        #endregion
    }
}

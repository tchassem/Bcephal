using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Bcephal.Models.Dashboards;
using System.Threading.Tasks;
using Microsoft.JSInterop;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Reporting.Services;
using System.Collections.ObjectModel;
using DevExpress.Blazor;
using Bcephal.Models.Base;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.Charts.components
{
    public partial class ChartComponent: ComponentBase
    {

        #region  :: Properties section ::
        [Inject]
        public IJSRuntime JSRuntime { get; set; }
        [Inject]
        public IToastService ToastService { get; set; }
        [Inject]
        public AppState AppState { get; set; }
        [Inject]
        public DashboardReportService DRService { get; set; }

        [Parameter]
        public DashboardReport Chart { get; set; }


        //[Parameter]
        //public Func<Task<ObservableCollection<Dictionary<string, object>>>> GetChartData { get; set; }


        [Parameter]
        public ObservableCollection<Dictionary<string, object>> ChartData_ { get; set; }
        private ObservableCollection<Dictionary<string, object>> ChartData { get; set; }

        public string ChartType
        {
            get { return Chart.ChartProperties.ChartType; }
            set { Chart.ChartProperties.ChartType = value; }
        }

        public string Title
        {
            get => Chart.ChartProperties.WebChartData.Title; 
        }

        public string Subtitle
        {
            get { return Chart.ChartProperties.WebChartData.Subtitle; }
            set { Chart.ChartProperties.WebChartData.Subtitle = value; }
        }

        public bool Rotated
        {
            get { 
                return Chart.ChartProperties.WebChartData.Rotated; 
            }
            set { Chart.ChartProperties.WebChartData.Rotated = value; }
        }

        public ChartLabelOverlap LabelOverlap
        {
            get
            {
                return Chart.ChartProperties.WebChartData.LabelOverlap == null ?
                   ChartLabelOverlap.ShowAll
                  : (ChartLabelOverlap)Enum.Parse(typeof(ChartLabelOverlap), Chart.ChartProperties.WebChartData.LabelOverlap);
            }
            set { Chart.ChartProperties.WebChartData.LabelOverlap = value.ToString(); }
        }

        public Orientation LegendOrientation
        {
            get
            {
                return Chart.ChartProperties.WebChartData.LegendOrientation == null ?
                  Orientation.Vertical
                  : (Orientation)Enum.Parse(typeof(Orientation), Chart.ChartProperties.WebChartData.LegendOrientation);
            }
            set { Chart.ChartProperties.WebChartData.LegendOrientation = value.ToString(); }
        }

        public bool ShowLegend
        {
            get { return Chart.ChartProperties.WebChartData.ShowLegend; }
        }
        
        public bool ShowAxisTitles
        {
            get { return Chart.ChartProperties.WebChartData.ShowAxisTitles; }
        }

        public bool ShowLegendBorder
        {
            get { return Chart.ChartProperties.WebChartData.ShowLegendBorder; }
            set { Chart.ChartProperties.WebChartData.ShowLegendBorder = value; }
        }

        public RelativePosition LegendPosition
        {
            get
            {
                return Chart.ChartProperties.WebChartData.LegendPosition == null ?
                   RelativePosition.Outside
                  : (RelativePosition)Enum.Parse(typeof(RelativePosition), Chart.ChartProperties.WebChartData.LegendPosition);
            }
            set { Chart.ChartProperties.WebChartData.LegendPosition = value.ToString(); }
        }

        public HorizontalAlignment LegendHorizontalAlignment
        {
            get
            {
                return Chart.ChartProperties.WebChartData.LegendHorizontalAlignment == null ?
                  HorizontalAlignment.Right
                  : (HorizontalAlignment)Enum.Parse(typeof(HorizontalAlignment), Chart.ChartProperties.WebChartData.LegendHorizontalAlignment);
            }
            set { Chart.ChartProperties.WebChartData.LegendHorizontalAlignment = value.ToString(); }
        }

        public bool LegendAllowToggleSeries
        {
            get { return Chart.ChartProperties.WebChartData.LegendAllowToggleSeries; }
            set { Chart.ChartProperties.WebChartData.LegendAllowToggleSeries = value; }
        }

        public string LegendTitle
        {
            get { return Chart.ChartProperties.WebChartData.LegendTitle; }
            set { Chart.ChartProperties.WebChartData.LegendTitle = value; }
        }

        public string LegendSubtitle
        {
            get { return Chart.ChartProperties.WebChartData.LegendSubtitle; }
            set { Chart.ChartProperties.WebChartData.LegendSubtitle = value; }
        }

        public string ValueAxisTitle
        {
            get { return Chart.ChartProperties.WebChartData.ValueAxisTitle; }
            set { Chart.ChartProperties.WebChartData.ValueAxisTitle = value; }
        }

        public string ArgumentAxisTitle
        {
            get { return Chart.ChartProperties.WebChartData.ArgumentAxisTitle; }
            set { Chart.ChartProperties.WebChartData.ArgumentAxisTitle = value; }
        }

        public ChartSerie DefaultSerie
        {
            get { return Chart.ChartProperties.WebChartData.DefaultSerie; }
        }

        public List<ChartSerie> ChartSerieList
        {
            get { return Chart.ChartProperties.WebChartData.ChartSerieList; }
        }

        private bool freese = false;
        private string defaultValueAxis = "defaultValueAxis";

        #endregion


        #region  :: Methods section ::
        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            // Cette vérification sert à éviter de charger les données alors que la modale de configation du chart est ouvert
            if (!freese && Chart  != null && Chart.ChartProperties != null && ( DefaultSerie != null || ChartSerieList.Count > 0))
            {
                if(ChartData_ == null)
                {
                    await Task.Run(RefreshView);
                }
                else
                {
                    ChartData = ChartData_;
                    UnfreezeView();
                }
                
            }
        }

        //protected override void OnAfterRender(bool firstRender)
        //{
        //    base.OnAfterRender(firstRender);
        //    //await JSRuntime.InvokeVoidAsync("console.log", " Recherche dans OnAfterRender ");
        //}

        protected override bool ShouldRender()
        {
            return !freese && base.ShouldRender();
        }

        public async Task RefreshView()
        {
            //AppState.ShowLoadingStatus();
            FreezeView();
            ChartData = await DRService.GetRows(Chart);
            //await JSRuntime.InvokeVoidAsync("console.log", " Données à afficher => ", ChartData);
            if (ChartData != null)
            {
                UnfreezeView();
                //AppState.HideLoadingStatus();
                StateHasChanged();
            }
        }

        public void FreezeView()
        {
            freese = true;
        }

        public void UnfreezeView()
        {
            freese = false;
        }

        // Verifie si une série est valide afin de savoir si on l'affiche ou pas
        public bool IsValidSerie(ChartSerie _serie, bool isDefaultSerie)
        {
            if (isDefaultSerie)
            {
                if (_serie.SerieAxis == null || _serie.ArgumentAxis == null || _serie.ValueAxis == null)
                {
                    return false;
                }
            }
            else
            {
                if (_serie.Name == null || _serie.ArgumentAxis == null || _serie.ValueAxis == null)
                {
                    return false;
                }
            }

            return true;
        }

        

        #endregion

    }
}

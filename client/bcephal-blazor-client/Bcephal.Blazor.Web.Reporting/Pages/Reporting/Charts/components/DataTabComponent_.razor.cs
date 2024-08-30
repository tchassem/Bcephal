using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Dashboards;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.Reporting.Charts.components
{
    public partial class DataTabComponent_: ComponentBase
    {
        #region :: Properties section ::


        [Inject]
        public AppState AppState { get; set; }
        [Inject]
        public IToastService ToastService { get; set; }

        [Inject]
        public IJSRuntime JSRuntime { get; set; }

        [Parameter]
        public DashboardReport Chart { get; set; }

        [Parameter]
        public long? ChartId { get; set; }
        [Parameter]
        public Action AfterRenderHandler { get; set; }

        bool ShowPopup { get; set; } = false;
        bool ShowConfigModal { get; set; } = false;

        string ContentModal { get; set; }

        ChartComponent ChartComponent { get; set; }
        BaseModalComponent ConfigModalComponent { get; set; }
        #endregion


        #region :: Methods section ::

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            if (Chart.ChartProperties == null )
            {
                Chart.ChartProperties = new Models.Dashboards.ChartProperties();
                Chart.ChartProperties.DashboardReportChartType = DashboardReportChartType.FULL_STACKED_BAR;
                Chart.ChartProperties.WebChartData = new Models.Dashboards.WebChartData()
                {
                    ShowLegend = true,
                    ShowLegendBorder = true,
                    LegendOrientation = Orientation.Vertical.ToString(),
                    LegendPosition = RelativePosition.Outside.ToString(),
                    LegendHorizontalAlignment = HorizontalAlignment.Right.ToString()
                };
            }
        }

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            if (firstRender)
            {
                AfterRenderHandler?.Invoke();
            }
        }

        public void OpenConfigModal()
        {
            ChartComponent.FreezeView();
            ShowConfigModal = true;
        }

        public async void ConfigModalOkHandler()
        {
            if (ChartComponent != null)
            {
                if(Chart.ChartProperties.WebChartData.DefaultSerie.SerieAxis == null || Chart.ChartProperties.WebChartData.DefaultSerie.ArgumentAxis == null || Chart.ChartProperties.WebChartData.DefaultSerie.ValueAxis == null )
                {
                    ToastService.ShowError(AppState["config.defaultSerie.issue"]);
                    ConfigModalComponent.CanClose = false;
                    return;
                }
                else if(Chart.ChartProperties.WebChartData.ChartSerieList.Any(s => s.Name == null || s.ArgumentAxis == null || s.ValueAxis == null))
                {
                    ToastService.ShowError(AppState["config.series.issue"]);
                    ConfigModalComponent.CanClose = false;
                    return;
                }
                else
                {
                    ConfigModalComponent.CanClose = true;
                    ChartComponent.UnfreezeView();
                    await ChartComponent.RefreshView();
                    AppState.Update = true;
                }
            }
        }

        #endregion
    }
}

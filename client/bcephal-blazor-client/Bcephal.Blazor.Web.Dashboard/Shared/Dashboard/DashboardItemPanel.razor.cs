using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Base.Shared.Component.Splitter;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Bcephal.Models.Functionalities;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Dashboard
{
  public partial  class DashboardItemPanel : ComponentBase
    {
        [CascadingParameter] public Error Error { get; set; }
        [Parameter] public Models.Dashboards.DashboardItem Item { get; set; }
        [Parameter] public NodeElement SelectItemNode { get; set; }
        [Parameter] public Action<string> UpdateUidHandler { get; set; }
        [Parameter] public EventCallback<NodeElement> SelectItemNodeChanged { get; set; }
        [Parameter] public bool Editable { get; set; } = true;
        [Parameter] public int? Position { get; set; }
        [Parameter] public bool IsReadOnlyData { get; set; } = false;
        [Parameter] public Action<Models.Dashboards.DashboardItem> AddItemHandler { get; set; }
        [Parameter] public Action<Models.Dashboards.DashboardItem> UpdateItemHandler { get; set; }
        [Parameter] public Func<int,Models.Dashboards.DashboardItem> GetItemHandler { get; set; }
        [Parameter] public Action<Models.Dashboards.DashboardItem> DeleteItemHandler { get; set; }

        [Inject] DashboardReportService DashboardReportService { get; set; }
        private DashboardReport Chart { get; set; }
        private EditorData<DashboardReport> EditorDataChart { get; set; }
        public ObservableCollection<Dictionary<string, object>> ChartData_ { get; set; }
        [Parameter] public string Width_ { get; set; }
        [Parameter] public string Height_ { get; set; }
        private string DivRef { get; set; } = Guid.NewGuid().ToString("d");

        private void AddDashboardItem(Models.Dashboards.DashboardItem item)
        {
            if(Item != null)
            {
                DeleteItemHandler?.Invoke(Item);
            }
            Item = item;
            if (Position.HasValue)
            {
                Item.Position = Position.Value;
            }
            AddItemHandler?.Invoke(Item);
        }

        private void UpdateDashboardItem(Models.Dashboards.DashboardItem item)
        {
            Item = item;
            Item.Position = Position.Value;
            UpdateItemHandler?.Invoke(Item);
        }

        private void DeleteDashboardItem(Models.Dashboards.DashboardItem item)
        {
            Models.Dashboards.DashboardItem item_ = GetItem_();
            DeleteItemHandler?.Invoke(item_);
        }

        public Models.Dashboards.DashboardItem GetItem()
        {
            if(Item != null)
            {
                return Item;
            }
            else
            {
                return GetItemHandler?.Invoke(Position.Value);
            }
        }

        public Models.Dashboards.DashboardItem GetItem_()
        {
            return GetItemHandler?.Invoke(Position.Value);
        }

        protected override async Task OnInitializedAsync()
        {
            if (Item != null && Item.DashboardItemType == DashboardItemType.CHART)
            {
                await LoadChart();
            }
            await base.OnInitializedAsync();
        }
   

        protected async Task LoadChart()
        {
            try
            {
                EditorDataFilter filter = new EditorDataFilter();
                filter.NewData = true;
                if (Item.ItemId.HasValue)
                {
                    filter.NewData = false;
                    filter.Id = Item.ItemId;
                }
                EditorDataChart = await DashboardReportService.GetEditorData(filter);
                Chart = EditorDataChart.Item;
                ChartData_ = await DashboardReportService.GetRows(Chart);

            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

    }

    
    public static class DashboardItemExtension
    {
        public static  async Task BoundingClientRect(this DashboardItem dashboardItem, IJSRuntime jSRuntime)
        {
            //CultureInfo provider = new CultureInfo("en-us");
            //string format = "0.000000";
            BoundingClientRect boundingClientRect  =  await SplitterCJsInterop.RoundingClientRectDashboardItem(jSRuntime, dashboardItem.ItemKey);
            if (boundingClientRect != null)
            {
                dashboardItem.Width = (int)boundingClientRect.Width;
                dashboardItem.Height = (int)boundingClientRect.Height;
            }
        
        }
    }
}

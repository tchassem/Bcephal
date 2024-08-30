using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Dashboards;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.IO;
using System.Threading;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Microsoft.JSInterop;
using Bcephal.Models.Filters;
using System.Data;
using System.Collections.ObjectModel;
using Newtonsoft.Json.Linq;
using Microsoft.AspNetCore.Components.Web;
using Newtonsoft.Json;

namespace Bcephal.Blazor.Web.Reporting.Shared.Reporting.pivot
{
   public partial class PivotGridComponent: IAsyncDisposable
    {
        #region Properties
        [Parameter]
        public EditorData<DashboardReport> EditorData { get; set; }
        [Parameter]
        public EventCallback<EditorData<DashboardReport>> EditorDataChanged { get; set; }

        [Parameter]
        public Action RefreshDataContentHandler { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;
        public DxPivotGrid PivotGrid { get; set; }

        [Inject]
        public DashboardReportService DashboardReportService { get; set; }

        [Inject]
        public AppState AppState { get; set; }

        [Inject]
        public IJSRuntime jSRuntime { get; set; }

        public ElementReference ElementReference { get; set; }

        DxPivotGridDataProvider<JObject> PivotGridDataProvider { get; set; }

        DashboardReport Report { get => EditorData != null ? EditorData.Item : null; }
        bool Properties { get; set; }

        #endregion

        #region Operations

        [Inject]
        private IJSRuntime JSRuntime { get; set; }
        DotNetObjectReference<PivotGridComponent> dotNetReference;
        string IdRow = Guid.NewGuid().ToString("d");
        string IdData = Guid.NewGuid().ToString("d");
        string IdFilter = Guid.NewGuid().ToString("d");
        string IdColumn = Guid.NewGuid().ToString("d");
        protected async override Task OnInitializedAsync()
        {
            dotNetReference = DotNetObjectReference.Create(this);
            if (Editable)
            {
                await JSRuntime.InvokeVoidAsync("drop_handler_Callback", IdRow, dotNetReference, "dropRow");
                await JSRuntime.InvokeVoidAsync("drop_handler_Callback", IdColumn, dotNetReference, "dropColumn");
                await JSRuntime.InvokeVoidAsync("drop_handler_Callback", IdData, dotNetReference, "dropData");
                await JSRuntime.InvokeVoidAsync("drop_handler_Callback", IdFilter, dotNetReference, "dropFilter");
                await base.OnInitializedAsync();
                PivotGridDataProvider = DxPivotGridDataProvider<JObject>.Create(DashboardReportService.GetRows_(Report));
            }
        }

        public async ValueTask DisposeAsync()
        {
            if (Editable)
            {
                await JSRuntime.InvokeVoidAsync("drop_handler_Callback_delete", IdRow);
                await JSRuntime.InvokeVoidAsync("drop_handler_Callback_delete", IdColumn);
                await JSRuntime.InvokeVoidAsync("drop_handler_Callback_delete", IdData);
                await JSRuntime.InvokeVoidAsync("drop_handler_Callback_delete", IdFilter);
            }
        }


        [JSInvokable("dropRow")]
        public async void dropRow(string Current_)
        {

            if (!string.IsNullOrEmpty(Current_))
            {
                SelectItem = JsonConvert.DeserializeObject<DashboardReportField>(Current_);
                if (Report.PivotTableProperties == null)
                {
                    Report.PivotTableProperties = new PivotTableProperties();
                    Report.PivotTableProperties.WebPivotTableLayout = new();
                }
                WebPivotTableLayoutField Item = new WebPivotTableLayoutField()
                { Caption = SelectItem.Name, DimensionName = SelectItem.Name, Type = SelectItem.Type };
                Report.PivotTableProperties
                    .WebPivotTableLayout.AddField(Item);
                if (Item != null)
                {
                    Item.FieldGroup = DashboardReportFieldGroup.ROW;
                }
                SelectItem = null;
                await EditorDataChanged.InvokeAsync(EditorData);
            }
        }
        [JSInvokable("dropColumn")]
        public async void dropColumn(string Current_)
        {
            if (!string.IsNullOrEmpty(Current_))
            {
                SelectItem = JsonConvert.DeserializeObject<DashboardReportField>(Current_);
                if (Report.PivotTableProperties == null)
                {
                    Report.PivotTableProperties = new PivotTableProperties();
                    Report.PivotTableProperties.WebPivotTableLayout = new();
                }
                WebPivotTableLayoutField Item = new WebPivotTableLayoutField()
                { Caption = SelectItem.Name, DimensionName = SelectItem.Name, Type = SelectItem.Type };
                Report.PivotTableProperties
                    .WebPivotTableLayout.AddField(Item);
                if (Item != null)
                {
                    Item.FieldGroup = DashboardReportFieldGroup.COLUMN;
                }
                SelectItem = null;
                await EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        [JSInvokable("dropData")]
        public async void dropData(string Current_)
        {
            if (!string.IsNullOrEmpty(Current_))
            {
                SelectItem = JsonConvert.DeserializeObject<DashboardReportField>(Current_);
                if (SelectItem != null && DimensionType.MEASURE.Equals(SelectItem.Type))
                {
                    if (Report.PivotTableProperties == null)
                    {
                        Report.PivotTableProperties = new PivotTableProperties();
                        Report.PivotTableProperties.WebPivotTableLayout = new();
                    }
                    WebPivotTableLayoutField Item = new WebPivotTableLayoutField()
                    { Caption = SelectItem.Name, DimensionName = SelectItem.Name, Type = SelectItem.Type };
                    Report.PivotTableProperties
                        .WebPivotTableLayout.AddField(Item);
                    if (Item != null)
                    {
                        Item.FieldGroup = DashboardReportFieldGroup.COMMON;
                    }
                    SelectItem = null;
                    await EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }
        [JSInvokable("dropFilter")]
        public async void dropFilter(string Current_)
        {
            if (!string.IsNullOrEmpty(Current_))
            {
                SelectItem = JsonConvert.DeserializeObject<DashboardReportField>(Current_);
                if (Report.PivotTableProperties == null)
                {
                    Report.PivotTableProperties = new PivotTableProperties();
                    Report.PivotTableProperties.WebPivotTableLayout = new();
                }
                WebPivotTableLayoutField Item = new WebPivotTableLayoutField()
                { Caption = SelectItem.Name, DimensionName = SelectItem.Name, Type = SelectItem.Type };
                Report.PivotTableProperties
                    .WebPivotTableLayout.AddField(Item);
                if (Item != null)
                {
                    Item.FieldGroup = DashboardReportFieldGroup.FILTER;
                }
                SelectItem = null;
                await EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        #endregion

        protected override void OnAfterRender(bool firstRender)
        {
            base.OnAfterRender(firstRender);
            if (firstRender)
            {
                RefreshDataContentHandler?.Invoke();
            }
        }

        private void Ok()
        {
            AppState.Update = true;
            PivotGridDataProvider = DxPivotGridDataProvider<JObject>.Create(DashboardReportService.GetRows_(Report));
            StateHasChanged();
        }
        private void Cancel()
        {
        }

        private DashboardReportField SelectItem { get; set; } = null;

        private void SelectItemDrap(DashboardReportField selectItem)
        {
            SelectItem = selectItem;
        }


        public async void dropRow(Microsoft.AspNetCore.Components.Web.DragEventArgs e)
        {
            if (SelectItem != null)
            {
                if (Report.PivotTableProperties == null)
                {
                    Report.PivotTableProperties = new PivotTableProperties();
                    Report.PivotTableProperties.WebPivotTableLayout = new();
                }
                WebPivotTableLayoutField Item = new WebPivotTableLayoutField()
                { Caption = SelectItem.Name, DimensionName = SelectItem.Name, Type = SelectItem.Type };
                Report.PivotTableProperties
                    .WebPivotTableLayout.AddField(Item);
                if (Item != null)
                {
                    Item.FieldGroup = DashboardReportFieldGroup.ROW;
                }
                SelectItem = null;
                await EditorDataChanged.InvokeAsync(EditorData);
            }
        }
        public async void dropColumn(Microsoft.AspNetCore.Components.Web.DragEventArgs e)
        {
            if (SelectItem != null)
            {
                if (Report.PivotTableProperties == null)
                {
                    Report.PivotTableProperties = new PivotTableProperties();
                    Report.PivotTableProperties.WebPivotTableLayout = new();
                }
                WebPivotTableLayoutField Item = new WebPivotTableLayoutField()
                { Caption = SelectItem.Name, DimensionName = SelectItem.Name, Type = SelectItem.Type };
                Report.PivotTableProperties
                    .WebPivotTableLayout.AddField(Item);
                if (Item != null)
                {
                    Item.FieldGroup = DashboardReportFieldGroup.COLUMN;
                }
                SelectItem = null;
                await EditorDataChanged.InvokeAsync(EditorData);
            }
        }
        public async void dropData(Microsoft.AspNetCore.Components.Web.DragEventArgs e)
        {
            if (SelectItem != null && DimensionType.MEASURE.Equals(SelectItem.Type))
            {
                if (Report.PivotTableProperties == null)
                {
                    Report.PivotTableProperties = new PivotTableProperties();
                    Report.PivotTableProperties.WebPivotTableLayout = new();
                }
                WebPivotTableLayoutField Item = new WebPivotTableLayoutField()
                { Caption = SelectItem.Name, DimensionName = SelectItem.Name, Type = SelectItem.Type };
                Report.PivotTableProperties
                    .WebPivotTableLayout.AddField(Item);
                if (Item != null)
                {
                    Item.FieldGroup = DashboardReportFieldGroup.COMMON;
                }
                SelectItem = null;
                await EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public async void dropFilter(Microsoft.AspNetCore.Components.Web.DragEventArgs e)
        {
            if (SelectItem != null)
            {
                if (Report.PivotTableProperties == null)
                {
                    Report.PivotTableProperties = new PivotTableProperties();
                    Report.PivotTableProperties.WebPivotTableLayout = new();
                }
                WebPivotTableLayoutField Item = new WebPivotTableLayoutField()
                { Caption = SelectItem.Name, DimensionName = SelectItem.Name, Type = SelectItem.Type };
                Report.PivotTableProperties
                    .WebPivotTableLayout.AddField(Item);
                if (Item != null)
                {
                    Item.FieldGroup = DashboardReportFieldGroup.FILTER;
                }
                SelectItem = null;
                await EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private async void DeleteItem(MouseEventArgs args,  WebPivotTableLayoutField item)
        {
            Report.PivotTableProperties
                     .WebPivotTableLayout.deleteField(item);
            await EditorDataChanged.InvokeAsync(EditorData);
        }
    }
}

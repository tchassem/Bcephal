using Bcephal.Models.Base;
using Bcephal.Models.Billing.Model;
using Bcephal.Models.Dimensions;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using Bcephal.Models.Utils;
using Microsoft.JSInterop;
using Bcephal.Blazor.Web.Base.Services;
using Microsoft.AspNetCore.Components.Web;
using Bcephal.Models.Dashboards;
using Newtonsoft.Json;
using Bcephal.Models.Filters;

namespace Bcephal.Blazor.Web.Reporting.Shared.Reporting.pivot
{
    public partial class PivotTableDesignComponent : ComponentBase,IAsyncDisposable
    {
        [Inject]
        private IJSRuntime JSRuntime { get; set; }

        [Parameter]
        public EditorData<DashboardReport> EditorData { get; set; }
        [Parameter]
        public EventCallback<EditorData<DashboardReport>> EditorDataChanged { get; set; }
        [Parameter]
        public IEnumerable<HierarchicalData> Entities { get; set; }

        [Inject]
        public AppState AppState { get; set; }

        public ObservableCollection<Dimension> Attributes { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public Action RefreshDesignContentHandler { get; set; }

        DotNetObjectReference<PivotTableDesignComponent> dotNetReference;
        string Id = Guid.NewGuid().ToString("d");
        protected async override Task OnInitializedAsync()
        {
            dotNetReference = DotNetObjectReference.Create(this);
            await JSRuntime.InvokeVoidAsync("drop_handler_Callback", Id, dotNetReference, "Drop");
            await base.OnInitializedAsync();
            InitFilterData();
        }

        string Drop_ = "Drop";
        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            if (firstRender)
            {
                RefreshDesignContentHandler?.Invoke();
                
            }
        }

        public async ValueTask DisposeAsync()
        {
            await JSRuntime.InvokeVoidAsync("drop_handler_Callback_delete", Id);
        }

        public void InitFilterData()
        {
            //List<Dimension> items = new List<Dimension>();
            //items.AddRange(EditorData.Periods);
            //items.AddRange(EditorData.Measures);
            //items.AddRange(InitAttributeList());
            Attributes = new ObservableCollection<Dimension>(InitAttributeList());
        }

        private ObservableCollection<Dimension> InitAttributeList()
        {
            int offset = 0;
            List<Dimension> items = new List<Dimension>();
            while (Entities != null && offset < Entities.Count())
            {
                items.Add(((Entity)Entities.ElementAt(offset)));
                offset++;
            }
            items.BubbleSort();
            return new ObservableCollection<Dimension>(items);
        }

        Dimension Current = null;
        private void HandleDragStart(HierarchicalData e)
        {
            Current = (Dimension)e;
        }

        [JSInvokable("Drop")]
        //public void Drop(DragEventArgs argv)
        public void Drop(string Current_)
        {

            if (Current_ != null)
            {
                Dimension Current__ = null;
                DimensionType type = Models.Filters.DimensionType.ATTRIBUTE;
                if (Current_.Contains("ATTRIBUTE"))
                {
                    Current__ = JsonConvert.DeserializeObject<Models.Dimensions.Attribute>(Current_);
                }else
                    if (Current_.Contains("MEASURE"))
                {
                    Current__ = JsonConvert.DeserializeObject<Models.Dimensions.Measure>(Current_);
                    type = Models.Filters.DimensionType.MEASURE;
                }
                else
                {
                    Current__ = JsonConvert.DeserializeObject<Models.Dimensions.Period>(Current_);
                    type = Models.Filters.DimensionType.PERIOD;
                }
                
                DashboardReportField field = new DashboardReportField();
                field.DimensionId = Current__.Id;
                field.DimensionName = Current__.Name;
                field.Name = field.DimensionName;
                field.Type = type;
                var found = EditorData.Item.FieldListChangeHandler.GetItems()
                    .Where(it=> it.DimensionId == field.DimensionId && it.DimensionName == field.DimensionName && it.Type.Equals(field.Type))
                    .FirstOrDefault();
                if (found == null)
                {
                    EditorData.Item.AddField(field);
                }                
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        DxContextMenu ContextMenuRef;
        private DashboardReportField DeleteItem_ { get; set; }
        private void DeleteItem(MouseEventArgs args, DashboardReportField item)
        {
            DeleteItem_ = item;
            ContextMenuRef.ShowAsync(args);
        }

        private async Task OnItemClick(ContextMenuItemClickEventArgs args)
        {
            string text = args.ItemInfo.Text;
            if (text.Equals(AppState["Delete"]) && DeleteItem_ != null)
            {
                await DeleteField(DeleteItem_);
            }
        }
        private async Task DeleteField(DashboardReportField field)
        {
            EditorData.Item.DeleteOrForgetField(field);
            await EditorDataChanged.InvokeAsync(EditorData);
            DeleteItem_ = null;
        }
        
    }
}

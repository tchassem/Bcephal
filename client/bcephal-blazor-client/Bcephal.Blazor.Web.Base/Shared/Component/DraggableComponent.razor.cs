using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
  public partial  class DraggableComponent<P> where P:Nameable
    {
        [Inject]
        private IJSRuntime JSRuntime { get; set; }
        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public IEnumerable<P> Data { get; set; }


        public List<List<object>> DataTems { get; set; }

        private bool canDataTems = false;

        [Parameter]
        public Action<P> SelectItemHandler { get; set; }

        [Parameter]
        public string Children { get; set; } = "Children";

        [Parameter]
        public bool Editable { get; set; } = true;

        private string Draggable { get; set; }

        private string DragClass { get; set; }  = "draggable";

        private string SerializeObject(P e)
        {
            string value = JsonConvert.SerializeObject(e);
            if (e is Models.Dimensions.Attribute)
            {
                DimensionType type = Models.Filters.DimensionType.ATTRIBUTE;
                value = $"{value.Substring(0, value.Length - 1)},\"Type\":\"{type}\"" + "}";
            }
            else
            if (e is Models.Dimensions.Measure)
            {
                DimensionType type = Models.Filters.DimensionType.MEASURE;
                value = $"{value.Substring(0, value.Length - 1)},\"Type\":\"{type}\"" + "}";
            }
            else
                if (e is Models.Dimensions.Period)
            {
                DimensionType type = Models.Filters.DimensionType.PERIOD;
                value = $"{value.Substring(0, value.Length - 1)},\"Type\":\"{type}\"" + "}";
            }
            return value;
        }

       
        private async void HandleDragStart(DragEventArgs argv,P e)
        {
            DragClass = "dragging ";
            SelectItemHandler?.Invoke(e);
            string data = JsonConvert.SerializeObject(e);
            await JSRuntime.InvokeVoidAsync("dragstart_handler", argv, data);
        }

        //protected override async Task OnAfterRenderAsync(bool firstRender)
        //{
        //    if (DataTems != null && canDataTems && DataTems.Count > 0)
        //    {
        //        //await JSRuntime.InvokeVoidAsync("gradEventConfigInit", DataTems[0][0]);
        //        await JSRuntime.InvokeVoidAsync("gradEventConfigInit", JsonConvert.SerializeObject(DataTems));
        //        // await JSRuntime.InvokeVoidAsync("dragstart_target", JsonConvert.SerializeObject(DataTems));
        //        canDataTems = false;
        //    }
        //    await base.OnAfterRenderAsync(firstRender);
        //}

    }
}

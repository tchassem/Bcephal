using Bcephal.Blazor.Web.Base.Services;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Dashboard
{
    public partial class HtmlEditorCustom : ComponentBase
    {
        [Inject]
        IJSRuntime JsRuntime { get; set; }
       
        [Inject]
        private AppState AppState { get; set; }
        [Parameter]
        public string UserMessage { get; set; }

        [Parameter]
        public EventCallback<string> UserMessageChanged { get; set; }

        ElementReference RefDiv;

        public string ClickedItem { get; set; } = "";

        bool Properties { get; set; }


        protected override async Task OnAfterRenderAsync(bool firstRender)
        { 
            await base.OnAfterRenderAsync(firstRender);
            await JsRuntime.InvokeVoidAsync("initHtmlEditor");
        }

        protected override  async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
        }

        async void OnItemClick(ToolbarItemClickEventArgs e)
        {
            ClickedItem = e.ItemName;
            if (!string.IsNullOrWhiteSpace(ClickedItem))
            {
                switch (ClickedItem)
                {
                    case "bold":
                        await JsRuntime.InvokeVoidAsync("boldtify");
                        break;
                    case "underline":
                        await JsRuntime.InvokeVoidAsync("underline");
                        break;
                    case "italic":
                        await JsRuntime.InvokeVoidAsync("italicize");
                        break;
                    case "color":
                        Properties = true;
                        break;
                    case "xx-small":
                        await JsRuntime.InvokeVoidAsync("changePolicySize", "xx-small");
                        break;
                    case "x-small":
                        await JsRuntime.InvokeVoidAsync("changePolicySize", "x-small");
                        break;
                    case "small":
                        await JsRuntime.InvokeVoidAsync("changePolicySize", "small");
                        break;
                    case "medium":
                        await JsRuntime.InvokeVoidAsync("changePolicySize", "medium");
                        break;
                    case "large":
                        await JsRuntime.InvokeVoidAsync("changePolicySize", "large");
                        break;
                    case "x-large":
                        await JsRuntime.InvokeVoidAsync("changePolicySize", "x-large");
                        break;
                    case "xx-large":
                        await JsRuntime.InvokeVoidAsync("changePolicySize", "xx-large");
                        break;
                    case "xxx-large":
                        await JsRuntime.InvokeVoidAsync("changePolicySize", "xxx-large");
                        break;
                    case "right":
                        await JsRuntime.InvokeVoidAsync("horizontalAlignment", "justifyRight");
                        break;
                    case "center":
                        await JsRuntime.InvokeVoidAsync("horizontalAlignment", "justifyCenter");
                        break;
                    case "left":
                        await JsRuntime.InvokeVoidAsync("horizontalAlignment", "justifyLeft");
                        break;
                    case "justify":
                        await JsRuntime.InvokeVoidAsync("horizontalAlignment", "justifyFull");
                        break;
                }
            }
          

        }

        private string TextColor { get; set; }
 
        async void UpdateColor(ChangeEventArgs e)
        {

            await JsRuntime.InvokeVoidAsync("changeColor", e?.Value?.ToString());
        }
    }
}

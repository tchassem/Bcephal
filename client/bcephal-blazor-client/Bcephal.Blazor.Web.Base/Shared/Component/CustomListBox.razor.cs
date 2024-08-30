using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.Extensions.Localization;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class CustomListBox<TData,TValue> : ComponentBase
    {
        [Parameter] public string HeaderTitle { get; set; }
        public string cssClass { get; set; }
        [Parameter] public string CssClass { get; set; } = "customlistbox-min-height w-auto mt-1 mr-1  border-0";
        [Parameter] public string FieldName { get; set; }
        [Parameter] public IEnumerable<TData> Data { get; set; }

        private IEnumerable<TValue> values_ { 
            get { return values; } 
            set {
                values = value;
                valuesChanged.InvokeAsync(value);
                } 
        }

        [Parameter] public IEnumerable<TValue> values { get; set; }
        [Parameter] public EventCallback<IEnumerable<TValue>> valuesChanged { get; set; }
        [Parameter] public EventCallback<MouseEventArgs> showContextMenu { get; set; }
        [Parameter] public RenderFragment<DxContextMenu> ContextMenu { get; set; }
        [Parameter] public bool Editable { get; set; } = true;

        public async Task showContextMenu_(MouseEventArgs args, dynamic context)
        {
            if (showContextMenu.HasDelegate)
            {
                if (context.GetType().Equals(typeof(TValue)))
                {
                    TValue va = (TValue)context;
                    values_ = new List<TValue>() { va };
                }
                await showContextMenu.InvokeAsync(args);
            }
        }
        protected override Task OnInitializedAsync()
        {
            if (!string.IsNullOrWhiteSpace(HeaderTitle))
            {
                cssClass = "card w-100 h-100 border-top-0";
            }
            else
            {
                cssClass = "card w-100 h-100 border-0";
            }
            return base.OnInitializedAsync();
        }
    }
}

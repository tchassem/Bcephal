
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Entities
{
    public partial class ModalAttributeValue : ComponentBase
    {
        [Parameter]
        public BrowserData SelectedItem { get; set; }

        [Parameter]
        public List<BrowserData> SelectedItemList { get; set; } = new List<BrowserData>();

        [Parameter]
        public EventCallback<List<BrowserData>> SelectedItemListChanged { get; set; }

        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public EventCallback<string> ValueTextChanged { get; set; }

        DxPopup DxPopupRef;

        [Parameter]
        public bool Show { get; set; }

        [Parameter]
        public EventCallback<bool> ShowChanged { get; set; }

        [Parameter]
        public long? AttributeId { get; set; }



        async Task ButtonClick()
        {
            if (SelectedItem != null && !String.IsNullOrWhiteSpace(SelectedItem.Name))
            {
                await ValueTextChanged.InvokeAsync(SelectedItem.Name);
                Show = false;
                await ShowChanged.InvokeAsync(Show);
                GC.SuppressFinalize(DxPopupRef);
                GC.SuppressFinalize(this);
            }

        }

        async Task ButtonClick_(GridRowClickEventArgs args)
        {
            await ButtonClick();
        }

        async Task ClosePopup()
        {
            await DxPopupRef.CloseAsync();
            Show = false;
            await ShowChanged.InvokeAsync(Show);
            GC.SuppressFinalize(DxPopupRef);
            GC.SuppressFinalize(this);
        }


        

    public string Key
{
    get
    {
        if (string.IsNullOrWhiteSpace(Key_))
        {
            Key_ = Guid.NewGuid().ToString("d");
        }
        return Key_;
    }
    set
    {
        Key_ = value;
    }
}

private string Key_ { get; set; }
    }
}

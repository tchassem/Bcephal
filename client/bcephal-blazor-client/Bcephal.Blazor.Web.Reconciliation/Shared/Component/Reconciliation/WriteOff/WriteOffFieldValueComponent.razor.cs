using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Reconciliation;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation.WriteOff
{
    public partial class WriteOffFieldValueComponent : ComponentBase
    {

        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public WriteOffFieldValue Item { get; set; }

        [Parameter]

        public long? AttributeId { get; set; }

        public BrowserData SelectedItem { get; set; }

        [Parameter]
        public EventCallback<WriteOffFieldValue> CallBackRemove { get; set; }

        [Parameter]
        public EventCallback<WriteOffFieldValue> CallBackAddorUpdate { get; set; }

        [Parameter]
        public Action AddRenderNext { get; set; }

        [Parameter]
        public List<BrowserData> SelectedItemList { get; set; }

        [Parameter]
        public EventCallback<List<BrowserData>> SelectedItemListChanged { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        [Parameter]
        public bool RemoveButton { get; set; }

        public bool RemoveButton_ { get; set; }


        public bool Show { get; set; }

        public string Value__ { get; set; }

        public string Value_
        {
            get
            {
                return String.IsNullOrEmpty(Item.StringValue) ? Value__ : Item.StringValue;
            }
            set
            {
                Value__ = SelectedItem.Name;
                Item.StringValue = SelectedItem.Name;
                CallBackAddorUpdate.InvokeAsync(Item);
            }
        }
        public void OpenDialog()
        {

            if(SelectedItem != null && !String.IsNullOrEmpty(SelectedItem.Name))
            {
                RemoveButton = true;
                RemoveButton_ = true;
            }
            Show = true;
            StateHasChanged();
        }

        public bool DefaultValue 
        {

            get 
            {
                return Item.DefaultValue;
            }
            set
            {
                Item.DefaultValue = value;
                CallBackAddorUpdate.InvokeAsync(Item);
            }
        }
    }
}

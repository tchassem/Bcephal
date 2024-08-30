using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Billing;
using Bcephal.Models.Billing.Model;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Models.Components
{
    public partial class BillingModelLabelTabComponent
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        [Parameter]
        public BillingModelEditorData BillingModelEditorData_ { get; set; }

        [Parameter]
        public EventCallback<BillingModelEditorData> BillingModelEditorData_Changed { get; set; }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            AppState.Update = true;
            return base.OnAfterRenderAsync(firstRender);
        }

        public bool IsSmallScreen { get; set; }

        private void NewBillingModelLabel()
        {
            BillingModelLabel label = new BillingModelLabel();
            foreach(string locale in BillingModelEditorData_.Locales)
            {
                BillingModelLabelValue value = new BillingModelLabelValue();
                value.Locale = locale;
                label.ValueListChangeHandler.AddNew(value);
            }
            BillingModelEditorData_.Item.AddLabel(label);
        }

        private void RemoveInvoiceLabel(BillingModelLabel item)
        {
            BillingModelEditorData_.Item.DeleteLabel(item);
            BillingModelEditorData_Changed.InvokeAsync(BillingModelEditorData_);
            AppState.Update = true;
        }

        private void UpdateInvoiceLabel(BillingModelLabel Item)
        {
            BillingModelEditorData_.Item.UpdateLabel(Item);
            BillingModelEditorData_Changed.InvokeAsync(BillingModelEditorData_);
            AppState.Update = true;
        }

    }
}

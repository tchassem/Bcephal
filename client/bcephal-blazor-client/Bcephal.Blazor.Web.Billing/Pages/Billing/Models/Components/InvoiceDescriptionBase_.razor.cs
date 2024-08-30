using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Billing.Model;
using Microsoft.AspNetCore.Components;
using System;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Models.Components
{
    public partial class InvoiceDescriptionBase_
    {
      
        [Parameter]
        public BillingModelEditorData BillingModelEditorData { get; set; }

        [Parameter]
        public BillingDescription BillingDescription { get; set; }

        [Parameter]
        public EventCallback<BillingDescription> DelDescriptionCallback { get; set; }

        [Parameter]
        public Action<BillingDescription> UpdateDescriptionCallback { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;
        private async void RemoveDescription()
        {
            await DelDescriptionCallback.InvokeAsync(BillingDescription);
        }

        private void UpdateDescription()
        {
            UpdateDescriptionCallback?.Invoke(BillingDescription);
        }
    }
}

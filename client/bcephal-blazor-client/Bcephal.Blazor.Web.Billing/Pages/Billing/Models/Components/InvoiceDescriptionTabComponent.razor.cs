using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Billing.Model;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Models.Components
{
    public partial class InvoiceDescriptionTabComponent : ComponentBase, IDisposable
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        [Parameter]
        public BillingModelEditorData BillingModelEditorData_ { get; set; }

        [Parameter]
        public EventCallback<BillingModelEditorData> BillingModelEditorData_Changed { get; set; }

        public BillingDescription BillingDescription_ { get; set; }

        public bool showModal { get; set; } = false;

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            AppState.Update = true;
            return base.OnAfterRenderAsync(firstRender);
        }

        private void AddDescription(BillingDescription item)
        {
            BillingModelEditorData_.Item.AddBillingDescription(item);
            BillingModelEditorData_Changed.InvokeAsync(BillingModelEditorData_);
        }

        private void RemoveDescription(BillingDescription item)
        {
            BillingModelEditorData_.Item.DeleteBillingDescription(item);
            BillingModelEditorData_Changed.InvokeAsync(BillingModelEditorData_);
        }

        public void EditDescription(BillingDescription billingDescription)
        {
            showModal = true;
            BillingDescription_ = billingDescription;
            BillingModelEditorData_Changed.InvokeAsync(BillingModelEditorData_);
        }

        public void UpdateDescription_(BillingDescription item)
        {
            BillingModelEditorData_.Item.UpdateBillingDescription(item);
            BillingModelEditorData_Changed.InvokeAsync(BillingModelEditorData_);
        }

        void IDisposable.Dispose()
        {
            AppState.Update = false;
        }

       
    }
}

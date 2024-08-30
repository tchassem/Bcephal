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
    public partial class BillingModelLabelItemComponent
    {
        [Inject]
        AppState AppState { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public BillingModelLabel ModelLabel { get; set; }

        public BillingModelLabelValue CurrentValue { get; set; }

        [Parameter]
        public BillingModelEditorData BillingModelEditorData { get; set; }

        [Parameter]
        public EventCallback<BillingModelLabel> DeleteInvoiceLabelCallBack { get; set; }

        [Parameter]
        public EventCallback<BillingModelLabel> UpdateLabelValueCallBack { get; set; }

        [Parameter]
        public EventCallback<BillingModelEditorData> BillingModelEditorDataChanged { get; set; }

        public bool showModal { get; set; }

        public string lang { get; set; } 

        public string Code__
        {
            get
            {
                if (ModelLabel != null)
                {
                    return ModelLabel.Code;
                }
                return null;
            }
            set
            {
                ModelLabel.Code = value;
                // async
                AppState.Update = true;
            }
        }

        private void ShowValue(BillingModelLabelValue value)
        {
            CurrentValue = value;
            showModal = true;
        }

        private void UpdateValue(BillingModelLabelValue value)
        {
            ModelLabel.UpdateValue(value);
            UpdateLabelValueCallBack.InvokeAsync(ModelLabel);
        }

        public void RemoveLabel()
        {
            DeleteInvoiceLabelCallBack.InvokeAsync(ModelLabel);
        }
    }
}

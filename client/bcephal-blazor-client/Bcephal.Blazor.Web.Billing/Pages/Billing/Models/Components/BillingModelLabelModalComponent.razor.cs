using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Billing;
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
    public partial class BillingModelLabelModalComponent
    {
        [Inject]
        public AppState AppState { get; set; }

        [Inject]
        public IJSRuntime JsRuntime { get; set; }

        [Parameter]
        public BillingModelLabelValue LabelValue { get; set; }

        [Parameter]
        public string Title { get; set; }

        [Parameter]
        public bool showModal { get; set; } = false;

        [Parameter]
        public EventCallback<bool> showModalChanged { get; set; }

        [Parameter]
        public EventCallback<BillingModelLabelValue> UpdateValuesCallBack { get; set; }

        [Parameter]
        public BillingModelEditorData BillingModelEditorData { get; set; }



        string cssclass2 = "d-flex flex-column justify-content-center align-items-center bc-h-60";
        string cssclass3 = "d-flex flex-column justify-content-center align-items-start ml-2";

        public string Value_
        {
            get
            {
                return LabelValue.Value;
            }
            set
            {
                LabelValue.Value = value;
                UpdateValuesCallBack.InvokeAsync(LabelValue);
                AppState.Update = true;

            }
        }

        private async void UpdateUserMessage(string element)
        {
            await JsRuntime.InvokeVoidAsync("InsertVariableInTextArea", "Textarea1", element);
            if (LabelValue.Value == null)
            {
                LabelValue.Value = "";
            }
            LabelValue.Value += " " + element;
            // await UpdateValuesCallBack.InvokeAsync(LabelValue);
            AppState.Update = true;
        }

        protected void OkHandler()
        {
            UpdateValuesCallBack.InvokeAsync(LabelValue);
            Close();
        }

        public void CancelHandler()
        {
            Close();
        }

        public void Close()
        {
            showModalChanged.InvokeAsync(false);
        }
    }
}

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
    public partial class BillingDescriptionModalComponent : ComponentBase, IDisposable
    {
        [Inject]
        public AppState AppState { get; set; }
       

        [Inject]
        public IJSRuntime JsRuntime { get; set; }

        [Parameter]
        public string Title { get; set; }
         [Parameter]
        public bool Editable { get; set; }

        [Parameter]
        public bool showDescriptionModal { get; set; } = false;

        [Parameter]
        public EventCallback<bool> showDescriptionModalChanged { get; set; }

        [Parameter]
        public BillingDescription BillingDescription { get; set; }

        [Parameter]
        public BillingModelEditorData BillingModelEditorData { get; set; }

        [Parameter]
        public EventCallback<BillingModelEditorData> BillingModelEditorDataChanged { get; set; }

        [Parameter]
        public EventCallback<BillingDescription> UpdateDescriptionCallBack { get; set; }

        [Parameter]
        public EventCallback<BillingDescription> AddDescriptionCallBack { get; set; }

        private bool IsNew { get; set; } = false;


        string cssclass2 = "d-flex flex-column justify-content-center align-items-center bc-h-60";
        string cssclass3 = "d-flex flex-column justify-content-center align-items-start ml-2";

        private async void UpdateUserMessage(string element)
        {
            await JsRuntime.InvokeVoidAsync("InsertVariableInTextArea", "Textarea1", element);
            if(BillingDescription.Description == null)
            {
                BillingDescription.Description = "";
            }
            BillingDescription.Description += " " + element;
        }

        public string lang
        {
            get
            {
                if (BillingDescription != null)
                {
                    return BillingDescription.Locale;
                }
                return null;
            }
            set
            {
                if (BillingDescription == null)
                {
                    BillingDescription = new BillingDescription();
                    IsNew = true;
                }

                List<BillingDescription> descriptions = BillingModelEditorData.Item.BillingDescriptionsListChangeHandler.Items.ToList();
                List<string> langs = new();
                descriptions.ForEach(des => {
                    langs.Add(des.Locale);
                });

                if (!langs.Contains(value))
                {
                    BillingDescription.Locale = value;
                }

            }
        }


       
        public string Description
        {
            get
            {
                if (BillingDescription != null)
                {
                    return BillingDescription.Description;
                }
                return null;
            }
            set
            {
                if (BillingDescription != null)
                {
                    BillingDescription.Description = value;
                }

            }
        }

        private void AddDescription(BillingDescription Item)
        {
            AddDescriptionCallBack.InvokeAsync(Item);
        }

        private void UpdateDescription(BillingDescription Item)
        {
            UpdateDescriptionCallBack.InvokeAsync(Item);
        }

        protected void OkHandler()
        {
            if (IsNew)
            {
                AddDescription(BillingDescription);
                IsNew = false;
            }
            else
            {
                UpdateDescription(BillingDescription);
            }
            Close();
        }

        public void CancelHandler()
        {
            Close();
        }

        public void Close()
        {
            showDescriptionModalChanged.InvokeAsync(false);
        }

        public void Dispose()
        {
            GC.SuppressFinalize(this);
        }
    }
}

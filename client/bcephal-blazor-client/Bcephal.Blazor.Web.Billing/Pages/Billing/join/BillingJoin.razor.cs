using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Billing.Pages.Billing.Models;
using Bcephal.Blazor.Web.Billing.Services;
using Bcephal.Blazor.Web.Reporting.Pages.Joins;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.join
{
    public partial class BillingJoin : JoinForm
    {
        [Inject]  BillingJoinService BillingJoinService { get; set; }

        public ModelBrowser Browser { get; set; }

        public bool OkBtnEnable = false;
        public EventCallback<bool> OkBtnEnableChanged { get; set; }

        public bool ShowModal_ = false;

        public override string GetBrowserUrl { get => null; set => base.GetBrowserUrl = null; }

        bool ShowModal
        {
            get => ShowModal_;
            set
            {
                ShowModal_ = value;
                if (RenderFormContentRef != null)
                {
                    RenderFormContentRef.StateHasChanged_();
                }
            }
        }

        protected override BillingJoinService GetService()
        {
            return BillingJoinService;
        }

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                AppState.CanRunAutoReconciliation = true;
                AppState.AutoReconciliationRunHander += ShowBrowserModal;
            }
            await   base.OnAfterRenderAsync(firstRender);
        }

        public override ValueTask DisposeAsync()
        {
            AppState.CanRunAutoReconciliation = false;
            AppState.AutoReconciliationRunHander -= ShowBrowserModal;
            return base.DisposeAsync();
        }

        public void RunSelectedModel()
        {
            Browser.RunBillingModel();
            ShowModal = false;
        }

        RenderFormContent RenderFormContentRef { get; set; }
        public void OkBtnHandleEnabled(bool val)
        {
            OkBtnEnable = val;
            OkBtnEnableChanged.InvokeAsync(OkBtnEnable);
        }

        private void ShowBrowserModal()
        {
            ShowModal = true;
        }
    }
}

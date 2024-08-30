using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Base.Shared.Scheduler;
using Bcephal.Blazor.Web.Reconciliation.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Reconciliation;
using Bcephal.Models.Schedulers;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Pages.Reconciliation
{
    //[RouteAttribute("/reconciliation-scheduled-auto-reco")]
    
    public class ScheduledAutoRecoBrowser : SchedulerBrowser_
    {
        public ScheduledAutoRecoBrowser()
        {
            Type = SchedulerTypes.AUTORECO;
        }

        protected override string NavLinkURI()
        { 
            return Route.RECONCILIATION_SCHEDULED_LOG_AUTO_BROWSER;   
        }

        public bool Editable
        {
            get => (AppState.PrivilegeObserver.ReconciliationAutoRecoSchedulerViewAllowed || AppState.PrivilegeObserver.ReconciliationAutoRecoSchedulerEditAllowed) && AppState.PrivilegeObserver.ReconciliationAutoRecoSchedulerAllowed;
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = true;
            EditButtonVisible = false;
            AppState.CanRestart =  !Editable && !AppState.IsDashboard;
            AppState.CanStop = !Editable && !AppState.IsDashboard;
        }

    }
}

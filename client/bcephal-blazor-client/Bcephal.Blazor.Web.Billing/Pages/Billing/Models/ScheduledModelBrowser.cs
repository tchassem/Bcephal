using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Base.Shared.Scheduler;
using Bcephal.Blazor.Web.Billing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Billing.Model;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Schedulers;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Models
{
    [RouteAttribute("/billing/scheduled-browser")]
    public class ScheduledModelBrowser : SchedulerBrowser_
    {
        public ScheduledModelBrowser()
        {
            Type = SchedulerTypes.BILLING;
        }
    }

}

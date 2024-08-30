using Bcephal.Blazor.Web.Base.Shared.Scheduler;
using Bcephal.Models.Schedulers;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation
{
    [RouteAttribute("/reconciliation-scheduled-auto-reco-log/{Id:long}")]
    public class ScheduledAutoRecoLogBrowser : SchedulerLogBrowser_
    {
        public ScheduledAutoRecoLogBrowser()
        {
            Type = SchedulerTypes.AUTORECO;
        }
    }
}

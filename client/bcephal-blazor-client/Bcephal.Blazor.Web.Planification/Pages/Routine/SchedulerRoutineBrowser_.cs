using Bcephal.Blazor.Web.Base.Shared.Scheduler;
using Bcephal.Models.Schedulers;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Planification.Pages.Routine
{
    //[RouteAttribute("browser-scheduler-routine/")]
    public class SchedulerRoutineBrowser_ : SchedulerBrowser_
    {
        public SchedulerRoutineBrowser_()
        {
            Type = SchedulerTypes.ROUTINE;
        }
    }
}

using Bcephal.Blazor.Web.Base.Shared.Scheduler;
using Bcephal.Models.Schedulers;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Planification.Shared.Routine
{
    [RouteAttribute("browser-scheduler-transformation-tree/")]
    public class SchedulerTransformationTreeBrowser_ : SchedulerBrowser_
    {
        public SchedulerTransformationTreeBrowser_()
        {
            Type = SchedulerTypes.TRANSFORMATIONTREE;
        }
    }
}

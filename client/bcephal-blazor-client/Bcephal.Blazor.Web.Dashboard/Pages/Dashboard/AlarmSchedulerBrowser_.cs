using Bcephal.Blazor.Web.Base.Shared.Scheduler;
using Bcephal.Models.Schedulers;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Pages.Dashboard
{
    public class AlarmSchedulerBrowser_ : SchedulerBrowser_
    {
        public AlarmSchedulerBrowser_()
        {
            Type = SchedulerTypes.ALARM;
        }
    }
}

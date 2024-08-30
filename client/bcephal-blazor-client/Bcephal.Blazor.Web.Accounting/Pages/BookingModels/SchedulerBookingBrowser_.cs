using Bcephal.Blazor.Web.Base.Shared.Scheduler;
using Bcephal.Models.Schedulers;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Accounting.Pages.BookingModels
{
     [RouteAttribute("browser-scheduler-booking/")]
    public class SchedulerBookingBrowser_: SchedulerBrowser_
    {
        public SchedulerBookingBrowser_() {

            Type = SchedulerTypes.BOOKING;

    }
    }
}

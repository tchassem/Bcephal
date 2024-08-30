using Bcephal.Blazor.Web.Base.Shared.Scheduler;
using Bcephal.Models.Schedulers;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.MultipleFileLoader
{
    [RouteAttribute("/browser-scheduled-files-loader-log")]
    public class ScheduledFileLoaderLogBrowser : SchedulerLogBrowser_
    {
        public ScheduledFileLoaderLogBrowser()
        {
            Type = SchedulerTypes.FILELOADER;
        }
    }
}

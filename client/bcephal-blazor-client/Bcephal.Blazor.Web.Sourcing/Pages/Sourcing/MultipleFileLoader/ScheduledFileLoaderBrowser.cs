using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Base.Shared.Scheduler;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Loaders;
using Bcephal.Models.Schedulers;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.MultipleFileLoader
{
    [RouteAttribute("/browser-scheduled-files-loader")]
    public class ScheduledFileLoaderBrowser : SchedulerBrowser_
    {
        public ScheduledFileLoaderBrowser()
        {
            Type = SchedulerTypes.FILELOADER;
        }

        protected override string NavLinkURI()
        {
            return Route.BROWSER_SCHEDULED_LOG_FILES_LOADER;
        }
    }
}

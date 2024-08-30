using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Schedulers
{
    public class SchedulerBrowserData : BrowserData
    {
        public bool CurrentlyExecuting { get; set; }

        public string Cron { get; set; }

        public string ProjectCode { get; set; }

        public string ProjectName { get; set; }

        public long ObjectId { get; set; }
    }
}

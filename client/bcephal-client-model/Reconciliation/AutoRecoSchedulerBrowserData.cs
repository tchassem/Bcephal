using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class AutoRecoSchedulerBrowserData : RecoBrowserData
    {
        public string ProjectName { get; set; }

        public string CurrentExecutionFirstTime { get; set; }

        public string CurrentExecutionRunTime { get; set; }

        public string PrevFireTime { get; set; }

        public string NextFireTime { get; set; }
    }
}

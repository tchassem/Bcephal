using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class RecoBrowserData : BrowserData
    {
        public bool CurrentlyExecuting { get; set; }

        public string RecoType { get; set; }

        public string User { get; set; }

        public string Mode { get; set; }

        public string Status { get; set; }

        public string End { get; set; }
    }
}

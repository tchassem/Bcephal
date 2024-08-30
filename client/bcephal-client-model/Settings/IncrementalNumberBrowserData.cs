using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Settings
{
    public class IncrementalNumberBrowserData : BrowserData
    {
        public long InitialValue { get; set; }

        public long IncrementValue { get; set; }

        public long MinimumValue { get; set; }

        public long MaximumValue { get; set; }

        public long CurrentValue { get; set; }

        public bool Cycle { get; set; }

        public int Size { get; set; }

        public string Prefix { get; set; }

        public string Suffix { get; set; }

    }
}

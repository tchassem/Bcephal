using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Grids
{
    public class GrilleBrowserData : BrowserData
    {
        public bool? Published { get; set; }
        public string Status { get; set; }

        public bool Editable { get; set; }

    }
}

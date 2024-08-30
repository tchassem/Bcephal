using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dimensions
{
    public class DimensionFormat
    {
        public int NbrOfDecimal { get; set; }

        public bool UsedSeparator { get; set; }

        public string DefaultFormat { get; set; }

        public DimensionFormat()
        {
            this.NbrOfDecimal = 2;
            this.UsedSeparator = true;
        }


    }
}

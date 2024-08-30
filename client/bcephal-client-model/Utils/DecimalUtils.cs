using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Utils
{
    public class DecimalUtils
    {
        public static decimal Trim(decimal? input)
        {
            return Trim(input.Value);
        }
        public static decimal Trim(decimal value)
        {
            return value / 1.000000000000000000000000000000000m;
        }
    }
}

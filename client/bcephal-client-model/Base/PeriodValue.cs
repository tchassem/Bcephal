using Bcephal.Models.Filters;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Base
{
    public class PeriodValue
    {

		public PeriodOperator DateOperator { get; set; }

		public DateTime? DateValue { get; set; }

		public string DateSign { get; set; }

		public int DateNumber { get; set; }

		public PeriodGranularity DateGranularity { get; set; }

    }
}

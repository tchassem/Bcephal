using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Grids.Filters
{
    public class FindReplaceFilter
    {

		public BrowserDataFilter Filter { get; set; }

		public GrilleColumn Column { get; set; }

		public bool IgnoreCase { get; set; }

		public bool ReplaceOnlyValuesCorrespondingToCondition { get; set; }

		public String Operation { get; set; }

		public String Criteria { get; set; }
		public decimal? MeasureCriteria { get; set; }

		public String ReplaceValue { get; set; }
		public decimal? MeasureReplaceValue { get; set; }

	}
}

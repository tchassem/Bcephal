using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing
{
    public class BillTemplate : MainObject
    {

		public string Code { get; set; }

		public string Description { get; set; }

		public string Repository { get; set; }

		public string MainFile { get; set; }

		public bool SystemTemplate { get; set; }

	}
}

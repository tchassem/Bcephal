using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing
{
    public class BillingRequest
    {
		public BillingRequestType Type { get; set; }

		public List<long> Ids { get; set; }

		public BillingRequest()
        {
            Ids = new List<long>();
        }
	}
}

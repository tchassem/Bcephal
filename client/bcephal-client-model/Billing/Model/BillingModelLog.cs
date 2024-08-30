using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelLog : Persistent
    {

		public string BillingName { get; set; }

		public long? BillingTypeId { get; set; }

		public string BillingTypeName { get; set; }

		public DateTime? EndDate { get; set; }

		public string Status { get; set; }

		public string Mode { get; set; }

		public string Username { get; set; }

		public long EventCount { get; set; }

		public long PeriodCount { get; set; }

		public long ClientCount { get; set; }

		public long GroupCount { get; set; }

		public long CategoryCount { get; set; }

		public long InvoiceCount { get; set; }

	}
}

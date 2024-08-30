using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing
{
    public class BillingRunOutcome : BrowserData
    {

        public string RunNumber { get; set; }

		public string Status { get; set; }

		[JsonIgnore]
		public InvoiceStatus InvoiceStatus
		{
			get { return !string.IsNullOrEmpty(Status) ? InvoiceStatus.GetByCode(Status) : null; }
			set { this.Status = value != null ? value.code : null; }
		}

		public string Mode { get; set; }

		[JsonIgnore]
		public RunModes RunModes
		{
			get { return !string.IsNullOrEmpty(Mode) ? RunModes.GetByCode(Mode) : null; }
			set { this.Mode = value != null ? value.code : null; }
		}

		public string Username { get; set; }

		public long InvoiceCount { get; set; }

		public decimal? InvoiceAmount { get; set; }

		public long CreditNoteCount { get; set; }

		public decimal? CreditNoteAmount { get; set; }

		public DateTime? PeriodFrom { get; set; }

		public DateTime? PeriodTo { get; set; }



	}
}

using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Invoices
{
    public class InvoiceLog : Persistent
    {

		public string Username { get; set; }

		public string Status { get; set; }

		[JsonIgnore]
		public InvoiceStatus InvoiceStatus
		{
			get { return !string.IsNullOrEmpty(Status) ? InvoiceStatus.GetByCode(Status) : null; }
			set { this.Status = value != null ? value.code : null; }
		}

		public string File { get; set; }

		public int Version { get; set; }

		public decimal? AmountWithoutVatBefore { get; set; }

		public decimal? VatAmountBefore { get; set; }

		public decimal? TotalAmountBefore { get; set; }

		public decimal? AmountWithoutVatAfter { get; set; }

		public decimal? VatAmountAfter { get; set; }

		public decimal? TotalAmountAfter { get; set; }

		public DateTime? Date { get; set; }

	}
}

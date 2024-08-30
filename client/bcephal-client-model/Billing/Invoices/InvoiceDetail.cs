using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Invoices
{
    public class InvoiceDetail : Persistent
    {

		public long? EventId { get; set; }

		public int Position { get; set; }

		public string Description { get; set; }

		public DateTime? Date { get; set; }

		public decimal? Quantity { get; set; }

		public string Unit { get; set; }

		public decimal? UnitCost { get; set; }

		public decimal? AmountWithoutVat { get; set; }

		public decimal? VatRate { get; set; }

		public string Currency { get; set; }


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is InvoiceDetail)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((InvoiceDetail)obj).Id)) return 0;
			if (this.Position.Equals(((InvoiceDetail)obj).Position))
			{
				return this.Description.CompareTo(((InvoiceDetail)obj).Description);
			}
			return this.Position.CompareTo(((InvoiceDetail)obj).Position);
		}

		public override string ToString()
		{
			return this.Description;
		}

	}
}

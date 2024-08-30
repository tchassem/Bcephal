using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Invoices
{
    public class AbstractInvoiceItem : Persistent
    {

		public int Position { get; set; }

		public string Category { get; set; }

		public string Description { get; set; }

		public bool IncludeQuantity { get; set; }

		public decimal? Quantity { get; set; }

		public string Unit { get; set; }

		public bool IncludeUnitCost { get; set; }

		public decimal? UnitCost { get; set; }

		public decimal? Amount { get; set; }

		public decimal? VatRate { get; set; }

		public string Currency { get; set; }


		public decimal? BillingQuantity { get; set; }

		public decimal? BillingUnitCost { get; set; }

		public decimal? BillingAmount { get; set; }

		public decimal? BillingVatRate { get; set; }


		public bool SubjectToVat { get; set; }

		public bool UseUnitCost { get; set; }


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is AbstractInvoiceItem)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((AbstractInvoiceItem)obj).Id)) return 0;
			if (this.Position.Equals(((AbstractInvoiceItem)obj).Position))
			{
				return this.Description.CompareTo(((AbstractInvoiceItem)obj).Description);
			}
			return this.Position.CompareTo(((AbstractInvoiceItem)obj).Position);
		}

		public override string ToString()
		{
			return this.Description;
		}

	}
}

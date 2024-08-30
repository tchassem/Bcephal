using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Invoices
{
    public class InvoiceItemInfo : Persistent
    {

		public string Name { get; set; }

		public DimensionType DimensionType { get; set; }

		public int Position { get; set; }

		public string StringValue { get; set; }

		public decimal? DecimalValue { get; set; }

		public DateTime? DateValue1 { get; set; }

		public DateTime? DateValue2 { get; set; }


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is InvoiceItemInfo)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((InvoiceItemInfo)obj).Id)) return 0;
			if (this.Position.Equals(((InvoiceItemInfo)obj).Position))
			{
				return this.Name.CompareTo(((InvoiceItemInfo)obj).Name);
			}
			return this.Position.CompareTo(((InvoiceItemInfo)obj).Position);
		}

		public override string ToString()
		{
			return this.Name;
		}

	}
}

using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelDriverGroupItem : Persistent
    {

		public string Value { get; set; }

		public bool ExcludeDriver { get; set; }

		public bool ExcludeUnitCost { get; set; }

		public int Position { get; set; }

		public BillingModelDriverGroupItem()
		{
			this.ExcludeDriver = true;
			this.ExcludeUnitCost = true;
		}

		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is BillingModelDriverGroupItem)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((BillingModelDriverGroupItem)obj).Id)) return 0;
			if (this.Position.Equals(((BillingModelDriverGroupItem)obj).Position))
			{
				return this.Value.CompareTo(((BillingModelDriverGroupItem)obj).Value);
			}
			return this.Position.CompareTo(((BillingModelDriverGroupItem)obj).Position);
		}

		public override string ToString()
		{
			return this.Value;
		}

	}
}

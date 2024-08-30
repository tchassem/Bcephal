using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelGroupingItem : Persistent
	{

		public string Name { get; set; }

		public int Position { get; set; }

		public long? AttributeId { get; set; }


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is BillingModelGroupingItem)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((BillingModelGroupingItem)obj).Id)) return 0;
			if (this.Position.Equals(((BillingModelGroupingItem)obj).Position))
			{
				return this.Name.CompareTo(((BillingModelGroupingItem)obj).Name);
			}
			return this.Position.CompareTo(((BillingModelGroupingItem)obj).Position);
		}

		public override string ToString()
		{
			return this.Name;
		}

	}
}

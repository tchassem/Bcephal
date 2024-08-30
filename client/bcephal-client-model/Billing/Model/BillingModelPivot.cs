using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelPivot : Persistent
    {

        public string Name { get; set; }

        public int Position { get; set; }

        public long? AttributeId { get; set; }

        public bool Show { get; set; }


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is BillingModelPivot)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((BillingModelPivot)obj).Id)) return 0;
			if (this.Position.Equals(((BillingModelPivot)obj).Position))
			{
				return this.Name.CompareTo(((BillingModelPivot)obj).Name);
			}
			return this.Position.CompareTo(((BillingModelPivot)obj).Position);
		}

		public override string ToString()
		{
			return this.Name;
		}

	}
}

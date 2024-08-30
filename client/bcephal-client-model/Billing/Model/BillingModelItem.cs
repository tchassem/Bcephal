using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelItem : Persistent
    {

        public string Name { get; set; }

        public int Position { get; set; }

		public string ItemType { get; set; }

		[JsonIgnore]
		public BillingModelItemType BillingModelItemType
		{
			get { return !string.IsNullOrEmpty(ItemType) ? BillingModelItemType.GetByCode(ItemType) : null; }
			set { this.ItemType = value != null ? value.code : null; }
		}


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is BillingModelItem)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((BillingModelItem)obj).Id)) return 0;
			if (this.Position.Equals(((BillingModelItem)obj).Position))
			{
				return this.Name.CompareTo(((BillingModelItem)obj).Name);
			}
			return this.Position.CompareTo(((BillingModelItem)obj).Position);
		}

		public override string ToString()
		{
			return this.Name;
		}

	}
}

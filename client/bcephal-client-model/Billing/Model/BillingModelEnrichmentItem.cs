using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelEnrichmentItem : Persistent
    {

		public decimal? DecimalValue { get; set; }

		public string StringValue { get; set; }

		public PeriodValue DateValue { get; set; }

		public DimensionType SourceType { get; set; }

		public long? SourceId { get; set; }

		public int Position { get; set; }


		public BillingModelEnrichmentItem()
        {
			this.DateValue = new PeriodValue();
			this.SourceType = DimensionType.BILLING_EVENT;

		}


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is BillingModelEnrichmentItem)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((BillingModelEnrichmentItem)obj).Id)) return 0;
			if (this.Position.Equals(((BillingModelEnrichmentItem)obj).Position))
			{
				return this.SourceId.Value.CompareTo(((BillingModelEnrichmentItem)obj).SourceId);
			}
			return this.Position.CompareTo(((BillingModelEnrichmentItem)obj).Position);
		}

	}
}

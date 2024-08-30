using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelParameter : Persistent
	{

		public string Name { get; set; }

		public int Position { get; set; }

		public DimensionType DimensionType { get; set; }

		public long? DimensionId { get; set; }

		public string DimensionName { get; set; }

		public string Functions { get; set; }

		[JsonIgnore]
		public MeasureFunctions MeasureFunction
		{
			get { return MeasureFunctions.GetByCode(this.Functions); }
			set { this.Functions = value != null ? value.code : null; }
		}

		public bool AddBillingFilters { get; set; }


		public BillingModelParameter()
        {
			AddBillingFilters = true;
			MeasureFunction = MeasureFunctions.SUM;
		}


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is BillingModelParameter)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((BillingModelParameter)obj).Id)) return 0;
			if (this.Position.Equals(((BillingModelParameter)obj).Position))
			{
				return this.Name.CompareTo(((BillingModelParameter)obj).Name);
			}
			return this.Position.CompareTo(((BillingModelParameter)obj).Position);
		}

		public override string ToString()
		{
			return this.Name;
		}

	}
}

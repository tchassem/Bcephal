using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class AutoRecoCommonDimension : Persistent
    {

		public DimensionType DimensionType { get; set; }

		public long? DimensionId { get; set; }

		public string DimensionName { get; set; }

		public string PeriodCondition { get; set; }
		[JsonIgnore]
		public AutoRecoPeriodCondition PeriodConditions
		{
			get
			{
				return string.IsNullOrEmpty(PeriodCondition) ? null : AutoRecoPeriodCondition.GetByCode(PeriodCondition);
			}
			set
			{
				this.PeriodCondition = value != null ? value.getCode() : null;
			}
		}

		public PeriodValue DateValue { get; set; }

	}
}

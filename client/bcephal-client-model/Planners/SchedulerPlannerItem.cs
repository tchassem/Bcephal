using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Planners
{
    public class SchedulerPlannerItem : Persistent
    {
		public bool Active { get; set; }

		public int Position { get; set; }

		public string Code { get; set; }

		public string Type { get; set; }

		[JsonIgnore]
		public SchedulerPlannerItemType ItemType
		{
			get { return SchedulerPlannerItemType.GetByCode(this.Type); }
			set { this.Type = value != null ? value.code : null; }
		}

		public long? ObjectId { get; set; }

		public string ObjectName { get; set; }

		public string Comparator { get; set; }

		public decimal? DecimalValue { get; set; }

		public int TemporisationValue { get; set; }

		public string TemporisationUnit { get; set; }
		[JsonIgnore]
		public SchedulerPlannerItemTemporisationUnit ItemTemporisationUnit
		{
			get { return SchedulerPlannerItemTemporisationUnit.GetByCode(this.TemporisationUnit); }
			set { this.TemporisationUnit = value != null ? value.code : null; }
		}

		public SchedulerPlannerItemAction Action1 { get; set; }

		public SchedulerPlannerItemAction Action2 { get; set; }

		public SchedulerPlannerItem()
        {
			this.Action1 = new SchedulerPlannerItemAction();
			this.Action2 = new SchedulerPlannerItemAction();
			this.Active = true;
		}

		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is SchedulerPlannerItem)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((SchedulerPlannerItem)obj).Id)) return 0;
			if (this.Position.Equals(((SchedulerPlannerItem)obj).Position))
			{
				//return this.ItemType.CompareTo(((SchedulerPlannerItem)obj).ItemType);
			}
			return this.Position.CompareTo(((SchedulerPlannerItem)obj).Position);
		}


	}
}

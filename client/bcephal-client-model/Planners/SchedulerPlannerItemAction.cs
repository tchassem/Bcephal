using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Planners
{
    public class SchedulerPlannerItemAction
    {
        public string Decision { get; set; }
		[JsonIgnore]
		public SchedulerPlannerItemDecision ItemDecision
		{
			get { return SchedulerPlannerItemDecision.GetByCode(this.Decision); }
			set { this.Decision = value != null ? value.code : null; }
		}

		public long? AlarmId { get; set; }

		public string GotoCode { get; set; }

		public SchedulerPlannerItemAction()
        {
			ItemDecision = SchedulerPlannerItemDecision.CONTINUE;

		}

	}

	
}
	
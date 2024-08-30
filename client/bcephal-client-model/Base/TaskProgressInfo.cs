using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Base
{
    public class TaskProgressInfo
    {

		public long? Id { get; set; }

		public string Name { get; set; }

		public bool TaskEnded { get; set; }

		public bool TaskInError { get; set; }

		public string Message { get; set; }

		public long StepCount { get; set; }

		public long CurrentStep { get; set; }

		public TaskProgressInfo CurrentSubTask { get; set; }

		[JsonIgnore]
		public string ProgressLabel
        {
            get
            {
				long rate = StepCount != 0 ? (Int32)(CurrentStep * 100 / StepCount) : 0;
				if(rate > 100)
                {
					rate = 100;
                }
				return "" + rate + " % (" + CurrentStep + "/" + StepCount + ")";

			}
        }

	}
}

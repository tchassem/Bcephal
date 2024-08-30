using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Schedulers
{
    public class SchedulerLogBrowserData : BrowserData
    {

		public string ObjectType { get; set; }

		public string Status { get; set; }

		public string Message { get; set; }

		public string Details { get; set; }

        public string EndDate { get; set; }

        [JsonIgnore]
        public DateTime? EndDateTime
        {
            get
            {
                try
                {
                    return DateUtils.ParseDateTime(EndDate);
                }
                catch (Exception)
                {
                    return null;
                }
            }
        }


    }
}

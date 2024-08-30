using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Planners
{
    public class SchedulerPlannerLogItemBrowserData : BrowserData
    {
        public int Position { get; set; }

        public string Type { get; set; }

        [JsonIgnore]
        public SchedulerPlannerItemType SchedulerPlannerType
        {
            get
            {
                return string.IsNullOrEmpty(Status) ? SchedulerPlannerItemType.ACTION : SchedulerPlannerItemType.GetByCode(Status);
            }
            set
            {
                this.Type = value != null ? value.getCode() : null;
            }
        }
        public long ObjectId { get; set; }
        public string ObjectName { get; set; }

        public string Message { get; set; }

        public string Status { get; set; }

        [JsonIgnore]
        public RunStatus RunStatus
        {
            get
            {
                return string.IsNullOrEmpty(Status) ? RunStatus.ENDED : RunStatus.GetByCode(Status);
            }
            set
            {
                this.Status = value != null ? value.getCode() : null;
            }
        }

        public string EndDate { get; set; }

        [JsonIgnore]
        public string EndDateTime
        {
            get
            {
                try
                {
                    return DateUtils.ParseDateTime(EndDate).ToString();
                }
                catch (Exception)
                {
                    return null;
                }
            }
        }
    }
}

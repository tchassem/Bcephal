using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;

namespace Bcephal.Kernel.Domain.Accounting
{
    public class BookingModelScheduledJob : BrowserData
    {

        public String projectName { get; set; }

        public String projectCode { get; set; }

        public string nextFireTime { get; set; }

        public bool currentlyExecuting { get; set; }

        public string previousFireTime;

        public string currentlyExecutionFireTime;

        public long? runTime;

        [JsonIgnore]
        public DateTime? nextFireTimeDateTime
        {
            get
            {
                if (string.IsNullOrWhiteSpace(nextFireTime))
                {
                    return null;
                }
                return DateUtils.ParseDateTime(nextFireTime);
            }
        }

        [JsonIgnore]
        public DateTime? previousFireTimeDateTime
        {
            get
            {
                if (string.IsNullOrWhiteSpace(previousFireTime))
                {
                    return null;
                }
                return DateUtils.ParseDateTime(previousFireTime);
            }
        }

        [JsonIgnore]
        public DateTime? currentlyExecutionFireTimeDateTime
        {
            get
            {
                if (string.IsNullOrWhiteSpace(currentlyExecutionFireTime))
                {
                    return null;
                }
                return DateUtils.ParseDateTime(currentlyExecutionFireTime);
            }
        }

    }
}

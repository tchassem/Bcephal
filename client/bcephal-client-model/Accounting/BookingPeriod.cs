using Newtonsoft.Json;
using System;

namespace Bcephal.Models.Base.Accounting
{
    public class BookingPeriod
    {

        public string name { get; set; }

        public string periodFrom { get; set; }
        [JsonIgnore]
        public DateTime? FromDateTime
        {
            get
            {
                try
                {
                    return DateUtils.Parse(periodFrom);
                }
                catch (Exception)
                {
                    return null;
                }
            }
            set
            {
                try
                {
                    periodFrom = DateUtils.Format(value);
                }
                catch (Exception)
                {
                    periodFrom = null;
                }
            }
        }

        public string periodTo { get; set; }
        [JsonIgnore]
        public DateTime? ToDateTime
        {
            get
            {
                try
                {
                    return DateUtils.Parse(periodTo);
                }
                catch (Exception)
                {
                    return null;
                }
            }
            set
            {
                try
                {
                    periodTo = DateUtils.Format(value);
                }
                catch (Exception)
                {
                    periodTo = null;
                }
            }
        }

    }
}

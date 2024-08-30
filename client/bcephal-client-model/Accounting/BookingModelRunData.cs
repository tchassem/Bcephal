using Newtonsoft.Json;
using System;
using System.Collections.Generic;

namespace Bcephal.Models.Base.Accounting
{
    public class BookingModelRunData
    {

        public int? bookingModelOid;
        public BookingModel bookingModel;
        public string mode;
        public List<long> postingEntryOids;

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

        public BookingModelRunData()
        {
            postingEntryOids = new List<long>(0);
        }

        public BookingModelRunData(BookingModel bookingModel) : this()
        {
            this.bookingModel = bookingModel;
            this.mode = "M";
        }

        public BookingModelRunData(int? bookingModelOid) : this()
        {
            this.bookingModelOid = bookingModelOid;
            this.mode = "M";
        }

    }
}

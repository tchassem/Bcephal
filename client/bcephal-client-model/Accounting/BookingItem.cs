
using Newtonsoft.Json;
using System;

namespace Bcephal.Models.Base.Accounting
{
    public class BookingItem : Persistent
    {
               
        private string accountId { get; set; }

        private string accountName { get; set; }

        public int position { get; set; }

        public decimal amount { get; set; }

        public decimal creditAmount { get; set; }

        public decimal debitAmount { get; set; }

        public string pivot { get; set; }

        public string bookingDate1 { get; set; }
        [JsonIgnore]
        public DateTime? BookingDate1
        {
            get
            {
                try
                {
                    return DateUtils.Parse(bookingDate1);
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
                    bookingDate1 = DateUtils.Format(value);
                }
                catch (Exception)
                {
                    bookingDate1 = null;
                }
            }
        }

        public string bookingDate2 { get; set; }
        [JsonIgnore]
        public DateTime? BookingDate2
        {
            get
            {
                try
                {
                    return DateUtils.Parse(bookingDate2);
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
                    bookingDate2 = DateUtils.Format(value);
                }
                catch (Exception)
                {
                    bookingDate2 = null;
                }
            }
        }

        public override string ToString()
        {
            return pivot;
        }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is BookingItem)) return 1;
            return this.position.CompareTo(((BookingItem)obj).position);
        }

    }
}

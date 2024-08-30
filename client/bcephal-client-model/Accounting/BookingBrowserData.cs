using Newtonsoft.Json;
using System;

namespace Bcephal.Models.Base.Accounting
{
    public class BookingBrowserData : BrowserData
    {

        public string entryDate { get; set; }
        [JsonIgnore]
        public DateTime? EntryDate
        {
            get
            {
                try
                {
                    return DateUtils.Parse(entryDate);
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
                    entryDate = DateUtils.Format(value);
                }
                catch (Exception)
                {
                    entryDate = null;
                }
            }
        }

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

        [JsonIgnore]
        public decimal BalanceAmount
        {
            get
            {
                return Math.Abs(creditAmount - debitAmount);
            }
        }

        [JsonIgnore]
        public string Sign
        {
            get
            {
                return creditAmount - debitAmount < 0 ? "D" : "C";
            }
        }

        public decimal creditAmount { get; set; }
        
        public decimal debitAmount { get; set; }
        
        public string username { get; set; }

        public bool manual { get; set; }

    }
}

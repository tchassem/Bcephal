using Newtonsoft.Json;
using System;
using System.Linq;

namespace Bcephal.Models.Base.Accounting
{
    public class Booking : Persistent
    {

        private string bookingId { get; set; }

        private string runNumber { get; set; }

        public string itemDate { get; set; }
        [JsonIgnore]
        public DateTime? EntryDate
        {
            get
            {
                try
                {
                    return DateUtils.Parse(itemDate);
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
                    itemDate = DateUtils.Format(value);
                }
                catch (Exception)
                {
                    itemDate = null;
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

        public ListChangeHandler<BookingItem> itemListChangeHandler { get; set; }


        public void AddItem(BookingItem item)
        {
            item.position = itemListChangeHandler.Items.Count;
            itemListChangeHandler.AddNew(item, true);
        }

        public void DeleteItem(BookingItem item)
        {
            itemListChangeHandler.AddDeleted(item, true);
            foreach (BookingItem child in itemListChangeHandler.Items)
            {
                if (child.position > item.position) child.position = child.position - 1;
            }
        }

        public void UpdateItem(BookingItem item)
        {
            itemListChangeHandler.AddUpdated(item);
        }

        public void ForgetItem(BookingItem item)
        {
            itemListChangeHandler.forget(item, true);
            foreach (BookingItem child in itemListChangeHandler.Items)
            {
                if (child.position > item.position) child.position = child.position - 1;
            }
        }

        public void DeleteOrForgetItem(BookingItem item)
        {
            if (item.Id.HasValue)
            {
                DeleteItem(item);
            }
            else
            {
                ForgetItem(item);
            }
        }

    }
}

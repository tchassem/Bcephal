
namespace Bcephal.Models.Base.Accounting
{
    public class BookingModelLogItemBrowserData : BrowserData
    {

        public decimal creditAmount { get; set; }

        public decimal debitAmount { get; set; }

        public decimal balanceAmount { get; set; }

        public string message { get; set; }

        public string status { get; set; }

        public long? accountCount { get; set; }

        public long? postingEntryCount { get; set; }

    }
}

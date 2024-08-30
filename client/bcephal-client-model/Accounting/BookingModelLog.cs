
namespace Bcephal.Models.Base.Accounting
{
    public class BookingModelLog : Persistent
    {

        public int? modelOid { get; set; }

        public string modelName { get; set; }
        
        public string endDate { get; set; }

        public string status { get; set; }

        public string mode { get; set; }

        public string user { get; set; }

        public long? accountCount { get; set; }

        public long? postingEntryCount { get; set; }

        public long? periodCount { get; set; }

        public long? bookingItemCount { get; set; }

    }
}

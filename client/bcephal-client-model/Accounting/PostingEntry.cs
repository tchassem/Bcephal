using Bcephal.Models.Accounting;
using Newtonsoft.Json;
using System;

namespace Bcephal.Models.Base.Accounting
{
    public class PostingEntry : Persistent
    {

        public string AccountId { get; set; }

        public string AccountName { get; set; }

        public decimal? Amount { get; set; }

        public int Position { get; set; }

        public string Comment { get; set; }

        public string Username { get; set; }

        public string sign { get; set; }
        [JsonIgnore]
        public PostingSign Sign
        {
            get { return !string.IsNullOrEmpty(sign) ? PostingSign.GetByCode(sign) : null; }
            set { this.sign = value != null ? value.code : null; }
        }

        public string status { get; set; }
        [JsonIgnore]
        public PostingStatus Status
        {
            get { return !string.IsNullOrEmpty(status) ? PostingStatus.GetByCode(status) : PostingStatus.DRAFT; }
            set { this.status = value != null ? value.code : null; }
        }
        [JsonIgnore]
        public int EditPosition { get; set; }

        [JsonIgnore]
        public decimal SignedAmount
        {
            get
            {
                if (!this.Amount.HasValue)
                {
                    this.Amount = decimal.Zero;
                }
                if (this.Sign == PostingSign.DEBIT)
                {
                    return decimal.Zero - this.Amount.Value;
                }
                return this.Amount.Value;
            }
        }


        

        public override String ToString()
        {
            return AccountId;
        }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is PostingEntry)) return 1;
            return this.Position.CompareTo(((PostingEntry)obj).Position);
        }
    }
}

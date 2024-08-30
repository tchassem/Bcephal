using Bcephal.Models.Base;
using Bcephal.Models.Base.Accounting;
using Newtonsoft.Json;
using System;
using System.Linq;

namespace Bcephal.Models.Accounting
{
    public class Posting : Persistent
    {

        public string name { get; set; }

        public decimal? balance { get; set; }

        public string comment { get; set; }

        public string username { get; set; }

        public string valueDate;

        [JsonIgnore]
        public DateTime ValueDate
        {
            get
            {
                try
                {
                    return DateUtils.Parse(valueDate);
                }
                catch (Exception)
                {
                    return DateTime.Today;
                }
            }
            set
            {
                try
                {
                    valueDate = DateUtils.Format(value);
                }
                catch (Exception)
                {
                    valueDate = null;
                }
            }
        }

        public string entryDate;

        [JsonIgnore]
        public DateTime EntryDate
        {
            get
            {
                try
                {
                    return DateUtils.Parse(entryDate);
                }
                catch (Exception)
                {
                    return DateTime.Today;
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

        
        public string status { get; set; }

        [JsonIgnore]
        public PostingStatus Status
        {
            get { return !string.IsNullOrEmpty(status) ? PostingStatus.GetByCode(status) : PostingStatus.DRAFT; }
            set { this.status = value != null ? value.code : null; }
        }

        public ListChangeHandler<PostingEntry> entryListChangeHandler { get; set; }


        public void AddEntry(PostingEntry entry)
        {
            entry.Position = entryListChangeHandler.Items.Count;
            entryListChangeHandler.AddNew(entry, true);
        }

        public void DeleteEntry(PostingEntry entry)
        {
            entryListChangeHandler.AddDeleted(entry, true);
            foreach (PostingEntry child in entryListChangeHandler.Items)
            {
                if (child.Position > entry.Position) child.Position = child.Position - 1;
            }
        }

        public void UpdateEntry(PostingEntry entry)
        {
            entryListChangeHandler.AddUpdated(entry);
        }

        public void ForgetEntry(PostingEntry entry)
        {
            entryListChangeHandler.forget(entry, true);
            foreach (PostingEntry child in entryListChangeHandler.Items)
            {
                if (child.Position > entry.Position) child.Position = child.Position - 1;
            }
        }

        public void DeleteOrForgetEntry(PostingEntry entry)
        {
            if (entry.Id.HasValue)
            {
                DeleteEntry(entry);
            }
            else
            {
                ForgetEntry(entry);
            }
        }


        public decimal[] Buildbalance()
        {
            decimal[] balance = new decimal[] { 0, 0, 0 };
            foreach (PostingEntry entry in entryListChangeHandler.Items)
            {
                if (!entry.Amount.HasValue)
                {
                    entry.Amount = decimal.Zero;
                }
                if (entry.Sign == PostingSign.DEBIT)
                {
                    balance[1] = balance[1] + entry.Amount.Value;
                }
                else
                {
                    balance[0] = balance[0] + entry.Amount.Value;
                }
            }
            balance[2] = balance[0] - balance[1];
            return balance;
        }

        public override String ToString()
        {
            return name;
        }
        

    }
}

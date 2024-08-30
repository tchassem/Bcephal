using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;

namespace Bcephal.Models.Accounting
{
    public class PostingBrowserData : BrowserData
    {

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
                    return DateTime.Now;
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
                    return DateTime.Now;
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

        public string comment { get; set; }

        public string username { get; set; }

        public string status { get; set; }
        [JsonIgnore]
        public PostingStatus Status
        {
            get { return !string.IsNullOrEmpty(status) ? PostingStatus.GetByCode(status) : null; }
            set { this.status = value != null ? value.code : null; }
        }

        public decimal? balance { get; set; }


    }
}

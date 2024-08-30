using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Invoices
{
    public class InvoiceBrowserData : BrowserData
    {
        public string Reference { get; set; }

        public string RunNumber { get; set; }

        public string ClientNumber { get; set; }

        public string ClientName { get; set; }

        public DateTime? InvoiceDate { get; set; }

        public DateTime? BillingDate { get; set; }

        public decimal? AmountWithoutVat { get; set; }

        public decimal? VatAmount { get; set; }

        public decimal? TotalAmount { get; set; }

        public int Version { get; set; }

        public bool ManuallyModified { get; set; }

        public string Status { get; set; }

        [JsonIgnore]
        public InvoiceStatus InvoiceStatus
        {
            get { return !string.IsNullOrEmpty(Status) ? InvoiceStatus.GetByCode(Status) : null; }
            set { this.Status = value != null ? value.code : null; }
        }

        public string MailStatus { get; set; }

        [JsonIgnore]
        public MailSendingStatus MailSendingStatus
        {
            get { return !string.IsNullOrEmpty(MailStatus) ? MailSendingStatus.GetByCode(MailStatus) : null; }
            set { this.MailStatus = value != null ? value.code : null; }
        }

    }
}

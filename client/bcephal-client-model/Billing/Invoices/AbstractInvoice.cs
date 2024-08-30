using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Invoices
{
    public abstract class AbstractInvoice : MainObject
    {

		public string Reference { get; set; }

		public string RunNumber { get; set; }

		public string OrderReference { get; set; }

		public string CommunicationMessage { get; set; }

		public string departmentNumber { get; set; }


		public string ClientContact { get; set; }

		public string ClientNumber { get; set; }

		public string ClientName { get; set; }

		public string ClientLegalForm { get; set; }

		public string ClientAdressStreet { get; set; }

		public string ClientAdressPostalCode { get; set; }

		public string ClientAdressCity { get; set; }

		public string ClientAdressCountry { get; set; }

		public string ClientVatNumber { get; set; }

		public string ClientEmail { get; set; }

		public string ClientPhone { get; set; }

		public string ClientLanguage { get; set; }


		public string BillingCompanyNumber { get; set; }

		public string BillingCompanyName { get; set; }

		public string BillingCompanyLegalForm { get; set; }

		public string BillingCompanyAdressStreet { get; set; }

		public string BillingCompanyAdressPostalCode { get; set; }

		public string BillingCompanyAdressCity { get; set; }

		public string BillingCompanyAdressCountry { get; set; }

		public string BillingCompanyVatNumber { get; set; }

		public string BillingCompanyEmail { get; set; }

		public string BillingCompanyPhone { get; set; }


		public DateTime? BillingDate { get; set; }

		public DateTime? InvoiceDate { get; set; }

		public DateTime? InvoiceDate2 { get; set; }

		public DateTime? DueDate { get; set; }


		public decimal? AmountWithoutVat { get; set; }

		public decimal? VatAmount { get; set; }


		public decimal? TotalAmount { get; set; }


		public decimal? BillingAmountWithoutVat { get; set; }

		public decimal? BillingVatAmount { get; set; }

		public string AmountUnit { get; set; }


		public string BillTemplateCode { get; set; }

		public int Version { get; set; }

		public bool ManuallyModified { get; set; }

		public bool subjectToVat { get; set; }

		public bool UseUnitCost { get; set; }

		public string PivotValues { get; set; }

		public bool OrderItems { get; set; }

		public bool OrderItemsAsc { get; set; }


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

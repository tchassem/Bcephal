using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services.Mocks
{
    public class CreditNoteModel : MainObject
    { 
        public string InvoiceDate { get; set; }

        public string Reference { get; set; }

        public string ClientNumero { get; set; }

        public string ClientName { get; set; }

        public Decimal AmountWithoutVat { get; set; }

        public Decimal VatAmount { get; set; }

        public Decimal TotalAmount { get; set; }

        public string Run { get; set; }

        public string PdfType { get; set; }

        public bool ManuallyModified { get; set; }


        public string Status { get; set; }
    }
}

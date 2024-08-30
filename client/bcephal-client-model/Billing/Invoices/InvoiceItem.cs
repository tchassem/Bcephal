using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Invoices
{
    public class InvoiceItem : AbstractInvoiceItem
    {

        public ListChangeHandler<InvoiceItemInfo> infoListChangeHandler { get; set; }

    }
}

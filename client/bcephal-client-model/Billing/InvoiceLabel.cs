using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing
{
    public class InvoiceLabel : Persistent
    {
        public int Position { get; set; }

        public string Code { get; set; } 

        public Dictionary<string, string> Values { get; set; }
    }
}

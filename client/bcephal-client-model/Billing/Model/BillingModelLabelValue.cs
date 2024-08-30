using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelLabelValue : Persistent
    {

        public string Locale { get; set; }

        public string Value { get; set; }

        public int Position { get; set; }
    }
}

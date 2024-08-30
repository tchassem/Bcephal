using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
   public class BillingDescription : Persistent
    {
        public int Position { get; set; }
        public string Locale { get; set; }
        public string Description { get; set; }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class PartialRecoItem
    {
        public long? Id { get; set; }
        public bool Left { get; set; }
        public decimal? Amount { get; set; }
        public decimal? ReconciliatedAmount { get; set; }
        public decimal? RemainningAmount { get; set; }

    }
}

using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class ReconciliationModelColumns
    {

        public ObservableCollection<GrilleColumn> LeftColumns { get; set; }
        public ObservableCollection<GrilleColumn> RightColumns { get; set; }

    }
}

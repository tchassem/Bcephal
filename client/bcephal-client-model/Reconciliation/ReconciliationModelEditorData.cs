using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class ReconciliationModelEditorData : EditorData<ReconciliationModel>
    {

        public ObservableCollection<Nameable> Sequences { get; set; }

        public long? DebitCreditAttributeId { get; set; }

        public string DebitValue { get; set; }

        public string CreditValue { get; set; }

    }
}

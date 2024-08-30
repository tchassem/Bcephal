using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class AutoRecoEditorData : EditorData<AutoReco>
    {

        public ObservableCollection<Nameable> ReconciliationModels { get; set; }

        public ObservableCollection<Nameable> Routines { get; set; }

    }
}

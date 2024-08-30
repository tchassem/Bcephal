using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelEditorData : EditorData<BillingModel>
    {

        public ObservableCollection<string> Variables { get; set; }
        public ObservableCollection<string> Locales { get; set; }
        public ObservableCollection<Nameable> Sequences { get; set; }

        public ObservableCollection<GrilleColumn> RepositoryColumns { get; set; }

    }
}

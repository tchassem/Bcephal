using Bcephal.Models.Grids;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Base.Accounting
{
    public class AccountingEditorData : EditorData<Grille>
    {
        public int? measureOid { get; set; }
        public int? signOid { get; set; }
        public string creditValue { get; set; }
        public string debitValue { get; set; }

    }
}


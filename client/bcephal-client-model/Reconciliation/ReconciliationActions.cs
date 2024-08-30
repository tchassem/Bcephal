using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class ReconciliationActions
    {

        public static ReconciliationActions RECONCILIATION = new ReconciliationActions("RECONCILIATION", "Reconciliation");
        public static ReconciliationActions RESET = new ReconciliationActions("RESET", "Reset");

        public String label;
        public String code;

        public ReconciliationActions(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String getCode()
        {
            return code;
        }


        public override String ToString()
        {
            return label;
        }

        public static ReconciliationActions GetByCode(String code)
        {
            if (code == null) return null;
            if (RECONCILIATION.code.Equals(code)) return RECONCILIATION;
            if (RESET.code.Equals(code)) return RESET;
            return null;
        }

        public static ObservableCollection<ReconciliationActions> GetMethods()
        {
            ObservableCollection<ReconciliationActions> conditions = new ObservableCollection<ReconciliationActions>();
            conditions.Add(RECONCILIATION);
            conditions.Add(RESET);
            return conditions;
        }

    }
}

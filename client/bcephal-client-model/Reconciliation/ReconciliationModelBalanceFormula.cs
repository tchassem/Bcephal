using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class ReconciliationModelBalanceFormula
    {
        public static ReconciliationModelBalanceFormula LEFT_MINUS_RIGHT = new ReconciliationModelBalanceFormula("LEFT_MINUS_RIGHT", "Left - Rigt = 0");
        public static ReconciliationModelBalanceFormula LEFT_PLUS_RIGHT = new ReconciliationModelBalanceFormula("LEFT_PLUS_RIGHT", "Left + Rigt = 0");

        public String label;
        public String code;

        public ReconciliationModelBalanceFormula(String code, String label)
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

        public static ReconciliationModelBalanceFormula GetByCode(String code)
        {
            if (code == null) return null;
            if (LEFT_MINUS_RIGHT.code.Equals(code)) return LEFT_MINUS_RIGHT;
            if (LEFT_PLUS_RIGHT.code.Equals(code)) return LEFT_PLUS_RIGHT;
            return null;
        }

        public static ObservableCollection<ReconciliationModelBalanceFormula> GetMethods()
        {
            ObservableCollection<ReconciliationModelBalanceFormula> conditions = new ObservableCollection<ReconciliationModelBalanceFormula>();
            conditions.Add(LEFT_MINUS_RIGHT);
            conditions.Add(LEFT_PLUS_RIGHT);
            return conditions;
        }

    }
}

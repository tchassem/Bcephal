using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class AutoRecoCondition
    {

        public static AutoRecoCondition BALANCE_IS_ZERO = new AutoRecoCondition("BALANCE_IS_ZERO", "Balance = 0");
        public static AutoRecoCondition BALANCE_AMOUNT_ITERVAL = new AutoRecoCondition("BALANCE_AMOUNT_ITERVAL", "Balance is superior or inferior to an amount");
        public static AutoRecoCondition BALANCE_AMOUNT_PERCENTAGE = new AutoRecoCondition("BALANCE_AMOUNT_PERCENTAGE", "Balance is superior or inferior to a percentage of reconciliated amount");

        public String label;
        public String code;

        public AutoRecoCondition(String code, String label)
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

        public static AutoRecoCondition GetByCode(String code)
        {
            if (code == null) return null;
            if (BALANCE_IS_ZERO.code.Equals(code)) return BALANCE_IS_ZERO;
            if (BALANCE_AMOUNT_ITERVAL.code.Equals(code)) return BALANCE_AMOUNT_ITERVAL;
            if (BALANCE_AMOUNT_PERCENTAGE.code.Equals(code)) return BALANCE_AMOUNT_PERCENTAGE;
            return null;
        }

        public static ObservableCollection<AutoRecoCondition> GetConditions()
        {
            ObservableCollection<AutoRecoCondition> conditions = new ObservableCollection<AutoRecoCondition>();
            conditions.Add(BALANCE_IS_ZERO);
            conditions.Add(BALANCE_AMOUNT_ITERVAL);
            conditions.Add(BALANCE_AMOUNT_PERCENTAGE);
            return conditions;
        }

    }
}

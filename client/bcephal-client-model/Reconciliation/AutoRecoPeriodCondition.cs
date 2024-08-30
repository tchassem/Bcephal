using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class AutoRecoPeriodCondition
    {

        public static AutoRecoPeriodCondition SAME_DAY = new AutoRecoPeriodCondition("SAME_DAY", "Same day");
        public static AutoRecoPeriodCondition SAME_WEEK = new AutoRecoPeriodCondition("SAME_WEEK", "Same week");
        public static AutoRecoPeriodCondition SAME_MONTH = new AutoRecoPeriodCondition("SAME_MONTH", "Same month");
        public static AutoRecoPeriodCondition SAME_YEAR = new AutoRecoPeriodCondition("SAME_YEAR", "Same year");

        public String label;
        public String code;

        public AutoRecoPeriodCondition(String code, String label)
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

        public static AutoRecoPeriodCondition GetByCode(String code)
        {
            if (code == null) return null;
            if (SAME_DAY.code.Equals(code)) return SAME_DAY;
            if (SAME_WEEK.code.Equals(code)) return SAME_WEEK;
            if (SAME_MONTH.code.Equals(code)) return SAME_MONTH;
            if (SAME_YEAR.code.Equals(code)) return SAME_YEAR;
            return null;
        }

        public static ObservableCollection<AutoRecoPeriodCondition> GetMethods()
        {
            ObservableCollection<AutoRecoPeriodCondition> conditions = new ObservableCollection<AutoRecoPeriodCondition>();
            conditions.Add(SAME_DAY);
            conditions.Add(SAME_WEEK);
            conditions.Add(SAME_MONTH);
            conditions.Add(SAME_YEAR);
            return conditions;
        }

    }
}

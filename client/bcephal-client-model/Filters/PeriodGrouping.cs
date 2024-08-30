using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Filters
{
    public class PeriodGrouping
    {

        public static PeriodGrouping DAY_OF_WEEK = new PeriodGrouping("DAY_OF_WEEK", "Day of week");
        public static PeriodGrouping DAY_OF_MONTH = new PeriodGrouping("DAY_OF_MONTH", "Day of month");
        public static PeriodGrouping WEEK = new PeriodGrouping("WEEK", "Week");
        public static PeriodGrouping MONTH = new PeriodGrouping("MONTH", "Month");
        public static PeriodGrouping QUARTER = new PeriodGrouping("QUARTER", "Quarter");
        public static PeriodGrouping YEAR = new PeriodGrouping("YEAR", "Year");

        public String label;
        public String code;


        public PeriodGrouping(String code, String label)
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

        public static PeriodGrouping GetByCode(String code)
        {
            if (code == null) return null;
            if (DAY_OF_WEEK.code.Equals(code)) return DAY_OF_WEEK;
            if (DAY_OF_MONTH.code.Equals(code)) return DAY_OF_MONTH;
            if (WEEK.code.Equals(code)) return WEEK;
            if (MONTH.code.Equals(code)) return MONTH;
            if (QUARTER.code.Equals(code)) return QUARTER;
            if (YEAR.code.Equals(code)) return YEAR;
            return null;
        }

        public static ObservableCollection<PeriodGrouping> GetGroupings()
        {
            ObservableCollection<PeriodGrouping> conditions = new ObservableCollection<PeriodGrouping>();
            conditions.Add(DAY_OF_WEEK);
            conditions.Add(DAY_OF_MONTH);
            conditions.Add(WEEK);
            conditions.Add(MONTH);
            conditions.Add(QUARTER);
            conditions.Add(YEAR);
            return conditions;
        }


    }
}

using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dimensions
{
    public class CalendarDayType
    {
        public static CalendarDayType FIXED_DATE = new CalendarDayType("FIXED_DATE", "Specific date");
        public static CalendarDayType DAY_OF_WEEK = new CalendarDayType("DAY_OF_WEEK", "Day of week");
        public static CalendarDayType DAY_OF_MONTH = new CalendarDayType("DAY_OF_MONTH", "Day of month");
        public static CalendarDayType MONTH = new CalendarDayType("MONTH", "Month");
        public static CalendarDayType YEAR = new CalendarDayType("YEAR", "Year");

        public String label;
        public String code;


        public CalendarDayType(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public bool IsFixedDate()
        {
            return this == FIXED_DATE;
        }

        public bool IsDayOfWeek()
        {
            return this == DAY_OF_WEEK;
        }

        public bool IsDayOfMonth()
        {
            return this == DAY_OF_MONTH;
        }

        public bool IsMonth()
        {
            return this == MONTH;
        }

        public bool IsYear()
        {
            return this == YEAR;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static CalendarDayType GetByCode(String code)
        {
            if (code == null) return null;
            if (FIXED_DATE.code.Equals(code)) return FIXED_DATE;
            if (DAY_OF_WEEK.code.Equals(code)) return DAY_OF_WEEK;
            if (DAY_OF_MONTH.code.Equals(code)) return DAY_OF_MONTH;
            if (MONTH.code.Equals(code)) return MONTH;
            if (YEAR.code.Equals(code)) return YEAR;
            return FIXED_DATE;
        }

        public static ObservableCollection<CalendarDayType> GetTypes()
        {
            ObservableCollection<CalendarDayType> types = new ObservableCollection<CalendarDayType>();
            types.Add(DAY_OF_MONTH);
            types.Add(DAY_OF_WEEK);
            types.Add(MONTH);
            types.Add(YEAR);
            types.Add(FIXED_DATE);
            return types;
        }

    }
}


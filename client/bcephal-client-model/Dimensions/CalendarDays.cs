using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dimensions
{
    public class CalendarDays
    {

        public static CalendarDays MONDAY = new CalendarDays(1, "MONDAY");
        public static CalendarDays TUESDAY = new CalendarDays(2, "TUESDAY");
        public static CalendarDays WEDNESDAY = new CalendarDays(3, "WEDNESDAY");
        public static CalendarDays THURSDAY = new CalendarDays(4, "THURSDAY");
        public static CalendarDays FRIDAY = new CalendarDays(5, "FRIDAY");
        public static CalendarDays SATURDAY = new CalendarDays(6, "SATURDAY");
        public static CalendarDays SUNDAY = new CalendarDays(7, "SUNDAY");

        public String label;
        public int? code;


        public CalendarDays(int? code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static CalendarDays GetByCode(int? code, CalendarDayType type)
        {
            if (!code.HasValue) return null;
            if (type != null && type.IsDayOfWeek())
            {
                if (MONDAY.code == code) return MONDAY;
                if (TUESDAY.code == code) return TUESDAY;
                if (WEDNESDAY.code == code) return WEDNESDAY;
                if (THURSDAY.code == code) return THURSDAY;
                if (FRIDAY.code == code) return FRIDAY;
                if (SATURDAY.code == code) return SATURDAY;
                if (SUNDAY.code == code) return SUNDAY;
                return null;
            }
            else
            {
                return new CalendarDays(code, code.ToString());
            }
        }

        public static ObservableCollection<CalendarDays> GetDaysOfWeek()
        {
            ObservableCollection<CalendarDays> types = new ObservableCollection<CalendarDays>();
            types.Add(MONDAY);
            types.Add(TUESDAY);
            types.Add(WEDNESDAY);
            types.Add(THURSDAY);
            types.Add(FRIDAY);
            types.Add(SATURDAY);
            types.Add(SUNDAY);
            return types;
        }

    }
}


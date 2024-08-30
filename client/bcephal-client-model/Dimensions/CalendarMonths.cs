using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dimensions
{
    public class CalendarMonths
    {
        public static CalendarMonths NULL = new CalendarMonths(null, "");
        public static CalendarMonths JANUARY = new CalendarMonths(1, "JANUARY");
        public static CalendarMonths FEBUARY = new CalendarMonths(2, "FEBUARY");
        public static CalendarMonths MARCH = new CalendarMonths(3, "MARCH");
        public static CalendarMonths APRIL = new CalendarMonths(4, "APRIL");
        public static CalendarMonths MAY = new CalendarMonths(5, "MAY");
        public static CalendarMonths JUNE = new CalendarMonths(6, "JUNE");
        public static CalendarMonths JULY = new CalendarMonths(7, "JULY");
        public static CalendarMonths AUGUST = new CalendarMonths(8, "AUGUST");
        public static CalendarMonths SEPTEMBER = new CalendarMonths(9, "SEPTEMBER");
        public static CalendarMonths OCTOBER = new CalendarMonths(10, "OCTOBER");
        public static CalendarMonths NOVEMBER = new CalendarMonths(11, "NOVEMBER");
        public static CalendarMonths DECEMBER = new CalendarMonths(12, "DECEMBER");

        public String label;
        public int? code;


        public CalendarMonths(int? code, String label)
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

        public static CalendarMonths GetByCode(int? code)
        {
            if (!code.HasValue) return NULL;
            if (JANUARY.code == code) return JANUARY;
            if (FEBUARY.code == code) return FEBUARY;
            if (MARCH.code == code) return MARCH;
            if (APRIL.code == code) return APRIL;
            if (MAY.code == code) return MAY;
            if (JUNE.code == code) return JUNE;
            if (JULY.code == code) return JULY;
            if (AUGUST.code == code) return AUGUST;
            if (SEPTEMBER.code == code) return SEPTEMBER;
            if (OCTOBER.code == code) return OCTOBER;
            if (NOVEMBER.code == code) return NOVEMBER;
            if (DECEMBER.code == code) return DECEMBER;
            return NULL;
        }


        public static ObservableCollection<CalendarMonths> GetMonths()
        {
            ObservableCollection<CalendarMonths> types = new ObservableCollection<CalendarMonths>();
            types.Add(NULL);
            types.Add(JANUARY);
            types.Add(FEBUARY);
            types.Add(MARCH);
            types.Add(APRIL);
            types.Add(MAY);
            types.Add(JUNE);
            types.Add(JULY);
            types.Add(AUGUST);
            types.Add(SEPTEMBER);
            types.Add(OCTOBER);
            types.Add(NOVEMBER);
            types.Add(DECEMBER);
            return types;
        }

    }
}

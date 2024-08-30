using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Grids
{
    public class DateFormats
    {

        public static DateFormats SHORT = new DateFormats("d", "Short date");
        public static DateFormats LONG = new DateFormats("D", "Long date");
        public static DateFormats CUSTOM = new DateFormats("CUSTOM", "Custom");

        public String label;
        public String code;


        public DateFormats(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String getCode()
        {
            return code;
        }

        public bool IsShort { get { return this == SHORT; } }

        public bool IsLong { get { return this == LONG; } }

        public bool IsCustom { get { return this == CUSTOM; } }

        public override String ToString()
        {
            return label;
        }

        public static DateFormats GetByCode(String code)
        {
            if (code == null) return SHORT;
            if (SHORT.code.Equals(code)) return SHORT;
            if (LONG.code.Equals(code)) return LONG;
            if (CUSTOM.code.Equals(code)) return CUSTOM;
            return null;
        }

        public static ObservableCollection<DateFormats> GetFormats()
        {
            ObservableCollection<DateFormats> conditions = new ObservableCollection<DateFormats>();
            conditions.Add(CUSTOM);
            conditions.Add(LONG);
            conditions.Add(SHORT);
            return conditions;
        }



    }
}

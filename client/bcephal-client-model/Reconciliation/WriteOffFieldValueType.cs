using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class WriteOffFieldValueType
    {

        public static WriteOffFieldValueType LEFT_SIDE = new WriteOffFieldValueType("LEFT_SIDE", "Left Side");
        public static WriteOffFieldValueType RIGHT_SIDE = new WriteOffFieldValueType("RIGHT_SIDE", "Right Side");
        public static WriteOffFieldValueType CUSTOM = new WriteOffFieldValueType("CUSTOM", "Custom");
        public static WriteOffFieldValueType CUSTOM_DATE = new WriteOffFieldValueType("CUSTOM_DATE", "Custom Date");
        public static WriteOffFieldValueType TODAY = new WriteOffFieldValueType("TODAY", "Today");
        public static WriteOffFieldValueType FREE = new WriteOffFieldValueType("FREE", "Free");
        public static WriteOffFieldValueType LIST_OF_VALUES = new WriteOffFieldValueType("LIST_OF_VALUES", "List of values");

        public String label;
        public String code;

        public WriteOffFieldValueType(String code, String label)
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

        public static WriteOffFieldValueType GetByCode(String code)
        {
            if (code == null) return null;
            if (LEFT_SIDE.code.Equals(code)) return LEFT_SIDE;
            if (RIGHT_SIDE.code.Equals(code)) return RIGHT_SIDE;
            if (CUSTOM.code.Equals(code)) return CUSTOM;
            if (CUSTOM_DATE.code.Equals(code)) return CUSTOM_DATE;
            if (TODAY.code.Equals(code)) return TODAY;
            if (FREE.code.Equals(code)) return FREE;
            if (LIST_OF_VALUES.code.Equals(code)) return LIST_OF_VALUES;
            return null;
        }

        public static ObservableCollection<WriteOffFieldValueType> GetMethods()
        {
            ObservableCollection<WriteOffFieldValueType> conditions = new ObservableCollection<WriteOffFieldValueType>();
            conditions.Add(LEFT_SIDE);
            conditions.Add(RIGHT_SIDE);
            conditions.Add(CUSTOM);
            conditions.Add(CUSTOM_DATE);
            conditions.Add(TODAY);
            conditions.Add(FREE);
            conditions.Add(LIST_OF_VALUES);
            return conditions;
        }

    }
}

using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class ReconciliationModelSide
    {

        public static ReconciliationModelSide LEFT = new ReconciliationModelSide("LEFT", "Left");
        public static ReconciliationModelSide RIGHT = new ReconciliationModelSide("RIGHT", "Right");
        public static ReconciliationModelSide CUSTOM = new ReconciliationModelSide("CUSTOM", "Custom");

        public String label;
        public String code;

        public ReconciliationModelSide(String code, String label)
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

        public bool IsLeft()
        {
            return this == LEFT;
        }

        public bool IsRight()
        {
            return this == RIGHT;
        }

        public bool IsCustom()
        {
            return this == CUSTOM;
        }

        public static ReconciliationModelSide GetByCode(String code)
        {
            if (code == null) return null;
            if (LEFT.code.Equals(code)) return LEFT;
            if (RIGHT.code.Equals(code)) return RIGHT;
            if (CUSTOM.code.Equals(code)) return CUSTOM;
            return null;
        }

        public static ObservableCollection<ReconciliationModelSide> GetSides()
        {
            ObservableCollection<ReconciliationModelSide> conditions = new ObservableCollection<ReconciliationModelSide>();
            conditions.Add(LEFT);
            conditions.Add(RIGHT);
            conditions.Add(CUSTOM);
            return conditions;
        }

    }
}

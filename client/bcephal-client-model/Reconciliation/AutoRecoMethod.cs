using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class AutoRecoMethod
    {

        public static AutoRecoMethod ONE_ON_ONE = new AutoRecoMethod("ONE_ON_ONE", "One on one (Only 1 'Left' element matches only 1 'Right' element)");
        public static AutoRecoMethod CUMULATED_RIGHT = new AutoRecoMethod("CUMULATED_RIGHT", "Cumulated right (Only 1 'Left' element matches Cumulated 'Right' elements)");
        public static AutoRecoMethod CUMULATED_LEFT = new AutoRecoMethod("CUMULATED_LEFT", "Cumulated left (Cumulated 'Left' elements matches only 1 'Right' element)");
        public static AutoRecoMethod BOTH_CUMULATED = new AutoRecoMethod("BOTH_CUMULATED", "Both cumulated (Cumulated 'Left' elements matches Cumulated 'Right' elements)");
        public static AutoRecoMethod ZERO_AMOUNT = new AutoRecoMethod("ZERO_AMOUNT", "Zero amount ('Left' or 'Right' elements with value = 0)");
        public static AutoRecoMethod LEFT_SIDE = new AutoRecoMethod("LEFT_SIDE", "Left side only");
        public static AutoRecoMethod RIGHT_SIDE = new AutoRecoMethod("RIGHT_SIDE", "Right side only");
        public static AutoRecoMethod NEUTRALIZATION = new AutoRecoMethod("NEUTRALIZATION", "Neutralization");

        public String label;
        public String code;

        public AutoRecoMethod(String code, String label)
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

        public static AutoRecoMethod GetByCode(String code)
        {
            if (code == null) return null;
            if (ONE_ON_ONE.code.Equals(code)) return ONE_ON_ONE;
            if (CUMULATED_RIGHT.code.Equals(code)) return CUMULATED_RIGHT;
            if (CUMULATED_LEFT.code.Equals(code)) return CUMULATED_LEFT;
            if (BOTH_CUMULATED.code.Equals(code)) return BOTH_CUMULATED;
            if (ZERO_AMOUNT.code.Equals(code)) return ZERO_AMOUNT;
            if (LEFT_SIDE.code.Equals(code)) return LEFT_SIDE;
            if (RIGHT_SIDE.code.Equals(code)) return RIGHT_SIDE;
            if (NEUTRALIZATION.code.Equals(code)) return NEUTRALIZATION;
            return null;
        }

        public static ObservableCollection<AutoRecoMethod> GetMethods()
        {
            ObservableCollection<AutoRecoMethod> conditions = new ObservableCollection<AutoRecoMethod>();
            conditions.Add(ZERO_AMOUNT);
            conditions.Add(ONE_ON_ONE);
            conditions.Add(CUMULATED_RIGHT);
            conditions.Add(CUMULATED_LEFT);
            conditions.Add(BOTH_CUMULATED);
            //conditions.Add(LEFT_SIDE);
            //conditions.Add(RIGHT_SIDE);
            conditions.Add(NEUTRALIZATION);
            return conditions;
        }

    }
}

using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Filters
{
    public class MeasureFunctions
    {

        public static MeasureFunctions COUNT = new MeasureFunctions("COUNT", "Count");
        public static MeasureFunctions AVERAGE = new MeasureFunctions("AVG", "Average");
        public static MeasureFunctions MAX = new MeasureFunctions("MAX", "Max");
        public static MeasureFunctions MIN = new MeasureFunctions("MIN", "Min");
        public static MeasureFunctions SUM = new MeasureFunctions("SUM", "Sum");


        public string label;
        public string code;
        private MeasureFunctions(String code, String label)
        {
            this.label = label;
            this.code = code;
        }

        public static MeasureFunctions GetByCode(String code)
        {
            if (code == null) return SUM;
            if (AVERAGE.code.Equals(code)) return AVERAGE;
            if (COUNT.code.Equals(code)) return COUNT;
            if (MAX.code.Equals(code)) return MAX;
            if (MIN.code.Equals(code)) return MIN;
            if (SUM.code.Equals(code)) return SUM;
            return SUM;
        }

        public static ObservableCollection<MeasureFunctions> GetAll()
        {
            ObservableCollection<MeasureFunctions> operators = new ObservableCollection<MeasureFunctions>();
            operators.Add(MeasureFunctions.AVERAGE);
            operators.Add(MeasureFunctions.COUNT);
            operators.Add(MeasureFunctions.MAX);
            operators.Add(MeasureFunctions.MIN);
            operators.Add(MeasureFunctions.SUM);
            return operators;
        }

        public override string ToString()
        {
            return this.label;
        }

    }
}

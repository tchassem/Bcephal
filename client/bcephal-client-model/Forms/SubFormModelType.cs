using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class SubFormModelType
    {
        public static SubFormModelType GRID = new SubFormModelType("GRID", "Grid");
        public static SubFormModelType PLAN = new SubFormModelType("PLAN", "Plan");

        public string label;
        public string code;
        private SubFormModelType(String code, String label)
        {
            this.label = label;
            this.code = code;
        }

        public static SubFormModelType GetByCode(String code)
        {
            if (code == null) return null;
            if (GRID.code.Equals(code)) return GRID;
            if (PLAN.code.Equals(code)) return PLAN;
            return null;
        }

        public static ObservableCollection<SubFormModelType> GetAll()
        {
            ObservableCollection<SubFormModelType> operators = new ObservableCollection<SubFormModelType>();
            operators.Add(GRID);
            operators.Add(PLAN);
            return operators;
        }

    }
}

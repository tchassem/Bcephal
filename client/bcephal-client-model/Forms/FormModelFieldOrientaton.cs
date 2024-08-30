using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class FormModelFieldOrientaton
    {
        public static FormModelFieldOrientaton HORIZONTAL = new FormModelFieldOrientaton("HORIZONTAL", "Horizontal");
        public static FormModelFieldOrientaton VERTICAL = new FormModelFieldOrientaton("VERTICAL", "Vertical");

        public string label;
        public string code;
        private FormModelFieldOrientaton(String code, String label)
        {
            this.label = label;
            this.code = code;
        }

        public static FormModelFieldOrientaton GetByCode(String code)
        {
            if (code == null) return null;
            if (HORIZONTAL.code.Equals(code)) return HORIZONTAL;
            if (VERTICAL.code.Equals(code)) return VERTICAL;
            return null;
        }

        public static ObservableCollection<FormModelFieldOrientaton> GetAll()
        {
            ObservableCollection<FormModelFieldOrientaton> operators = new ObservableCollection<FormModelFieldOrientaton>();
            operators.Add(HORIZONTAL);
            operators.Add(VERTICAL);
            return operators;
        }

        public bool IsHorizontal { get { return this == HORIZONTAL; } }

        public bool IsVertical { get { return this == VERTICAL; } }

        public override string ToString()
        {
            return label;
        }

    }
}

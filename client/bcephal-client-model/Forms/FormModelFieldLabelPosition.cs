using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class FormModelFieldLabelPosition
    {

        public static FormModelFieldLabelPosition TOP = new FormModelFieldLabelPosition("TOP", "Top");

        public string label;
        public string code;
        private FormModelFieldLabelPosition(String code, String label)
        {
            this.label = label;
            this.code = code;
        }

        public static FormModelFieldLabelPosition GetByCode(String code)
        {
            if (code == null) return null;
            if (TOP.code.Equals(code)) return TOP;
            return null;
        }

        public static ObservableCollection<FormModelFieldLabelPosition> GetAll()
        {
            ObservableCollection<FormModelFieldLabelPosition> operators = new ObservableCollection<FormModelFieldLabelPosition>();
            operators.Add(TOP);
            return operators;
        }

        public bool IsHorizontal { get { return this == TOP; } }


        public override string ToString()
        {
            return label;
        }

    }
}

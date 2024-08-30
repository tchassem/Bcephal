using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class FormModelFieldNature
    {
        //FormModelFieldNature INPUT,
        //FormModelFieldNature REPORT

        public static FormModelFieldNature INPUT = new FormModelFieldNature("INPUT", "Input");
        public static FormModelFieldNature REPORT = new FormModelFieldNature("REPORT", "Report");

        public string label;
        public string code;
        private FormModelFieldNature(String code, String label)
        {
            this.label = label;
            this.code = code;
        }

        public static FormModelFieldNature GetByCode(String code)
        {
            if (code == null) return null;
            if (INPUT.code.Equals(code)) return INPUT;
            if (REPORT.code.Equals(code)) return REPORT;
            return null;
        }

        public static ObservableCollection<FormModelFieldNature> GetAll()
        {
            ObservableCollection<FormModelFieldNature> operators = new ObservableCollection<FormModelFieldNature>();
            operators.Add(REPORT);
            operators.Add(INPUT);
            return operators;
        }
    }
}

using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class FormModelFieldType
    {
        public static FormModelFieldType EDITION = new FormModelFieldType("EDITION", "Edition");
        public static FormModelFieldType SELECTION = new FormModelFieldType("SELECTION", "Selection");
        public static FormModelFieldType SELECTION_AND_EDITION = new FormModelFieldType("SELECTION_AND_EDITION", "Selection & edition");
        public static FormModelFieldType SELECTION_FROM_LINKED_ATTRIBUTE = new FormModelFieldType("SELECTION_FROM_LINKED_ATTRIBUTE", "Selection  form linked attribute");
        public static FormModelFieldType SELECTION_FROM_LINKED_FIELD = new FormModelFieldType("SELECTION_FROM_LINKED_FIELD", "Selection from field");
        public static FormModelFieldType INFO = new FormModelFieldType("INFO", "Info");
        public static FormModelFieldType CALCULATE = new FormModelFieldType("CALCULATE", "Calculate");
        public static FormModelFieldType ATTACHMENT = new FormModelFieldType("ATTACHMENT", "Attachment");
        public static FormModelFieldType LABEL = new FormModelFieldType("LABEL", "Label");

        public string label;
        public string code;
        private FormModelFieldType(String code, String label)
        {
            this.label = label;
            this.code = code;
        }

        public static FormModelFieldType GetByCode(String code)
        {
            if (code == null) return null;
            if (SELECTION_AND_EDITION.code.Equals(code)) return SELECTION_AND_EDITION;
            if (SELECTION_FROM_LINKED_ATTRIBUTE.code.Equals(code)) return SELECTION_FROM_LINKED_ATTRIBUTE;
            if (EDITION.code.Equals(code)) return EDITION;
            if (SELECTION.code.Equals(code)) return SELECTION;
            if (SELECTION_FROM_LINKED_FIELD.code.Equals(code)) return SELECTION_FROM_LINKED_FIELD;
            if (INFO.code.Equals(code)) return INFO;
            if (CALCULATE.code.Equals(code)) return CALCULATE;
            if (ATTACHMENT.code.Equals(code)) return ATTACHMENT;
            if (LABEL.code.Equals(code)) return LABEL;
            return null;
        }

        public static ObservableCollection<FormModelFieldType> GetAll()
        {
            ObservableCollection<FormModelFieldType> operators = new ObservableCollection<FormModelFieldType>();
            operators.Add(EDITION);
            operators.Add(SELECTION);
            operators.Add(SELECTION_AND_EDITION);
            operators.Add(SELECTION_FROM_LINKED_ATTRIBUTE);
            operators.Add(SELECTION_FROM_LINKED_FIELD);
            operators.Add(CALCULATE);
            operators.Add(INFO);
            operators.Add(LABEL);
            operators.Add(ATTACHMENT);
            return operators;
        }

        public override string ToString()
        {
            return label;
        }

    }
}

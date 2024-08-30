using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class FormModelFieldValidatorType
    {

        public static FormModelFieldValidatorType MANDATORY = new FormModelFieldValidatorType("MANDATORY", "Mandatory");
        public static FormModelFieldValidatorType MIN_LENGTH = new FormModelFieldValidatorType("MIN_LENGTH", "Min length");
        public static FormModelFieldValidatorType MAX_LENGTH = new FormModelFieldValidatorType("MAX_LENGTH", "Max length");
        public static FormModelFieldValidatorType MIN_VALUE = new FormModelFieldValidatorType("MIN_VALUE", "Min value");
        public static FormModelFieldValidatorType MAX_VALUE = new FormModelFieldValidatorType("MAX_VALUE", "Max value");

        public string label;
        public string code;
        private FormModelFieldValidatorType(String code, String label)
        {
            this.label = label;
            this.code = code;
        }

        public static FormModelFieldValidatorType GetByCode(String code)
        {
            if (code == null) return null;
            if (MIN_LENGTH.code.Equals(code)) return MIN_LENGTH;
            if (MAX_LENGTH.code.Equals(code)) return MAX_LENGTH;
            if (MIN_VALUE.code.Equals(code)) return MIN_VALUE;
            if (MAX_VALUE.code.Equals(code)) return MAX_VALUE;
            if (MANDATORY.code.Equals(code)) return MANDATORY;
            return null;
        }

        public static ObservableCollection<FormModelFieldValidatorType> GetAll()
        {
            ObservableCollection<FormModelFieldValidatorType> operators = new ObservableCollection<FormModelFieldValidatorType>();
            operators.Add(MANDATORY);
            operators.Add(MIN_LENGTH);
            operators.Add(MAX_LENGTH);
            operators.Add(MIN_VALUE);
            operators.Add(MAX_VALUE);
            return operators;
        }

        public bool IsMandatory { get { return this == MANDATORY; } }

        public bool IsMaxLength { get { return this == MAX_LENGTH; } }

        public bool IsMinLength { get { return this == MIN_LENGTH; } }

        public bool IsMaxValue { get { return this == MAX_VALUE; } }

        public bool IsMinValue { get { return this == MIN_VALUE; } }

        public override string ToString()
        {
            return label;
        }
    }
}

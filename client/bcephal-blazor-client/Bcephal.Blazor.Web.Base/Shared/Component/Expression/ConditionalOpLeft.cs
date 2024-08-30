using System;
using System.Collections.ObjectModel;

namespace Bcephal.Blazor.Web.Base.Shared.Component.Expression
{
    public class ConditionalOpLeft
    {

        public static ConditionalOpLeft FIRST_BRACKET = new ConditionalOpLeft("(", "(");
        public static ConditionalOpLeft SECOND_BRACKET = new ConditionalOpLeft("((", "((");
        public static ConditionalOpLeft THIRD_BRACKET = new ConditionalOpLeft("(((", "(((");

        public String label { get; set; }
        public String code { get; set; }

        public ConditionalOpLeft(String code, String label)
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

        public static ConditionalOpLeft GetByCode(String code)
        {
            if (code == null) return null;
            if (FIRST_BRACKET.code.Equals(code)) return FIRST_BRACKET;
            if (SECOND_BRACKET.code.Equals(code)) return SECOND_BRACKET;
            if (THIRD_BRACKET.code.Equals(code)) return THIRD_BRACKET;
            return null;
        }

        public static ObservableCollection<ConditionalOpLeft> GetAll()
        {
            ObservableCollection<ConditionalOpLeft> operatorsLeft = new ObservableCollection<ConditionalOpLeft>();
            operatorsLeft.Add(FIRST_BRACKET);
            operatorsLeft.Add(SECOND_BRACKET);
            operatorsLeft.Add(THIRD_BRACKET);
            return operatorsLeft;
        }
    }
}

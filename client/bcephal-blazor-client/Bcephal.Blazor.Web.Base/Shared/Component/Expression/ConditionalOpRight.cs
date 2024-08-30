using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component.Expression
{
    public class ConditionalOpRight
    {

        public static ConditionalOpRight FIRST_BRACKET = new ConditionalOpRight(")", ")");
        public static ConditionalOpRight SECOND_BRACKET = new ConditionalOpRight("))", "))");
        public static ConditionalOpRight THIRD_BRACKET = new ConditionalOpRight(")))", ")))");

        public String label { get; set; }
        public String code { get; set; }

        public ConditionalOpRight(String code, String label)
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

        public static ConditionalOpRight GetByCode(String code)
        {
            if (code == null) return null;
            if (FIRST_BRACKET.code.Equals(code)) return FIRST_BRACKET;
            if (SECOND_BRACKET.code.Equals(code)) return SECOND_BRACKET;
            if (THIRD_BRACKET.code.Equals(code)) return THIRD_BRACKET;
            return null;
        }

        public static ObservableCollection<ConditionalOpRight> GetAll()
        {
            ObservableCollection<ConditionalOpRight> operatorsRight = new ObservableCollection<ConditionalOpRight>();
            operatorsRight.Add(FIRST_BRACKET);
            operatorsRight.Add(SECOND_BRACKET);
            operatorsRight.Add(THIRD_BRACKET);
            return operatorsRight;
        }
    }
}

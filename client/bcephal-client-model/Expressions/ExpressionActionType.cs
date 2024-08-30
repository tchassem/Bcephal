using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Expressions
{
    public class ExpressionActionType
    {

		public static ExpressionActionType CONDITION = new ExpressionActionType("CONDITION", "Condition");
		public static ExpressionActionType SET_VALUE = new ExpressionActionType("SET_VALUE", "Set value");
		public static ExpressionActionType STOP = new ExpressionActionType("STOP", "Stop");
		public static ExpressionActionType NOT_ACTION = new ExpressionActionType("NOT_ACTION", "No action");

        public string label;
        public string code;
        private ExpressionActionType(String code, String label)
        {
            this.label = label;
            this.code = code;
        }

        public static ExpressionActionType GetByCode(String code)
        {
            if (code == null) return null;
            if (STOP.code.Equals(code)) return STOP;
            if (NOT_ACTION.code.Equals(code)) return NOT_ACTION;
            if (CONDITION.code.Equals(code)) return CONDITION;
            if (SET_VALUE.code.Equals(code)) return SET_VALUE;
            return null;
        }

        public static ObservableCollection<ExpressionActionType> GetAll()
        {
            ObservableCollection<ExpressionActionType> operators = new ObservableCollection<ExpressionActionType>();
            operators.Add(CONDITION);
            operators.Add(SET_VALUE);
            operators.Add(STOP);
            operators.Add(NOT_ACTION);
            return operators;
        }


	}
}

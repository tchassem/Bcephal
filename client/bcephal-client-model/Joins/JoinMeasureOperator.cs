using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Joins
{
    public class JoinMeasureOperator
    {
        public static string EQUALS = "=";
        public static string NOT_EQUALS = "<>";
        public static string GRETTER_THAN = ">";
        public static string GRETTER_OR_EQUALS = ">=";
        public static string LESS_THAN = "<";
        public static string LESS_OR_EQUALS = "<=";
        public static string IS_NULL = "IsNull";
        public static string Is_Not_Null = "IsNotNull";

        public static ObservableCollection<string> GetAll()
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(JoinMeasureOperator.EQUALS);
            operators.Add(JoinMeasureOperator.NOT_EQUALS);
            operators.Add(JoinMeasureOperator.GRETTER_THAN);
            operators.Add(JoinMeasureOperator.GRETTER_OR_EQUALS);
            operators.Add(JoinMeasureOperator.LESS_THAN);
            operators.Add(JoinMeasureOperator.LESS_OR_EQUALS);
            operators.Add(JoinMeasureOperator.IS_NULL);
            operators.Add(JoinMeasureOperator.Is_Not_Null);
            return operators;
        }
    }
}

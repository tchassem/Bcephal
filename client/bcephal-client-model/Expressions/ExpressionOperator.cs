using Bcephal.Models.Filters;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Expressions
{
    public class ExpressionOperator
    {
        public static ExpressionOperator EQUALS = new ExpressionOperator("EQUALS", "=");
        public static ExpressionOperator NOT_EQUALS = new ExpressionOperator("NOT_EQUALS", "<>");
        public static ExpressionOperator NULL = new ExpressionOperator("NULL", "Null");
        public static ExpressionOperator NOT_NULL = new ExpressionOperator("NOT_NULL", "Not null");
        public static ExpressionOperator CONTAINS = new ExpressionOperator("CONTAINS", "Contains");
        public static ExpressionOperator STARTS_WITH = new ExpressionOperator("STARTS_WITH", "Starts with");
        public static ExpressionOperator ENDS_WITH = new ExpressionOperator("ENDS_WITH", "Ends with");

        public static ExpressionOperator GRETTER_THAN = new ExpressionOperator("GRETTER_THAN", ">");
        public static ExpressionOperator GRETTER_OR_EQUALS = new ExpressionOperator("GRETTER_OR_EQUALS", ">=");
        public static ExpressionOperator LESS_THAN = new ExpressionOperator("LESS_THAN", "<");
        public static ExpressionOperator LESS_OR_EQUALS = new ExpressionOperator("LESS_OR_EQUALS", "<=");



        public string label;
        public string code;
        private ExpressionOperator(String code, String label)
        {
            this.label = label;
            this.code = code;
        }

        public static ExpressionOperator GetByCode(String code)
        {
            if (code == null) return null;
            if (CONTAINS.code.Equals(code)) return CONTAINS;
            if (NULL.code.Equals(code)) return NULL;
            if (NOT_NULL.code.Equals(code)) return NOT_NULL;
            if (EQUALS.code.Equals(code)) return EQUALS;
            if (NOT_EQUALS.code.Equals(code)) return NOT_EQUALS;
            if (STARTS_WITH.code.Equals(code)) return STARTS_WITH;
            if (ENDS_WITH.code.Equals(code)) return ENDS_WITH;
            if (GRETTER_THAN.code.Equals(code)) return GRETTER_THAN;
            if (GRETTER_OR_EQUALS.code.Equals(code)) return GRETTER_OR_EQUALS;
            if (LESS_THAN.code.Equals(code)) return LESS_THAN;
            if (LESS_OR_EQUALS.code.Equals(code)) return LESS_OR_EQUALS;

            return null;
        }

        public static ObservableCollection<ExpressionOperator> GetOperators(DimensionType? dimensionType)
        {
            ObservableCollection<ExpressionOperator> operators = new ObservableCollection<ExpressionOperator>();
            if (dimensionType.HasValue)
            {
                if (dimensionType.Value.IsMeasure() || dimensionType.Value.IsPeriod())
                {
                    operators.Add(EQUALS);
                    operators.Add(NOT_EQUALS);
                    operators.Add(GRETTER_THAN);
                    operators.Add(GRETTER_OR_EQUALS);
                    operators.Add(LESS_THAN);
                    operators.Add(LESS_OR_EQUALS);
                }
                else if (dimensionType.Value.IsAttribute())
                {

                    operators.Add(EQUALS);
                    operators.Add(NOT_EQUALS);
                    operators.Add(NULL);
                    operators.Add(NOT_NULL);
                    operators.Add(CONTAINS);
                    operators.Add(STARTS_WITH);
                    operators.Add(ENDS_WITH);
                }
            }
            return operators;
        }

        public static ExpressionOperator GetDefaultOperator(DimensionType? dimensionType)
        {
            if (dimensionType.HasValue)
            {
                if (dimensionType.Value.IsMeasure() || dimensionType.Value.IsPeriod())
                {
                    return EQUALS;
                }
                else if (dimensionType.Value.IsAttribute())
                {
                    return STARTS_WITH;
                }
            }
            return null;
        }

        public static bool Match(ExpressionOperator op, DimensionType? dimensionType)
        {
            if (op != null && dimensionType.HasValue)
            {
                return GetOperators(dimensionType).Contains(op);
            }
            return false;
        }

        public override string ToString()
        {
            return this.label;
        }

    }
}

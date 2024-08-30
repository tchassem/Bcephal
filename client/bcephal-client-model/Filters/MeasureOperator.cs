using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

namespace Bcephal.Models.Filters
{
    public class MeasureOperator
    {

        public static string EQUALS = "=";
        public static string NOT_EQUALS = "<>";
        public static string GRETTER_THAN = ">";
        public static string GRETTER_OR_EQUALS = ">=";
        public static string LESS_THAN = "<";
        public static string LESS_OR_EQUALS = "<=";
        public static string NULL = "NULL";
        public static string Not_Null = "NOT_NULL";


        public static MeasureOperator EQUALS_ = new MeasureOperator("=", "=");
        public static MeasureOperator NOT_EQUALS_ = new MeasureOperator("<>", "<>");
        public static MeasureOperator GRETTER_THAN_ = new MeasureOperator(">", ">");
        public static MeasureOperator GRETTER_OR_EQUALS_ = new MeasureOperator(">=", ">=");
        public static MeasureOperator LESS_THAN_ = new MeasureOperator("<", "<");
        public static MeasureOperator LESS_OR_EQUALS_ = new MeasureOperator("<=", "<=");
        public static MeasureOperator NULL_ = new MeasureOperator("NULL", "Null");
        public static MeasureOperator NOT_NULL_ = new MeasureOperator("NOT_NULL", "Not_Null");

        public String label;
        public String code;

        public MeasureOperator(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static MeasureOperator GetByCode(string code)
        {
            if (code == null) return null;
            if (EQUALS_.code.Equals(code)) return EQUALS_;
            if (NOT_EQUALS_.code.Equals(code)) return NOT_EQUALS_;
            if (GRETTER_THAN_.code.Equals(code)) return GRETTER_THAN_;
            if (GRETTER_OR_EQUALS_.code.Equals(code)) return GRETTER_OR_EQUALS_;
            if (LESS_THAN_.code.Equals(code)) return LESS_THAN_;
            if (LESS_OR_EQUALS_.code.Equals(code)) return LESS_OR_EQUALS_;
            if (NULL_.code.Equals(code)) return NULL_;
            if (NOT_NULL_.code.Equals(code)) return NOT_NULL_;
            return null;
        }

        public static ObservableCollection<string> GetAll()
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(MeasureOperator.EQUALS);
            operators.Add(MeasureOperator.NOT_EQUALS);
            operators.Add(MeasureOperator.GRETTER_THAN);
            operators.Add(MeasureOperator.GRETTER_OR_EQUALS);
            operators.Add(MeasureOperator.LESS_THAN);
            operators.Add(MeasureOperator.LESS_OR_EQUALS);
            return operators;
        }


        public static string GetSymbol_(string operator_)
        {
            if (EQUALS.Equals(operator_))
            {
                //return "bi bi-pause";
                return "fa-solid fa-equals text-secondary fa-sm py-2";
            }
            if (NOT_EQUALS.Equals(operator_))
            {
                //return "bi bi-file-code";
                return "fa-solid fa-not-equal text-secondary fa-sm py-2";
            }
            if (GRETTER_THAN.Equals(operator_))
            {
                //return "bi bi-chevron-right";
                return "fa-solid fa-greater-than text-secondary fa-sm py-2";
            }
            if (GRETTER_OR_EQUALS.Equals(operator_))
            {
                //return "bi bi-arrow-bar-right";
                return "fa-solid fa-greater-than-equal text-secondary fa-sm py-2";
            }

            if (LESS_THAN.Equals(operator_))
            {
                //return "bi bi-chevron-left";
                return "fa-solid fa-less-than text-secondary fa-sm py-2";
            }
            if (LESS_OR_EQUALS.Equals(operator_))
            {
                //return "bi bi-arrow-bar-left";
                return "fa-solid fa-less-than-equal text-secondary fa-sm py-2";
            }

            return null;
        }

    } 
    public static class MeasureOperatorExtensionMethods
    {
        public static ObservableCollection<string> GetAllForJoin(this MeasureOperator measureOperator, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(null);
            operators.Add(Localize?.Invoke("="));
            operators.Add(Localize?.Invoke("<>"));
            operators.Add(Localize?.Invoke(">"));
            operators.Add(Localize?.Invoke(">="));
            operators.Add(Localize?.Invoke("<"));
            operators.Add(Localize?.Invoke("<="));
            operators.Add(Localize?.Invoke("NULL"));
            operators.Add(Localize?.Invoke("NOT_NULL"));
            return operators;
        }

        public static string GetText(this MeasureOperator measureOperator, Func<string, string> Localize)
        {
            if (measureOperator.Equals(MeasureOperator.EQUALS_))
            {
                return Localize?.Invoke("=");
            }
            if (measureOperator.Equals(MeasureOperator.NOT_EQUALS_))
            {
                return Localize?.Invoke("<>");
            }
            if (measureOperator.Equals(MeasureOperator.GRETTER_THAN_))
            {
                return Localize?.Invoke(">");
            }
            if (measureOperator.Equals(MeasureOperator.GRETTER_OR_EQUALS_))
            {
                return Localize?.Invoke(">=");
            }
            if (measureOperator.Equals(MeasureOperator.LESS_THAN_))
            {
                return Localize?.Invoke("<");
            }
            if (measureOperator.Equals(MeasureOperator.LESS_OR_EQUALS_))
            {
                return Localize?.Invoke("<=");
            }
            if (measureOperator.Equals(MeasureOperator.NULL_))
            {
                return Localize?.Invoke("NULL");
            }
            if (measureOperator.Equals(MeasureOperator.NOT_NULL_))
            {
                return Localize?.Invoke("NOT_NULL");
            }
            return null;
        }

        public static MeasureOperator GetMeasureOperator(this MeasureOperator measureOperator, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("=")))
                {
                    return MeasureOperator.EQUALS_;
                }
                if (text.Equals(Localize?.Invoke("<>")))
                {
                    return MeasureOperator.NOT_EQUALS_;
                }
                if (text.Equals(Localize?.Invoke(">")))
                {
                    return MeasureOperator.GRETTER_THAN_;
                }
                if (text.Equals(Localize?.Invoke(">=")))
                {
                    return MeasureOperator.GRETTER_OR_EQUALS_;
                }
                if (text.Equals(Localize?.Invoke("<")))
                {
                    return MeasureOperator.LESS_THAN_;
                } 
                if (text.Equals(Localize?.Invoke("<=")))
                {
                    return MeasureOperator.LESS_OR_EQUALS_;
                }
                if (text.Equals(Localize?.Invoke("NULL")))
                {
                    return MeasureOperator.NULL_;
                }
                if (text.Equals(Localize?.Invoke("NOT_NULL")))
                {
                    return MeasureOperator.NOT_NULL_;
                }
                
            }

            return null; 
        }
    }
}

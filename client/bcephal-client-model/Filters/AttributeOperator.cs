using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System;
using System.Collections.ObjectModel;

namespace Bcephal.Models.Filters
{
    [JsonConverter(typeof(StringEnumConverter))]
    public enum AttributeOperator
    {

        CONTAINS,
        NOT_CONTAINS,
        NULL,
        NOT_NULL,
        EQUALS,
        NOT_EQUALS,
        STARTS_WITH,
        ENDS_WITH

    }

    public static class AttributeOperatorExtensionMethods
    {
        public static ObservableCollection<string> GetAll(this AttributeOperator? filterOperator, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(null);
            operators.Add(Localize?.Invoke("CONTAINS"));
            operators.Add(Localize?.Invoke("NOT_CONTAINS"));
            operators.Add(Localize?.Invoke("NULL"));
            operators.Add(Localize?.Invoke("NOT_NULL"));
            operators.Add(Localize?.Invoke("EQUALS_"));
            operators.Add(Localize?.Invoke("NOT_EQUALS"));
            operators.Add(Localize?.Invoke("STARTS_WITH"));
            operators.Add(Localize?.Invoke("ENDS_WITH"));
            return operators;
        }

        public static ObservableCollection<string> GetFilter(this AttributeOperator filterOperator, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(null);
            operators.Add(Localize?.Invoke("EQUALS"));
            operators.Add(Localize?.Invoke("NOT_EQUALS"));
            operators.Add(Localize?.Invoke("STARTS_WITH"));
            operators.Add(Localize?.Invoke("ENDS_WITH"));
            operators.Add(Localize?.Invoke("CONTAINS"));
            operators.Add(Localize?.Invoke("NOT_CONTAINS"));
            return operators;
        }

        public static AttributeOperator? Parse(this AttributeOperator operator_, string text)
        {
            try
            {
               return string.IsNullOrWhiteSpace(text) ? null : ((AttributeOperator?)Enum.Parse(typeof(AttributeOperator), text));
            }
            catch
            {
                return null;
            }
        }
        public static string GetText(this AttributeOperator? operator_, Func<string,string> Localize)
        {
            if (AttributeOperator.CONTAINS.Equals(operator_))
            {
                return Localize?.Invoke("CONTAINS");
            }
            if (AttributeOperator.ENDS_WITH.Equals(operator_))
            {
                return Localize?.Invoke("ENDS_WITH");
            }
            if (AttributeOperator.EQUALS.Equals(operator_))
            {
                return Localize?.Invoke("EQUALS_");
            }
            if (AttributeOperator.NOT_CONTAINS.Equals(operator_))
            {
                return Localize?.Invoke("NOT_CONTAINS");
            }
            if (AttributeOperator.NOT_EQUALS.Equals(operator_))
            {
                return Localize?.Invoke("NOT_EQUALS");
            }
            if (AttributeOperator.NOT_NULL.Equals(operator_))
            {
                return Localize?.Invoke("NOT_NULL");
            }
            if (AttributeOperator.NULL.Equals(operator_))
            {
                return Localize?.Invoke("NULL");
            }
            if (AttributeOperator.STARTS_WITH.Equals(operator_))
            {
                return Localize?.Invoke("STARTS_WITH");
            }
            return null;
        }

        public static AttributeOperator? GetAttributeOperator(this AttributeOperator? filterOperator, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("CONTAINS")))
                {
                    return AttributeOperator.CONTAINS;
                }
                if (text.Equals(Localize?.Invoke("ENDS_WITH")))
                {
                    return AttributeOperator.ENDS_WITH;
                }
                if (text.Equals(Localize?.Invoke("EQUALS_")))
                {
                    return AttributeOperator.EQUALS;
                }
                if (text.Equals(Localize?.Invoke("NOT_CONTAINS")))
                {
                    return AttributeOperator.NOT_CONTAINS;
                }
                if (text.Equals(Localize?.Invoke("NOT_EQUALS")))
                {
                    return AttributeOperator.NOT_EQUALS;
                }
                if (text.Equals(Localize?.Invoke("NOT_NULL")))
                {
                    return AttributeOperator.NOT_NULL;
                }
                if (text.Equals(Localize?.Invoke("NULL")))
                {
                    return AttributeOperator.NULL;
                }
                if (text.Equals(Localize?.Invoke("STARTS_WITH")))
                {
                    return AttributeOperator.STARTS_WITH;
                }
            }
            return null;
        }

        public static string GetSymbol_(this AttributeOperator? operator_)
        {
            if (AttributeOperator.CONTAINS.Equals(operator_))
            {
                //return "bi bi-arrow-bar-down";
                return "fa-solid fa-c text-secondary fa-sm py-2";
            }
            if (AttributeOperator.ENDS_WITH.Equals(operator_))
            {
                //return "bi bi-arrow-bar-left";
                return "fa-solid fa-arrow-right-to-bracket text-secondary fa-sm py-2";
            }
            if (AttributeOperator.EQUALS.Equals(operator_))
            {
                //return "bi bi-pause";
                return "fa-solid fa-equals text-secondary fa-sm py-2";
            }
            if (AttributeOperator.NOT_CONTAINS.Equals(operator_))
            {
                //return "bi bi-arrow-bar-up";
                return "fa-solid fa-cedi-sign text-secondary fa-sm py-2";
            }
            if (AttributeOperator.NOT_EQUALS.Equals(operator_))
            {
                // return "fa fa-code";
                //return "bi bi-chevron-left bi-chevron-right";
                return "fa-solid fa-not-equal text-secondary fa-sm py-2";
            }
            if (AttributeOperator.NOT_NULL.Equals(operator_))
            {
                //return "bi bi-circle-half";
                return "fa-solid fa-ban fa-flip-horizontal text-secondary fa-sm py-2";
            }
            if (AttributeOperator.NULL.Equals(operator_))
            {
                //return "bi bi-slash-circle";
                return "fa-solid fa-0 text-secondary fa-sm py-2";
            }
            if (AttributeOperator.STARTS_WITH.Equals(operator_))
            {
                //return "bi bi-arrow-bar-right";
                return "fa-solid fa-arrow-right-from-bracket text-secondary fa-sm py-2";
            }
            return null;
        }

    }

}

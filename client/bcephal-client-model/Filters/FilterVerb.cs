using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

namespace Bcephal.Models.Filters
{
    [JsonConverter(typeof(StringEnumConverter))]
    public enum FilterVerb
    {

        AND,
        OR,

    }

    public static class FilterVerbExtensionMethods
    {

        public static bool IsAnd(this FilterVerb verb)
        {
            return verb == FilterVerb.AND;
        }

        public static bool IsOr(this FilterVerb verb)
        {
            return verb == FilterVerb.OR;
        }

        public static ObservableCollection<FilterVerb> GetAll()
        {
            ObservableCollection<FilterVerb> operators = new ObservableCollection<FilterVerb>();
            operators.Add(FilterVerb.AND);
            operators.Add(FilterVerb.OR);
            return operators;
        }

        public static ObservableCollection<string> GetAll(this FilterVerb verb, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(null);
            operators.Add(Localize?.Invoke("AND"));
            operators.Add(Localize?.Invoke("OR"));
            return operators;
        }

        public static string GetText(this FilterVerb verb, Func<string, string> Localize)
        {
            if (FilterVerb.AND.Equals(verb))
            {
                return Localize?.Invoke("AND");
            }
            if (FilterVerb.OR.Equals(verb))
            {
                return Localize?.Invoke("OR");
            }
            return null;
        }

        public static FilterVerb GetFilterVerb(this FilterVerb verb, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("AND")))
                {
                    return FilterVerb.AND;
                }
                if (text.Equals(Localize?.Invoke("OR")))
                {
                    return FilterVerb.OR;
                }
            }
            return FilterVerb.AND;
        }

        public static FilterVerb? Parse(this FilterVerb verb, string text)
        {
            try
            {
                return string.IsNullOrWhiteSpace(text) ? null : ((FilterVerb?)Enum.Parse(typeof(FilterVerb), text));
            }
            catch
            {
                return null;
            }
        }
    }

}

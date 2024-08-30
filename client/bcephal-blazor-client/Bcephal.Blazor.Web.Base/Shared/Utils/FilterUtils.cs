using Bcephal.Models.Filters;
using System.Collections.Generic;


namespace Bcephal.Blazor.Web.Base.Shared
{
    public class FilterUtils
    {
        public static List<string> OpenBrackets = new List<string>() 
        { 
            "(",
            "((",
            "((("
        };

        public static List<string> CloseBrackets = new List<string>()
        {
            ")",
            "))",
            ")))"
        };

        public static List<string> Signs = new List<string>()
        {
            "+",
            "-"
        };

        public static List<FilterVerb> FilterVerbs = new List<FilterVerb>() 
        {
            FilterVerb.OR,
            FilterVerb.AND
        };


    }
}

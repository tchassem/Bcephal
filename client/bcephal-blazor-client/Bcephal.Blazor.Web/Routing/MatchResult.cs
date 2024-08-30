using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Routing
{
    public class MatchResult
    {
        public bool IsMatch { get; set; }
        public CustomRoute MatchedRoute { get; set; }

        public MatchResult(bool isMatch, CustomRoute matchedRoute)
        {
            IsMatch = isMatch;
            MatchedRoute = matchedRoute;
        }

        public static MatchResult Match(CustomRoute matchedRoute)
        {
            return new MatchResult(true, matchedRoute);
        }

        public static MatchResult NoMatch()
        {
            return new MatchResult(false, null);
        }
    }
}

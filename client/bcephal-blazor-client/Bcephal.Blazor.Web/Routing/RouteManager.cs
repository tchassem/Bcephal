using System.Collections.Generic;
using System.Linq;

namespace Bcephal.Blazor.Web.Routing
{
    public class RouteManager
    {
        public CustomRoute[] Routes { get; private set; }
        public void Initialise()
        {
            Routes = GetPagesToPreRender().ToArray();
        }
        public static List<CustomRoute> GetPagesToPreRender() => PrerenderRouteHelper
            .GetRoutesToRender(new() { 
                typeof(Bcephal.Blazor.Web._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Base._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Sourcing._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Reporting._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Reconciliation._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Billing._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Dashboard._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Form._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Archive._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Initiation._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Accounting._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Setting._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Planification._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Administration._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Messenger._Imports).Assembly,
                typeof(Bcephal.Blazor.Web.Scheduling._Imports).Assembly,
                //typeof(Bcephal.Blazor.Web.FileManager._Imports).Assembly
            });


        public MatchResult Match(string[] segments)
        {
            if (segments.Length == 0)
            {
                var indexRoute = Routes.SingleOrDefault(x => x.Handler.FullName.ToLower().EndsWith("HomePage"));
                return MatchResult.Match(indexRoute);
            }

            foreach (var route in Routes)
            {
                var matchResult = route.Match(segments);
                if (matchResult.IsMatch)
                {
                    return matchResult;
                }
            }
            return MatchResult.NoMatch();
        }
    }
}

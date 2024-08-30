using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Routing
{
    public static class PrerenderRouteHelper
    {
        public static List<CustomRoute> GetRoutesToRender(List<Assembly> assemblies)
        {
            var routesList = new List<CustomRoute>();
            foreach (var assembly in assemblies)
            {
                // Get all the components whose base class is ComponentBase
                var components = assembly.ExportedTypes.Where(t => t.IsSubclassOf(typeof(ComponentBase)));                
                var routes = components.Select(component => GetRouteFromComponent(component));
                foreach (var route in routes)
                {
                    routesList.AddRange(route);
                }
            }            
            return routesList;
        }

        private static List<CustomRoute> GetRouteFromComponent(Type component)
        {
            var attributes = component.GetCustomAttributes(inherit: true);
            var routeAttributes = attributes.OfType<RouteAttribute>();
            if (routeAttributes is null)
            {
                // Only map routable components
                return null;
            }
            var routesList = new List<CustomRoute>();
            foreach (var routeAttribute in routeAttributes)
            {
                var route = routeAttribute.Template;
               //Console.WriteLine("route => " + route.ToString());
               //Console.WriteLine("component => " + component.ToString());
                var newRoute = new CustomRoute
                {
                    UriSegments = getSplit(route.Split('/')),
                    Handler = component
                };
                routesList.Add(newRoute);
            }
            return routesList;
        }

      private  static string [] getSplit(string[] split)
        {
            var routesList = new List<string>();
            int i = 0;
            foreach(var spl in split)
            {
                if (!string.IsNullOrEmpty(spl) && spl.Contains("{") && spl.Contains("}"))
                {
                    string sep = ":>>>>[";
                    if (!spl.Contains(":"))
                    {
                        var el = "[" + spl.Substring(1, spl.IndexOf("}") - 1) + sep + ".*";
                        routesList.Add(el);
                    }else
                    if (spl.Contains(":") && spl.Contains("int"))
                    {
                        var el = "[" + spl.Substring(1, spl.IndexOf(':') - 1 ) + sep + "i\\d+";
                        routesList.Add(el);
                    }
                    else
                    if (spl.Contains(":") &&  spl.Contains("long"))
                    {
                        var el = "[" + spl.Substring(1, spl.IndexOf(':') - 1) + sep + "l\\d+";
                        routesList.Add(el);
                    }
                    else
                    if (spl.Contains(":") && spl.Contains("bool"))
                    {
                        var el = "[" + spl.Substring(1, spl.IndexOf(':') - 1) + sep + "b\\d+";
                        routesList.Add(el);
                    }
                    else
                    if (spl.Contains(":") && spl.Contains("decimal"))
                    {
                        var el = "[" + spl.Substring(1, spl.IndexOf(':') - 1) + sep + "a\\d+";
                        routesList.Add(el);
                    }
                    else
                    if (spl.Contains(":") && spl.Contains("double"))
                    {
                        var el = "[" + spl.Substring(1, spl.IndexOf(':') - 1) + sep + "do\\d+";
                        routesList.Add(el);
                    }
                    else
                    if (spl.Contains(":") && spl.Contains("float"))
                    {
                        var el = "[" + spl.Substring(1, spl.IndexOf(':') - 1) + sep + "f\\d+";
                        routesList.Add(el);
                    }
                    else
                    if (spl.Contains(":") && spl.Contains("guid"))
                    {
                        var el = "[" + spl.Substring(1, spl.IndexOf(':') - 1) + sep + "g\\d+";
                        routesList.Add(el);
                    }
                    else
                    {
                        var el = "[" + spl.Substring(1, spl.IndexOf("}") - 1) + sep + ".*";
                        //Console.WriteLine("Property 2 : " + el);
                        routesList.Add(el);
                    }
                }
                else
                {
                    if (!string.IsNullOrWhiteSpace(spl) && !spl.StartsWith("/"))
                    {
                        routesList.Add(spl);
                    } 
                }
                i++;
            }
            return routesList.ToArray();
        }
    }

}

using System.Globalization;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class SupportedCultures
    {
        public static CultureInfo[] Cultures { get; } = new CultureInfo[] {
           // new CultureInfo("de-DE"),
            new CultureInfo("en-US"),
           new CultureInfo("fr-FR"),
           //new CultureInfo("it-IT"),
        };
    }
}
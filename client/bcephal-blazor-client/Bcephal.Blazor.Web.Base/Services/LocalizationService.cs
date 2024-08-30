using DevExpress.Blazor.Localization;
using System.Globalization;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class LocalizationService : DxLocalizationService, IDxLocalizationService
    {
        //public string this[string name] { get { return GetString(name); } }
        //public string this[string name, params object[] arguments] { get { return GetString(name, arguments); } }


        //public string GetString(string key)
        //{
        //    return Resources.App.ResourceManager.GetString(key);
        //}

        //public string GetString(string key, params object[] arguments)
        //{
        //    return string.Format(Resources.App.ResourceManager.GetString(key), arguments);
        //}

        string IDxLocalizationService.GetString(string key)
        {
            var culture = CultureInfo.CurrentUICulture.Name;
            string value = null;
            return value ?? base.GetString(key);
        }
    }
}
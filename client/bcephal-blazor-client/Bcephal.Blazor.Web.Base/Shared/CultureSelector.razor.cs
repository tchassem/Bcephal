using Bcephal.Blazor.Web.Base.Services;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System.Globalization;
namespace Bcephal.Blazor.Web.Base.Shared
{
    public partial class CultureSelector : ComponentBase
    {
        

        [Inject] AppState AppState { get; set; }

        public CultureInfo Culture
        {
            get { return AppState.getCurrentCulture(); }

           set
            {
                if (AppState.getCurrentCulture() != value)
                {
                    AppState.ChangeCulture(value);
                }
            }
        }
    }
}

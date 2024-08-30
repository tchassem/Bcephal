using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Initiation.Services;
using Microsoft.AspNetCore.Components;

namespace Bcephal.Blazor.Web.Initiation.Pages.Measures
{
    public partial class MeasuresTree : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        [Inject]
        public MeasuresService MeasuresService { get; set; }
    }
}


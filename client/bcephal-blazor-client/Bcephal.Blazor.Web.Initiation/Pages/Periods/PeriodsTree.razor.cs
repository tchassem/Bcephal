using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Initiation.Services;
using Microsoft.AspNetCore.Components;

namespace Bcephal.Blazor.Web.Initiation.Pages.Periods
{
    public partial class PeriodsTree : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        [Inject]
        public PeriodService PeriodService { get; set; }
    }
}

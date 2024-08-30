using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Initiation.Services;
using Microsoft.AspNetCore.Components;

namespace Bcephal.Blazor.Web.Initiation.Pages.Models
{
    public partial class ModelsTree : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        [Inject]
        public ModelInitService ModelInitService { get; set; }
    }
}

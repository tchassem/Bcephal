
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Grids;
using Microsoft.AspNetCore.Components;

namespace Bcephal.Blazor.Web.Sourcing.Shared.Grille
{
    public partial class GridColumnComponentTag : ComponentBase
    {
        [Parameter] public int ActiveTabIndex { get; set; } = 0;
        [Inject] private AppState AppState { get; set; }
        [Parameter] public Models.Grids.GrilleColumn GrilleColumn { get; set; }
        [Parameter] public EventCallback<Models.Grids.GrilleColumn> GrilleColumnChanged { get; set; }
        [Parameter] public GrilleType grilleType { get; set; }
        [Parameter] public bool Editable { get; set; } = true;
        [Parameter] public int ItemsCount { get; set; } = 0;

        public Models.Grids.GrilleColumn GrilleColumnBinding
        {
            get { return GrilleColumn; }
            set
            {
                GrilleColumn = value;
                GrilleColumnChanged.InvokeAsync(value);
            }
        }
    }
}

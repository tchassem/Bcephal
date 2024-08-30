using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Dimensions;
using Microsoft.AspNetCore.Components;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class DimensionsDraggableComponent: ComponentBase
    {
        [Inject] private AppState AppState { get; set; }

        [Parameter] public IEnumerable<Dimension> Attributes { get; set; }
        [Parameter] public IEnumerable<Dimension> Measures { get; set; }
        [Parameter] public IEnumerable<Dimension> Periods { get; set; }
        [Parameter] public Action<Dimension> SelectDimensionHandler { get; set; }

        [Parameter] public string DefaultHeight { get; set; } = "var(--grid-bc-pivot-form)";

        [Parameter] public bool Editable { get; set; } = true;
        public string HeaderStyle { get; set; } = "bc-header-3 p-0 bc-header-height bc-text-align pl-1";
    }
}

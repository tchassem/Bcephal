
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System.Collections.ObjectModel;
namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class ColumnItemFieldComponent : ComponentBase
    {
        private bool isXSmallScreen { get; set; }
        private bool IsSmallScreen { get; set; }
        [CascadingParameter]
        public Error Error { get; set; }
        [Inject]
        public IJSRuntime JSRuntime { get; set; }
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public EditorData<Bcephal.Models.Grids.Grille> EditorData { get; set; }
        [Parameter]
        public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> EditorDataChanged { get; set; }
        [Parameter]
        public ObservableCollection<HierarchicalData> Entities { get; set; }
        [Parameter]
        public EventCallback<HierarchicalData> SelectAttributeCallback { get; set; }
        [Parameter]
        public EventCallback<Models.Dimensions.Measure> SelectMesureCallback { get; set; }
        [Parameter]
        public EventCallback<Models.Dimensions.Period> SelectPeriodeCallback { get; set; }

        public ObservableCollection<GrilleColumn> Items { get { return EditorData.Item.ColumnListChangeHandler.GetItems(); } }

        [Parameter]
        public bool Editable { get; set; } = true;
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using System.Collections.ObjectModel;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components.Web;

namespace Bcephal.Blazor.Web.Sourcing.Shared.Grille
{
    public partial class GridDimensions_Configuration : ComponentBase
    {
        [Inject] public IJSRuntime JSRuntime { get; set; }
        [Inject] public AppState AppState { get; set; }

        [CascadingParameter] public Error Error { get; set; }
        [Parameter] public EditorData<Bcephal.Models.Grids.Grille> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> EditorDataChanged { get; set; }
        [Parameter] public bool UsingPane { get; set; } = true;

        [Parameter]
        public bool Editable_ { get; set; } = true;

        private ObservableCollection<GrilleDimension> DimensionList { get => EditorData.Item.DimensionListChangeHandler.GetItems(); }
        private IEnumerable<GrilleDimension> SelectedDimensions { get; set; }
        private DxContextMenu DxContextMenu_ { get; set; }

        #region :: Methods section ::
        protected override void OnInitialized()
        {
            SelectedDimensions = new List<GrilleDimension>();
        }

        public void SetSelectedDimension(GrilleDimension dimension)
        {
            SelectedDimensions = new List<GrilleDimension>(){ dimension };
        }

        public async void showContextMenu_(MouseEventArgs args)
        {
            await DxContextMenu_.ShowAsync(args);
        }

        async void DeleteSelectedDimension()
        {
            if (SelectedDimensions.Any())
            {
                var grilleDimension = SelectedDimensions.First();
                EditorData.Item.DeleteOrForgetDimension(grilleDimension);
                await EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        #endregion
    }
}

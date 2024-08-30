using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Shared.Grille
{
    public partial class InputGridComponentForm_ : ComponentBase
    {
        [Parameter] public GrilleService GrilleService { get; set; }
        [Parameter] public Func<RenderFragment> CustomHeaderRenderHandler { get; set; }
        public string stylefilter = "filter-height";
        [Parameter] public EditorData<Bcephal.Models.Grids.Grille> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> EditorDataChanged { get; set; }
        [Parameter] public ObservableCollection<HierarchicalData> Entities { get; set; }
        [Inject] public AppState AppState { get; set; }
        [Parameter] public bool DuplicateButtonVisible { get; set; } = false;
        [Parameter] public bool CanRefreshGrid { get; set; } = true;
        [Parameter] public bool Editable { get; set; } = true;
        [Parameter] public Action<BrowserDataFilter> DataFilter { get; set; }

        protected readonly IDictionary<string, object> attributes = new Dictionary<string, object>();
        private InputNewGridComponent InputNewGridComponent { get; set; }

        protected virtual GrilleService GetService()
        {
            return GrilleService;
        }

        public EditorData<Bcephal.Models.Grids.Grille> EditorDataBinding
        {
            get { return EditorData; }
            set
            {
                EditorData = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        
        public void ExportData(GrilleExportDataType type)
        {
            InputNewGridComponent.ExportData(InputNewGridComponent.getFilter(), type);
        }

        protected override async Task OnInitializedAsync()
        {
            
            if (attributes != null)
            {
                if (CustomHeaderRenderHandler != null)
                {
                    attributes.Add("CustomHeaderRenderHandler", CustomHeaderRenderHandler);
                }
                if (DataFilter != null)
                {
                    attributes.Add("DataFilter", DataFilter);
                }
            }
            await base.OnInitializedAsync();
        }
    }
}

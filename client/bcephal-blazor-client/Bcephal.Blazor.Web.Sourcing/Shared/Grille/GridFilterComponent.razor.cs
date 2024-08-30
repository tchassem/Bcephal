
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Shared.Grille
{
    public partial class GridFilterComponent : ComponentBase
    {
        [Parameter] public int ActiveTabIndex { get; set; } = 0;
        [Parameter] public EventCallback<int> ActiveTabIndexChanged { get; set; }
        [Inject] private AppState AppState { get; set; }
        [Parameter] public EditorData<Bcephal.Models.Grids.Grille> EditorData { get; set; }
        [Parameter] public ObservableCollection<HierarchicalData> Entities { get; set; }
        [Parameter] public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> EditorDataChanged { get; set; }
        [Parameter] public string Filterstyle { get; set; } = "grid-bc-filtercontent";
        [Parameter]
        public bool CanRefreshGrid { get; set; } = false;

        [Parameter]
        public bool Editable { get; set; } = true;
        public UniverseFilter UserFilterBinding
        {
            get { return EditorData.Item.UserFilter; }
            set
            {
                ShouldRender_ = true;
                EditorData.Item.UserFilter = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public UniverseFilter AdminFilterBinding
        {
            get { return EditorData.Item.AdminFilter; }
            set
            {
                ShouldRender_ = true;
                EditorData.Item.AdminFilter = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public EditorData<Bcephal.Models.Grids.Grille> EditorDataBinding
        {
            get { return EditorData; }
            set
            {
                ShouldRender_ = true;
                EditorData = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public int ActiveTabIndexBinding
        {
            get { return ActiveTabIndex; }
            set
            {
                ShouldRender_ = true;
                ActiveTabIndex = value;
                ActiveTabIndexChanged.InvokeAsync(ActiveTabIndex);
            }
        }

        public bool ShouldRender_ { get; set; } = true;

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            ShouldRender_ = false;
            //Console.WriteLine("OnAfterRenderAsync GridFilterComponent");
            return base.OnAfterRenderAsync(firstRender);
        }

        protected override bool ShouldRender()
        {
            return ShouldRender_;
        }
    }
}

using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Blazor.Web.Sourcing.Shared.Grille;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bcephal.Models.Utils;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Report
{
    public partial class ReportGridComponentForm
    {
        [Inject]
        public GrilleService GrilleService { get; set; }
        public EditorData<Grille> EditorData { get; set; }
        [CascadingParameter]
        public Error Error { get; set; }
        [Parameter]
        public long? Id { get; set; }

        [Inject]
        public AppState AppState { get; set; }
        [Inject]
        public IToastService toastService { get; set; }

        private InputGridComponentForm_ InputGridComponentForm { get; set; }
        public ObservableCollection<HierarchicalData> Entities { get; set; }

        protected override async Task OnParametersSetAsync()
        {
            await base.OnParametersSetAsync();
            await init();
        }

        private async Task init()
        {
            try
            {
                await initComponent();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }
        protected virtual async Task initComponent()
        {
            try
            {
                AppState.ShowLoadingStatus();
                if (EditorData == null)
                {
                    EditorDataFilter filter = new EditorDataFilter();
                    filter.NewData = true;
                    if (Id.HasValue)
                    {
                        filter.NewData = false;
                        filter.Id = Id;
                    }
                    EditorData = await GrilleService.GetEditorData(filter);
                    initModelParams();
                }
                AppState.HideLoadingStatus();
                StateHasChanged();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }
        private void InitEntities()
        {
            int offset = 0;
            List<HierarchicalData> items = new List<HierarchicalData>();
            while (EditorData != null && EditorData.Models != null && offset < EditorData.Models.Count)
            {
                Models.Dimensions.Model model = EditorData.Models[offset];
                int offset2 = 0;
                while (model != null && model.Entities != null && offset2 < model.Entities.Count)
                {
                    items.Add(model.Entities[offset2]);
                    offset2++;
                }
                offset++;
            }
            items.BubbleSort();
            Entities = new ObservableCollection<HierarchicalData>(items);
        }

        protected void initModelParams()
        {
            if (EditorData == null || EditorData.Item == null)
            {
                return;
            }
            if (EditorData.Item is Models.Grids.Grille)
            {
                Models.Grids.Grille grille = EditorData.Item as Models.Grids.Grille;
                if (grille.ColumnListChangeHandler == null)
                {
                    grille.ColumnListChangeHandler = new ListChangeHandler<GrilleColumn>();
                }
                initFilters(grille);
            }
            if (EditorData.Measures == null)
            {
                EditorData.Measures = new ObservableCollection<Models.Dimensions.Measure>();
            }
            if (EditorData.Periods == null)
            {
                EditorData.Periods = new ObservableCollection<Models.Dimensions.Period>();
            }
            if (EditorData.Models == null)
            {
                EditorData.Models = new ObservableCollection<Models.Dimensions.Model>();
            }
            InitEntities();
        }

        protected virtual void initFilters(Models.Grids.Grille grille)
        {
            if (grille == null)
            {
                return;
            }
            if (grille.UserFilter == null)
            {
                grille.UserFilter = new UniverseFilter()
                {
                    AttributeFilter = new AttributeFilter(),
                    PeriodFilter = new PeriodFilter(),
                    MeasureFilter = new MeasureFilter()
                };
            }
            if (grille.AdminFilter == null)
            {
                grille.AdminFilter = new UniverseFilter()
                {
                    AttributeFilter = new AttributeFilter(),
                    PeriodFilter = new PeriodFilter(),
                    MeasureFilter = new MeasureFilter()
                };
            }
        }
        protected GrilleService GetService()
        {
            return GrilleService;
        }
    }
}

using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Spot;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.Spot
{
    public partial class SpotForm : Form<Models.Spot.Spot, BrowserData>
    {
        [Inject]
        public SpotService SpotService { get; set; }

        public List<Nameable> GridList = new List<Nameable>();

        public override string LeftTitle { get { return AppState["New.Spot"]; ; } }

        public override string LeftTitleIcon { get { return "bi-plus"; } }

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            AppState.Update = true;
        }
        protected override string DuplicateName()
        {
            return AppState["duplicate.spot.name", EditorData.Item.Name];
        }
        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        protected override Service<Models.Spot.Spot, BrowserData> GetService()
        {
            return SpotService;
        }

        public override async ValueTask DisposeAsync()
        {
            AppState.Update = false;
            await base.DisposeAsync();
        }

        protected override async Task initComponent()
        {
            try
            {
                AppState.ShowLoadingStatus();
                if (EditorDataBinding == null)
                {
                    await base.initComponent();
                    if (EditorDataBinding.Item != null)
                    {
                        //if (EditorDataBinding.Item.measureFilterItem == null)
                        //{
                        //    EditorDataBinding.Item.measureFilterItem = new Models.Filters.MeasureFilterItem();
                        //}
                        if (EditorDataBinding.Item.Filter == null)
                        {
                            EditorDataBinding.Item.Filter = new Models.Grids.UniverseFilter();
                        }
                    }
                }
                AppState.HideLoadingStatus();
                StateHasChanged();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }

            if(EditorDataBinding.Item.MeasureId != null && EditorDataBinding.Item.MeasureId.HasValue)
            {
                Measure_ = EditorDataBinding.Measures.Where(x => x.Id == EditorDataBinding.Item.MeasureId).ToList().FirstOrDefault();
            }
        }

        public override string GetBrowserUrl { get => Route.BROWSER_SPOT; set => base.GetBrowserUrl = value; }

        public Measure Measure_ { get; set; }

        public void MeasureChanged(Bcephal.Models.Dimensions.Measure Measure__)
        {
            if (Measure__ != null)
            {
                Measure_ = Measure__;
                EditorData.Item.MeasureId = Measure__.Id;
            }
            else
            {
                EditorData.Item.MeasureId = null;
            }
            StateHasChanged();
        }

        public SpotEditorData tempVar { get; set; }

        protected override void AfterInit(EditorData<Models.Spot.Spot> EditorData)
        {
            this.tempVar = (SpotEditorData)EditorData;

            this.GridList = tempVar.Grids.ToList();
            StateHasChanged();
        }

        public string LabelWidth { get; set; } = Constant.GROUP_INFOS_LABEL_WIDTH;

        public string TextWidth { get; set; } = Constant.GROUP_INFOS_TEXT_WIDTH;

        public Nameable Grid_ { get; set; }

        public Nameable Grid
        {
            get
            {
                if (EditorDataBinding.Item.Id.HasValue && EditorDataBinding.Item.GridId.HasValue)
                {
                    ObservableCollection<Nameable> obs = new ObservableCollection<Nameable>(this.GridList.Where(x => x.Id == EditorDataBinding.Item.GridId));
                    if (obs.Any())
                    {
                        Grid_ = obs.FirstOrDefault();
                    }
                }
                return Grid_;
            }
            set
            {
                Grid_ = value;
                EditorDataBinding.Item.GridId = Grid_.Id;
                StateHasChanged();
            }
        }

        public string Description
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.Description;
                }
                return "";
            }
            set
            {
                if (EditorData.Item == null)
                {
                    EditorData.Item = new Models.Spot.Spot();
                }
                EditorData.Item.Description = value;
                AppState.Update = true;
            }
        }

        public decimal? Evaluation { get; set;}

        public async Task Evaluate()
        {
            if (EditorData.Item.MeasureId == null)
            {
                ToastService.ShowError(AppState["unable.to.evaluate.spot", LeftTitle]);
                return;
            }
            AppState.ShowLoadingStatus();
            Evaluation = await SpotService.Evaluate(EditorData.Item);
            AppState.HideLoadingStatus();
        }

        private void RemoveGrid()
        {
            Grid_ = null;
            EditorDataBinding.Item.GridId = null;
            AppState.Update = true;
            StateHasChanged();
        }

    }
}
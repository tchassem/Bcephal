using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation.AutomaticReco;
using Bcephal.Blazor.Web.Reconciliation.Services;
using Bcephal.Blazor.Web.Sourcing.Shared.Grille;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Bcephal.Models.Reconciliation;
using Bcephal.Models.Utils;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation
{
    public partial class ReconciliationForm : Form<ReconciliationModel, BrowserData>
    {
        public override bool usingUnitPane => false;
        public override string LeftTitle { get { return AppState["New.Reconciliation.Filter"]; ; } }
        public bool ShowAutoRecoListModal = false;

        public override string LeftTitleIcon { get { return "bi-plus"; } }

        int ActiveTabIndex { get; set; } = 0;

        [Inject]
        public ReconciliationModelService ReconciliationModelService { get; set; }

        [Parameter]
        public EventCallback<EditorData<ReconciliationModel>> EditorDataChanged { get; set; }

        public bool Editable
        {
            get
            {
                var first = AppState.PrivilegeObserver.CanCreatedReconciliationFilter;
                var second = AppState.PrivilegeObserver.CanEditReconciliationFilter(EditorData.Item);
                return first || second;
            }
        }
        private int ItemCount { get; set; } = 1;

        int ActiveTabIndexFilterLeft { get; set; } = 0;
        int ActiveTabIndexFilterRight { get; set; } = 0;

        private ConfigurationGrid ConfigurationGridLeft { get; set; }
        private AutoRecoBrowser AutoRecoBrowser { get; set; }
        private ConfigurationGrid ConfigurationGridRight { get; set; }
        private ConfigurationGrid ConfigurationGridBottom { get; set; }

        private string LeftKey { get; set; } = "RecoLeftKey";
        private string RightKey { get; set; } = "RecoRightKey";
        private string BottomKey { get; set; } = "RecoBottomKey";

        private EditorData<Models.Grids.Grille> EditorDataBindingLeft_
        {
            get; set;
        }

        private EditorData<Bcephal.Models.Grids.Grille> EditorDataBindingRight_
        {
            get; set;
        }

        private EditorData<Bcephal.Models.Grids.Grille> EditorDataBindingBottom_
        {
            get; set;
        }

        protected override void AfterSave(EditorData<ReconciliationModel> EditorData)
        {
            EditorDataBindingLeft = initEditorData(EditorDataBinding.Item.LeftGrid);
            EditorDataBindingRight = initEditorData(EditorDataBinding.Item.RigthGrid);
            EditorDataBindingBottom = initEditorData(EditorDataBinding.Item.BottomGrid);
        }


        public override string GetBrowserUrl { get => Route.RECONCILIATION_FILTER_BROWSER; set => base.GetBrowserUrl = value; }
        protected override Task BeforeSave(EditorData<ReconciliationModel> EditorData)
        {
            CheckGridColumnsConfig(EditorDataBinding.Item.LeftGrid, EditorDataBinding.Item.LeftMeasureId, DimensionType.MEASURE);
            CheckGridColumnsConfig(EditorDataBinding.Item.LeftGrid, EditorDataBinding.Item.ReconciliatedMeasureId, DimensionType.MEASURE);
            CheckGridColumnsConfig(EditorDataBinding.Item.LeftGrid, EditorDataBinding.Item.RemainningMeasureId, DimensionType.MEASURE);
            CheckGridColumnsConfig(EditorDataBinding.Item.RigthGrid, EditorDataBinding.Item.RigthMeasureId, DimensionType.MEASURE);
            CheckGridColumnsConfig(EditorDataBinding.Item.RigthGrid, EditorDataBinding.Item.ReconciliatedMeasureId, DimensionType.MEASURE);
            CheckGridColumnsConfig(EditorDataBinding.Item.RigthGrid, EditorDataBinding.Item.RemainningMeasureId, DimensionType.MEASURE);
            CheckGridColumnsConfig(EditorDataBinding.Item.BottomGrid, EditorDataBinding.Item.LeftMeasureId, DimensionType.MEASURE);
            CheckGridColumnsConfig(EditorDataBinding.Item.BottomGrid, EditorDataBinding.Item.ReconciliatedMeasureId, DimensionType.MEASURE);
            CheckGridColumnsConfig(EditorDataBinding.Item.BottomGrid, EditorDataBinding.Item.RemainningMeasureId, DimensionType.MEASURE);
            return Task.CompletedTask;
        }


        private void CheckGridColumnsConfig(Bcephal.Models.Grids.Grille grille, long? DimensionId, DimensionType type)
        {
            object colum_ = null;
            foreach (var column in grille.ColumnListChangeHandler.GetItems())
            {
                if (column != null && column.Type.Equals(type) && column.DimensionId == DimensionId)
                {
                    colum_ = column;
                }
            }
            if(colum_ == null)
            {
                if (DimensionType.ATTRIBUTE.Equals(type))
                {
                    if (DimensionId.HasValue)
                    {
                        HierarchicalData colum = FindAttributeFromEntities(DimensionId.Value);
                        if (colum != null)
                        {
                            addAttributColumn(grille, colum);
                        }
                    }
                   
                }else
                    if (DimensionType.PERIOD.Equals(type))
                {
                    if (DimensionId.HasValue)
                    {
                        Period colum = FindPeriods(DimensionId.Value);
                        if (colum != null)
                        {
                            addPeriodColumn(grille, colum);
                        }
                    }
                }
                else
                    if (DimensionType.MEASURE.Equals(type))
                {
                    if (DimensionId.HasValue)
                    {
                        Measure colum = FindMeasures(DimensionId.Value);
                        if (colum != null)
                        {
                            addMeasureColumn(grille, colum);
                        }
                    }
                }
            }
        }

        private HierarchicalData FindAttributeFromEntities(long id)
        {
            HierarchicalData item = null;
            foreach (var entity in Entities)
            {
                item = FindAttributeFromEntity(entity, id);
                if(item != null)
                {
                    return item;
                }
            }
            return item;
        }
      private HierarchicalData FindAttributeFromEntity(HierarchicalData entity_, long id)
        {
            if(entity_ == null)
            {
                return null;
            }
            Entity entity = entity_ as Entity;
            if (entity.Attributes != null)
            {
                foreach (var attribute in entity.Attributes)
                {
                    if (attribute.Id.HasValue && attribute.Id.Value == id)
                    {
                        return attribute;
                    }
                    else
                    {
                        if (attribute.Children != null && attribute.Children.Count > 0)
                        {
                            Models.Dimensions.Attribute attri = FindAttribute(attribute, id);
                            if (attri != null)
                            {
                                return attri;
                            }
                        }
                    }
                }
            }
            return null;
        }

        private Models.Dimensions.Attribute FindAttribute(Models.Dimensions.Attribute attribute, long id)
        {
            if (attribute == null)
            {
                return null;
            }
            else
            {
                if (attribute.Id.HasValue && attribute.Id.Value == id)
                {
                    return attribute;
                }
            }
            if (attribute.Children != null)
            {
                foreach (var attribute_ in attribute.Children)
                {
                    if (attribute_.Id.HasValue && attribute_.Id.Value == id)
                    {
                        return attribute_;
                    }
                    else
                    {
                        if (attribute_.Children != null && attribute_.Children.Count > 0)
                        {
                            Models.Dimensions.Attribute attri = FindAttribute(attribute_, id);
                            if (attri != null)
                            {
                                return attri;
                            }
                        }
                    }
                }
            }
            return null;
        }

        private Models.Dimensions.Period FindPeriods(long id)
        {
            Models.Dimensions.Period item = null;
            foreach (var period in EditorData.Periods)
            {
                item = FindPeriod(period, id);
                if (item != null)
                {
                    return item;
                }
            }
            return item;
        }

        private Models.Dimensions.Period FindPeriod(Models.Dimensions.Period period, long id)
        {
            if (period == null)
            {
                return null;
            }
            else
            {
                if (period.Id.HasValue && period.Id.Value == id)
                {
                    return period;
                }
            }
            if (period.Children != null)
            {
                foreach (var period_ in period.Children)
                {
                    if (period_.Id.HasValue && period_.Id.Value == id)
                    {
                        return period_;
                    }
                    else
                    {
                        if (period_.Children != null && period_.Children.Count > 0)
                        {
                            Models.Dimensions.Period peri = FindPeriod(period_, id);
                            if (peri != null)
                            {
                                return peri;
                            }
                        }
                    }
                }
            }

                return null;
        }

        private Models.Dimensions.Measure FindMeasures(long id)
        {
            Models.Dimensions.Measure item = null;
            foreach (var measure in EditorData.Measures)
            {
                item = FindMeasure(measure, id);
                if (item != null)
                {
                    return item;
                }
            }
            return item;
        }
        private Models.Dimensions.Measure FindMeasure(Models.Dimensions.Measure measure, long id)
        {
            if (measure == null)
            {
                return null;
            }
            else
            {
                if (measure.Id.HasValue && measure.Id.Value == id)
                {
                    return measure;
                }
            }
            if (measure.Children != null)
            {
                foreach (var measure_ in measure.Children)
                {
                    if (measure_.Id.HasValue && measure_.Id.Value == id)
                    {
                        return measure_;
                    }
                    else
                    {
                        if (measure_.Children != null && measure_.Children.Count > 0)
                        {
                            Models.Dimensions.Measure measu = FindMeasure(measure_, id);
                            if (measu != null)
                            {
                                return measu;
                            }
                        }
                    }
                }
            }
            return null;
        }

        public EditorData<Models.Grids.Grille> EditorDataBindingLeft
        {
            get => EditorDataBindingLeft_;
            set {
                EditorDataBindingLeft_ = value;
                EditorDataBinding.Item.LeftGrid = EditorDataBindingLeft_.Item;
                AppState.Update = true;
            }
        }

        public EditorData<Bcephal.Models.Grids.Grille> EditorDataBindingRight
        {
            get => EditorDataBindingRight_;
            set
            {
                EditorDataBindingRight_ = value;
                EditorDataBinding.Item.RigthGrid = EditorDataBindingRight_.Item;
                AppState.Update = true;
            }
        }

        public EditorData<Bcephal.Models.Grids.Grille> EditorDataBindingBottom
        {
            get => EditorDataBindingBottom_;
            set
            {
                EditorDataBindingBottom_ = value;
                EditorDataBinding.Item.BottomGrid = EditorDataBindingBottom_.Item;
                AppState.Update = true;
            }
        }

        private long? LeftGridId
        {
            get
            {
                if (EditorDataBinding != null && EditorDataBinding.Item != null && EditorDataBinding.Item.LeftGrid != null)
                {
                    return EditorDataBinding.Item.LeftGrid.Id;
                }
                else
                {
                    return null;
                }
            }
        }
        private long? RightGridId
        {
            get
            {
                if (EditorDataBinding != null && EditorDataBinding.Item != null && EditorDataBinding.Item.RigthGrid != null)
                {
                    return EditorDataBinding.Item.RigthGrid.Id;
                }
                else
                {
                    return null;
                }
            }
        }

        private long? BottomGridId
        {
            get
            {
                if (EditorDataBinding != null && EditorDataBinding.Item != null && EditorDataBinding.Item.BottomGrid != null)
                {
                    return EditorDataBinding.Item.BottomGrid.Id;
                }
                else
                {
                    return null;
                }
            }
        }

        protected override void AfterInit(EditorData<ReconciliationModel> EditorData)
        {
            EditorDataBindingLeft = initEditorData(EditorDataBinding.Item.LeftGrid);
            EditorDataBindingRight = initEditorData(EditorDataBinding.Item.RigthGrid);
            EditorDataBindingBottom = initEditorData(EditorDataBinding.Item.BottomGrid);
            AppState.Update = false;

            if (EditorDataBinding.Item.WriteOffModel == null)
            {
                EditorDataBinding.Item.WriteOffModel = new();
            }

            AppState.HideLoadingStatus();
            if (!EditorData.Item.Id.HasValue)
            {
                EditorDataBindingLeft.Item.UseLink = true;
                EditorDataBindingRight.Item.UseLink = true;
                EditorDataBindingBottom.Item.UseLink = true;
            }
            ShouldRender_ = true;
            StateHasChanged();
        }

        //protected override async Task initComponent()
        //{
            
        //    try
        //    {
        //        AppState.ShowLoadingStatus();                
        //        if (EditorDataBinding == null)
        //        {
        //          await base.initComponent();
        //        }
               
        //        EditorDataBindingLeft = initEditorData(EditorDataBinding.Item.LeftGrid);
        //        EditorDataBindingRight = initEditorData(EditorDataBinding.Item.RigthGrid);
        //        EditorDataBindingBottom = initEditorData(EditorDataBinding.Item.BottomGrid);
        //        AppState.Update = false;

        //        if (EditorDataBinding.Item.WriteOffModel == null)
        //        {
        //            EditorDataBinding.Item.WriteOffModel = new();
        //            //await EditorDataChanged.InvokeAsync(EditorData);
        //        }

        //        AppState.HideLoadingStatus();
        //        if(!EditorData.Item.Id.HasValue)
        //        {
        //            EditorDataBindingLeft.Item.UseLink = true;
        //            EditorDataBindingRight.Item.UseLink = true;
        //            EditorDataBindingBottom.Item.UseLink = true;
        //        }
        //        ShouldRender_ = true;
        //            StateHasChanged();
               
        //    }
        //    catch (Exception ex)
        //    {
        //        AppState.HideLoadingStatus();
        //        Error.ProcessError(ex);
        //    }
        //}

        private EditorData<Models.Grids.Grille> initEditorData(Models.Grids.Grille grid)
        {
            EditorData<Models.Grids.Grille> editorData = new EditorData<Models.Grids.Grille>();
            editorData.Item = grid;
            editorData.Measures = EditorData.Measures;
            editorData.Periods = EditorData.Periods;
            initFilters(editorData.Item);
            return editorData;
        }


        private void addAttributColumn(Models.Grids.Grille grille, HierarchicalData attribute_)
        {
            Models.Dimensions.Attribute attribute = attribute_ as Models.Dimensions.Attribute;
            if (attribute != null && GridUtils.CheckIfColumnExist(grille, attribute.Name, Bcephal.Models.Filters.DimensionType.ATTRIBUTE, (name) => DuplicateColumnEvent(name) ))
            {
                GrilleColumn grilleColumn = new GrilleColumn();
                grilleColumn.Name = attribute.Name;
                grilleColumn.DimensionName = attribute.Name;
                grilleColumn.Type = DimensionType.ATTRIBUTE;
                grilleColumn.DimensionId = attribute.Id.Value;
                grille.AddColumn(grilleColumn);
            }
        }

        private void addMeasureColumn(Models.Grids.Grille grille, Models.Dimensions.Measure measure)
        {
            if (measure != null && GridUtils.CheckIfColumnExist(grille, measure.Name, Bcephal.Models.Filters.DimensionType.MEASURE, (name) => DuplicateColumnEvent(name) ))
            {
                GrilleColumn grilleColumn = new GrilleColumn();
                grilleColumn.Name = measure.Name;
                grilleColumn.DimensionName = measure.Name;
                grilleColumn.Type = DimensionType.MEASURE;
                grilleColumn.DimensionId = measure.Id.Value;
                grille.AddColumn(grilleColumn);
            }
        }

        private void addPeriodColumn(Models.Grids.Grille grille, Models.Dimensions.Period period)
        {
            if (period != null && GridUtils.CheckIfColumnExist(grille, period.Name, Bcephal.Models.Filters.DimensionType.PERIOD, (name) => DuplicateColumnEvent(name)))
            {
                GrilleColumn grilleColumn = new GrilleColumn();
                grilleColumn.Name = period.Name;
                grilleColumn.DimensionName = period.Name;
                grilleColumn.Type = DimensionType.PERIOD;
                grilleColumn.DimensionId = period.Id.Value;
                grille.AddColumn(grilleColumn);
            }
        }

        private void addMeasureColumn(Models.Dimensions.Measure measure)
        {
            AppState.Update = true;
            //StateHasChanged();
        }

        private void addPeriodColumn(Models.Dimensions.Period period)
        {
            AppState.Update = true;
            //StateHasChanged();
        }

        private void addAttributColumn(HierarchicalData attribute_)
        {
            AppState.Update = true;
            //StateHasChanged();
        }

        protected override ReconciliationModelService GetService()
        {
            return ReconciliationModelService;
        }

        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        public override Task SetParametersAsync(ParameterView parameters)
        {
            usingMixPane = false;
            displayRight = DISPLAY_NONE;
            displayLeft = WIDTH_100;
            return base.SetParametersAsync(parameters);
        }

        protected override Task OnInitializedAsync()
        {
            if (!Id.HasValue)
            {
                ActiveTabIndex = 1;
            }
            return base.OnInitializedAsync();
        }

        protected override void OnAfterRender(bool firstRender)
        {
            base.OnAfterRender(firstRender);
            if (firstRender)
            {
                ShouldRender_ = false;
                AppState.CanRunAutoReconciliation = true;
                AppState.AutoReconciliationRunHander += ShowAutoRecoBrowserModal;

                AppState.PublishedHander += Publish;
                AppState.ResetPublicationHandler += ResetPublication;
                AppState.RefreshPublicationHandler += RefreshPublication;
            }

            if (EditorData != null && EditorData.Item != null && EditorData.Item.Id.HasValue)
            {
                AppState.CanPublished = !EditorData.Item.Published;
                AppState.CanResetPublication = EditorData.Item.Published;
                AppState.CanRefreshPublication = EditorData.Item.Published;
            }
        }

        public override ValueTask DisposeAsync()
        {
            AppState.CanRunAutoReconciliation = false;
            AppState.AutoReconciliationRunHander -= ShowAutoRecoBrowserModal;

            AppState.CanPublished = false;
            AppState.PublishedHander -= Publish;
            AppState.CanResetPublication = false;
            AppState.CanRefreshPublication = false;
            AppState.ResetPublicationHandler -= ResetPublication;
            AppState.RefreshPublicationHandler -= RefreshPublication;
            return base.DisposeAsync();
        }


        private async void Publish()
        {
              await GetService().Publish(EditorData.Item.Id.Value);
            EditorDataBinding.Item.Published = true;
            AfterInit(EditorDataBinding);
            if (EditorData != null && EditorData.Item != null && EditorData.Item.Id.HasValue)
            {
                AppState.CanPublished = !EditorData.Item.Published;
                AppState.CanResetPublication = EditorData.Item.Published;
                AppState.CanRefreshPublication = EditorData.Item.Published;
            }
            //StateHasChanged();
        }

        private async void ResetPublication()
        {
            await GetService().ResetPublication(EditorData.Item.Id.Value);
            EditorDataBinding.Item.Published = false;
            AfterInit(EditorDataBinding);
            if (EditorData != null && EditorData.Item != null && EditorData.Item.Id.HasValue)
            {
                AppState.CanPublished = !EditorData.Item.Published;
                AppState.CanResetPublication = EditorData.Item.Published;
                AppState.CanRefreshPublication = EditorData.Item.Published;
            }
            //StateHasChanged();
        }
        private async void RefreshPublication()
        {
            await GetService().RefreshPublication(EditorData.Item.Id.Value);
            if (EditorData != null && EditorData.Item != null && EditorData.Item.Id.HasValue)
            {
                AppState.CanPublished = !EditorData.Item.Published;
                AppState.CanResetPublication = EditorData.Item.Published;
                AppState.CanRefreshPublication = EditorData.Item.Published;
            }
        }


        /// <summary>
        ///     Cette méthode permet d'appeler le pop up du browser de AutoReco
        /// </summary>
        protected void ShowAutoRecoBrowserModal()
        {
            ShowAutoRecoListModal = !ShowAutoRecoListModal;
            StateHasChanged();
        }

        protected void RunSelectedAutoReco()
        {
            AutoRecoBrowser.RunSelectedAutoReco();
        }

        protected void DuplicateColumnEvent(string name)
        {
            ToastService.ShowError(AppState["duplicate.grid.column", name]);
        }
        protected override string DuplicateName()
        {
            return AppState["duplicate.reconciliation.name", EditorData.Item.Name];
        }
        public void Dispose()
        {
            AppState.Update = false;
        }

        private bool ShouldRender_ { get; set; } = true;
        protected override bool ShouldRender()
        {
            return ShouldRender_;
        }

    }
}
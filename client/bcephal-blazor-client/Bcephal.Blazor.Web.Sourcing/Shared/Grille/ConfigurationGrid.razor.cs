using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Bcephal.Models.Utils;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Shared.Grille
{
    public partial class ConfigurationGrid : ComponentBase
    {
        #region Inject and Parameters
        [Inject] public IToastService toastService { get; set; }
        [Inject]    public AppState AppState { get; set; }
        [Parameter] public EditorData<Bcephal.Models.Grids.Grille> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> EditorDataChanged { get; set; }
        [Parameter] public EventCallback<HierarchicalData> SelectAttributeCallback { get; set; }
        [Parameter] public EventCallback<Models.Dimensions.Measure> SelectMesureCallback { get; set; }
        [Parameter] public EventCallback<Models.Dimensions.Period> SelectPeriodeCallback { get; set; }
        [Parameter] public ObservableCollection<HierarchicalData> Entities { get; set; }
        [Parameter] public bool DisplayGroup { get; set; } = true;
        [Parameter] public bool DisplayVisibleInShortcut { get; set; } = true;
        [Parameter] public bool DisplayEditable { get; set; } = true;
        [Parameter] public RenderFragment<ComponentBase> ChildContent { get; set; }
        [Parameter] public string Name { get; set; } = "ConfigurationGrid";
        [Parameter] public int ActiveTabIndexFilter { get; set; } = 0;
        [Parameter] public EventCallback<int> ActiveTabIndexFilterChanged { get; set; }        
        [Parameter] public ObservableCollection<BrowserData> BGroups { get; set; } = new ObservableCollection<BrowserData>();
        [Parameter] public EventCallback<ObservableCollection<BrowserData>> BGroupsChanged { get; set; }
        [Parameter] public bool DisplayDimensionView { get; set; } = true;
        [Parameter] public bool Editable_ { get; set; } = true;
        [Parameter] public bool CanRefreshGrid { get; set; } = true;
        [Parameter] public string Filterstyle { get; set; }
        [Parameter] public bool SendTag { get; set; } = false;
        [Parameter] public Action<RenderFragment> Filter { get; set; }

        #endregion
        #region properties
        public bool IsSmallScreen { get; set; }
        public string LabelWidth { get; set; } = Constant.GROUP_INFOS_LABEL_WIDTH;
        public string TextWidth { get; set; } = Constant.GROUP_INFOS_TEXT_WIDTH;
        public int ContentTabindex { get; set; } = 0;
        private string ColumnItemFieldKey { get; set; } = "ColumnItemFieldKey";
        private string FilterKey { get; set; } = "FilterKey";
        private string DxTabsKey { get; set; } = "DxTabsKey";
        private string PropertiesKey { get; set; } = "PropertiesKey";
        private string InfosKey { get; set; } = "InfosKey";
        private string  keyGen { get; set; }  = new Guid().ToString("d");
        private ConfigurationGridContent ConfigurationGridContentRef { get; set; }
        private GridDimensions_Configuration GridDimensions_ConfigurationRef { get; set; }
        public bool ShouldRender_ { get; set; } = true;

        public int GrilleColumnPosition { get; set; } = 0;


        #endregion
        #region Binding
        private ObservableCollection<BrowserData> BGroups_
        {
            get => BGroups;
            set
            {
                BGroups = value;
                BGroupsChanged.InvokeAsync(BGroups);
            }
        }

        private int ActiveTabIndexFilterBinding
        {
            get => ActiveTabIndexFilter;
            set
            {
                ActiveTabIndexFilter = value;
                ActiveTabIndexFilterChanged.InvokeAsync(ActiveTabIndexFilter);
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
                AppState.Update = true;
            }
        }

        public bool Editable
        {
            get
            {
                if (EditorData.Item != null)
                {
                    return EditorData.Item.Editable;
                }
                return false;
            }
            set
            {
                ShouldRender_ = true;
                EditorData.Item.Editable = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
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

        #endregion

        public void selectedColumn(Models.Grids.GrilleColumn GrilleColumn__)
        {
            GrilleColumnPosition = GrilleColumn__.Position;
            ShouldRender_ = true;
        }
        public void SetSelectedDimension(Models.Grids.GrilleDimension grilleDimension)
        {
            GridDimensions_ConfigurationRef.SetSelectedDimension(grilleDimension);
            ShouldRender_ = true;
        }

        

        #region override method
        public override async Task SetParametersAsync(ParameterView parameters)
        {
            await base.SetParametersAsync(parameters);
            if (ColumnItemFieldKey.Equals("ColumnItemFieldKey"))
            {
                ColumnItemFieldKey = $"{Name}{ColumnItemFieldKey}";
                FilterKey = Name + FilterKey;
                DxTabsKey = Name + DxTabsKey;
                PropertiesKey = Name + PropertiesKey;
                InfosKey = Name + InfosKey;
            }
        }

        protected override bool ShouldRender()
        {
            return ShouldRender_;
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            ShouldRender_ = false;
            if (firstRender)
            {
                if (!SendTag)
                {
                    Filter?.Invoke(RightContentSend);
                }
                else
                {
                    Filter?.Invoke(RightContentSendTag);
                }
            }
            //Console.WriteLine("OnAfterRenderAsync ConfigurationGrid");
            return base.OnAfterRenderAsync(firstRender);
        }

        #endregion

        #region other method
        private void addMeasureColumn(Models.Grids.Grille grille, Models.Dimensions.Measure measure)
        {
            // On verifie si on est dans le tab des columns ou dans le tab des dimensions
            //  afin de savoir dans quelle liste on doit ajouter cet élément
            if (ContentTabindex == 0 && measure != null)
            {
                if (GridUtils.CheckIfColumnExist(grille, measure.Name, Bcephal.Models.Filters.DimensionType.MEASURE, (name) => DuplicateColumnEvent(name)))
                {
                    GrilleColumn grilleColumn = new GrilleColumn();
                    grilleColumn.Name = measure.Name;
                    grilleColumn.DimensionName = measure.Name;
                    grilleColumn.Type = DimensionType.MEASURE;
                    grilleColumn.DimensionId = measure.Id.Value;
                    EditorData.Item.AddColumn(grilleColumn);
                    selectedColumn(grilleColumn);
                    SelectMesureCallback.InvokeAsync(measure);
                }
                else
                {
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
            else if (ContentTabindex == 1 && measure != null)
            {
                if (GridUtils.CheckIfDimensionExist(grille, measure.Name, Bcephal.Models.Filters.DimensionType.MEASURE, (name) => DuplicateDimensionEvent(name)))
                {
                    GrilleDimension grilleDimension = new GrilleDimension();
                    grilleDimension.Name = measure.Name;
                    grilleDimension.Type = DimensionType.MEASURE;
                    grilleDimension.DimensionId = measure.Id.Value;
                    EditorData.Item.AddDimension(grilleDimension);
                    SetSelectedDimension(grilleDimension);
                    SelectMesureCallback.InvokeAsync(measure);
                }
                else
                {
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        private void addPeriodColumn(Models.Grids.Grille grille, Models.Dimensions.Period period)
        {
            // On verifie si on est dans le tab des columns ou dans le tab des dimensions
            //  afin de savoir dans quelle liste on doit ajouter cet élément
            if (ContentTabindex == 0 && period != null)
            {
                if (GridUtils.CheckIfColumnExist(grille, period.Name, Bcephal.Models.Filters.DimensionType.PERIOD, (name) => DuplicateColumnEvent(name)))
                {
                    GrilleColumn grilleColumn = new GrilleColumn();
                    grilleColumn.Name = period.Name;
                    grilleColumn.DimensionName = period.Name;
                    grilleColumn.Type = DimensionType.PERIOD;
                    grilleColumn.DimensionId = period.Id.Value;
                    EditorData.Item.AddColumn(grilleColumn);
                    selectedColumn(grilleColumn);
                    SelectPeriodeCallback.InvokeAsync(period);
                }
                else
                {
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
            else if (ContentTabindex == 1 && period != null)
            {
                if (GridUtils.CheckIfDimensionExist(grille, period.Name, Bcephal.Models.Filters.DimensionType.PERIOD, (name) => DuplicateDimensionEvent(name)))
                {
                    GrilleDimension grilleDimension = new GrilleDimension();
                    grilleDimension.Name = period.Name;
                    grilleDimension.Type = DimensionType.PERIOD;
                    grilleDimension.DimensionId = period.Id.Value;
                    EditorData.Item.AddDimension(grilleDimension);
                    SetSelectedDimension(grilleDimension);
                    SelectPeriodeCallback.InvokeAsync(period);
                }
                else
                {
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        private void addAttributColumn(Models.Grids.Grille grille, HierarchicalData attribute_)
        {
            Models.Dimensions.Attribute attribute = attribute_ as Models.Dimensions.Attribute;
            // On verifie si on est dans le tab des columns ou dans le tab des dimensions
            //  afin de savoir dans quelle liste on doit ajouter cet élément
            if (ContentTabindex == 0 && attribute != null)
            {
                if (GridUtils.CheckIfColumnExist(grille, attribute.Name, Bcephal.Models.Filters.DimensionType.ATTRIBUTE, (name) => DuplicateColumnEvent(name)))
                {
                    GrilleColumn grilleColumn = new GrilleColumn();
                    grilleColumn.Name = attribute.Name;
                    grilleColumn.DimensionName = attribute.Name;
                    grilleColumn.Type = DimensionType.ATTRIBUTE;
                    grilleColumn.DimensionId = attribute.Id.Value;
                    EditorData.Item.AddColumn(grilleColumn);
                    selectedColumn(grilleColumn);
                    SelectAttributeCallback.InvokeAsync(attribute_);
                }
                else
                {
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
            else if (ContentTabindex == 1 && attribute != null)
            {
                if (GridUtils.CheckIfDimensionExist(grille, attribute.Name, Bcephal.Models.Filters.DimensionType.ATTRIBUTE, (name) => DuplicateDimensionEvent(name)))
                {
                    GrilleDimension grilleDimension = new GrilleDimension();
                    grilleDimension.Name = attribute.Name;
                    grilleDimension.Type = DimensionType.ATTRIBUTE;
                    grilleDimension.DimensionId = attribute.Id.Value;
                    EditorData.Item.AddDimension(grilleDimension);
                    SetSelectedDimension(grilleDimension);
                    SelectAttributeCallback.InvokeAsync(attribute_);
                }
                else
                {
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        protected void DuplicateColumnEvent(string name)
        {
            toastService.ShowError(AppState["duplicate.grid.column", name]);
        }

        protected void DuplicateDimensionEvent(string name)
        {
            toastService.ShowError(AppState["duplicate.grid.dimension", name]);
        }

        #endregion
    }
}

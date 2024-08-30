using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Filters;
using Bcephal.Models.Routines;
using Microsoft.AspNetCore.Components;
using Bcephal.Models.Grids;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Sourcing.Services;

namespace Bcephal.Blazor.Web.Planification.Shared.Routine
{
    public partial class RoutineItemComponent : ComponentBase
    {
        #region :: Properties and attributes section ::

        [Inject] public AppState AppState { get; set; }
        [Inject] public GrilleService GrilleService { get; set; }

        [Parameter] public TransformationRoutineItem TRoutineItem { get; set; }
        [Parameter] public EventCallback<TransformationRoutineItem> TRoutineItemChanged { get; set; }
        [Parameter] public EventCallback<TransformationRoutineItem> TRoutineItemDeleted { get; set; }
        [Parameter] public ObservableCollection<HierarchicalData> Attributes { get; set; }
        [Parameter] public ObservableCollection<HierarchicalData> Measures { get; set; }
        [Parameter] public ObservableCollection<HierarchicalData> Periods { get; set; }
        [Parameter] public ObservableCollection<GrilleBrowserData> Grilles_ { get; set; } 
        [Parameter] public ObservableCollection<Nameable> Routines { get; set; }
        [Parameter] public string ItemCssClass { get; set; }

        private DimensionType? Type {
            get {
                if (TRoutineItem.Type.IsAttribute() || TRoutineItem.Type.IsPeriod() || TRoutineItem.Type.IsMeasure())
                {
                    return TRoutineItem.Type;
                }
                return null;
            }
            set {
                TRoutineItem.Type = value.Value;
                ResetConcernedDimensions();
                TRoutineItemChanged.InvokeAsync(TRoutineItem);
            }
        }
        private GrilleBrowserData Grille {
            get {
                if (Grilles_ != null && Grilles_.Count > 0 && TRoutineItem.TargetGridId.HasValue)
                {
                    return Grilles_.FirstOrDefault(g => g.Id == TRoutineItem.TargetGridId);
                }
                return null;
            }
            set {
                TRoutineItem.TargetGridId = value.Id;
                TRoutineItemChanged.InvokeAsync(TRoutineItem);
            }
        }
        private bool ApplyOnlyIfEmpty
        {
            get => TRoutineItem.ApplyOnlyIfEmpty;
            set
            {
                TRoutineItem.ApplyOnlyIfEmpty = value;
                TRoutineItemChanged.InvokeAsync(TRoutineItem);
            }
        }
        private UniverseFilter Filter
        {
            get => TRoutineItem.Filter;
            set
            {
                TRoutineItem.Filter = value;
                TRoutineItemChanged.InvokeAsync(TRoutineItem);
            }
        }

        private HierarchicalData targetDimension;
        private HierarchicalData TargetDimension
        {
            get
            {
                if (Dimensions != null && Dimensions.Count > 0 && TRoutineItem.TargetDimensionId.HasValue)
                {
                    if (targetDimension != null && targetDimension.Id.Value == TRoutineItem.TargetDimensionId.Value)
                    {
                        return targetDimension;
                    }
                    else
                    {
                        if (TRoutineItem.Type.IsAttribute())
                        {
                            foreach (Models.Dimensions.Entity entity in Dimensions)
                            {
                                targetDimension = entity.Attributes.FirstOrDefault(d => d.Id == TRoutineItem.TargetDimensionId.Value);
                                if (targetDimension != null && targetDimension.Id.HasValue)
                                {
                                    return targetDimension;
                                }
                            }
                        }
                        else if (TRoutineItem.Type.IsPeriod() || TRoutineItem.Type.IsMeasure())
                        {
                            targetDimension = Dimensions.FirstOrDefault(d => d.Id == TRoutineItem.TargetDimensionId.Value);
                            if (targetDimension != null && targetDimension.Id.HasValue)
                            {
                                return targetDimension;
                            }
                        }
                    }
                }
                return null;
            }
            set {
                TRoutineItem.TargetDimensionId = value.Id;
                targetDimension = value;
                TRoutineItemChanged.InvokeAsync(TRoutineItem);
            }
        }
        private TransformationRoutineField SourceField
        {
            get
            {
                if (TRoutineItem.SourceField == null)
                {
                    TRoutineItem.SourceField = new TransformationRoutineField();
                }
                return TRoutineItem.SourceField;
            }
            set {
                TRoutineItem.SourceField = value;
                TRoutineItemChanged.InvokeAsync(TRoutineItem);
            }
        }

        private Bcephal.Models.Grids.Grille TargetGrid
        {
            get
            {
                //if (Dimensions.Count > 0 && TRoutineItem.TargetDimensionId.HasValue)
                //{
                //    return Dimensions.FirstOrDefault(d => d.Id == TRoutineItem.TargetDimensionId.Value);
                //}
                return null;
            }
            set {
                TRoutineItem.TargetDimensionId = value.Id;
                TRoutineItemChanged.InvokeAsync(TRoutineItem);
            }
        }

        private string textLength = "0.8fr";
        bool IsXSmallScreen;
        IEnumerable<DimensionType?> dimensionTypes;
        IEnumerable<string> periodGranularities;
        public ObservableCollection<HierarchicalData> Dimensions { get; set; }
        #endregion

        #region :: Methods section ::
        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            dimensionTypes = new List<DimensionType?>(){
                                DimensionType.ATTRIBUTE,
                                DimensionType.MEASURE,
                                DimensionType.PERIOD };

            if ( periodGranularities == null)
            {
                periodGranularities = PeriodGranularity.DAY.GetAll(text => AppState[text]);
            }

            Dimensions = getDimensions(TRoutineItem.Type);
        }

        protected override void OnAfterRender(bool firstRender)
        {
            if(Dimensions == null)
            {
                Dimensions = getDimensions(TRoutineItem.Type);
                if (Dimensions != null)
                {
                    StateHasChanged();
                }
            }
        }

        protected ObservableCollection<HierarchicalData> getDimensions(DimensionType type)
        {
            switch (type)
            {
                case DimensionType.MEASURE:
                    return Measures;
                case DimensionType.PERIOD:
                    return Periods;
                case DimensionType.ATTRIBUTE:
                    return Attributes;
                default:
                    return new ObservableCollection<HierarchicalData>();
            }
        }

        public string GetSpacing(bool isHorizontal)
        {
            return isHorizontal ? "4px" : "0px";
        }

        public void TargetDimensionChanged(HierarchicalData hierarchicalData)
        {
            TargetDimension = hierarchicalData;
        }

        public void OnSourceFieldChanged(TransformationRoutineField field)
        {
            TRoutineItemChanged.InvokeAsync(TRoutineItem);
        }

        public void OnConcatenateItemsChanged(ListChangeHandler<TransformationRoutineConcatenateItem> ItemsHandler)
        {
            TRoutineItemChanged.InvokeAsync(TRoutineItem);
        }

        public void OnCalculateItemsChanged(ListChangeHandler<TransformationRoutineCalculateItem> ItemsHandler)
        {
            TRoutineItemChanged.InvokeAsync(TRoutineItem);
        }

        public void ResetConcernedDimensions()
        {
            TRoutineItem.TargetDimensionId = null;
            SourceField.DimensionId = null;
            Dimensions = null;
            StateHasChanged();
        }
        #endregion
    }
}

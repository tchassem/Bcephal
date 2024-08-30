using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Bcephal.Models.Base;
using Bcephal.Models.Reconciliation;
using System.Collections.ObjectModel;
using Bcephal.Models.Grids;
using Bcephal.Models.Filters;
using Bcephal.Blazor.Web.Base.Services;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation
{
    public partial class RecoEnrichValueComponent : ComponentBase
    {
        [Parameter]
        public ReconciliationModelEnrichment Item { get; set; }

        [Parameter]
        public EventCallback<ReconciliationModelEnrichment> CallBackRemove { get; set; }

        [Parameter]
        public EventCallback<ReconciliationModelEnrichment> CallBackAddorUpdate { get; set; }

        [Parameter]
        public bool TargetColumnSelected { get; set; }

        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public EditorData<Bcephal.Models.Grids.Grille> EditorDataBindingLeft { get; set; }
        [Parameter]
        public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> EditorDataBindingLeftChanged { get; set; }

        [Parameter]
        public EditorData<Bcephal.Models.Grids.Grille> EditorDataBindingRight { get; set; }
        [Parameter]
        public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> EditorDataBindingRightChanged { get; set; }

        [Parameter]
        public ReconciliationModelEditorData EnrichmentValueEditorData { get; set; }

        [Parameter]
        public Action AddRenderNext { get; set; }

        [Parameter]
        public bool RemoveButton { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        public GrilleColumn SelectedTargetColumn { get; set; }

        public bool IsSpecific { get; set; } = true;

        public GrilleColumn TargetColumn
        {
            get
            {

                if (Item.TargetColumnId.HasValue)
                {
                    List<Models.Grids.GrilleColumn> Obs = EnrichValuesTargetSide.Where(x => x.Id == Item.TargetColumnId).ToList();
                    if (Obs != null && Obs.Any())
                    {
                        SelectedTargetColumn = Obs.First();
                    }
                }
                else
                {
                    SelectedTargetColumn = new GrilleColumn();
                }

                return SelectedTargetColumn;
            }
            set
            {
                long? Id = Item.TargetColumnId;
                SelectedTargetColumn = value;
                Item.TargetColumnId = SelectedTargetColumn.Id;
                Item.DateValue = new();

                if (SelectedTargetColumn.IsMeasure)
                {
                    Item.DimensionType = DimensionType.MEASURE;
                }
                else if (SelectedTargetColumn.IsPeriod)
                {
                    Item.DimensionType = DimensionType.PERIOD;
                }
                else if (SelectedTargetColumn.IsAttribute)
                {
                    Item.DimensionType = DimensionType.ATTRIBUTE;
                }

                CallBackAddorUpdate.InvokeAsync(Item);
                RemoveButton = true;
                if (!Id.HasValue)
                {
                    AddRenderNext.Invoke();
                }

            }
        }

        public GrilleColumn SelectedSourceColumn_ { get; set; }

        public GrilleColumn SourceColumn
        {
            get
            {
                if (Item.SourceColumnId.HasValue)
                {
                    GetColumnsSourceSide();
                    List<Models.Grids.GrilleColumn> Obs = EnrichValuesSourceSide.Where(x => x.Id == Item.SourceColumnId).ToList();

                    if (Obs != null && Obs.Count > 0)
                    {
                        SelectedSourceColumn_ = Obs.First();
                    }
                }
                else
                {
                    SelectedSourceColumn_ = new GrilleColumn();
                }

                return SelectedSourceColumn_;
            }
            set
            {
                SelectedSourceColumn_ = value;
                Item.SourceColumnId = SelectedSourceColumn_.Id;
                CallBackAddorUpdate.InvokeAsync(Item);
            }
        }

        public string AttributeValue_ = null;
        public string AttributeValue
        {
            get
            {
                if (!String.IsNullOrEmpty(Item.StringValue))
                {
                    AttributeValue_ = Item.StringValue;
                }
                return String.IsNullOrEmpty(Item.StringValue) ? AttributeValue_ : Item.StringValue;
            }
            set
            {
                AttributeValue_ = value;
                Item.StringValue = AttributeValue_;
                CallBackAddorUpdate.InvokeAsync(Item);
            }
        }


        public PeriodOperator DateOperator_ { get; set; }

        public PeriodOperator DateOperator
        {
            get
            {
                if(Item.DateValue != null)
                {
                    DateOperator_ = Item.DateValue.DateOperator;
                    CheckedPeriodOperator(DateOperator_);
                }
                return DateOperator_;
            }
            set
            {
                DateOperator_ = value;
                Item.DateValue.DateOperator = DateOperator_;
                CheckedPeriodOperator(DateOperator_);
                CallBackAddorUpdate.InvokeAsync(Item);
            }
        }


        string sign = null;

        public string Sign
        {
            get
            {
                return Item.DateValue == null ? sign : Item.DateValue.DateSign;
            }
            set
            {
                sign = value;
                Item.DateValue.DateSign = sign;
                CallBackAddorUpdate.InvokeAsync(Item);
                StateHasChanged();
            }
        }

        public int? Number_ { get; set; }
        public int? Number
        {
            get 
            { 
                return Item.DateValue == null ? Number_ : Item.DateValue.DateNumber; 
            }
            set
            {
                Number_ = value;
                Item.DateValue.DateNumber = Number_.Value;
                CallBackAddorUpdate.InvokeAsync(Item);
            }
        }

        public PeriodGranularity? Granularity_ = null;

        public PeriodGranularity? Granularity
        {
            get
            {
                return Item.DateValue == null ? Granularity_ : Item.DateValue.DateGranularity;
            }

            set
            {
                Granularity_ = value.Value;
                Item.DateValue.DateGranularity = Granularity_.Value;
                CallBackAddorUpdate.InvokeAsync(Item);
               
            }
        }


        public Decimal DecimalValue_ { get; set; }

        public Decimal? DecimalValue
        {
            get
            {
                if (Item.DecimalValue.HasValue)
                {
                    DecimalValue_ = Item.DecimalValue.Value;
                }
                return DecimalValue_;
            }
            set
            {
                DecimalValue_ = value.Value;
                Item.DecimalValue = DecimalValue_;
                CallBackAddorUpdate.InvokeAsync(Item);
            }

        }

        public PeriodValue DateValue_ { get; set; }
        public PeriodValue DateValue
        {
            get
            {
                if (Item.DateValue !=null)
                {
                    DateValue_ = Item.DateValue;
                   
                }
                return DateValue_;
            }
            set
            {
                DateValue_ = value;
                Item.DateValue = DateValue_;
                CallBackAddorUpdate.InvokeAsync(Item);
            }

        }

        public ReconciliationModelSide SelectedTargetSide
        {
            get { return Item.TargetModelSide; }
            set
            {
                Item.TargetModelSide = value;
                GetColumnTargetSide();
                CallBackAddorUpdate.InvokeAsync(Item);
                StateHasChanged();
            }
        }

        public ReconciliationModelSide SelectedSourceSide_ { get; set; }
        public ReconciliationModelSide SelectedSourceSide
        {
            get 
            {
                return Item.SourceModelSide;
            }
            set
            {
                Item.SourceModelSide = value;
                GetColumnsSourceSide();
                CallBackAddorUpdate.InvokeAsync(Item);
            }
        }

        public ObservableCollection<Models.Grids.GrilleColumn> EnrichValuesSourceSide { get; set; }

        public ObservableCollection<ReconciliationModelSide> ReconciliationModelSideConditions { get; set; }


        public ObservableCollection<GrilleColumn> EnrichValuesTargetSide { get; set; } = new ObservableCollection<GrilleColumn>();


        public void GetColumnTargetSide()
        {
            if (Item.TargetModelSide != null && Item.TargetModelSide.Equals(ReconciliationModelSide.LEFT))
            {
                EnrichValuesTargetSide = EnrichValuesTargetSideLeft;
            }
            else if (Item.TargetModelSide != null && Item.TargetModelSide.Equals(ReconciliationModelSide.RIGHT))
            {
                EnrichValuesTargetSide = EnrichValuesTargetSideRight;
            }
        }


        public  void GetColumnsSourceSide()
        {
            DimensionType _Type = Item.DimensionType;
            if (Item.SourceModelSide.Equals(ReconciliationModelSide.LEFT))
            {

                EnrichValuesSourceSide = new ObservableCollection<GrilleColumn>(EditorDataBindingLeft.Item.GetPersistentColumns().Where(x => x.Type == Item.DimensionType).ToList());
               
            }
            else if (Item.SourceModelSide.Equals(ReconciliationModelSide.RIGHT))
            {
                EnrichValuesSourceSide = new ObservableCollection<GrilleColumn>(EditorDataBindingRight.Item.GetPersistentColumns().Where(x => x.Type == Item.DimensionType).ToList());
            }
          
        }

        [Parameter]
        public ObservableCollection<GrilleColumn> EnrichValuesTargetSideLeft { get; set; }

        [Parameter]
        public ObservableCollection<GrilleColumn> EnrichValuesTargetSideRight { get; set; }

        protected override Task OnInitializedAsync()
        {
            ReconciliationModelSideConditions = ReconciliationModelSide.GetSides();
            ReconciliationModelSideConditions.Remove(ReconciliationModelSide.CUSTOM);
            GetColumnTargetSide();
            return base.OnInitializedAsync();
        }

        public void CheckedPeriodOperator(PeriodOperator DateOperator_)
        {
            IsSpecific = PeriodOperatorExtensionMethods.IsSpecific(DateOperator_);
        }


    }


}

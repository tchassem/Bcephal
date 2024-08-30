using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Bcephal.Models.Reconciliation;
using Microsoft.AspNetCore.Components;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation
{
    public partial class ReconciliationConditionComponent: ComponentBase
    {

        #region :: Parameters and attributes section ::
        [Parameter]
        public ReconciliationCondition Condition { get; set; }
        [Parameter]
        public bool IsNew { get; set; } = true;
        ReconciliationCondition tempReco = new ReconciliationCondition() { };

        [Parameter]
        public ReconciliationModelColumns ReconciliationModelColumns { get; set; }

        [Parameter]
        public EventCallback<ReconciliationCondition> ConditionChanged { get; set; }

        [Parameter]
        public EventCallback<ReconciliationCondition> OnClickBtnRemove { get; set; }


        [Parameter]
        public bool Editable { get; set; } = true;
        public ObservableCollection<GrilleColumn> FirstDimensionGroup
        {
            get
            {
                if (Condition.ModelSide1 != null)
                {
                    return Condition.ModelSide1 == ReconciliationModelSide.LEFT
                                ? ReconciliationModelColumns.LeftColumns
                                : ReconciliationModelColumns.RightColumns;
                }
                return new ObservableCollection<GrilleColumn>();

            }
        }
        public ObservableCollection<GrilleColumn> SecondDimensionGroup
        {
            get
            {
                if (Condition.ModelSide2 != null && Condition.ColumnId1.HasValue)
                {
                    return Condition.ModelSide2 == ReconciliationModelSide.RIGHT
                                    ? ReconciliationModelColumns.RightColumns.Where(c => c.Type == Column1.Type).ToObservableCollection()
                                    : ReconciliationModelColumns.LeftColumns.Where(c => c.Type == Column1.Type).ToObservableCollection();
                }
                return new ObservableCollection<GrilleColumn>();
            }
        }

        private GrilleColumn Column1
        {
            get
            {
                if (FirstDimensionGroup.Count > 0 && Condition.ColumnId1.HasValue)
                {
                    return FirstDimensionGroup.Where(c => c.Id == Condition.ColumnId1).FirstOrDefault();
                }
                return null;
            }
            set
            {
                Condition.ColumnId1 = value != null ? value.Id : null;
            }
        }
        private GrilleColumn Column2
        {
            get
            {
                if (SecondDimensionGroup.Count > 0 && Condition.ColumnId2.HasValue)
                {
                    var found = SecondDimensionGroup.Where(c => c.Id == Condition.ColumnId2.Value)
                            .FirstOrDefault();
                    return found;
                }
                return null;
            }
            set
            {
                Condition.ColumnId2 = value != null ? value.Id : null;
            }
        }

        private string OpeningBracket
        {
            get => Condition.OpeningBracket;
            set
            {
                Condition.OpeningBracket = value;
                ConditionChanged.InvokeAsync(Condition);

            }
        }
        private string ClosingBracket
        {
            get => Condition.ClosingBracket;
            set
            {
                Condition.ClosingBracket = value;
                ConditionChanged.InvokeAsync(Condition);

            }
        }
        public bool IsTypeAttribute { get => Column1 != null && Column1.Type == DimensionType.ATTRIBUTE; }
        public bool IsTypePeriod { get => Column1 != null && Column1.Type == DimensionType.PERIOD; }

        public ReconciliationModelSide ModelSide1
        {
            get { return Condition.ModelSide1; }
            set {
                Condition.ModelSide1 = value;
                if (value == ReconciliationModelSide.LEFT)
                {
                    ModelSide2 = ReconciliationModelSide.RIGHT;
                }
                else if (value == ReconciliationModelSide.RIGHT)
                {
                    ModelSide2 = ReconciliationModelSide.LEFT;
                }
                Column1 = null;
                Column2 = null;
                Condition.Operator = null;
                ConditionChanged.InvokeAsync(Condition);
            }
        }
        public ReconciliationModelSide ModelSide2
        {
            get { return Condition.ModelSide2; }
            set { Condition.ModelSide2 = value; }
        }

        IEnumerable<string> FilterVerbs { get; set; }
        public String SelectedFilterVerb
        {
            get
            {
                FilterVerb? verb = FilterVerb.AND.Parse(Condition.Verb);
                if (verb.HasValue)
                {
                    return verb.Value.GetText(text => AppState[text]);
                }
                return null;
            }
            set
            {
                Condition.Verb = string.IsNullOrWhiteSpace(value) ? null : FilterVerb.AND.GetFilterVerb(value, text => AppState[text]).ToString();
                ConditionChanged.InvokeAsync(Condition);
            }
        }
        private bool showConfigModal = false;
        IEnumerable<string> periodGranularities;
        AttributeOperator? Operator { get; set; } = AttributeOperator.CONTAINS;
        public string SelectedAttributeOp
        {
            get
            {
                AttributeOperator? Operator_ = AttributeOperator.CONTAINS.Parse(Condition.Operator);
                if (Operator_.HasValue)
                {
                    return Operator_.GetText(text => AppState[text]);
                }
                return null;
            }
            set
            {
                Condition.Operator = Operator.GetAttributeOperator(value, text => AppState[text]).ToString();
            }
        }
        IEnumerable<string> AttributeOperators { get; set; }
        IEnumerable<string> MeasureOperators { get; set; }
        public string SelectedGranularity
        {
            get
            {
                try
                {
                    return Condition.DateGranularity.GetText(text => AppState[text]);
                }
                catch
                {
                    return null;
                }
            }
            set
            {
                if (!string.IsNullOrWhiteSpace(value))
                {
                    Condition.DateGranularity = Condition.DateGranularity.GetPeriodGranularity(value, text => AppState[text]);
                }
            }
        }

        private IEnumerable<ReconciliationModelSide> modelSides;

        private List<string> dateSigns = new List<string>()
        {
            "+",
            "-",
            "+/-"
        };
        #endregion

        #region Form items change or selection handlers

        protected override void OnInitialized()
        {
            FilterVerbs = FilterVerb.AND.GetAll(text => AppState[text]);
            modelSides = new List<ReconciliationModelSide>()
                                { ReconciliationModelSide.LEFT, ReconciliationModelSide.RIGHT }
                                .Select(ms => { ms.label = AppState[(ms.getCode() + "_")]; return ms;  });

            AttributeOperators = Operator.GetAll(text => AppState[text]);
            MeasureOperators = MeasureOperator.GetAll();
        }

        protected override void OnAfterRender(bool firstRender)
        {
            base.OnAfterRender(firstRender);
            if (!firstRender && showConfigModal && periodGranularities == null)
            {
                periodGranularities = PeriodGranularity.DAY.GetAll(text => AppState[text]);
            }

            if (!firstRender)
            {
                AppState.Update = true;
            }
        }
        
        protected async void Column1ValueChanged(GrilleColumn column)
        {
            Column1 = column;
            // je cherche dans l'autre groupe s'il y a une dimension ayant le même id que celle selectionnée
            // et je l'affecte à columnId2
            var foundDim = SecondDimensionGroup.FirstOrDefault(d => d.DimensionId == column.DimensionId && d.Type == column.Type);
            if (foundDim != null)
            {
                Column2 = foundDim;
                Condition.Operator = IsTypeAttribute ? AttributeOperator.EQUALS.ToString() : MeasureOperator.EQUALS;
                await ConditionChanged.InvokeAsync(Condition);
                return;
            }

            // Au cas ou on change la colonne de gauche alors qu'on a déjà affecté une valeur à la colonne de droite
            // et que les types sont différents
            if (Column2 == null || Column1.Type != Column2.Type)
            {
                Condition.ColumnId2 = null;
                await ConditionChanged.InvokeAsync(Condition);
            }
            else if (Column2 == null && Condition.ColumnId2 != null)
            {
                Condition.ColumnId2 = null;
            }

            // je reset l'opérateur si sa valeur n'est pas dans la liste correspondante au type de grille column
            if ( IsTypeAttribute )
            {
                if (!string.IsNullOrWhiteSpace(Condition.Operator) && !AttributeOperators.Contains(Condition.Operator))
                {
                    Condition.Operator = null;
                }
            }
            else if ( !string.IsNullOrWhiteSpace(Condition.Operator) && !MeasureOperators.Contains(Condition.Operator))
            {
                Condition.Operator = null;
            }

            StateHasChanged();
        }

        protected async void Column2ValueChanged(GrilleColumn column)
        {
            Column2 = column;
            await ConditionChanged.InvokeAsync(Condition);
        }

        protected async void OperatorValueChanged(string op)
        {
            if(Column1.Type == DimensionType.ATTRIBUTE)
            {
                SelectedAttributeOp = op;
            }
            else
            {
                Condition.Operator = op;
            }

            if (Condition.ColumnId1.HasValue && Condition.ColumnId2.HasValue)
            {
                await ConditionChanged.InvokeAsync(Condition);
            }
        }

        protected async void Delete(ReconciliationCondition condition)
        {
            if (IsNew)
            {
                Column1 = null;
            }
            else
            {
                await OnClickBtnRemove.InvokeAsync(Condition);
            }
        }

        private async void PeriodModalOkHandler()
        {
            //AppState.Update = true;
            await ConditionChanged.InvokeAsync(Condition);
        }
        #endregion

    }
}

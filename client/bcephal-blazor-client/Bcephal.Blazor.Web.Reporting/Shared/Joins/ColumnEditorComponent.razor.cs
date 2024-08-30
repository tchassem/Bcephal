using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Bcephal.Models.Joins;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Shared.Joins
{
    public partial class ColumnEditorComponent : ComponentBase
    {
        [Inject] private AppState AppState { get; set; }
        [Inject] public IToastService ToastService { get; set; }
        [Parameter] public bool Editable { get; set; } = true;
        [Parameter] public EditorData<Join> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<Join>> EditorDataChanged { get; set; }
        [Parameter] public int? JoinColumnPosition { get; set; }
        [Parameter] public ObservableCollection<HierarchicalData> Entities { get; set; }
       
        [Parameter] public string ItemCssClass { get; set; }

        private bool CanShowConcatenateModal { get; set; }

        private bool CanShowCalcualteModal { get; set; }

        private string ItemLength = "0.2fr";

        private string LabelLength = "0.75fr";

        private string TextLength = "1fr";

        private string CheckboxLength = "0.1fr";

        public string ItemSpacing { get; set; } = "4px";

        bool ShowFormatPopUp = false;
        
        private CardComponent CardComponentRef { get; set; }
        private JoinColumn JoinColumn_
        {
            get
            {
                if (EditorData != null && EditorData.Item != null
                    && EditorData.Item.ColumnListChangeHandler != null
                    && EditorData.Item.ColumnListChangeHandler.Items != null)
                {
                    return EditorData.Item.ColumnListChangeHandler.Items.Where(x => JoinColumnPosition.HasValue && x.Position == JoinColumnPosition.Value).FirstOrDefault();
                }
                else
                {
                    return null;
                }
            }
        }

        public DimensionType DimensionType
        { 
            get 
            {
                if (JoinColumn_ != null)
                {
                    if (JoinColumn_.Type.IsMeasure())
                    {
                        return DimensionType.MEASURE;
                    }
                    else if (JoinColumn_.Type.IsPeriod())
                    {
                        return DimensionType.PERIOD;
                    }
                }
                return DimensionType.ATTRIBUTE;
            }
            set
            {
                if (value.IsMeasure())
                {
                    JoinColumn_.Type = DimensionType.MEASURE;
                }
                else if (value.IsPeriod())
                {
                    JoinColumn_.Type = DimensionType.PERIOD;
                }
                else
                {
                    JoinColumn_.Type = DimensionType.ATTRIBUTE;
                }
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public string DimensionType_
        { 
            get 
            {
                if (JoinColumn_ != null && JoinColumn_.Properties != null && JoinColumn_.Properties.Field != null)
                {
                    return JoinColumn_.Properties.Field.DimensionType.GetText(text => AppState[text]);
                }
                if(JoinColumn_ == null || JoinColumn_.Properties == null || JoinColumn_.Properties.Field == null)
                {
                    if (JoinColumn_.Properties == null)
                    {
                        JoinColumn_.Properties = new();
                    }
                    if (JoinColumn_.Properties.Field == null)
                    {
                        JoinColumn_.Properties.Field = new();
                    }
                    JoinColumn_.Properties.Field.DimensionType = DimensionType.ATTRIBUTE;
                    JoinColumn_.Type = DimensionType.ATTRIBUTE;
                }
                return DimensionType.ATTRIBUTE.GetText(text => AppState[text]);
            }
            set
            {
                if(!JoinColumn_.Properties.Field.DimensionType.Equals(value))
                {
                    JoinColumn_.Properties.Field = new();
                    if (DimensionType.PERIOD.Equals(value))
                    {
                        JoinColumn_.Properties.Field.DateValue = new PeriodValue()
                        {
                            DateOperator = PeriodOperator.SPECIFIC,
                            DateValue = DateTime.Today
                        };
                    }
                }
                JoinColumn_.Properties.Field.DimensionType = DimensionType.MEASURE.GetDimensionType(value, text => AppState[text]);
                JoinColumn_.Type = JoinColumn_.Properties.Field.DimensionType;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public GrilleColumnFixedStyle GrilleColumnFixedStyle
        {
            get
            {
                if (GrilleColumnFixedStyle.IsLeft())
                {
                    return GrilleColumnFixedStyle.Left;
                }
                else if (GrilleColumnFixedStyle.IsRight())
                {
                    return GrilleColumnFixedStyle.Right;
                }
                return GrilleColumnFixedStyle.None;
            }
            set
            {
                if (value.IsLeft())
                {
                    JoinColumn_.ColumnFixedStyle = GrilleColumnFixedStyle.Left;
                }
                else if (value.IsRight())
                {
                    JoinColumn_.ColumnFixedStyle = GrilleColumnFixedStyle.Right;
                }
                else
                {
                    JoinColumn_.ColumnFixedStyle = GrilleColumnFixedStyle.None;
                }
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public string GrilleColumnFixedStyle_
        {
            get
            {
                if (JoinColumn_ != null && JoinColumn_.ColumnFixedStyle != null)
                {
                    return JoinColumn_.ColumnFixedStyle.GetText(text => AppState[text]);
                }
                return GrilleColumnFixedStyle.None.GetText(text => AppState[text]);
            }
            set
            {
                JoinColumn_.ColumnFixedStyle = GrilleColumnFixedStyle.None.GetGrilleColumnFixedStyle(value, text => AppState[text]);
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public string JoinColumnType_
        {
            get
            {
                if(JoinColumn_ != null && JoinColumn_.Properties != null && JoinColumn_.Properties.Field != null && JoinColumn_.Properties.Field.JoinColumnType != null)
                {
                    return JoinColumn_.Properties.Field.JoinColumnType.GetText(text => AppState[text]);
                }
                return null;
            }
            set
            {
                if(JoinColumn_ != null)
                {
                    if(JoinColumn_.Properties == null)
                    {
                        JoinColumn_.Properties = new();
                    }
                    if(JoinColumn_.Properties.Field == null)
                    {
                        JoinColumn_.Properties.Field = new();
                    }
                    JoinColumn_.Properties.Field.JoinColumnType = JoinColumnType.FREE.GetJoinColumnType(value, text => AppState[text]);
                    JoinColumn_.Properties.Field.DateValue = null;
                    JoinColumn_.Properties.Field.GridId = null;
                    JoinColumn_.Properties.Field.ColumnId = null;
                    JoinColumn_.Properties.Field.DecimalValue = null;
                    JoinColumn_.Properties.Field.StringValue = null;
                    JoinColumn_.Properties.Field.DimensionId = null;
                    JoinColumn_.Properties.Field.DimensionName = null;
                    EditorData.Item.UpdateColumn(JoinColumn_);
                    EditorDataChanged.InvokeAsync(EditorData);
                }                          
            }        
        }

        public Nameable Sequence
        {
            get
            {
                if (JoinColumn_ != null && JoinColumn_.Properties != null && JoinColumn_.Properties.Field != null && JoinColumn_.Properties.Field.ColumnId.HasValue)
                {
                    Nameable Sequence_ = GetEditorData().Sequences.Where(x => x.Id == JoinColumn_.Properties.Field.ColumnId).FirstOrDefault();
                    return Sequence_;
                }
                return null;
            }
            set
            {
                if (value != null && value.Id.HasValue)
                {
                    if (JoinColumn_.Properties == null)
                    {
                        JoinColumn_.Properties = new();
                    }
                    JoinColumn_.Properties.Field.ColumnId = value.Id;
                    EditorData.Item.UpdateColumn(JoinColumn_);
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        public Nameable Spot
        {
            get
            {
                if (JoinColumn_ != null && JoinColumn_.Properties != null && JoinColumn_.Properties.Field != null && JoinColumn_.Properties.Field.ColumnId.HasValue)
                {
                    Nameable Spot_ = GetEditorData().Spots.Where(x => x.Id == JoinColumn_.Properties.Field.ColumnId).FirstOrDefault();
                    return Spot_;
                }
                return null;
            }
            set
            {
                if (value != null && value.Id.HasValue)
                {
                    if (JoinColumn_.Properties == null)
                    {
                        JoinColumn_.Properties = new();
                    }
                    JoinColumn_.Properties.Field.ColumnId = value.Id;
                    EditorData.Item.UpdateColumn(JoinColumn_);
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        public JoinGrid Grille
        {
            get
            {
                if (JoinColumn_!= null && JoinColumn_.Properties != null &&  JoinColumn_.Properties.Field != null && JoinColumn_.Properties.Field.GridId.HasValue)
                {
                    JoinGrid joinGrid = GetEditorData().Item.GridListChangeHandler.Items.Where(x => x.GridId == JoinColumn_.Properties.Field.GridId).FirstOrDefault();
                    return joinGrid;
                }
                return null;
            }
            set
            {
                if(value != null && value.GridId.HasValue)
                {
                    if(JoinColumn_.Properties == null )
                    {
                        JoinColumn_.Properties = new();
                    }
                    JoinColumn_.Properties.Field.GridId = value.GridId;
                    EditorData.Item.UpdateColumn(JoinColumn_);
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        public SmallGrilleColumn Column
        {
            get
            {
                if (Grille != null && Grille.GridId.HasValue)
                {
                    return GetColumns().Where(x=>x.Id == JoinColumn_.Properties.Field.ColumnId).FirstOrDefault();
                }
                return null;
            }
            set
            {
                JoinColumn_.Properties.Field.ColumnId = value.Id;
                JoinColumn_.Properties.Field.DimensionId = value.DimensionId;
                JoinColumn_.Properties.Field.DimensionName = value.DimensionName;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }
  
        private ObservableCollection<SmallGrilleColumn> GetColumns()
        {
            if (GetEditorData() != null && GetEditorData().Grids != null && Grille != null)
            {
                SmartGrille smartGrille = GetEditorData().Grids.Where(x => x.Id == Grille.GridId).FirstOrDefault();
                if (smartGrille != null && smartGrille.Columns != null)
                {
                    return new ObservableCollection<SmallGrilleColumn>(smartGrille.Columns.Where(x => x.Type == JoinColumn_.Properties.Field.DimensionType));
                }

            }
            return new();
        }

        public JoinColumn SelectedJoinColumn
        {
            get
            {
                Func<JoinColumn, bool> condition = (item) =>
                {
                    bool firstCondition = false;
                    if (JoinColumn_.Properties != null && JoinColumn_.Properties.Field != null && JoinColumn_.Properties.Field.ColumnId.HasValue)
                    {
                       firstCondition = ((item.ColumnId == JoinColumn_.Properties.Field.ColumnId) && item.IsPersistent == true && item.Type == JoinColumn_.Properties.Field.DimensionType);
                    }
                    bool firstCondition2 = false;
                    if (JoinColumn_.Properties != null && JoinColumn_.Properties.Field != null && JoinColumn_.Properties.Field.JoinColumnType != null && JoinColumn_.Properties.Field.JoinColumnType.IsCopy() && JoinColumn_.Category.IsCustom())
                    {
                        firstCondition2 = ((item.Id == JoinColumn_.Properties.Field.ColumnId) && item.IsPersistent == true && item.Type == JoinColumn_.Properties.Field.DimensionType);
                    }
                    bool secondCondition = (item.Category.IsCustom() && item.IsPersistent == true && item.Id == JoinColumn_.Properties.Field.ColumnId);
                    return firstCondition || secondCondition || firstCondition2;
                };      
                return EditorData.Item.ColumnListChangeHandler.Items.Where(condition).FirstOrDefault();
            }
            set
            {
                if (value != null)
                {
                    if (value.ColumnId.HasValue)
                    {
                        if (JoinColumn_.Properties.Field.JoinColumnType.IsCopy() && JoinColumn_.Category.IsCustom()){
                            JoinColumn_.Properties.Field.ColumnId = value.Id;
                            JoinColumn_.Properties.Field.DimensionId = null;
                            JoinColumn_.Properties.Field.DimensionName = null;
                        }
                        else
                        {      
                            JoinColumn_.Properties.Field.ColumnId = value.ColumnId;
                            JoinColumn_.Properties.Field.DimensionId = value.DimensionId;
                            JoinColumn_.Properties.Field.DimensionName = value.DimensionName;
                        }
                    }
                    else
                    {
                        JoinColumn_.Properties.Field.ColumnId = value.Id;
                        JoinColumn_.Properties.Field.DimensionId = null;
                        JoinColumn_.Properties.Field.DimensionName = null;
                    }
                    EditorData.Item.UpdateColumn(JoinColumn_);
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        public string StringValue
        {
            get
            {
                if (JoinColumn_ != null && JoinColumn_.Properties != null && JoinColumn_.Properties.Field != null && JoinColumn_.Properties.Field.StringValue != null)
                {
                    return JoinColumn_.Properties.Field.StringValue;
                }
                return null;
            }
            set
            {
                if (JoinColumn_ == null || JoinColumn_.Properties == null || JoinColumn_.Properties.Field == null || JoinColumn_.Properties.Field.StringValue == null)
                {
                    if (JoinColumn_.Properties == null)
                    {
                        JoinColumn_.Properties = new();
                    }
                }

                JoinColumn_.Properties.Field.StringValue = value;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public decimal? DecimalValue
        {
            get
            {
                if (JoinColumn_ != null && JoinColumn_.Properties != null && JoinColumn_.Properties.Field != null && JoinColumn_.Properties.Field.DecimalValue != null)
                {
                    return JoinColumn_.Properties.Field.DecimalValue;
                }
                return null;     
            }
            set
            {
                if (JoinColumn_ == null ||JoinColumn_.Properties == null || JoinColumn_.Properties.Field == null || JoinColumn_.Properties.Field.DecimalValue == null)
                {
                    if (JoinColumn_.Properties == null)
                    {
                        JoinColumn_.Properties = new();
                    }
                } 
                
                JoinColumn_.Properties.Field.DecimalValue = value;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private PeriodValue PeriodValue_
        {
            get
            {
                if (JoinColumn_.Properties != null && JoinColumn_.Properties.Field != null && JoinColumn_.Properties.Field.DateValue != null)
                {
                    return JoinColumn_.Properties.Field.DateValue;
                }
                return new PeriodValue()
                {
                    DateOperator = PeriodOperator.SPECIFIC,
                    DateValue = DateTime.Today
                };
            }
            set
            {
                if (JoinColumn_.Properties == null || JoinColumn_.Properties.Field == null || JoinColumn_.Properties.Field.DateValue == null)
                {
                    if (JoinColumn_.Properties == null)
                    {
                        JoinColumn_.Properties = new();
                    }

                    if (JoinColumn_.Properties.Field == null){
                        JoinColumn_.Properties.Field = new();
                    }
                    JoinColumn_.Properties.Field.DateValue = new PeriodValue()
                    {
                        DateOperator = PeriodOperator.SPECIFIC,
                        DateValue = DateTime.Today
                    };
                }

                JoinColumn_.Properties.Field.DateValue = value;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public int StartPosition
        {
            get
            {
                if (JoinColumn_ != null && JoinColumn_.Properties != null && JoinColumn_.Properties.Field != null && JoinColumn_.Properties.Field.StartPosition != null)
                {
                    return JoinColumn_.Properties.Field.StartPosition;
                }
                return 0;
            }
            set
            {
                if (JoinColumn_ == null || JoinColumn_.Properties == null || JoinColumn_.Properties.Field == null || JoinColumn_.Properties.Field.StartPosition == null)
                {
                    if (JoinColumn_.Properties == null)
                    {
                        JoinColumn_.Properties = new();
                    }
                }
                JoinColumn_.Properties.Field.StartPosition = value;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public int EndPosition
        {
            get
            {
                if (JoinColumn_ != null && JoinColumn_.Properties != null && JoinColumn_.Properties.Field != null && JoinColumn_.Properties.Field.EndPosition != null)
                {
                    return JoinColumn_.Properties.Field.EndPosition;
                }
                return 0;
            }
            set
            {
                if (JoinColumn_ == null || JoinColumn_.Properties == null || JoinColumn_.Properties.Field == null || JoinColumn_.Properties.Field.EndPosition == null)
                {
                    if (JoinColumn_.Properties == null)
                    {
                        JoinColumn_.Properties = new();
                    }
                }

                JoinColumn_.Properties.Field.EndPosition = value;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public string Backgrounds
        {
            get { return JoinColumn_.Backgrounds; }
            set
            {
                JoinColumn_.Backgrounds = value;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public string Name
        {
            get { return JoinColumn_.Name; }
            set
            {
                int count = EditorData.Item.ColumnListChangeHandler.Items.Where(x => x.Name == value).Count();
                if(count > 0)
                {
                    ToastService.ShowError(AppState["duplicate.join.column", value]);
                }
                else
                {
                    JoinColumn_.Name = value;
                    EditorData.Item.UpdateColumn(JoinColumn_);
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        public bool Show
        {
            get { return JoinColumn_.Show; }
            set
            {
                JoinColumn_.Show = value;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public int NbrOfDecimal
        {
            get { return JoinColumn_.Format.NbrOfDecimal; }
            set
            {
                JoinColumn_.Format.NbrOfDecimal = value;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public int ItemsCount
        {
            get
            {
                if (EditorData != null && EditorData.Item != null && EditorData.Item.ColumnListChangeHandler != null)
                {
                    return EditorData.Item.ColumnListChangeHandler.Items.Count;
                }
                return 0;
            }
        }
        private string ColumnWidth() { return ItemsCount == 0 ? "auto" : ItemsCount < 10 ? "auto" : "200px"; }
        public int? Width
        {
            get {
                string valString = ColumnWidth();
                if (!string.IsNullOrWhiteSpace(valString) && valString.Contains("px") && !JoinColumn_.Width.HasValue)
                {
                    int.TryParse(valString.Replace("px", ""), out int v);
                    JoinColumn_.Width = v;
                }
                return JoinColumn_.Width;
            }
            set
            {
                JoinColumn_.Width = value;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }   
        
        public bool UsedForPublication
        {
            get { return JoinColumn_.UsedForPublication; }
            set
            {
                JoinColumn_.UsedForPublication = value;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        } 

        public string DimensionName
        {
            get { return JoinColumn_.DimensionName; }
            set
            {
                JoinColumn_.DimensionName = value;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }  
        
        public string DimensionFunction
        {
            get { return JoinColumn_.DimensionFunction; }
            set
            {
                JoinColumn_.DimensionFunction = value;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }
        
        public string DefaultFormat
        {
            get { return JoinColumn_.Format.DefaultFormat; }
            set
            {
                JoinColumn_.Format.DefaultFormat = value;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public string Foregrounds
        {
            get { return JoinColumn_.Foregrounds; }
            set
            {
                JoinColumn_.Foregrounds = value;
                EditorData.Item.UpdateColumn(JoinColumn_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private JoinEditorData GetEditorData()
        {
            JoinEditorData JoinEditorData = (JoinEditorData)EditorData;
            return JoinEditorData;
        }

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            AppState.Update = true;
        }

        IEnumerable<string> DimensionTypes;
        IEnumerable<string> ColumnAttributeTypes;
        IEnumerable<string> ColumnMeasureTypes;
        IEnumerable<string> ColumnPeriodTypes;
        ObservableCollection<string> GrilleColumnFixedStyles;
        IEnumerable<string> periodGranularities;

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            DimensionTypes = new List<string>() {
                                DimensionType.ATTRIBUTE.GetText(text => AppState[text]),
                                DimensionType.MEASURE.GetText(text => AppState[text]),
                                DimensionType.PERIOD.GetText(text => AppState[text]) };
            ColumnAttributeTypes = JoinColumnType.FREE.GetAllTypeAttributes(text => AppState[text]);    
            ColumnMeasureTypes = JoinColumnType.FREE.GetAllTypeMeasures(text => AppState[text]);
            ColumnPeriodTypes = JoinColumnType.FREE.GetAllTypePeriods(text => AppState[text]);
            GrilleColumnFixedStyles = GrilleColumnFixedStyle.None.GetAll(text => AppState[text]);
            periodGranularities = PeriodGranularity.DAY.GetAll(text => AppState[text]);
        }

        List<string> Functions => new() { "Sum", "Average", "Count", "Max", "Min" };

        public void JoinColumnDimensionChanged(HierarchicalData hierarchical)
        {
            JoinColumn_.DimensionName = hierarchical.Name;
            JoinColumn_.DimensionId = hierarchical.Id;
            EditorDataChanged.InvokeAsync(EditorData);
        }

        private void ShowModalConcatenate()
        {
            CanShowConcatenateModal = true;
        }

        private void ShowModalCalculate()
        {
            CanShowCalcualteModal = true;
        }
    }
}
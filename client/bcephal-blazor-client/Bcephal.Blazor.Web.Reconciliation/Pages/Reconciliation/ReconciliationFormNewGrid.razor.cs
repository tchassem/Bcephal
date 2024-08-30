using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Reconciliation.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Reconciliation;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation
{
    public partial class ReconciliationFormNewGrid : ComponentBase,IAsyncDisposable
    {
        [Inject]
        public ReconciliationModelService ReconciliationModelService { get; set; }
        [Inject]
        private AppState AppState { get; set; }
        [Parameter] public EditorData<ReconciliationModel> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<ReconciliationModel>> EditorDataChanged { get; set; }
        [Parameter] public Action<RenderFragment> Filter { get; set; }
        [Parameter] public EditorData<Bcephal.Models.Grids.Grille> LeftG { get; set; }
        [Parameter] public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> LeftGChanged { get; set; }
        [Parameter] public EditorData<Bcephal.Models.Grids.Grille> RightG { get; set; }
        [Parameter] public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> RightGChanged { get; set; }
        [Parameter] public EditorData<Bcephal.Models.Grids.Grille> BottomG { get; set; }
        [Parameter] public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> BottomGChanged { get; set; }
        [Parameter] public bool Editable { get; set; } = true;
        [Parameter] public Action ActionState { get; set; }
        [Parameter] public Func<EditorData<ReconciliationModel>, Task> CheckGridColumnsConfig { get; set; }

        private LeftNewGrid  LeftGridRef { get; set; }
        private RightNewGrid RightGridRef { get; set; }
        private BottomNewGrid BottomGridRef { get; set; }

        private string LeftGridName => EditorData.Item != null && EditorData.Item.LeftGrid != null && !string.IsNullOrWhiteSpace(EditorData.Item.LeftGrid.Name) ? EditorData.Item.LeftGrid.Name : AppState["Reconciliation.Left"];
        private string RigthGridName => EditorData.Item != null && EditorData.Item.RigthGrid != null && !string.IsNullOrWhiteSpace(EditorData.Item.RigthGrid.Name) ? EditorData.Item.RigthGrid.Name : AppState["Reconciliation.Right"];
        private bool ConfirmSelection_ { get; set; } = false;

        private bool ConfirmSelection { get => ConfirmSelection_; 
            set {

                ConfirmSelection_ = value;
                RenderFormContentConfirmSelection.StateHasChanged_();
            }
        }

        private RenderFormContent RenderFormContentConfirmSelection { get; set; }
        private bool ChoiseSelection { get; set; } = false;
        private List<string> BottomNewColumns { get; set; }

        private IEnumerable<string> DataFilter { get; set; } = new List<string>() { };

    public string Note_ { get; set; }
        public string Note
        {
            get
            {
                return Note_;
            }
            set
            {
                Note_ = value;
            }
        }

        public override Task SetParametersAsync(ParameterView parameters)
        {
            BottomNewColumns = new List<string>() { AppState["Side"] };
            return base.SetParametersAsync(parameters);
        }
        protected override  Task OnParametersSetAsync()
        {
            Task task = base.OnParametersSetAsync();            
            return task.ContinueWith(t=>  InitDataFilter());
        }


        private void InitDataFilter()
        {
            bool canRefresh = false;
            if (!DataFilter.Contains(AppState["all"]))
            {
                ((List<string>)DataFilter).Add(AppState["all"]);
                canRefresh = true;
            }
            if (!DataFilter.Contains(AppState["not.reconciled.row.only"]))
            {
                ((List<string>)DataFilter).Add(AppState["not.reconciled.row.only"]);
                canRefresh = true;
            }
            if (!DataFilter.Contains(AppState["reconciled.row.only"]))
            {
                ((List<string>)DataFilter).Add(AppState["reconciled.row.only"]);
                canRefresh = true;
            }
            if (!DataFilter.Contains(AppState["Freeze.type"]) && EditorData.Item.FreezeAttributeId.HasValue)
            {
                ((List<string>)DataFilter).Add(AppState["Freeze.type"]);
                canRefresh = true;
            }
            if (canRefresh)
            {
                StateHasChanged();
            }
        }

        private string LeftHeaderMessage;
        private string RightHeaderMessage;
        private bool CanFreeze_ => Can_  && EditorData.Item.FreezeAttributeId.HasValue && EditorData.Item.AllowFreeze;
        private bool CanUnFreeze_ => Can_ && EditorData.Item.FreezeAttributeId.HasValue && EditorData.Item.AllowFreeze;

        private bool CanNeutralization_ => Can_ && EditorData.Item.NeutralizationAttributeId.HasValue && EditorData.Item.AllowNeutralization;
        private bool CanUnNeutralization_ => Can_ && EditorData.Item.NeutralizationAttributeId.HasValue && EditorData.Item.AllowNeutralization;

        private bool Can_ => BottomGridRef != null && BottomGridRef.InputGridComponentRef.SelectedItems_.Count > 0;

        private List<GridItem> GridItemLeft => BottomGridRef.InputGridComponentRef.SelectedItems_.Where(item => item.IsLeftSide()).ToList();

        private List<GridItem> GridItemRight => BottomGridRef.InputGridComponentRef.SelectedItems_.Where(item => item.IsRightSide()).ToList();

        public bool CanFreeze { get; set; }
        public bool CanUnFreeze { get; set; }

        public bool CanNeutralization { get; set; }
        public bool CanUnNeutralization { get; set; }

        public bool CanRun { get; set; }
        public bool CanReset { get; set; }
        public bool CanDelete { get; set; }


        public EditorData<ReconciliationModel> EditorDataBinding
        {
            get { return EditorData; }
            set
            {
                EditorData = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }


        private ReconciliationModelEditorData GetModelEditorData()
        {
            return (ReconciliationModelEditorData)this.EditorData;
        }

        public EditorData<Bcephal.Models.Grids.Grille> EditorDataBindingLeft
        {
            get { return LeftG; }
            set
            {
                LeftG = value;
                LeftGChanged.InvokeAsync(LeftG);
            }
        }

        public EditorData<Bcephal.Models.Grids.Grille> EditorDataBindingRight
        {
            get { return RightG; }
            set
            {
                RightG = value;
                RightGChanged.InvokeAsync(RightG);
            }
        }

        public EditorData<Bcephal.Models.Grids.Grille> EditorDataBindingBottom
        {
            get { return BottomG; }
            set
            {
                BottomG = value;
                BottomGChanged.InvokeAsync(BottomG);
            }
        }

        private string GetDimension(Models.Reconciliation.WriteOffField WriteOffField_)
        {
            string dimension = "";
            DimensionType Dtype = WriteOffField_.DimensionType;
            if (Dtype.Equals(DimensionType.PERIOD))
            {
                if (EditorData.Periods.Where(x => x.Id == WriteOffField_.DimensionId).Any())
                {
                    dimension = EditorData.Periods.Where(x => x.Id == WriteOffField_.DimensionId).First().Name;
                }
            }
            else
            {
                ObservableCollection<Models.Dimensions.Attribute> attributes = new ObservableCollection<Models.Dimensions.Attribute>();
                foreach (Models.Dimensions.Model model in EditorData.Models)
                {
                    if (model.Entities.Any())
                    {
                        foreach (Models.Dimensions.Entity entity in model.Entities)
                        {
                            if (entity.Attributes.Any())
                            {
                                foreach (Models.Dimensions.Attribute attribute in entity.Attributes)
                                {
                                    attributes.Add(attribute);
                                }
                                if (attributes.Any())
                                {
                                    if (attributes.Where(x => x.Id == WriteOffField_.DimensionId).Any())
                                    {
                                        dimension = attributes.Where(x => x.Id == WriteOffField_.DimensionId).First().Name;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return dimension;
        }


        private void buildFilter(BrowserDataFilter filter, RecoNewGrid recoGrid)
        {
            filter.RecoAttributeId = EditorDataBinding.Item.RecoAttributeId;
            filter.NoteAttributeId = EditorDataBinding.Item.NoteAttributeId;
            filter.FreezeAttributeId = EditorDataBinding.Item.FreezeAttributeId;
            filter.RowType = recoGrid.EditorData.Item.RowType;
            if (recoGrid.EditorData.Item.Credit.HasValue)
            {
                filter.credit = recoGrid.EditorData.Item.Credit.Value;
            }
            if (recoGrid.EditorData.Item.Debit.HasValue)
            {
                filter.debit = recoGrid.EditorData.Item.Debit.Value;
            }
            long? AmountMeasureId = null;
            if (recoGrid is LeftNewGrid)
            {
                AmountMeasureId = EditorDataBinding.Item.LeftMeasureId;
            }else
                if (recoGrid is RightNewGrid)
            {
                AmountMeasureId = EditorDataBinding.Item.RigthMeasureId;
            }
            filter.RecoData = new ReconciliationDataFilter()
            {
                AllowFreeze = EditorDataBinding.Item.AllowFreeze,
                AllowNeutralization = EditorDataBinding.Item.AllowNeutralization,
                AllowPartialReco = EditorDataBinding.Item.AllowPartialReco,
                AmountMeasureId = AmountMeasureId,
                FreezeAttributeId = EditorData.Item.FreezeAttributeId,
                FreezeSequenceId = EditorData.Item.FreezeSequenceId,
                NeutralizationAttributeId = EditorData.Item.NeutralizationAttributeId,
                NeutralizationSequenceId = EditorData.Item.NeutralizationSequenceId,
                PartialRecoAttributeId = EditorData.Item.PartialRecoAttributeId,
                PartialRecoSequenceId = EditorData.Item.PartialRecoSequenceId,
                ReconciliatedMeasureId = EditorData.Item.ReconciliatedMeasureId,
                RecoSequenceId = EditorData.Item.RecoSequenceId,
                RemainningMeasureId = EditorData.Item.RemainningMeasureId,
                RecoAttributeId = EditorData.Item.RecoAttributeId,
                NeutralizationAllowCreateNewValue = EditorData.Item.NeutralizationAllowCreateNewValue,
                NeutralizationInsertNote = EditorData.Item.NeutralizationInsertNote,
                NeutralizationMandatoryNote = EditorData.Item.NeutralizationMandatoryNote,
                NeutralizationMessage = EditorData.Item.NeutralizationMessage,
                NeutralizationRequestSelectValue = EditorData.Item.NeutralizationRequestSelectValue,
                NoteAttributeId = EditorData.Item.NoteAttributeId,
                credit = recoGrid.EditorData.Item.Credit.HasValue ? recoGrid.EditorData.Item.Credit.Value : false,
                debit = recoGrid.EditorData.Item.Debit.HasValue ? recoGrid.EditorData.Item.Debit.Value : false,

            };


        }
        private void ExportData(RecoNewGrid recoGrid, GrilleExportDataType type)
        {
            recoGrid.InputGridComponentRef.ExportData(recoGrid.InputGridComponentRef.getFilter(), type);
        }

        private bool? LeftCredit
        {
            get => EditorDataBindingLeft.Item.Credit;
            set
            {
                EditorDataBindingLeft.Item.Credit = value;
                EditorDataBindingLeft = EditorDataBindingLeft;
                buildFilterCreditPart(LeftGridRef);
            }
        }

        private bool? LeftDebit
        {
            get => EditorDataBindingLeft.Item.Debit;
            set
            {
                EditorDataBindingLeft.Item.Debit = value;
                EditorDataBindingLeft = EditorDataBindingLeft;
                buildFilterDebitPart(LeftGridRef);
            }
        }

        private bool? RightCredit
        {
            get => EditorDataBindingRight.Item.Credit;
            set
            {
                EditorDataBindingRight.Item.Credit = value;
                EditorDataBindingRight = EditorDataBindingRight;
                buildFilterCreditPart(RightGridRef);
            }
        }

        private bool? RightDebit
        {
            get => EditorDataBindingRight.Item.Debit;
            set
            {
                EditorDataBindingRight.Item.Debit = value;
                EditorDataBindingRight = EditorDataBindingRight;
                buildFilterDebitPart(RightGridRef);
            }
        }
        private bool?  CreditPart(bool IsLeft)
        {
            if (IsLeft)
            {
               return LeftCredit;
            }
            else
            {
                return RightCredit;
            }
        }

        private bool? DebitPart(bool IsLeft)
        {
            if (IsLeft)
            {
                return LeftDebit;
            }
            else
            {
                return RightDebit;
            }
        }

        private void BindingCreditPart(bool IsLeft, bool? value)
        {
            if (IsLeft)
            {
                LeftCredit = value;
            }
            else
            {
                RightCredit = value;
            }
        }

        private void BindingDebitPart(bool IsLeft, bool? value)
        {
            if (IsLeft)
            {
                LeftDebit = value;
            }
            else
            {
                RightDebit = value;
            }
        }

        private void buildFilterCreditPart(RecoNewGrid recoGrid)
        {
            if (recoGrid == null || recoGrid.InputGridComponentRef == null)
            {
                return;
            }
            BrowserDataFilter filer = recoGrid.InputGridComponentRef.getFilter();
            buildFilter(filer, recoGrid);
            if (recoGrid.EditorData.Item.Credit.HasValue)
            {
                filer.credit = recoGrid.EditorData.Item.Credit.Value;
            }
            else
            {
                filer.credit = false;
            }
            GrilleRowTypeChangedGrid(recoGrid);
        }
        private void buildFilterDebitPart(RecoNewGrid recoGrid)
        {
            if (recoGrid == null || recoGrid.InputGridComponentRef == null)
            {
                return;
            }
            BrowserDataFilter filer = recoGrid.InputGridComponentRef.getFilter();
            buildFilter(filer, recoGrid);
            if (recoGrid.EditorData.Item.Debit.HasValue)
            {
                filer.debit = recoGrid.EditorData.Item.Debit.Value;
            }
            else
            {
                filer.debit = false;
            }
            GrilleRowTypeChangedGrid(recoGrid);
        }

        private Task GrilleRowTypeChangedGrid(RecoNewGrid recoGrid)
        {
            if (recoGrid == null || recoGrid.InputGridComponentRef == null)
            {
                return Task.CompletedTask;
            }
            buildFilter(recoGrid.InputGridComponentRef.getFilter(), recoGrid);
            Task tas = recoGrid.InputGridComponentRef.RefreshGrid();
            tas = tas.ContinueWith(t => BuildBalance(recoGrid));
            //tas = tas.ContinueWith(t => InvokeAsync(ActionState));
            return tas;
        }

        private void buildFilterShowConterPart(RecoNewGrid recoGrid, ObservableCollection<long?> ids, bool isConterpart)
        {
            if (recoGrid == null || recoGrid.InputGridComponentRef == null)
            {
                return;
            }
            BrowserDataFilter filer = recoGrid.InputGridComponentRef.getFilter();
            buildFilter(filer, recoGrid);
            filer.Conterpart = isConterpart;
            if (isConterpart)
            {
                filer.RowType = GrilleRowType.RECONCILIATED.code;
                filer.Ids = ids;
            }
            else
            {
                buildFilter(filer, recoGrid);
                filer.Ids = new ObservableCollection<long?>();
            }
        }

        private void SetLabelMessageHeader(bool isLeftGrid_, string message)
        {
            if (isLeftGrid_)
            {
                LeftHeaderMessage = message;
            }
            else
            {
                RightHeaderMessage = message;
            }
        }
        private string LabelMessageHeader(bool isLeftGrid_)
        {
            if (isLeftGrid_)
            {
                return LeftHeaderMessage;
            }
            else
            {
                return RightHeaderMessage;
            }
        }
        private Task ShowConterPart(RecoNewGrid recoGrid, RecoNewGrid recoGrid2)
        {
            if (recoGrid == null)
            {
                return Task.CompletedTask;
            }
            bool isLeftGrid_ = recoGrid is LeftNewGrid;
            string HeaderMessage_ = LabelMessageHeader(isLeftGrid_);
            bool isConterpart = !string.IsNullOrWhiteSpace(HeaderMessage_) && HeaderMessage_.Equals(AppState["show.counter.part"]);
            if (isConterpart)
            {
                SetLabelMessageHeader(isLeftGrid_, AppState["Hide.counter.part"]);
            }
            else
            {
                SetLabelMessageHeader(isLeftGrid_, AppState["show.counter.part"]);
            }
            buildFilterShowConterPart(recoGrid2, new(), isConterpart);
            Task tas = recoGrid2.InputGridComponentRef.RefreshGrid();
            tas = tas.ContinueWith(t => BuildBalance(recoGrid2));
            //tas = tas.ContinueWith(t => InvokeAsync(StateHasChanged));
            return tas;
        }

        private async void setGrilleRowType(string value, EditorData<Bcephal.Models.Grids.Grille> grid, EventCallback<EditorData<Bcephal.Models.Grids.Grille>> callBack, Func<RecoNewGrid, Task> action, RecoNewGrid RecoGrid)
        {
            if (!string.IsNullOrWhiteSpace(value))
            {
                if (AppState["all"].Equals(value))
                {
                    grid.Item.GrilleRowType = GrilleRowType.ALL;
                }
                else
                     if (AppState["not.reconciled.row.only"].Equals(value))
                {
                    grid.Item.GrilleRowType = GrilleRowType.NOT_RECONCILIATED;
                }
                else
                     if (AppState["Freeze.type"].Equals(value))
                {
                    grid.Item.GrilleRowType = GrilleRowType.ON_HOLD;
                }
                else
                {
                    grid.Item.GrilleRowType = GrilleRowType.RECONCILIATED;
                }
                Task task =  callBack.InvokeAsync(grid);
                if (action != null)
                {
                    task = task.ContinueWith(t => action?.Invoke(RecoGrid));
                }
                await task;
            }
        }

        private string getGrilleRowType(EditorData<Bcephal.Models.Grids.Grille> grid)
        {
            if (grid.Item.GrilleRowType == GrilleRowType.ALL)
            {
                return AppState["all"];
            }
            else
            if (grid.Item.GrilleRowType == GrilleRowType.RECONCILIATED)
            {
                return AppState["reconciled.row.only"];
            }
            else
            if (grid.Item.GrilleRowType == GrilleRowType.ON_HOLD)
            {
                return AppState["Freeze.type"];
            }
            else
            {
                return AppState["not.reconciled.row.only"];
            }
        }

        private bool ShouldRender_ { get; set; } = true;
        protected override bool ShouldRender()
        {
            return ShouldRender_;
        }

        protected override  Task OnAfterRenderAsync(bool firstRender)
        {
            ShouldRender_ = false;
            if (firstRender)
            {
                AppState.RefreshReconciliationHander += RefreshPartialGrid__;
                AppState.CanRefreshReconciliation = true;

                Filter?.Invoke(null);
                
                if (string.IsNullOrWhiteSpace(LeftHeaderMessage))
                {
                    LeftHeaderMessage = AppState["show.counter.part"];
                }
                if (string.IsNullOrWhiteSpace(RightHeaderMessage))
                {
                    RightHeaderMessage = AppState["show.counter.part"];
                }
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        private  Task AfterRefreshLeft()
        {
            return LeftGridRef.InputGridComponentRef.RefreshSelection(BottomGridRef.InputGridComponentRef.GetSelectedItem(GridItem.LEFT_SIDE));
        }

        private async Task LeftSelectionCanged(List<long> selectedItems)
        {
            //Task task =  BottomGridRef.InputGridComponentRef.AddNewRow(selectedItems, GridItem.LEFT_SIDE);
            //task = task.ContinueWith(t1 => BuildBalance(LeftGridRef)).Unwrap();
            //task = task.ContinueWith(t1 => LeftGridRef.RefreshFooter()).Unwrap();
            ////task = task.ContinueWith(t1 => InvokeAsync(StateHasChanged)).Unwrap();
            //return task;
            await  BottomGridRef.InputGridComponentRef.AddNewRow(selectedItems, GridItem.LEFT_SIDE);
            await BuildBalance(LeftGridRef);
            await LeftGridRef.RefreshFooter();
        }


        private bool CanRunOrFreeLeft => GrilleRowType.NOT_RECONCILIATED.code.Equals(LeftGridRef.EditorData.Item.RowType) ||
            GrilleRowType.ALL.code.Equals(LeftGridRef.EditorData.Item.RowType);
        private bool CanRunOrFreeRight => GrilleRowType.NOT_RECONCILIATED.code.Equals(RightGridRef.EditorData.Item.RowType) ||
            GrilleRowType.ALL.code.Equals(RightGridRef.EditorData.Item.RowType);

        private bool CanUnFreeLeft => GrilleRowType.ON_HOLD.code.Equals(LeftGridRef.EditorData.Item.RowType);
        private bool CanUnFreeRight => GrilleRowType.ON_HOLD.code.Equals(RightGridRef.EditorData.Item.RowType);


        private bool CanRunOrNeutralizationLeft => GrilleRowType.NOT_RECONCILIATED.code.Equals(LeftGridRef.EditorData.Item.RowType) ||
            GrilleRowType.ALL.code.Equals(LeftGridRef.EditorData.Item.RowType);
        private bool CanRunOrNeutralizationRight => GrilleRowType.NOT_RECONCILIATED.code.Equals(RightGridRef.EditorData.Item.RowType) ||
            GrilleRowType.ALL.code.Equals(RightGridRef.EditorData.Item.RowType);

        private bool CanUnNeutralizationLeft => GrilleRowType.ON_HOLD.code.Equals(LeftGridRef.EditorData.Item.RowType);
        private bool CanUnNeutralizationRight => GrilleRowType.ON_HOLD.code.Equals(RightGridRef.EditorData.Item.RowType);

        private Task checkFreeze()
        {
            
            Task task = Task.Run(() =>
            {
                bool leftCount = GridItemLeft.Count() > 0;
                bool rightCount = GridItemRight.Count() > 0;
                CanFreeze = CanFreeze_ && ((CanRunOrFreeLeft && leftCount) || (CanRunOrFreeLeft && rightCount));
                CanUnFreeze = CanUnFreeze_ && ((CanUnFreeLeft && leftCount) || (CanUnFreeRight && rightCount));
                CanRun = Can_ && ((CanRunOrFreeLeft && leftCount) || (CanRunOrFreeLeft && rightCount));
            });
            bool Result = false;
            if (EditorData.Item.RecoAttributeId.HasValue)
            {
                task = task.ContinueWith(t =>  ReconciliationModelService.ContainsReconciliation(EditorData.Item.RecoAttributeId.Value, BottomGridRef.InputGridComponentRef.GetSelectionDataItemsIds())).Unwrap();
            }
            task = task.ContinueWith(t =>
            {
                if(t is Task<bool>)
                {
                    Result = ((Task<bool>)t).Result;
                }
                CanReset = Can_ && Result;
                CanDelete = Can_;
                //Console.WriteLine("CanReset : {0}, CanDelete: {1},CanFreeze : {2}, CanUnFreeze: {3},CanRun: {4}", CanReset, CanDelete, CanFreeze, CanUnFreeze, CanRun);
            });
            return task;
        }

        private void checkNeutralization()
        {
            bool leftCount = GridItemLeft.Count() > 0;
            bool rightCount = GridItemRight.Count() > 0;
            CanNeutralization = CanNeutralization_ && ((CanRunOrNeutralizationLeft && leftCount) || (CanRunOrNeutralizationLeft && rightCount));
            CanUnNeutralization = CanUnNeutralization_ && ((CanUnNeutralizationLeft && leftCount) || (CanUnNeutralizationRight && rightCount));
            //Console.WriteLine("CanNeutralization : {0}, CanUnNeutralization: {1}", CanNeutralization, CanUnNeutralization);
        }

        private  Task AfterRefreshRight()
        {
           return RightGridRef.InputGridComponentRef.RefreshSelection(BottomGridRef.InputGridComponentRef.GetSelectedItem(GridItem.RIGHT_SIDE));
        }
        private async Task RightSelectionCanged(List<long> selectedItems)
        {
            //Task task = BottomGridRef.InputGridComponentRef.AddNewRow(selectedItems, GridItem.RIGHT_SIDE);
            //task = task.ContinueWith(t1 => BuildBalance(RightGridRef)).Unwrap();
            //task = task.ContinueWith(t1 => RightGridRef.RefreshFooter()).Unwrap();
            ////task = task.ContinueWith(t1 => InvokeAsync(StateHasChanged)).Unwrap();
            //return task;
            await BottomGridRef.InputGridComponentRef.AddNewRow(selectedItems, GridItem.RIGHT_SIDE);
            await BuildBalance(RightGridRef);
            await RightGridRef.RefreshFooter();
        }



        private async Task BottomSelectionCanged(List<long> selectedItems)
        {
            //Task task = BuildBalance(BottomGridRef, true);
            //task = task.ContinueWith(t1 => checkFreeze()).Unwrap();
            //task = task.ContinueWith(t1 => Task.Run(()=> checkNeutralization())).Unwrap();
            //task = task.ContinueWith(t1 => RenderFormRef.StateHasChanged_()).Unwrap();
            //task = task.ContinueWith(t1 => InvokeAsync(StateHasChanged)).Unwrap();
            //task = task.ContinueWith(t1 => InvokeAsync(ActionState)).Unwrap();
            //return task;
            await BuildBalance(BottomGridRef, true);
            await checkFreeze();
            await Task.Run(() => checkNeutralization());            
            await BottomGridRef.RefreshFooter();
            await RenderFormDxButtonRef.StateHasChanged_();
            //Task task = BuildBalance(BottomGridRef, true);
            //task = task.ContinueWith(t1 => checkFreeze()).Unwrap();
            //task = task.ContinueWith(t1 => Task.Run(() => checkNeutralization())).Unwrap();
            //task = task.ContinueWith(t1 => RenderFormDxButtonRef.StateHasChanged_()).Unwrap();
            //task = task.ContinueWith(t1 => BottomGridRef.RefreshFooter()).Unwrap();
            ////task = task.ContinueWith(t1 => InvokeAsync(StateHasChanged)).Unwrap();
            ////task = task.ContinueWith(t1 => InvokeAsync(ActionState)).Unwrap();
            //return task;
        }

        private async Task BottomSelectionCangedPopupRef(List<long> selectedItems)
        {
            //Task task = BuildBalance(BottomGridRefDxPopupRef, true);
            //task = task.ContinueWith(t1 => RenderFormContentDxButtonRef.StateHasChanged_());
            //task = task.ContinueWith(t1 => RenderFormRefDxPopupRef.StateHasChanged_());
            //task = task.ContinueWith(t1 => InvokeAsync(StateHasChanged)).Unwrap();
            //task = task.ContinueWith(t1 => InvokeAsync(ActionState)).Unwrap();
            //return task;
            await BuildBalance(BottomGridRefDxPopupRef, true);
            await RenderFormContentDxButtonRef.StateHasChanged_();
            await BottomGridRefDxPopupRef.RefreshFooter();
        }

        private async  Task RefreshAllGrid()
        {
            await RightGridRef.RefreshBody();
            await LeftGridRef.RefreshBody();
            await BottomGridRef.RefreshBody();
        }

        private  Task ClearGrid()
        {
            Task task = LeftGridRef.InputGridComponentRef.ClearGrid();
            task = task.ContinueWith(t1 => RightGridRef.InputGridComponentRef.ClearGrid()).Unwrap();
            task = task.ContinueWith(t1 => LeftGridRef.InputGridComponentRef.ClearGrid()).Unwrap();
            task = task.ContinueWith(t1 => BuildBalance(BottomGridRef, true)).Unwrap();
            task = task.ContinueWith(t1 => BottomGridRef.InputGridComponentRef.RefreshSelection()).Unwrap();
            task = task.ContinueWith(t1 => RefreshAllGrid());
            return task;
        }

        private Task RefreshGrid()
        {
            Task task = LeftGridRef.InputGridComponentRef.RefreshGrid();
            task = task.ContinueWith(t1 => RightGridRef.InputGridComponentRef.RefreshGrid()).Unwrap();
            task = task.ContinueWith(t1 => LeftGridRef.InputGridComponentRef.RefreshGrid()).Unwrap();
            task = task.ContinueWith(t1 => BuildBalance(LeftGridRef)).Unwrap();
            task = task.ContinueWith(t1 => BuildBalance(RightGridRef)).Unwrap();
            task = task.ContinueWith(t1 => BuildBalance(BottomGridRef, true)).Unwrap();
            task = task.ContinueWith(t1 => RefreshAllGrid()).Unwrap();
            return task;
        }

        private  Task RefreshPartialAmounts()
        {
            Task task = LeftGridRef.InputGridComponentRef.RefreshPartialGrid();
            task = task.ContinueWith(t1 => RightGridRef.InputGridComponentRef.RefreshPartialGrid()).Unwrap();
            task = task.ContinueWith(t1 => BottomGridRef.InputGridComponentRef.RefreshPartialGrid()).Unwrap();
            task = task.ContinueWith(t1 => BuildBalance(LeftGridRef)).Unwrap();
            task = task.ContinueWith(t1 => BuildBalance(RightGridRef)).Unwrap();
            task = task.ContinueWith(t1 => BuildBalance(BottomGridRef, true)).Unwrap();
            task = task.ContinueWith(t1 => RefreshAllGrid()).Unwrap();
            return task;
        }


        private async void RefreshPartialGrid__()
        {
            await RefreshPartialAmounts();
        }


        public ValueTask DisposeAsync()
        {
            AppState.RefreshReconciliationHander -= RefreshPartialGrid__;
            AppState.CanRefreshReconciliation = false;
            return ValueTask.CompletedTask;
        }

        private bool IsColumnPosition()
        {
            return EditorDataBinding.Item.RigthGridPositions.Equals(RecoGridPosition.COLUMN);
        }

        private void ChangePosition(ToolbarItemClickEventArgs args)
        {
            ChangePosition_();
        }
        private  void ChangePosition_()
        {
            ShouldRender_ = true;
            if (IsColumnPosition())
            {
                EditorDataBinding.Item.RigthGridPosition = RecoGridPosition.ROW.code;
            }
            else
            {
                EditorDataBinding.Item.RigthGridPosition = RecoGridPosition.COLUMN.code;
            }
            ActionState?.Invoke();
        }

        #region Balance

        private Task BuildBalance(RecoNewGrid form, bool IsBottom = false)
        {
            if (this.EditorData.Item.UseDebitCredit)
            {
                //form.Grid.EnableDebitCreditBox(this.EditedObject.UseDebitCredit);
            }


            String creditValue = "C";
            String debitValue = "D";
            long? dcId = null;
            if (this.EditorData.Item.UseDebitCredit)
            {
                creditValue = GetModelEditorData().CreditValue;
                debitValue = GetModelEditorData().DebitValue;
                dcId = GetModelEditorData().DebitCreditAttributeId;
            }

            if (IsBottom)
            {
                //grid.GridForm.desableCheckBox();
                Decimal[] balances = BuildBottomBalance(form,this.EditorData.Item.LeftMeasureId, this.EditorData.Item.RigthMeasureId, dcId, creditValue, debitValue);
                DisplayBalance(form, balances[0], balances[1], balances[2], true);
            }
            else
            {
                long? measureId = form is LeftNewGrid ? this.EditorData.Item.LeftMeasureId : this.EditorData.Item.RigthMeasureId;
                Decimal[] balances = BuildBalance(form,measureId, dcId, creditValue, debitValue);
                if (this.EditorData.Item.UseDebitCredit)
                {
                    DisplayBalance(form, balances[0], balances[1], balances[0] - balances[1]);
                }
                else
                {
                    DisplayBalance(form, balances[0], balances[1], balances[0] - Math.Abs(balances[1]));
                }
            }

            return Task.CompletedTask;
        }

        private void DisplayBalance(RecoNewGrid form, Decimal Credit, Decimal Debit, Decimal Balance, bool isBottom = false)
        {
            String credit = isBottom ? "left" : (this.EditorData.Item.UseDebitCredit ? "Credit" : "positive.amount"); 
            String debit = isBottom ? "right" : (this.EditorData.Item.UseDebitCredit ? "Debit" : "negative.amount");
            String balance = "balance";
            form.Credit = Credit.ToString("n2");
            form.CreditLabel = credit;
            form.Debit = Debit.ToString("n2");
            form.DebitLabel = debit;
            form.Balance = Balance.ToString("n2");
            form.BalanceLabel = balance;
            form.RecoCustomToolBar.Refresh();
        }

        private Decimal[] BuildBalance(RecoNewGrid form, long? measureId, long? dcId, String creditValue, String debitValue)
        {
            Decimal credit = 0;
            Decimal debit = 0;
            GrilleColumn amountColumn = null;
            GrilleColumn creditDebitColumn = null;
            GrilleColumn reconciliatedColumn = null;
            GrilleColumn remainingColumn = null;

            if (measureId.HasValue)
            {
                amountColumn = form.InputGridComponentRef.EditorData.Item.GetColumn(DimensionType.MEASURE, measureId.Value);
            }



            if (this.EditorData.Item.AllowPartialReco)
            {
                reconciliatedColumn = form.InputGridComponentRef.EditorData.Item.GetColumn(DimensionType.MEASURE, this.EditorData.Item.ReconciliatedMeasureId);
                remainingColumn = form.InputGridComponentRef.EditorData.Item.GetColumn(DimensionType.MEASURE, this.EditorData.Item.RemainningMeasureId);
            }

            if (amountColumn != null)
            {
                if (dcId.HasValue && this.EditorData.Item.UseDebitCredit)
                {
                    creditDebitColumn = form.InputGridComponentRef.EditorData.Item.GetColumn(DimensionType.ATTRIBUTE, dcId.Value);
                }
                foreach (object row in form.InputGridComponentRef.SelectedDataItems)
                {
                    if (row is GridItem)
                    {
                        GridItem gridItem = (GridItem)row;
                        Object[] datas = gridItem.Datas;
                        Decimal amount = GetAmount(datas, amountColumn);
                        Decimal reconcilitedAmount = GetAmount(datas, reconciliatedColumn);
                        Decimal remainingAmount = GetAmount(datas, remainingColumn);
                        if (this.EditorData.Item.AllowPartialReco)
                        {
                            if (reconcilitedAmount == 0 && remainingAmount == 0)
                            {
                                remainingAmount = amount;
                            }
                            else
                            {
                                amount = remainingAmount;
                            }
                        }
                        if (this.EditorData.Item.UseDebitCredit)
                        {
                            if (creditDebitColumn == null) continue;
                            object item = datas[creditDebitColumn.Position];
                            Boolean isCredit = item != null && item.ToString().Equals(creditValue, StringComparison.OrdinalIgnoreCase);
                            Boolean isDebit = item != null && item.ToString().Equals(debitValue, StringComparison.OrdinalIgnoreCase);
                            if (isCredit) credit += amount;
                            else if (isDebit) debit += amount;
                        }
                        else
                        {
                            if (amount > 0) credit += amount;
                            else debit += amount;
                        }
                    }
                }
            }
            Decimal[] balances = new Decimal[] { credit, debit };
            return balances;
        }

        private Decimal[] BuildBottomBalance(RecoNewGrid form, long? measureId, long? rightMeasureId, long? dcId, String creditValue, String debitValue)
        {
            Decimal credit = 0;
            Decimal debit = 0;
            GrilleColumn amountColumn = null;
            GrilleColumn rightAmountColumn = null;
            GrilleColumn creditDebitColumn = null;
            GrilleColumn reconciliatedColumn = null;
            GrilleColumn remainingColumn = null;

            if (measureId.HasValue)
            {
                amountColumn = form.InputGridComponentRef.EditorData.Item.GetColumn(DimensionType.MEASURE, measureId.Value);
            }
            if (rightMeasureId.HasValue)
            {
                rightAmountColumn = form.InputGridComponentRef.EditorData.Item.GetColumn(DimensionType.MEASURE, rightMeasureId.Value);
            }
            if (rightAmountColumn == null)
            {
                rightAmountColumn = amountColumn;
            }

            if (this.EditorData.Item.AllowPartialReco)
            {
                reconciliatedColumn = form.InputGridComponentRef.EditorData.Item.GetColumn(DimensionType.MEASURE, this.EditorData.Item.ReconciliatedMeasureId);
                remainingColumn = form.InputGridComponentRef.EditorData.Item.GetColumn(DimensionType.MEASURE, this.EditorData.Item.RemainningMeasureId);
            }

            if (amountColumn != null)
            {
                if (this.EditorData.Item.UseDebitCredit)
                {
                    if (dcId.HasValue)
                    {
                        creditDebitColumn = form.InputGridComponentRef.EditorData.Item.GetColumn(DimensionType.ATTRIBUTE, dcId.Value);
                    }
                }

                foreach (object row in form.InputGridComponentRef.SelectedDataItems)
                {
                    if (row is GridItem)
                    {
                        GridItem gridItem = (GridItem)row;
                        Object[] datas = gridItem.Datas;
                        bool isLeftSide = gridItem.IsLeftSide();
                        Decimal amount = GetAmount(datas, isLeftSide ? amountColumn : rightAmountColumn);
                        Decimal reconcilitedAmount = GetAmount(datas, reconciliatedColumn);
                        Decimal remainingAmount = GetAmount(datas, remainingColumn);

                        if (this.EditorData.Item.AllowPartialReco)
                        {
                            if (reconcilitedAmount == 0 && remainingAmount == 0)
                            {
                                remainingAmount = amount;
                            }
                            else
                            {
                                amount = remainingAmount;
                            }
                        }

                        if (this.EditorData.Item.UseDebitCredit)
                        {
                            if (creditDebitColumn == null)
                            {
                                continue;
                            }
                            object item = datas[creditDebitColumn.Position];
                            Boolean isCredit = item != null && item.ToString().Equals(creditValue, StringComparison.OrdinalIgnoreCase);
                            Boolean isDebit = item != null && item.ToString().Equals(debitValue, StringComparison.OrdinalIgnoreCase);

                            if (isDebit)
                            {
                                amount = 0 - amount;
                            }

                            if (isLeftSide)
                            {
                                credit += amount;
                            }
                            else
                            {
                                debit += amount;
                            }
                        }
                        else
                        {
                            if (isLeftSide)
                            {
                                credit += amount;
                            }
                            else
                            {
                                debit += amount;
                            }
                        }
                    }
                }
            }
            Decimal balance = 0;
            if (this.EditorData.Item.BalanceFormulas == ReconciliationModelBalanceFormula.LEFT_PLUS_RIGHT)
            {
                balance = credit + debit;
            }
            else
            {
                balance = credit - debit;
            }
            Decimal[] balances = new Decimal[] { credit, debit, balance };
            return balances;
        }



        #endregion

        #region run action

        private bool modalAction_ { get; set; }  = false;
        private bool modalAction { get => modalAction_; 
            set {
                modalAction_ = value;
                RenderFormContentModalAction.StateHasChanged_();
            }
        }

        private RenderFormContent RenderFormContentModalAction { get; set; }
        private bool modalActionFreeze_ { get; set; }  = false;
        private bool modalActionFreeze
        {
            get => modalActionFreeze_;
            set
            {
                modalActionFreeze_ = value;
                RenderFormContentModalActionFreeze.StateHasChanged_();
            }
        }

        private RenderFormContent RenderFormContentModalActionFreeze { get; set; }

        private bool modalActionNeutralization_ { get; set; } = false;
        private bool modalActionNeutralization
        {
            get => modalActionNeutralization_;
            set
            {
                modalActionNeutralization_ = value;
                RenderFormContentModalActionNeutralization.StateHasChanged_();
            }
        }
        private RenderFormContent RenderFormContentModalActionNeutralization { get; set; }
        private RenderFormContent RenderFormContentDxButtonRef { get; set; }
        private RenderFormContent RenderFormDxButtonRef { get; set; }
        
        private DxPopup DxPopupRef { get; set; }
        private DxPopup DxPopupRefFreeze { get; set; }
        private DxPopup DxPopupRefNeutralization { get; set; }    
        private BottomNewGrid BottomGridRefDxPopupRef { get; set; }

        private void ConfirmDialogRun()
        {
            modalAction = true;
        }

        private  Task ConfirmChoiseDialogRun()
        {
            return Task.Run(() => {
                decimal.TryParse(BottomGridRef.Balance, out decimal value);
            if (value != 0)
            {
                ConfirmSelection = true;
            }
            else
            {
                ConfirmDialogRun();
            }
            });
        }

        private Task ConfirmDialogRunFreeze()
        {
            return Task.Run(() => { modalActionFreeze = true; });
        }

        private Task ConfirmDialogNeutralization()
        {
            return Task.Run(() => { modalActionNeutralization = true; });
        }

        private Task StateHasChanged_()
        {
            return InvokeAsync(StateHasChanged);
        }

        private async Task Run()
        {
            //Task tas = CheckGridColumnsConfig?.Invoke(EditorData);
            //tas = tas.ContinueWith(t => ReconciliationModelService.Reconciliate(GetReconciliationData(EditorDataBinding.Item.RecoAttributeId)));
            //tas = tas.ContinueWith(t => Task.Run(() => { modalAction = false; }));
            //tas = tas.ContinueWith(t => StateHasChanged_());
            //tas = tas.ContinueWith(t => RefreshGrid());
            //return tas;

           await CheckGridColumnsConfig?.Invoke(EditorData);
           await ReconciliationModelService.Reconciliate(GetReconciliationData(EditorDataBinding.Item.RecoAttributeId));
           modalAction = false;
           await RefreshGrid();
        }

        private bool VisibleRow2 { get; set; } = false;
        private bool VisibleRow3 { get; set; } = false;

        private void ConfirmSelectionBeforePerformAction(bool selectedWriteOff = true)
        {
            ChoiseSelection = selectedWriteOff;
            ConfirmSelection = false;
            VisibleRow2 = EditorData.Item.AllowWriteOff && EditorData.Item.WriteOffModel.FieldListChangeHandler.GetItems().Any() && ChoiseSelection;
            VisibleRow3 = EditorData != null && EditorData.Item != null && EditorData.Item.WriteOffModel != null && EditorData.Item.WriteOffModel.FieldListChangeHandler.GetItems().Any() && ChoiseSelection;
            ConfirmDialogRun();
        }

        private void CancelConfirmSelectionBeforePerformAction()
        {
            ConfirmSelection = false;
        }

        private async Task ResetReconciliation()
        {
            //Task tas = ReconciliationModelService.ResetReconciliation(GetReconciliationData(EditorDataBinding.Item.RecoAttributeId));
            //tas = tas.ContinueWith(t => RefreshPartialAmounts());
            //return tas;
            await ReconciliationModelService.ResetReconciliation(GetReconciliationData(EditorDataBinding.Item.RecoAttributeId));
            await RefreshPartialAmounts();
        }

        private async Task RunFreeze()
        {
            //Task tas = ReconciliationModelService.RunFreeze(GetReconciliationData(EditorDataBinding.Item.RecoAttributeId));
            //tas = tas.ContinueWith(t => Task.Run(() => modalActionFreeze = false));
            //tas = tas.ContinueWith(t => RefreshGrid());
            //return tas;
            await ReconciliationModelService.RunFreeze(GetReconciliationData(EditorDataBinding.Item.RecoAttributeId));
            modalActionFreeze = false;
            await RefreshGrid();
        }

        private  async Task RunUnFreeze()
        {
            //Task tas = ReconciliationModelService.RunUnFreeze(GetReconciliationData(EditorDataBinding.Item.RecoAttributeId));
            //tas = tas.ContinueWith(t => RefreshGrid());
            //return tas;
            await ReconciliationModelService.RunUnFreeze(GetReconciliationData(EditorDataBinding.Item.RecoAttributeId));
            await RefreshGrid();
        }

        private async Task RunNeutralization()
        {
            //Task tas = ReconciliationModelService.RunNeutralization(GetReconciliationData(EditorDataBinding.Item.RecoAttributeId));
            //tas = tas.ContinueWith(t => Task.Run(() => modalActionNeutralization = false));
            //tas = tas.ContinueWith(t => RefreshGrid());
            //return tas; 
            await ReconciliationModelService.RunNeutralization(GetReconciliationData(EditorDataBinding.Item.RecoAttributeId));
            modalActionNeutralization = false;
            await RefreshGrid();
        }

        private async Task RunUnNeutralization()
        {
            //Task tas = ReconciliationModelService.RunUnNeutralization(GetReconciliationData(EditorDataBinding.Item.RecoAttributeId));
            //tas = tas.ContinueWith(t => RefreshGrid());
            //return tas;
            await ReconciliationModelService.RunUnNeutralization(GetReconciliationData(EditorDataBinding.Item.RecoAttributeId));
            await RefreshGrid();
        }


        public ReconciliationData GetReconciliationData(long? recoTypeId)
        {
            decimal.TryParse(BottomGridRef.Credit, out decimal LeftAmount);
            decimal.TryParse(BottomGridRef.Debit, out decimal RigthAmount);
            decimal.TryParse(BottomGridRef.Balance, out decimal BalanceAmount);
            ReconciliationData ReconciliationData_ = new ReconciliationData()
            {
                ReconciliationId = EditorData.Item.Id,
                Leftids = BottomGridRefDxPopupRef.InputGridComponentRef.GetSelectionDataItemsIds(item => item.IsLeftSide()),
                Rightids = BottomGridRefDxPopupRef.InputGridComponentRef.GetSelectionDataItemsIds(item => item.IsRightSide()),
                RecoTypeId = recoTypeId,
                WriteOffMeasureId = EditorData.Item.WriteOffModel.WriteOffMeasureId,
                WriteOffAmount = BalanceAmount,
                LeftAmount = LeftAmount,
                RigthAmount = RigthAmount,
                BalanceAmount = 0,
                WriteOffFields = EditorData.Item.WriteOffModel.FieldListChangeHandler.GetItems(),
                EnrichmentItemDatas = EditorData.Item.EnrichmentListChangeHandler.GetItems(),
                AddRecoDate = EditorData.Item.AddRecoDate,
                AddUser = EditorData.Item.AddUser,
                AddNote = EditorData.Item.AddNote,
                MandatoryNote = EditorData.Item.MandatoryNote,
                Note = Note,
                AddAutomaticManual = EditorData.Item.AddAutomaticManual,
                RecoDateId = EditorData.Item.RecoPeriodId,

                AllowPartialReco = EditorData.Item.AllowPartialReco,
                AllowNeutralization = EditorData.Item.AllowNeutralization,
                AllowFreeze = EditorData.Item.AllowFreeze,
                FreezeAttributeId = EditorData.Item.FreezeAttributeId,
                FreezeSequenceId = EditorData.Item.FreezeSequenceId,
                LeftMeasureId = EditorData.Item.LeftMeasureId,
                NeutralizationAttributeId = EditorData.Item.NeutralizationAttributeId,
                NeutralizationSequenceId = EditorData.Item.NeutralizationSequenceId,
                PartialRecoAttributeId = EditorData.Item.PartialRecoAttributeId,
                PartialRecoSequenceId = EditorData.Item.PartialRecoSequenceId,
                ReconciliatedMeasureId = EditorData.Item.ReconciliatedMeasureId,
                RecoSequenceId = EditorData.Item.RecoSequenceId,
                RemainningMeasureId = EditorData.Item.RemainningMeasureId,
                RigthMeasureId = EditorData.Item.RigthMeasureId,
                PerformPartialReco = !ChoiseSelection,
            };
            BuildPartialRecoItems(ReconciliationData_);
            return ReconciliationData_;
        }

        private void BuildPartialRecoItems(ReconciliationData reco)
        {
            GrilleColumn amountColumn = null;
            GrilleColumn rightAmountColumn = null;
            GrilleColumn reconciliatedColumn = null;
            GrilleColumn remainingColumn = null;
            RecoNewGrid form = BottomGridRef;
            if (this.EditorData.Item.LeftMeasureId != null)
            {
                amountColumn = form.InputGridComponentRef.EditorData.Item.GetColumn(DimensionType.MEASURE, EditorData.Item.LeftMeasureId.Value);
            }
            if (this.EditorData.Item.RigthMeasureId != null)
            {
                rightAmountColumn = form.InputGridComponentRef.EditorData.Item.GetColumn(DimensionType.MEASURE, this.EditorData.Item.RigthMeasureId.Value);
            }
            if (rightAmountColumn == null)
            {
                rightAmountColumn = amountColumn;
            }

            if (this.EditorData.Item.AllowPartialReco)
            {
                reconciliatedColumn = form.InputGridComponentRef.EditorData.Item.GetColumn(DimensionType.MEASURE, this.EditorData.Item.ReconciliatedMeasureId.Value);
                remainingColumn = form.InputGridComponentRef.EditorData.Item.GetColumn(DimensionType.MEASURE, this.EditorData.Item.RemainningMeasureId.Value);
            }

            decimal leftAmount = reco.LeftAmount;
            decimal rigthAmount = reco.RigthAmount;

            reco.PartialRecoItems = new ObservableCollection<PartialRecoItem>();
            foreach (Object row in form.InputGridComponentRef.SelectedDataItems)
            {
                if (row is GridItem)
                {
                    GridItem gridItem = (GridItem)row;
                    bool isLeftSide = gridItem.IsLeftSide();
                    PartialRecoItem item = new PartialRecoItem();
                    item.Id = gridItem.GetId();
                    item.Left = isLeftSide;

                    Decimal amount = GetAmount(gridItem.Datas, isLeftSide ? amountColumn : rightAmountColumn);
                    Decimal reconcilitedAmount = GetAmount(gridItem.Datas, reconciliatedColumn);
                    Decimal remainingAmount = GetAmount(gridItem.Datas, remainingColumn);

                    item.Amount = amount;
                    if (isLeftSide)
                    {
                        decimal amounttoreconciliate = remainingAmount <= rigthAmount ? remainingAmount : rigthAmount;
                        item.ReconciliatedAmount = reconcilitedAmount + amounttoreconciliate;
                        item.RemainningAmount = remainingAmount - amounttoreconciliate;
                        rigthAmount = rigthAmount - amounttoreconciliate;
                    }
                    else
                    {
                        decimal amounttoreconciliate = remainingAmount <= leftAmount ? remainingAmount : leftAmount;
                        item.ReconciliatedAmount = reconcilitedAmount + amounttoreconciliate;
                        item.RemainningAmount = remainingAmount - amounttoreconciliate;
                        leftAmount = leftAmount - amounttoreconciliate;
                    }
                    reco.PartialRecoItems.Add(item);
                }
            }
        }

        public Decimal GetAmount(Object[] datas, GrilleColumn column)
        {
            Decimal amount = 0;
            if (column != null)
            {
                object item = datas[column.Position];
                try
                {
                    String value = item.ToString();
                    bool ok = Decimal.TryParse(value, out amount);
                    if (!ok)
                    {
                        if (value.Contains(".")) value = value.Replace(".", ",");
                        else if (value.Contains(",")) value = value.Replace(",", ".");
                        Decimal.TryParse(value, out amount);
                    }
                }
                catch (Exception) { }
            }
            return amount;
        }

       

        #endregion

    }

  
}

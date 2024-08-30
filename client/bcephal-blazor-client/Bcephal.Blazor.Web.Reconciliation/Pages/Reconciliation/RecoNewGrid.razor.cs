using Bcephal.Models.Base;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Bcephal.Models.Grids;
using Bcephal.Models.Grids.Filters;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation;
using Bcephal.Blazor.Web.Reconciliation.Services;
using Bcephal.Models.Reconciliation;
using System.Collections.ObjectModel;
using Bcephal.Blazor.Web.Base.Shared;

namespace Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation
{
    public partial class RecoNewGrid : ComponentBase
    {
        [Inject] public ReconciliationModelService ReconciliationModelService { get; set; }
        [Inject] public AppState AppState { get; set; }

        #region Parameters
        [Parameter] public EditorData<Bcephal.Models.Grids.Grille> EditorData{ get; set; }
        [Parameter] public List<string> AddColumns { get; set; } = new();
        [Parameter] public ObservableCollection<GridItem> ManualyData { get; set; } = new ObservableCollection<GridItem>();
        [Parameter]
        public ObservableCollection<GridItem> SelectionManualyDatas { get; set; } = new ObservableCollection<GridItem>();
        [Parameter] public Action<BrowserDataFilter, RecoNewGrid> FilterHandler { get; set; }
        [Parameter] public bool UsingManualyData { get; set; } = false;
        [Parameter] public bool IsBottom { get; set; } = false;
        [Parameter] public bool CanDisplayToolBar { get; set; } = true;
        [Parameter] public bool DisplayCommandToobar { get; set; } = true;
        [Parameter] public bool RecoShowSelectionColumnVisible { get; set; } = true;
        [Parameter] public bool CanFreeze { get; set; } = false;
        [Parameter] public bool CanUnFreeze { get; set; } = false;
        [Parameter] public bool CanNeutralization { get; set; } = false;
        [Parameter] public bool CanUnNeutralization { get; set; } = false;
        [Parameter] public bool CanRun { get; set; } = false;
        [Parameter] public bool CanReset { get; set; } = false;
        [Parameter] public bool CanDelete { get; set; } = false;
        [Parameter] public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> EditorDataChanged { get; set; }
        [Parameter] public EventCallback<bool> CanFreezeChanged { get; set; }
        [Parameter] public EventCallback<bool> CanUnFreezeChanged { get; set; }
        [Parameter] public EventCallback<bool> CanNeutralizationChanged { get; set; }
        [Parameter] public EventCallback<bool> CanUnNeutralizationChanged { get; set; }
        [Parameter] public Func<Task> RunHander { get; set; }
        [Parameter] public Func<Task> ResetHander { get; set; }
        [Parameter] public Func<Task> ClearGridHander_ { get; set; }
        [Parameter] public Func<Task> RunFreezeHander { get; set; }
        [Parameter] public Func<Task> RunUnFreezeHander { get; set; }
        [Parameter] public Func<Task> NeutralizationHanderHander { get; set; }
        [Parameter] public Func<Task> UnNeutralizationHanderHander { get; set; }
        [Parameter] public Func<RenderFragment> HeaderRenderHandler { get; set; }
        [Parameter] public Func<List<long>, Task> SelectedHandler_ { get; set; }
        [Parameter] public Func<Task> DoHandlerAfterRefresh_ { get; set; }
        #endregion

        protected virtual string KeyName { get; set; } = "RecoNewGridAbstractGridComponent";
        protected virtual string height => "calc(100% - 21px)";
        public string Credit { get; set; } = "0";
        public string Debit { get; set; } = "0";
        public string Balance { get; set; } = "0";
        public string CreditLabel { get; set; } = "left";
        public string DebitLabel { get; set; } = "right";
        public string BalanceLabel { get; set; } = "balance";

        public RecoInputNewGridComponent InputGridComponentRef { get; set; }
        public RecoNewCustomToolBar RecoCustomToolBar { get; set; }
        
        private RenderFormContent RenderFormRef { get; set; }

        protected readonly IDictionary<string, object> attributes = new Dictionary<string, object>();

        protected override async Task OnInitializedAsync()
        {
            if (attributes != null)
            {
                if (HeaderRenderHandler != null)
                {
                    attributes.Add("CustomHeaderRenderHandler", HeaderRenderHandler);
                }
            }
            await base.OnInitializedAsync();
        }

        public Task RefreshFooter()
        {
            if(RenderFormRef != null)
            {
              return  RenderFormRef.StateHasChanged_();
            }
            return Task.CompletedTask;
        }
        public async Task RefreshBody()
        {
            if(InputGridComponentRef != null)
            {
              await InputGridComponentRef.RefreshGrid();
            }
            await RefreshFooter();
        }

    public EditorData<Bcephal.Models.Grids.Grille> EditorDataBindings
        {
            get { return EditorData; }
            set
            {
                if (value != null)
                {

                    EditorData = value;
                    EditorDataChanged.InvokeAsync(EditorData);
                    AppState.Update = true;
                }

            }
        }

        protected virtual ReconciliationModelService GetService()
        {
            return ReconciliationModelService;
        }

        private Task OnSelectionChangeHandler(List<long> selectedItems)
        {
            return SelectedHandler_?.Invoke(selectedItems);
        }

        public virtual bool AllowBuildChecBoxTotalPage  => true;
        private  Task OnDoHandlerAfterRefresh()
        {
           return DoHandlerAfterRefresh_?.Invoke();
        }

        private void OnFilterHandler(BrowserDataFilter filter)
        {
            FilterHandler?.Invoke(filter, this);
        }
        private Task OnRunHander()
        {
            return  RunHander?.Invoke();
        }
        private Task OnClearGridHander()
        {
            return ClearGridHander_?.Invoke();
        }
        private Task OnResetHander()
        {
            return ResetHander?.Invoke();
        }
        private Task OnRunFreezeHander()
        {
            return RunFreezeHander?.Invoke();
        }
        private Task OnRunUnFreezeHander()
        {
            return RunUnFreezeHander?.Invoke();
        }


        private Task OnNeutralizationHander()
        {
            return NeutralizationHanderHander?.Invoke();
        }
        private Task OnUnNeutralizationHander()
        {
          return  UnNeutralizationHanderHander?.Invoke();
        }
    }
}

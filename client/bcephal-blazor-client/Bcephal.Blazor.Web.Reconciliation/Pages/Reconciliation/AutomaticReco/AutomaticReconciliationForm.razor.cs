using System;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Reconciliation;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;

namespace Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation.AutomaticReco
{
    public partial class AutomaticReconciliationForm : Form<AutoReco, RecoBrowserData>
    {

        [Inject]
        private WebSocketAddress WebSocketAddress { get; set; }

        [Inject]
        AutoRecoService AutomaticRecoService { get; set; }
        [Inject] IJSRuntime JSRuntime { get; set; }

        [Parameter] public string Filterstyle { get; set; }
        [Parameter] public bool CanRefreshGrid { get; set; } = true;
        [Parameter] public int ActiveTabIndexFilter { get; set; } = 0;

        private string FilterKeyLeft { get; set; } = "FilterKeyLeft";
        private string FilterKeyRight { get; set; } = "FilterKeyRight";

        private int ActiveTabIndexFilterBinding
        {
            get => ActiveTabIndexFilter;
            set
            {
                ActiveTabIndexFilter = value;
                InvokeAsync(StateHasChanged);
            }
        }

        public UniverseFilter LeftFilterBinding
        {
            get { return EditorData.Item.LeftFilter; }
            set
            {
                EditorDataBinding.Item.LeftFilter = value;
            }
        }

        public UniverseFilter RightFilterBinding
        {
            get { return EditorData.Item.RightFilter; }
            set
            {

                EditorDataBinding.Item.RightFilter = value;
            }
        }

        public bool Editable
        {
            get
            {
                var first = AppState.PrivilegeObserver.CanCreatedReconciliationAutoReco;
                var second = AppState.PrivilegeObserver.CanEditReconciliationAutoReco(EditorData.Item);
                return first || second;
            }
        }

        public override string LeftTitle { get { return AppState["New.Automatic.Reconciliation"]; ; } }

        public override string LeftTitleIcon { get { return "bi-file-plus"; } }

        private string EditorRoute;

        protected override AutoRecoService GetService()
        {
            return AutomaticRecoService;
        }

        public override string GetBrowserUrl { get => Route.RECONCILIATION_AUTO_BROWSER; set => base.GetBrowserUrl = value; }
        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }


        #region Region reserved for Methods

        protected override EditorData<AutoReco> EditorDataBinding
        {
            get { return base.EditorDataBinding; }
            set
            {
                base.EditorDataBinding = value;
                if (EditorData != null && EditorData.Item != null && EditorData.Item.Id.HasValue)
                {
                    AppState.CanRun = true;
                }
            }
        }

        protected override async Task OnInitializedAsync()
        {
            EditorRoute = Route.RECONCILIATION_AUTO_FORM;
            await base.OnInitializedAsync();
        }

        protected override void OnAfterRender(bool firstRender)
        {
            base.OnAfterRender(firstRender);
            if (firstRender)
            {
                AppState.RunHander += RunReco;                
            }
        }

        public override async ValueTask DisposeAsync()
        {
            AppState.Update = false;
            AppState.CanRun = false;
            AppState.RunHander -= RunReco;
            await base.DisposeAsync();
        }

        public void StateHasChanged_()
        {
            StateHasChanged();
        }
        public async void RunReco()
        {
            try
            {
                if ( AppState.Update )
                {
                    AppState.Save();
                }

                //await JSRuntime.InvokeVoidAsync("console.log", "Try to manually run an automatic reconciliation");
                SocketJS Socket = new SocketJS(WebSocketAddress, null, JSRuntime, AppState, true);
                Socket.SendHandler += () =>
                {
                    Socket.FullyProgressbar.FullBase = false;
                    AppState.CanLoad = false;
                    string data = AutomaticRecoService.Serialize(EditorData.Item);
                    Socket.send(data);
                };
                await AutomaticRecoService.ConnectSocketJS(Socket, "/run");
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            finally
            {
                StateHasChanged();
            }
        }
        protected override string DuplicateName()
        {
            return AppState["duplicate.automatic.reconciliation.name", EditorData.Item.Name];
        }

        [Inject]
        SchedulerService SchedulerService { get; set; }

        protected override Task BeforeSave(EditorData<AutoReco> EditorData)
        {
            canStop = EditorData.Item.CanStop;
            canModifier = EditorData.Item.Modified;
            canRestart = EditorData.Item.CanRestart;
            return Task.CompletedTask;
        }

        bool canStop = false;
        bool canModifier = false;
        bool canRestart = false;
        protected async override void AfterSave(EditorData<AutoReco> EditorData)
        {
            if (canModifier)
            {
                if (canRestart)
                {
                    await SchedulerService.restart(AppState.ProjectCode, SchedulerType.AUTORECO, new() { EditorData.Item.Id.Value });
                }
                else
                    if (canStop)
                {
                    await SchedulerService.stop(AppState.ProjectCode, SchedulerType.AUTORECO, new() { EditorData.Item.Id.Value });
                }
                EditorData.Item.Init();
            }
        }

        #endregion

    }

}

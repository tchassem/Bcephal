using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Joins;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.Joins
{
    public partial class JoinForm : Form<Join, JoinBrowserData>
    {
        [Inject] IJSRuntime JSRuntime { get; set; }
        [Inject] public JoinService JoinService { get; set; }
        [Inject]
        SchedulerService SchedulerService { get; set; }
        [Inject] private WebSocketAddress WebSocketAddress { get; set; }
        public override string LeftTitle { get { return AppState["JOIN"]; } }
        public override string LeftTitleIcon { get { return "bi-file-plus"; } }
        public override bool usingUnitPane => false;
        public override string GetBrowserUrl { get => Route.BROWSER_REPORTING_JOIN; set => base.GetBrowserUrl = value; }
        protected override string DuplicateName()
        {
            return AppState["duplicate.join.name", EditorData.Item.Name];
        }

        int ActiveTabIndex_ { get; set; } = 0;

        bool IsConfirmation { get; set; } = true;

        bool DisplayDialogPublicationConfirmation_ { get; set; } = false;
        bool DisplayDialogPublicationConfirmation { 
            get => DisplayDialogPublicationConfirmation_;
            set {
                DisplayDialogPublicationConfirmation_ = value;
                if (RenderFormContentRef != null)
                {
                    RenderFormContentRef.StateHasChanged_();
                }
            }
        }

        protected string PublicationMessage { get; set; } = "";

        public bool Editable {
            get
            {
                if(EditorData != null)
                {
                    var first = AppState.PrivilegeObserver.CanCreatedReportingJoinGrid;
                    var second = EditorData != null && EditorData.Item != null && AppState.PrivilegeObserver.CanEditReportingJoinGrid(EditorData.Item);
                    return first || second;
                }
                else
                {
                    return false;
                }
            }
        }       

        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        protected override JoinService GetService()
        {
            return JoinService;
        }   

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);            
            if (firstRender)
            {
                AppState.Update = true;
                AppState.CanRunJoin = true;
                AppState.RunJoinHander += RunJoinPublication_;
            }
            if (firstRender && !Id.HasValue)
            {
                ActiveTabIndex = 1;
            }
            else if (firstRender)
            {
                ActiveTabIndex = 0;
            }
        }

        public override async ValueTask DisposeAsync()
        {
            AppState.Update = false;
            if (AppState.CanRunJoin)
            {
                AppState.CanRunJoin = false;
                AppState.RunJoinHander -= RunJoinPublication_;
            }
            await base.DisposeAsync();
        }

        public int ActiveTabIndex
        {
            get => ActiveTabIndex_;
            set
            {
                ActiveTabIndex_ = value;                
                if (ActiveTabIndex_ == 0)
                {
                    RemoveProperties();
                }
                else
                {
                    AddProperties();
                }
            }
        }

        private void AddProperties()
        {
            RefreshRightContent(RightContent___);
        }

        private void RemoveProperties()
        {
            RefreshRightContent(null);
        }

        public void ServerMsgSent(object sender, object message)
        {
            if (message != null && !string.IsNullOrWhiteSpace(message.ToString()))
            {
                JSRuntime.InvokeVoidAsync("\n===> message: ", message.ToString());
            }
        }

        public async void RunJoinPublication()
        {
            //if (Join.RoutineListChangeHandler.Items.Count > 0)
            //{
                try
                {                    
                    AppState.ShowLoadingStatus();
                    DisplayDialogPublicationConfirmation = false;

                    if (AppState.Update)
                        {
                            AppState.Save();
                        }

                    await JSRuntime.InvokeVoidAsync("console.log", "Attempt to handling log of joining!");
                    SocketJS Socket = new SocketJS(WebSocketAddress, ServerMsgSent, JSRuntime, AppState, true);
                    bool valueClose = false;
                    bool valueError = false;
                    Socket.CloseHandler += () =>
                    {
                        if (!valueClose && !valueError)
                        {
                            AppState.HideLoadingStatus();
                            ToastService.ShowSuccess(AppState["trans.join.run.success"], AppState["Loader"]);
                            valueClose = true;
                        }
                    };
                    Socket.ErrorHandler += (errorMessage) =>
                    {
                        if (!valueError)
                        {
                            AppState.HideLoadingStatus();
                            ToastService.ShowError((string)errorMessage, AppState["Error"]);
                            valueError = true;
                        }
                    };
                    Socket.SendHandler += () =>
                    {
                        Socket.FullyProgressbar.FullBase = false;
                        AppState.CanLoad = false;
                        string data = GetService().Serialize(EditorData.Item);
                        Socket.send(data);
                        AppState.HideLoadingStatus();
                    };
                
                    await GetService().ConnectSocketJS(Socket, "/run-join");
                }
                catch (Exception ex)
                {
                    Error.ProcessError(ex);
                }
                finally
                {
                    AppState.HideLoadingStatus();
                    StateHasChanged();
                }
            //}
            //else
            //{
            //    ToastService.ShowError(AppState["trans.join.run.error"], AppState["Loader"]);
            //}
        }

        RenderFormContent RenderFormContentRef { get; set; }

        private void RunJoinPublication_()
        {
            PublicationMessage = AppState["confirmation.publication.message", EditorDataBinding.Item.Name];
            DisplayDialogPublicationConfirmation = true;            
        }

        private  void CancelDialogPublicationConfirmation()
        {
            DisplayDialogPublicationConfirmation = false;
        }

        protected override Task BeforeSave(EditorData<Join> EditorData)
        {
            canStop = EditorData.Item.CanStop;
            canModifier = EditorData.Item.Modified;
            canRestart = EditorData.Item.CanRestart;
            return Task.CompletedTask;
        }

        bool canStop = false;
        bool canModifier = false;
        bool canRestart = false;
        protected async override void AfterSave(EditorData<Join> EditorData)
        {
            if (canModifier)
            {

                if (canRestart)
                {
                    await SchedulerService.restart(AppState.ProjectCode, SchedulerType.JOIN, new() { EditorData.Item.Id.Value });
                }
                else
                    if (canStop)
                {
                    await SchedulerService.stop(AppState.ProjectCode, SchedulerType.JOIN, new() { EditorData.Item.Id.Value });
                }
                EditorData.Item.Init();
            }
        }
    }
}

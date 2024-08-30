using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Scheduling.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Planners;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Scheduling.Pages.SchedulerPlanner_
{
    public partial class SchedulerPlannerForm: Form<SchedulerPlanner, SchedulerPlannerBrowserData>
    {
        public override string LeftTitle { get { return AppState["SCHEDULER"]; } }
        public override string LeftTitleIcon { get { return "bi-file-plus"; } }
        public override bool usingUnitPane => false;
        public override string GetBrowserUrl { get => Route.SCHEDULED_PLANNER_BROWSER; set => base.GetBrowserUrl = value; }
        protected override string DuplicateName()
        {
            return AppState["duplicate.scheduler.planner.name", EditorData.Item.Name];
        }

        [Inject] private WebSocketAddress WebSocketAddress { get; set; }
        [Inject] private IJSRuntime JSRuntime { get; set; }
        [Inject] public SchedulerService SchedulerService { get; set; }
        [Inject] private SchedulerPlannerService SchedulePlannerService { get; set; }
        

        public bool Editable
        {
            get
            {
                if (EditorData != null)
                {
                    var first = AppState.PrivilegeObserver.CanCreatedSchedulerPlannerScheduler;
                    var second = AppState.PrivilegeObserver.CanEditSchedulerPlannerScheduler(EditorData.Item);
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

        protected override SchedulerPlannerService GetService()
        {
            return SchedulePlannerService;
        }

        protected override void OnAfterRender(bool firstRender)
        {
            base.OnAfterRender(firstRender);
            if (firstRender)
            {
                AppState.Update = true;
                AppState.RunHander += Run;
                AppState.CanRun = true;
            }
        }

        private int ActiveTabIndex_ { get; set; } = 0;
        public int ActiveTabIndex
        {
            get => ActiveTabIndex_;
            set
            {
                ActiveTabIndex_ = value;
                if (ActiveTabIndex_ == 0)
                {
                    AddProperties();                    
                }
                else
                {
                    RemoveProperties();
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

        public override async ValueTask DisposeAsync()
        {
            AppState.Update = false;
            AppState.RunHander -= Run;
            AppState.CanRun = false;
            await base.DisposeAsync();
        }


        public async void Run()
        {
            try
            {
                
                if (EditorData != null && EditorData.Item != null)
                {
                    AppState.ShowLoadingStatus();
                    if (!EditorData.Item.Id.HasValue)
                    {
                        save();
                    }
                    try
                    {
                        SocketJS Socket = new SocketJS(WebSocketAddress, null, JSRuntime, AppState, true);                        
                        bool valueClose = false;
                        bool valueError = false;
                        Socket.CloseHandler += () =>
                        {
                            if (!valueClose && !valueError)
                            {
                                AppState.HideLoadingStatus();
                                ToastService.ShowSuccess(AppState["scheduler.planner.run.succes.message", EditorData.Item.Name], AppState["scheduler.planner"]);
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
                            AppState.HideLoadingStatus();
                            string data = SchedulePlannerService.Serialize(EditorData.Item);
                            Socket.send(data);
                        };
                        await SchedulePlannerService.ConnectSocketJS(Socket, "/run", true);
                    }
                    catch (Exception ex)
                    {
                        Error.ProcessError(ex);
                    }
                }
                else
                {
                    ToastService.ShowWarning(AppState["empty.input.files"], AppState["warning"]);
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }
    }
}

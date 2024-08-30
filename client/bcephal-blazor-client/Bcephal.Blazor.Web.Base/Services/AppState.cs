using System;
using System.Collections.ObjectModel;
using System.Collections.Specialized;
using System.Globalization;
using System.Text.Json.Serialization;
using System.Threading;
using System.Threading.Tasks;
using System.Web;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Projects;
using Bcephal.Models.Users;
using Microsoft.AspNetCore.Components;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Localization;

using Microsoft.AspNetCore.Components.WebAssembly.Hosting;
using Microsoft.JSInterop;
using System.Net.Http;
using System.Reflection;
using Microsoft.Extensions.Options;
using Microsoft.Extensions.FileProviders;
using System.Collections.Generic;
using System.IO;
using System.Resources;
using System.Collections;
using System.Linq;
using System.Collections.Concurrent;
using DevExpress.Blazor.Localization;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class AppState
    {
        #region properties
        public long? ClientId { get; set; }
        public long? ProfilId { get; set; }
        public string SendMessage { get; set; } = "send.messages";
        public string ResetMessage { get; set; } = "reset.messages";

        [JsonIgnore]
        public bool IsOpenProject { get { return !String.IsNullOrEmpty(ProjectName); } }
        [JsonIgnore]
        public bool Download { get { return !IsOpenProject; } }

        [JsonIgnore]
        public bool IsDashboard { get; set; } = false;

        public string ProjectName { get; set; }
        public long? ProjectId { get; set; }

        public Models.Security.User CurrentUser { get; set; }
        public string ProjectCode { get; set; }

        [JsonIgnore]
        public bool ActivateSchedulingFileLoaderVisible_ { get; set; }

        [JsonIgnore]
        public customStringLocalizer Localize { private get; set; }

        [JsonIgnore]
        public string this[string name] { get => Localize[name]; }
        [JsonIgnore]
        public string  this[string name, params object[] arguments] { get => Localize[name, arguments]; }

        [JsonIgnore]
        public bool ActivateSchedulingAutoRecoVisible_ { get; set; }
        [JsonIgnore]
        public bool ActivateSchedulingLogAutoRecoVisible_ { get; set; }
        [JsonIgnore]
        public bool ActivateSchedulingFileLoader_ { get; set; } = false;
        [JsonIgnore]
        public bool ActivateSchedulingAutoReco_ { get; set; } = false;
        [JsonIgnore]
        public bool ActivateSchedulingLogAutoReco_ { get; set; } = false;
        [JsonIgnore]
        public bool ActivateSchedulingFileLoaderVisible
        {
            get { return ActivateSchedulingFileLoaderVisible_; }
            set
            {
                ActivateSchedulingFileLoaderVisible_ = value;
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool ActivateSchedulingAutoRecoVisible
        {
            get { return ActivateSchedulingAutoRecoVisible_; }
            set
            {
                ActivateSchedulingAutoRecoVisible_ = value;
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool ActivateSchedulingLogAutoRecoVisible
        {
            get { return ActivateSchedulingLogAutoRecoVisible_; }
            set
            {
                ActivateSchedulingLogAutoRecoVisible_ = value;
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        private bool ActivateSchedulingFileLoaderInit_ { get; set; } = false;
        [JsonIgnore]
        private bool ActivateSchedulingAutoRecoInit_ { get; set; } = false;
        [JsonIgnore]
        private bool ActivateSchedulingLogAutoRecoInit_ { get; set; } = false;
        [JsonIgnore]
        public bool ActivateSchedulingFileLoaderInit
        {
            get { return ActivateSchedulingFileLoaderInit_; }
            set
            {
                ActivateSchedulingFileLoaderInit_ = value;
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool ActivateSchedulingAutoRecoInit
        {
            get { return ActivateSchedulingAutoRecoInit_; }
            set
            {
                ActivateSchedulingAutoRecoInit_ = value;
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool ActivateSchedulingLogAutoRecoInit
        {
            get { return ActivateSchedulingLogAutoRecoInit_; }
            set
            {
                ActivateSchedulingLogAutoRecoInit_ = value;
                NotifyStateChanged();
            }
        }
        public string lastUri { get; set; }

        private string LoadingStatus_ { get; set; }
        [JsonIgnore]
        public string LoadingStatus {
            get {
                return LoadingStatus_;
            }
            set {
                LoadingStatus_ = value;
                LoadingHander?.Invoke();
                NotifyStateChanged();
            }
        }

        public enum LoadingTypeSender
        {
            DASHBOARD,
            OTHER,
            All,
        }

        LoadingTypeSender? TypeSender = null;
        public void ShowLoadingStatus(LoadingTypeSender? typeSender = null)
        {
            if (!TypeSender.HasValue)
            {
                LoadingStatus = "show";
            }
            TypeSender = typeSender;
        }
        public void HideLoadingStatus(LoadingTypeSender? typeSender = null)
        {
            bool isEmpty = !typeSender.HasValue && !TypeSender.HasValue;
            bool hasValue = typeSender.HasValue && TypeSender.HasValue;
            bool isIdentity = hasValue && typeSender.Equals(TypeSender);
            bool isAll = hasValue && typeSender.Equals(LoadingTypeSender.All);

            if (isEmpty || (isIdentity || isAll))
            {
                LoadingStatus = "hidden";
            }
            if (isIdentity || isAll)
            {
                TypeSender = null;
            }
        }
        public event Func<Task> StateChanged;
        public event Func<Task> MenuStateChanged;
        public event Action LoadingHander;
        public event Func<SocketJS, Task> ProgressBarsHander;
        public event Func<Task> MenusHander;
        public event Func<bool, bool,Task> ChangedStateUploadProgressbarHander;
        public event Func<long,long ,Task> SetParentProgressbarHander;
        public event Func<long, long, Task> SetChildrenProgressbarHander;
        public event Func<RenderFragment> DashboardHander;
        public event Func<long,Task> EditProjectHander;
        public event Func<Task> CreateProjectHander;

        [JsonIgnore]
        public NavigationManager navigationManager { private get; set; }
        [JsonIgnore]
        public Action ValidateRunOutcomeHander { get; set; }
        [JsonIgnore]
        public Action Hander { get; set; }
        [JsonIgnore]
        public Action CancelHander { get; set; }
        [JsonIgnore]
        public Action SendHander { get; set; }
        [JsonIgnore]
        public Action RunHander { get; set; }
        [JsonIgnore]
        public Action RunJoinHander { get; set; }
        [JsonIgnore]
        public Action AutoReconciliationRunHander { get; set; }
        [JsonIgnore]
        public Action LoadHander { get; set; }
        [JsonIgnore]
        public Action RefreshHander { get; set; }
        [JsonIgnore]
        public Action RefreshPublicationHandler { get; set; }
        [JsonIgnore]
        public Action RefreshReconciliationHander { get; set; }
        [JsonIgnore]
        public Action RefreshFooterHandler { get; set; }

        [JsonIgnore]
        public Action ActivateSchedulingFileLoaderHandler { get; set; }
        [JsonIgnore]
        public Action ActivateSchedulingAutoRecoHandler { get; set; }
        [JsonIgnore]
        public Action ActivateSchedulingLogAutoRecoHandler { get; set; }
        [JsonIgnore]
        public Action CreateHander { get; set; }
        [JsonIgnore]
        public Action CreateCreditNoteHander { get; set; }
        [JsonIgnore]
        public Action RestartSchedulerHander { get; set; }
        [JsonIgnore]
        public Action StopSchedulerHander { get; set; }
        [JsonIgnore]
        public Func<Task> PasteHander { get; set; }
        [JsonIgnore]
        public Action ValidateHandler { get; set; }
        [JsonIgnore]
        public Action SaveAllHandler { get; set; }
        [JsonIgnore]
        public Action DeleteHandler { get; set; }
        [JsonIgnore]
        public Action ResetHandler { get; set; }
        [JsonIgnore]
        public Action ResetValidationHandler { get; set; }
        [JsonIgnore]
        public Action ImportBackupHandler { get; set; }
        [JsonIgnore]
        public Action ResetPublicationHandler { get; set; }
        [JsonIgnore]
        public Action StandardShowHandler { get; set; }
        [JsonIgnore]
        public Action CustomizedShowHandler { get; set; }
        [JsonIgnore]
        public Action<GrilleExportDataType> ExportDataHandler { get; set; }
        [JsonIgnore]
        public Action<object, object> CustomHander { get; set; }
        [JsonIgnore]
        public Func<HierarchicalData, Entity, ObservableCollection<HierarchicalData>> UpdateDimensionHander { get; set; }
        [JsonIgnore]
        public Func<ProjectBrowserData, Func<ProjectBrowserData,Task>, Task> CloseProjectHander { get; set; }
        [JsonIgnore]
        public Action BeforeCloseProjectHander { get; set; }
        [JsonIgnore]
        public Action PublishedHander { get; set; }
        [JsonIgnore]
        public Action UnPublishedHander { get; set; }
        [JsonIgnore]
        public Func<string> DuplicateNameHandler { get; set; }
        [JsonIgnore]
        private bool CanRun_;
        [JsonIgnore]
        private bool CanCreateCreditNote_;
        [JsonIgnore]
        private bool CanResetValidation_;
        [JsonIgnore]
        private bool CanRunJoin_;
        [JsonIgnore]
        private bool CanRunAutoReconciliation_;
        [JsonIgnore]
        private bool CanLoad_;
        [JsonIgnore]
        private bool CanRefresh_;
        [JsonIgnore]
        private bool CanRefreshPublication_;
        [JsonIgnore]
        private bool CanRefreshReconciliation_;
        [JsonIgnore]
        private bool CanCreate_;
        [JsonIgnore]
        private bool CanPaste_;
        [JsonIgnore]
        private bool CanValidate_;
        [JsonIgnore]
        private bool CanSaveAll_;
        [JsonIgnore]
        private bool Update_;
        [JsonIgnore]
        private bool CanDelete_;
        [JsonIgnore]
        private bool CanImportBackup_;
        [JsonIgnore]
        private bool CanReset_;
        [JsonIgnore]
        private bool CanResetPublication_;
        [JsonIgnore]
        private bool CanExport_;
        [JsonIgnore]
        private bool CanPublished_;
        [JsonIgnore]
        private bool CanUnPublished_;
        [JsonIgnore]
        private bool CanRestart_; 
        [JsonIgnore]
        private bool CanStop_;
        [JsonIgnore]
        private bool CanRenderDashboard_;
        [JsonIgnore]
        private bool CanCancel_;
        [JsonIgnore]
        private bool CanSend_;
        [JsonIgnore]
        private bool CanValidateRunOutcome_;


        [JsonIgnore]
        public bool CanCreateCreditNote
        {
            get { return CanCreateCreditNote_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanCreateCreditNote_ = value;
                }
                else
                {
                    CanCreateCreditNote_ = false;
                }
                NotifyStateChanged();
            }
        }

        [JsonIgnore]
        public bool CanResetValidation
        {
            get { return CanResetValidation_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanResetValidation_ = value;
                }
                else
                {
                    CanResetValidation_ = false;
                }
                NotifyStateChanged();
            }
        }

        [JsonIgnore]
        public bool CanValidateRunOutcome
        {
            get { return CanValidateRunOutcome_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanValidateRunOutcome_ = value;
                }
                else
                {
                    CanValidateRunOutcome_ = false;
                }
                NotifyStateChanged();
            }
        }

        [JsonIgnore]
        public bool CanRun
        {
            get { return CanRun_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanRun_ = value;
                }
                else
                {
                    CanRun_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanRunJoin
        {
            get { return CanRunJoin_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanRunJoin_ = value;
                }
                else
                {
                    CanRunJoin_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanCancel
        {
            get { return CanCancel_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanCancel_ = value;
                }
                else
                {
                    CanCancel_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanSend
        {
            get { return CanSend_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanSend_ = value;
                }
                else
                {
                    CanSend_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanRunAutoReconciliation
        {
            get { return CanRunAutoReconciliation_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanRunAutoReconciliation_ = value;
                }
                else
                {
                    CanRunAutoReconciliation_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanExport
        {
            get { return CanExport_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanExport_ = value;
                }
                else
                {
                    CanExport_ = false;
                }
                NotifyStateChanged();
            }
        }

        public void NotifyState()
        {
            if (true)
            {

            }
            NotifyStateChanged();
        }

        [JsonIgnore]
        public bool CanLoad
        {
            get { return CanLoad_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanLoad_ = value;
                }
                else
                {
                    CanLoad_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanRefresh
        {
            get { return CanRefresh_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanRefresh_ = value;
                }
                else
                {
                    CanRefresh_ = value;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanRefreshReconciliation
        {
            get { return CanRefreshReconciliation_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanRefreshReconciliation_ = value;
                }
                else
                {
                    CanRefreshReconciliation_ = false;
                }
                NotifyStateChanged();
            }
        } 
        [JsonIgnore]
        public bool CanRefreshPublication
        {
            get { return CanRefreshPublication_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanRefreshPublication_ = value;
                }
                else
                {
                    CanRefreshPublication_ = false;
                }
                NotifyStateChanged();
            }
        }        
        [JsonIgnore]
        public bool CanCreate
        {
            get { return CanCreate_; }
            set
            {
                CanCreate_ = value;
                NotifyStateChanged();
            }
        }  
        [JsonIgnore]
        public bool CanPaste
        {
            get { return CanPaste_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanPaste_ = value;
                }
                else
                {
                    CanPaste_ = false;
                }
                NotifyStateChanged();
            }
        }  
        [JsonIgnore]
        public bool CanValidate
        {
            get { return CanValidate_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanValidate_ = value;
                }
                else
                {
                    CanValidate_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanSaveAll
        {
            get { return CanSaveAll_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanSaveAll_ = value;
                }
                else
                {
                    CanSaveAll_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool Update {
            get { return Update_; }
            set {
                if ((IsOpenProject || IsOtherFreeView) && value)
                {
                    Update_ = value;
                }
                else
                {
                    Update_ = false;
                }
                NotifyStateChanged();
            }
        }

        [JsonIgnore]
        public bool CanDelete {
            get { return CanDelete_; }
            set {
                if (IsOpenProject && value)
                {
                    CanDelete_ = value;
                }
                else
                {
                    CanDelete_ = false;
                }
                NotifyStateChanged();
            }
        }
        
        [JsonIgnore]
        public bool CanImportBackup
        {
            get { return CanImportBackup_; }
            set {
                if (IsOpenProject && value)
                {
                    CanImportBackup_ = value;
                }
                else
                {
                    CanImportBackup_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanReset {
            get { return CanReset_; }
            set {
                if (IsOpenProject && value)
                {
                    CanReset_ = value;
                }
                else
                {
                    CanReset_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanResetPublication
        {
            get { return CanResetPublication_; }
            set {
                if (IsOpenProject && value)
                {
                    CanResetPublication_ = value;
                }
                else
                {
                    CanResetPublication_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        private bool CanShowStandard_ { get; set; } = false;
        [JsonIgnore]
        public bool CanShowStandard
        {
            get { return CanShowStandard_; }
            set
            {
                CanShowStandard_ = value;
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        private bool CanShowCustomized_ { get; set; } = false;
        [JsonIgnore]
        public bool CanShowCustomized
        {
            get { return CanShowCustomized_; }
            set
            {
                CanShowCustomized_ = value;
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanPublished
        {
            get { return CanPublished_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanPublished_ = value;
                }
                else
                {
                    CanPublished_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanUnPublished
        {
            get { return CanUnPublished_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanUnPublished_ = value;
                }
                else
                {
                    CanUnPublished_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanRestart
        {
            get { return CanRestart_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanRestart_ = value;
                }
                else
                {
                    CanRestart_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanStop
        {
            get { return CanStop_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanStop_ = value;
                }
                else
                {
                    CanStop_ = false;
                }
                NotifyStateChanged();
            }
        }
        [JsonIgnore]
        public bool CanRenderDashboard
        {
            get { return CanRenderDashboard_; }
            set
            {
                if (IsOpenProject && value)
                {
                    CanRenderDashboard_ = value;
                }
                else
                {
                    CanRenderDashboard_ = false;
                }
                StateBodyChangedEvent();
            }
        }
        [JsonIgnore]
        private Nameable ClientBinding_ { get; set; }
        [JsonIgnore]
        private Nameable ProfilBinding_ { get; set; }
        public Nameable ClientBinding
        {
            get => ClientBinding_;
            set
            {
                bool isNew = ClientBinding_ == null && value != null || ClientBinding_ != null && value == null;
                bool isUpdate = ClientBinding_ != null && value != null && (value != ClientBinding_ || !ClientBinding_.Id.Equals(value.Id));
                bool changedClient = isNew || isUpdate;

                ClientBinding_ = value;
                if (ClientBinding_ != null)
                {
                    SetClientId(ClientBinding_.Id);
                }
                else
                {
                    SetClientId(null);
                }
                CheckProfilBindingHander(changedClient);
            }
        }
        private async void CheckProfilBindingHander(bool check) {
            if (ProfilBindingHander != null)
            {
                await ProfilBindingHander.Invoke(check);
            }
        }

        public event Func<Task> PrivilegeObserverHander;
        public event Func<bool, Task> ProfilBindingHander;
        public event Action RefreshHomePageHander;

        public Nameable ProfilBinding
        {
            get => ProfilBinding_;
            set
            {
                ProfilBinding_ = value;
                if (ProfilBinding_ != null)
                {
                    SetProfilId(ProfilBinding_.Id);
                }
                else
                {
                    SetProfilId(null);
                }
                Action acti = async () =>
                {
                    if (PrivilegeObserverHander != null)
                    {
                        Task task =  PrivilegeObserverHander.Invoke();
                        task = task.ContinueWith(t=> RefreshHomePageHander?.Invoke());
                        await task;
                    }                     
                };
                acti.Invoke();
            }
        }

        public Task  GetPrivilegeObserver()
        {
            if (PrivilegeObserverHander != null)
            {
              return   PrivilegeObserverHander.Invoke();
            }
            return Task.CompletedTask;
        }
        
        private bool CanRenderClient_ { get; set; }
        private bool CanRenderProfil_ { get; set; }

        public bool CanRenderClientBinding
        {
            get => CanRenderClient_;
            set
            {
                CanRenderClient_ = value;
                NotifyStateChanged();
            }
        }
        public bool CanRenderProfilBinding
        {
            get => CanRenderProfil_;
            set
            {
                CanRenderProfil_ = value;
                NotifyStateChanged();
            }
        }

        private ObservableCollection<Nameable> Clients_ { get; set; }
        private ObservableCollection<Nameable> Profils_ { get; set; }

        public ObservableCollection<Nameable> ClientsBinding
        {
            get => Clients_;
            set
            {
                Clients_ = value;
                NotifyStateChanged();
            }
        }
        public ObservableCollection<Nameable> ProfilsBinding
        {
            get => Profils_;
            set
            {
                Profils_ = value;
                NotifyStateChanged();
            }
        }

        #endregion

        #region operation
        public void SetParams(string _ProjectName, long? ProjectId_, string ProjectCode_)
        {
            ProjectName = _ProjectName;
            ProjectId = ProjectId_;
            ProjectCode = ProjectCode_;
            CustomHttpClientHandler.TENANT_ID = ProjectCode;
            NotifyStateChanged();
        }
        public void Clear()
        {
            ProjectName = null;
            ProjectId = null;
            ProjectCode = null;
            Update_ = false;
            Hander = null;
            AutoReconciliationRunHander = null;
            ResetValidationHandler = null;
            RunHander = null;
            CreateCreditNoteHander = null;
            RunJoinHander = null;
            ValidateRunOutcomeHander = null;
            LoadHander = null;
            PasteHander = null;
            ValidateHandler = null;
            DeleteHandler = null;
            ResetHandler = null;
            CancelHander = null;
            SendHander = null;
            ResetPublicationHandler = null;
            RefreshPublicationHandler = null;
            ActivateSchedulingFileLoaderHandler = null;
            ActivateSchedulingAutoRecoHandler = null;
            ActivateSchedulingLogAutoRecoHandler = null;
            CustomHttpClientHandler.TENANT_ID = ProjectCode;
            NotifyStateChanged();
        }
        public void SetClientId(long? Id)
        {
            ClientId = Id;
            if (ClientId.HasValue)
            {
                CustomHttpClientHandler.CLIENT_ID = ClientId.ToString();
            }
            else
            {
                CustomHttpClientHandler.CLIENT_ID = null;
            }
            NotifyStateChanged();
        }

        public void SetProfilId(long? Id)
        {
            ProfilId = Id;
            if (ProfilId.HasValue)
            {
                CustomHttpClientHandler.PROFILE_ID = ProfilId.ToString();
            }
            else
            {
                CustomHttpClientHandler.PROFILE_ID = null;
            }
            NotifyStateChanged();
        }

        public Task NavigateTo(string uri)
        {
            lastUri = uri;
            if (!string.IsNullOrWhiteSpace(uri))
            {
                navigationManager.NavigateTo(uri);
            }
            return Task.CompletedTask;
        }
        public async void GoToLastPage()
        {
            if (!string.IsNullOrWhiteSpace(lastUri))
            {
              await  NavigateTo(lastUri);
            }
        }
        public string GetCurrentUri()
        {
            return navigationManager.Uri;
        }
        public async void GoToHomePage()
        {
            await NavigateTo(Route.INDEX + ProjectId);
        }
        public void Save() 
        {
             Hander?.Invoke();
        }
        public void Run() => RunHander?.Invoke();
        public void ResetValidation_() => ResetValidationHandler?.Invoke();
        public void ValidateRunOutcome() => ValidateRunOutcomeHander?.Invoke();
        public void CreateCreditNote__() => CreateCreditNoteHander?.Invoke();
        public void RunJoin() => RunJoinHander?.Invoke();
        public void AutoReconciliationRun() => AutoReconciliationRunHander?.Invoke();
        public void Load() => LoadHander?.Invoke();
        public void Refresh() => RefreshHander?.Invoke();
        public void RefreshPublication() => RefreshPublicationHandler?.Invoke();
        public void RefreshEventReconciliation() => RefreshReconciliationHander?.Invoke();
        public void RefreshFooter() => RefreshFooterHandler?.Invoke();
        public void ActSchedulingFileLoader() => ActivateSchedulingFileLoaderHandler?.Invoke();
        public void ActSchedulingAutoReco() => ActivateSchedulingAutoRecoHandler?.Invoke();
        public void ActSchedulingAutoRecoLog() => ActivateSchedulingLogAutoRecoHandler?.Invoke();
        private async void NotifyStateChanged()
            {
            if (StateChanged != null)
            {
                await StateChanged.Invoke();
            }
        }

        public Task AddProgressBars(SocketJS socketJS)
        {
            if (ProgressBarsHander != null)
            {
                ProgressBarsHander.Invoke(socketJS);
            }
            return Task.CompletedTask;
        }
        public string DuplicateName()
        {
            if (DuplicateNameHandler != null)
            {
              return  DuplicateNameHandler.Invoke();
            }
            return null;
        }

        
        public Task EditProject(long projectId)
        {
            if (EditProjectHander != null)
            {
                EditProjectHander.Invoke(projectId);
            }
            return Task.CompletedTask;
        }
        
        public Task CreateProject()
        {
            if (CreateProjectHander != null)
            {
                CreateProjectHander.Invoke();
            }
            return Task.CompletedTask;
        }
        
        public Task ChangedStateUploadProgressBar(bool state, bool fullBase = false)
        {
            if (ChangedStateUploadProgressbarHander != null)
            {
                ChangedStateUploadProgressbarHander.Invoke(state, fullBase);
            }
            return Task.CompletedTask;
        }
        
        public Task SetParentProgressbar(long count,long currentPosition)
        {
            if (SetParentProgressbarHander != null)
            {
                SetParentProgressbarHander.Invoke(count, currentPosition);
            }
            return Task.CompletedTask;
        }
        
        public Task SetChildrenProgressbar(long count, long currentPosition)
        {
            if (SetChildrenProgressbarHander != null)
            {
                SetChildrenProgressbarHander.Invoke(count, currentPosition);
            }
            return Task.CompletedTask;
        }
        public void Create() => CreateHander?.Invoke();
        public void Restart() => RestartSchedulerHander?.Invoke();
        public void Stop() => StopSchedulerHander?.Invoke();
        public void Paste() => PasteHander?.Invoke();
        public void Validate() => ValidateHandler?.Invoke();
        public void SaveAll() => SaveAllHandler?.Invoke();
        public void Delete() => DeleteHandler?.Invoke();
        public void Reset() => ResetHandler?.Invoke();
        public void ImportBackup() => ImportBackupHandler?.Invoke();
        public void Cancel() => CancelHander?.Invoke();
        public void Send() => SendHander?.Invoke();
        public void ResetPublication() => ResetPublicationHandler?.Invoke();
        public void StandardShow() => StandardShowHandler?.Invoke();
        public void CustomizedShow() => CustomizedShowHandler?.Invoke();
        public void ExportData(GrilleExportDataType type) => ExportDataHandler?.Invoke(type);
        public Task BuildDynamicMenu()
        {
            if(MenusHander != null)
            {
                MenusHander.Invoke();
            }
            return Task.CompletedTask;
        }
        public void Published() => PublishedHander?.Invoke();
        public void UnPublished() => UnPublishedHander?.Invoke();
        public RenderFragment DashboardRender() => DashboardHander?.Invoke();

       public ObservableCollection<HierarchicalData> UpdateDimension(HierarchicalData hierarchicalData, Entity entity) => UpdateDimensionHander?.Invoke(hierarchicalData, entity);
        #endregion

        #region Project

        public async Task OpenProject(string projectName_, long? projectId_, string ProjectCode_, string url = null)
        {
            ProjectBrowserData project_ = new ProjectBrowserData()
            {
                Id = projectId_,
                Code = ProjectCode_,
                Name = projectName_
            };
            Func<ProjectBrowserData, Task> action = async (project) => {
                try
                {
                    if (project != null && project.Id.HasValue)
                    {
                        SetParams(project.Name, project.Id, project.Code);
                        if (string.IsNullOrWhiteSpace(url))
                        {
                            url = Route.INDEX + project.Id.ToString();
                        }
                        // await BuildDynamicMenu();
                        await NavigateTo(url);
                    }
                }
                catch (Exception) { }
                finally
                {
                    NotifyStateChanged();
                }
            };

            if (!string.IsNullOrWhiteSpace(ProjectCode))
            {
                CloseProjectHander?.Invoke(project_, action);
            }
            else
            {
                await action?.Invoke(project_);
            }
        }
        #endregion
        public string GetParams(string key)  { 
            var query = navigationManager.QueryString();
            if (query.Count > 0) {
                return query[key];
            }
            return null;
        }

        #region Privilege Observer
        public PrivilegeObserver PrivilegeObserver_ { get; set; }

        public PrivilegeObserver PrivilegeObserver
        {
            get => PrivilegeObserver_;
            set
            {
                PrivilegeObserver_ = value;
                MenuStateChanged?.Invoke();
            }
        }
        public bool IsProjectBrowser { get => GetCurrentUri().Contains(Route.PROJECT_BROWSER); }
        public bool IsOtherFreeView { get => HasFreePrivilege();}
        

        private bool HasFreePrivilege()
        {
            string relativeUri = GetCurrentUri();
            return relativeUri.Contains(Route.CLIENT_FORM)
                 || relativeUri.Contains(Route.BROWSER_CLIENT)
                 || relativeUri.Contains(Route.USER_FORM)
                 || relativeUri.Contains(Route.BROWSER_USER)
                 || relativeUri.Contains(Route.PROFIL_EDIT)
                 || relativeUri.Contains(Route.ABOUT)
                 || relativeUri.Contains(Route.PROFIL_LIST);
        }

        public void DesableClientAndProfile()
        {
            CanRenderClientBinding = false;
            CanRenderProfilBinding = false;
        }

        public void EnableClientAndProfile()
        {
            CanRenderClientBinding = ClientsBinding != null && ClientsBinding.Count > 0;
            CanRenderProfilBinding = ProfilsBinding != null && ProfilsBinding.Count > 0;
        }

        #endregion

        #region close Project
        [JsonIgnore]
        public UserWorkspace UserWorkspace { get; set; }
        
        public event Func<Task> LoadProjectHandler;
        public Task LoadProject()
        {
            if (LoadProjectHandler != null)
            {
                return LoadProjectHandler.Invoke();
            }
            return Task.CompletedTask;
        }

        [JsonIgnore]
        public bool Close { get; set; } = false;
        public async void CloseEventHandler()
        {
            if (CanNavigateToBrowser)
            {
                NavigateToBrowserHander();
                return;
            }
            string end1 = Route.INDEX + ProjectId;
            string end2 = Route.INDEX;
            bool IsEnd1 = GetCurrentUri().EndsWith(end1);
            bool IsEnd2 = GetCurrentUri().EndsWith(end2);
            bool IsEnd3 = GetCurrentUri().EndsWith("Form/");
            bool isHomePage = (IsEnd1 || IsEnd2);

            if (ProjectName != null && isHomePage)
            {
                await LoadProjectHandler?.Invoke();
                Close = false;
                await DisplaySelectProjectList(true, false);
                NotifyStateChanged();
            }
            else if (ProjectName != null && IsEnd3)
            {
                GoToLastPage();
            }

            else
            {
                GoToHomePage();
            }
        }
        #endregion

        [JsonIgnore]
        public AppStateStore StateStore => new AppStateStore()
        {
            lastUri = lastUri,
            ClientId = ClientId,
            CurrentUser = CurrentUser,
            ProfilId = ProfilId,
            ProjectCode = ProjectCode,
            ProjectId = ProjectId,
            ProjectName = ProjectName,
            CanRenderClientBinding = CanRenderClientBinding,
            CanRenderProfilBinding = CanRenderProfilBinding,
            ClientsBinding = ClientsBinding,
            ClientBinding = ClientBinding,
            ProfilBinding = ProfilBinding,
            ProfilsBinding = ProfilsBinding,
            PrivilegeObserver_ = PrivilegeObserver_,
        };

        

        #region culture
        [JsonIgnore]
        public CultureInfo currentCulture { get; private set; } = CultureInfo.CurrentCulture;
        public Func<bool, Task> StateMainLayoutHandler { get; set; }
        public Func<Task> StateBodyChanged { get; set; }
        public Action NavigateToBrowserHander_ { get; set; }


        public bool CanNavigateToBrowser => NavigateToBrowserHander_ != null;

        public event Action DisplayBackupModalHandler;
        public event Func<bool, bool, Task> DisplaySelectProjectListHandler; 
        public event Func<Task> InitUserWorkspaceHander;
        public event Func<Task> InitHander;

        public Task InitApp()
        {
            if (InitHander != null && PrivilegeObserver == null)
            {
                return InitHander.Invoke();
            }
            return Task.CompletedTask;
        }
        
        public Task InitUserWorkspace()
        {
            if (InitUserWorkspaceHander != null)
            {
                InitUserWorkspaceHander.Invoke();
            }
            return Task.CompletedTask;
        }
        
        public Task DisplaySelectProjectList(bool value, bool value_) {

            if(DisplaySelectProjectListHandler != null)
            {
                return DisplaySelectProjectListHandler?.Invoke(value, value_);
            }
            return Task.CompletedTask;
        }

        public void DisplayBackupModal() => DisplayBackupModalHandler?.Invoke();

        public event Action<string, bool> StartLinkInNewTabHandle;
        public event Action ResetSessionHandler;

        public void ResetSession() => ResetSessionHandler?.Invoke();
        public void NavigateToBrowserHander() => NavigateToBrowserHander_?.Invoke();

        public Task StateBodyChangedEvent()
        {
            if (StateBodyChanged != null)
            {
                StateBodyChanged.Invoke();
            }
            return Task.CompletedTask;
        }
        
        public Task MainLayoutShouldRenderTrue()
        {
            if(StateMainLayoutHandler != null)
            {
              return  StateMainLayoutHandler.Invoke(true);
            }
            return Task.CompletedTask;
        }

        public  void StartLinkInNewTab(string nextUrl, bool targetIsBcephal = true) => StartLinkInNewTabHandle?.Invoke(nextUrl, targetIsBcephal);


        public event Func<ClientStorage> GetClientStorage;
        public event Func<Task> OnChangeCulture;
        private async void NotifyChangeCulture()
        {
            if (OnChangeCulture != null)
            {
                await OnChangeCulture.Invoke();
            }
        }
       
        public void ChangeCulture(CultureInfo newCulture)
        {
            GetClientStorage().SetCookie("blazorBcephalCulture", newCulture.Name);
            currentCulture = newCulture;
            Thread thread = Thread.CurrentThread;
            CultureInfo.DefaultThreadCurrentCulture.ClearCachedData();
            CultureInfo.DefaultThreadCurrentUICulture.ClearCachedData();
            CultureInfo.CurrentCulture.ClearCachedData();
            CultureInfo.CurrentUICulture.ClearCachedData();  
            CultureInfo.DefaultThreadCurrentCulture = newCulture;
            CultureInfo.DefaultThreadCurrentUICulture = newCulture;           
            CultureInfo.CurrentCulture = newCulture;
            CultureInfo.CurrentUICulture = newCulture;
            thread.CurrentCulture.ClearCachedData();
            thread.CurrentUICulture.ClearCachedData();
            thread.CurrentCulture = newCulture;
            thread.CurrentUICulture = newCulture;
            NotifyChangeCulture();
        }
        
        public CultureInfo getCurrentCulture()
        {
            if (currentCulture == null)
            {
                ChangeCulture(CultureInfo.CurrentCulture);
            }
            return currentCulture;
        }

        #endregion
    }

    public class customStringLocalizer
    {
        private ConcurrentDictionary<string, ConcurrentDictionary<string, string>> stringMap { get; set; } = new();
        public string this[string name]
        {
            get
            {
                var cultureInfo = CultureInfo.CurrentUICulture;
                var cultureName = cultureInfo.Name;
                if (!stringMap.ContainsKey(cultureName) || stringMap[cultureName] == null)
                {
                    stringMap[cultureName] = LoadStringMap();
                }
                string value = name;
                if (stringMap[cultureName].ContainsKey(name))
                {
                    value = stringMap[cultureName][name];
                }
                return value;
            }
        }

        public string this[string name, params object[] arguments]
        {
            get
            {
                var cultureInfo = CultureInfo.CurrentUICulture;
                var cultureName = cultureInfo.Name;
                if (!stringMap.ContainsKey(cultureName) || stringMap[cultureName] == null)
                {
                    stringMap[cultureName] = LoadStringMap();
                }
                string value = name;
                if (stringMap[cultureName].ContainsKey(name))
                {
                    value = stringMap[cultureName][name];
                }
                return string.Format(value, arguments);
            }
        }

        private ConcurrentDictionary<string, string> LoadStringMap()
        {
            var cultureInfo = CultureInfo.CurrentUICulture;
            var cultureName = cultureInfo.Name;
            Assembly assembly = Assembly.GetExecutingAssembly();
            ConcurrentDictionary<string, string> dictionary = new ConcurrentDictionary<string, string>();
            Console.WriteLine(String.Format("CultureInfo : {0} ", cultureName));
            var names = $"Resources.App_{cultureName}.resources";
            var FileProvider = new EmbeddedFileProvider(assembly);
            IFileInfo fileInfo = FileProvider.GetFileInfo(names);
            if (fileInfo == null || !fileInfo.Exists)
            {
                names = $"Resources.App.resources";   
                fileInfo = FileProvider.GetFileInfo(names);
            }
            //Console.WriteLine(String.Format("stream ======::: In {0}--{1}", cultureName, cultureNameN));
           // Console.WriteLine(String.Format("Search ======::: In {0}", names));
            //Console.WriteLine(String.Format("fileInfo ======::: In : {0} ", fileInfo.Name));
            using var stream = fileInfo.CreateReadStream();
           // Console.WriteLine(String.Format("stream Is not null : {0}", stream != null));
            using (ResourceReader r = new ResourceReader(stream))
            {
                //Console.WriteLine(String.Format("ResourceReader Is not null : {0}", r != null));
                foreach (DictionaryEntry entry in r.OfType<DictionaryEntry>())
                {
                    //Console.WriteLine(String.Format("Key : {0} === value : {1}", entry.Key.ToString(), entry.Value));
                    dictionary.TryAdd((string)entry.Key, (string)entry.Value);
                }
            }
            return dictionary;
        }
    }
    public class CommonLocalizationService : DxLocalizationService
    {
        public readonly customStringLocalizer localizer;
        public CommonLocalizationService()
        {    
            localizer = new customStringLocalizer();
        }
        protected override string GetString(string key)
        {
            return localizer[key];
        }
    }

    public static class ExtensionMethods
    {
        // get entire querystring name/value collection
        public static NameValueCollection QueryString(this NavigationManager navigationManager)
        {
            return HttpUtility.ParseQueryString(new Uri(navigationManager.Uri).Query);
        }

        // get single querystring value with specified key
        public static string QueryString(this NavigationManager navigationManager, string key)
        {
            return navigationManager.QueryString()[key];
        }


        public static void AddBaseServices(this IServiceCollection service)
        {
            service.AddSingleton<SupportedCultures>();
            service.AddSingleton<ServiceExecption>();
            service.AddSingleton<UserWorkspaceService>();
            service.AddSingleton<FunctionalityService>();
            service.AddSingleton<FunctionnalityWorkspaceService>();
            service.AddSingleton<CustomHttpClientHandler>();
            service.AddSingleton<AppState>();
            service.AddSingleton<CommonLocalizationService>();
            service.AddSingleton<BillingModelPivotService>();
            service.AddSingleton<ClientStorage>();
        }

        public static void AddConfigHttpClientServices(this IServiceCollection service, WebAssemblyHost host, string address, bool IsProduction)
        {
            var jsInterop = host.Services.GetRequiredService<IJSRuntime>();
            var handler = host.Services.GetRequiredService<CustomHttpClientHandler>();
            var appState = host.Services.GetRequiredService<AppState>();
            if (IsProduction)
            {
                if (address.Contains("/bcephal/"))
                {
                    address = address.Replace("/bcephal/", "");
                }
                string protocole = "ws://";
                if (address.StartsWith("https://"))
                {
                    protocole = "wss://";
                    handler.SetHttpsConfigClientHandler();
                }
                var WebSocketAddress = new WebSocketAddress() { BaseAddress = new Uri(protocole + address.Substring(address.IndexOf("://") + 3)) };
                service.AddTransient(sp => new HttpClient(handler) { BaseAddress = new Uri(address) });
                service.AddTransient(ws => WebSocketAddress);
            }
            else
            {
                string protocoleWs = "ws://";
                string protocoleHttp = "http://";
                if (address.StartsWith("https://"))
                {
                    protocoleWs = "wss://";
                    protocoleHttp = "https://";
                    handler.SetHttpsConfigClientHandler();
                }
                var WebSocketAddress = new WebSocketAddress() { BaseAddress = new Uri($"{protocoleWs}localhost:9000") };
                var client = new HttpClient(handler)
                {
                    BaseAddress = new Uri($"{protocoleHttp}localhost:9000"),
                };

                CustomHttpClientHandler.SessionId = "d49f9828-022f-4c21-95d0-0eca25f5f0e8";
                service.AddTransient(sp => client);
                service.AddTransient(ws => WebSocketAddress);
            }
            service.AddSingleton<UserService>();
            service.AddSingleton<ProjectService>();
            service.AddSingleton<ModelService>();
            service.AddSingleton<BGroupService>();
            service.AddSingleton<IToastService, ToastService>();
            service.AddSingleton<LocalStorageService>();
            service.AddSingleton<FormModelService>();            
            service.AddSingleton<ProjectBackupService>();
            service.AddSingleton<SchedulerService>();
            service.AddSingleton<SchedulerLogService>();
            service.AddSingleton<PrivilegeObserverControl> ();
            service.AddSingleton<PeriodicExecutor>();
        }

        public async static Task SetDefaultCulture(this WebAssemblyHost host)
        {
            var slientStorage = host.Services.GetRequiredService<ClientStorage>();
            var result = await slientStorage.GetCookie("blazorBcephalCulture");
            if (string.IsNullOrWhiteSpace(result))
            {
                result = "en-US";
            }
            var culture = new CultureInfo(result);
            CultureInfo.CurrentCulture = culture;
            CultureInfo.CurrentUICulture = culture;
            CultureInfo.DefaultThreadCurrentCulture = culture;
            CultureInfo.DefaultThreadCurrentUICulture = culture;
        }
    }
}

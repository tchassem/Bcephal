using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Microsoft.AspNetCore.Components;
using Microsoft.Extensions.Localization;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Helper
{
    public partial class StorageHelper : ComponentBase
    {
        [CascadingParameter] public Error Error { get; set; }
        [Parameter] public RenderFragment ChildContent { get; set; }        
        [Inject] public IJSRuntime JSRuntime { get; set; }
        [Inject] public AppState AppState { get; set; }
        [Inject] private NavigationManager navigationManager { get; set; }
        [Inject] private CommonLocalizationService Service { get; set; }
        [Inject] private UserService UserService { get; set; }
        
        private string keySessionId = "state-Id";
        private string stateId = null;
        private bool hasLoaded = false;
        private string OpenNewTabUrlKey { get; set; } = null;
        private string OpenNewTabAppStateKey { get; set; } = null;

        
        public override Task SetParametersAsync(ParameterView parameters)
        {
            AppState.navigationManager = navigationManager;
            OpenNewTabUrlKey = AppState.GetParams("qr");
            OpenNewTabAppStateKey = AppState.GetParams("ap");
            return base.SetParametersAsync(parameters);
        }

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            if (firstRender)
            {
                AppState.Localize = Service.localizer;
                // See warning about memory above in the article
                var dotNetReference = DotNetObjectReference.Create(this);
                await JSRuntime.InvokeVoidAsync("stateManager.saveAppState_", dotNetReference, AppState["AppState.lived"]);
                await RestoreNewTab();
            }
        }

        [JSInvokable("saveAppState")]
        public async Task saveState()
        {
            await JSRuntime.InvokeVoidAsync("console.log", "Call saveState");
            JsonSerializerSettings settings = new JsonSerializerSettings()
            {
                ReferenceLoopHandling = Newtonsoft.Json.ReferenceLoopHandling.Serialize
            };
            string AppStateString_ = JsonConvert.SerializeObject(AppState, settings);
            //  await JSRuntime.InvokeVoidAsync("console.log", "SerializeObject AppState :", AppStateString_);
            await JSRuntime.InvokeAsync<string>("stateManager.save", nameof(AppState), AppStateString_);
            //  await JSRuntime.InvokeVoidAsync("console.log", "saved :", AppStateString_);
            await JSRuntime.InvokeAsync<string>("stateManager.save", keySessionId, stateId);
            //  await JSRuntime.InvokeVoidAsync("console.log", "saved keySessionId :", stateId);
        }

        public async Task Restore()
        {
            string OldStateId = await JSRuntime.InvokeAsync<string>("stateManager.load", keySessionId);
            string AppStateString_ = await JSRuntime.InvokeAsync<string>("stateManager.load", nameof(AppState));
            stateId = await UserService.GetStateId();
            if (!string.IsNullOrWhiteSpace(stateId))
            {
                await JSRuntime.InvokeVoidAsync("console.log", "loaded Old ID :", OldStateId, ", New ID", stateId);
                await JSRuntime.InvokeAsync<string>("stateManager.delete", nameof(AppState));
                if (!string.IsNullOrWhiteSpace(AppStateString_) && !string.IsNullOrWhiteSpace(OldStateId) && OldStateId.Equals(stateId))
                {
                    //await JSRuntime.InvokeVoidAsync("console.log", "AppState :", AppStateString_);
                    AppState AppStateS_ = JsonConvert.DeserializeObject<AppState>(AppStateString_);
                    //await JSRuntime.InvokeVoidAsync("console.log", "DeserializeObject AppState :", AppStateS_);
                    AppState.ClientId = AppStateS_.ClientId;
                    AppState.ProfilId = AppStateS_.ProfilId;
                    AppState.CurrentUser = AppStateS_.CurrentUser;
                    AppState.ProjectCode = AppStateS_.ProjectCode;
                    AppState.ProjectId = AppStateS_.ProjectId;
                    AppState.ProjectName = AppStateS_.ProjectName;
                    AppState.CanRenderClientBinding = AppStateS_.CanRenderClientBinding;
                    AppState.CanRenderProfilBinding = AppStateS_.CanRenderProfilBinding;
                    AppState.ClientsBinding = AppStateS_.ClientsBinding;
                    AppState.ClientBinding = AppStateS_.ClientBinding;
                    AppState.ProfilBinding = AppStateS_.ProfilBinding;
                    AppState.ProfilsBinding = AppStateS_.ProfilsBinding;
                    AppState.PrivilegeObserver_ = AppStateS_.PrivilegeObserver_;
                    AppState.SetParams(AppStateS_.ProjectName, AppStateS_.ProjectId, AppStateS_.ProjectCode);
                    AppState.GoToLastPage();
                    await JSRuntime.InvokeVoidAsync("console.log", "loaded AppState :", AppStateString_);
                    await JSRuntime.InvokeAsync<string>("stateManager.delete", nameof(AppState));
                    await JSRuntime.InvokeAsync<string>("stateManager.delete", keySessionId);
                }
                else
                {
                    await JSRuntime.InvokeVoidAsync("console.log", "AppState is null or empty");
                }
            }
        }


        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            try
            {
                await Restore();
            }
            catch (InvalidOperationException)
            {
                return;
            }
            catch (Exception e)
            {
                string message = e.Message;
                if(!string.IsNullOrWhiteSpace(message) && message.Contains("ERR_CONNECTION_REFUSED"))
                {
                    message = AppState["erro.http.connection.refused"];
                }
                Error.ProcessError(new(message,e));
            }
            hasLoaded = true;
        }

        [Inject]
        protected LocalStorageService LocalStorageService { get; set; }

        public async Task RestoreNewTab()
        {
            if (!string.IsNullOrWhiteSpace(OpenNewTabUrlKey) && !string.IsNullOrWhiteSpace(OpenNewTabAppStateKey))
            {
                string AppStateString_ = await LocalStorageService.GetFromLocalStorage(OpenNewTabAppStateKey);
                string url = await LocalStorageService.GetFromLocalStorage(OpenNewTabUrlKey);
                await LocalStorageService.DeleteLocalStorage(OpenNewTabAppStateKey);
                await LocalStorageService.DeleteLocalStorage(OpenNewTabUrlKey);
                AppState AppStateS_ = JsonConvert.DeserializeObject<AppState>(AppStateString_);
                AppState.lastUri = url;
                AppState.ClientId = AppStateS_.ClientId;
                AppState.ProfilId = AppStateS_.ProfilId;
                AppState.CurrentUser = AppStateS_.CurrentUser;
                AppState.ProjectCode = AppStateS_.ProjectCode;
                AppState.ProjectId = AppStateS_.ProjectId;
                AppState.ProjectName = AppStateS_.ProjectName;
                AppState.CanRenderClientBinding = AppStateS_.CanRenderClientBinding;
                AppState.CanRenderProfilBinding = AppStateS_.CanRenderProfilBinding;
                AppState.ClientsBinding = AppStateS_.ClientsBinding;
                AppState.ClientBinding = AppStateS_.ClientBinding;
                AppState.ProfilBinding = AppStateS_.ProfilBinding;
                AppState.ProfilsBinding = AppStateS_.ProfilsBinding;
                AppState.PrivilegeObserver_ = AppStateS_.PrivilegeObserver_;
                AppState.SetParams(AppStateS_.ProjectName, AppStateS_.ProjectId, AppStateS_.ProjectCode);
                AppState.GoToLastPage();
            }
        }
    }
}


using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Threading;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared
{
    public partial class MainLayout : LayoutComponentBase, IDisposable
    {
        [Inject] AppState _AppState { get; set; }
        [Inject] IJSRuntime JSRuntime { get; set; }
        [Inject] LocalStorageService LocalStorageService { get; set; }
        [Inject] PeriodicExecutor PeriodicExecutor { get; set; }

        [CascadingParameter]
        public Error Error { get; set; }
        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            if (!String.IsNullOrEmpty(_AppState.ProjectName))
            {
                await JSRuntime.InvokeVoidAsync("SetCurrentOpentProject", "Bcephal - " + @_AppState.ProjectName);
            }
            else
            {
                await JSRuntime.InvokeVoidAsync("SetCurrentOpentProject", "Bcephal");
            }
            ShouldRender_ = false;
            _AppState.StateChanged -= StateMainLayoutChanged__;
            //Console.WriteLine("Call OnAfterRenderAsync from MainLayout");
        }

        protected override void OnInitialized()
        {
            //Console.WriteLine("Call OnInitializedAsync from MainLayout");
            _AppState.StateChanged += StateMainLayoutChanged__;
            _AppState.StateChanged += StateToastsChanged;
            _AppState.StateBodyChanged += StateBodyChanged;
            _AppState.StateMainLayoutHandler+= SetShouldRender;
            _AppState.OnChangeCulture += StateCutureChanged;
            _AppState.GetClientStorage += GetClientStorage;
            _AppState.StartLinkInNewTabHandle += StartLinkInNewTab;
            //PeriodicExecutor.StartExecuting(this.GetType().Name);
        }

        public async Task StateMainLayoutChanged__()
        {
            //Console.WriteLine("Call StateChanged from MainLayout");
            await InvokeAsync(StateHasChanged);
        }

        public void Dispose()
        {
            //Console.WriteLine("Call OnInitializedAsync from MainLayout");
            _AppState.StateChanged -= StateMainLayoutChanged__;
            _AppState.StateChanged -= StateToastsChanged;
            _AppState.StateBodyChanged -= StateBodyChanged;
            _AppState.OnChangeCulture -= StateCutureChanged;
            _AppState.GetClientStorage -= GetClientStorage;
            _AppState.StateMainLayoutHandler -= SetShouldRender;
            _AppState.StartLinkInNewTabHandle -= StartLinkInNewTab;
        }

        [Inject] ClientStorage ClientStorage { get; set; }
        ClientStorage GetClientStorage() => ClientStorage;

        #region culture

        private RenderFormContent RenderFormContentRefToasts { get; set; }
        private RenderFormContent RenderFormContentRefBody { get; set; }
        public async Task StateToastsChanged()
        {
            await InvokeAsync(RenderFormContentRefToasts.StateHasChanged_);
        }

        public async Task StateBodyChanged()
        {
            await InvokeAsync(RenderFormContentRefBody.StateHasChanged_);
        }

        private bool ShouldRenderCulture { get; set; }

        private bool ShouldRender_ { get; set; } = true;

        public  Task SetShouldRender(bool value)
        {
            ShouldRender_ = true;
            _AppState.StateChanged -= StateMainLayoutChanged__;
            _AppState.StateChanged += StateMainLayoutChanged__;
            //Console.WriteLine("Call SetShouldRender true from MainLayout");
            return Task.CompletedTask;
        }

        protected override bool ShouldRender()
        {
            return ShouldRenderCulture || ShouldRender_;//base.ShouldRender();
        }
        string RenderFormContentRefKey { get; set; } = Guid.NewGuid().ToString();
        public async Task StateCutureChanged()
        {
            //Console.WriteLine("Call StateCutureChanged from MainLayout");
            RenderFormContentRefKey = Guid.NewGuid().ToString();
            ShouldRenderCulture = true;
             await InvokeAsync(StateHasChanged);
            ShouldRenderCulture = false;
        }


        public async void StartLinkInNewTab(string nexUrl, bool targetIsBcephal)
        {
            try
            {
                if (targetIsBcephal)
                {
                    JsonSerializerSettings settings = new JsonSerializerSettings()
                    {
                        ReferenceLoopHandling = Newtonsoft.Json.ReferenceLoopHandling.Serialize
                    };
                    var lastUri = _AppState.lastUri;
                    _AppState.lastUri = nexUrl;
                    string AppStateString_ = JsonConvert.SerializeObject(_AppState.StateStore, settings);
                    _AppState.lastUri = lastUri;
                    var urlKey = Guid.NewGuid().ToString("d");
                    var appStateKey = Guid.NewGuid().ToString("d");
                    await LocalStorageService.SetLocalStorage(appStateKey, AppStateString_);
                    await LocalStorageService.SetLocalStorage(urlKey, nexUrl);
                    var target = nexUrl + "?qr=" + urlKey + "&ap=" + appStateKey;
                    StartLink_(target);
                }
                else
                {
                    StartLink_(nexUrl);
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }


        private void StartLink_(string nexUrl)
        {
            JSRuntime.InvokeVoidAsync("open", nexUrl, "_blank");
        }

        #endregion
    }
}

using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Projects;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Routing;
using System;
using System.Linq;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.JSInterop;

namespace Bcephal.Blazor.Web.Routing
{
    public class ConventionRouter : IComponent, IHandleAfterRender, IDisposable
    {
        RenderHandle _renderHandle;
        bool _navigationInterceptionEnabled;
        string _location;

        [Inject] private NavigationManager NavigationManager { get; set; }
        [Inject] private INavigationInterception NavigationInterception { get; set; }
        [Inject] private RouteManager RouteManager { get; set; }
        [Inject] private AppState _AppState { get; set; }
        [Inject] private PrivilegeObserverControl PrivilegeObserverControl  { get; set; }
        [Inject] private ProjectService ProjectService { get; set; }
        [Inject] private IJSRuntime IJSRuntime { get; set; }

        [Parameter] public RenderFragment NotFound { get; set; }
        [Parameter] public RenderFragment<RouteData> Found { get; set; }
        [Parameter] public RenderFragment<object> AccessDenied { get; set; }

        [CascadingParameter]
        public Error Error { get; set; }

        private bool CantInit { get; set; } = false;

        //[Parameter] public RenderFragment Navigating { get; set; }

        public void addHandler()
        {
            _AppState.PrivilegeObserverHander += GetPrivilegeObserver;
            _AppState.ProfilBindingHander += GetProfils;
            _AppState.InitHander += GetClients;
            _AppState.InitUserWorkspaceHander += InitUserWorkspace;
            _AppState.ResetSessionHandler += ResetSession;
        }

        public void Attach(RenderHandle renderHandle)
        {
            _renderHandle = renderHandle;
            _location = NavigationManager.Uri;
            NavigationManager.LocationChanged += HandleLocationChanged;
            if (!CantInit)
            {
                CustomHttpClientHandler.BASE_URL = UserWorkspaceService.RestClient.BaseAddress.AbsoluteUri;
                CantInit = true;
                addHandler();
            }
        }

        public  Task SetParametersAsync(ParameterView parameters)
        {
            parameters.SetParameterProperties(this);

            if (Found == null)
            {
                throw new InvalidOperationException($"The {nameof(ConventionRouter)} component requires a value for the parameter {nameof(Found)}.");
            }

            if (NotFound == null)
            {
                throw new InvalidOperationException($"The {nameof(ConventionRouter)} component requires a value for the parameter {nameof(NotFound)}.");
            }

            RouteManager.Initialise();
            Refresh();
            return Task.CompletedTask;
        }

        public Task OnAfterRenderAsync()
        {
            if (!_navigationInterceptionEnabled)
            {
                _navigationInterceptionEnabled = true;
                return NavigationInterception.EnableNavigationInterceptionAsync();
            }
            return Task.CompletedTask;
        }

        public void Dispose()
        {
            NavigationManager.LocationChanged -= HandleLocationChanged;
            _AppState.PrivilegeObserverHander -= GetPrivilegeObserver;
            _AppState.ProfilBindingHander -= GetProfils;
            _AppState.InitHander -= GetClients;
            _AppState.InitUserWorkspaceHander -= InitUserWorkspace;
            _AppState.ResetSessionHandler -= ResetSession;
            //Console.WriteLine("Call from Dispose : {0}", _location);
        }

        private void HandleLocationChanged(object sender, LocationChangedEventArgs args)
        {
            _location = args.Location;
            //Console.WriteLine("Call from HandleLocationChanged :");
            Refresh();
        }

        private async void Refresh()
        {
            //_renderHandle.Render(Navigating);
           await _AppState.MainLayoutShouldRenderTrue();
            //Console.WriteLine("_location :" + _location);
            
                var relativeUri = NavigationManager.ToBaseRelativePath(_location);
                //Console.WriteLine("relativeUri :" + relativeUri);
                
                    var parameters = ParseQueryString(relativeUri);

                    if (relativeUri.IndexOf('?') > -1)
                    {
                        relativeUri = relativeUri.Substring(0, relativeUri.IndexOf('?'));
                    }

                    var segments = relativeUri.Trim().Split('/', StringSplitOptions.RemoveEmptyEntries);
                    var matchResult = RouteManager.Match(segments);

                if (matchResult.IsMatch)
                {
                    if (!_AppState.IsOpenProject && HasFreePrivilege(relativeUri))
                    {
                        if (!relativeUri.EndsWith(Route.HOME_PAGE))
                        {
                            string path = Route.HOME_PAGE.Substring(0, Route.HOME_PAGE.Length - 1);
                            if (!relativeUri.EndsWith(path))
                            {
                                NavigationManager.NavigateTo(Route.HOME_PAGE);
                                return;
                            }
                        }
                    }
                    foreach (var para in matchResult.MatchedRoute.UriParameters)
                    {
                        if (!parameters.ContainsKey(para.Key))
                        {
                            parameters.Add(para.Key, para.Value);
                        }
                    }
                if (PrivilegeObserverControl.HasPrivilege(relativeUri))
                    {
                        var routeData = new RouteData(matchResult.MatchedRoute.Handler, parameters);
                        _renderHandle.Render(Found(routeData));
                    }
                    else
                    {
                        if (_AppState.PrivilegeObserver == null)
                        {
                            _AppState.GoToHomePage();
                        }
                        else
                        {
                            _renderHandle.Render(AccessDenied(null));
                        }
                    }
                }
                else
                {
                    _renderHandle.Render(NotFound);
                }
            return;
        }

        private bool HasFreePrivilege(string relativeUri)
        {
            return !(relativeUri.StartsWith(Route.PROJECT_BROWSER)
                 || relativeUri.StartsWith(Route.CLIENT_FORM)
                 || relativeUri.StartsWith(Route.BROWSER_CLIENT)
                 || relativeUri.StartsWith(Route.USER_FORM)
                 || relativeUri.StartsWith(Route.BROWSER_USER)
                 || relativeUri.StartsWith(Route.PROFIL_EDIT)
                 || relativeUri.StartsWith(Route.ABOUT)
                 || relativeUri.StartsWith(Route.PROFIL_LIST));
        }

        private Dictionary<string, object> ParseQueryString(string uri)
        {
            var querystring = new Dictionary<string, object>();

            foreach (string kvp in uri.Substring(uri.IndexOf("?") + 1).Split(new[] { '&' }, StringSplitOptions.RemoveEmptyEntries))
            {
                if (kvp != "" && kvp.Contains("="))
                {
                    var pair = kvp.Split('=');
                    querystring.Add(pair[0], pair[1]);
                }
            }

            return querystring;
        }

        [Inject]
        UserWorkspaceService UserWorkspaceService { get; set; }
        private List<ProjectBrowserData> Projects_ { get; set; } = new();
        


        async Task InitApplicationWorkspace()
        {
            if (!_AppState.ClientId.HasValue || ! _AppState.ProfilId.HasValue)
            {
                _AppState.ShowLoadingStatus();
                await _AppState.InitApp();
                _AppState.HideLoadingStatus();
            }
        }

        public void ResetSession()
        {
            string baseUri = UserWorkspaceService.RestClient.BaseAddress.AbsoluteUri;
            string uri = baseUri + "rest-session";
            Action navigate = async() => await _AppState.NavigateTo(uri);
            navigate();
        }

        async Task InitUserWorkspace()
        {
            try
            {
                if(_AppState.PrivilegeObserver == null)
                {
                    await GetPrivilegeObserver();
                }
                _AppState.UserWorkspace = await UserWorkspaceService.GetUserWorkspace();
                ObservableCollection<ProjectBrowserData> ProjectsDefault = _AppState.UserWorkspace.DefaultProjects;

                if (ProjectsDefault == null || ProjectsDefault.Count == 0)
                {
                    ObservableCollection<ProjectBrowserData> ProjectsNotDefault = _AppState.UserWorkspace.AvailableProjects;
                    if (ProjectsNotDefault == null || ProjectsNotDefault.Count == 0)
                    {
                        await _AppState.CreateProject();
                    }
                    else
                    {
                        Projects_.Clear();
                        Projects_.AddRange(ProjectsNotDefault);
                    }
                }
                else
                {
                    Projects_.Clear();
                    Projects_.AddRange(ProjectsDefault);
                }

                if (Projects_ != null && Projects_.Count > 0)
                {
                    if (Projects_.Count == 1)
                    {
                        ProjectBrowserData Project = Projects_.First();
                        await _AppState.OpenProject(Project.Name, Project.Id, Project.Code);
                    }
                    else if (Projects_.Count > 1)
                    {
                        await _AppState.DisplaySelectProjectList(true, true);
                    }
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }


        [Inject]
        public ClientService ClientService { get; set; }

        private async Task GetClients()
        {
            try
            {
                Task task = ClientService.getConnectedUserClients();
                task = task.ContinueWith(t =>
                {
                    if (t.IsCompletedSuccessfully)
                    {
                        _AppState.ClientsBinding = ((Task<ObservableCollection<Nameable>>)t).Result;
                        if (_AppState.ClientsBinding.Count > 0)
                        {
                            _AppState.ClientBinding = _AppState.ClientsBinding[0];
                        }
                        else
                        {
                            _AppState.ClientBinding = null;
                        }
                        _AppState.CanRenderClientBinding = _AppState.ClientsBinding.Count > 0;
                    }
                    else
                    {
                        
                    }
                });
                await task;
            }
            catch (Exception ex)
            {
                if (ex != null && ex.Message.Contains("Internal Server Error"))
                {
                    ex = new Bcephal.Models.Exceptions.BcephalException(_AppState["unable.to.load.client"], ex.InnerException);
                }
                Error.ProcessError(ex);
                _AppState.ClientsBinding = new ObservableCollection<Nameable>();
                _AppState.ProfilsBinding = new ObservableCollection<Nameable>();
                await GetPrivilegeObserver();
            }
        }

        private async Task GetProfils(bool isNewClient)
        {
            try
            {
                Task task = ClientService.getConnectedUserProfiles();
                 task = task.ContinueWith(t =>
                {
                    _AppState.ProfilsBinding = ((Task<ObservableCollection<Nameable>>)t).Result;
                    if (_AppState.ProfilsBinding.Count > 0)
                    {
                        _AppState.ProfilBinding = _AppState.ProfilsBinding[0];
                    }
                    else
                    {
                        _AppState.ProfilBinding = null;
                    }
                    _AppState.CanRenderProfilBinding = _AppState.ProfilsBinding.Count > 0;
                });
                await task;
            }
            catch (Exception ex)
            {
                if (ex != null && ex.Message.Contains("Internal Server Error"))
                {
                    ex = new Bcephal.Models.Exceptions.BcephalException(_AppState["unable.to.load.profile"], ex.InnerException);
                }
                Error.ProcessError(ex);
                _AppState.ProfilsBinding = new ObservableCollection<Nameable>();
                await GetPrivilegeObserver();
            }
            finally
            {
                if (isNewClient)
                {
                    Task task = Task.CompletedTask;
                    if (_AppState.ProjectId.HasValue)
                    {
                        task = task.ContinueWith(t=> ProjectService.CloseProject(_AppState.ProjectCode)).Unwrap();
                        task = task.ContinueWith(t=> Task.Run(()=> {
                            _AppState.BeforeCloseProjectHander?.Invoke();
                            _AppState.Clear();
                        })).Unwrap();
                        task = task.ContinueWith(t => IJSRuntime.InvokeVoidAsync("SetCurrentOpentProject", "Bcephal").AsTask()).Unwrap();
                    }
                    if (_AppState.ClientId.HasValue && _AppState.ProfilId.HasValue && _AppState.PrivilegeObserver == null)
                    {
                        task = task.ContinueWith(t => InitUserWorkspace()).Unwrap();
                    }
                    await task;
                }
                _AppState.Refresh();
            }
        }
        private async Task GetPrivilegeObserver()
        {
            try
            {
                _AppState.PrivilegeObserver = await ClientService.getPrivilegeObservers();
            }
            catch (Exception ex)
            {
                if (ex != null && ex.Message.Contains("Internal Server Error"))
                {
                    ex = new Bcephal.Models.Exceptions.BcephalException(_AppState["unable.to.load.privilege"], ex.InnerException);
                }
                Error.ProcessError(ex);
                _AppState.PrivilegeObserver = new();
            }
        }
    }

}

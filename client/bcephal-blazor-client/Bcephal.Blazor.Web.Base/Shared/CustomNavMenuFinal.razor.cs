using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Forms;
using Bcephal.Models.Projects;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared
{
   public partial class CustomNavMenuFinal : IAsyncDisposable
    {
        [Inject] private UserService userService { get; set; }
        [Inject] private FormModelService FormModelService { get; set; }
        [Inject] public AppState AppState { get; set; }
        [CascadingParameter] public Error Error { get; set; }
        [Parameter] public bool ClosedItem { get; set; }

        #region properties
        private string CssClass { get; set; } = "";        
        private BackupProject BackupRef { get; set; }
        private ImportProject ImportRef { get; set; }
        public string Name { get; set; }
        public bool UserConnected { get; set; }
        public ObservableCollection<FormModelMenu> DynamicMenus { get; set; }
        public ObservableCollection<MenuRender> DynamicMenusRenders { get; set; }
        private CreateProject createProject { get; set; }
        private long? ProjectId { get; set; }
        private bool Loading { get; set; }
        private bool OpenOrClose { get; set; }
        private bool ShouldRenderProject_ { get; set; } = false;
        private bool ModalCreateProject_ { get; set; } = false;
        private bool ModalBackupProject_ { get; set; } = false;
        private bool PopupVisible_ { get; set; }
        private bool ModalImport_ { get; set; }

        private RenderFormContent RenderFormContentRef { get; set; }
        private RenderFormContent RenderFormContentRefProjectList { get; set; }
        private RenderFormContent RenderFormContentRefCreateProject { get; set; }
        private RenderFormContent RenderFormContentRefBackupProject { get; set; }
        private RenderFormContent RenderFormContentRefModalImport { get; set; }
        private RenderFormContent RenderFormContentRefProjectName { get; set; }
    #endregion

    #region Handler
    bool ModalCreateProject { get => ModalCreateProject_;
            set {
                ModalCreateProject_ = value;
                StateHasChangedProject();
            } 
        }
        private Task CreateProject()
        {
            ModalCreateProject = true;
            return Task.CompletedTask;
        }        
        private async Task setProjectList(bool val, bool open)
        {
            await LoadProject();
            OpenOrClose = open;
            PopupVisible = val;
        }
        private Task EditProject(long projectId)
        {
            ProjectId = projectId;
            ModalCreateProject = true;
            return Task.CompletedTask;
        }

        private async  void StateHasChangedProject()
        {
            if (ModalCreateProject_)
            {
                ShouldRenderProject_ = ModalCreateProject_;
                await InvokeAsync(RenderFormContentRefCreateProject.StateHasChanged_);
            }
            else
            {
                if (ShouldRenderProject_)
                {
                    await InvokeAsync(RenderFormContentRefCreateProject.StateHasChanged_);
                    ShouldRenderProject_ = false;
                    ProjectId = null;
                }
            }
        }
        #endregion

        bool ModalBackupProject
        {
            get => ModalBackupProject_;
            set
            {
                if (value)
                {
                    ModalBackupProject_ = value;
                    RenderFormContentRefBackupProject.StateHasChanged_();
                }
                else
                {
                    if (ModalBackupProject_)
                    {
                        RenderFormContentRefBackupProject.StateHasChanged_();
                        ModalBackupProject_ = value;
                    }
                }
            }
        }

        bool PopupVisible
        {
            get => PopupVisible_;
            set
            {
                if (value)
                {
                    PopupVisible_ = value;
                    RenderFormContentRefProjectList.StateHasChanged_();
                }
                else
                {
                    if (PopupVisible_)
                    {
                        RenderFormContentRefProjectList.StateHasChanged_();
                        PopupVisible_ = value;
                    }
                }
            }
        }

        bool ModalImport
        {
            get => ModalImport_;
            set
            {

                if (value)
                {
                    ModalImport_ = value;
                    RenderFormContentRefModalImport.StateHasChanged_();
                }
                else
                {
                    if (ModalImport_)
                    {

                        RenderFormContentRefModalImport.StateHasChanged_();
                        ModalImport_ = value;
                    }
                }
            }
        }
        

        private async Task BuildMenus()
        {
            try
            {
                DynamicMenus = await FormModelService.Menus();
                await BuildDynamicMenusRenders();
            }
            catch
            {
                DynamicMenus = new();
                DynamicMenusRenders = new();
            }
            finally
            {
                await StateChanged_();
            }
        }

        private Task BuildDynamicMenusRenders()
        {
            DynamicMenusRenders = new();
            if (DynamicMenus != null)
            {

                foreach (var menu in DynamicMenus)
                {
                    AddMenuRender(menu);
                }
            }
            return Task.CompletedTask;
        }

        private void AddMenuRender(FormModelMenu menu)
        {
            if (DynamicMenusRenders.Count() == 0 || (menu != null && string.IsNullOrWhiteSpace(menu.Parent)))
            {
                MenuRender findExistMenu = GetPerentMenu(DynamicMenusRenders, menu.Caption);
                if (findExistMenu == null)
                {
                    DynamicMenusRenders.Add(new() { Menu = menu });
                }
            }
            else
            {
                MenuRender findMenu = GetPerentMenu(DynamicMenusRenders, menu.Parent);
                if (findMenu != null)
                {
                    if (findMenu.children == null)
                    {
                        findMenu.children = new();
                    }
                    findMenu.children.Add(new() { Menu = menu });
                }
                else
                {
                    FormModelMenu SubMenuFound = DynamicMenus.Where(item => item.Caption.Equals(menu.Parent)).FirstOrDefault();
                    if (SubMenuFound != null)
                    {
                        AddMenuRender(SubMenuFound);
                        AddMenuRender(menu);
                    }
                    else
                    {
                        MenuRender findExistMenu = GetPerentMenu(DynamicMenusRenders, menu.Caption);
                        if (findExistMenu == null)
                        {
                            DynamicMenusRenders.Add(new() { Menu = menu });
                        }
                    }
                }
            }
        }
        private MenuRender GetPerentMenu(IEnumerable<MenuRender> Renders, string parent)
        {
            if (Renders != null)
            {
                foreach (var itemRender in Renders)
                {
                    MenuRender item = FindRenderMenu(itemRender, parent);
                    if (item != null)
                    {
                        return item;
                    }
                }
            }
            return null;
        }

        private MenuRender FindRenderMenu(MenuRender render, string parent)
        {
            if (render != null)
            {
                if (render.Menu.Caption.Equals(parent))
                {
                    return render;
                }
                return GetPerentMenu(render.children, parent);
            }
            return null;
        }

        public ValueTask DisposeAsync()
        {
            //Console.WriteLine("Call Dispose from CustomNavMenuFinal");
            AppState.MenusHander -= BuildMenus;
            AppState.StateChanged -= StateChanged;
            AppState.MenuStateChanged -= StateChanged_;
            AppState.LoadProjectHandler -= LoadProject;
            AppState.CloseProjectHander -= CloseProject;
            AppState.CreateProjectHander -= CreateProject;
            AppState.EditProjectHander -= EditProject;
            AppState.DisplayBackupModalHandler -= BackupProjectEvt;
            AppState.DisplaySelectProjectListHandler -= setProjectList;
            return ValueTask.CompletedTask;

        }

        

        protected override async Task OnInitializedAsync()
        {
            try
            {
                //Console.WriteLine("Call OnInitializedAsync from CustomNavMenuFinal");
                Bcephal.Models.Security.User User = await userService.GetUser();
                AppState.CurrentUser = User;
                if(User != null)
                {
                    Name = User.Name;
                    UserConnected = true;
                }
                if (!string.IsNullOrEmpty(Name))
                {
                    Name = CultureInfo.CurrentCulture.TextInfo.ToTitleCase(Name.ToLower());
                }
                AppState.MenusHander += BuildMenus;
                AppState.StateChanged += StateChanged;
                AppState.MenuStateChanged += StateChanged_;
                AppState.LoadProjectHandler += LoadProject;
                AppState.CloseProjectHander += CloseProject;
                AppState.CreateProjectHander += CreateProject;
                AppState.EditProjectHander += EditProject;
                AppState.DisplayBackupModalHandler += BackupProjectEvt;
                AppState.DisplaySelectProjectListHandler += setProjectList;
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            await base.OnInitializedAsync();
        }

        private void CreateProjectEvt()
        {
            ModalCreateProject = true;
        }

        private void BackupProjectEvt()
        {
            ModalBackupProject = true;
            RenderFormContentRefBackupProject.StateHasChanged_();
        }

        private void ImportProject()
        {
            ModalImport = true;
        }

        public void Logout()
        {
            string baseUri = userService.RestClient.BaseAddress.AbsoluteUri;
            string uri = baseUri + "signout";
            NavigateTo(uri);
        }

        public void Edit()
        {
            string baseUri = userService.RestClient.BaseAddress.AbsoluteUri;
            string uri = baseUri + "edit-user";
            AppState.StartLinkInNewTab(uri, false);
        }


        public void AboutBcephal()
        {
            NavigateTo(Route.ABOUT);
        }

        public void onchange()
        {
            ClosedItem = AppState.IsOpenProject;
        }

        public  void NavigateTo(string uri)
        {
            AppState.NavigateTo(uri);
        }

        private void GotToHomePage()
        {
            NavigateTo(Route.INDEX + AppState.ProjectId);
        }

        
        public async Task StateChanged_()
        {
            await InvokeAsync(StateHasChanged);
        }

        public async Task StateChanged()
        {
            if (RenderFormContentRef != null)
            {
                await InvokeAsync(RenderFormContentRef.StateHasChanged_);
            }
            if(RenderFormContentRefProjectList != null)
            {
                await InvokeAsync(RenderFormContentRefProjectList.StateHasChanged_);
            }
            if (RenderFormContentRefProjectName != null)
            {
                await InvokeAsync(RenderFormContentRefProjectName.StateHasChanged_);
            }            
        }

        void RefreshEvent()
        {
            try
            {
                AppState.Refresh();
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


        #region context menu to link

        

        DxContextMenu ContextMenuOpenLinkRef;

        private string currentUrl { get; set; }      

       
        private void OpenLing(MouseEventArgs args, string url)
        {
            currentUrl = url;
            ContextMenuOpenLinkRef.ShowAsync(args);
        }
        private  void OnItemClickOpenLinkOnNewTab(ContextMenuItemClickEventArgs args)
        {
            string text = args.ItemInfo.Text;
            if (text.Equals(AppState["Open"]))
            {
                NavigateTo(currentUrl);
            }
            else
           if (text.Equals(AppState["OpenOnNewTab"]))
            {
               AppState .StartLinkInNewTab(currentUrl);
            }
        }
        #endregion


        #region project close

        private List<ProjectBrowserData> Projects_ { get; set; } = new();
        private string key { get; set; } = Guid.NewGuid().ToString("d");
        private ProjectListComponent ProjectListComponent { get; set; }
        [Inject] IToastService ToastService { get; set; }
        [Inject] ProjectService ProjectService { get; set; }
        public async Task CloseEvent(bool canRedirect, ProjectBrowserData project, Func<ProjectBrowserData, Task> action)
        {
            if (AppState.CanNavigateToBrowser)
            {
                AppState.NavigateToBrowserHander();
                return;
            }
            string end1 = Route.INDEX + AppState.ProjectId;
            string end2 = Route.INDEX;
            bool IsEnd1 = AppState.GetCurrentUri().EndsWith(end1);
            bool IsEnd2 = AppState.GetCurrentUri().EndsWith(end2);
           
            bool isHomePage = (IsEnd1 || IsEnd2);

            if (AppState.ProjectName != null && isHomePage)
            {
                await CloseProject(project, action);
                if (canRedirect)
                {
                    await AppState.NavigateTo(Route.HOME_PAGE);
                }
                ToastService.ShowSuccess(AppState["ProjectSuccessfullyClosed"]);
            }else
            {
                AppState.GoToHomePage();
            }
        }

        private async Task CloseProject(ProjectBrowserData project, Func<ProjectBrowserData, Task>  action = null)
        {
            await ProjectService.CloseProject(AppState.ProjectCode);
            AppState.BeforeCloseProjectHander?.Invoke();
            AppState.Clear();
            if (action != null)
            {
                await action.Invoke(project);
            }
        }

         Task LoadProject()
        {

            if (!AppState.ClientId.HasValue)
            {
                AppState.SetClientId(AppState.UserWorkspace.DefaultClient.Id);
            }
            ObservableCollection<ProjectBrowserData> ProjectsDefault = AppState.UserWorkspace.DefaultProjects;

            if (ProjectsDefault == null || ProjectsDefault.Count == 0)
            {
                ObservableCollection<ProjectBrowserData> ProjectsNotDefault = AppState.UserWorkspace.AvailableProjects;

                if (ProjectsNotDefault.Any())
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

            if (AppState.Close == false && Projects_.Any())
            {
                int pos = Projects_.FindIndex(ProjectBrowserData => ProjectBrowserData.Id == AppState.ProjectId);
                if (pos >= 0)
                {
                    Projects_.RemoveAt(pos);
                }
            }

            return Task.CompletedTask;
        }

        #endregion
    }

    public class MenuRender
    {
        public FormModelMenu Menu;
        public List<MenuRender> children;
    }
}

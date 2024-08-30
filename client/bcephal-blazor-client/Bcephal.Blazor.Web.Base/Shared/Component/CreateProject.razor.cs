using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Utils;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class CreateProject : ComponentBase
    {
        [CascadingParameter]
        public Error Error { get; set; }
        [Inject] ProjectService ProjectService { get; set; }
        [Inject] AppState AppState_ { get; set; }
        [Inject] IToastService toastService { get; set; }
        [Inject] IJSRuntime JSRuntime { get; set; }
        [Parameter]
        public bool ModalCreateProject { get; set; }
        [Parameter]
        public EventCallback<bool> ModalCreateProjectChanged { get; set; }
        public Models.Projects.Project Project { get; set; }
        [Parameter]
        public bool Loading { get; set; }
        [Parameter]
        public long? ProjectId { get; set; }
        [Parameter]
        public EventCallback<bool> LoadingChanged { get; set; }
        public string HeaderTitle { get; set; }
        private bool nameHasFocus = false;
        DxPopup DxPopupRef;

        protected override async Task OnInitializedAsync()
        {
            if (!ProjectId.HasValue)
            {
                HeaderTitle = AppState_["Projects.Create"];
            }
            else
            {
                HeaderTitle = AppState_["Projects.Edit"];
            }
            await InitProject();
            await base.OnInitializedAsync();
        }

        private async Task InitProject()
        {
            
            if (AppState_.ClientId.HasValue)
            {
                if (!ProjectId.HasValue)
                {
                    if (Project == null)
                    {
                        Project = new Models.Projects.Project();
                    }
                    Project.Name = await ProjectService.NewProjectName(AppState_.ClientId.Value.ToString());
                }
                else
                {
                    Project = await ProjectService.getById(ProjectId.Value);
                }
            }
        }

        private string Name
        {
            get
            {
                if (Project != null)
                {
                    return Project.Name;
                }
                return null;
            }
            set
            {
                Project.Name = value;
            }
        }

        private string Description
        {
            get
            {
                if (Project != null)
                {
                    return Project.Description;
                }
                return null;
            }
            set
            {
                Project.Description = value;
            }
        }

        private bool DefaultProject
        {
            get
            {
                if (Project != null)
                {
                    return Project.DefaultProject;
                }
                return false;
            }
            set
            {
                Project.DefaultProject = value;
            }
        }

        protected async override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                ProjectService.SetAppState(AppState_);
            }
            if (!firstRender && ModalCreateProject && !nameHasFocus)
            {
                nameHasFocus = true;                
                await Task.Delay(100).ContinueWith(t => JsInterop.SetFocus(JSRuntime, "new-project-id") );                
            }
            await base.OnAfterRenderAsync(firstRender);
        }

        private async Task ChangeModal(bool modal)
        {
            ModalCreateProject = modal;
            nameHasFocus = false;
            await ModalCreateProjectChanged.InvokeAsync(ModalCreateProject);
        }

        public async Task Create()
        {
            string name = null;
            Func<string> duplicateName = () => AppState_["duplicate.project.name", name];

            try
            {
                ProjectService.ErrorMessage = "";
                bool ShowToastSuccess = true;
                Loading = true;
                object result;
                if (!Project.Id.HasValue)
                {
                    name = Project.Name;
                    AppState_.DuplicateNameHandler += duplicateName;
                    result = await ProjectService.Createproject(AppState_.ClientId.Value.ToString(), Project);
                    ShowToastSuccess = true;
                }
                else
                {
                    name = Project.Name;
                    AppState_.DuplicateNameHandler += duplicateName;
                    result = await ProjectService.updateProject(AppState_.ClientId.Value.ToString(), Project);
                    ShowToastSuccess = false;
                }
                if (result is Models.Projects.Project)
                {
                    Project = result as Models.Projects.Project;
                    Loading = false;
                    await ChangeModal(false);
                    if(ShowToastSuccess)
                    {
                        toastService.ShowSuccess(AppState_["Projects.ProjectSuccessfullyAdded"]);
                    }
                    else
                    {
                        toastService.ShowSuccess(AppState_["Projects.ProjectSuccessfullyEdited"]);
                    }
                   await AppState_.OpenProject(Project.Name, Project.Id, Project.Code);
                }
                else
                {
                    Loading = false;
                    Project.Name = "";
                    Project.Description = "";
                    Project.DefaultProject = false;
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            finally
            {

                AppState_.DuplicateNameHandler -= duplicateName;
            }
        }

       async void CloseCreateProject()
        {
            await ChangeModal(false);
            ProjectService.ErrorMessage = "";
        }
    }
}

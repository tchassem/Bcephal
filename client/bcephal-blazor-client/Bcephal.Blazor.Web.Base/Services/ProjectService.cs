using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Exceptions;
using Bcephal.Models.Projects;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class ProjectService : Service<Project, ProjectBrowserData>
    {
        public static string ErrorMessage { get; set; } = "";
        // CustomWebSocket Socket;


        public ProjectService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "projects";
            SocketResourcePath = "ws/project";
        }

        protected override async Task<bool> CheckDuplicateObject(Project item)
        {
            Project project = await getByName(item.Name);
            return project == null || !(item.Id.HasValue && project.Id.Value == item.Id.Value) ? false : true;
        }

        public async Task<Project> updateProject(string clientId, Project project)
        {
            string uri = ResourcePath + "/update?BC-CLIENT=" + clientId;
            Project result = null;
            string responseMessage;
            try
            {
                responseMessage = await ExecutePost(uri, project);
                bool ValidJson = Helpers.IsJsonValid(responseMessage);
                if (ValidJson)
                {
                    result = JsonConvert.DeserializeObject<Project>(responseMessage);
                }
                else
                {
                    ErrorMessage = responseMessage;
                }
            }
            catch (BcephalException e)
            {
                ErrorMessage = e.Message;
            }
            return result;
        }

        public async Task<Project> Createproject(string clientId, Project project)
        {
            string uri = ResourcePath + "/create?BC-CLIENT=" + clientId;
            Project result = null;
            string responseMessage;
            try
            {
                responseMessage = await ExecutePost(uri, project);
                bool ValidJson = Helpers.IsJsonValid(responseMessage);
                if (ValidJson)
                {
                    result = JsonConvert.DeserializeObject<Project>(responseMessage);
                }
                else
                {
                    ErrorMessage = responseMessage;
                }
            }
            catch (BcephalException e)
            {
                ErrorMessage = e.Message;
                FormatMessage(ErrorMessage);
            }
            return result;
        }

        public async Task<bool> DeleteProject(long? projectId)
        {
            string uri = ResourcePath + "/delete/" + projectId;
            bool responseMessage = await DeleteString(uri);
            try
            {
                bool resul = Convert.ToBoolean(responseMessage);
            }
            catch (BcephalException e)
            {
                ErrorMessage = e.Message;
                FormatMessage(ErrorMessage);
            }
            return responseMessage;
        }

        public async Task<bool> Saveprojectsworkpace(object[] itemproject, string idparam)
        {
            string uri = ResourcePath + "/save-project-blocks?BC-CLIENT=" + idparam;
            string responseMessage = await ExecutePost(uri, itemproject);
            bool resul = Convert.ToBoolean(responseMessage);
            return resul;
        }

        public async Task<string> OpenProject(string projectcode, string idparam)
        {
            string uri = ResourcePath + "/open?BC-CLIENT=" + idparam;
            string responseMessage = await ExecutePostString(uri, projectcode);
            return responseMessage;
        }

        public async Task<string> NewProjectName(string idparam)
        {
            string uri = ResourcePath + "/new-project-name?BC-CLIENT=" + idparam;
            string responseMessage = await ExecuteGet(uri);
            return responseMessage;
        }


        public async Task<List<Project>> Getprojectsworkpace(string itemproject)
        {
            string uri = ResourcePath + "/user-workspace";
            if (string.IsNullOrEmpty(itemproject))
            {
                itemproject = " 0 ";
            }
            string responseMessage = await ExecutePostString(uri, itemproject);
            List<Project> resul = JsonConvert.DeserializeObject<List<Project>>(responseMessage);
            return resul;
        }

        public async Task<List<Project>> GetProjects(bool DefaultProject, long? SubscriptionId)
        {
            string uri = ResourcePath +  $"/{SubscriptionId}/{DefaultProject}";

            string responseMessage = await ExecuteGet(uri);
            List<Project> Projects = null;
            try
            {
                Projects = JsonConvert.DeserializeObject<List<Project>>(responseMessage);

            }
            catch (Exception e)
            {
                await JSRuntime.InvokeVoidAsync("console.log", "message d'exception: ", e.Message);
            }
            return Projects;
        }

        public async Task<bool> RemoveProjectsWorkpace(long? itemproject)
        {
            string uri = ResourcePath + "/delete-project-block/" + itemproject;
            string responseMessage = await ExecuteGet(uri);
            bool resul = Convert.ToBoolean(responseMessage);
            return resul;
        }


        public async Task<Project> RenameProject(ProjectBlock project, string projectNameNew)
        {
            string uri = $"{ResourcePath + "/rename/"}{project.SubcriptionId}{"/" + project.ProjectId}";
            Project resul = null;
            try
            {
                string responseMessage = await ExecutePostString(uri, projectNameNew);
                resul = JsonConvert.DeserializeObject<Project>(responseMessage);
            }

            catch (BcephalException e)
            {
                ErrorMessage = e.Message;
                FormatMessage(ErrorMessage);
            }

            return resul;
        }

        public void SetAppState(AppState appState)
        {
            AppState = appState;
        }

        private void FormatMessage(string errorMessage)
        {
            if ("duplicate.name".Equals(ErrorMessage))
            {
                string val = AppState.DuplicateName();
                if (!string.IsNullOrEmpty(val))
                {
                    ErrorMessage = val;
                }
                else
                {
                    ErrorMessage = AppState[ErrorMessage];
                }
            }
        }

        public async Task<string> CloseProject(string ProjectCode)
        {
            string uri = ResourcePath + "/close";
            string responseMessage = await ExecutePost(uri, ProjectCode);
            return responseMessage;
        }

        public async Task<bool> ExistsByName(string projectName)
        {
            string uri = ResourcePath + $"/exists-by-name?value={projectName}";
            bool exist = JsonConvert.DeserializeObject<bool>( await ExecuteGet(uri) );
            return exist;
        }

        /*public async Task ExportProject(long ProjectId, string ProjectName, long ClientId, WebSocketAddress webSocketAddress, Action<object, object> CallbackHandler)
        {
            Socket = new CustomWebSocket(webSocketAddress, new System.Net.WebSockets.ClientWebSocket(), new System.Threading.CancellationTokenSource(), CallbackHandler);
            string uri = SocketResourcePath + "/backup-project";
            SimpleArchive _SimpleArchive = new SimpleArchive()
            {
                Description = "",
                ArchiveMaxCount = 3000,
                FileName = $"{ProjectName}.bcp",
                ProjectId = ProjectId,
                ClientId = ClientId,
                locale = UserService.DefaultLanguage
            };

            await Socket.ConnectAsync(uri);
            await Socket.SendStringAsync_(Serialize(_SimpleArchive));
            await Socket.ReceiveByteAsync();
        }*/
    }
}

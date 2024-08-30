
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Projects;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Forms;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.WebSockets;
using System.Threading;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class FeedBackImport : ComponentBase
    {
         DxPopup Popup;

        [Parameter]
        public IBrowserFile LoadedFile { get; set; }

        [Parameter]
        public EventCallback<IBrowserFile> LoadedFileChanged { get; set; }

        [Parameter]
        public List<byte[]> BytesloadedFile_ { get; set; }

        [Parameter]
        public EventCallback<List<byte[]>> BytesloadedFile_Changed { get; set; }



        private long maxFileSize = 1024 * 1024;
        private bool isUploading { get; set; } = false;
        private string loadingMessage { get; set; } = "Uploading...";

        [Parameter]
        public ImportProjectData ImportProjectData { get; set; }

        [Parameter]
        public EventCallback<ImportProjectData> ImportProjectDataChanged { get; set; }

        [Parameter]
        public EventCallback<bool> HideImportCallback { get; set; }

        [Inject]
        private WebSocketAddress WebSocketAddress { get; set; }
        private CustomWebSocket Socket { get; set; }
        [Inject]
        private ProjectService ProjectService { get; set; }

        [Inject]
        private IToastService ToastService { get; set; }
        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public bool IsLoadedFile { get; set; }


        [Parameter]
        public EventCallback<bool> IsLoadedFileChanged { get; set; }

        [CascadingParameter]
        public Error Error { get; set; }

        [Inject]
        private IJSRuntime JSRuntime { get; set; }

        private bool overwriteExistingProject
        {
            get { return ImportProjectData != null ? ImportProjectData.OverwriteExistingProject : false; }
            set
            {
                //if (ImportProjectData == null)
                //{
                //    Init();
                //}
                ImportProjectData.OverwriteExistingProject = value;
                if (value)
                {
                    ImportProjectData.RenameProjectToImport = false;
                    ImportProjectData.RenameExistingProject = false;
                }
            }
        }

        private bool renameExistingProject
        {
            get { return ImportProjectData != null ? ImportProjectData.RenameExistingProject : false; }
            set
            {
                //if (ImportProjectData == null)
                //{
                //    Init();
                //}
                ImportProjectData.RenameExistingProject = value;
                if (value)
                {
                    ImportProjectData.RenameProjectToImport = false;
                    ImportProjectData.OverwriteExistingProject = false;
                }
            }
        }

        private bool renameProjectToImport
        {
            get { return ImportProjectData != null ? ImportProjectData.RenameProjectToImport : false; }
            set
            {
                //if (ImportProjectData == null)
                //{
                //    Init();
                //}
                ImportProjectData.RenameProjectToImport = value;
                if (value)
                {
                    ImportProjectData.RenameExistingProject = false;
                    ImportProjectData.OverwriteExistingProject = false;
                }
            }
        }

        private string newProjectName
        {
            get { return ImportProjectData != null ? ImportProjectData.NewProjectName : ""; }
            set
            {
                //if (ImportProjectData == null)
                //{
                //    Init();
                //}
                ImportProjectData.NewProjectName = value;
            }
        }

        async void ClosePopup()
        {
            await Popup.CloseAsync();
            IsLoadedFile = false;
            await IsLoadedFileChanged.InvokeAsync(IsLoadedFile);
            Dispose();
        }

        public void Dispose()
        {
            LoadedFile = null;
            LoadedFileChanged.InvokeAsync(LoadedFile);
            ImportProjectData = null;
            BytesloadedFile_ = null;
            BytesloadedFile_Changed.InvokeAsync(BytesloadedFile_);
            StateHasChanged();
        }

        //protected Task Init()
        //{

        //    ImportProjectData = new ImportProjectData();
        //    return Task.CompletedTask;
        //}

        private async Task Transfert()
        {
            isUploading = true;
            AppState.ShowLoadingStatus();
            try
            {
                Socket = new CustomWebSocket(WebSocketAddress, new ClientWebSocket(), new CancellationTokenSource(), ImportHandler);
                await JSRuntime.InvokeVoidAsync("console.log", "try to loading project");
                await ProjectService.ConnectSocket(Socket, "/import-project");
                //double maxreader = 2048;
               // byte[] bytes = new byte[((long)maxreader)];
               // Stream stream = LoadedFile.OpenReadStream(maxFileSize);
                //double increment = loadedFile.Size / maxreader;
                ImportProjectData.ClientId = AppState.ClientId.Value;
                ImportProjectData.Locale = UserService.DefaultLanguage;
                await Socket.SendStringAsync_(ProjectService.Serialize(ImportProjectData));
                if(BytesloadedFile_ != null && BytesloadedFile_.Any())
                {
                    foreach (byte[] bytes in BytesloadedFile_)
                    {
                        await Socket.SendbyteAsync_(bytes);
                    }
                }
                await Socket.SendbyteAsync_(new byte[] { });
                await Socket.ReceiveAsync();
                 ClosePopup();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                ToastService.ShowError(AppState["import.project.failure"]);
                IsLoadedFile = false;
                StateHasChanged();

            }
            AppState.HideLoadingStatus();
            isUploading = false;
            StateHasChanged();

        }

        private void ImportHandler(object sender, object message_)
        {
            string message = (string)message_;
            if (!string.IsNullOrWhiteSpace(message))
            {
                ProjectBrowserData result = JsonConvert.DeserializeObject<ProjectBrowserData>(message);
                if (result != null && result.Id.HasValue)
                {
                    JSRuntime.InvokeVoidAsync("console.log", "Resulte ", result);
                    ToastService.ShowSuccess(AppState["import.project.successfuly", result.Name]);
                    StateHasChanged();
                    ClosePopup();
                    AppState.Refresh();

                }

            }
        }

    }
}

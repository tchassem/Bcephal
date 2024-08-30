using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Threading.Tasks;
using System.IO;
using Microsoft.AspNetCore.Components.Forms;
using Bcephal.Models.Projects;
using Newtonsoft.Json;
using DevExpress.Blazor;
using System.Collections.Generic;
using Bcephal.Blazor.Web.Base.Services;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class ImportProject: ComponentBase, IDisposable
    {
        DxPopup Popup;
        private IBrowserFile LoadedFile { get; set; }
        private List<byte[]> BytesloadedFile { get; set; }

        private long maxFileSizeMo = 100;
        private long DefautmaxFileSizeMo = 1024;
        private long DefautminFileSizeMo = 1;
        private long maxFileSize = 1024 * 1024;
        private int maxAllowedFiles = 1;

        private long DefautmaxFileSize = 1024 * 1024;
        private int DefautmaxAllowedFiles = 1000;

        private bool isLoading;
        private bool isUploading { get; set; } = false;
        private string loadingMessage { get; set; } = "Uploading...";

        private bool IsLoadedWithError { get; set; } = false;

        private int ColSpanMd { get; set; } = 2;

        [Inject]
       private  IJSRuntime JSRuntime { get; set; }

        [CascadingParameter]
        public Error Error { get; set; }

       [Parameter]
       public bool ModalAction { get; set; }

       [Parameter]
        public EventCallback<bool> ModalActionChanged { get; set; }
        [Inject]
        private WebSocketAddress WebSocketAddress { get; set; }
        private CustomWebSocket Socket { get; set; }
        [Inject] 
        private ProjectService ProjectService { get; set; }

        [Inject]
        private IToastService ToastService { get; set; }
        [Inject]
        private AppState AppState { get; set; }

        private ImportProjectData ImportProjectData { get; set; }

        private bool IsLoadedFile { get; set; } 
  

        protected override Task OnInitializedAsync()
        {
            Init();
            return base.OnInitializedAsync();
        }

        protected  Task Init()
        {
            DefautmaxFileSize = 1024 * 1024 * DefautmaxFileSizeMo;
            maxFileSize = 1024 * 1024 * maxFileSizeMo;
            ImportProjectData = new ImportProjectData();
            return Task.CompletedTask;
        }

        private async Task LoadFiles(InputFileChangeEventArgs e)
        {
            isLoading = true;
            if( maxAllowedFiles < 0 || maxAllowedFiles > 1000)
            {
                maxAllowedFiles = DefautmaxAllowedFiles;
            }
            if (maxFileSizeMo < 0 || maxFileSizeMo > DefautmaxFileSizeMo)
            {
                maxFileSize = DefautmaxFileSize;
            }
            else
            {
                maxFileSize = 1024 * 1024 * maxFileSizeMo;
            }
            try
            {
                LoadedFile = e.File;
                double maxreader = 2048;
                BytesloadedFile = new();
                byte[] BytesloadedFile_ = new byte[((long)maxreader)];
                Stream stream = LoadedFile.OpenReadStream(maxFileSize);
                while (maxreader > 0)
                {
                    maxreader = await stream.ReadAsync(BytesloadedFile_);
                    BytesloadedFile.Add(BytesloadedFile_);
                    BytesloadedFile_ = new byte[((long)maxreader)];
                }                
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
            isLoading = false;
            await Task.CompletedTask;
        }

        private async void ShowFeedBack()
        {
            AppState.ShowLoadingStatus();
            await Task.Delay(400);
            AppState.HideLoadingStatus();
            IsLoadedFile = true;
            Dispose();
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
                    AppState.Refresh();
                    Dispose();
                } 
            }
        }


        public void Dispose()
        {
            ModalAction = false;
            ModalActionChanged.InvokeAsync(ModalAction);
            GC.SuppressFinalize(this);
            StateHasChanged();
        }

    }
}

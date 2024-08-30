using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Utils;
using Bcephal.Models.Projects;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Net.Http;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class BackupProject : ComponentBase, IDisposable
    {
        [Inject] ProjectBackupService ProjectBackupService { get; set; }
        [Inject] private WebSocketAddress WebSocketAddress { get; set; }
        [Inject] AppState AppState { get; set; }
        [Inject] IToastService ToastService { get; set; }
        [Inject] ProjectService ProjectService { get; set; }
        [Inject] IJSRuntime JSRuntime { get; set; }
        [CascadingParameter] public Error Error { get; set; }

        [Parameter]
        public bool ModalBackupProject { get; set; }

        [Parameter]
        public EventCallback<bool> ModalBackupProjectChanged { get; set; }

        DxPopup Popup;

        private bool nameHasFocus = false;
        private bool issueOccured = false;
        private string issueMessage;
        FileStream contentF;
        SocketJS Socket { get; set; }
        //int offset = 0;

        //System.IO.MemoryStream contentF;

        //public Error Error { get; set; }

        public SimpleArchive ProjectArchive { get; set; }

        protected override Task OnInitializedAsync()
        {
            Init();
            return base.OnInitializedAsync();
        }

        protected Task Init()
        {
            var date = DateTime.Now;
            ProjectArchive = new SimpleArchive()
            {
                Name = $"{AppState.ProjectName}_{date.ToString("yyyyMMdd_HHmmss")}",
                Description = $"Project: {AppState.ProjectName}\nDate: {date.ToString("dd/MM/yyyy HH:mm:ss")}"
            };
            return Task.CompletedTask;
        }

        private string FileName
        {
            get
            {
                if (ProjectArchive != null && ProjectArchive.FileName != null)
                {
                    return ProjectArchive.FileName;
                }
                return null;
            }
            set
            {
                ProjectArchive.FileName = value;
            }
        }
        private string Name
        {
            get
            {
                if (ProjectArchive != null && ProjectArchive.Name != null)
                {
                    return ProjectArchive.Name;
                }
                return null;
            }
            set
            {
                ProjectArchive.Name = value;
            }
        }

        private string Description
        {
            get
            {
                if (ProjectArchive != null && ProjectArchive.Description != null)
                {
                    return ProjectArchive.Description;
                }
                return null;
            }
            set
            {
                ProjectArchive.Description = value;
            }
        }

        // Lorsque le serveur envoi des données, on les ajoute dans le stream
        public async void CallBackExport(object sender, object message)
        {
            if (bool.TryParse(message.ToString(), out bool isNameUsed))
            {
                // Si la socket renvoi true, dc on a trouvé un projet sur le serveur ayant le même nom on doit dc demandé confirmation de l'action à exécuter au user
                // Sinon on effectue tout simplement l'import du projet
                if (isNameUsed)
                {
                    issueOccured = true;
                    issueMessage = AppState["duplicate.simpleArchive.name", ProjectArchive.Name];
                    StateHasChanged();
                }
                else
                {
                    await CloseBackupProject();
                }
            }
        }

        public async void CreateProjectBackup()
        {
            if (ProjectArchive == null || string.IsNullOrWhiteSpace(ProjectArchive.Name))
            {
                ToastService.ShowError(AppState["export.archiveName.required"], AppState["Error"]);
                return;
            }

            AppState.ShowLoadingStatus();

            try
            {
                await JSRuntime.InvokeVoidAsync("console.log", "Attempt to export the project");

                if (Socket == null)
                {
                    Socket = new SocketJS(WebSocketAddress, CallBackExport, JSRuntime, AppState, true);
                    bool valueClose = false;
                    bool valueError = false;
                    Socket.CloseHandler += () =>
                    {
                        if (!valueClose && !valueError)
                        {
                            if (!issueOccured)
                            {
                                ToastService.ShowSuccess(AppState["Projects.SuccessfullyExported"]);
                            }
                        }
                    };

                    Socket.ErrorHandler += (errorMessage) =>
                    {
                        if (!valueError)
                        {
                            ToastService.ShowError((string)errorMessage, AppState["Error"]);
                            valueError = true;
                        }
                    };

                    Socket.SendHandler += () =>
                    {
                        // var path = Environment.GetFolderPath(Environment.SpecialFolder.UserProfile) + "/tempo.bcp";
                        var path = Path.GetTempFileName();
                        contentF = new(path, FileMode.Create);

                        Socket.FullyProgressbar.FullBase = false;
                        AppState.CanLoad = false;

                        ProjectArchive.ArchiveMaxCount = 3000;
                        ProjectArchive.Name = ProjectArchive.Name.Trim();
                        ProjectArchive.FileName = AppState.ProjectName;
                        ProjectArchive.ProjectId = AppState.ProjectId.Value;
                        ProjectArchive.ProjectCode = AppState.ProjectCode;
                        ProjectArchive.ClientId = AppState.ClientId.Value;
                        ProjectArchive.locale = UserService.DefaultLanguage;

                        SendProjectArchive();
                    };

                    await ProjectService.ConnectSocketJS(Socket, "/backup-project");
                }
                else
                {
                    SendProjectArchive();
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            finally
            {
                AppState.HideLoadingStatus();
            }
        }

        private void SendProjectArchive()
        {
            issueOccured = false;

            string data = ProjectService.Serialize(ProjectArchive);
            Socket.send(data);
        }

        //public byte[] ReadBytes(Stream inputStream)
        //{
        //    using (MemoryStream ms = new MemoryStream())
        //    {
        //        inputStream.CopyTo(ms);
        //        return ms.ToArray();
        //    }
        //}

        protected async override void OnAfterRender(bool firstRender)
        {
            if (!firstRender && ModalBackupProject && !nameHasFocus)
            {
                nameHasFocus = true;
                await Task.Delay(100).ContinueWith(t => JsInterop.SetFocus(JSRuntime, "new-backup-id"));
            }
        }

        public void Dispose()
        {
            //Init();
            StateHasChanged();
            GC.SuppressFinalize(Popup);
            GC.SuppressFinalize(this);
        }

        private async Task ChangeModal(bool modal)
        {
            ModalBackupProject = modal;
            nameHasFocus = false;
            await ModalBackupProjectChanged.InvokeAsync(ModalBackupProject);
            if (!modal)
            {
                Dispose();
            }
        }

        private async Task CloseBackupProject()
        {
            if (issueOccured)
            {
                Socket.close(null);
            }
            await ChangeModal(false);
            //ProjectService.ErrorMessage = "";
        }

    }
}

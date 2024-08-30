using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Projects;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Pages.Project
{
    public partial class ProjectBackupBrowser_ : AbstractNewGridComponent<SimpleArchive, SimpleArchive>
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="20%",  ColumnName = nameof(SimpleArchive.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Project.Code"] ,ColumnWidth="12%",  ColumnName = nameof(SimpleArchive.ProjectCode), ColumnType = typeof(string)},
                        new {CaptionName = AppState["ProjectName"] ,ColumnWidth="14%", ColumnName = nameof(SimpleArchive.FileName), ColumnType = typeof(string)},
                        new {CaptionName = AppState["User"] ,ColumnWidth="12%",  ColumnName = nameof(SimpleArchive.UserName), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Description"],ColumnWidth="25%",  ColumnName = nameof(SimpleArchive.Description), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="14%", ColumnName = nameof(SimpleArchive.CreationDateTime), ColumnType = typeof(DateTime?)},
                    };

        [Inject]
        public ProjectBackupService ProjectBackupService { get; set; }
        [Inject]
        public ProjectService ProjectService { get; set; }
        [Inject] private WebSocketAddress WebSocketAddress { get; set; }
        [Inject] IToastService ToastService { get; set; }

        protected override int ItemsCount => GridColumns.Length;
        SocketJS Socket { get; set; }
        Models.Projects.Project ImportedProject { get; set; }

        private bool importCanceled = false;
        private bool showValidationModal = false;
        protected bool ShowValidationModal {
            get => showValidationModal;
            set
            {
                showValidationModal = value;
                ValidationModalStateHasChanged();
            }
        }

        private RenderFormContent RenderFormContentRefValidation { get; set; }
        private ImportProjectData ImportProjectData { get; set; }
        private SimpleArchive SelectedArchive { get; set; }
        private BaseModalComponent ImportConfirmationModal { get; set; }

        private object GetPropertyValue(SimpleArchive item, string propName)
        {
            object value =  item.GetType().GetProperty(propName).GetValue(item, null);
            if (value != null && nameof(SimpleArchive.Description).Equals(propName))
            {
                value = new MarkupString(value.ToString().Replace("\n","<br/>"));
            }
            return value;
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            IsNavLink = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            CanRefresh = true;
            DeleteButtonVisible = true;
            DeleteAllButtonVisible = false;
            SelectionMode = DevExpress.Blazor.GridSelectionMode.Single;
            OnSelectionChangeHandler_ += SelectedItemsChanged;

        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.ProjectAllowed && AppState.PrivilegeObserver.ProjectBackupAllowed && AppState.PrivilegeObserver.ProjectBackupCreateAllowed)
            {
                AppState.CanCreate = true;
                CanCreate = true;
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        public override ValueTask DisposeAsync()
        {
            if (AppState.CanCreate)
            {
                AppState.CanCreate = false;
            }
            if (AppState.CanImportBackup)
            {
                AppState.CanImportBackup = false;
                AppState.ImportBackupHandler -= ImportSelectedArchive;
            }

            return base.DisposeAsync();
        }

        protected override void Create()
        {
            if (AppState.PrivilegeObserver != null
                 && AppState.PrivilegeObserver.ProjectAllowed
                 && AppState.PrivilegeObserver.ProjectBackupAllowed
                 && AppState.PrivilegeObserver.ProjectBackupCreateAllowed)
            {
                AppState.DisplayBackupModal();
            }
        }

        protected override object GetFieldValue(SimpleArchive item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        /// <summary>
        ///     Cette méthode sert à verifier qu'il y a des éléments sélectionnés dans la liste et que le bouton run n'est pas encore activé
        ///     et si c'est le cas, on affiche le bouton run à la toolbar
        ///     <br/><br/>
        ///     Cette méthode est attachée au OnSelectionChangeHandler_ de AbstractGridComponent et est exécuté lorsque la liste change
        /// </summary>
        /// <param name="SelectedItems"></param>
        /// <returns></returns>
        private Task SelectedItemsChanged(List<long> SelectedItems)
        {
            if (SelectedItems.Count > 0 && !AppState.CanImportBackup)
            {
                AppState.ImportBackupHandler += ImportSelectedArchive;
                AppState.CanImportBackup = true;
            }
            else if (SelectedItems.Count == 0 && AppState.CanImportBackup)
            {
                AppState.CanImportBackup = false;
                AppState.ImportBackupHandler -= ImportSelectedArchive;
            }
            return Task.CompletedTask;
        }

        // Lorsque le serveur envoi des données, on les ajoute dans le stream
        public void CallBackExport(object sender, object message)
        {
            if (bool.TryParse(message.ToString(), out bool projectExist))
            {
                // Si la socket renvoi true, dc on a trouvé un projet sur le serveur ayant le même nom on doit dc demandé confirmation de l'action à exécuter au user
                // Sinon on effectue tout simplement l'import du projet
                if (projectExist)
                {
                    ShowValidationModal = true;
                }
                else
                {
                    ContinueImport();
                }
            }
            else
            {
                // Lorsque l'import est terminé, le serveur renvoi les données du projet nouvellement créé et on essaye de le garder
                ImportedProject = JsonConvert.DeserializeObject<Models.Projects.Project>(message.ToString());
            }
        }

        private async void ImportSelectedArchive()
        {
            SelectedArchive = (SimpleArchive)SelectedDataItems.First();
            if (SelectedArchive != null)
            {
                ImportProjectData = new()
                {
                    Decision = ImportProjectDecision.OverwriteExistingProject,
                    NewProjectName = SelectedArchive.FileName + "_2",
                    Locale = UserService.DefaultLanguage,
                    ClientId = AppState.ClientId.Value,
                    ProfileId = AppState.ProfilId.Value
                };

                try
                {
                    AppState.ShowLoadingStatus();

                    await JSRuntime.InvokeVoidAsync("console.log", "Attempt to import a project dump");
                    Socket = new SocketJS(WebSocketAddress, CallBackExport, JSRuntime, AppState, true);
                    bool valueClose = false;
                    bool valueError = false;
                    Socket.CloseHandler += () =>
                    {
                        if (!valueClose && !valueError && !importCanceled)
                        {
                            if (ImportedProject !=  null)
                            {
                                ToastService.ShowSuccess(AppState["import.project.successfuly", ImportedProject.Name]);
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
                        Socket.FullyProgressbar.FullBase = false;
                        AppState.CanLoad = false;

                        string data = ProjectService.Serialize(SelectedArchive);
                        Socket.send(data);
                    };

                    await ProjectService.ConnectSocketJS(Socket, "/import-project");
                    AppState.HideLoadingStatus();
                }
                catch (Exception ex)
                {
                    Error.ProcessError(ex);
                }
            }
        }

        private void SelectOverwrite()
        {
            ImportProjectData.OverwriteExistingProject = true;
            ImportProjectData.RenameExistingProject = false;
            ImportProjectData.RenameProjectToImport = false;
        }
        private void SelectRenameExisting()
        {
            ImportProjectData.RenameExistingProject = true;
            ImportProjectData.OverwriteExistingProject = false;
            ImportProjectData.RenameProjectToImport = false;
        }
        private void SelectRenameCurrent()
        {
            ImportProjectData.RenameProjectToImport = true;
            ImportProjectData.OverwriteExistingProject = false;
            ImportProjectData.RenameExistingProject = false;
        }
        private async void OptionChanged(ImportProjectDecision decision)
        {
            ImportProjectData.Decision = decision;
            switch (decision)
            {
                case ImportProjectDecision.OverwriteExistingProject:
                    SelectOverwrite();
                    break;
                case ImportProjectDecision.RenameExistingProject:
                    SelectRenameExisting();
                    break;
                case ImportProjectDecision.RenameProjectToImport:
                    SelectRenameCurrent();
                    break;
                default:
                    break;
            }
            await InvokeAsync(RenderFormContentRefValidation.StateHasChanged_);
        }

        private async void ValidationModalStateHasChanged()
        {
            if (ShowValidationModal)
            {
                await Task.Run(RenderFormContentRefValidation.StateHasChanged_);
            }
        }

        private void ContinueImport()
        {
            string data = ProjectService.Serialize(ImportProjectData);
            Socket.send(data);
        }
        
        private void CancelImport()
        {
            importCanceled = true;
            Socket.close(null);
        }

        private void CheckAndContinueImport()
        {
            // si on choisi override, on doit remettre le nom existant
            if (ImportProjectData.OverwriteExistingProject)
            {
                ImportProjectData.NewProjectName = SelectedArchive.FileName;
            }
                        
            if(string.IsNullOrWhiteSpace(ImportProjectData.NewProjectName))
            {
                ToastService.ShowError(AppState["project.import.valid.name.empty"], AppState["Error"]);
                ImportConfirmationModal.CanClose = false;
                return;
            }
            else if ((ImportProjectData.RenameExistingProject || ImportProjectData.RenameProjectToImport)
                    && ImportProjectData.NewProjectName == SelectedArchive.FileName)
            {
                ToastService.ShowError(AppState["project.import.valid.name.issue"], AppState["Error"]);
                ImportConfirmationModal.CanClose = false;
                return;
            }
            else
            {
                ImportConfirmationModal.CanClose = true;
                ContinueImport();
            }
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override string NavLinkURI()
        {
            return "";
        }

        protected override string KeyFieldName()
        {
            return nameof(ProjectBackup.Id);
        }

        protected override object KeyFieldValue(SimpleArchive item)
        {
            return item.Id;
        }

        protected override Task OnRowInserting(SimpleArchive newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(SimpleArchive dataItem)
        {
            await ProjectBackupService.DeleteProjectBackup(dataItem.Id.Value);
        }

        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var id = ( (SimpleArchive) ids.First() ).Id.Value;
                await ProjectBackupService.DeleteProjectBackup(id);
            }
        }

        protected override Task OnRowUpdating(SimpleArchive dataItem, SimpleArchive newValues)
        {
            return Task.CompletedTask;
        }

        protected override Task<BrowserDataPage<SimpleArchive>> SearchRows(BrowserDataFilter filter)
        {
            var columnFilter = new ColumnFilter();
            // columnFilter.dimensionType = Models.Filters.DimensionType.MEASURE;
            columnFilter.Operation = "Equals";
            columnFilter.Name = "clientId";
            columnFilter.Value = AppState.ClientId + "";

            if (filter.ColumnFilters == null)
            {
                filter.ColumnFilters = columnFilter;
            }
            else
            {
                filter.ColumnFilters.Items.Add(columnFilter);
            }

            filter.ClientId = AppState.ClientId.Value ;
            return ProjectBackupService.Search(filter);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }
    }

}

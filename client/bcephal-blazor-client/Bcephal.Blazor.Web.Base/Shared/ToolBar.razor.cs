using Bcephal.Models.Projects;
using Bcephal.Blazor.Web.Base.Services;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Shared.Component;
using System.Threading;
using Bcephal.Models.Grids.Filters;

namespace Bcephal.Blazor.Web.Base.Shared
{
    public partial class ToolBar : ComponentBase, IAsyncDisposable
    {
        [CascadingParameter] public Error Error { get; set; }
        [Inject] AppState _AppState { get; set; }


        public bool ModalAction { get; set; } = false;

        public void CloseModalAction()
        {
            ModalAction = false;
            StateHasChanged();
        }

        public async Task StateChanged()
        {
           await InvokeAsync(StateHasChanged);
        }

        protected override void OnInitialized()
        {
            base.OnInitialized();
            _AppState.StateChanged += StateChanged;
        }

        
        public ValueTask DisposeAsync()
        {
            _AppState.StateChanged -= StateChanged;
            return ValueTask.CompletedTask;
        }

        void CreateCreditNoteEvent()
        {
            try
            {
                _AppState.CreateCreditNote__();
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

        void SaveEvent()
        {
            try
            {
                _AppState.Save();
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

        void ValidateRunOutcomeEvent()
        {
            try
            {
                _AppState.ValidateRunOutcome();
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

        void RunEvent()
        {
            try
            {
                _AppState.Run();
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

        void RunJoinEvent()
        {
            try
            {
                _AppState.RunJoin();
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
        
        void RestartEvent()
        {
            try
            {
                _AppState.Restart();
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

        void AutoRecoRunEvent()
        {
            try
            {
                _AppState.AutoReconciliationRun();
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

        void StandartEvent()
        {
            try
            {
                _AppState.StandardShow();
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

        void LoadEvent()
        {
            try
            {
                _AppState.Load();
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

        void ExportExcelEvent()
        {
            try
            {
                _AppState.ExportData(GrilleExportDataType.EXCEL);
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

        void ExportCsvEvent()
        {
            try
            {
                _AppState.ExportData(GrilleExportDataType.CSV);
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

        void ExportJsonEvent()
        {
            try
            {
                _AppState.ExportData(GrilleExportDataType.JSON);
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

        void RefreshEvent()
        {
            try
            {
                _AppState.Refresh();
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

        void RefreshEventReconciliation()
        {
            try
            {
                _AppState.RefreshEventReconciliation();
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
        
        void RefreshPublicationEvent()
        {
            try
            {
                _AppState.RefreshPublication();
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

        void PublishedEvent()
        {
            try
            {
                _AppState.Published();
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

        void UnPublishedEvent()
        {
            try
            {
                _AppState.UnPublished();
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

        public bool ActivateSchedulingFileLoader
        {
            get { return _AppState.ActivateSchedulingFileLoader_ || _AppState.ActivateSchedulingFileLoaderInit; }
            set
            {
                _AppState.ActivateSchedulingFileLoader_ = value;
                _AppState.ActivateSchedulingFileLoaderInit = value;
                _AppState.ActSchedulingFileLoader();
            }
        }

        public bool ActivateSchedulingAutoReco
        {
            get { return _AppState.ActivateSchedulingAutoReco_ || _AppState.ActivateSchedulingAutoRecoInit; }
            set
            {
                _AppState.ActivateSchedulingAutoReco_ = value;
                _AppState.ActivateSchedulingAutoRecoInit = value;
                _AppState.ActSchedulingAutoReco();
            }
        }

        public bool ActivateSchedulingAutoRecoLog
        {
            get { return _AppState.ActivateSchedulingLogAutoReco_ || _AppState.ActivateSchedulingLogAutoRecoInit; }
            set
            {
                _AppState.ActivateSchedulingLogAutoReco_ = value;
                _AppState.ActivateSchedulingLogAutoRecoInit = value;
                _AppState.ActSchedulingAutoRecoLog();
            }
        }

        void CreateEvent()
        {
            try
            {
                _AppState.Create();
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

        void StopEvent()
        {
            try
            {
                _AppState.Stop();
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

        void PasteEvent()
        {
            try
            {
                _AppState.Paste();
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

        void ValidateEvent()
        {
            try
            {
                _AppState.Validate();
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

        void SaveAllEvent()
        {
            try
            {
                _AppState.SaveAll();
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

        void ResetValidationEvent()
        {
            try
            {
                _AppState.ResetValidation_();
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

        void ResetEvent()
        {
            try
            {
                _AppState.Reset();
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
        void CancelEvent()
        {
            try
            {
                _AppState.Cancel();
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
        void SendEvent()
        {
            try
            {
                _AppState.Send();
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
        
        void ResetPublicationEvent()
        {
            try
            {
                _AppState.ResetPublication();
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

        void DeleteEvent()
        {
            try
            {
                _AppState.Delete();
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
        
        void ImportBackupEvent()
        {
            try
            {
                _AppState.ImportBackup();
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

        //private UserWorkspace UserWorkspace { get; set; }

        //private List<ProjectBrowserData> Projects_ { get; set; } = new();

        //async void LoadProject()
        //{
        //    UserWorkspace = await UserWorkspaceService.GetUserWorkspace();

        //    if (!_AppState.ClientId.HasValue)
        //    {
        //        _AppState.SetClientId(UserWorkspace.DefaultClient.Id);
        //    }
        //    ObservableCollection<ProjectBrowserData> ProjectsDefault = UserWorkspace.DefaultProjects;

        //    if (ProjectsDefault == null || ProjectsDefault.Count == 0)
        //    {
        //        ObservableCollection<ProjectBrowserData> ProjectsNotDefault = UserWorkspace.AvailableProjects;

        //        if (ProjectsNotDefault.Any())
        //        {
        //            Projects_.Clear();
        //            Projects_.AddRange(ProjectsNotDefault);
        //        }
        //    }
        //    else
        //    {
        //        Projects_.Clear();
        //        Projects_.AddRange(ProjectsDefault);
        //    }

        //    if (Close == false && Projects_.Any())
        //    {
        //        int pos = Projects_.FindIndex(ProjectBrowserData => ProjectBrowserData.Id == _AppState.ProjectId);
        //        if (pos >= 0)
        //        {
        //            Projects_.RemoveAt(pos);
        //        }
        //    }
        //}
    }
}

using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Loaders;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using Bcephal.Models.Grids;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Base;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Reconciliation;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.MultipleFileLoader
{
    public partial class FileLoaderForm : Form<FileLoader, FileLoaderBrowserData>
    {
        
        [Inject] public FileLoaderService fileLoaderService { get; set; }

        [Inject] public GrilleService GrilleService { get; set; }

        [Inject] SchedulerService SchedulerService { get; set; }
        private int ActiveIndex_ { get; set; } = 0;
        public bool HeaderIsReadOnly { get; set; } = false;
        public override string LeftTitle { get { return AppState["FileLoader"]; } }
        public override string LeftTitleIcon { get { return "bi-cloud-arrow-up"; } }
        
        private bool Active
        {
            get  { return EditorData.Item.Active; }
            set
            {
                EditorData.Item.Active = value;
                AppState.Update = true;
            }
        }

        protected override string DuplicateName()
        {
            return AppState["duplicate.file.loader.name", EditorData.Item.Name];
        }

        public ObservableCollection<GrilleColumn> Columns_ { get; set; } = new ObservableCollection<GrilleColumn>();

        public BrowserDataPage<BrowserData> Page { get; set; }

        protected override FileLoaderService GetService()
        {
            return fileLoaderService;
        }

        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        public override string GetBrowserUrl { get => Route.LIST_FILES_LOADER; set => base.GetBrowserUrl = value; }
        protected override async Task initComponent()
        {
            try
            {
                if (EditorData == null)
                {
                    AppState.ShowLoadingStatus();
                    if (EditorData == null)
                    {
                        EditorDataFilter filter = getEditorDataFilter();
                        filter.NewData = true;
                        if (Id.HasValue)
                        {
                            filter.NewData = false;
                            filter.Id = Id;
                            
                        }
                        EditorData<FileLoader> EditorData_ = await GetService().GetEditorData(filter);
                        bool any = EditorData_ != null && EditorData_.Item != null && EditorData_.Item.ColumnListChangeHandler.GetItems().Any();
                        
                        if (any && !filter.NewData)
                        {
                            HeaderIsReadOnly = true;
                        }
                        EditorDataBinding = EditorData_;
                        AppState.Update = false;
                        initModelParams();
                    }                    
                    BrowserDataFilter PageFilter = new BrowserDataFilter();
                    Page = await GrilleService.Search<BrowserData>(PageFilter);
                    AppState.Update = true;                    
                    if (EditorData.Item.TargetId.HasValue)
                    {
                        LoadTarget(EditorData.Item.TargetId.Value);
                    }
                    AppState.HideLoadingStatus();
                    //StateHasChanged();
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                //StateHasChanged();
            }
        }

        protected override Task BeforeSave(EditorData<FileLoader> EditorData)
        {
            canStop = EditorData.Item.CanStop;
            canModifier = EditorData.Item.Modified;
            canRestart = EditorData.Item.CanRestart;
            return Task.CompletedTask;
        }

        bool canStop = false;
        bool canModifier = false;
        bool canRestart = false;
        protected async override void AfterSave(EditorData<FileLoader> EditorData)
        {
            if (canModifier)
            {
                if (canRestart)
                {
                    await SchedulerService.restart(AppState.ProjectCode, SchedulerType.FILELOADER, new() { EditorData.Item.Id.Value });
                }else
                    if (canStop)
                {
                    await SchedulerService.stop(AppState.ProjectCode, SchedulerType.FILELOADER, new() { EditorData.Item.Id.Value });
                }
                EditorData.Item.Init();
            }
        }

        protected override void save()
        {
            base.save();
            InvokeAsync(() => {
                AppState.Update = true;
            });            
        }
        private async void LoadTarget(long? id)
        {
            if (id.HasValue)
            {
                ObservableCollection<GrilleColumn>  ColumnsNew = await GrilleService.GetColumns(id.Value);
                Columns_.Clear();
                foreach (var item in ColumnsNew)
                {
                    Columns_.Add(item);
                }
            }
            else
            {
                Columns_.Clear();
            }
        }

        public bool Editable
        {
            get
            {
                var first = AppState.PrivilegeObserver.CanCreatedSourcingFileLoader;
                var second = AppState.PrivilegeObserver.CanEditSourcingFileLoader(EditorData.Item);
                return first || second;
            }
        }
    }
}

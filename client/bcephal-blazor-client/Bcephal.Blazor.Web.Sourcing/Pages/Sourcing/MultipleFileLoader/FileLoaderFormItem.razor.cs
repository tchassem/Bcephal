using Bcephal.Models.Base;
using Bcephal.Models.Loaders;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;
using Microsoft.JSInterop;
using Bcephal.Models.Grids;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Grids.Filters;
using System.IO;
using Newtonsoft.Json;
using Bcephal.Models.socket;
using System.Threading;
using System.Net.WebSockets;
using Microsoft.AspNetCore.Components.Forms;
using System.Linq;
using Bcephal.Blazor.Web.Base.Shared;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.MultipleFileLoader
{
    public partial class FileLoaderFormItem : ComponentBase, IAsyncDisposable
    {
        [CascadingParameter] public Error Error { get; set; }
        [Inject] public FileLoaderService fileLoaderService { get; set; }
        [Inject] private IJSRuntime JSRuntime { get; set; }
        [Inject] public GrilleService GrilleService { get; set; }
        [Inject] public AppState AppState { get; set; }
        [Inject] public IToastService toastService { get; set; }
        [Inject] private WebSocketAddress WebSocketAddress { get; set; }
        [Parameter] public int ActiveIndex { get; set; } = 0;
        [Parameter] public EventCallback<int> ActiveIndexChanged { get; set; }        
        [Parameter] public bool HeaderIsReadOnly { get; set; } = false;
        [Parameter] public ObservableCollection<HierarchicalData> Entities { get; set; }
        [Parameter] public EditorData<FileLoader> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<FileLoader>> EditorDataChanged { get; set; }
        [Parameter] public RenderFragment  Config { get; set; }
        [Parameter] public EventCallback<RenderFragment> ConfigChanged { get; set; }
        [Parameter] public Action<long?> LoadTargetHandler { get; set; }
        [Parameter] public BrowserDataPage<BrowserData> Page { get; set; }
        [Parameter] public ObservableCollection<GrilleColumn> Columns_ { get; set; } = new ObservableCollection<GrilleColumn>();

        ObservableCollection<FileLoaderColumn> FileLoaderColumns { get; set; } = new ObservableCollection<FileLoaderColumn>();
        [Parameter] public bool Editable { get; set; } = true;
        private string TemplateFileName { get; set; } = "";
        RenderFormContent RenderFormContentRef { get; set; }
        public int ActiveIndex_
        {
            get => ActiveIndex;
            set
            {
                ActiveIndex = value;
                ActiveIndexChanged.InvokeAsync(ActiveIndex);
            }
        }
        private List<IBrowserFile> templateFiles { get; set; } = new List<IBrowserFile>();
        public FileType FileType { get; set; }
        public List<IBrowserFile> LoadedFiles { get; set; }
        public bool IsLoaderTemplate { get; set; } = false;
        public int LoadTemplateCount { get; set; } = 0;

        bool ShowNewDimension { get; set; } = false;
        

        protected EditorData<FileLoader> EditorDataBinding
        {
            get { return EditorData; }
            set
            {
                EditorData = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }
       

        protected RenderFragment ConfigBinding
        {
            get { return Config; }
            set
            {
                Config = value;
                ConfigChanged.InvokeAsync(Config);
            }
        }



        protected override Task OnAfterRenderAsync(bool firstRender)
        {            
            if (firstRender)
            {
                AppState.LoadHander += LoadBySocketFile;
            }
            AppState.CanLoad = true;
            return base.OnAfterRenderAsync(firstRender);
        }

        public async  Task BrowserFileUpdate(List<IBrowserFile> loadedFiles_, string repository)
        {
            templateFiles = loadedFiles_;
            Repository = repository;
            if (!string.IsNullOrWhiteSpace(repository))
            {
                RepositoryFullPath = $"{Repository}/{loadedFiles_.FirstOrDefault().Name}";
            }
            else
            {
                RepositoryFullPath = repository;
            }
           await InvokeAsync(StateHasChanged);
        }

        public  void LoadBySocketFile()
        {
             FileLoaderColumns = EditorData.Item.ColumnListChangeHandler.Items.Where(x => x.DimensionId.HasValue == false).ToObservableCollection();
            if (FileLoaderColumns.Any())
            {
                ShowNewDimension = true;
                StateHasChanged();
            }
            else
            {
                LoadBySocketFile_();
            }
        }

        private void OkHandler()
        {
            ShowNewDimension = false;
            StateHasChanged();
            LoadBySocketFile_();
        }

        private void CancelHandler()
        {
            ShowNewDimension = false;
            StateHasChanged();
        }

        public async void LoadBySocketFile_()
        {
            try
            {
                FileLoaderRunData FileLoaderRunData = new FileLoaderRunData();              
                if (LoadedFiles == null)
                {
                    LoadedFiles = templateFiles;
                }
                if (LoadedFiles != null && LoadedFiles.Count > 0)
                {
                    AppState.ShowLoadingStatus();
                    if (LoadedFiles.Count() == 1)
                    {
                        FileLoaderRunData.Files.Add(LoadedFiles.Last().Name);
                        FileLoaderRunData.Repository = Repository;
                    }
                    else
                    {
                        foreach(var file in LoadedFiles)
                        {
                            FileLoaderRunData.Files.Add(file.Name);
                        }
                        FileLoaderRunData.Repository = Repository;
                    }
                    try
                    {
                        SocketJS Socket2 = new SocketJS(WebSocketAddress, null, JSRuntime, AppState, true);
                        if (LoadedFiles != null && LoadedFiles.Count > 0 && FileLoaderRunData.Files.Count == LoadedFiles.Count)
                        {
                            FileLoaderRunData.Id = EditorData.Item.Id;
                            bool valueClose = false;
                            bool valueError = false;
                            Socket2.CloseHandler += () =>
                            {
                                if (!valueClose && !valueError)
                                {
                                    AppState.HideLoadingStatus();
                                    toastService.ShowSuccess(AppState["LoadedSuccess.files.message"], AppState["Loader"]);
                                    valueClose = true;
                                    if (IsLoaderTemplate)
                                    {
                                        LoadTemplateCount++;
                                        StateHasChanged();
                                    }
                                }
                            };

                            Socket2.ErrorHandler += (errorMessage) =>
                            {
                                if (!valueError)
                                {
                                    AppState.HideLoadingStatus();
                                    toastService.ShowError((string)errorMessage, AppState["Error"]);
                                    valueError = true;
                                }
                            };
                            Socket2.SendHandler += () =>
                            {
                                AppState.HideLoadingStatus();
                                string data = fileLoaderService.Serialize(FileLoaderRunData);
                                Socket2.send(data);
                            };
                            await fileLoaderService.ConnectSocketJS(Socket2, "/load", true);
                        }
                        else
                        {
                            toastService.ShowWarning(AppState["empty.input.files"], AppState["warning"]);
                        }
                    }
                    catch (Exception ex)
                    {
                        Error.ProcessError(ex);
                    }
                    finally
                    {
                        LoadedFiles = null;
                        StateHasChanged();
                    }
                }
                else
                {
                    toastService.ShowWarning(AppState["empty.input.files"], AppState["warning"]);
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }
        public ValueTask DisposeAsync()
        {
            AppState.Update = false;
            AppState.CanLoad = false;
            AppState.LoadHander -= LoadBySocketFile;
            return ValueTask.CompletedTask;
        }

        private string Repository { get; set; }

        private string RepositoryFullPath { get; set; }
    }
}

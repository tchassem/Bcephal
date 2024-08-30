using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Loaders;
using Bcephal.Models.Sheets;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Forms;
using Newtonsoft.Json;
using NPOI.SS.UserModel;
using NPOI.XSSF.UserModel;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.MultipleFileLoader
{
    public partial class FileLoaderConfigComponent : ComponentBase,IAsyncDisposable
    {
        [Inject] public AppState AppState { get; set; }
        [CascadingParameter] public Error Error { get; set; }
        [Parameter] public EditorData<FileLoader> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<FileLoader>> EditorDataChanged { get; set; }
        [Parameter] public BrowserDataPage<BrowserData> Page { get; set; }
        [Inject] public FileLoaderService fileloaderService { get; set; }        
        [Parameter] public string TemplateFileName { get; set; } = "";
        [Parameter] public EventCallback<string> TemplateFileNameChanged { get; set; }
        [Parameter] public RenderFragment Config { get; set; }
        [Parameter] public EventCallback<RenderFragment> ConfigChanged { get; set; }
        public List<IBrowserFile> loadedFiles { get; set; } = new();
        [Parameter] public Func<List<IBrowserFile>, string, Task> BrowserFileChanged { get; set; }
        [Parameter] public Action<long?> TargetIdHandler { get; set; }
        [Parameter] public Action LoadHandler { get; set; }
        [Parameter] public int LoadTemplateCount { get; set; } = 0;
        [Parameter] public bool HeaderIsReadOnly { get; set; } = false;
        [Parameter] public bool IsLoaderTemplate { get; set; } = false;
        [Parameter] public EventCallback<bool> IsLoaderTemplateChanged { get; set; }        
        [Parameter] public bool Editable { get; set; } = true;


        List<string> InputfileExtension = new List<String>(){
            ".xlsx",
            ".xls"
        };

        public bool IsSmallScreen { get; set; }

        public ObservableCollection<SheetData> SheetFiles = new ObservableCollection<SheetData>();

        public ObservableCollection<SheetData> SheetFiles_
        {
            get
            {
                return SheetFiles;
            }
            set
            {
                SheetFiles = value;
            }
        }
        private SheetData SelectedExcelFile_ { get; set; }

        public SheetData SelectedExcelFile
        {
            get
            {
                return SelectedExcelFile_;
            }
            set
            {
                SelectedExcelFile_ = value;
                if (SelectedExcelFile_ != null)
                {
                    EditorData.Item.SheetName = SelectedExcelFile_.Name;
                    EditorData.Item.SheetIndex = SelectedExcelFile_.Index;
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        public string TemplateFileName_
        {
            get
            {
                return TemplateFileName;
            }
            set
            {
                TemplateFileName = value;
                TemplateFileNameChanged.InvokeAsync(TemplateFileName);
            }
        }

        private string FileExtension
        {
            get { return EditorData.Item.FileExtension; }
            set
            {
                EditorData.Item.FileExtension = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        IEnumerable<string> fileExtension = new List<String>(){
            "All(.*)",
            "CSV(.csv)",
            "EXCEL(.xslx)",
            "TEXT(.txt)",
        };

        private bool HasHeader
        {
            get { return EditorData.Item.HasHeader; }
            set
            {
                EditorData.Item.HasHeader = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        private string FileSeparator
        {
            get { return EditorData.Item.FileSeparator; }
            set
            {
                EditorData.Item.FileSeparator = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        private IEnumerable<FileLoaderMethod> FileLoaderMethods
        {
            get { return new List<FileLoaderMethod>() { EditorData.Item.FileLoaderMethod }; }
        }

        private bool CanUploadMethod => (loadedFiles.Any() || EditorData.Item.IsPersistent);


        private bool CanDisplayItem => EditorData.Item.FileLoaderMethod != null;

        private string UploadMethod
        {
            get {
                
                if (EditorData.Item.FileLoaderMethod == null)
                {
                    EditorData.Item.TargetId = null;
                    return null;
                }
                if (FileLoaderMethod.NEW_GRID.Equals(EditorData.Item.FileLoaderMethod))
                {
                    EditorData.Item.TargetId = null;
                }
                return EditorData.Item.FileLoaderMethod.GetText(x => AppState[x]);
            }
            set
            {
                
                EditorData.Item.FileLoaderMethod = FileLoaderMethod.NEW_GRID.GetFileLoaderMethod(value, x => AppState[x]);
                if (FileLoaderMethod.NEW_GRID.Equals(EditorData.Item.FileLoaderMethod))
                {
                    EditorData.Item.TargetId = null;
                }
                EditorData.Item.UploadMethod = EditorData.Item.FileLoaderMethod != null ? EditorData.Item.FileLoaderMethod.getCode() : null;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        private int HeaderRowCount
        {
            get { return EditorData.Item.HeaderRowCount; }
            set
            {
                EditorData.Item.HeaderRowCount = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        private bool IndentifySheetByPosition
        {
            get { return EditorData.Item.IndentifySheetByPosition; }
            set
            {
                EditorData.Item.IndentifySheetByPosition = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        private bool LoadAllSheets
        {
            get { return EditorData.Item.LoadAllSheets; }
            set
            {
                EditorData.Item.LoadAllSheets = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        private IEnumerable<BrowserData> PageItems
        {
            get {
                return ( Page != null ? Page.Items : new ObservableCollection<BrowserData>() ).Prepend(null);
            }
        }

        private BrowserData currentUploadMethod_;

        private BrowserData currentUploadMethod
        {
            get
            {
                if (currentUploadMethod_ != null)
                {
                    return currentUploadMethod_;
                }
                if (!EditorData.Item.TargetId.HasValue)
                {
                    return currentUploadMethod_;
                }
                if (PageItems.Count() > 0)
                {
                    foreach (var item in PageItems)
                    {
                        if (item != null && item.Id == EditorData.Item.TargetId.Value)
                        {
                            currentUploadMethod_ = item;
                            break;
                        }
                    }
                }
                return currentUploadMethod_;
            }

            set
            {
                currentUploadMethod_ = value;
                if (value != null)
                {
                    EditorData.Item.TargetId = value.Id;
                    EditorDataChanged.InvokeAsync(EditorData);
                    AppState.Update = true;
                    TargetIdHandler?.Invoke(value.Id);
                }
            }
        }


        private Dictionary<string, List<string>> FileNameConditionSelectedInputFile
        {
            get
            {
                Dictionary<string, List<string>> dico = new();
                var items = EditorData.Item.ConditionListChangeHandler.GetItems();
                foreach (var item in items)
                {
                    if (item != null)
                    {
                        if (!dico.ContainsKey(item.FileNameCondition.code))
                        {
                            List<string> items_ = new();
                            items_.Add(item.Filter);
                            dico.Add(item.FileNameCondition.code, items_);
                        }
                        else
                        {
                            List<string> items_ = new();
                            dico.TryGetValue(item.FileNameCondition.code, out items_);
                            items_.Add(item.Filter);
                            dico.Remove(item.FileNameCondition.code);
                            dico.TryAdd(item.FileNameCondition.code, items_);
                        }
                    }
                }
                return dico;
            }
            set
            {

            }
        }
      public async Task UpdateFileNames(List<IBrowserFile> loadedFiles_)
        {
            try
            {
                loadedFiles = loadedFiles_;
                string repository = null;
                ObservableCollection<Dictionary<string, string>> repositories = null;
                await BrowserFileChanged.Invoke(loadedFiles, repository);
                if (loadedFiles != null && loadedFiles.Any())
                {
                    repositories = await loadedFiles.UplaodAll(fileloaderService, AppState);
                    repository = repositories[0][loadedFiles.FirstOrDefault().Name];
                    if (SheetFiles != null)
                    {
                        SheetFiles.Clear();
                    }
                    if (loadedFiles != null && loadedFiles.Any())
                    {
                        FileExtension = RetrieveFileNameExtension(loadedFiles.FirstOrDefault().Name);
                        if (IsExcelFile(FileExtension))
                        {

                            SpreadSheetData spreadSheetData = await fileloaderService.GetSpreadSheetData($"{repository}/{loadedFiles.FirstOrDefault().Name}");
                            int nbsheet = spreadSheetData.SheetDatas.Count;
                            if (spreadSheetData != null && spreadSheetData.SheetDatas.Any())
                            {
                                ObservableCollection<SheetData> sheetDatas = spreadSheetData.SheetDatas;
                                for (int i = 0; i < nbsheet; i++)
                                {
                                    SheetData sheedData = sheetDatas.ElementAt(i);
                                    SheetFiles_.Add(sheedData);
                                }
                                SelectedExcelFile = SheetFiles_.FirstOrDefault();
                            }
                        }
                    }
                    await BrowserFileChanged.Invoke(loadedFiles, repository);
                    StateHasChanged();
                }
            }
            catch (Exception e)
            {
                Error.ProcessError(e);
                StateHasChanged();
            }
        }

        protected JsonSerializerSettings getJsonSerializerSettings()
        {
            return new JsonSerializerSettings()
            {
                ReferenceLoopHandling = ReferenceLoopHandling.Ignore,
            };
        }
        private string RetrieveFileNameExtension(string filename)
        {
            string extension = "";
            if (!string.IsNullOrEmpty(filename))
            {
                if (filename.Contains(".") && filename.IndexOf(".") < filename.Length)
                {
                    string[] Files = filename.Split(".");
                    extension = "." + Files.LastOrDefault();
                    EditorData.Item.Name = Files.FirstOrDefault();
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
            return extension;
        }
        protected override void OnInitialized()
        {
            base.OnInitialized();
             if (SheetFiles_ != null && SheetFiles_.Any())
            {
                SelectedExcelFile = SheetFiles_.FirstOrDefault();
            }
        }

        private bool IsExcelFile(string fileExtension)
        {

            return !string.IsNullOrWhiteSpace(fileExtension) && (fileExtension.EndsWith(".xls") || fileExtension.EndsWith(".xlsx"));
        }

        private static bool IsCsvFile(string fileExtension)
        {

            return !string.IsNullOrWhiteSpace(fileExtension) && (fileExtension.EndsWith(".csv"));
        }

        bool DisplayDialogConfirmation { get; set; } = false;
        bool IsConfirmation { get; set; } = true;

        private void LoadTemplate()
        {
            IsLoaderTemplate = true;
            if (IsLoaderTemplateChanged.HasDelegate)
            {
                IsLoaderTemplateChanged.InvokeAsync(IsLoaderTemplate);
            }
            if (LoadTemplateCount == 0)
            {
                LoadHandler?.Invoke();
                AppState.LoadHander?.Invoke();
            }
            else
            {
                DisplayDialogConfirmation = true;
            }
        }
        private void DisposeDisplayDialogConfirmation()
        {
           
        }
        private void LoadTemplateAfterConfirmation()
        {
            LoadHandler?.Invoke();
            AppState.LoadHander?.Invoke();
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                Config = FileLoaderConfigInfo;
                ConfigChanged.InvokeAsync(Config);
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        public ValueTask DisposeAsync()
        {
            Config = null;
            ConfigChanged.InvokeAsync(Config);
            return ValueTask.CompletedTask;
        }


        public ObservableCollection<string> Methods
        {
            get
            {
                if(!AppState.PrivilegeObserver.CanCreatedSourcingInputGrid)
                {
                    return new(FileLoaderMethod.NEW_GRID.GetAll(x => AppState[x]).Where(x => x != FileLoaderMethod.NEW_GRID.GetText(x => AppState[x])));
                }
                return FileLoaderMethod.NEW_GRID.GetAll(x=> AppState[x]);
            }
        }
    }
}

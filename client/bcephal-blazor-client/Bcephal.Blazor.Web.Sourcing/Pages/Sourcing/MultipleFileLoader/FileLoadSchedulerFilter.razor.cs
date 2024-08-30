using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Loaders;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Forms;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.MultipleFileLoader
{
    public partial class FileLoadSchedulerFilter : ComponentBase
    {

        [Parameter]
        public string TemplateFileName { get; set; }

        [Parameter]
        public EventCallback<string> TemplateFileNameChanged { get; set; }

        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public List<IBrowserFile> loadedFiles { get; set; }

        [Parameter]
        public EventCallback<List<IBrowserFile>> loadedFilesChanged { get; set; }

        [Parameter]
        public Action LoadFileEvent { get; set; }

        [Parameter]
        public EditorData<FileLoader> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<FileLoader>> EditorDataChanged { get; set; }

        [Parameter] public bool Editable { get; set; } = true;

        IEnumerable<int> maxBackupCount = new List<int>() { 1, 2, 5, 10, 15, 20, 25, 30, 50, };

        public bool IsSmallScreen { get; set; }
        public string LabelWidth { get; set; } = Constant.GROUP_INFOS_LABEL_WIDTH;

        public string TextWidth { get; set; } = Constant.GROUP_TEXT_WIDTH;

        List<string> InputfileExtension = new List<String>(){
            ".csv",
            ".xslx",
            ".txt",
        };

        private byte[] AllBytes { get; set; }

        [Parameter]
        public EventCallback<byte[]> AllBytesChanged { get; set; }

        private byte[] AllBytes_
        {
            get { return AllBytes; }
            set
            {
                AllBytes = value;
                AllBytesChanged.InvokeAsync(AllBytes);
            }
        }


        public string SelectedFilesName_ { get; set; }
        public string SelectedFilesName
        {
            get
            {
                return EditorData.Item.File;
            }
            set
            {
                SelectedFilesName_ = value;
                EditorData.Item.File = SelectedFilesName_;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public List<IBrowserFile> loadedFiles_
        {
            get
            {
                return loadedFiles;
            }
            set
            {
                loadedFiles = value;
                loadedFilesChanged.InvokeAsync(loadedFiles);
            }
        }


        IEnumerable<string> fileExtension = new List<String>(){
            "All(.*)",
            "CSV(.csv)",
            "EXCEL(.xslx)",
            "TEXT(.txt)",
        };


        private int MaxBackupCount
        {
            get { return EditorData.Item.MaxBackupCount; }
            set
            {
                EditorData.Item.MaxBackupCount = value;
                AppState.Update = true;
            }
        }

        public string SelectFolder
        {
            get
            {
                return EditorData.Item.RepositoryOnServer;
            }
            set
            {
                EditorData.Item.RepositoryOnServer = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }


        private bool AllowBackup
        {
            get { return EditorData.Item.AllowBackup; }
            set
            {
                EditorData.Item.AllowBackup = value;
                AppState.Update = true;
            }
        }

        public bool Scheduled
        {
            get
            {
                return EditorData.Item.Scheduled;
            }
            set
            {
                EditorData.Item.Scheduled = value;
                AppState.Update = true;
            }
        }

        private bool Active
        {
            get { return EditorData.Item.Active; }
            set
            {
                EditorData.Item.Active = value;
                AppState.Update = true;
            }
        }

        private string CronExpression
        {
            get { return EditorData.Item.CronExpression; }
            set
            {
                EditorData.Item.CronExpression = value;
                AppState.Update = true;
            }
        }

        private string Repository
        {
            get { return EditorData.Item.Repository; }
            set
            {
                EditorData.Item.Repository = value;
                AppState.Update = true;
            }
        }

        private string RepositoryOnServer
        {
            get { return EditorData.Item.RepositoryOnServer; }
            set
            {
                EditorData.Item.RepositoryOnServer = value;
                AppState.Update = true;
            }
        }

        ObservableCollection<FileLoaderNameCondition> Conditions
        {
            get
            {
                return EditorData.Item.ConditionListChangeHandler.GetItems();
            }
        }

        List<string> Keys = new();
        List<RenderFragment> renders = new();
        private Task AddRenderInputCondition(FileLoaderNameCondition item)
        {
            Keys.Add(item.Key);
            renders.Add(renderInputCondition(item));
            return Task.CompletedTask;
        }
        private void RemoveRenderInputCondition(FileLoaderNameCondition Item)
        {
            int pos = Keys.FindIndex(key => key == Item.Key);
            if (pos >= 0)
            {
                Keys.RemoveAt(pos);
                renders.RemoveAt(pos);
            }
        }

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                await InitRendersInputCondition();
            }
            await base.OnAfterRenderAsync(firstRender);
        }

        protected  async Task InitRendersInputCondition()
        {
            foreach (var condition in Conditions)
            {
                await AddRenderInputCondition(condition);
            }
            await AddRenderInputCondition(new FileLoaderNameCondition());
            StateHasChanged(); 
        }

        protected void AddCondition(FileLoaderNameCondition item)
        {
            if (!item.IsPersistent)
            {
                EditorData.Item.AddCondition(item);
            }
            AppState.Update = true;
            StateHasChanged();
        }
        protected void updateCondition(FileLoaderNameCondition item)
        {
            if (item.IsPersistent)
            {
                EditorData.Item.UpdateCondition(item);
            }
            AppState.Update = true;
            StateHasChanged();
        }

        protected void AddRenderCondition()
        {
            AddRenderInputCondition(new FileLoaderNameCondition());
            StateHasChanged();
        }

        protected void DeleteCondition(FileLoaderNameCondition item)
        {
            EditorData.Item.DeleteOrForgetCondition(item);
            AppState.Update = true;
            RemoveRenderInputCondition(item);
            StateHasChanged();
        }


        private Dictionary<string, List<string>> FileNameConditionSelectedInputFile
        {
            get
            {
                Dictionary<string, List<string>> dico = new();
                var items = EditorData.Item.ConditionListChangeHandler.GetItems();
                foreach (var item in items)
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
                return dico;
            }
            set
            {

            }
        }
      
        public void UpdateFileNames(List<string> filenames)
        {
            //if (filenames.Any())
            //{
            //    SheetFiles.Clear();

            //    if (filenames.Count == 1)
            //    {
            //        SelectedFilesName = InputFiles.Keys.First();
            //        byte[] bytes = InputFiles[SelectedFilesName];
            //        FileExtension = RetrieveFileNameExtension(filenames.First());
            //        if (IsExcelFile(FileExtension))
            //        {
            //            XSSFWorkbook XSSFWorkbook_ = ByteArrayToObject(bytes);
            //            int nbsheet = XSSFWorkbook_.NumberOfSheets;
            //            for (int i = 0; i < nbsheet; i++)
            //            {
            //                SheetFiles_.Add(XSSFWorkbook_.GetSheetAt(i));
            //            }
            //            SelectedExcelFile = SheetFiles_.FirstOrDefault();
            //        }
            //    }
            //    else if (filenames.Count > 1)
            //    {
            //        SelectedFilesName = "";
            //        foreach (string names in filenames)
            //        {
            //            SelectedFilesName = SelectedFilesName + ";" + names;
            //        }
            //    }
            //    StateHasChanged();
            //}
        }

      

    }
}

using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Loaders;
using Bcephal.Models.socket;
using DevExpress.Blazor;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Forms;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;


namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.MultipleFileLoader
{
  public partial  class FileLoaderGrilleBrowser : AbstractGridComponent<Models.Loaders.FileLoaderColumn, Models.Loaders.FileLoaderColumn>
    {
        protected dynamic[] GridColumns => new[] {
                         new {CaptionName = AppState["LigneNumber"] ,ColumnWidth="5%",  ColumnName = nameof(Models.Loaders.FileLoaderColumn.Position), ColumnType = typeof(int)},
                        new {CaptionName = AppState["ColumnName"] ,ColumnWidth="auto",  ColumnName = nameof(Models.Loaders.FileLoaderColumn.GrilleColumn.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Type"] ,ColumnWidth="auto", ColumnName = nameof(Models.Loaders.FileLoaderColumn.Type), ColumnType = typeof(string)},
                        new {CaptionName = AppState["SelectionDimension"] ,ColumnWidth="auto", ColumnName = nameof(FileLoaderColumn.DimensionName), ColumnType = typeof(string)},
                        new {CaptionName = AppState["InputFileColumn"] ,ColumnWidth="auto", ColumnName = nameof(Models.Loaders.FileLoaderColumn.FileColumn), ColumnType = typeof(string)},

                    };
        protected override int ItemsCount => GridColumns.Length;

        [Inject] private FileLoaderService FileLoaderService { get; set; }
        [Inject] private WebSocketAddress WebSocketAddress { get; set; }
        [Inject] public IToastService toastService { get; set; }

        [Parameter] public bool Editable { get; set; } = true;
        [Parameter] public List<IBrowserFile> loadedFiles { get; set; }
        
        [Parameter] public ObservableCollection<HierarchicalData> Entities { get; set; }
        [Parameter] public ObservableCollection<GrilleColumn> Columns { get; set; } = new ObservableCollection<GrilleColumn>();
        [Parameter] public EventCallback<EditorData<FileLoader>> EditorDataFileLoaderChanged { get; set; }
        List<Models.Dimensions.Attribute> ModelsAttributes { get; set; } = new List<Models.Dimensions.Attribute>();

        private bool CanRefreshGrid { get; set; } = false;

        private EditorData<FileLoader> EditorDataFileLoader_ { get; set; }
        [Parameter] public EditorData<FileLoader> EditorDataFileLoader { get => EditorDataFileLoader_; 
            set {
                EditorDataFileLoader_ = value;
                if (CanRefreshGrid)
                {
                    AppState.ShowLoadingStatus();
                    InvokeAsync(Refresh);
                    AppState.HideLoadingStatus();
                }
            } 
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            IsNavLink = false;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = false;
            EditButtonVisible = true;
            SelectionMode = DataGridSelectionMode.SingleSelectedDataRow;
            CurrentEditMode = DataGridEditMode.EditForm;
            UsingDefaultCustomTemplate = false;
            ShowPager = false;
            ShowsAll_ = true;
            PageSizeSelector = false;
            EmptyLineVisible = false;
            page_PageSize = -1;
            InitAttributes();
        }
        private object GetPropertyValue(Models.Loaders.FileLoaderColumn obj, string propName)
        {
            if (obj == null)
            {
                return null;
            }
            if (nameof(Models.Loaders.FileLoaderColumn.GrilleColumn.Name).Equals(propName))
            {
                string name_ = nameof(Models.Loaders.FileLoaderColumn.GrilleColumn);
                GrilleColumn obValue = (GrilleColumn)obj.GetType().GetProperty(name_).GetValue(obj, null);
                if (obValue != null)
                {
                    string name = nameof(GrilleColumn.Name);
                    return obValue.GetType().GetProperty(name).GetValue(obValue, null);
                }
                return "";
            }
            if (nameof(Models.Loaders.FileLoaderColumn.GrilleColumn.ColumnName).Equals(propName))
            {
                string name_ = nameof(Models.Loaders.FileLoaderColumn.GrilleColumn);
                GrilleColumn obValue = (GrilleColumn)obj.GetType().GetProperty(name_).GetValue(obj, null);
                if (obValue != null)
                {
                    string name = nameof(GrilleColumn.ColumnName);
                    return obValue.GetType().GetProperty(name).GetValue(obValue, null);
                }
                return "";
            }
            if (nameof(FileLoaderColumn.Position).Equals(propName))
            {
                int val = (int)obj.GetType().GetProperty(propName).GetValue(obj, null) + 1;
                return val;
            }       
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override AbstractGridDataItem GetGridDataItem(int Position)
        {
            return new GridDataItem(GridColumns[Position], Position);
        }


        protected override object GetFieldValue(Models.Loaders.FileLoaderColumn item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);

        }

        protected override string KeyFieldName()
        {
            return nameof(Models.Loaders.FileLoaderColumn.Position);
        }

        protected override object KeyFieldValue(Models.Loaders.FileLoaderColumn item)
        {
            return item.Position;
        }

        protected override void OnHtmlDataCellDecoration(DataGridHtmlDataCellDecorationEventArgs<Models.Loaders.FileLoaderColumn> eventArgs)
        {
            if (eventArgs.FieldName == nameof(Models.Loaders.FileLoaderColumn.FileColumn))
            {
                if (eventArgs.DataItem != null && !eventArgs.DataItem.DimensionId.HasValue)
                {
                    eventArgs.Style += " color: #dc3545;";
                }
            }
        }

        protected override string NavLinkURI()
        {
            throw new NotImplementedException();
        }

        protected override Task OnRowInserting(Dictionary<string, object> newValues)
        {
            throw new NotImplementedException();
        }

        protected override Task OnRowRemoving(Models.Loaders.FileLoaderColumn dataItem)
        {
            throw new NotImplementedException();
        }

        protected override Task OnRowRemoving(List<long> ids)
        {
            throw new NotImplementedException();
        }

        protected override Task OnRowUpdating(Models.Loaders.FileLoaderColumn dataItem, Dictionary<string, object> newValues)
        {
            throw new NotImplementedException();
        }

        private void Clear()
        {
            foreach (Models.Loaders.FileLoaderColumn row in EditorDataFileLoader.Item.ColumnListChangeHandler.GetItems())
            {
                EditorDataFileLoader.Item.DeleteOrForgetColumn(row);
            }
        }

        private FileType GetFileType(string value)
        {
            if (!string.IsNullOrWhiteSpace(value))
            {
                string value_ = value.ToLower();
                if (value_.EndsWith(".xls") || value_.EndsWith(".xlsx"))
                {
                    return FileType.EXCEL;
                }
            }
            return FileType.CSV;
        }

        public async Task RefreshGrid_(bool CanRefresh = false)
        {
            if (loadedFiles.Count > 0)
            {
                AppState.ShowLoadingStatus();
                IBrowserFile iBrowserFile = loadedFiles.FirstOrDefault();
                var data = new FileLoaderColumnDataBuilder();
                data.GridId = EditorDataFileLoader.Item.TargetId;
                data.HasHeader = EditorDataFileLoader.Item.HasHeader;
                data.SheetName = EditorDataFileLoader.Item.SheetName;
                data.SheetIndex = EditorDataFileLoader.Item.SheetIndex;
                data.FileType = GetFileType(EditorDataFileLoader.Item.FileExtension);
                data.Separator = EditorDataFileLoader.Item.FileSeparator;

                if (data.HasHeader)
                {
                    data.HeaderRowcount = 1;
                }
                try
                {
                    string repository = await iBrowserFile.Uplaod(FileLoaderService, "/upload/resume");
                    ObservableCollection<Models.Loaders.FileLoaderColumn> page = await FileLoaderService.LoaderFileLoaderColumn(data, repository);
                    Clear();
                    foreach (Models.Loaders.FileLoaderColumn row in page)
                    {
                        row.DimensionName = GetDimensionName(row.DimensionId, row.Type);
                        EditorDataFileLoader.Item.AddColumn(row);
                    }
                    AppState.ShowLoadingStatus();
                   await EditorDataFileLoaderChanged.InvokeAsync(EditorDataFileLoader);
                    if (CanRefresh)
                    {
                        AppState.ShowLoadingStatus();
                        await Refresh();
                    }
                }
                catch (Exception e)
                {
                    Error.ProcessError(e);
                    StateHasChanged();
                }

            }
        }

        public async Task RefreshWebSocketGrid(string repository,  Action errorCallBack, bool CanRefresh = false)
        {
            if (loadedFiles != null && loadedFiles.Count > 0)
            {
                AppState.ShowLoadingStatus();

                var item = loadedFiles.FirstOrDefault();
                var data = new FileLoaderColumnDataBuilder();
                data.GridId = EditorDataFileLoader.Item.TargetId;
                data.HasHeader = EditorDataFileLoader.Item.HasHeader;
                data.SheetName = EditorDataFileLoader.Item.SheetName;
                data.SheetIndex = EditorDataFileLoader.Item.SheetIndex;
                data.FileType = GetFileType(EditorDataFileLoader.Item.FileExtension);
                data.Separator = EditorDataFileLoader.Item.FileSeparator;

                if (data.HasHeader)
                {
                    data.HeaderRowcount = 1;
                }
                if (!string.IsNullOrWhiteSpace(repository))
                {
                    try
                    {
                        ObservableCollection<Models.Loaders.FileLoaderColumn> page = await FileLoaderService.LoaderFileLoaderColumn(data, repository);
                        Clear();
                        foreach (Models.Loaders.FileLoaderColumn row in page)
                        {
                            row.DimensionName = GetDimensionName(row.DimensionId, row.Type);
                            EditorDataFileLoader.Item.AddColumn(row);
                        }
                        AppState.ShowLoadingStatus();
                        await EditorDataFileLoaderChanged.InvokeAsync(EditorDataFileLoader);
                        if (CanRefresh)
                        {
                            AppState.ShowLoadingStatus();
                            await Refresh();
                        }
                    }
                    catch (Exception e)
                    {
                        errorCallBack.Invoke();
                        Error.ProcessError(e);
                        StateHasChanged();
                    }
                }
            }
            else
            {
                toastService.ShowWarning(AppState["empty.input.files"], AppState["warning"]);
            }
        }

        public async Task RefreshWebSocketGrid_(bool CanRefresh = false)
        {
            if (loadedFiles != null && loadedFiles.Count > 0)
            {
                AppState.ShowLoadingStatus();

                var item = loadedFiles.FirstOrDefault();
                var data = new FileLoaderColumnDataBuilder();
                data.GridId = EditorDataFileLoader.Item.TargetId;
                data.HasHeader = EditorDataFileLoader.Item.HasHeader;
                data.SheetName = EditorDataFileLoader.Item.SheetName;
                data.SheetIndex = EditorDataFileLoader.Item.SheetIndex;
                data.FileType = GetFileType(EditorDataFileLoader.Item.FileExtension);
                data.Separator = EditorDataFileLoader.Item.FileSeparator;

                if (data.HasHeader)
                {
                    data.HeaderRowcount = 1;
                }
                Func<string, Task> ExecuteCallBackLoader = async (repository) =>
                {
                    try
                    {
                        ObservableCollection<Models.Loaders.FileLoaderColumn> page = await FileLoaderService.LoaderFileLoaderColumn(data, repository);
                        Clear();
                        foreach (Models.Loaders.FileLoaderColumn row in page)
                        {
                            row.DimensionName = GetDimensionName(row.DimensionId, row.Type);
                            EditorDataFileLoader.Item.AddColumn(row);
                        }
                        AppState.ShowLoadingStatus();
                        await EditorDataFileLoaderChanged.InvokeAsync(EditorDataFileLoader);
                        if (CanRefresh)
                        {
                            AppState.ShowLoadingStatus();
                            await Refresh();
                        }
                    }
                    catch (Exception e)
                    {
                        Error.ProcessError(e);
                        StateHasChanged();
                    }
                };
                await item.UplaodBySocket(async (datatransf) =>
                {
                    await JSRuntime.InvokeVoidAsync("console.log", $"Successfuly to transfert :{datatransf.Name} to server to path:{datatransf.RemotePath}");
                    if (Models.socket.Decision.CONTINUE.Equals(datatransf.decision) && !string.IsNullOrWhiteSpace(datatransf.RemotePath))
                    {
                        await JSRuntime.InvokeVoidAsync("console.log", $"try to do callback");
                        await ExecuteCallBackLoader?.Invoke(datatransf.RemotePath);
                    }
                },
                async (Socket) => await FileLoaderService.ConnectSocketJS(Socket, "/upload-data-file"), JSRuntime, AppState, WebSocketAddress, toastService
                );

            }
            else
            {
                toastService.ShowWarning(AppState["empty.input.files"], AppState["warning"]);
            }
        }

        protected override Task SearchRows(BrowserDataFilter filter, BrowserDataPage<Models.Loaders.FileLoaderColumn> page_, DataSourceLoadOptionsBase options)
        {
            ObservableCollection<Models.Loaders.FileLoaderColumn> page = EditorDataFileLoader.Item.ColumnListChangeHandler.GetItems();
            foreach (Models.Loaders.FileLoaderColumn row in page)
            {
                if (string.IsNullOrWhiteSpace(row.DimensionName))
                {
                    row.DimensionName = GetDimensionName(row.DimensionId, row.Type);
                }
                page_.Items.Add(row);
            }
            page_.CurrentPage = 1;
            page_.PageCount = page.Count;
            page_.PageFirstItem = 1;
            page_.PageLastItem = page.Count;
            page_.PageSize = 25;
            page_.TotalItemCount = page.Count;
            CanRefreshGrid = true;
            return Task.CompletedTask;
        }

        private string GetDimensionName(long? dimensionId, DimensionType type)
        {
            if (!dimensionId.HasValue)
            {
                return null;
            }
            if (DimensionType.MEASURE.Equals(type))
            {
                IEnumerable<Models.Dimensions.Measure> measure = EditorDataFileLoader.Measures.Where((item) => item.Id.Value == dimensionId.Value);
                if (measure.Count() > 0)
                {
                    return measure.FirstOrDefault().Name;
                }
            }
            else
                if (DimensionType.PERIOD.Equals(type))
            {
                IEnumerable<Models.Dimensions.Period> period = EditorDataFileLoader.Periods.Where((item) => item.Id.Value == dimensionId.Value);
                if (period.Count() > 0)
                {
                    return period.FirstOrDefault().Name;
                }
            }
            else
            {
                IEnumerable<Models.Dimensions.Attribute> entity = ModelsAttributes.Where((item) => item.Id.Value == dimensionId.Value && item is Models.Dimensions.Attribute);
                if (entity.Count() > 0)
                {
                    return entity.FirstOrDefault().Name;
                }
            }
            return null;
        }

        private Dimension GetDimension(string dimensionName, DimensionType type)
        {
            if (string.IsNullOrWhiteSpace(dimensionName))
            {
                return null;
            }
            if (DimensionType.MEASURE.Equals(type))
            {
                IEnumerable<Models.Dimensions.Measure> measure = EditorDataFileLoader.Measures.Where((item) => item.Name.Equals(dimensionName));
                if (measure.Count() > 0)
                {
                    return measure.FirstOrDefault();
                }
            }
            else
                if (DimensionType.PERIOD.Equals(type))
            {
                IEnumerable<Models.Dimensions.Period> period = EditorDataFileLoader.Periods.Where((item) => item.Name.Equals(dimensionName));
                if (period.Count() > 0)
                {
                    return period.FirstOrDefault();
                }
            }
            else
            {

                IEnumerable<Models.Dimensions.Attribute> entity = ModelsAttributes.Where((item) => item.Name.Equals(dimensionName) && item is Models.Dimensions.Attribute);
                if (entity.Count() > 0)
                {
                    return entity.FirstOrDefault();
                }
            }
            return null;
        }

        private void InitAttributes()
        {
            foreach (var item in Entities)
            {
                if (item is Models.Dimensions.Entity)
                {
                    Models.Dimensions.Entity item_ = (Models.Dimensions.Entity)item;
                    ModelsAttributes.AddRange(item_.Descendents);
                }
                else
                    if (item is Models.Dimensions.Attribute)
                {
                    Models.Dimensions.Attribute item_ = (Models.Dimensions.Attribute)item;
                    ModelsAttributes.AddRange(item_.Descendents);
                }

            }
        }
        FormEditContext EditContext = null;

        protected override async void OnSingleSelectionChanged(Models.Loaders.FileLoaderColumn selection)
        {
            if (selectedItem != null && !selectedItem.Equals(selection) && editContexts != null && editContexts.Count > 0)
            {
                var cts = editContexts.Where(item => item.DataItem.Equals(selectedItem)).FirstOrDefault();
                if (cts != null)
                {
                    //OnCancelButtonClick(selectedItem);
                    var e = new KeyboardEventArgs();
                    e.Code = "Enter";
                    await EnterHandleValidSubmit(e,selectedItem);
                }
            }
            base.OnSingleSelectionChanged(selection);
        }

        //protected override async Task onClick_(MouseEventArgs e, Models.Loaders.FileLoaderColumn item)
        //{
        //    if (selectedItem != null && !selectedItem.Equals(item) && editContexts != null && editContexts.Count > 0)
        //    {
        //        var cts = editContexts.Where(item => item.DataItem.Equals(selectedItem)).FirstOrDefault();
        //        if (cts != null)
        //        {
        //            var e = new KeyboardEventArgs();
        //            e.Code = "Enter";
        //            await EnterHandleValidSubmit(e, selectedItem);
        //        }
        //    }
        //}

        protected override Task OnRowEditStarting(Models.Loaders.FileLoaderColumn item)
        {
            EditContext = new FormEditContext(item, Columns, GetDimension, StateHasChanged);
            return Task.CompletedTask;
        }
        protected override async Task OnCancelButtonClick()
        {
            await DxDataGridRef.CancelRowEdit();
            EditContext = null;
        }

        protected override Task OnCancelButtonClick(Models.Loaders.FileLoaderColumn DataItem)
        {
            EditContext = null;
            editContexts.Remove(GetContexts(DataItem));
            return base.OnCancelButtonClick(DataItem);
        }
        protected override async Task HandleValidSubmit()
        {
            EditContext.update();
            EditorDataFileLoader.Item.UpdateColumn(EditContext.DataItem);
            await EditorDataFileLoaderChanged.InvokeAsync(EditorDataFileLoader);
            await DxDataGridRef.CancelRowEdit();
        }

        List<FormEditContext> editContexts = new List<FormEditContext>();

        private FormEditContext GetContexts(Models.Loaders.FileLoaderColumn DataItem)
        {
            FormEditContext Contexts = editContexts.Where(item => item.DataItem.Equals(DataItem)).FirstOrDefault();
            if (Contexts == null)
            {
                Contexts = new FormEditContext(DataItem, Columns,GetDimension, StateHasChanged);
                editContexts.Add(Contexts);
            }
            return Contexts;
        }

        protected override Task HandleValidSubmit(Models.Loaders.FileLoaderColumn DataItem)
        {
            try
            {
                FormEditContext Contexts = GetContexts(DataItem);
                Contexts.update();
                if (IsNewRow)
                {
                    EditorDataFileLoader.Item.AddColumn(Contexts.DataItem);
                    EditorDataFileLoaderChanged.InvokeAsync(EditorDataFileLoader);
                }
                else
                {
                    EditorDataFileLoader.Item.UpdateColumn(Contexts.DataItem);
                    EditorDataFileLoaderChanged.InvokeAsync(EditorDataFileLoader);
                }
                CanRefreshGrid = true;
                EditContext = null;
                editContexts.Remove(Contexts);
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
            return Task.CompletedTask;
        }

        public ObservableCollection<DimensionType> DimensionTypes = new ObservableCollection<DimensionType>()
            {
                DimensionType.ATTRIBUTE,
                DimensionType.MEASURE,
                DimensionType.PERIOD
            };

        protected class FormEditContext
        {
            public FormEditContext(FileLoaderColumn dataItem, ObservableCollection<GrilleColumn> Columns, Func<string, DimensionType, Dimension> dimensionHandler, Action stateHasChanged)
            {
                DataItem = dataItem;
                Type = dataItem.Type.ToString();
                SelectedDimensionId = dataItem.DimensionId;
                SelectedDimensionName = dataItem.DimensionName;
                this.Columns = Columns;
                InitialDimensionType();
                DimensionHandler = dimensionHandler;
                StateHasChanged = stateHasChanged;
            }

            private Func<string, DimensionType, Dimension> DimensionHandler { get; set; }
            public ObservableCollection<GrilleColumn> Columns { get; set; }

            private GrilleColumn Column_ { get; set; }

            public GrilleColumn Column
            {
                get => Column_;
                set
                {
                    Column_ = value;
                    SelectedDimensionId = value.DimensionId;
                    SelectedDimensionName = value.DimensionName;
                    DimensionType_ = value.Type;
                    StateHasChanged();
                }
            }

            public FileLoaderColumn DataItem { get; set; }

            public string Type { get; set; }

            public long? SelectedDimensionId { get; set; }

            public string SelectedDimensionName { get; set; }

            public DimensionType DimensionTypeAux { get; set; }
            public DimensionType DimensionType_
            {
                get
                {
                    return DimensionTypeAux;
                }
                set
                {
                    DimensionTypeAux = value;                    
                    SelectedDimensionId = null;
                    SelectedDimensionName = null;
                    var dimension_ = DimensionHandler?.Invoke(DataItem.FileColumn, value);
                    if(dimension_ != null)
                    {
                        SelectedDimensionId = dimension_.Id;
                        SelectedDimensionName = dimension_.Name;
                    }
                    StateHasChanged();
                }
            }

            public ObservableCollection<DimensionType> DimensionTypes = new ObservableCollection<DimensionType>()
            {
                DimensionType.ATTRIBUTE,
                DimensionType.MEASURE,
                DimensionType.PERIOD
            };

            private void InitialDimensionType()
            {
                if (DataItem != null)
                {
                    DimensionTypeAux = DataItem.Type;
                }
                else
                {
                    DimensionTypeAux = DimensionTypes.First();
                }
            }

            public Action StateHasChanged { get; set; }
            public void update()
            {
                DataItem.Type = DimensionType_;
                DataItem.DimensionId = SelectedDimensionId;
                DataItem.DimensionName = SelectedDimensionName;
                if (DataItem.GrilleColumn != null)
                {
                    DataItem.GrilleColumn.Name = SelectedDimensionName;
                    DataItem.GrilleColumn.Type = DataItem.Type;
                    DataItem.GrilleColumn.DimensionId = DataItem.DimensionId;
                    DataItem.GrilleColumn.DimensionName = DataItem.DimensionName;
                    DataItem.GrilleColumn.Position = DataItem.Position;
                }
            }
        }



        public void SelectedDimensionChanged(object hierarchicalData_, Models.Loaders.FileLoaderColumn DataItem, DimensionType type)
        {
            if (hierarchicalData_ is HierarchicalData)
            {
                HierarchicalData hierarchicalData = (HierarchicalData)hierarchicalData_;
                //EditContext.SelectedDimensionName = hierarchicalData.Name;
                //EditContext.SelectedDimensionId = hierarchicalData.Id;

                DataItem.Type = type;
                DataItem.DimensionId = hierarchicalData.Id;
                DataItem.DimensionName = hierarchicalData.Name;
                if (DataItem.GrilleColumn != null)
                {
                    DataItem.GrilleColumn.Name = hierarchicalData.Name;
                    DataItem.GrilleColumn.Type = type;
                    DataItem.GrilleColumn.DimensionId = DataItem.DimensionId;
                    DataItem.GrilleColumn.DimensionName = DataItem.DimensionName;
                    DataItem.GrilleColumn.Position = DataItem.Position;
                }
            }
        }

        public void SelectedDimensionChanged(object hierarchicalData_)
        {
            if (hierarchicalData_ is HierarchicalData)
            {
                HierarchicalData hierarchicalData = (HierarchicalData)hierarchicalData_;
                EditContext.SelectedDimensionName = hierarchicalData.Name;
                EditContext.SelectedDimensionId = hierarchicalData.Id;
            }
        }

        public void SelectedDimensionChanged2(object hierarchicalData_, dynamic Contexts)
        {
            if (hierarchicalData_ is HierarchicalData)
            {
                HierarchicalData hierarchicalData = (HierarchicalData)hierarchicalData_;
                Contexts.SelectedDimensionName = hierarchicalData.Name;
                Contexts.SelectedDimensionId = hierarchicalData.Id;
            }
        }
    }
}

using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using Bcephal.Models.Utils;
using DevExpress.Blazor;
using System.Text.RegularExpressions;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Blazor.Web.Base.Services; 
using Newtonsoft.Json;
using System.Collections;
using System.Text;
using System.IO;
using System.Net.Http;
using Bcephal.Models.Joins;
using Bcephal.Blazor.Web.Reporting.Services;
using System.Globalization;

namespace Bcephal.Blazor.Web.Reporting.Pages.Joins
{
    public partial class GridJoinBrowserComponent : AbstractNewGridComponent<Join, GridItem>
    {
        [Parameter] public bool Editable { get; set; } = true;

        [Parameter] public List<string> AddColumns { get; set; } = new();

        [Parameter] public bool CanDisplayDeleteButton { get; set; } = true;

        [Inject] private WebSocketAddress WebSocketAddress { get; set; }

        [Inject] public JoinService JoinService { get; set; }

        [Inject] public GrilleService GrilleService { get; set; }   
        
        [Inject] public IToastService ToastService { get; set; }

        protected override int ItemsCount { get => Items.Count(); }

        public ObservableCollection<JoinColumn> Items_ { get; set; }

        private bool IsLoaded { get; set; } = false;

        private string JoinExportFileName = "";

        private FileStream contentF;

        protected override bool CanShowSelectionColumn => false;

        public ObservableCollection<JoinColumn> Items
        {
            get
            {
                if (EditorData != null && EditorData.Item != null && !IsLoaded)
                {
                    Items_ = EditorData.Item.ColumnListChangeHandler.GetItems();
                    Items_.BubbleSort();
                    IsLoaded = true;
                }
                else
                {
                    if (!IsLoaded)
                    {
                        Items_ = new();
                    }
                }
                return Items_;
            }
        }

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                AppState.RefreshPublicationHandler += RefreshPublication;
            }
            if (CanRefresh && !AppState.IsDashboard)
            {
                AppState.CanRefresh = true;
                UpdateCanRfreshGridStatus(true);
            }
            await base.OnAfterRenderAsync(firstRender);
        }


        protected override async Task OnInitializedAsync()
        {
            IsNavLink = false;
            if (ItemsCount > 0 && EditorData.Item != null)
            {
                NewButtonVisible = false;
                EditButtonVisible = false;
                DeleteButtonVisible = false;
                ClearFilterButtonVisible = true;
                if (EditorData.Item.ShowAllRowsByDefault)
                {
                    page_PageSize = -1;
                }
                AllowRowCounting = EditorData.Item.AllowLineCounting;
                AllowAllRow = EditorData.Item.ShowAllRowsByDefault;
            }
            else
            {
                NewButtonVisible = false;
                ClearFilterButtonVisible = false;
            }
            AppState.CanRefreshPublication = true;
            await base.OnInitializedAsync();
        }

        public override ValueTask DisposeAsync()
        {
            AppState.CanRefreshPublication = false;
            AppState.RefreshPublicationHandler -= RefreshPublication;
            return base.DisposeAsync();
        }

        protected override GridItem NewItem()
        {
            return new GridItem(new object[ItemsCount + 1]);
        }

        /// <summary>
        ///     Cette methode est appelée lorsque la socket renvoi un résultat
        ///     le résultat est une classe qui contient un attribut Bites[] qui est le fichier à sauvegarder
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="message"></param>
        public async void CallBackExport(object sender, object message)
        {
            //await JSRuntime.InvokeVoidAsync("console.log", "call of export grid callback : ");
            if (message != null && !string.IsNullOrWhiteSpace(message.ToString()))
            {
                TaskProgressInfo info = JsonConvert.DeserializeObject<TaskProgressInfo>(message.ToString());
                if (info != null && !string.IsNullOrWhiteSpace(info.Message))
                {
                    ExportDataTransfert contentFile = JsonConvert.DeserializeObject<ExportDataTransfert>(info.Message);

                    {
                        if (contentFile != null && contentFile.Data != null && (contentFile.Data.Length > 0 || Decision.END.Equals(contentFile.decision)))
                        {
                            if (Decision.NEW.Equals(contentFile.decision) || contentF == null)
                             {
                                 var path = Path.GetTempFileName();
                                //Console.WriteLine(String.Format("TempFileName ==> {0} ", path));
                                contentF = new(path, FileMode.Create);
                             }

                             string ext = contentFile.DataType.extension;
                            //string data = Convert.ToBase64String(contentFile.Data.ToArray());
                            JoinExportFileName = EditorData.Item.Name + ext;
                            // await JSRuntime.InvokeAsync<object>("saveAsFileBytes", EditorData.Item.Name + ext, data);
                            contentF.Write(contentFile.Data, 0, contentFile.Data.Length);
                             if (Decision.END.Equals(contentFile.decision))
                             {
                                 try
                                 {
                                    if (contentF != null && contentF.Length > 0)
                                    {
                                        ///Console.WriteLine(String.Format("Decision.END ==> export file "));
                                        contentF.Seek(0, SeekOrigin.Begin);
                                        var stream = new StreamContent(contentF);
                                        await JSRuntime.InvokeAsync<object>("saveAsFileBytes", $"{JoinExportFileName}", Convert.ToBase64String(await stream.ReadAsByteArrayAsync()));
                                        //await JSRuntime.InvokeAsync<object>("saveAsFileBytes", $"{JoinExportFileName}", Convert.ToBase64String(ReadBytes(contentF)));
                                        await contentF.DisposeAsync();
                                        contentF = null;
                                        ToastService.ShowSuccess(AppState["export.Success.files.message"], AppState["Loader"]);
                                    }
                                    else
                                    {
                                        ToastService.ShowError(AppState["export.Error.files.message"], AppState["Error"]);
                                    }
                                 }
                                 catch (Exception ex)
                                 {
                                     ToastService.ShowError((string)ex.Message, AppState["Error"]);
                                 }
                             }
                         }
                     };
                }
            }
        }

        public async void ExportData(BrowserDataFilter browserDataFilter, GrilleExportDataType type)
        {
           // await JSRuntime.InvokeVoidAsync("console.log", "Début de l'export des données !!");
            try
            {
                AppState.ShowLoadingStatus();
                await JSRuntime.InvokeVoidAsync("console.log", "try to download file");
                await BuildFilter(browserDataFilter);
                browserDataFilter.Grid = new Bcephal.Models.Grids.Grille() { Id = EditorData.Item.Id };
                GrilleExportData data = new GrilleExportData();
                data.Filter = browserDataFilter;
                data.DataType = type;
                //await JSRuntime.InvokeVoidAsync("console.log", "objet transmis => ", data);
                SocketJS Socket = new SocketJS(WebSocketAddress, CallBackExport, JSRuntime, AppState, true, true) {
                    //CallBackJsFunction = "resumeFileAfterDownload",
                   // CallBackJsFunctionKey = Guid.NewGuid().ToString("d")
                };
                Socket.CloseHandler +=  () =>
                {
                    
                };

                Socket.ErrorHandler += (errorMessage) =>
                {
                    ToastService.ShowError(AppState["export.Error.files.message"], AppState["Loader"]);
                };

                Socket.SendHandler += () =>
                {
                    string json = JoinService.Serialize(data);
                    Socket.send(json);
                };
                await JoinService.ConnectSocketJS(Socket, "/export-grille");
                AppState.HideLoadingStatus();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

        protected async void RefreshPublication()
        {
            AppState.ShowLoadingStatus();
            try {
             bool val =   await GrilleService.RefreshPublication(EditorData.Item.GridListChangeHandler.Items.Select(it => it.GridId.Value).ToList());
                if (val)
                {
                    ToastService.ShowSuccess(AppState["refresh.publication.success"]);
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

        protected virtual Task<BrowserDataPage<P>> CustomSearchRows<P>(BrowserDataFilter filter)
        {
           return JoinService.SearchRows<P>(filter);
        }

        protected override async Task<BrowserDataPage<GridItem>> SearchRows(BrowserDataFilter filter)
        {
            filter.Join = EditorData.Item;
            BrowserDataPage<object[]> page = await CustomSearchRows<object[]>(filter);
            BrowserDataPage<GridItem> page_ = new();
            foreach (object[] row in page.Items)
            {
                if (row != null)
                {
                    page_.Items.Add(new GridItem(row));
                }
            }
            page_.CurrentPage = page.CurrentPage;
            page_.PageCount = page.PageCount;
            page_.PageFirstItem = page.PageFirstItem;
            page_.PageLastItem = page.PageLastItem;
            page_.PageSize = page.PageSize;
            page_.TotalItemCount = page.TotalItemCount;
            return page_;
        }

        protected override Task BuildFilter(BrowserDataFilter filter)
        {
            filter.ColumnFilters = null;
            filter.Criteria = null;
            ColumnFilter columnFilter2 = new ColumnFilter();
            foreach (var abstractNewGridDataItem in ColumnsDatas)
            {
                if (!string.IsNullOrWhiteSpace(abstractNewGridDataItem.ColumnFilters.Operation))
                {
                    bool isNull = string.IsNullOrWhiteSpace(abstractNewGridDataItem.ColumnFilters.Value) && abstractNewGridDataItem.ColumnFilters.Operation.Equals("IsNullOrEmpty");
                    bool isNotNull = string.IsNullOrWhiteSpace(abstractNewGridDataItem.ColumnFilters.Value) && abstractNewGridDataItem.ColumnFilters.Operation.Equals("NotIsNullOrEmpty");
                    bool isNullORisNotNull = string.IsNullOrWhiteSpace(abstractNewGridDataItem.ColumnFilters.Value) && (isNull || isNotNull);
                    if (!string.IsNullOrWhiteSpace(abstractNewGridDataItem.ColumnFilters.Value) || isNullORisNotNull)
                    {
                        JoinColumn column = GetItemsByName(abstractNewGridDataItem.ColumnName);
                        if (column != null)
                        {
                            abstractNewGridDataItem.ColumnFilters.dimensionType = column.Type;
                            abstractNewGridDataItem.ColumnFilters.dimensionId = column.DimensionId;
                            abstractNewGridDataItem.ColumnFilters.Name = column.DimensionName;
                            abstractNewGridDataItem.ColumnFilters.JoinColumnId = column.Id;
                        }
                        columnFilter2.Items.Add(abstractNewGridDataItem.ColumnFilters);
                    }
                }
            }
            if (columnFilter2.Items.Count > 1)
            {
                filter.ColumnFilters = new();
                filter.ColumnFilters.Link = " And ";
                filter.ColumnFilters.Operation = " And ";
                filter.ColumnFilters.Grouped = true;
                filter.ColumnFilters.Items.AddRange(columnFilter2.Items);
            }
            else
            {
                if (columnFilter2.Items.Count == 1)
                {
                    filter.ColumnFilters = columnFilter2.Items[0];
                }
            }
            return Task.CompletedTask;
        }

        JoinColumn GetItemsByName(string name)
        {
            foreach (var item in Items)
            {
                var ite = GetItemsByPosition(item.Position);
                if (ite != null)
                {
                    string NewName = ite.DimensionName + ite.DimensionId;
                    if (NewName.Equals(name))
                    {
                        return item;
                    }
                }
            }
            return null;
        }

        protected override string getPopupEditFormHeaderTextLabel()
        {
            return "EDIT.FORM.GRID";
        }

        protected override string ColumnWidth(int Position)
        {
            JoinColumn val = GetItemsByPosition(Position);
            if (val != null && val.Width.HasValue)
            {
                return $"{val.Width.Value}px";
            }
            return base.ColumnWidth(Position);
        }

        JoinColumn GetItemsByPosition(int position)
        {
            if (position >= 0 && Items.Count() > position)
            {
                return Items[position];
            }
            return null;
        }

        protected override object GetFieldValue(GridItem item, int JoinColumnPosition)
        {
                if (item != null)
                {
                    if (item.Datas == null)
                    {
                        item.Datas = new object[ItemsCount];
                    }
                    if (JoinColumnPosition >= 0 && item.Datas.Count() > JoinColumnPosition)
                    {
                        object value = item.Datas[JoinColumnPosition];
                        return value;
                    }                
                }
            return null;
        }

        protected override void SetFieldValue(GridItem item, int JoinColumnPosition, object value)
        {
                if (item != null)
                {
                    if (item.Datas == null)
                    {
                        item.Datas = new object[ItemsCount];
                    }
                    if (JoinColumnPosition >= 0 && item.Datas.Count() > JoinColumnPosition)
                    {
                        item.Datas[JoinColumnPosition] = value;
                    }
                }
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            if (obj == null)
            {
                return null;
            }
            try
            {
                DateTime dt = DateTime.ParseExact(obj.ToString(), format, CultureInfo.InvariantCulture);
                string value = dt.ToString(format);

                if (value == null || value.Contains("/0001"))
                {
                    DateTime.TryParse(obj.ToString(), out DateTime obj_);
                    value = obj_.ToString(format);
                }
                return value;
            }
            catch { }
            return obj.ToString();
        }

        protected override Task OnRowRemoving(GridItem dataItem)
        {
            return Task.CompletedTask;
        }

        protected override  Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            return Task.CompletedTask;
        }

        protected override  Task OnRowUpdating(GridItem dataItem, GridItem newValues)
        {
            return Task.CompletedTask;
        }

        protected override Task OnRowInserting(GridItem newValues)
        {
            return Task.CompletedTask;
        }

        protected override string KeyFieldName()
        {
            return "Id";
        }

        protected override string GridCssClass()
        {
            return $"custom-grid custom-grid-2 custom_-grid";
        }

        protected override object KeyFieldValue(GridItem item)
        {
            return GetFieldValue(item, ItemsCount);
        }
        protected override string NavLinkURI()
        {
            return "#";
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
                var column = GetItemsByPosition(Position);
                if (column == null)
                {
                    return null;
                }
                var item =  new JoinGridDataItem(column, Position);
                item.ColumnStyle += $"color:{column.Foregrounds} !important;";
                item.ColumnStyle += $"background-color:{column.Backgrounds} !important;";
                return item;
        }
    }

    public class JoinGridDataItem : AbstractNewGridDataItem
    {
        public JoinColumn Column;

        string Caption;

        public JoinGridDataItem(JoinColumn Column, int position) : base(position)
        {
            this.Column = Column;
        }

        public JoinGridDataItem(string Caption, int position) : base(position * (-1))
        {
            this.Column = null;
            this.Caption = Caption;
        }

        public override string CaptionName
        {
            get
            {
                if (Column == null)
                {
                    if (!string.IsNullOrWhiteSpace(Caption))
                    {
                        return Caption;
                    }
                    return "";
                }
                return Column.Name;
            }
        }

        public override string ColumnName
        {
            get
            {
                if (Column == null)
                {
                    return "";
                }
                return Column.DimensionName + Column.DimensionId;
            }
        }

        public override bool Visible
        {
            get
            {
                if (Column != null)
                {
                    return Column.Show;
                }
                else
                {
                    return true;
                }
            }
        }

        public override Type ColumnType
        {
            get
            {
                if (Column == null)
                {
                    if (!string.IsNullOrWhiteSpace(Caption))
                    {
                        return typeof(string);
                    }
                    return typeof(string);
                }
                if (Models.Filters.DimensionType.MEASURE.Equals(Column.Type))
                {
                    return typeof(decimal);
                }
                else
                       if (Models.Filters.DimensionType.PERIOD.Equals(Column.Type))
                {
                    return typeof(DateTime?);
                }
                else
                {
                    return typeof(string);
                }
            }
        }        

        public override string ColumnFormat
        {
            get
            {
                if (Column == null)
                {
                    return base.ColumnFormat;
                }
                if (Column.Format != null)
                {
                    if (Column.Type.IsMeasure())
                    {
                        return Column.Format.UsedSeparator ? "N" + Column.Format.NbrOfDecimal : "F" + Column.Format.NbrOfDecimal;
                    }
                    return Column.Format.DefaultFormat;
                }
                else
                {
                    return base.ColumnFormat;
                }
            }
        }
    }
}

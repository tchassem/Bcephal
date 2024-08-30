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
using Microsoft.AspNetCore.Components.Web;

namespace Bcephal.Blazor.Web.Sourcing.Shared.Grille
{
    public partial class InputNewGridComponent : AbstractNewGridComponent<Models.Grids.Grille, GridItem>
    {
        [Parameter] public virtual IGridItemService Service { get; set; }
        [Parameter] public List<string> AddColumns { get; set; } = new();
        [Parameter] public bool CanDisplayDeleteButton { get; set; } = true;
        [Inject] private WebSocketAddress WebSocketAddress { get; set; }
        [Inject] public GrilleService GrilleService { get; set; }
        [Parameter] public bool Editable { get; set; } = true;
        [Inject] public ModelService ModelService { get; set; }
        [Inject] public IToastService ToastService { get; set; }
        protected override int ItemsCount { get => Items.Count() + AddColumns.Count(); }
        private bool HasNewColomn => AddColumns.Count() > 0;
        public ObservableCollection<GrilleColumn> Items_ { get; set; }
        public ObservableCollection<string> AttributeValues { get; set; } = new();
        public ObservableCollection<decimal?> MeasureValues { get; set; } = new();
        public ObservableCollection<DateTime?> PeriodValues { get; set; } = new();

        [Parameter] public Action<BrowserDataFilter> DataFilter { get; set; }

        private int PositionPlus = 2;        
        private bool IsLoaded { get; set; } = false;
        private string GrilleExportFileName = "";
        private FileStream contentF;




        public ObservableCollection<GrilleColumn> Items
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
        protected override async void Duplicate(List<long> selectedItems)
        {
          await  GrilleService.Duplicate(selectedItems);
          await RefreshGrid_();
        }

        protected override async Task OnInitializedAsync()
        {
           
            IsNavLink = false;
            if (ItemsCount > 0 && EditorData.Item != null)
            {
                NewButtonVisible = EditorData.Item.Editable;
                EditButtonVisible = EditorData.Item.Editable;
                DeleteButtonVisible = EditorData.Item.Editable;
                CanRefreshAfterEdit = EditorData.Item.Editable;
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
            if (HasNewColomn)
            {
                PositionPlus += AddColumns.Count();
            }
            await base.OnInitializedAsync();
        }

        protected override RenderFragment<Action> GetEditData(GridDataColumnCellDisplayTemplateContext context, AbstractNewGridDataItem GridDataItem)
        {

            InputNewGridDataItem inputGridDataItem = (InputNewGridDataItem)GridDataItem;
            if (!inputGridDataItem.ShowExistingValue)
            {
                return null;
            }
            return GetCustomEditData((GridItem)context.DataItem, GridDataItem);
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
                            GrilleExportFileName = EditorData.Item.Name + ext;
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
                                        await JSRuntime.InvokeAsync<object>("saveAsFileBytes", $"{GrilleExportFileName}", Convert.ToBase64String(await stream.ReadAsByteArrayAsync()));
                                        //await JSRuntime.InvokeAsync<object>("saveAsFileBytes", $"{GrilleExportFileName}", Convert.ToBase64String(ReadBytes(contentF)));
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
                Console.WriteLine("try to download file");
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
                    string json = GrilleService.Serialize(data);
                    Socket.send(json);
                };
                await GrilleService.ConnectSocketJS(Socket, "/export-grille");
                AppState.HideLoadingStatus();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }


        public BrowserDataFilter getFilter()
        {
            return filter;
        }
        
        protected virtual Task<BrowserDataPage<object[]>> CustomSearchRows(BrowserDataFilter filter)
        {
            return Service.SearchRows(filter);
        }

        protected virtual Task<BrowserDataPage<P>> CustomSearchRows<P>(BrowserDataFilter filter)
        {
            return Service.SearchRows<P>(filter);
        }

        protected override async Task<BrowserDataPage<GridItem>> SearchRows(BrowserDataFilter filter)
        {
            filter.Grid = new Bcephal.Models.Grids.Grille(EditorData.Item, true);
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
            return Task.Run(() =>
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
                            GrilleColumn column = GetItemsByName(abstractNewGridDataItem.ColumnName);
                            if (column != null)
                            {
                                abstractNewGridDataItem.ColumnFilters.dimensionType = column.Type;
                                abstractNewGridDataItem.ColumnFilters.dimensionId = column.DimensionId;
                                abstractNewGridDataItem.ColumnFilters.Name = column.DimensionName;
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
                if (DataFilter != null)
                    {
                        DataFilter(filter);
                    }
            });
        }

        GrilleColumn GetItemsByName(string name)
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

        protected override string ColumnWidth(int Position) {
            GrilleColumn val = GetItemsByPosition(Position);
            if(val != null && val.Width.HasValue)
            {
                return $"{val.Width.Value}px";
            }
            return base.ColumnWidth(Position); 
        }
        GrilleColumn GetItemsByPosition(int position)
        {
            if (position >= 0 && Items.Count() > position)
            {
                return Items[position];
            }
            return null;
        }


        protected override object GetFieldValue(GridItem item, int grilleColumnPosition)
        {
            if (!HasNewColomn)
            {
                if (item != null)
                {
                    if (item.Datas == null)
                    {
                        item.Datas = new object[ItemsCount];
                    }
                    object value = item.Datas[grilleColumnPosition];
                    return value;
                }
            }
            else
            {
                if (grilleColumnPosition < AddColumns.Count())
                {
                    return item.Side;
                }
                else
                {
                    if (item != null)
                    {
                        if (item.Datas == null)
                        {
                            item.Datas = new object[ItemsCount];
                        }
                        object value = item.Datas[grilleColumnPosition - AddColumns.Count()];
                        return value;
                    }
                }
            }
            return null;
        }

        protected override void SetFieldValue(GridItem item, int grilleColumnPosition, object value)
        {
            if (!HasNewColomn)
            {
                if (item != null)
                {
                    if (item.Datas == null)
                    {
                        item.Datas = new object[ItemsCount];
                    }
                    item.Datas[grilleColumnPosition] = value;
                }
            }
            else
            {
                if (grilleColumnPosition < AddColumns.Count())
                {
                    return;
                }
                else
                {
                    if (item != null)
                    {
                        if (item.Datas == null)
                        {
                            item.Datas = new object[ItemsCount];
                        }
                        item.Datas[grilleColumnPosition - AddColumns.Count()] = value;
                    }
                }
            }
        }

        protected override async void OnSelectedDataItemChanged(object selection)
        {
            if (LastEditeItem != null && !LastEditeItem.Equals(selection))
            {
                if (LastEditeItem.IsInEditMode)
                {
                    var e = new KeyboardEventArgs();
                    e.Code = "Enter";
                    await EnterHandleValidSubmit(e, (GridItem)LastEditeItem);
                    await InvokeAsync(StateHasChanged);
                }
            }
            base.OnSelectedDataItemChanged(selection);
        }

        protected override async Task onClick_(MouseEventArgs ee)
        {
            if (LastEditeItem != null)
            {
                if (LastEditeItem.IsInEditMode)
                {
                    var e = new KeyboardEventArgs();
                    e.Code = "Enter";
                    await EnterHandleValidSubmit(e, (GridItem)LastEditeItem);
                }
            }
        }

        //protected override async Task onFocusLost(FocusEventArgs ee, GridCustomizeElementEventArgs e)
        //{
        //    GridItem selection = (GridItem)e.Grid.GetDataItem(e.VisibleIndex);
        //    if (selection.IsInEditMode)
        //    {
        //        var e1 = new KeyboardEventArgs();
        //        e1.Code = "Enter";            
        //        await EnterHandleValidSubmit(e1, selection);                
        //    }
        //}

        protected override Task OnRowRemoving(GridItem dataItem)
        {
            return Service.DeleteRows(new List<long>() { dataItem.GetId().Value });
        }
        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss =   ids.Select(obj => ((GridItem)obj).Id.Value).ToList();
                await Service.DeleteRows(idss);
            }
        }
        protected override async Task OnRowUpdating(GridItem dataItem, GridItem newValues)
        {
            List<GrilleEditedElement> datas = new List<GrilleEditedElement>();
            SetNewValues(datas, newValues, dataItem.GetId());
            GridItem GridItem = await Service.EditCells(datas);
        }
        protected override async Task OnRowInserting(GridItem newValues)
        {
            List<GrilleEditedElement> datas = new List<GrilleEditedElement>();
            SetNewValues(datas, newValues);
            GridItem GridItem = await Service.EditCells(datas);
        }

        private void SetNewValues(List<GrilleEditedElement> dataItem, GridItem newValues, long? id = null)
        {
            foreach (var grilleColumn in Items)
            {
                string columnName = grilleColumn.DimensionName + grilleColumn.DimensionId;

                GrilleEditedElement ite = new GrilleEditedElement();
                ite.Column = grilleColumn;
                ite.Grid = EditorData.Item;
                if (id.HasValue)
                {
                    ite.Id = id;
                }
                if (grilleColumn.IsAttribute)
                {
                    ite.StringValue = (string)newValues.Datas[grilleColumn.Position];
                }
                else
                    if (grilleColumn.IsMeasure)
                {
                    object els = newValues.Datas[grilleColumn.Position];
                    if (els != null && els is decimal?)
                    {
                        ite.DecimalValue = (decimal?)els;
                    }
                    else
                    if(els !=  null)
                    {
                        try
                        {
                            decimal.TryParse(els.ToString(), out decimal val);
                            ite.DecimalValue = val;
                        }
                        catch
                        {

                        }
                    }
                }
                else
                    if (grilleColumn.IsPeriod)
                {
                    object els = newValues.Datas[grilleColumn.Position];
                    if (els != null && els is DateTime?)
                    {
                        ite.DateValue = (DateTime?)els;
                    }
                    else
                    if (els != null)
                    {
                        try
                        {
                            DateTime.TryParse(els.ToString(), out DateTime val);
                            ite.DateValue = val;
                        }
                        catch
                        {

                        }
                    }
                }
                dataItem.Add(ite);
            }
        }

        protected override string KeyFieldName()
        {
            return "Id";
        }

        protected override object KeyFieldValue(GridItem item)
        {
            return GetFieldValue(item, ItemsCount);
        }
        protected override string NavLinkURI()
        {
            return "#";
        }

        protected override string GridCssClass()
        {
            return $"custom-grid custom-grid-2 custom_-grid";
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            if (!HasNewColomn)
            {
                var column = GetItemsByPosition(Position);
                if (column == null)
                {
                    return null;
                }
                var item =  new InputNewGridDataItem(column, Position);
                item.ColumnStyle += $"color:{column.Foregrounds} !important;";
                item.ColumnStyle += $"background-color:{column.Backgrounds} !important;";
                return item;
            }
            else
            {
                if ((Position - AddColumns.Count()) < 0)
                {
                    return new InputNewGridDataItem(AddColumns.ElementAt(Position), Position);
                }
                else
                {
                    var column = GetItemsByPosition(Position - AddColumns.Count());
                    if (column == null)
                    {
                        return null;
                    }
                    var item = new InputNewGridDataItem(column, Position);
                    item.ColumnStyle += $"color:{column.Foregrounds} !important;";
                    item.ColumnStyle += $"background-color:{column.Backgrounds} !important;";
                    return item;
                }
            }
        }
        protected async Task<System.Collections.Generic.IEnumerable<string>> GetAttributeValues(GrilleColumn column)
        {

            BrowserDataFilter browserDataFilter = new BrowserDataFilter();
            browserDataFilter.GroupId = column.DimensionId;
            if (column.Type.IsAttribute())
            {
                BrowserDataPage<string> attribs = await ModelService.SearchAttributeValues<string>(browserDataFilter);
                IEnumerable<string> attribs_ = attribs.Items;
                return attribs_;
            }
            return new List<string>();
        }

        protected async Task<System.Collections.Generic.IEnumerable<decimal?>> GetMeasureValues(GrilleColumn column)
        {
            try
            {
                BrowserDataFilter browserDataFilter = new BrowserDataFilter();
                browserDataFilter.GroupId = column.DimensionId;
                if (column.Type.IsMeasure())
                {
                    BrowserDataPage<decimal?> measures = await ModelService.SearchMeasureValues<decimal?>(browserDataFilter);
                    IEnumerable<decimal?> measures_ = measures.Items;
                    return measures_;
                }
            }catch(Exception ex)
            {
                Error.ProcessError(ex);
            }
            return new List<decimal?>();
        }
        protected async Task<System.Collections.Generic.IEnumerable<DateTime?>> GetPeriodValues(GrilleColumn column)
        {
            try
            {
                BrowserDataFilter browserDataFilter = new BrowserDataFilter();
                browserDataFilter.GroupId = column.DimensionId;
                if (column.Type.IsPeriod())
                {
                    BrowserDataPage<DateTime?> periods = await ModelService.SearchPeriodValues<DateTime?>(browserDataFilter);
                    IEnumerable<DateTime?> periods_ = periods.Items;
                    return periods_;
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            return new List<DateTime?>();
        }

    }





    public class InputNewGridDataItem : AbstractNewGridDataItem
    {
        public GrilleColumn Column;
        string Caption;

        public InputNewGridDataItem(GrilleColumn Column, int position) : base(position)
        {
            this.Column = Column;
        }

        public InputNewGridDataItem(string Caption, int position) : base(position * (-1))
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

        public bool ShowExistingValue
        {
            get

            {
                if (Column == null)
                {
                    return false;
                }
                return Column.ShowValuesInDropList;
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
        public override bool CanEditColumn
        {
            get
            {
                if (Column != null)
                {
                    return !Column.Editable;
                }
                return true;
            }
        }

        public override DataGridFixedStyle GridFixedStyle
        {
            get
            {
                if (Column != null)
                {
                    if (GrilleColumnFixedStyle.Left.Equals(Column.ColumnFixedStyle))
                    {
                        return DataGridFixedStyle.Left;
                    }
                    else
                    if (GrilleColumnFixedStyle.Right.Equals(Column.ColumnFixedStyle))
                    {
                        return DataGridFixedStyle.Right;
                    }
                }
                return DataGridFixedStyle.None;
            }
        }



    }
}

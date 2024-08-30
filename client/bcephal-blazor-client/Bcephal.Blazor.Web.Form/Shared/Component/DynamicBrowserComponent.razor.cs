using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Base;
using Bcephal.Models.Forms;
using Bcephal.Models.Grids;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using Bcephal.Models.Utils;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using System.Text.RegularExpressions;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Filters;
using Bcephal.Blazor.Web.Sourcing.Shared.Grille;
using Bcephal.Blazor.Web.Form.Services;

namespace Bcephal.Blazor.Web.Form.Shared.Component
{
    public partial  class DynamicBrowserComponent<P> : AbstractNewGridComponent<FormModel, P> where P : GridItem
    {
        [Inject]
        public FormDataService FormDataService { get; set; }
        [Inject]
        public IToastService ToastService { get; set; }

        protected override int ItemsCount { get => Items.Count(); }

        public ObservableCollection<GrilleColumn> Items_ { get; set; }

        string GrilleColumnCssStyle = "";
        private bool IsLoaded { get; set; } = false;

        public ObservableCollection<GrilleColumn> Items
        {
            get
            {
                if (EditorData != null && EditorData.Item != null && !IsLoaded)
                {
                    ObservableCollection<FormModelField> fields = EditorData.Item.FieldListChangeHandler.GetItems();
                    if (fields != null)
                    {
                        if (Items_ == null)
                        {
                            Items_ = new();
                        }
                        else
                        {
                            return Items_;
                        }
                        fields.BubbleSort();
                        foreach (var field in fields) {
                            GrilleColumn grilleColumn = new GrilleColumn();
                            grilleColumn.Id = field.ColumnId;
                            grilleColumn.DimensionId = field.DimensionId;
                            grilleColumn.DimensionName = field.DimensionName;
                            grilleColumn.Type = field.DimensionType.Value;
                            grilleColumn.Name = field.Label;
                            grilleColumn.Position = field.Position;
                            Items_.Add(grilleColumn);
                        }
                        IsLoaded = true;
                    }
                    else
                    {
                        Items_ = new();
                    }
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

        //protected override string GridCssClass()
        //{
        //    return $"custom-grid custom-grid-2 {headerClassStyle}";
        //}
        protected virtual string headerClassStyle { get; set; }
        private string CSSSheeteStyleToGridId { get; set; }

        //protected override async Task OnAfterRenderAsync(bool firstRender)
        //{
        //    await base.OnAfterRenderAsync(firstRender);
        //    if (firstRender)
        //    {
        //        await JSRuntime.InvokeVoidAsync("addCSSSheeteStyleToGrid", GrilleColumnCssStyle, CSSSheeteStyleToGridId);
        //    }

        //}

        //public override async ValueTask DisposeAsync()
        //{
        //    await JSRuntime.InvokeVoidAsync("removeCSSSheeteStyleToGrid", CSSSheeteStyleToGridId);
        //    await base.DisposeAsync();
        //}


        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            IsNavLink = true;
            if (EditorData != null)
            {
                headerClassStyle = Regex.Replace(EditorData.Item.Name, @"\W", "_");
                CSSSheeteStyleToGridId = Guid.NewGuid().ToString("d");
            }
            if (ItemsCount > 0)
            {
                NewButtonVisible = false;
                EditButtonVisible = false;
                DeleteButtonVisible = true;
                ClearFilterButtonVisible = true;
            }
            else
            {
                NewButtonVisible = false;
                ClearFilterButtonVisible = false;
            }
            AppState.CanExport = true;
            await Task.Delay(TimeSpan.FromSeconds(1.2)).ContinueWith(t => AppState.CanCreate = true);
        }
        //protected async override Task OnAddNewClick()
        //{
        //    try
        //    {
        //        await AppState.NavigateTo($"{Route.NEW_DYNAMIC_FORM}/{EditorData.Item.Id}");
        //    }
        //    catch (Exception ex)
        //    {
        //        Error.ProcessError(ex);
        //    }
        //}
        protected override P NewItem()
        {
            return (P)new GridItem(new object[ItemsCount]);
        }

        protected override async Task<BrowserDataPage<P>> SearchRows(BrowserDataFilter filter)
        {
            filter.GroupId = EditorData.Item.Id;
            BrowserDataPage<object[]> page = await FormDataService.Search(filter);
            foreach (object[] row in page.Items)
            {
                P rs = NewItem();
                rs.Datas = row;
                page_.Items.Add(rs);
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
            return Task.CompletedTask;
        }

        GrilleColumn GetItemsByName(string name)
        {
            foreach (var item in Items)
            {
                var ite = GetItemsByPosition(item.Position);
                string NewName = ite.DimensionName + ite.DimensionId;
                if (NewName.Equals(name))
                {
                    return item;
                }
            }
            return null;
        }

        protected override string getPopupEditFormHeaderTextLabel()
        {
            return "EDIT.FORM.GRID";
        }

        GrilleColumn GetItemsByPosition(int position)
        {
            if (position >= 0 && Items.Count() > position)
            {
                return Items[position];
            }
            return null;
        }

        protected override object GetFieldValue(P item, int grilleColumnPosition)
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
            return null;
        }
       
        protected override void SetFieldValue(P item, int grilleColumnPosition, object value)
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

        protected override  Task OnRowRemoving(P dataItem)
        {
            return FormDataService.Delete(new List<long>() { dataItem.GetId().Value });
        }
        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((P)obj).Id.Value).ToList();
                await FormDataService.Delete(idss);
            }
        }
        protected override  Task OnRowUpdating(P dataItem, P newValues)
        {
            List<GrilleEditedElement> datas = new List<GrilleEditedElement>();
            SetNewValues(datas, newValues, dataItem.GetId());
            // P GridItem = await FormDataService.EditCells(datas);
            return Task.CompletedTask;
        }
        protected override  Task OnRowInserting(P newValues)
        {
            List<GrilleEditedElement> datas = new List<GrilleEditedElement>();
            SetNewValues(datas, newValues);
            // P GridItem = await FormDataService.EditCells(datas);
            return Task.CompletedTask;
        }

        private void SetNewValues(List<GrilleEditedElement> dataItem, P newValues, long? id = null)
        {
            foreach (var grilleColumn in Items)
            {
                string columnName = grilleColumn.DimensionName + grilleColumn.DimensionId;

                GrilleEditedElement ite = new GrilleEditedElement();
                ite.Column = grilleColumn;
               // ite.Grid = EditorData.Item;
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
                }
                else
                    if (grilleColumn.IsPeriod)
                {
                    object els = newValues.Datas[grilleColumn.Position];
                    if (els != null && els is DateTime?)
                    {
                        ite.DateValue = (DateTime?)els;
                    }
                }
                dataItem.Add(ite);
            }
        }


        protected override string KeyFieldName()
        {
            return "Id";
        }

        protected override object KeyFieldValue(P item)
        {
            return item.Id;
        }
        protected override string NavLinkURI()
        {
            return $"{Route.NEW_DYNAMIC_FORM}/{EditorData.Item.Id}";
        }

        private string cssHeaderColumnNative { get; set; } = " header column. ";
       
        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
             var column = GetItemsByPosition(Position);
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

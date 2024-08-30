using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Forms;
using Bcephal.Models.Grids;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bcephal.Models.Utils;
using Microsoft.AspNetCore.Components;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Base;
using DevExtreme.AspNet.Data;

namespace Bcephal.Blazor.Web.Form.Shared.Component
{
  public partial  class SubFormModelGridBrowser : DynamicBrowserComponent<GridItemModel>
    {

        [Parameter]
        public Action<long, FormData> AddOrUpdateFormDataHandler { get; set; }
        [Parameter]
        public Action<long, FormData> DeleteFormDataHandler { get; set; }

        [Parameter]
        public Func<long?, long?, int, FormData> GetFormDataHandler { get; set; }

        [Parameter]
        public Func<long?, IEnumerable<FormData>> GetFormDatasHandler { get; set; }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            IsNavLink = false;
            
            if (ItemsCount > 0)
            {
                NewButtonVisible = false;
                EditButtonVisible = true;
                DeleteButtonVisible = true;
                DeleteAllButtonVisible = false;
                ClearFilterButtonVisible = true;
                CanRefresh = false;
                CanCreate = false;
            }
            else
            {
                NewButtonVisible = true;
                ClearFilterButtonVisible = false;
            }
            ShowFilterRow = false;
            // to show all items
            page_PageSize = -1;
            ShowPager = false;
            ShowsAll_ = false;
            PageSizeSelector = false;
            AppState.CanExport = true;
        }

        protected override string getPopupEditFormHeaderTextLabel()
        {
            return "EDIT.FORM.GRID";
        }

        protected override string KeyFieldName()
        {
            return "Position";
        }

        protected override object KeyFieldValue(GridItemModel item)
        {
            return item.Position;
        }


        protected override Task OnRowUpdating(GridItemModel dataItem, GridItemModel newValues)
        {
            return AddOrUpdateData(dataItem,newValues);
        }
        protected override Task OnRowInserting(GridItemModel newValues)
        {
            //return AddOrUpdateData(newValues);
            return Task.CompletedTask;
        }

        protected override object GetFieldValue(GridItemModel item, int grilleColumnPosition)
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

        protected override GridItemModel NewItem()
        {
            return new GridItemModel(new object[ItemsCount]);
        }

        private IEnumerable<GridItemModel> GridItems()
        {
            ObservableCollection<FormModelField> fields = EditorData.Item.FieldListChangeHandler.GetItems();
            fields.BubbleSort();
            IEnumerable<FormData> items = GetFormDatasHandler?.Invoke(EditorData.Item.Id);
            ObservableCollection<GridItemModel> results = new ObservableCollection<GridItemModel>();
            int offset = 0;
            foreach (var item in items)
            {
                GridItemModel element = new GridItemModel();
                element.Datas = new object[fields.Count];
                foreach (var el in fields)
                {
                    try
                    {
                        item.Datas.TryGetValue(el.Id, out FormDataValue dataValue);
                        if (dataValue != null)
                        {
                            element.Datas[el.Position] = dataValue.GetValue(el.DimensionType.Value);
                        }
                    }
                    catch { }
                }
                results.Add(element);
                element.Position = results.Count;
                offset++;
                item.Position = offset;
            }
            return results;
        }

        private async Task AddOrUpdateData(GridItemModel dataItem, GridItemModel newValues)
        {
            ObservableCollection<FormModelField> fields = EditorData.Item.FieldListChangeHandler.GetItems();
            fields.BubbleSort();
            FormData formData = GetFormDataHandler?.Invoke(EditorData.Item.Id.Value, dataItem.Id, dataItem.Position.Value);
            foreach (var field in fields)
            {
                object value = GetFieldValue(newValues, field.Position);
                if (value != null)
                {
                    FormDataValue formDataValue = new FormDataValue();
                    formDataValue.SetValue(field.DimensionType.Value, value);
                    dataItem.Datas[field.Position] = formDataValue.GetValue(field.DimensionType.Value);
                    AddOrUpdateFromData(formData, field.Id.Value, formDataValue);
                }
            }
            AddOrUpdateFormDataHandler?.Invoke(EditorData.Item.Id.Value, formData);
            await RefreshGrid_();
        }

        private void AddOrUpdateFromData(FormData formData, long key, FormDataValue value)
        {
            formData.Datas.Remove(key);
            formData.Datas.Add(key, value);
        }
        protected override  Task OnRowRemoving(GridItemModel dataItem)
        {
            FormData formData = GetFormDataHandler?.Invoke(EditorData.Item.Id.Value, dataItem.Id, dataItem.Position.Value);
            DeleteFormDataHandler.Invoke(EditorData.Item.Id.Value, formData);
            return Task.CompletedTask;
        }
        protected override string NavLinkURI()
        {
            return "#";
        }
    }

    public class GridItemModel : GridItem
    {
      public  int? Position { get; set; }
        public GridItemModel() : base() {  }
        public GridItemModel(Object[] datas, String side = null) : base(datas, side) { }
    }
}

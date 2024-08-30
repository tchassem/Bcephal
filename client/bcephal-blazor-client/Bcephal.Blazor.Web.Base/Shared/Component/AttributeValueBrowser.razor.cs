using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using DevExpress.Blazor;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Base.Services;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class AttributeValueBrowser : AbstractNewGridComponent<Models.Grids.Grille, BrowserData>
    {
       
        [Parameter]
        public List<Models.Dimensions.Attribute> Attributes { get; set; }

        [Parameter]
        public long? AttributeId { get; set; }

        [Parameter]
        public string SelectedValue { get; set; }

        [Parameter]
        public BrowserData SelectedItem_ { get; set; }

        [Parameter]
        public EventCallback<BrowserData> SelectedItem_Changed { get; set; }

        [Parameter]
        public List<BrowserData> SelectedItemList { get; set; }

        [Parameter]
        public EventCallback<List<BrowserData>> SelectedItemListChanged { get; set; }


        [Inject]
        public ModelService ModelService { get; set; }

      
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="50px", ColumnName = nameof(BrowserData.Name), ColumnType = typeof(string)},
                    };

        protected override int ItemsCount => GridColumns.Length;
        private object GetPropertyValue(BrowserData item, string propName)
        {
            return item.GetType().GetProperty(propName).GetValue(item, null);
        }
        protected override void OnInitialized()
        {
            base.OnInitialized();
            IsNavLink = false;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            AppState.CanCreate = false;
            DeleteButtonVisible = false;
            DeleteAllButtonVisible = false;
            SelectionMode = DevExpress.Blazor.GridSelectionMode.Single;
            CurrentEditMode = DevExpress.Blazor.GridEditMode.PopupEditForm;
            CanCreate = false;
        }
        public override ValueTask DisposeAsync()
        {       
            AppState.CanCreate = false;
            AppState.CanRefresh = false;
            return base.DisposeAsync();
        }

        protected override object GetFieldValue(BrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(BrowserData.Id);
        }

        protected override object KeyFieldValue(BrowserData item)
        {
            return item.Id;
        }

        protected override Task OnRowInserting(BrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override Task OnRowUpdating(BrowserData dataItem, BrowserData  newValues)
        {

            return Task.CompletedTask;
        }

        protected  async Task<BrowserDataPage<BrowserData>> SearchRows_(BrowserDataFilter filter, BrowserDataPage<BrowserData> page_)
        { 
  
            filter.GroupId = AttributeId;
            BrowserDataPage<string> page = await ModelService.SearchAttributeValues<string>(filter);
            List<string> selectedItems = new List<string>();

            if(SelectedItemList != null && SelectedItemList.Any())
            {
                SelectedItemList.ForEach(x => selectedItems.Add(x.Name));
            }
            
            int pos = 0;
            foreach (string str in page.Items)
            {
                if (!selectedItems.Contains(str))
                {
                   page_.Items.Add(new BrowserData() { Name = str, Id = pos++ });
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

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override void OnSelectedDataItemChanged(object selectedDataItem)
        {
            SelectedItem_ = selectedDataItem as BrowserData;
            SelectedItem_Changed.InvokeAsync(SelectedItem_);
        }


        protected override async Task<BrowserDataPage<BrowserData>>  SearchRows(BrowserDataFilter filter)
        {
            BrowserDataPage<BrowserData> page_ = new BrowserDataPage<BrowserData>();
            if (AttributeId.HasValue)
            {
                 page_ = await SearchRows_(filter, new BrowserDataPage<BrowserData>());
            }
            
            return page_;
            
        }


        [Parameter]
        public Action<GridRowClickEventArgs> OndbClickRow { get; set; }

        protected override void RowDoubleClick(GridRowClickEventArgs args)
        {
              OndbClickRow?.Invoke(args);
        }

        protected override Task OnRowRemoving(BrowserData dataItem)
        {
            return Task.CompletedTask;
        }

        protected override Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            return Task.CompletedTask;
        }

        protected override string NavLinkURI()
        {
            return null;
        }


    }
 
}

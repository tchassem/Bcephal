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
using Bcephal.Models.Loaders;
using System.Collections.ObjectModel;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.MultipleFileLoader
{
    public partial class LoaderCreatedDimension : AbstractNewGridComponent<Models.Loaders.FileLoaderColumn, Models.Loaders.FileLoaderColumn>
    {
        [Parameter]
        public ObservableCollection<FileLoaderColumn> FileLoaderColumns { get; set; }
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="50px", ColumnName = nameof(Models.Loaders.FileLoaderColumn.FileColumn), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Type"] ,ColumnWidth="50px", ColumnName = nameof(Models.Loaders.FileLoaderColumn.Type), ColumnType = typeof(string)},
                    };

        protected override int ItemsCount => GridColumns.Length;
        private object GetPropertyValue(Models.Loaders.FileLoaderColumn item, string propName)
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
            CanShowSelectionColumn = false;
            ShowAllRows = true;
        }
        public override ValueTask DisposeAsync()
        {
            AppState.CanCreate = false;
            AppState.CanRefresh = false;
            return base.DisposeAsync();
        }

        protected override object GetFieldValue(Models.Loaders.FileLoaderColumn item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(BrowserData.Id);
        }

        protected override object KeyFieldValue(Models.Loaders.FileLoaderColumn item)
        {
            return item.Id;
        }

        protected override Task OnRowInserting(Models.Loaders.FileLoaderColumn newValues)
        {
            return Task.CompletedTask;
        }

        protected override Task OnRowUpdating(Models.Loaders.FileLoaderColumn dataItem, Models.Loaders.FileLoaderColumn newValues)
        {

            return Task.CompletedTask;
        }

      

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

      

        protected override  Task<BrowserDataPage<FileLoaderColumn>> SearchRows(BrowserDataFilter filter)
        {
            BrowserDataPage<FileLoaderColumn> page_ = new BrowserDataPage<FileLoaderColumn>();
            if(FileLoaderColumns != null && FileLoaderColumns.Any())
            {
                IEnumerable<FileLoaderColumn>  FileLoaderColumns_ = FileLoaderColumns.Skip((filter.Page - 1)* filter.PageSize).Take(filter.PageSize);
                foreach (Models.Loaders.FileLoaderColumn fileLoaderColumn in FileLoaderColumns_)
                {
                    page_.Items.Add(fileLoaderColumn);
                }
            }
            page_.CurrentPage = filter.Page;
            page_.PageSize = filter.PageSize;
            page_.TotalItemCount = FileLoaderColumns.Count;
            return Task.FromResult(page_);
        }

      

        protected override async void RowDoubleClick(GridRowClickEventArgs args)
        {
            await Task.CompletedTask;
        }

        protected override Task OnRowRemoving(Models.Loaders.FileLoaderColumn dataItem)
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

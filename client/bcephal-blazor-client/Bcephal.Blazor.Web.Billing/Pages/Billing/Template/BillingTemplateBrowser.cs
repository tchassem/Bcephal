using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using DevExtreme.AspNet.Data;
using Bcephal.Models.Billing;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Billing.Invoices;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Template
{
    //[RouteAttribute("/template/list")]
    public class BillingTemplateBrowser : AbstractGridComponent<BillTemplate, BillingTemplateBrowserData>, IDisposable
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="15%",  ColumnName = nameof(BrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Group"] ,ColumnWidth="10%", ColumnName = nameof(BrowserData.Group), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Code"] ,ColumnWidth="10%", ColumnName = nameof(BillingTemplateBrowserData.Code), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Repository"] ,ColumnWidth="10%", ColumnName = nameof(BillingTemplateBrowserData.Repository), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CreationDate"],ColumnWidth="auto", ColumnName = nameof(BrowserData.CreationDateTime), ColumnType = typeof(DateTime)},
                        new {CaptionName = AppState["ModificationDate"] ,ColumnWidth="auto", ColumnName = nameof(BrowserData.ModificationDateTime), ColumnType = typeof(DateTime)},
                        new {CaptionName = AppState["VisibleInShortcut"] ,ColumnWidth="10%", ColumnName = nameof(BrowserData.VisibleInShortcut), ColumnType = typeof(bool)},
                       
                    };

        protected override int ItemsCount => GridColumns.Length;

        [Inject]
        public BillingTemplateService Templateservice { get; set; }

        private BrowserDataPage<BillTemplate> pages { get; set; }

        public void Dispose()
        {
            AppState.Hander = null;
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.BILLING_TEMPLATE;
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.BillingTemplateCreateAllowed)
            {
                await Task.Delay(TimeSpan.FromSeconds(1.2)).ContinueWith(t => AppState.CanCreate = true && !AppState.IsDashboard);
            }
        }

        protected override BillingTemplateBrowserData NewItem()
        {
            return null;
        }

        protected override string KeyFieldName()
        {
            return nameof(BrowserData.Id);
        }
        
        protected override object KeyFieldValue(BillingTemplateBrowserData item)
        {
            return item.Id;
        }
        protected override string NavLinkURI()
        {
            return Route.BILLING_TEMPLATE;
        }
        //protected override string GetColumnWidth(int position)
        //{
        //    return GridColumns[position].ColumnWidth;
        //}
        protected override Task OnRowInserting(Dictionary<string, object> newValues)
        {
            return Task.CompletedTask;
        }


        protected override async Task SearchRows(BrowserDataFilter filter, BrowserDataPage<BillingTemplateBrowserData> page_, DataSourceLoadOptionsBase options)
        {
            BrowserDataPage<BillingTemplateBrowserData> page = await Templateservice.Search(filter);
            foreach (BillingTemplateBrowserData row in page.Items)
            {
                page_.Items.Add(row);
            }
            page_.CurrentPage = page.CurrentPage;
            page_.PageCount = page.PageCount;
            page_.PageFirstItem = page.PageFirstItem;
            page_.PageLastItem = page.Items.Count;
            page_.PageSize = page.Items.Count; ;
            page_.TotalItemCount = page.TotalItemCount;
        
        }

        protected async override Task OnAddNewClick()
        {
            try
            {
                await AppState.NavigateTo(NavLinkURI());
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

        protected override async Task OnRowRemoving(BillingTemplateBrowserData dataItem)
        {
            await Templateservice.Delete(new List<long>() { dataItem.Id.Value });
        }

        protected override async Task OnRowRemoving(List<long> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                await Templateservice.Delete(ids);
            }
        }

        protected override Task OnRowUpdating(BillingTemplateBrowserData dataItem, Dictionary<string, object> newValues)
        {
            string link = NavLinkURI();
            return Task.CompletedTask;
        }

        protected override AbstractGridDataItem GetGridDataItem(int Position)
        {
            return new GridDataItem(GridColumns[Position], Position);
        }
        //protected override string GetColumnNameAt(int position)
        //{
        //    return GridColumns[position].ColumnName;
        //}

        //protected override string GetCaptionNameAt(int position)
        //{
        //    return GridColumns[position].CaptionName;
        //}

        //protected override Type GetColumnTyepAt(int position)
        //{
        //    return GridColumns[position].ColumnType;
        //}

        protected override object GetFieldValue(BillingTemplateBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

     
        private object GetPropertyValue(BrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }


    }

}

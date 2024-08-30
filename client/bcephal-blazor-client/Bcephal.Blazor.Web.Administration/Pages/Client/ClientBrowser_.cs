using Bcephal.Blazor.Web.Administration.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Clients;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Pages.Project.Client
{
   // [RouteAttribute("/client-browser")]
    public class ClientBrowser_ : AbstractNewGridComponent<Bcephal.Models.Clients.Client, Bcephal.Models.Clients.Client>
    {

        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="100px",  ColumnName = nameof(Models.Clients.Client.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Status"] ,ColumnWidth="100px",  ColumnName = nameof(Models.Clients.Client.Status), ColumnType = typeof(string)},
                       new {CaptionName = AppState["CreationDate"],ColumnWidth="100px", ColumnName = nameof(BrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDate"] ,ColumnWidth="100px", ColumnName = nameof(BrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["Default.Client"] ,ColumnWidth="100px",  ColumnName = nameof(Models.Clients.Client.DefaultClient), ColumnType = typeof(bool)},
                      };
        [Inject]
        public ClientService clientService { get; set; }

        public virtual ClientService GetService()
        {
            return clientService;
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.CLIENT_FORM;
            IsNavLink = true;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = true;
            //if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.AdministrationClientCreateAllowed && AppState.PrivilegeObserver.AdministrationCreateAllowed)
            //{
                await Task.Delay(TimeSpan.FromSeconds(1.2)).ContinueWith(t => AppState.CanCreate = true && !AppState.IsDashboard);
            //}
            AppState.CanRefresh = true;

        }

        public override async ValueTask DisposeAsync()
        {

            AppState.CreateHander = null;
            //if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.AdministrationClientCreateAllowed && AppState.PrivilegeObserver.AdministrationCreateAllowed)
            //{
                AppState.CanCreate = false;
            //}
            await base.DisposeAsync();
        }

        protected override int ItemsCount => GridColumns.Length;
        private object GetPropertyValue(Models.Clients.Client obj, string propName)
        {
             return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }
       

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override object GetFieldValue(Models.Clients.Client item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }
        protected override string KeyFieldName()
        {
            return nameof(Bcephal.Models.Clients.Client.Id);
        }

        protected override object KeyFieldValue(Models.Clients.Client item)
        {
            return item.Id;
        }
        protected override string NavLinkURI()
        {
            return Route.CLIENT_FORM;
        }

        protected override Task OnRowInserting(Models.Clients.Client newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(Models.Clients.Client dataItem)
        {
            await GetService().Delete(new List<long>() { dataItem.Id.Value });
        }
        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((Models.Clients.Client)obj).Id.Value).ToList();
                await GetService().Delete(idss);
            }
        }

        protected async override Task OnRowUpdating(Models.Clients.Client dataItem, Models.Clients.Client newValues)
        {
            try
            {
                string link = NavLinkURI();
                if (link.Trim().EndsWith("/"))
                {
                    link += dataItem.Id;
                }
                else
                {
                    link += "/" + dataItem.Id;
                }
              await AppState.NavigateTo(link);
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

        protected override  Task<BrowserDataPage<Models.Clients.Client>> SearchRows(BrowserDataFilter filter)
        {
            return  GetService().Search(filter);
        }
        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);
        }
    }
}


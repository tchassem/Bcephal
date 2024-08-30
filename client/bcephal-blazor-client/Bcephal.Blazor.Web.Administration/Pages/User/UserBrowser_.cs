using Bcephal.Blazor.Web.Administration.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Administration.Pages.User
{
   // [RouteAttribute("/user-browser")]
    public class UserBrowser_ : AbstractNewGridComponent<Bcephal.Models.Users.User, Bcephal.Models.Users.User>
    {
        

            protected dynamic[] GridColumns => new[] {
                         new {CaptionName = AppState["User.name"] ,ColumnWidth="100px",  ColumnName = nameof(Models.Users.User.username), ColumnType = typeof(string)},
                        new {CaptionName = AppState["FirstName"] ,ColumnWidth="100px",  ColumnName = nameof(Models.Users.User.FirstName), ColumnType = typeof(string)},
                        new {CaptionName = AppState["LastName"] ,ColumnWidth="100px",  ColumnName = nameof(Models.Users.User.lastName), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Email"] ,ColumnWidth="100px",  ColumnName = nameof(Models.Users.User.email), ColumnType = typeof(string)},
                         new {CaptionName = AppState["CreationDate"] ,ColumnWidth="100px", ColumnName = nameof(BrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                         new {CaptionName = AppState["ModificationDate"],ColumnWidth="100px", ColumnName = nameof(BrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                       
                       };
            [Inject]
            public UsersService UsersService { get; set; }
            public bool Editable => AppState.PrivilegeObserver.CanCreatedAdministrationUser ;
            public virtual UsersService GetService()
            {
                return UsersService;
            }
            protected override async Task OnInitializedAsync()
            {
                await base.OnInitializedAsync();
                EditorRoute = Route.USER_FORM;
                ClearFilterButtonVisible = false;
                DeleteButtonVisible = true;
               AppState.CanRefresh = true && !AppState.IsDashboard;
               AppState.CanCreate = Editable && !AppState.IsDashboard; 
               IsNavLink = true; ;
            }

            public override async ValueTask DisposeAsync()
            {
                AppState.CreateHander = null;
                AppState.CanCreate = false;
                await base.DisposeAsync();
            }

            protected override int ItemsCount => GridColumns.Length;


            private object GetPropertyValue(Models.Users.User obj, string propName)
            {
                return obj.GetType().GetProperty(propName).GetValue(obj, null);
            }

            protected override AbstractNewGridDataItem GetGridDataItem(int Position)
            {
                return new NewGridDataItem(GridColumns[Position], Position);
            }

            protected override object GetFieldValue(Models.Users.User item, int grilleColumnPosition)
            {
                return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);

            }
            protected override string KeyFieldName()
            {
                return nameof(Bcephal.Models.Users.User.Id);
            }

            protected override object KeyFieldValue(Models.Users.User item)
            {
                return item.Id;
            }
            protected override string NavLinkURI()
            {
                return Route.USER_FORM;
            }

            protected override Task OnRowInserting(Models.Users.User newValues)
            {
                return Task.CompletedTask;
            }

            protected override Task OnRowRemoving(Models.Users.User dataItem)
            {
                return GetService().Delete(new List<long>() { dataItem.Id.Value });
            }
            protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((Models.Users.User)obj).Id.Value).ToList();
                await GetService().Delete(idss);
            }
        }

        protected async override Task OnRowUpdating(Models.Users.User dataItem, Models.Users.User newValues)
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

            protected override Task<BrowserDataPage<Models.Users.User>> SearchRows(BrowserDataFilter filter)
            {
                return GetService().Search(filter);
            }
            protected override string FormatDateCellValue(string format, Object obj)
            {
                return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);
            }
        }
    }


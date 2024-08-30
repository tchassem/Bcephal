using Bcephal.Blazor.Web.Administration.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Profiles;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Administration.Pages.Profile
{
    
    
    public class ProfileBrowser : AbstractNewGridComponent<Models.Profiles.Profile, ProfileBrowserData>
    {

        [Inject]
        public ProfileService ProfileService { get; set; }

        protected dynamic[] GridColumns => new[] {
            new {CaptionName = AppState["Name"], ColumnWidth="15%", ColumnName = nameof(ProfileBrowserData.Name), ColumnType = typeof(string)},
            new {CaptionName = AppState["Description"], ColumnWidth="35%", ColumnName = nameof(ProfileBrowserData.Description), ColumnType = typeof(string)},
            new {CaptionName = AppState["CreationDate"], ColumnWidth="auto", ColumnName = nameof(ProfileBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
            new {CaptionName = AppState["ModificationDate"], ColumnWidth="auto", ColumnName = nameof(ProfileBrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
        };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(ProfileBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.PROFIL_EDIT;
            IsNavLink = true;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = true;
            EditButtonVisible = false;
            AppState.CanRun = false;
            //if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.AdministrationProfileCreateAllowed && AppState.PrivilegeObserver.AdministrationCreateAllowed)
            //{
                await Task.Delay(TimeSpan.FromSeconds(1.2)).ContinueWith(t => AppState.CanCreate = true && !AppState.IsDashboard);
            //}
            AppState.CanRefresh = true && !AppState.IsDashboard;
        }
        public override async ValueTask DisposeAsync()
        {
            AppState.CreateHander = null;
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.AdministrationProfileCreateAllowed && AppState.PrivilegeObserver.AdministrationCreateAllowed)
            {
                AppState.CanCreate = false;
            }
            await base.DisposeAsync();
        }
        protected override object GetFieldValue(ProfileBrowserData item, int grilleColumnPosition)
        {
            return item != null ? GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName) : null;
        }

        protected override string KeyFieldName()
        {
            return nameof(BrowserData.Id);
        }
        protected override object KeyFieldValue(ProfileBrowserData item)
        {
            return item != null ? item.Id : null;
        }

        protected override string NavLinkURI()
        {
            return Route.PROFIL_EDIT;
        }

        protected override Task OnRowInserting(ProfileBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override Task OnRowRemoving(ProfileBrowserData dataItem)
        {
            return ProfileService.Delete(new List<long>() { dataItem.Id.Value });
        }
        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((ProfileBrowserData)obj).Id.Value).ToList();
                await ProfileService.Delete(idss);
            }
        }

        protected async override Task OnRowUpdating(ProfileBrowserData dataItem, ProfileBrowserData newValues)
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

        protected override  Task<BrowserDataPage<ProfileBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            return ProfileService.Search(filter);
        }
        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }
        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);
        }
    }
}

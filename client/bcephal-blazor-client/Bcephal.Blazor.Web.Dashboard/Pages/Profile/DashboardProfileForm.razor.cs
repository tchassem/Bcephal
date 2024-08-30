using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Dashboard.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Bcephal.Models.Profiles;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Pages.Profile
{
    public partial class DashboardProfileForm : ComponentBase
    {
        [Inject]
        public DashboardProfileService DashboardProfileService { get; set; }

        [Inject]
        public AppState AppState { get; set; }

        bool IsXSmallScreen { get; set; }

        [Parameter]
        public string CssClass { get; set; } = "w-100 m-0 p-0";

        [Parameter]
        public string ItemSpacing { get; set; } = "0px";

        [Parameter]
        public long? ProfId { get; set; }

        public long? DefaultDashId { get; set; }

        DxContextMenu ContextMenu { get; set; }

        [Parameter]
        public DeviceSize DeviceSize_ { get; set; } = DeviceSize.XSmall | DeviceSize.Small | DeviceSize.Medium;

        [Parameter]
        public EventCallback<DashboardProfileEditorData> GetDashboardProfilesCallback { get; set; }

        public DashboardProfileEditorData DashboardProfileEditorData { get; set; }

        bool ShowCheckboxes { get; set; } = true;

        IEnumerable<Nameable> AddingProfiles { get; set; }
        IEnumerable<ProfileDashboard> DeletingProfiles { get; set; }

        protected override async Task OnInitializedAsync()
        {
            AddingProfiles = new List<Nameable>() { }.AsEnumerable();
            DeletingProfiles = new List<ProfileDashboard>() { }.AsEnumerable();
            DashboardProfileEditorData = new DashboardProfileEditorData();
            await Task.CompletedTask;
        }


        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                DashboardProfileEditorData = await DashboardProfileService.GetProfiles(ProfId);
                StateHasChanged();
            }
            await Task.CompletedTask;
        }

        public void SelectAllAdding(MouseEventArgs evt)
        {
            this.AddingProfiles = new List<Nameable>(DashboardProfileEditorData.Profiles);
            StateHasChanged();
        }

        public void HandlerAddingProfiles(MouseEventArgs evt)
        {
            bool hasAdded = false;
            foreach (Nameable item in AddingProfiles)
            {
                ProfileDashboard ProfileItem = DashboardProfileEditorData.ItemListChangeHandler.Items.Where(p => p.DashboardId == item.Id).ToList().FirstOrDefault();
                if (ProfileItem == null)
                {
                    DashboardProfileEditorData.AddItem(new ProfileDashboard(item));
                    if (!hasAdded)
                    {
                        hasAdded = !hasAdded;
                    }
                }
            }
            AddingProfiles = new List<Nameable>();
            StateHasChanged();
            if (hasAdded)
            {
                GetDashboardProfilesCallback.InvokeAsync(DashboardProfileEditorData);
            }
        }

        public void SelectAllDeleting(MouseEventArgs evt)
        {
            DeletingProfiles = new List<ProfileDashboard>(DashboardProfileEditorData.ItemListChangeHandler.Items);
            StateHasChanged();
        }

        public void HandlerRemovingProfiles(MouseEventArgs evt)
        {
            if (DeletingProfiles != null && DeletingProfiles.Count() > 0)
            {
                foreach (ProfileDashboard item in DeletingProfiles)
                {
                    DashboardProfileEditorData.DeleteOrForgetItem(item);
                }
                GetDashboardProfilesCallback.InvokeAsync(DashboardProfileEditorData);
                DeletingProfiles = new List<ProfileDashboard>();
            };
            StateHasChanged();
        }

        public async Task OnContextMenu(MouseEventArgs e, ProfileDashboard Item)
        {
            DefaultDashId = Item.DashboardId;
            await ContextMenu.ShowAsync(e);
        }

        void OnItemClick(ContextMenuItemClickEventArgs args)
        {
            foreach (ProfileDashboard d in DashboardProfileEditorData.ItemListChangeHandler.Items)
            {
                if (d.DashboardId != DefaultDashId)
                {
                    if (d.DefaultDashboard)
                    {
                        d.DefaultDashboard = false;
                        DashboardProfileEditorData.UpdateItem(d);
                        GetDashboardProfilesCallback.InvokeAsync(DashboardProfileEditorData);
                    }
                }
                else
                {
                    d.DefaultDashboard = true;
                    DashboardProfileEditorData.UpdateItem(d);
                    GetDashboardProfilesCallback.InvokeAsync(DashboardProfileEditorData);
                }
            }
            StateHasChanged();
        }

    }
}

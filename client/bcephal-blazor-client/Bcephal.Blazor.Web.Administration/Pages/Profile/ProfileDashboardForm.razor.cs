using Bcephal.Blazor.Web.Administration.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
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

namespace Bcephal.Blazor.Web.Administration.Pages.Profile
{
    public partial class ProfileDashboardForm : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        bool IsXSmallScreen { get; set; }

        [Parameter]
        public string CssClass { get; set; } = "w-100 m-0 p-0";

        [Parameter]
        public string ItemSpacing { get; set; } = "0px";

        [Parameter]
        public long? ProfId { get; set; }

        [Parameter]
        public bool ShowModal { get; set; } = false;

        [Parameter]
        public string Title { get; set; }

        BaseModalComponent ModalPro { get; set; }

        public long? DefaultDashId { get; set; }

        DxContextMenu ContextMenu { get; set; }

        [Parameter]
        public DeviceSize DeviceSize_ { get; set; } = DeviceSize.XSmall | DeviceSize.Small | DeviceSize.Medium;

        [Parameter]
        public EventCallback<bool> ShowModalChanged { get; set; }

        [Parameter]
        public EventCallback<ProfileDashboardEditorData> ProfileDashboardEditorDataChanged { get; set; }

        [Parameter]
        public ProfileDashboardEditorData ProfileDashboardEditorData { get; set; }
        public ProfileDashboardEditorData CurrentProfileDashboard { get; set; }

        bool ShowCheckboxes { get; set; } = true;

        IEnumerable<Nameable> AddingDashboards { get; set; }

        IEnumerable<ProfileDashboard> DeletingDashboards { get; set; }

        protected override async Task OnParametersSetAsync()
        {
            if (ProfileDashboardEditorData != null) {
                CurrentProfileDashboard.Dashboards = ProfileDashboardEditorData.Dashboards;
                ResetFields();
            }
            await base.OnParametersSetAsync();
        }

        protected override async Task OnInitializedAsync()
        {
            AddingDashboards = new List<Nameable>() { }.AsEnumerable();
            DeletingDashboards = new List<ProfileDashboard>() { }.AsEnumerable();
            CurrentProfileDashboard = new ProfileDashboardEditorData();

            await base.OnInitializedAsync();
        }

        public void SelectAllAdding(MouseEventArgs evt)
        {
            AddingDashboards = new List<Nameable>(CurrentProfileDashboard.Dashboards);
            StateHasChanged();
        }

        public void HandlerAddingDashboards(MouseEventArgs evt)
        {
            foreach (Nameable item in AddingDashboards)
            {
                ProfileDashboard ProfileItem = CurrentProfileDashboard.ItemListChangeHandler.Items.Where(p =>p.DashboardId == item.Id).ToList().FirstOrDefault();
                if (ProfileItem == null)
                {
                    CurrentProfileDashboard.AddItem(new ProfileDashboard(item));
                }
            }
            AddingDashboards = new List<Nameable>();
        }

        public void SelectAllDeleting(MouseEventArgs evt)
        {
            DeletingDashboards = new List<ProfileDashboard>(CurrentProfileDashboard.ItemListChangeHandler.Items);
            StateHasChanged();
        }

        public void HandlerRemovingDashboards(MouseEventArgs evt)
        {
            if (DeletingDashboards != null && DeletingDashboards.Count() > 0)
            {
                foreach (ProfileDashboard item in DeletingDashboards)
                {
                    CurrentProfileDashboard.DeleteOrForgetItem(item);
                }
                DeletingDashboards = new List<ProfileDashboard>();
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
            foreach(ProfileDashboard d in CurrentProfileDashboard.ItemListChangeHandler.Items)
            {
                if (d.DashboardId != DefaultDashId)
                {
                    if(d.DefaultDashboard)
                    {
                        d.DefaultDashboard = false;
                        CurrentProfileDashboard.UpdateItem(d);
                    }
                }
                else
                {
                    d.DefaultDashboard = true;
                    CurrentProfileDashboard.UpdateItem(d);
                }
            }
            StateHasChanged();
        }

        protected void OkHandler()
        {
            if ((CurrentProfileDashboard.ItemListChangeHandler.NewItems.Count() > 0) || (CurrentProfileDashboard.ItemListChangeHandler.UpdatedItems.Count() > 0) ||
                (CurrentProfileDashboard.ItemListChangeHandler.DeletedItems.Count() > 0) || (CurrentProfileDashboard.ItemListChangeHandler.Items != ProfileDashboardEditorData.ItemListChangeHandler.Items))
            {
                AppState.Update = true;
            }

            ProfileDashboardEditorData.ItemListChangeHandler.Items = new ObservableCollection<ProfileDashboard>(CurrentProfileDashboard.ItemListChangeHandler.Items.Select(p => p.Copy()).ToList());
            ProfileDashboardEditorData.ItemListChangeHandler.NewItems = new ObservableCollection<ProfileDashboard>(CurrentProfileDashboard.ItemListChangeHandler.NewItems.Select(p => p.Copy()).ToList());
            ProfileDashboardEditorData.ItemListChangeHandler.UpdatedItems = new ObservableCollection<ProfileDashboard>(CurrentProfileDashboard.ItemListChangeHandler.UpdatedItems.Select(p => p.Copy()).ToList());
            ProfileDashboardEditorData.ItemListChangeHandler.DeletedItems = new ObservableCollection<ProfileDashboard>(CurrentProfileDashboard.ItemListChangeHandler.DeletedItems.Select(p => p.Copy()).ToList());

            ProfileDashboardEditorDataChanged.InvokeAsync(ProfileDashboardEditorData);
            Close();
        }

        public void CancelHandler()
        {
            ResetFields();
            Close();
        }

        public void Close()
        {
            ModalPro.CanClose = true;
            ModalPro.Dispose();
            ShowModalChanged.InvokeAsync(false);
        }

        private void ResetFields()
        {
            CurrentProfileDashboard.ItemListChangeHandler.Items = new ObservableCollection<ProfileDashboard>(ProfileDashboardEditorData.ItemListChangeHandler.Items.Select(p => p.Copy()).ToList());
            CurrentProfileDashboard.ItemListChangeHandler.NewItems = new ObservableCollection<ProfileDashboard>(ProfileDashboardEditorData.ItemListChangeHandler.NewItems.Select(p => p.Copy()).ToList());
            CurrentProfileDashboard.ItemListChangeHandler.UpdatedItems = new ObservableCollection<ProfileDashboard>(ProfileDashboardEditorData.ItemListChangeHandler.UpdatedItems.Select(p => p.Copy()).ToList());
            CurrentProfileDashboard.ItemListChangeHandler.DeletedItems = new ObservableCollection<ProfileDashboard>(ProfileDashboardEditorData.ItemListChangeHandler.DeletedItems.Select(p => p.Copy()).ToList());

            StateHasChanged();
        }
    }
}

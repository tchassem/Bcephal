  using Bcephal.Blazor.Web.Administration.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Profiles;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Administration.Pages.Profile
{

    public partial class ProfileTabUsers : ComponentBase
    {

        [Inject]
        public AppState AppState { get; set; }

        bool IsXSmallScreen { get; set; }

        [Parameter]
        public string CssClass { get; set; } = "w-100 m-0 p-0";

        [Parameter]
        public string ItemSpacing { get; set; } = "0px";

        [Parameter]
        public DeviceSize DeviceSize_ { get; set; } = DeviceSize.XSmall | DeviceSize.Small | DeviceSize.Medium;

        public List<Nameable> Users { get; set; } = new();

        [Parameter]
        public EditorData<Models.Profiles.Profile> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Models.Profiles.Profile>> EditorDataChanged { get; set; }

        bool ShowCheckboxes { get; set; } = true;

        IEnumerable<Nameable> AddingUsers { get; set; }
        IEnumerable<Nameable> DeletingUsers { get; set; }

        protected override async Task OnInitializedAsync()
        {
            AddingUsers = new List<Nameable>() {}.AsEnumerable();
            DeletingUsers = new List<Nameable>() {}.AsEnumerable();
            Users = ((ProfileEditorData) EditorData).Users.ToList();

            await base.OnInitializedAsync();
        }

        public void SelectAllAdding(MouseEventArgs evt)
        {
            AddingUsers = new List<Nameable>(Users);
            StateHasChanged();
        }

        public void HandlerAddingProfiles(MouseEventArgs evt)
        {
            bool hasAdded = false;
            foreach (Nameable item in AddingUsers)
            {
                Nameable Profile = EditorData.Item.UserListChangeHandler.Items.Where(p => p.Id == item.Id).ToList().FirstOrDefault();
                if (Profile == null)
                {
                    EditorData.Item.AddUser(item);
                    hasAdded = true;
                }
            }
            AddingUsers = new List<Nameable>();
            if (hasAdded)
            {
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
                StateHasChanged();
            }
        }

        public void SelectAllDeleting(MouseEventArgs evt)
        {
            DeletingUsers = new List<Nameable>(EditorData.Item.UserListChangeHandler.Items);
            StateHasChanged();
        }

        public void HandlerRemovingProfiles(MouseEventArgs evt)
        {
            bool hasAdded = false;
            foreach (Nameable item in DeletingUsers)
            {
                Nameable obj = EditorData.Item.UserListChangeHandler.Items.Where(u => u.Equals(item)).ToList().FirstOrDefault();
                if (obj != null)
                {
                    EditorData.Item.DeleteOrForgetUser(item);
                    hasAdded = true;
                    if (item.IsPersistent)
                    {
                        AppState.Update = true;
                    }
                }
            }
            DeletingUsers = new List<Nameable>();
            if (hasAdded)
            {
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
                StateHasChanged();
            }
        }

        public bool Editable
        {
            get
            {   if(EditorData!= null)
                {
                    return AppState.PrivilegeObserver.CanCreatedAdministrationProfile || AppState.PrivilegeObserver.CanEditAdministrationProfile(EditorData.Item);
                }
                return false;
            }
        }
    }
}

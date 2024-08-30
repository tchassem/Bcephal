using Bcephal.Blazor.Web.Administration.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Clients;
using Bcephal.Models.Profiles;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Administration.Pages.Profile
{
    public partial class ProfileTabRoles : ComponentBase
    {
        [Inject]
        public ProfileService ProfileService { get; set; }

        [Inject]
        public AppState AppState { get; set; }
      

        bool IsXSmallScreen { get; set; }
        [Parameter]
        public string CssClass { get; set; } = "w-100 m-0 p-0";

        [Parameter]
        public string ItemSpacing { get; set; } = "0px";

        [Parameter]
        public DeviceSize DeviceSize_ { get; set; } = DeviceSize.XSmall | DeviceSize.Small | DeviceSize.Medium;

        [Parameter]
        public EditorData<Models.Profiles.Profile> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Models.Profiles.Profile>> EditorDataChanged { get; set; }

        [Parameter]
        public List<ClientFunctionality> ClientFuncs { get; set; } = new();
        bool ShowCheckboxes { get; set; } = true;

        public Right ItemRole { get; set; } = new();

        IEnumerable<RightLevel> Rights = new List<RightLevel>().AsEnumerable();
        ClientFunctionality Functionality = new();

        protected override async Task OnInitializedAsync()
        {
            ClientFuncs = ((ProfileEditorData) EditorData).Functionalities.ToList();

            await base.OnInitializedAsync();
        }

        void ChangeRole(IEnumerable<RightLevel> value)
        {
            if (value != null)
            {
                ItemRole.RightLevel = value.Single();
                Right Item = EditorData.Item.RightListChangeHandler.Items.Where(u => u.Functionality.Equals(ItemRole.Functionality)).ToList().FirstOrDefault();
                if (Item == null)
                {
                    EditorData.Item.AddRight(ItemRole);
                }
                else
                {
                    Item.RightLevel = ItemRole.RightLevel;
                    EditorData.Item.UpdateRight(Item);
                }

                AppState.Update = true;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public void HandlerRemoveRole(MouseEventArgs evt)
        {
            if(ItemRole.Functionality.Equals(Functionality.Code) && ItemRole.RightLevel != null)
            {
                Right Item = EditorData.Item.RightListChangeHandler.Items.Where(u => u.Functionality.Equals(ItemRole.Functionality)).ToList().FirstOrDefault();
                if (Item != null)
                {
                    EditorData.Item.DeleteOrForgetRight(Item);
                }

                ItemRole.RightLevel = null;
                AppState.Update = true;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        IEnumerable<ClientFunctionality>  ChangeFunctionalityBing
        {
            get => new List<ClientFunctionality>() { Functionality };
            set
            {
                if (value != null)
                {
                    Functionality = value.Single();
                    ItemRole = new Right(Functionality.Code);
                    Rights = Functionality.Levels.AsEnumerable();
                    loadRolesFromCache();
                    StateHasChanged();
                }
            }
        }

        private void loadRolesFromCache()
        {
            Right Item = EditorData.Item.RightListChangeHandler.Items.Where(u => u.Functionality.Equals(Functionality.Code)).ToList().FirstOrDefault();
            if (Item != null)
            {
                ItemRole = Item;
            }
        }
    }
}

using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Base;
using Bcephal.Models.Forms;
using Bcephal.Models.Clients;
using Microsoft.AspNetCore.Components;
using System;
using Bcephal.Models.Grids;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections.ObjectModel;
using Bcephal.Blazor.Web.Administration.Services;
using Bcephal.Models.Grids.Filters;
using Microsoft.AspNetCore.Components.Web;
using Bcephal.Blazor.Web.Services;
using DevExpress.Blazor;
using Bcephal.Models.Users;

namespace Bcephal.Blazor.Web.Administration.Pages.Client
{
    public partial class ClientForm: Form<Bcephal.Models.Clients.Client, Models.Clients.Client>
    {
        private string EditorRoute { get;  set; }
        public bool NewButtonVisible { get; private set; }
        public bool DeleteButtonVisible { get; private set; }
        int ActiveTabIndex { get; set; } = 0;
        [Inject]
        private ClientService ClientService { get; set; }
        public override bool CanUsingGroup { get => false; set => base.CanUsingGroup = value; }
        public override bool usingUnitPane => false;
        public Nameable selectProfile;
        bool IsXSmallScreen { get; set; }
        public enum Language
        {
            Fr,
            En
        }
        List<string> LanguageTypes = new List<string>();

        IEnumerable<ClientFunctionality> FunctionalityListChangeHandler
        {
            get
            {
                if (EditorData != null && EditorData.Item != null)
                {
                    return EditorData.Item.FunctionalityListChangeHandler.Items;
                }
                return new List<ClientFunctionality>();
            }
        }

        IEnumerable<ClientFunctionality> FunctionalityList { 
            get {
                if (EditorData != null && EditorData.Item != null)
                {
                    return EditorData.Item.FunctionalityListChangeHandler.Items.Where(c => c.Active == true).ToList();
                }
                return new List<ClientFunctionality>();
            } set {
                if (EditorData != null && EditorData.Item != null)
                { 
                    foreach(var item in EditorData.Item.FunctionalityListChangeHandler.Items)
                    {
                        item.Active = value.Contains(item);
                        
                    }
                   }
                AppState.Update = true;
            }
        }
      
        protected override Service<Models.Clients.Client, Models.Clients.Client> GetService()
        {
            return ClientService;
        }

        public override string GetBrowserUrl { get => Route.BROWSER_CLIENT; set => base.GetBrowserUrl = value; }
               
        public void OnSelectedFunctionalities()
        {
            bool hasActivate = false;
            foreach (var item in EditorData.Item.FunctionalityListChangeHandler.Items)
            {
                if (!item.Active)
                {
                    item.Active = true;
                    if (!hasActivate)
                    {
                        hasActivate = true;
                    }
                }
            }
            if (hasActivate)
            {
                AppState.Update = true;
            }
        }
              
        public void OnDeselectedFunctionalities()
        {
            bool hasActivate = false;
            foreach (var item in EditorData.Item.FunctionalityListChangeHandler.Items)
            {
                if (item.Active)
                {
                    item.Active = false;
                    if (!hasActivate)
                    {
                        hasActivate = true;
                    }
                }
            }
            if (hasActivate)
            {
                AppState.Update = true;
            }
        }

        private void ChangeTab(TabClickEventArgs e)
        {
            if (AppState.CanCreate)
            {
            }
            this.ActiveTabIndex = e.TabIndex;
        }

        protected override async Task OnInitializedAsync()
        {
            foreach (var t in Enum.GetValues(typeof(Language)).OfType<Language>().ToList())
            {

                LanguageTypes.Add(t.ToString());

            }
            EditorRoute = Route.BROWSER_CLIENT;
            NewButtonVisible = true;
            DeleteButtonVisible = true;
            EditButtonVisible = true;
            await base.OnInitializedAsync();
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                AppState.CanRefresh = true;
                RefreshRightContent(null);
               
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        public override ValueTask DisposeAsync()
        {
            AppState.CanRefresh = false;
            AppState.CanCreate = false;
            AppState.Update = false;
            return base.DisposeAsync();

        }

        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        public async void AddProfile()
        {
            await AppState.NavigateTo(Route.PROFIL_EDIT);
        }
        
        public void DeleteProfile(MouseEventArgs evt)
        {
            EditorData.Item.DeleteOrForgetProfile(selectProfile);
            AppState.Update = true;
        }

        protected override string DuplicateName()
        {
            return AppState["duplicate.client.name", EditorData.Item.Name];
        }

        #region attribut 
        private string Name
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.Name;
                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                     EditorData.Item.Name= value;
                    AppState.Update = true;
                }
               
            }
        }
      
        private string Description
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.Description;
                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.Description = value;
                    AppState.Update = true;
                }
            }
        }
        
        
        private string OwnerUser
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.OwnerUser;
                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.OwnerUser = value;
                    AppState.Update = true;
                }
            }
        }
  
       

        private ClientNature ClientNature
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.ClientNature;
                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.ClientNature = value;
                    AppState.Update = true;
                }

            }
        }
        private string ClientStatus_
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.ClientStatus.GetText(Text => AppState[Text]);
                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                    if (value != null)
                    {
                        EditorData.Item.ClientStatus = ClientStatus.ACTIVE.GetClientStatus(value,Text => AppState[Text]);
                    }
                    AppState.Update = true;
                }

            }
        }
        private ClientType ClientType
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.ClientType;
                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.ClientType = value;
                    AppState.Update = true;
                }

            }
        }
        
        private string DefaultLanguage
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.DefaultLanguage;
                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.DefaultLanguage = value;
                    AppState.Update = true;
                }

            }
        }

        private Address Address
        {
            get
            {
                if (EditorData != null)
                {
                    if(EditorData.Item.Address == null)
                    {
                        EditorData.Item.Address = new();
                    }
                    return EditorData.Item.Address;
                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.Address = value;
                    AppState.Update = true;
                }

            }
        }
        private int MaxUser
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.MaxUser;
                }
                return 0;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.MaxUser = value;
                    AppState.Update = true;
                }

            }
        }
        
        private bool DefaultClient
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.DefaultClient;
                }
                return false;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.DefaultClient = value;
                    AppState.Update = true;
                }

            }
        }
        #endregion

        public override string LeftTitle
        {
            get
            {
                return AppState["EditCustomer"];
            }
        }

        public bool EditButtonVisible { get; private set; }

        public bool Editable
        {
            get
            {
                if(EditorData != null)
                {
                    return AppState.PrivilegeObserver.CanCreatedAdministrationClient || 
                           AppState.PrivilegeObserver.CanEditAdministrationClient(EditorData.Item);
                }
                return false;
            }
        }
    }
}

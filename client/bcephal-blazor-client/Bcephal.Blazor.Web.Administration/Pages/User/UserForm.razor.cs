using Bcephal.Blazor.Web.Base.Services;

using Bcephal.Models.Base;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Forms;  
using Bcephal.Models.Grids;
using System.Collections.ObjectModel;
using Bcephal.Blazor.Web.Administration.Services;
using Bcephal.Models.Exceptions;
using Bcephal.Models.Clients;
using DevExpress.Blazor;
using Bcephal.Models.Users;

namespace Bcephal.Blazor.Web.Administration.Pages.User
{
   public partial class UserForm: Base.Shared.Form<Bcephal.Models.Users.User, Bcephal.Models.Users.User>
    {
   
        public string EditorRoute { get; set; }
        protected bool IsNavLink { get; set; } = false;
        protected bool DeleteButtonVisible { get; set; } = false;
        protected bool NewButtonVisible { get; set; } = false;
        protected bool ClearFilterButtonVisible { get; set; } = false;
        protected bool NewRowButtonVisible { get; set; } = false;
        protected bool EditButtonVisible { get; set; } = false;
        public Models.Users.User User { get; set; }
        public string TextAreaValue { get; set; } = string.Empty;
        public Language language { get; set; }
        public DeviceSize DeviceSize_ { get; set; } = DeviceSize.XSmall | DeviceSize.Small | DeviceSize.Medium;

      

        public enum Language
        {
            En,

            Fr
        }
        private List<string> LanguageTypes = new List<string>();
       
        public override bool CanUsingGroup { get => false; set => base.CanUsingGroup = value; }
        [Parameter]
        public string ItemSpacing { get; set; } = "0px";
        [Inject]

        private UsersService UsersService { get; set; }

        private PrivilegeObserver PrivilegeObserver { get; set; }

        protected override Service<Models.Users.User, Models.Users.User> GetService()
        {
            return UsersService;
        }

        public override string GetBrowserUrl { get => Route.BROWSER_USER; set => base.GetBrowserUrl = value; }
        public override Task SetParametersAsync(ParameterView parameters)
        {
            usingMixPane = false;
            displayRight = DISPLAY_NONE;
            displayLeft = WIDTH_100;
            return base.SetParametersAsync(parameters);
        }

       
        protected override async Task OnInitializedAsync()
        {
            foreach (var t in Enum.GetValues(typeof(Language)).OfType<Language>().ToList())
            {

                LanguageTypes.Add(t.ToString());
                
            }
            await base.OnInitializedAsync();
            IsNavLink = false;
            NewButtonVisible = true;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = true;
            EditButtonVisible = true;
            NewRowButtonVisible = false;
        }
      
        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
               
                RefreshRightContent(null);
            }
            return  base.OnAfterRenderAsync(firstRender);
           
        }
        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        public override ValueTask DisposeAsync()
        {
            AppState.CanRefresh = false;
            AppState.Update = false;
            AppState.CanCreate = false;
            return base.DisposeAsync();
        }

        protected override string DuplicateName()
        {
            return AppState["duplicate.user.name", EditorData.Item.Name];
        }

        #region
        private string FirstName
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.FirstName;
                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.FirstName = value;
                    AppState.Update = true;
                }

            }
        }
       private string username
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.username;
                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.username = value;
                    AppState.Update = true;
                }

            }
        }
        private string lastName
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.lastName;

                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.lastName = value;
                    AppState.Update = true;
                }

            }
        } 
        private UserType UserType
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.UserType;

                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.UserType = value;
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
        private string email
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.email;
                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.email = value;
                    AppState.Update = true;
                }

            }
        }
        private bool Enabled
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.Enabled;
                }
                return false;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.Enabled = value;
                    AppState.Update = true;
                }

            }
        }
        private string password
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.password;
                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.password = value;
                    AppState.Update = true;
                }

            }
        }
        private bool emailVerified
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.emailVerified;
                }
                return false;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.emailVerified = value;
                    AppState.Update = true;
                }

            }
        }

        #endregion

        protected override async Task initComponent()
        {
            try
            {
                AppState.ShowLoadingStatus();
                if (EditorDataBinding == null)
                {
                    await base.initComponent();

                }
                AppState.HideLoadingStatus();
                StateHasChanged();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }
        private void UpdateProfile(Models.Users.User user)
        {
            this.EditorData.Item = user;
        }
        public override string LeftTitle
        {
            get
            {
                return AppState["Edit.User"];
            }
        }
        public bool Editable
        {
            get
            {  if(EditorData != null)
                {
                    return AppState.PrivilegeObserver.CanCreatedAdministrationUser || AppState.PrivilegeObserver.CanEditAdministrationUser(EditorData.Item);
                }
                return false;
                
            }
        }

    }
}


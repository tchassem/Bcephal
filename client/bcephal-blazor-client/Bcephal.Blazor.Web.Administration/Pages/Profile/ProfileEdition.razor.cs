using Bcephal.Blazor.Web.Administration.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Base;
using Bcephal.Models.Clients;
using Bcephal.Models.Dashboards;
using Bcephal.Models.Profiles;
using DevExpress.Blazor;
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
    public partial class ProfileEdition : Form<Models.Profiles.Profile, ProfileBrowserData>
    {
        public bool IsSmallScreen { get; set; }

        public string LabelWidth { get; set; } = Constant.GROUP_INFOS_LABEL_WIDTH;

        public string TextWidth { get; set; } = Constant.GROUP_INFOS_TEXT_WIDTH;

        public ObservableCollection<UserType> UserTypeItems { get; set; }

        int ActiveTabIndex { get; set; } = 0;

        public ProfileDashboardEditorData ProfileDashboardEditorData { get; set; }

        [Inject]
        public ProfileService ProfileService { get; set; }

        [Inject]
        public ProfileDashboardService ProfileDashboardService { get; set; }

        private UserType ProfileType
        {
            get
            {
                if (EditorData.Item != null)
                {
                    return EditorData.Item.ProfileType;
                }
                return null;
            }
            set
            {
                if (EditorData.Item == null)
                {
                    EditorData.Item = new Models.Profiles.Profile();
                }
                EditorData.Item.ProfileType = value;
                AppState.Update = true;
            }
        }

        private string Description
        {
            get
            {
                if (EditorData.Item != null)
                {
                    return EditorData.Item.Description;
                }
                return "";
            }
            set
            {
                if (EditorData.Item == null)
                {
                    EditorData.Item = new Models.Profiles.Profile();
                }
                EditorData.Item.Description = value;
                AppState.Update = true;
            }
        }

        private bool showProfileDashboardModal = false;
        public override string LeftTitle { get { return AppState["Profil.Edit"]; } }

        protected override ProfileService GetService()
        {
            return ProfileService;
        }

        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        protected override void OnInitialized()
        {
            base.OnInitialized();
            UserTypeItems = new ObservableCollection<UserType>(UserType.GetAll().Where(u => !u.Equals(UserType.ADMINISTRATOR)).ToList());
        }

        protected override async void AfterInit(EditorData<Models.Profiles.Profile> EditorData)
        {
            if(EditorData != null)
            {
                // EditorData = (ProfileEditorData)ProfileEditorData;
                if (EditorData.Item != null && EditorData.Item.Id.HasValue)
                {
                    ProfileDashboardEditorData = await ProfileDashboardService.GetDashboards(EditorData.Item.Id.Value.ToString());
                    StateHasChanged();
                }
            }
        }
        protected override string DuplicateName()
        {
            return AppState["duplicate.profile.name", EditorData.Item.Name];
        }
        public override string GetBrowserUrl { get => Route.PROFIL_LIST; set => base.GetBrowserUrl = value; }
        // -------------------------------------------------------------------------------------------------------------------------------------------------------------------

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                AppState.CanRefresh = true;
                AppState.RefreshHander += RefreshView;
            }
            //AppState.Update = true;
            return base.OnAfterRenderAsync(firstRender);
        }

        public override ValueTask DisposeAsync()
        {
            AppState.CanRefresh = false;
            AppState.RefreshHander -= RefreshView;
            AppState.Update = false;
            return base.DisposeAsync();
        }

        protected override void AfterSave(EditorData<Models.Profiles.Profile> ProfileEditorData)
        {
            AfterInit(ProfileEditorData);
            StateHasChanged();
        }
      
        private void ChangeTab(TabClickEventArgs e)
        {
            if (AppState.CanCreate)
            {
            }
            ActiveTabIndex = e.TabIndex;
        }

        private async void RefreshView()
        {
            EditorData = null;
            await base.initComponent();
            StateHasChanged();
        }

        protected override async void save()
        {
            try
            {
                AppState.ShowLoadingStatus();
                if(EditorData.Item != null && EditorData.Item.Id.HasValue && ProfileDashboardEditorData != null)
                {
                    await ProfileDashboardService.Save(ProfileDashboardEditorData.ItemListChangeHandler, EditorData.Item.Id.ToString());
                }
                base.save();
            }
            catch (Exception ex)
            {
                AppState.Update = true;
                AppState.HideLoadingStatus();
                Error.ProcessError(ex);
            }
        }

        private  void SaveDashboards()
        {
            showProfileDashboardModal = false;
            StateHasChanged();
        }

        private async void CancelDashboards()
        {
            if (EditorData.Item != null && EditorData.Item.Id.HasValue)
            {
                ProfileDashboardEditorData = await ProfileDashboardService.GetDashboards(EditorData.Item.Id.Value.ToString());
            }
            AppState.Update = false;
            showProfileDashboardModal = false;
            StateHasChanged();
        }

        public bool Editable
        {
            get
            {
                if (EditorData != null)
                {
                    return AppState.PrivilegeObserver.CanCreatedAdministrationProfile || AppState.PrivilegeObserver.CanEditAdministrationProfile(EditorData.Item);
                }
                return false;
            }
        }
    }
}

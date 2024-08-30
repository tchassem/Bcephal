using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Initiation.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Profiles;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Initiation.Pages.AccessRights
{
    public partial class AccessRight : Form<ProfileProject, AccessRightEditorData>
    {
        [Inject]
        public AccessRightService AccessRightService { get; set; }

        bool IsXSmallScreen { get; set; }

        public string ItemSpacing { get; set; } = "0px";
        
        public override bool CanUsingGroup { get => false; set => base.CanUsingGroup = value; }

        public override bool usingUnitPane => false;

        bool ShowCheckboxes { get; set; } = true;

        IEnumerable<Nameable> AddingProfiles { get; set; }

        IEnumerable<ProfileProject> DeletingProfiles { get; set; }

        public EventCallback<AccessRightEditorData> GetProfilesCallback { get; set; }

        public override string LeftTitle => AppState["access.rights"];

        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        protected override AccessRightService GetService()
        {
            return AccessRightService;
        }

        protected override async Task OnInitializedAsync()
        {
            AddingProfiles = new List<Nameable>() { }.AsEnumerable();
            DeletingProfiles = new List<ProfileProject>() { }.AsEnumerable();
            await Task.CompletedTask;
        }

        protected override async Task initComponent()
        {
            try
            {
                AppState.ShowLoadingStatus();
                if (EditorData == null)
                {
                    EditorDataFilter filter = getEditorDataFilter();
                    filter.NewData = true;
                    if (Id.HasValue)
                    {
                        filter.NewData = false;
                        filter.Id = Id;
                    }
                    EditorDataBinding = await AccessRightService.GetProfileProjects(AppState.ProjectId);
                    EditorData.Item = new();
                    AppState.Update = false;
                    initModelParams();
                }
                AppState.HideLoadingStatus();
                StateHasChanged();
            }
            catch (Exception ex)
            {
                AppState.HideLoadingStatus();
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }

        AccessRightEditorData GetEditorData => (AccessRightEditorData) EditorDataBinding;

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            if (firstRender)
            {
                AppState.Hander -= save;
                AppState.CanRefresh = true;
                AppState.RefreshHander += RefreshView;
                AppState.Hander += RunSave;
                RefreshRightContent(null);            
            }
        }

        public override ValueTask DisposeAsync()
        {
            AppState.CanRefresh = false;
            AppState.RefreshHander -= RefreshView;
            AppState.Update = false;
            AppState.Hander -= RunSave;
            return base.DisposeAsync();
        }

        private async void RefreshView()
        {
            this.EditorData = null;
            await base.initComponent();
            StateHasChanged();
        }

        private async void RunSave()
        {
            AppState.ShowLoadingStatus();           
            AppState.Update = false;
            EditorDataBinding = await AccessRightService.SaveAccessRight(GetEditorData.ItemListChangeHandler, AppState.ProjectId.ToString()); 
            ToastService.ShowSuccess(AppState["save.SuccessfullyAdd", LeftTitle]);
            AppState.HideLoadingStatus();
            StateHasChanged();
        }        

        public void SelectAllAdding(MouseEventArgs evt)
        {
            this.AddingProfiles = new List<Nameable>(GetEditorData.Profiles);
            StateHasChanged();
        }

        public void HandlerAddingProfiles(MouseEventArgs evt)
        {
            bool hasAdded = false;
            foreach (Nameable item in AddingProfiles)
            {
                ProfileProject ProfileItem = GetEditorData.ItemListChangeHandler.Items.Where(p => p.ProfileId == item.Id).ToList().FirstOrDefault();
                if (ProfileItem == null)
                {
                    GetEditorData.AddItem(new ProfileProject(item));
                    if (!hasAdded)
                    {
                        hasAdded = !hasAdded;
                    }
                }
            }
            AddingProfiles = new List<Nameable>();
            if (hasAdded)
            {
                GetProfilesCallback.InvokeAsync(GetEditorData);
                AppState.Update = true;
                StateHasChanged();
            }
        }

        public void SelectAllDeleting(MouseEventArgs evt)
        {
            DeletingProfiles = new List<ProfileProject> (GetEditorData.ItemListChangeHandler.Items);
            StateHasChanged();
        }

        public void HandlerRemovingProfiles(MouseEventArgs evt)
        {
            if (DeletingProfiles != null && DeletingProfiles.Count() > 0)
            {
                foreach (ProfileProject item in DeletingProfiles)
                {
                    ProfileProject obj = GetEditorData.ItemListChangeHandler.Items.Where(u => u.Equals(item)).ToList().FirstOrDefault();
                    if (obj != null)
                    {
                        GetEditorData.DeleteOrForgetItem(item);
                        if (item.IsPersistent)
                        {
                            AppState.Update = true;
                        }
                    }
                }
                GetProfilesCallback.InvokeAsync(GetEditorData);
                DeletingProfiles = new List<ProfileProject>();
            };
            StateHasChanged();
        }
    }
}

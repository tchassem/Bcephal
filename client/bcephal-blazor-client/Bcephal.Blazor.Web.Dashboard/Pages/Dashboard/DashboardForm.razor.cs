using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Dashboard.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Bcephal.Models.Functionalities;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Dashboard.Shared.Dashboard;
using Bcephal.Models.Profiles;


namespace Bcephal.Blazor.Web.Dashboard.Pages.Dashboard
{
    public partial class DashboardForm : Form<Models.Dashboards.Dashboard, Models.Dashboards.Dashboard>
    {
        public override bool usingUnitPane => false;
        public bool IsSchedulerActive { get; set; }
        public int SchedulerValue { get; set; }
        public string TimeFrequency { get; set; }
        private bool IsSmallScreen { get; set; }
        public Models.Dashboards.Dashboard DashboardItem { get; set; }

        [Inject]
        IJSRuntime JsRuntime { get; set; }

        [Parameter]
        public bool DisplayDesign { get; set; } = true;

        [Parameter]
        public bool IsHomePage { get; set; } = false;

        [Parameter]
        public int ActiveTabIndex { get; set; } = 0;


        [Inject]
        public DashboardService DashboardService { get; set; }

        [Inject]
        public DashboardProfileService DashboardProfileService { get; set; }

        [Inject]
        FunctionalityService FunctionalityService { get; set; }

        public override string LeftTitle { get { return !IsHomePage ? AppState["New.Dashboard"] : EditorData != null &&  EditorData.Item != null ? EditorData.Item.Name : null;  } }

        public override string LeftTitleIcon { get { return !IsHomePage ? "bi-file-plus" : "bi-file-plus"; } }

        public override string LeftTitlePage { get { return !IsHomePage? base.LeftTitlePage : null;  }}

        private bool showDashboardProfileModal = false;

        public DashboardProfileEditorData DashboardProfileEditorData { get; set; }

        Dictionary<long, FunctionalityBlockGroup> HomePages = new();

        protected override DashboardService GetService()
        {
            return DashboardService;
        }


        public override string GetBrowserUrl { get => !IsHomePage ? Route.BROWSER_REPORT_DASHBOARD : null; set => base.GetBrowserUrl = value; }
        public override async ValueTask DisposeAsync()
        {
            await base.DisposeAsync();
            AppState.Update = false;
            AppState.CanLoad = false;
            AppState.CustomHander -= AddConfig;
            AppState.IsDashboard = false;
            AppState.CanRefresh = false;
        }

        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        protected override string DuplicateName()
        {
            return AppState["duplicate.dashboard.name", EditorData.Item.Name];
        }

        public int ActiveTabIndexBinding { get => ActiveTabIndex;
            set {
                ActiveTabIndex = value;
                if (ActiveTabIndex == 0 && !IsHomePage && DisplayDesign)
                {
                    RefreshRightContent__(RightContent_);
                }
            } 
        }

        protected override void AfterInit(EditorData<Models.Dashboards.Dashboard> EditorData)
        {
            this.DashboardItem = EditorData.Item;
            base.AfterInit(EditorData);
        }

        protected override void OnAfterRender(bool firstRender)
        {
            base.OnAfterRender(firstRender);
            AppState.Update = true;
            AppState.IsDashboard = true;
            AppState.CanShowCustomized = false;
            AppState.CanShowStandard = false;
            if (ActiveTabIndexBinding == 1)
            {
                AppState.CanRefresh = false;
            }
            if (!DisplayDesign && firstRender)
            {
                RefreshRightContent__(null);
            }
            if (firstRender)
            {
                AppState.CustomHander += AddConfig;
            }
            if (firstRender && ActiveTabIndex == 0 && !IsHomePage && DisplayDesign)
            {
                RefreshRightContent__(RightContent_);
            }
        }
      
        protected override async void save()
        {
            await BeforeSave(EditorData);
            if (EditorData != null && EditorData.Item != null)
            {
                try
                {
                    AppState.ShowLoadingStatus();
                    AppState.Update = false;
                    await SaveFunctionalityBlockGroup(EditorData);
                    EditorData.Item = await GetService().Save(EditorData.Item);
                    AfterSave(EditorData);                    
                    ToastService.ShowSuccess(AppState["save.SuccessfullyAdd", LeftTitlePage]);
                }
                catch (Exception ex)
                {
                    AppState.Update = true;
                    Error.ProcessError(ex);
                }
                finally
                {
                    AppState.HideLoadingStatus();
                    StateHasChanged();
                }
            }
        }

        private async Task SaveFunctionalityBlockGroup(EditorData<Models.Dashboards.Dashboard> EditorData)
        {
            IEnumerable<DashboardItem> items = EditorDataBinding.Item.ItemsListChangeHandler.GetItems();
            foreach (var key in HomePages.Keys)
            {
                var grop = await FunctionalityService.SaveGroup(HomePages[key], AppState.ProjectId.ToString());
                DashboardItem item = GetDashboardItem(items, key);
                if (item != null)
                {
                    item.ItemId = grop.Id;
                    EditorDataBinding.Item.UpdateItem(item);
                }
            }
        }

        private DashboardItem GetDashboardItem(IEnumerable<DashboardItem> items, long id)
        {
            foreach(var item in items)
            {
                if(item.Id.HasValue && item.Id.Value == id)
                {
                    return item;
                }
            }
            return null;
        }
        private void AddConfig(object ItemId_, object functionalityBlockGroup_)
        {
            long.TryParse(ItemId_.ToString(), out long ItemId);
            if (HomePages.ContainsKey(ItemId)) {
                HomePages.Remove(ItemId);
            }
            FunctionalityBlockGroup group = (FunctionalityBlockGroup)functionalityBlockGroup_;
            HomePages.Add(ItemId, group);
        }

        private void RefreshRightContent__(RenderFragment render)
        {
            RefreshRightContent(render);
            StateHasChanged();
        }

        protected override void AfterSave(EditorData<Models.Dashboards.Dashboard> EditorData)
        {

        }

        protected override async Task BeforeSave(EditorData<Models.Dashboards.Dashboard> EditorData)
        {
            await BeforeSaveDashboard(EditorData);
            await base.BeforeSave(EditorData);
        }

        public bool IsData { get; set; }
               
        protected virtual async Task BeforeSaveDashboard(EditorData<Models.Dashboards.Dashboard> EditorData)
        {
            if (ActiveTabIndex == 1 && !IsHomePage && DisplayDesign)
            {
                ObservableCollection<DashboardItem> items = EditorData.Item.ItemsListChangeHandler.GetItems();
                foreach (DashboardItem item in items)
                {
                   await item.BoundingClientRect(JsRuntime);
                    EditorData.Item.UpdateItem(item);
                }
            }
        }

        private void UpdateDashboardProfiles(DashboardProfileEditorData DashboardProfile)
        {
            DashboardProfileEditorData = DashboardProfile;
        }

        private async void SaveProfiles()
        {
            bool result = await DashboardProfileService.Save(DashboardProfileEditorData.ItemListChangeHandler, DashboardItem.Id.ToString());
            showDashboardProfileModal = false;
        }

        private void CancelProfiles()
        {
            showDashboardProfileModal = false;
        }

        public bool Editable
        {
            get
            {   if(EditorData != null)
                {
                    var first = AppState.PrivilegeObserver.CanCreatedDashboardingDashboard;
                    var second = AppState.PrivilegeObserver.CanEditDashboardingDashboard(EditorData.Item);
                    return first || second;
                }
                return false;
            }
        }
    }
}

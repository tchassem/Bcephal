using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Bcephal.Models.Functionalities;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Dashboard
{
    public partial class DashboardConfig_ : ComponentBase
    {
        [Parameter]
        public RenderFragment ChildContent { get; set; }
   
        int ActiveSecondTabIndex { get; set; }

        public string DashboardName { get; set; }

        public string Bgroup { get; set; }

        public bool VisibleInShortCut { get; set; }

        public bool VisibleInBrowser { get; set; }

        public bool Default { get; set; }

        public List<string> Bgroups = new List<string>() { "Default" };

        [Parameter]
        public Action<RenderFragment> RefreshView { get; set; }

        [Parameter]
        public EditorData<Models.Dashboards.Dashboard> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Models.Dashboards.Dashboard>> EditorDataChanged { get; set; }

        int ActiveTabIndex { get; set; } = 0;

        [CascadingParameter]
        public Error Error { get; set; }

        [Inject]
        private AppState AppState { get; set; }

        private int TabPageItemCount { get; set; } = 1;

        List<RenderFragment> renderFragments = new List<RenderFragment>();

        [Parameter]
        public bool Editable { get; set; } = true;

        public EditorData<Models.Dashboards.Dashboard> EditorDataBinding
        {
            get
            {
                return EditorData;
            }
            set
            {
                EditorData = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public DashboardLayout SelectedLayout
        {
            get
            {
                if (EditorData.Item != null && EditorData.Item.DashboardLayout != null)
                {
                    return EditorData.Item.DashboardLayout;
                }
                return DashboardLayouts[0];
            }
            set
            {
                EditorData.Item.DashboardLayout = value;
                if (EditorData.Item.ItemsListChangeHandler.GetItems().Count > 0)
                {
                    ObservableCollection<DashboardItem> ites = EditorData.Item.ItemsListChangeHandler.GetItems();
                   
                    foreach (DashboardItem dite in ites)
                    {
                       EditorData.Item.RemoveOrForgetItem(dite);
                    }
                   
                }

                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public ObservableCollection<DashboardLayout> DashboardLayouts = new()
        {
            DashboardLayout.ONE,
            DashboardLayout.VERTICAL_2x1,
            DashboardLayout.VERTICAL_2x2,
            DashboardLayout.VERTICAL_3x3,
            DashboardLayout.VERTICAL_3x4,
            DashboardLayout.VERTICAL_4x3,
            DashboardLayout.HORIZONTAL_1x2,
            DashboardLayout.HORIZONTAL_2x2,
            DashboardLayout.HORIZONTAL_3x3,
            DashboardLayout.HORIZONTAL_3x4,
            DashboardLayout.HORIZONTAL_4x3
        };


        protected override void OnAfterRender(bool firstRender)
        {
            base.OnAfterRender(firstRender);
            AppState.CanCreate = false;
            AppState.Update = true;
        }

        protected override async Task OnInitializedAsync()
        {
            AppState.ShowLoadingStatus();
            await base.OnInitializedAsync();
            AppState.HideLoadingStatus();
        }

    }
}
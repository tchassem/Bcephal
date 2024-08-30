using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Bcephal.Models.Functionalities;
using Microsoft.AspNetCore.Components;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Dashboard
{
    public partial class DashboardComponetShortCut : ComponentBase
    {
        [Parameter]
        public bool IsToolbarVisible { get; set; } = false;
        bool PopupVisible { get; set; } = false;


        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public DashboardItem Item { get; set; }

        [Parameter]
        public Action<Models.Dashboards.DashboardItem> ItemChanged { get; set; }

        [Parameter]
        public Action<Models.Dashboards.DashboardItem> ItemHandler { get; set; }

        [Parameter]
        public Func<Models.Dashboards.DashboardItem> GetItemHandler { get; set; }

        [Parameter]
        public Func<long, FunctionalityBlockGroup, Task> UpdateHander { get; set; }

        public List<Functionality> SelectedFunctionalities { get; set; } = new List<Functionality>();
        [Inject]
        FunctionalityService FunctionalityService { get; set; }
        [Inject]
        FunctionnalityWorkspaceService FunctionnalityWorkspaceService { get; set; }
        [Inject]
        AppState AppState { get; set; }
        public FunctionalityWorkspace functionalityWorkspace { get; set; }

        public ObservableCollection<Functionality> AvailableFunctionalities = new ObservableCollection<Functionality>();

        FunctionalityBlockGroup FunctionalityBlockGroup_;

        private RenderFormContent RenderFormContentRef { get; set; }
        protected override async Task OnInitializedAsync()
        {
            Item = GetItem();
            functionalityWorkspace = await FunctionnalityWorkspaceService.GetFunctionalityWorkspace(AppState.ProjectId.ToString());            
            AvailableFunctionalities = LoadTilableFunctionalities();
            InitFunctionalitiesBlock();
            await base.OnInitializedAsync();
        }

        private ObservableCollection<Functionality> LoadTilableFunctionalities()
        {
            IEnumerable<Functionality> Tilables_ = new List<Functionality>();
            if (functionalityWorkspace != null)
            {
                Tilables_ = functionalityWorkspace.AvailableFunctionalities.Where(x => x.IsTilable);
            }
            return new ObservableCollection<Functionality>(Tilables_);
        }
        void SelectFonctionalityWorkspaceAction(Functionality functionality)
        {
            SelectedFunctionalities.Add(functionality);
            FunctionalityBlock functionalityblock = new FunctionalityBlock()
            {
                ProjectId = Convert.ToInt64(AppState.ProjectId),
                Code = functionality.Code,
                Name = functionality.Name,
                Username = "",
            };

            if (functionalityblock != null && functionalityWorkspace.FunctionalityBlockGroups.Count > 0)
            {
                FunctionalityBlockGroup_.AddBlock(functionalityblock);                
            }
        }

        private async void InitFunctionalitiesBlock()
        {
            if (Item != null && Item.ItemId.HasValue)
            {
                try
                {
                    FunctionalityBlockGroup_ = await FunctionalityService.GetGroupById(AppState.ProjectId.ToString(), Item.ItemId.ToString());
                    if (RenderFormContentRef != null)
                    {
                        if (FunctionalityBlockGroup_ == null)
                            FunctionalityBlockGroup_ = new();
                        await RenderFormContentRef.StateHasChanged_();
                    }
                }
                catch
                {
                    FunctionalityBlockGroup_ = new FunctionalityBlockGroup();
                }
            }
            else
            {
                FunctionalityBlockGroup_ = new FunctionalityBlockGroup();
            }
        }

        

        public void AddToFunctionalityBlockGroup()
        {
            if (Item.Id.HasValue)
            {
                PopupVisible = false;
                FunctionalityBlockGroup_.Name = Item.Name;
                AppState.CustomHander?.Invoke(Item.Id.Value, FunctionalityBlockGroup_);
                if (SelectedFunctionalities.Count != 0)
                {
                    foreach (Functionality fnt in SelectedFunctionalities)
                    {
                        AvailableFunctionalities.Remove(fnt);
                    }
                    SelectedFunctionalities.Clear();
                }
              //   UpdateItem();
            }
        }


        public Models.Dashboards.DashboardItem GetItem()
        {
            if (Item != null)
            {
                return Item;
            }
            else
            {
                Models.Dashboards.DashboardItem item = GetItemHandler?.Invoke();
                return item;
            }
        }

        public  void UpdateItem()
        {
           ItemHandler.Invoke(Item);
        }

        public bool IsEmptyAddFunc()
        {
            return SelectedFunctionalities.Count != 0;
        }

    }
}

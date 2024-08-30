using Bcephal.Blazor.Web.Base;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Functionalities;
using Microsoft.AspNetCore.Components;
using Microsoft.Extensions.Localization;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Pages.Project
{
    public partial class FunctionalitiesHome : ComponentBase, IAsyncDisposable
    {
        [CascadingParameter]
        public Error Error { get; set; }

        [Inject]
        protected IToastService ToastService { get; set; }
        [Parameter]
        public string projectId { get; set; }

        public int ActiveTabIndex_ = 0;
        [Inject]
        protected IJSRuntime JSRuntime { get; set; }
        [Inject]
        protected AppState AppState { get; set; }
        bool PopupVisible { get; set; } = false;
        [Inject]
        FunctionalityService FunctionalityService { get; set; }
        [Inject]
        FunctionnalityWorkspaceService FunctionnalityWorkspaceService { get; set; }

        [Parameter]
        public bool IsToolbarVisible { get; set; } = false;
        public FunctionalityWorkspace functionalityWorkspace { get; set; }

        public ObservableCollection<Functionality> AvailableFunctionalities = new ObservableCollection<Functionality>();

        public List<Functionality> SelectedFunctionalities { get; set; } = new List<Functionality>();

        protected override async Task OnParametersSetAsync()
        {
            projectId = AppState.ProjectId.ToString();
            functionalityWorkspace = await FunctionnalityWorkspaceService.GetFunctionalityWorkspace(projectId);
            if (functionalityWorkspace.FunctionalityBlockGroups == null)
            {
                functionalityWorkspace.FunctionalityBlockGroups = new ObservableCollection<FunctionalityBlockGroup>();
            }
            AvailableFunctionalities = LoadTilableFunctionalities();
            // await JSRuntime.InvokeVoidAsync("console.log", "*************** functionnalities => ", AvailableFunctionalities);
            await base.OnParametersSetAsync();
        }

        private ObservableCollection<Functionality> LoadTilableFunctionalities()
        {
            IEnumerable<Functionality> Tilables_  = new List<Functionality>();
            if (functionalityWorkspace != null)
            {
                 Tilables_ = functionalityWorkspace.AvailableFunctionalities.Where(x => x.IsTilable);
            }
            return new ObservableCollection<Functionality>(Tilables_);
        }

        public ObservableCollection<FunctionalityBlockGroup> Items_ { get; set; } = new();
        public ObservableCollection<FunctionalityBlockGroup> Items { 
            get => functionalityWorkspace != null ? functionalityWorkspace.FunctionalityBlockGroups : Items_;
            set {
                Items_ = value;
                if(functionalityWorkspace != null)
                {
                    functionalityWorkspace.FunctionalityBlockGroups = Items_;
                }
            } }


        private void ItemChanged(ObservableCollection<FunctionalityBlockGroup> items_, int index)
        {
            Items = items_;
            ActiveTabIndex_ = index;
        }

        void SelectFonctionalityWorkspaceAction(Functionality functionality)
        {
            SelectedFunctionalities.Add(functionality);
            FunctionalityBlock functionalityblock = new FunctionalityBlock()
            {
                ProjectId = Convert.ToInt64(projectId),
                Code = functionality.Code,
                Name = functionality.Name,
                Username = ""
            };

            if (functionalityblock != null && functionalityWorkspace.FunctionalityBlockGroups.Count > 0)
            {
                //int pos = functionalityWorkspace.FunctionalityBlockGroups[ActiveTabIndex_]
                //    .BlockListChangeHandler
                //    .GetItems()
                //    .ToList()
                //    .FindIndex(x => !string.IsNullOrWhiteSpace(x.Code) && x.Code.Equals(functionalityblock.Code));
                //if (pos < 0)
                //{
                //    functionalityWorkspace.FunctionalityBlockGroups[ActiveTabIndex_].AddBlock(functionalityblock);
                //}
                functionalityWorkspace.FunctionalityBlockGroups[0].AddBlock(functionalityblock);
            }
        }

        public async Task AddToFunctionalityBlockGroup()
        {
            await FunctionalityService.SaveGroup(functionalityWorkspace.FunctionalityBlockGroups[ActiveTabIndex_], projectId);
            PopupVisible = false;

            if (SelectedFunctionalities.Count != 0)
            {
                foreach (Functionality fnt in SelectedFunctionalities)
                {
                    AvailableFunctionalities.Remove(fnt);
                }
                SelectedFunctionalities.Clear();
            }
        }

        public bool IsEmptyAddFunc()
        {
            return SelectedFunctionalities.Count == 0 ? false : true;
        }
        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                AppState.CanShowCustomized = true;
                AppState.CanShowStandard = true;
                AppState.StandardShowHandler += ShowStandardPopup;
                AppState.CustomizedShowHandler += ShowCustomizedPopup;
                AppState.BeforeCloseProjectHander += BeforCloseProject;
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        public async Task StateChanged()
        {
            await InvokeAsync(StateHasChanged);
        }

        protected override void OnInitialized()
        {
            base.OnInitialized();
            projectId = AppState.ProjectId.ToString();
            AppState.StateChanged += StateChanged;
        }

        public ValueTask DisposeAsync()
        {

            AppState.StandardShowHandler -= ShowStandardPopup;
            AppState.CustomizedShowHandler -= ShowCustomizedPopup;
            AppState.CanShowCustomized = false;
            AppState.CanShowStandard = false;
            AppState.StateChanged -= StateChanged;
            AppState.BeforeCloseProjectHander -= BeforCloseProject;
            return ValueTask.CompletedTask;
        }
        void StandartEvent()
        {
            try
            {
                AppState.StandardShow();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            finally
            {
                StateHasChanged();
            }
        }

        public void BeforCloseProject()
        {
            ActiveTabIndex_ = 0;
        }

        public void ShowStandardPopup()
        {
            PopupVisible = true;
            StateHasChanged();
        }
        public void ShowCustomizedPopup()
        {
            //PopupVisible = true;
            //StateHasChanged();
        }


        public async void DeleteFunctionalityBlockGroup(FunctionalityBlockGroup FBlockGroup)
        {
            string result = await FunctionalityService.DeleteFunctionalityBlockGroup(FBlockGroup);
            if (result.Equals("true"))
            {
                ToastService.ShowSuccess(AppState["FunctionalityBlockGroup.SuccessFullyDeleted"]);
            }
            else
            {
                ToastService.ShowError(result);
            }
        }

    }
}

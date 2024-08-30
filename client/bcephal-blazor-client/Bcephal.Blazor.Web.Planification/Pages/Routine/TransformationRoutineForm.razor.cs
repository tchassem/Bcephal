using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Planification.Services;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Bcephal.Models.Reconciliation;
using Bcephal.Models.Routines;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Planification.Pages.Routine
{
    public partial class TransformationRoutineForm : Form<TransformationRoutine, BrowserData>
    {
        #region :: Properties and attributes section ::

        [Inject] IJSRuntime JSRuntime { get; set; }
        [Inject] TransformationRoutineService TRoutineService { get; set; }
        [Inject] private WebSocketAddress WebSocketAddress { get; set; }
        [Inject] public IToastService ToastService { get; set; }
        [Inject] public GrilleService GrilleService { get; set; }

        public override string LeftTitle { get { return AppState["Trans.rout"]; ; } }
        public override string LeftTitleIcon { get { return "bi-file-plus"; } }
        private TransformationRoutine TransformationRoutine { get => EditorData.Item; }

        private string LabelWidth { get; set; } = Constant.GROUP_INFOS_LABEL_WIDTH;
        private string TextWidth { get; set; } = Constant.GROUP_INFOS_TEXT_WIDTH;

        private bool isSmallScreen;
        private ObservableCollection<GrilleBrowserData> grilles { get; set; }
        ObservableCollection<HierarchicalData> attributes;
        ObservableCollection<HierarchicalData> measures;
        ObservableCollection<HierarchicalData> periods;
        TransformationRoutineItem ChangedItem;
        //private TransformationRoutineItem tempItem = new TransformationRoutineItem() { Type = DimensionType.BILLING_EVENT };
        #endregion

        public bool Editable
        {
            get
            {   if(EditorData != null)
                {
                    var first = AppState.PrivilegeObserver.CanCreatedTransformationRoutine;
                    var second = AppState.PrivilegeObserver.CanEditTransformationRoutine(EditorData.Item);
                    return first || second;
                }
                return false;
            }
        }

        #region :: Methods section ::
        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            var page = await GrilleService.Search<GrilleBrowserData>(new Models.Grids.Filters.BrowserDataFilter() { ShowAll = true });
            grilles = page.Items;

            //if (attributes == null && Entities != null && Entities.Count > 0)
            //{
            //    LoadAttributes();
            //}

            if (measures == null && EditorData != null)
            {
                measures = EditorData.Measures.Select(p => (HierarchicalData)p).ToObservableCollection();
            }
            if (periods == null && EditorData != null)
            {
                periods = EditorData.Periods.Select(p => (HierarchicalData)p).ToObservableCollection();
            }
        }

        public override string GetBrowserUrl { get => Route.BROWSER_TRANSFORMATION_ROUTINE; set => base.GetBrowserUrl = value; }

        protected override void OnAfterRender(bool firstRender)
        {
            if (AppState.CanRun == false && EditorData != null && EditorData.Item != null && EditorData.Item.Id.HasValue)
            {
                AppState.RunHander += RunRoutine;
                AppState.CanRun = true;
            }

            //if (attributes == null && Entities != null && Entities.Count > 0)
            //{
            //    LoadAttributes();
            //}
            if (measures == null && EditorData != null)
            {
                measures = EditorData.Measures.Select(p => (HierarchicalData)p).ToObservableCollection();
                StateHasChanged();
            }
            if (periods == null && EditorData != null)
            {
                periods = EditorData.Periods.Select(p => (HierarchicalData)p).ToObservableCollection();
                StateHasChanged();
            }
        }
        protected override TransformationRoutineService GetService()
        {
            return TRoutineService;
        }

        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        protected override string DuplicateName()
        {
            return AppState["duplicate.transformationRoutine.name", EditorData.Item.Name];
        }

        private void LoadAttributes()
        {
            List<Models.Dimensions.Attribute> attributes_ = new();
            if (Entities != null && Entities.Count > 0)
            {
                foreach (Models.Dimensions.Entity entity in Entities)
                {
                    foreach (Models.Dimensions.Attribute attribute in entity.Attributes)
                    {
                        attributes_.Add(attribute);
                        attributes_.AddRange(attribute.Descendents);
                    }
                }
            }
            attributes = new ObservableCollection<HierarchicalData>(attributes_);
        }

        public override async ValueTask DisposeAsync()
        {
            AppState.Update = false;
            AppState.CanRun = false;
            AppState.RunHander -= RunRoutine;
            await base.DisposeAsync();
        }

        public async void RunRoutine()
        {
            if (TransformationRoutine.ItemListChangeHandler.Items.Count > 0)
            {
                try
                {
                    AppState.ShowLoadingStatus();
                    if (AppState.Update)
                    {
                        AppState.Save();
                    }

                    // await JSRuntime.InvokeVoidAsync("console.log", "Try running a new transformation routine");
                    SocketJS Socket = new SocketJS(WebSocketAddress, null, JSRuntime, AppState, true);
                    bool valueClose = false;
                    bool valueError = false;
                    Socket.CloseHandler += () =>
                    {
                        if (!valueClose && !valueError)
                        {
                            AppState.HideLoadingStatus();
                            ToastService.ShowSuccess(AppState["trans.routine.run.success"], AppState["Loader"]);
                            valueClose = true;
                        }
                    };
                    Socket.ErrorHandler += (errorMessage) =>
                    {
                        if (!valueError)
                        {
                            AppState.HideLoadingStatus();
                            ToastService.ShowError((string)errorMessage, AppState["Error"]);
                            valueError = true;
                        }
                    };
                    Socket.SendHandler += () =>
                    {
                        Socket.FullyProgressbar.FullBase = false;
                        AppState.CanLoad = false;
                        string data = GetService().Serialize(EditorData.Item);
                        Socket.send(data);
                        AppState.HideLoadingStatus();
                    };
                    await GetService().ConnectSocketJS(Socket, "");
                }
                catch (Exception ex)
                {
                    Error.ProcessError(ex);
                }
                finally
                {
                    AppState.HideLoadingStatus();
                    StateHasChanged();
                }
            }
            else
            {
                ToastService.ShowError(AppState["trans.routine.run.error"], AppState["Loader"]);
            }
        }

        protected void OnTRoutineItemChanged(TransformationRoutineItem item)
        {
            if (!TransformationRoutine.ItemListChangeHandler.GetItems().Contains(item))
            {
                TransformationRoutine.AddItem(item);
            }
            else
            {
                TransformationRoutine.UpdateItem(item);
            }
            AppState.Update = true;
            ChangedItem = item;
            StateHasChanged();
        }

        // Ceci est le handler du ListBox Widget de selection des attributs de classes pour grouping
        protected void OnRoutineItemDeleted(TransformationRoutineItem item)
        {
            TransformationRoutine.DeleteOrForgetItem(item);
            AppState.Update = true;
            StateHasChanged();
        }

        #endregion
    }
}

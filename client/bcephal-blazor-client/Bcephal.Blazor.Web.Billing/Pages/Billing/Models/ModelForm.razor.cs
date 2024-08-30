using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Billing.Services;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Utils;
using Bcephal.Models.Billing.Model;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;
using Bcephal.Models.Grids;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Models
{
    public partial class ModelForm : Form<BillingModel, BrowserData>
    {
        public override string LeftTitle { get { return AppState["Functionality.new.billingModel"]; } }

        public override string LeftTitleIcon { get { return "bi-file-plus"; } }

        [Parameter] public string Filterstyle { get; set; }
        [Parameter] public bool CanRefreshGrid { get; set; } = true;
        [Parameter] public int ActiveTabIndexFilter { get; set; } = 0;
        [Inject] BillingModelService BillingModelService { get; set; }
        [Inject] public BillingTemplateService BillingTemplateService { get; set; }
        [Inject] private WebSocketAddress WebSocketAddress { get; set; }
        [Inject] IJSRuntime JSRuntime { get; set; }

        private string FilterKey { get; set; } = "FilterKey";

        private int ActiveTabIndexFilterBinding
        {
            get => ActiveTabIndexFilter;
            set
            {
                ActiveTabIndexFilter = value;
                InvokeAsync(StateHasChanged);
            }
        }

        public UniverseFilter FilterBinding
        {
            get { return EditorData.Item.Filter; }
            set
            {
                EditorDataBinding.Item.Filter = value;
            }
        }

        protected override BillingModelService GetService()
        {
            return BillingModelService;
        }

        public override async ValueTask DisposeAsync()
        {
            await base.DisposeAsync();
            AppState.Update = false;
            AppState.CanLoad = false;
            AppState.CanRun = false;
            AppState.RunHander -= RunBillingModel;
        }

        public bool Editable
        {
            get
            {
                var first = AppState.PrivilegeObserver.CanCreatedBillingModel;
                var second = AppState.PrivilegeObserver.CanEditBillingModel(EditorData.Item);
                return first || second;
            }
        }
        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }
        

        public BillingModelEditorData BillingModelEditorData { 
            get => GetEditorData();
            set {
                EditorDataBinding = value;
            } 
        }

        public async void RunBillingModel()
        {
            try
            {
                AppState.ShowLoadingStatus();
                if (AppState.Update)
                {
                    AppState.Save();
                }

                await JSRuntime.InvokeVoidAsync("console.log", "Attempt to handling log of billing model!");
                SocketJS Socket = new SocketJS(WebSocketAddress, null, JSRuntime, AppState, true);
                bool valueClose = false;
                bool valueError = false;
                Socket.CloseHandler += () =>
                {
                    if (!valueClose && !valueError)
                    {
                        AppState.HideLoadingStatus();
                        ToastService.ShowSuccess(AppState["billing.model.run.success"], AppState["Loader"]);
                        valueClose = true;
                    }
                };
                Socket.ErrorHandler += (errorMessage) =>
                {
                    if (!valueError)
                    {
                        AppState.HideLoadingStatus();
                        ToastService.ShowError((string)errorMessage, AppState["billing.model.run.error"]);
                        valueError = true;
                    }
                };
                Socket.SendHandler += () =>
                {
                    Socket.FullyProgressbar.FullBase = true;
                    AppState.CanLoad = false;
                    string data = GetService().Serialize(EditorData.Item.Id);
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
            }
        }

        public override string GetBrowserUrl { get => Route.BILLING_MODEL_BROWSER; set => base.GetBrowserUrl = value; }
        private BillingModelEditorData GetEditorData()
        {
            return (BillingModelEditorData)EditorDataBinding;
        }
        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                AppState.CanRun = true;
                AppState.RunHander += RunBillingModel;
            }
            await base.OnAfterRenderAsync(firstRender);
        }

        bool IsSmallScreen;

        #region Internal properties and attributes

        int ActiveTabIndex { get; set; } = 0;

        public ObservableCollection<HierarchicalData> AttributeList { get; set; }
        public ObservableCollection<HierarchicalData> MesureList { get; set; }

        #endregion

        #region Region reserved for Methods

        

        protected override void AfterInit(EditorData<BillingModel> EditorData)
        {
            base.AfterInit(EditorData);
            try
            {

                InitMesureList();
                InitAttributeList();
            }
            catch(Exception ex)
            {
                Error.ProcessError(ex);
            }
        }


        // Cette methode construit la liste hiérarchique des attributs des classes de Bcephal
        // qui sont chargés dans le combo box du Card Grouping Items et le tab Enrichment
        private void InitAttributeList()
        {
            int offset = 0;
            List<HierarchicalData> items = new List<HierarchicalData>();
            while (EditorData != null && EditorData.Models != null && offset < EditorData.Models.Count)
            {
                Bcephal.Models.Dimensions.Model model = EditorData.Models[offset];
                int offset2 = 0;
                while (model != null && model.Entities != null && offset2 < model.Entities.Count)
                {
                    items.Add(model.Entities[offset2]);
                    offset2++;
                }
                offset++;
            }
            items.BubbleSort();
            AttributeList = new ObservableCollection<HierarchicalData>(items);
        }

        private void InitMesureList()
        {
            int offset = 0;
            List<HierarchicalData> items = new List<HierarchicalData>();
            while (EditorData != null && EditorData.Models != null && offset < EditorData.Models.Count)
            {
                Bcephal.Models.Dimensions.Measure mesure = EditorData.Measures[offset];
                int offset2 = 0;
                while (mesure != null && mesure.Children != null && offset2 < mesure.Children.Count)
                {
                    items.Add(mesure.Children[offset2]);
                    offset2++;
                }
                offset++;
            }
            items.BubbleSort();
            MesureList = new ObservableCollection<HierarchicalData>(items);
        }

        #endregion

    }
}

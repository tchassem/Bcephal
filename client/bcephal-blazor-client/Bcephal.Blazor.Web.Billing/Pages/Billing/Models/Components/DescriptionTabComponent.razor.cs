using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Billing.Services;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Billing.Invoices;
using Bcephal.Models.Billing.Model;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids.Filters;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Models.Components
{
    public partial class DescriptionTabComponent : ComponentBase
    {

        #region Injected properties

        [Inject]
        public AppState AppState { get; set; }

        [Inject]
        private IJSRuntime JSRuntime { get; set; }

        [Inject]
        public BillingModelService BillingModelService { get; set; }

        [Inject]
        public BillingTemplateService BillingTemplateService { get; set; }

        [Parameter]
        public BillingModelEditorData BillingModelEditorData { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> AttributeList { get; set; }

        [Parameter]
        public EventCallback<BillingModelEditorData> BillingModelEditorDataChanged { get; set; }

        [Parameter]
        public BillingDescription BillingDescription { get; set; }
        [Parameter]
        public bool Editable { get; set; }
        #endregion

        #region Internal properties and attributes

        IEnumerable<string> BillingCompanies = new List<String>(){
        "Company 1",
        "Company 2",
        "Company 3",
        "Company 4",
    };

        
        private BrowserDataPage<BillingTemplateBrowserData> BillTemplates { get; set; } = new BrowserDataPage<BillingTemplateBrowserData>();

        #endregion

        #region Properties binded to the form items
        // La plupart des éléments ici est liée à une propriété dans BillingModelEditorData.Item ( BillingModel )

        // tab Description
        private string PeriodSide
        {
            get
            {
                BillingModelPeriodSide period = BillingModelPeriodSide.GetByCode(BillingModelEditorData.Item.PeriodSide);
                if (period != null)
                {
                    return period.GetText(text => AppState[text]);
                }
                return null;
            }
            set
            {
                BillingModelEditorData.Item.PeriodSide = BillingModelPeriodSide.ALL.GetBillingModelPeriodSide(value, text => AppState[text]).code;
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }

        private string PeriodGranularity
        {
            get 
            {
                BillingModelPeriodGranularity granular = BillingModelPeriodGranularity.GetByCode(BillingModelEditorData.Item.PeriodGranularity);
                if(granular != null)
                {
                    return granular.GetText(text => AppState[text]);
                }
                return null;
            }
            set
            {
                BillingModelEditorData.Item.PeriodGranularity = BillingModelPeriodGranularity.WEEK.GetBillingModelPeriodGranularity(value, text => AppState[text]).code;
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }

        private string Currency
        {
            get { return BillingModelEditorData.Item.Currency; }
            set
            {
                BillingModelEditorData.Item.Currency = value;
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }
        private bool SeparateInvoicePerPeriod
        {
            get { return BillingModelEditorData.Item.SeparateInvoicePerPeriod; }
            set
            {
                BillingModelEditorData.Item.SeparateInvoicePerPeriod = value;
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }
        private string BillingCompanyCode
        {
            get { return BillingModelEditorData.Item.BillingCompanyCode; }
            set
            {
                BillingModelEditorData.Item.BillingCompanyCode = value;
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }
        private long? BillingInvoiceNbr
        {
            get { return BillingModelEditorData.Item.InvoiceSequenceId; }
            set
            {
                BillingModelEditorData.Item.InvoiceSequenceId = value.HasValue ? value.Value : 0;
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }
        
        private bool SelectPeriodAtRuntime
        {
            get { return BillingModelEditorData.Item.SelectPeriodAtRuntime; }
            set
            {
                BillingModelEditorData.Item.SelectPeriodAtRuntime = value;
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }
        private string BillTemplateCode
        {
            get { return BillingModelEditorData.Item.BillTemplateCode; }
            set
            {
                BillingModelEditorData.Item.BillTemplateCode = value;
                StateHasChanged_();
            }
        }
        private bool IncludeZeroAmountEvents
        {
            get { return BillingModelEditorData.Item.IncludeZeroAmountEvents; }
            set
            {
                BillingModelEditorData.Item.IncludeZeroAmountEvents = value;
                StateHasChanged_();
            }
        }
        private bool UseVat
        {
            get { return BillingModelEditorData.Item.UseVat; }
            set
            {
                BillingModelEditorData.Item.UseVat = value;
                StateHasChanged_();
            }
        }
        private bool OrderItems
        {
            get { return BillingModelEditorData.Item.OrderItems; }
            set
            {
                BillingModelEditorData.Item.OrderItems = value;
                StateHasChanged_();
            }
        }

        IEnumerable<string> orderItemsOption = new List<string>() {
            "Asc",
            "Desc",
        };

        public string currentOrderOption {
            get {
                if (BillingModelEditorData.Item.OrderItemsAsc)
                {
                    return "Asc";
                }
                else
                {
                    return "Desc";
                }
            }
            set {
                if (value== "Asc")
                {
                    BillingModelEditorData.Item.OrderItemsAsc = true;
                }
                else
                {
                    BillingModelEditorData.Item.OrderItemsAsc = false;
                }
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        } 

        private bool BuildCommunicationMessage
        {
            get { return BillingModelEditorData.Item.BuildCommunicationMessage; }
            set
            {
                BillingModelEditorData.Item.BuildCommunicationMessage = value;
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }
        private bool DueDateCalculation
        {
            get { return BillingModelEditorData.Item.DueDateCalculation; }
            set
            {
                BillingModelEditorData.Item.DueDateCalculation = value;
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }
        private bool InvoiceDateCalculation
        {
            get { return BillingModelEditorData.Item.InvoiceDateCalculation; }
            set
            {
                BillingModelEditorData.Item.InvoiceDateCalculation = value;
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }
        private PeriodValue FromDate
        {
            get
            {
                if (BillingModelEditorData.Item.FromDateValue == null)
                {
                    BillingModelEditorData.Item.FromDateValue = new PeriodValue()
                    {
                        DateOperator = PeriodOperator.TODAY,
                        DateValue = DateTime.Today
                    };
                }
                return BillingModelEditorData.Item.FromDateValue;
            }
            set
            {
                BillingModelEditorData.Item.FromDateValue = value;
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }
        private PeriodValue ToDate
        {
            get
            {
                if (BillingModelEditorData.Item.ToDateValue == null)
                {
                    BillingModelEditorData.Item.ToDateValue = new PeriodValue()
                    {
                        DateOperator = PeriodOperator.TODAY,
                        DateValue = DateTime.Today
                    };
                }
                return BillingModelEditorData.Item.ToDateValue;
            }
            set
            {
                BillingModelEditorData.Item.ToDateValue = value;
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }
        private PeriodValue DueDate
        {
            get
            {
                if (BillingModelEditorData.Item.DueDateValue == null)
                {
                    BillingModelEditorData.Item.DueDateValue = new PeriodValue()
                    {
                        DateOperator = PeriodOperator.TODAY,
                        DateValue = DateTime.Today
                    };
                }
                return BillingModelEditorData.Item.DueDateValue;
            }
            set
            {
                BillingModelEditorData.Item.DueDateValue = value;
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }
        private PeriodValue InvoiceDate
        {
            get
            {
                if (BillingModelEditorData.Item.InvoiceDateValue == null)
                {
                    BillingModelEditorData.Item.InvoiceDateValue = new PeriodValue()
                    {
                        DateOperator = PeriodOperator.TODAY,
                        DateValue = DateTime.Today
                    };
                }
                return BillingModelEditorData.Item.InvoiceDateValue;
            }
            set
            {
                BillingModelEditorData.Item.InvoiceDateValue = value;
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }

        
        private bool IsGroupingListNotEmpty
        {
            get
            {
                if (BillingModelEditorData == null || BillingModelEditorData.Item == null || BillingModelEditorData.Item.GroupingItemListChangeHandler == null)
                {
                    return false;
                }

                // I've done this like that because according to the doc, .Count is the fastest way to check it
                // since it doesn't need to get the enumerator { for ICollection class, .Count is faster than .Any( ) that is faster than .Count( ) }
                return BillingModelEditorData.Item.GroupingItemListChangeHandler.Items.Count > 0;
            }
        }
        private bool IsGroupingListHasManyItems
        {
            get
            {
                return IsGroupingListNotEmpty && BillingModelEditorData.Item.GroupingItemListChangeHandler.Items.Count > 1;
            }
        }
        IEnumerable<BillingModelGroupingItem> SelectedGroupingItems;
        private bool IsGroupingListFirstItemSelected { get; set; } = false;
        private bool IsGroupingListLastItemSelected { get; set; } = false;
        private bool IsParameterListNotEmpty
        {
            get
            {
                if (BillingModelEditorData == null || BillingModelEditorData.Item == null || BillingModelEditorData.Item.ParameterListChangeHandler == null)
                {
                    return false;
                }

                // I've done this like that because according to the doc, .Count is the fastest way to check it
                // since it doesn't need to get the enumerator { for ICollection class, .Count is faster than .Any( ) that is faster than .Count( ) }
                return BillingModelEditorData.Item.ParameterListChangeHandler.Items.Count > 0;
            }
        }
        private bool IsParameterListHasManyItems
        {
            get
            {
                return IsParameterListNotEmpty && BillingModelEditorData.Item.ParameterListChangeHandler.Items.Count > 1;
            }
        }
        private bool IsParameterListFirstItemSelected { get; set; } = false;
        private bool IsParameterListLastItemSelected { get; set; } = false;
        private bool IsGroupingListItemsSelected { get; set; } = false;
        private bool IsParameterListItemsSelected { get; set; } = false;
        private ObservableCollection<BillingModelPivot> PivotList
        {
            get
            {
                // Ceci est à revoir lorsque le mock des Pivots sera retiré
                if (!BillingModelEditorData.Item.PivotListChangeHandler.Items.Any())
                {
                    BillingModelEditorData.Item.AddPivot(new BillingModelPivot() { AttributeId = 1, Name = "Client" });
                    BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
                }

                return BillingModelEditorData.Item.PivotListChangeHandler.Items;
            }
        }
        // end of tab Description

        IEnumerable<string> PeriodGranularities;
        IEnumerable<string> PeriodSides;
        #endregion

        #region Region reserved for Methods

        protected async override Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            BillTemplates = await BillingTemplateService.Search(new BrowserDataFilter() { ShowAll = true });
            PeriodGranularities = BillingModelPeriodGranularity.WEEK.GetAll(text => AppState[text]);
            PeriodSides = BillingModelPeriodSide.ALL.GetAll(text => AppState[text]);
        }

        public void StateHasChanged_()
        {
            StateHasChanged();
            AppState.Update = true;
        }

        private void RefreshGroupingItemsMoveButtons()
        {
            if (SelectedGroupingItems != null)
            {
                IsGroupingListFirstItemSelected = SelectedGroupingItems.First().Position == BillingModelEditorData.Item.GroupingItemListChangeHandler.Items.First().Position;
                IsGroupingListLastItemSelected = SelectedGroupingItems.Last().Position == BillingModelEditorData.Item.GroupingItemListChangeHandler.Items.Last().Position;
            }
        }

        private DimensionType ParseStringToDimensiontype(string s)
        {
            switch (s)
            {
                case "MEASURE":
                    return DimensionType.MEASURE;
                case "PERIOD":
                    return DimensionType.PERIOD;
                case "ATTRIBUTE":
                default:
                    return DimensionType.ATTRIBUTE;
            }
        }

        private string ParseDimensiontypeToString(DimensionType dm)
        {
            return dm.ToString();
        }

        #endregion

        #region Form items change or selection handlers
        // Ici on trouve tous les handlers qui écoutent sur la modification de l'état d'un élément du formulaire

        // Si on clique sur le chechBox Select all dans la liste des billing event types,
        // alors on doit sélectionner tous les autres events de cette liste
        void SelectAllCheckedChanged(bool value)
        {
            //CheckSelectAll = value;
            if(CheckCreditNote != value)
            {
                CheckCreditNote = value;
            }
            if(CheckInvoice != value)
            {
                CheckInvoice = value;
            }
        }

        private bool CheckSelectAll_ { get; set; }
        private bool CheckSelectAll
        {
            get
            {
                return CheckSelectAll_;
            }
            set
            {
                CheckSelectAll_ = value;
                SelectAllCheckedChanged(value);

            } 
        }

        public bool CheckCreditNote_ = true;
        public bool CheckInvoice_ = false;

        private bool CheckCreditNote
        {
            get => CheckCreditNote_;
            set
            {
                CheckCreditNote_ = value;
                OthersCheckedChanged(value);

            }
        }
        private bool CheckInvoice
        {
            get
            {
                return CheckInvoice_;
            }
            set
            {
                CheckInvoice_ = value;
                OthersCheckedChanged(value);

            }
        }
        void OthersCheckedChanged(bool value)
        {
            if (!value)
            {
                CheckSelectAll = false;
            }
        }

        // Ceci est le handler du ListBox Widget de selection des attributs de classes pour grouping
        protected void GroupingListSelectionChanged(IEnumerable<BillingModelGroupingItem> values)
        {
            if (values != null && values.Any())
            {
                SelectedGroupingItems = values;
                IsGroupingListItemsSelected = true;

                RefreshGroupingItemsMoveButtons();
            }

            StateHasChanged_();
        }

        // Ceci est le handler du TreeView Widget pour la selection des attributs de classes pour grouping
        protected async void AttributeSelectionChanged(HierarchicalData selectedAttr)
        {
            // Je verifie d'abord que l'élément sélectionné n'existe pas encore dans la liste avant de l'insérer
            var match = BillingModelEditorData.Item.GroupingItemListChangeHandler.Items
                        .FirstOrDefault(el => el.Name == selectedAttr.Name && el.AttributeId == selectedAttr.Id);

            if (match == null)
            {
                BillingModelEditorData.Item.AddGroupingItem(
                    new BillingModelGroupingItem()
                    {
                        AttributeId = selectedAttr.Id,
                        Name = selectedAttr.Name
                    }
                );
               await BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
                RefreshGroupingItemsMoveButtons();
            }
            else
                await JSRuntime.InvokeVoidAsync("console.log", "Cet attribut existe déjà dans la liste !!!!");

            StateHasChanged_();
        }

        protected void MoveUpSelectedGroupingItem()
        {
            foreach (var item in SelectedGroupingItems)
            {
                BillingModelEditorData.Item.MoveUpGroupingItem(item);
            }
            BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            RefreshGroupingItemsMoveButtons();

        }

        protected void MoveDownSelectedGroupingItem()
        {
            foreach (var item in SelectedGroupingItems)
            {
                BillingModelEditorData.Item.MoveDownGroupingItem(item);
            }
            BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            RefreshGroupingItemsMoveButtons();
        }

        protected void DeleteSelectedGroupingItem()
        {
            foreach (var item in SelectedGroupingItems)
            {
                BillingModelEditorData.Item.DeleteOrForgetGroupingItem(item);
            }
            BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            IsGroupingListItemsSelected = false;
        }

        // Ceci est le handler du TreeView Widget pour la selection des attributs de classes pour Invoice
        protected async void InvoicePivotSelectionChanged(KeyValuePair<string, string> val)
        {
            // Je verifie d'abord que l'élément sélectionné n'existe pas encore dans la liste avant de l'insérer
            var match = PivotList.FirstOrDefault(el => el.Name == val.Value);

            if (match == null)
            {
                BillingModelEditorData.Item.AddPivot(
                    new BillingModelPivot()
                    {
                        Name = val.Value
                    }
                );
              await  BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
            else
                await JSRuntime.InvokeVoidAsync("console.log", "Cet attribut existe déjà dans la liste !!!!");

            StateHasChanged_();
        }

        // Ceci est le handler du TreeView Widget pour la selection des attributs de classes pour Invoice
        protected void DeleteInvoicePivot(BillingModelPivot ip)
        {
            BillingModelEditorData.Item.DeleteOrForgetPivot(ip);
            BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
        }

        public void ModelPivotHandler(HierarchicalData hierarchical)
        {
            if (!BillingModelEditorData.Item.PivotListChangeHandler.GetItems().Where(d => d.Name == hierarchical.Name).Any())
            {
                BillingModelEditorData.Item.AddPivot(new BillingModelPivot() { Name = hierarchical.Name, AttributeId = hierarchical.Id, Id = hierarchical.Id });
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }

        protected void GroupingComboboxSelectionChanged(BillingModelGroupingItem groupingItem)
        {
            // l'élément n'est ajouté que s'il n'existe aucun DriverGroup ayant le même nom que le grouping item
            // en d'autres termes, on vérifie que le groupingItem n'ait pas encore été sélectionné pour renseigner les valeurs d'exclusions
            // qui s'appliquent sur lui, afin d'éviter qu'on affiche un même groupingItem pour +sieurs DriverGroup
            if (!BillingModelEditorData.Item.DriverGroupListChangeHandler.GetItems().Where(d => d.GroupName == groupingItem.Name).Any())
            {
                BillingModelEditorData.Item.AddDriverGroup(new BillingModelDriverGroup() { GroupName = groupingItem.Name });
                BillingModelEditorDataChanged.InvokeAsync(BillingModelEditorData);
            }
        }

       

        #endregion

        string heightCall = "var(--bc-dx-tabs-content-panel-height)";

    }
}

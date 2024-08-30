using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Base.Shared.Utils;
using Bcephal.Blazor.Web.Billing.Services;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Billing.Model;
using Bcephal.Models.Filters;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Models.Components
{
    public partial class ParametersTabComponent : ComponentBase
    {
        #region Injected properties

        [Inject]
        public AppState AppState { get; set; }
        [Parameter]
        public EditorData<BillingModel> EditorData { get; set; }
        [Parameter]
        public EventCallback<EditorData<BillingModel>> EditorDataChanged { get; set; }
        [Parameter]
        public ObservableCollection<HierarchicalData> AttributeList { get; set; }
        [Parameter]
        public ObservableCollection<HierarchicalData> MesureList { get; set; }

        DxListBox<BillingModelParameter, BillingModelParameter> DxListBoxRef { get; set; }
        [Inject] BillingModelService BillingModelService { get; set; }
        [Inject] BillingTemplateService BillingTemplateService { get; set; }
        [Inject] public IToastService toastService { get; set; }
        private RenderFormContent renderFormContent { get; set; }
        #endregion

        #region Input Parameters
        [Parameter]
        public bool Editable { get; set; }
        #endregion

        #region Internal properties and attributes

        bool showModalBillingParam = false;
        IEnumerable<DimensionType> DimensionTypes = new List<DimensionType>(){
            DimensionType.ATTRIBUTE,
            DimensionType.MEASURE,
            DimensionType.PERIOD,
        };

        private bool nameHasFocus = false;

        #endregion

        #region Properties binded to the form items
        // La plupart des éléments ici est liée à une propriété dans EditorData.Item ( BillingModel )

        // tab Parameters
        // Ce param est sélectionné  est affiché dans le modal
        private bool IsParameterListNotEmpty
        {
            get
            {
                if (EditorData == null || EditorData.Item == null || EditorData.Item.ParameterListChangeHandler == null)
                {
                    return false;
                }

                // I've done this like that because according to the doc, .Count is the fastest way to check it
                // since it doesn't need to get the enumerator { for ICollection class, .Count is faster than .Any( ) that is faster than .Count( ) }
                return EditorData.Item.ParameterListChangeHandler.Items.Count > 0;
            }
        }
        private bool IsParameterListHasManyItems
        {
            get
            {
                return IsParameterListNotEmpty && EditorData.Item.ParameterListChangeHandler.Items.Count > 1;
            }
        }
        private bool IsParameterListFirstItemSelected { get; set; } = false;
        private bool IsParameterListLastItemSelected { get; set; } = false;
        BillingModelParameter OpenedBMParameter { get; set; } = new BillingModelParameter();
        private bool IsParameterListItemsSelected { get; set; } = false;

        IEnumerable<BillingModelParameter> SelectedParamItems;
        // end of tab Parameters

        #endregion

        #region Region reserved for Methods

        protected async override Task OnAfterRenderAsync(bool firstRender)
        {
            if (!firstRender && showModalBillingParam && !nameHasFocus)
            {
                nameHasFocus = true;
                await Task.Delay(100).ContinueWith(t => JsInterop.SetFocus(JSRuntime, "param-name-id"));
            }
        }


        public void StateHasChanged_()
        {
            StateHasChanged();
            AppState.Update = true;
        }

        private void RefreshParameterMoveButtons()
        {
            if (SelectedParamItems != null && SelectedParamItems.Any() && EditorData.Item.ParameterListChangeHandler.Items.Count >= 2)
            {
                IsParameterListFirstItemSelected = SelectedParamItems.First().Position == EditorData.Item.ParameterListChangeHandler.Items.First().Position;
                IsParameterListLastItemSelected = SelectedParamItems.Last().Position == EditorData.Item.ParameterListChangeHandler.Items.Last().Position;
            }
            else
            {
                IsParameterListFirstItemSelected = false;
                IsParameterListLastItemSelected = false;
            }
        }



        private void OpenModalBillingParam(BillingModelParameter bmp)
        {
            OpenedBMParameter = bmp;
            showModalBillingParam = true;
        }

        //private DimensionType ParseStringToDimensiontype(string s)
        //{
        //    switch (s)
        //    {
        //        case "MEASURE":
        //            return DimensionType.MEASURE;
        //        case "PERIOD":
        //            return DimensionType.PERIOD;
        //        case "ATTRIBUTE":
        //        default:
        //            return DimensionType.ATTRIBUTE;
        //    }
        //}

        //private string ParseDimensiontypeToString(DimensionType dm)
        //{
        //    return dm.ToString();
        //}

        #endregion

        #region Form items change or selection handlers
        // Ici on trouve tous les handlers qui écoutent sur la modification de l'état d'un élément du formulaire

        protected void ParameterListSelectionChanged(IEnumerable<BillingModelParameter> values)
        {
            if (values != null && values.Any())
            {
                SelectedParamItems = values;
                IsParameterListItemsSelected = true;
                RefreshParameterMoveButtons();
                StateHasChanged_();
            }
        }

        public void SelectedModelParameter(BillingModelParameter billingModelParameter)
        {
            SelectedParamItems = new List<BillingModelParameter>() { billingModelParameter };
            IsParameterListItemsSelected = true;
            RefreshParameterMoveButtons();
        }

        protected void SaveBillingModelParameter()
        {
            if (EditorData.Item.ParameterListChangeHandler.Items.Contains(OpenedBMParameter))
            {

                EditorData.Item.UpdateParameter(OpenedBMParameter);
                EditorDataChanged.InvokeAsync(EditorData);
                renderFormContent.Refresh();
               SelectedModelParameter(OpenedBMParameter);
            }
            else
            {
                if(!EditorData.Item.ParameterListChangeHandler.Items.Where(x=> (x.DimensionId == OpenedBMParameter.DimensionId && x.DimensionType.Equals(OpenedBMParameter.DimensionType))).Any())
                {
                    EditorData.Item.AddParameter(OpenedBMParameter);
                    SelectedModelParameter(OpenedBMParameter);
                }
                else
                {
                    toastService.ShowError(AppState["duplicate.parameter", OpenedBMParameter.Name]);
                }
              
            }
            OpenedBMParameter = new BillingModelParameter();
            showModalBillingParam = false;
            RefreshParameterMoveButtons();
            StateHasChanged_();
        }

        protected void MoveUpSelectedBillingModelParameter()
        {
            foreach (var item in SelectedParamItems)
            {
                EditorData.Item.MoveUpParameter(item);
            }
            RefreshParameterMoveButtons();
            StateHasChanged_();

        }

        protected void MoveDownSelectedBillingModelParameter()
        {
            foreach (var item in SelectedParamItems)
            {
                EditorData.Item.MoveDownParameter(item);
            }
            RefreshParameterMoveButtons();
            StateHasChanged_();
        }

        protected void DeleteSelectedBillingModelParameter()
        {
            foreach (var item in SelectedParamItems)
            {
                EditorData.Item.DeleteOrForgetParameter(item);
                renderFormContent.Refresh();
            }
            IsParameterListItemsSelected = false;
            SelectedParamItems = EditorData.Item.ParameterListChangeHandler.Items;
            if(SelectedParamItems != null && SelectedParamItems.Any())
            {
                SelectedModelParameter(SelectedParamItems.First());
            }
            
            EditorDataChanged.InvokeAsync(EditorData);
            AppState.Update = true;
        }

        public string Name
        {
            get { return OpenedBMParameter.Name; }
            set
            {

                OpenedBMParameter.Name = value;
                //SelectedParamItems.InvokeAsync(OpenedBMParameter);
            }
        }

        private void AttrEvtAction(HierarchicalData d)
        {
            OpenedBMParameter.DimensionName = d.Name;
            Name = d.Name;
            OpenedBMParameter.DimensionId = d.Id;
        }
        private void PeriodEvtAction(Bcephal.Models.Dimensions.Period p)
        {
            OpenedBMParameter.DimensionName = p.Name;
            Name = p.Name;
            OpenedBMParameter.DimensionId = p.Id;
        }
        private void MeasureEvtAction(Bcephal.Models.Dimensions.Measure m)
        {
            OpenedBMParameter.DimensionName = m.Name;
            Name = m.Name;
            OpenedBMParameter.DimensionId = m.Id;
        }


        public string DimensionName
        {
            get { return OpenedBMParameter.DimensionName; }
            set
            {
                OpenedBMParameter.DimensionName = value;

            }
        }
       
        #endregion
    }
}


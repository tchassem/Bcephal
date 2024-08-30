using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Entities
{
    public partial class InputFilter : ComponentBase
    {
        private string display => (removeButton == true && ExpandOtherFields == true) ? "block" : "none";
        private string display2 => ExpandField == true? "block" : "none";

        Bcephal.Blazor.Web.Base.Shared.Entities.ModalAttributeValue ModalAttributeValueRef;

        [Inject]
        private AppState AppState { get; set; }

        public bool Show { get; set; }

        [Parameter]
        public BrowserData SelectedItem { get; set; }

        [Parameter]
        public EventCallback<BrowserData> SelectedItemChanged { get; set; }

        [Parameter]
        public AttributeFilterItem modelattribute { get; set; }

        [Parameter]
        public EventCallback<AttributeFilterItem> ItemCallback { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> EntityItems { get; set; }

        [Parameter]
        public EventCallback<ObservableCollection<HierarchicalData>> EntityItemsChanged { get; set; }

        [Parameter]
        public EventCallback<AttributeFilterItem> OnClickRemove { get; set; }

        [Parameter]
        public bool removeButton { get; set; }

        [Parameter]
        public bool DisplayRemoveButton { get; set; }

        [Parameter]
        public bool DisplayFilterVerbs { get; set; } = true;

        [Parameter]
        public bool DisplayTreeViewFilterRender { get; set; } = true;

        [Parameter]
        public bool ExpandOtherFields { get; set; }

        [Parameter]
        public EventCallback<bool> ExpandOtherFieldsChanged { get; set; }

        [Parameter]
        public bool ExpandField { get; set; }

        [Parameter]
        public EventCallback<bool> ExpandFieldChanged { get; set; }

        [Parameter]
        public ObservableCollection<string> Values { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;
        [Parameter]
        public bool ShouldRender_ { get; set; } = false;
        protected override bool ShouldRender()
        {
            return ShouldRender_;
        }
        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            ShouldRender_ = false;
            await base.OnAfterRenderAsync(firstRender);
        }

        SizeMode SizeMode { get; set; }

        IEnumerable<string> AttributeOperators { get; set; }
        IEnumerable<string> FilterVerbs { get; set; }


        public string FilterVerb_
        {
            get
            {
                return modelattribute.FilterVerb.GetText(text => AppState[text]); ;
            }
            set
            {
                if (!string.IsNullOrWhiteSpace(value))
                {
                    ShouldRender_ = true;
                    modelattribute.FilterVerb = modelattribute.FilterVerb.GetFilterVerb(value, text => AppState[text]);
                    ItemCallback.InvokeAsync(modelattribute);
                }
            }

        }

        public string Operator2 { get; set; }

        public string Operator_
        {
            get
            {
                return modelattribute.Operator.GetText(text => AppState[text]);
            }
            set
            {

                if (!string.IsNullOrWhiteSpace(value))
                {
                    ShouldRender_ = true;
                    modelattribute.Operator = modelattribute.Operator.GetAttributeOperator(value, text => AppState[text]);
                    ItemCallback.InvokeAsync(modelattribute);
                }
            }
        }

        public string OpenBrackets
        {
            get { return modelattribute.OpenBrackets; }
            set
            {
                ShouldRender_ = true;
                modelattribute.OpenBrackets = value;
                ItemCallback.InvokeAsync(modelattribute);
            }
        }

        public string CloseBrackets
        {
            get { return modelattribute.CloseBrackets; }
            set
            {
                ShouldRender_ = true;
                modelattribute.CloseBrackets = value;
                ItemCallback.InvokeAsync(modelattribute);
            }
        }


        public string Value
        {
            get { return modelattribute.Value; }
            set
            {
                ShouldRender_ = true;
                modelattribute.Value = value;
                ItemCallback.InvokeAsync(modelattribute);
            }
        }


        public void AttributeChanged(HierarchicalData Attribute)
        {
            modelattribute.DimensionName = Attribute.Name;
            modelattribute.DimensionId = Attribute.Id;
            ExpandField = true;
            ShouldRender_ = true;
            ItemCallback.InvokeAsync(modelattribute);
        }


        public void ValueTextChanged(string newValue)
        {
            Value = newValue;
        }

        string key = Guid.NewGuid().ToString("d");

        


    public void OpenDialog()
    {
        Show = true;
        ShouldRender_ = true;
        StateHasChanged();
    }

    protected override void OnInitialized()
    {
        AttributeOperators = modelattribute.Operator.GetAll(text => AppState[text]);
        FilterVerbs = modelattribute.FilterVerb.GetAll(text => AppState[text]);
    }

}
}

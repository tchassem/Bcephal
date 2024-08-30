using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;

namespace Bcephal.Blazor.Web.Sourcing.Shared.Grille
{
    public partial class LinkedAttribute : ComponentBase
    {
        [Parameter]
        public string Title { get; set; }
        [Parameter]
        public bool Iskey { get; set; } = false;

        [Parameter]
        public ObservableCollection<Models.Dimensions.Attribute> Attributes { get; set; }

        [Parameter]
        public List<Models.Links.LinkedAttribute> LinkedAttributes { get; set; } = new();

        [Parameter]
        public Action<Models.Links.LinkedAttribute> AddLinkedAttribute { get; set; }

        [Parameter]
        public Action<Models.Links.LinkedAttribute> UpdateLinkedAttribute { get; set; }

        [Parameter]
        public Action<Models.Links.LinkedAttribute> RemoveLinkedAttribute { get; set; }

        [Parameter]
        public Action<Action<Models.Grids.GrilleColumn, Models.Links.LinkedAttribute>, Models.Links.LinkedAttribute> SelectedHandler { get; set; }

        [Parameter]
        public Func<long, bool> ContainHandler { get; set; }

        [Parameter]
        public string SelectedValue { get; set; } = "1";

        [Parameter]
        public EventCallback<string> SelectedValueChanged { get; set; }

        private bool display(Models.Links.LinkedAttribute linkedAttribute) => 
            linkedAttribute != null && linkedAttribute.IsPersistent || LinkedAttributes.Count > 1 ? true : false;

        private void AttributeNameChanged(Models.Links.LinkedAttribute item, string value)
        {
            item.AttributeName = value;
            Models.Dimensions.Attribute Attrib =  Attributes.Where(item => item.Name.Equals(value)).FirstOrDefault();
            if(Attrib != null)
            {
                item.AttributeId = Attrib.Id;
            }
            if (item.IsPersistent)
            {
                UpdateLinkedAttribute?.Invoke(item);
            }
            else
            {
                RemoveLinkedAttribute?.Invoke(item);
                AddLinkedAttribute?.Invoke(item);
            }
        }

        private void AttributeChanged(Models.Grids.GrilleColumn column, Models.Links.LinkedAttribute item)
        {
            bool? check = ContainHandler?.Invoke(column.DimensionId.Value);
            if (check.HasValue && check.Value)
            {
                if (column != null)
                {
                    item.AttributeId = column.DimensionId;
                    item.AttributeName = column.DimensionName;
                }
                if (item.IsPersistent)
                {
                    UpdateLinkedAttribute?.Invoke(item);
                }
                else
                {
                    RemoveLinkedAttribute?.Invoke(item);
                    AddLinkedAttribute?.Invoke(item);
                }
                StateHasChanged();
            }
        }

        private void SelectedTextBox(Models.Links.LinkedAttribute item, bool selected = false)
        {
            if (selected)
            {
                if (Iskey)
                {
                    SelectedValue = "1";
                }
                else
                {
                    SelectedValue = "2";
                }
                if (SelectedValueChanged.HasDelegate)
                {
                    SelectedValueChanged.InvokeAsync(SelectedValue);
                }

            }
            SelectedHandler?.Invoke(AttributeChanged, item);
        }
    }
}

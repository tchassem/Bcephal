using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class EditeField<T> : ComponentBase where T: HierarchicalData
    {
        [Parameter]
        public string headerTitle { get; set; }
        [Parameter]
        public ObservableCollection<T> items { get; set; }
        [Parameter]
        public EventCallback<T> SelectItemCallback { get; set; }

        [Parameter]
        public string IconCssClass { get; set; }

        [Parameter]
        public string TopProperty { get; set; } = "0px";

        [Parameter]
        public bool Editable { get; set; } = true;
        protected void SelectionChanged(TreeViewNodeEventArgs e)
        {
            SelectItemCallback.InvokeAsync((T)e.NodeInfo.DataItem);
            InvokeAsync(StateHasChanged);
        }

        public IEnumerable<HierarchicalData> TreeViewChildrenExpression(object itemsTarget)
        {
            if (itemsTarget is Models.Dimensions.Measure)
            {
                Models.Dimensions.Measure item = itemsTarget as Models.Dimensions.Measure;
                return item.Descendents as IEnumerable<Models.Dimensions.Measure>;
            }
            if (itemsTarget is Models.Dimensions.Period)
            {
                Models.Dimensions.Period item = itemsTarget as Models.Dimensions.Period;
                return item.Descendents as IEnumerable<Models.Dimensions.Period>;
            }
            //if (itemsTarget is Model)
            //{
            //    Model item = itemsTarget as Model;
            //    return item.Descendents as IEnumerable<Models.Dimensions.Attribute>;
            //}
            if (itemsTarget is Entity)
            {
                Entity item = itemsTarget as Entity;
                return item.Descendents as IEnumerable<Models.Dimensions.Attribute>;
            }
            if (itemsTarget is Models.Dimensions.Attribute)
            {
                Models.Dimensions.Attribute item = itemsTarget as Models.Dimensions.Attribute;
                return item.Descendents as IEnumerable<Models.Dimensions.Attribute>;
            }
            return new List<HierarchicalData>() as IEnumerable<HierarchicalData>;
        }
    }
}

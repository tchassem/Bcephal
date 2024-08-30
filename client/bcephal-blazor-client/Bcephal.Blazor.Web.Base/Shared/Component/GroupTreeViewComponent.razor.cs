using Bcephal.Models.Base;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class GroupTreeViewComponent<C> : ComponentBase where C : BGroup
    {
        [Parameter]
        public C SelectedItem { get; set; }

        [Parameter]
        public EventCallback<C> SelectedItemChanged { get; set; }

        bool IsOpen { get; set; } = false;

        [Parameter]
        public bool IsFieldsExpanded { get; set; }

        [Parameter]
        public ObservableCollection<C> Items { get; set; }

        [Parameter]
        public EventCallback<ObservableCollection<C>> ItemsChanged { get; set; }


        [Parameter]
        public EventCallback<C> SelectFilterItemCallback { get; set; }

        public void OnSelectItemChanged(TreeViewNodeEventArgs e)
        {
            SelectedItem = (C)e.NodeInfo.DataItem;
            SelectFilterItemCallback.InvokeAsync((C)e.NodeInfo.DataItem);
            IsOpen = false;
        }



        void OpenFilterTreeView()
        {
            IsOpen = !IsOpen;
        }


        public IEnumerable<C> TreeViewChildrenExpression(object itemsTarget)
        {

            if (itemsTarget is C)
            {
                C item = itemsTarget as C;
                return item.childrenListChangeHandler.GetItems() as IEnumerable<C>;
            }
            return new List<C>() as IEnumerable<C>;
        }


    }
}

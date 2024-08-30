using Bcephal.Blazor.Web.Dashboard.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Pages.Dashboard
{
   public partial class DashboardPanel
    {
        [Parameter]
        public EditorData<Models.Dashboards.Dashboard> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Models.Dashboards.Dashboard>> EditorDataChanged { get; set; }

        [Parameter]
        public Action<RenderFragment> RefreshView { get; set; }

        [Parameter]
        public bool IsData { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        public Models.Dashboards.DashboardItem GetItemByPosition(int position)
        {
            var items = EditorData.Item.ItemsListChangeHandler.GetItems();      
            foreach (var it in items)
            {
                if (it.Position == position)
                {
                    return it;
                }
            }           
            return null;
        }

        [Parameter]
        public string heightCallTotal { get; set; } = "var(--grid-bc-two)";
        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                RefreshView?.Invoke(null);
            }
            IsData = true;
            return base.OnAfterRenderAsync(firstRender);
        }

        private Dictionary<int, DimensionPanel> GetDimensionPanels()
        {
            Dictionary<int, DimensionPanel> PositionDimension = new();
            var items_ = EditorData.Item.ItemsListChangeHandler.Items;
            if(items_ != null && items_.Any())
            {
                items_.ToList().ForEach(
                    
                    x => { PositionDimension.Add(x.Position, x); }
                );
            }
            return PositionDimension;
        }
    }
}

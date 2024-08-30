using Bcephal.Blazor.Web.Dashboard.Services;
using Bcephal.Models.Base;
using Microsoft.AspNetCore.Components;
using System;
using System.Linq;


namespace Bcephal.Blazor.Web.Dashboard.Shared.Dashboard
{
    public partial class DashboardConfigDesignItemContent : ComponentBase
    {
        [Parameter]
        public EditorData<Models.Dashboards.Dashboard> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Models.Dashboards.Dashboard>> EditorDataChanged { get; set; }

        [Inject]
        public DashboardService DashboardService { get; set; }

        [Parameter]
        public Action<NodeElement> SelectItemHandler { get; set; }

        [Parameter]
        public NodeElement SelectItemNode { get; set; }

        [Parameter]
        public EventCallback<NodeElement> SelectItemNodeChanged { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;
        public void UpdateItem(Models.Dashboards.DashboardItem item)
        {
            EditorData.Item.UpdateItem(item);
            EditorDataChanged.InvokeAsync(EditorData);
        }

        private Models.Dashboards.DashboardItem GetItemByPosition(int position)
        {
            Models.Dashboards.DashboardItem item = EditorData.Item.ItemsListChangeHandler.GetItems().Where(ite => ite.Position == position).FirstOrDefault();
            return item;
        }

        public void AddItem(Models.Dashboards.DashboardItem item)
        {
            EditorData.Item.AddItem(item);
            EditorDataChanged.InvokeAsync(EditorData);
        }

        public async void DeleteItem(Models.Dashboards.DashboardItem item)
        {
            EditorData.Item.RemoveOrForgetItem(item);
            await EditorDataChanged.InvokeAsync(EditorData);
        }


    }
}

using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Bcephal.Models.Dimensions;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Shared.Reporting.pivot
{
   public partial class PivotTableComponent
    {
        [Parameter]
        public EditorData<DashboardReport> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<DashboardReport>> EditorDataChanged { get; set; }


        [Inject]
        public AppState AppState { get; set; }

        int ActiveTabIndex { get; set; } = 0;

        [Parameter]
        public ObservableCollection<HierarchicalData> Entities { get; set; } = new();

        [Parameter]
        public Action RefreshDesignContentHandler { get; set; }
        [Parameter]
        public Action RefreshDataContentHandler { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        public ObservableCollection<Dimension> Items { get; set; } = new ObservableCollection<Dimension>();

        public EditorData<DashboardReport> EditorDataBinding { get => EditorData;
            set {
                EditorData = value;
                EditorDataChanged.InvokeAsync(EditorData);
            } }

        private void RefreshDesignContent()
        {
            RefreshDesignContentHandler?.Invoke();
        }
        private void RefreshDataContent()
        {
            RefreshDataContentHandler?.Invoke();
        }
    }
}

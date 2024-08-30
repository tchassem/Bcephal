using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Joins;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Shared.Joins
{
    public partial class Concatenation : ComponentBase
    {
        [Parameter]
        public string Title { get; set; }
        [Parameter]
        public ObservableCollection<HierarchicalData> Entities { get; set; }
        [Inject]
        public AppState AppState { get; set; }
        [Parameter]
        public bool ShowModal { get; set; } = false;

        [Parameter]
        public EventCallback<bool> ShowModalChanged { get; set; }

        [Parameter]
        public JoinColumn JoinColumn { get; set; }
        public ObservableCollection<SmallGrilleColumn> Columns1 { get; set; }
        public JoinCondition JoinCondition { get; set; }

        [Parameter]
        public EditorData<Join> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Join>> EditorDataChanged { get; set; }
        public IEnumerable<JoinGrid> JoinGrids { get; set; } = new List<JoinGrid> { };

        public JoinColumnConcatenateItem JoinColumnConcatenateItem { get; set; }

 
        private void OkHandler()
        {
            ShowModal = false;
            ShowModalChanged.InvokeAsync(ShowModal);
        }
        private void CloseHandler()
        {
            ShowModal = false;
            ShowModalChanged.InvokeAsync(ShowModal);
        }

        public void Dispose()
        {
            ShowModal = false;
            ShowModalChanged.InvokeAsync(ShowModal);
        }


    }
}

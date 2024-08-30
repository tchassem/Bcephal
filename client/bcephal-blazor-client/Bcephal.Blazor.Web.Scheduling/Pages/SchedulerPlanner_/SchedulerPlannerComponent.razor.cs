using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Planners;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Scheduling.Pages.SchedulerPlanner_
{
    public partial class SchedulerPlannerComponent
    {

        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public EditorData<SchedulerPlanner> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<SchedulerPlanner>> EditorDataChanged { get; set; }

        private SchedulerPlannerEditorData SchedulerPlannerEditorData => GetEditorData();

        private SchedulerPlannerEditorData GetEditorData()
        {
            return (SchedulerPlannerEditorData) EditorData;
        }

        protected override async Task OnInitializedAsync()
        {
            AppState.Update = true;
            await base.OnInitializedAsync();
        }
        private void UpdateSchedulerPlanner(EditorData<SchedulerPlanner> EditorData)
        {
            EditorDataChanged.InvokeAsync(EditorData);

        }

        
    }
}

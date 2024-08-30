using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Planners;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Scheduling.Pages.SchedulerPlanner_
{
    public partial class SchedulerPlannerItemActionComponent
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public SchedulerPlannerEditorData SchedulerPlannerEditorData { get; set; }

        [Parameter]
        public SchedulerPlannerItemAction Action { get; set; }

        [Parameter]
        public EventCallback<SchedulerPlannerItemAction> ActionChanged { get; set; }

        public List<string> codes { get; set; } = new();

        SchedulerPlannerItemDecision schedulerPlannerItemDecision => SchedulerPlannerItemDecision.CONTINUE;
        private ObservableCollection<string> SchedulerPlannerItemDecision_ { get => schedulerPlannerItemDecision.GetAll(text => AppState[text]); }

        protected override async Task OnInitializedAsync()
        {
            if(Action == null)
            {
                Action = new();
            }

            foreach (var t in SchedulerPlannerEditorData.Item.ItemListChangeHandler.Items)
            {
                codes.Add(t.Code);
            }
            await base.OnInitializedAsync();
        }

        public string Decision
        {

            get
            {
                if (Action != null && Action.Decision != null)
                {
                    return Action.ItemDecision.GetText(text => AppState[text]);
                }
                return null;
            }

            set
            {
                Action.ItemDecision = Action.ItemDecision.GetSchedulerPlannerItemDecision(value, text => AppState[text]);
                Action.GotoCode = null;
                Action.AlarmId = null;
                ActionChanged.InvokeAsync(Action);
                AppState.Update = true;
            }

        }

        private SchedulerPlannerItemDecision GetSchedulerPlannerItemDecisionType()
        {
            return Action.ItemDecision;
        }

        public string fecthCodde
        {

            get
            {
                if (Action != null && Action.GotoCode != null)
                {
                    return Action.GotoCode;
                }
                return null;
            }

            set
            {
                Action.GotoCode = value;
                ActionChanged.InvokeAsync(Action);
                AppState.Update = true;
            }

        }
        public long? AlarmSelected
        {

            get
            {
                if (Action != null && Action.AlarmId != null)
                {
                    return Action.AlarmId;
                }
                return null;
            }

            set
            {
                Action.AlarmId = value;
                ActionChanged.InvokeAsync(Action);
                AppState.Update = true;
            }

        }
    }
}


using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Reconciliation;
using Bcephal.Models.Routines;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation.AutomaticReco.Components
{
    public partial class AutoRecoRoutineItem : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public AutoRecoEditorData EditorData { get; set; }

        [Parameter]
        public EventCallback<AutoRecoEditorData> EditorDataChanged { get; set; }

        [Parameter]
        public RoutineExecutor RoutineExecutor_ { get; set; }

        [Parameter]
        public EventCallback<RoutineExecutor> RoutineExecutorChanged { get; set; }

        public Nameable RoutineName_ { get; set; }

        public Nameable RoutineName
        {
            get
            {
                return RoutineName_;
            }
            set
            {
                RoutineName_ = value;
                RoutineExecutor_.RoutineId = value.Id.Value;
                EditorData.Item.UpdateRoutine(RoutineExecutor_);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private void RoutineNameChanged(Nameable nameable)
        {
            RoutineName = nameable;

        }

        [Parameter]
        public bool Editable { get; set; } = true;
        protected override async Task OnInitializedAsync()
        {
            RoutineName_ = EditorData.Routines.Where(x => x.Id == RoutineExecutor_.RoutineId).FirstOrDefault();
            await base.OnInitializedAsync();
        }

        public RoutineExecutorType RoutineExecutorType
        {
            get
            {
                return RoutineExecutor_.RoutineExecutorType;
            }
            set
            {
                RoutineExecutor_.RoutineId = RoutineName_.Id.Value;
                RoutineExecutor_.RoutineExecutorType = value;
                if (RoutineExecutor_.IsPersistent)
                {
                    EditorData.Item.UpdateRoutine(RoutineExecutor_);
                }
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private void RoutineDeleteHandler(Bcephal.Models.Routines.RoutineExecutor Routine)
        {
            EditorData.Item.DeleteRoutine(Routine);
            EditorDataChanged.InvokeAsync(EditorData);
            AppState.Update = true;
        }
    }
}

using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Reconciliation;
using Bcephal.Models.Routines;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation.AutomaticReco.Components
{
    public partial class AutoRecoRoutineComponent : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        public AutoRecoEditorData AutoRecoEditorData { 
            get 
            { 
                return (AutoRecoEditorData)EditorData; 
            } 
            set 
            { 
                EditorData = value; 
            } 
        }
        [Parameter]
        public bool Editable { get; set; } = true;
        [Parameter]
        public EditorData<AutoReco> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<AutoReco>> EditorDataChanged { get; set; }

        public ObservableCollection<RoutineExecutorType> RoutineExecutorType_ { get; set; }

        public RoutineExecutor RoutineExecutor_ { get; set; }

        public List<string> RoutineExecType = new List<string>();

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            RoutineExecType = RoutineExecutorType.GetAll().OrderBy(x => x.code).Select(r => { return r.code; }).ToList();
            AppState.Update = true;
        }

        public Nameable SelectedRoutine { get; set; }

        private void AddRoutine(Nameable val)
        {
            AutoRecoEditorData.Item.AddRoutine(new RoutineExecutor() 
            { 
                RoutineId = val.Id.Value,
                RoutineExecutorType = RoutineExecutorType.POST
            });
            EditorDataChanged.InvokeAsync(EditorData);
            SelectedRoutine = null;
            AppState.Update = true;
        }
    }
}

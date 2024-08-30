using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Joins;
using Bcephal.Models.Routines;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.Joins
{
    public partial class JoinRoutineComponent: ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        [Parameter]
        public EditorData<Join> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Join>> EditorDataChanged { get; set; }

        private void AddRoutineExecutor(RoutineExecutor routineExecutor)
        {
            EditorData.Item.AddRoutine(routineExecutor);
            EditorDataChanged.InvokeAsync(EditorData);
        }
        private void UpdateRoutineExecutor(RoutineExecutor routineExecutor)
        {
            EditorData.Item.UpdateRoutine(routineExecutor);
            EditorDataChanged.InvokeAsync(EditorData);
        }
        private void DeleteRoutineExecutor(RoutineExecutor routineExecutor)
        {
            EditorData.Item.DeleteRoutine(routineExecutor);
            EditorDataChanged.InvokeAsync(EditorData);
        }
    }
}

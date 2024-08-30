using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Scheduler
{
    public partial class SchedulerItemsInfos<P> where P : SchedulableObject
    {

        [Inject] private AppState AppState { get; set; }
        [Parameter] public RenderFragment<ComponentBase> ChildR2Content { get; set; }
        [Parameter] public RenderFragment<ComponentBase> ChildR6Content { get; set; }
        [Parameter] public EditorData<P> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<P>> EditorDataChanged { get; set; }
        [Parameter] public bool Editable { get; set; } = true;
        public bool IsSmallScreen { get; set; }

        private bool Active
        {
            get { return EditorData.Item.Active; }
            set
            {
                EditorData.Item.Modified = true;
                EditorData.Item.Active = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        public bool Scheduled
        {
            get
            {
                return EditorData.Item.Scheduled;
            }
            set
            {
                EditorData.Item.Modified = true;
                EditorData.Item.Scheduled = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        private string CronExpression
        {
            get { return EditorData.Item.CronExpression; }
            set
            {
                EditorData.Item.Modified = true;
                EditorData.Item.CronExpression = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        protected override Task OnInitializedAsync()
        {
            EditorData.Item.Init();
            return base.OnInitializedAsync();
        }

    }
}

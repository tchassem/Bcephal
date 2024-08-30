using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Microsoft.AspNetCore.Components;
using System.Collections.Generic;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Dashboard
{
    public partial class Alarm : ComponentBase
    {
        [Parameter]
        public EditorData<Models.Alarms.Alarm> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Models.Alarms.Alarm>> EditorDataChanged { get; set; }

        int ActiveTabIndex { get; set; } = 0;

        [CascadingParameter]
        public Error Error { get; set; }

        [Inject]
        private AppState AppState { get; set; }

        private int TabPageItemCount { get; set; } = 1;

        List<RenderFragment> renderFragments = new List<RenderFragment>();

    }
}

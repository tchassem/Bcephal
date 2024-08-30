using Bcephal.Models.Base;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Dashboard
{
    public partial class DashboardConfigData_ : ComponentBase
    {
        [Parameter]
        public EditorData<Models.Dashboards.Dashboard> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Models.Dashboards.Dashboard>> EditorDataChanged { get; set; }
    }
}

using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Joins;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Shared.Joins
{
    public partial class JoinEditOperationComponent:ComponentBase, IDisposable
    {
        [Parameter]
        public bool ShowModal { get; set; }

        [Parameter]
        public EventCallback<bool>  ShowModalChanged { get; set; }

        [Inject]
        public AppState AppState { get; set; }
        [Parameter]
        public EditorData<Join> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Join>> EditorDataChanged { get; set; }

        [Parameter]
        public JoinColumn Item { get; set; }

        private void OkHandler()
        {
            ShowModal = false;
            ShowModalChanged.InvokeAsync(ShowModal);
        }
        private void CloseHandler()
        {
            Dispose();
        }

        public void Dispose()
        {
            ShowModal = false;
            ShowModalChanged.InvokeAsync(ShowModal);
        }

        
    }
}

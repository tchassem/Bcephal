using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Bcephal.Models.Joins;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.Joins
{
    public partial class JoinKeyComponent : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public EditorData<Join> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Join>> EditorDataChanged { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;
        private CardComponent CardComponentRef { get; set; }
        private void AddKey(JoinKey Item)
        {
            EditorData.Item.AddKey(Item);
            EditorDataChanged.InvokeAsync(EditorData);
            CardComponentRef.RefreshBody();
    }

        private void UpdateKey(JoinKey Item)
        {
            EditorData.Item.UpdateKey(Item);
            EditorDataChanged.InvokeAsync(EditorData);
            CardComponentRef.RefreshBody();
        }

        private void RemoveKey(JoinKey Item)
        {
            EditorData.Item.DeleteOrForgetKey(Item);
            EditorDataChanged.InvokeAsync(EditorData);
            CardComponentRef.RefreshBody();
        }
    }
}

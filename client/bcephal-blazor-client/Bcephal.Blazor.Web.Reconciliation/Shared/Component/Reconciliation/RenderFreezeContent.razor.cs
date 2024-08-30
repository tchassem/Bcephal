using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Reconciliation;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation
{
  public partial  class RenderFreezeContent
    {
        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public EditorData<ReconciliationModel> EditorData { get; set; }
        [Parameter]
        public EventCallback<EditorData<ReconciliationModel>> EditorDataChanged { get; set; }

        [Parameter]
        public Func<RenderFragment> FreezeAttributeRender { get; set; }

        bool IsXSmallScreen;
        string labelLg = "d-flex justify-content-end mx-1 text-right";
        string labelSm = "d-flex justify-content-start mx-1 text-right";

        [Parameter]
        public string Item1Length { get; set; } = "30%";
        [Parameter]
        public string Item2Length { get; set; } = "70%";
        [Parameter]
        public string ItemSpacing { get; set; } = "10px";
        [Parameter]
        public Func<long?, Nameable> GetNameable { get; set; }

        [Parameter]
        public bool Editable { get; set; } 
        public ReconciliationModelEditorData GetEditorData
        {
            get { return (ReconciliationModelEditorData)EditorData; }

        }
        private RenderFragment FreezeAttributeRender_()
        {
            return FreezeAttributeRender?.Invoke();
        }

        public bool AllowFreeze
        {
            get { return EditorData.Item.AllowFreeze; }
            set
            {
                EditorData.Item.AllowFreeze = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public Nameable FreezeSequenceId
        {
            get { return GetNameable.Invoke(EditorData.Item.FreezeSequenceId); }
            set
            {
                EditorData.Item.FreezeSequenceId = value.Id;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }
    }
}

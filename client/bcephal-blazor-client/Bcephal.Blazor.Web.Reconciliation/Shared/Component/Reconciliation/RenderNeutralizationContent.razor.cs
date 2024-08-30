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
  public partial  class RenderNeutralizationContent
    {
        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public EditorData<ReconciliationModel> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<ReconciliationModel>> EditorDataChanged { get; set; }

        [Parameter]
        public Func<long?, Bcephal.Models.Base.HierarchicalData> GetAttribute_ { get; set; }


        [Parameter]
        public Func<long?, Nameable> GetNameable { get; set; }


        [Parameter]
        public Func<Action<Bcephal.Models.Base.HierarchicalData>, string, RenderFragment> GetAttributTypeRender { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        bool IsXSmallScreen;
        string labelLg = "d-flex justify-content-end mx-1 text-right";
        string labelSm = "d-flex justify-content-start mx-1 text-right";
        string ChecboxlabelClass = "mr-auto mt-auto mb-auto ml-1";

        [Parameter]
        public string Item1Length { get; set; } = "30%";
        [Parameter]
        public string Item2Length { get; set; } = "70%";
        [Parameter]
        public string ItemSpacing { get; set; } = "10px";
        string NeutralizationAttributeName => NeutralizationAttribute != null ? NeutralizationAttribute.Name : "";

        private RenderFragment attributTypeRender()
        {
            return GetAttributTypeRender?.Invoke(NeutralizationRecoAttributeChanged, NeutralizationAttributeName);
        }
        public ReconciliationModelEditorData GetEditorData
        {
            get { return (ReconciliationModelEditorData)EditorData; }
        }

        public void NeutralizationRecoAttributeChanged(HierarchicalData Attribute)
        {
            NeutralizationAttribute = Attribute;
        }


        public HierarchicalData NeutralizationAttribute
        {
            get
            {
                return GetAttribute_?.Invoke(NeutralizationAttributeId);
            }
            set
            {
                if (value != null)
                {
                    EditorData.Item.NeutralizationAttributeId = value.Id;
                }
                else
                {
                    EditorData.Item.NeutralizationAttributeId = null;
                }
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public bool AllowNeutralization
        {
            get { return EditorData.Item.AllowNeutralization; }
            set
            {
                EditorData.Item.AllowNeutralization = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public long? NeutralizationAttributeId
        {
            get { return EditorData.Item.NeutralizationAttributeId; }
            set
            {
                EditorData.Item.NeutralizationAttributeId = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }


        public Nameable NeutralizationSequenceId
        {
            get { return GetNameable(EditorData.Item.NeutralizationSequenceId); }
            set
            {
                EditorData.Item.NeutralizationSequenceId = value.Id;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public string NeutralizationMessage
        {
            get { return EditorData.Item.NeutralizationMessage; }
            set
            {
                EditorData.Item.NeutralizationMessage = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public bool NeutralizationRequestSelectValue
        {
            get { return EditorData.Item.NeutralizationRequestSelectValue; }
            set
            {
                EditorData.Item.NeutralizationRequestSelectValue = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }


        public bool NeutralizationAllowCreateNewValue
        {
            get { return EditorData.Item.NeutralizationAllowCreateNewValue; }
            set
            {
                EditorData.Item.NeutralizationAllowCreateNewValue = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }


        public bool NeutralizationInsertNote
        {
            get { return EditorData.Item.NeutralizationInsertNote; }
            set
            {
                EditorData.Item.NeutralizationInsertNote = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public bool NeutralizationMandatoryNote
        {
            get { return EditorData.Item.NeutralizationMandatoryNote; }
            set
            {
                EditorData.Item.NeutralizationMandatoryNote = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }
    }
}

using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Reconciliation;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation
{
  public partial  class RenderPartialReconciliationContent
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
        public Func<long?, Models.Dimensions.Measure> GetMeasure_ { get; set; }

        [Parameter]
        public Func<long?, Nameable> GetNameable { get; set; }

        [Parameter]
        public Func<Action<Bcephal.Models.Base.HierarchicalData>, string, RenderFragment> GetAttributTypeRender { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        bool IsXSmallScreen;
        string labelLg = "d-flex justify-content-end mx-1 text-right";
        string labelSm = "d-flex justify-content-start mx-1 text-right";

        [Parameter]
        public string Item1Length { get; set; } = "30%";
        [Parameter]
        public string Item2Length { get; set; } = "70%";
        [Parameter]
        public string ItemSpacing { get; set; } = "10px";
        string PartielRecoAttributeName => PartialRecoAttribute != null ? PartialRecoAttribute.Name : "";

        public long? PartialRecoAttributeId
        {
            get { return EditorData.Item.PartialRecoAttributeId; }
            set
            {
                EditorData.Item.PartialRecoAttributeId = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public bool AllowPartialReco
        {
            get { return EditorData.Item.AllowPartialReco; }
            set
            {
                EditorData.Item.AllowPartialReco = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        
        public ReconciliationModelEditorData GetEditorData
        {
            get { return (ReconciliationModelEditorData)EditorData; }
        }

        private RenderFragment attributTypeRender()
        {
            return GetAttributTypeRender?.Invoke(PartialRecoAttributeChanged, PartielRecoAttributeName);
        }

        public HierarchicalData PartialRecoAttribute
        {
            get
            {
                return GetAttribute_?.Invoke(PartialRecoAttributeId);
            }
            set
            {
                if (value != null)
                {
                    EditorData.Item.PartialRecoAttributeId = value.Id;
                }
                else
                {
                    EditorData.Item.PartialRecoAttributeId = null;
                }
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }


        public Nameable PartialRecoSequenceId
        {
            get { return GetNameable(EditorData.Item.PartialRecoSequenceId); }
            set
            {
                EditorData.Item.PartialRecoSequenceId = value.Id;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public void ReconciliatedSelectFilterItemCallback(Measure newValue)
        {
            ReconciliatedMeasure = newValue;
        }

        public void RemainningSelectFilterItemCallback(Measure newValue)
        {
            RemainningMeasure = newValue;
        }

        public void PartialRecoAttributeChanged(HierarchicalData Attribute)
        {
            PartialRecoAttribute = Attribute;
        }


        public long? ReconciliatedMeasureId
        {
            get { return EditorData.Item.ReconciliatedMeasureId; }
            set
            {
                EditorData.Item.ReconciliatedMeasureId = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public long? RemainningMeasureId
        {
            get { return EditorData.Item.RemainningMeasureId; }
            set
            {
                EditorData.Item.RemainningMeasureId = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public Models.Dimensions.Measure ReconciliatedMeasure
        {
            get { return GetMeasure_?.Invoke(ReconciliatedMeasureId); }
            set
            {
                ReconciliatedMeasureId = value.Id;
            }
        }


        public Models.Dimensions.Measure RemainningMeasure
        {
            get { return GetMeasure_?.Invoke(RemainningMeasureId); }
            set
            {
                RemainningMeasureId = value.Id;
            }
        }


    }
}

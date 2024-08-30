using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Reconciliation;
using Microsoft.AspNetCore.Components;
using System;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation
{
   public partial class RenderReconciliationContent
    {
        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public EditorData<ReconciliationModel> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<ReconciliationModel>> EditorDataChanged { get; set; }

        [Parameter]
        public Func<long?,Models.Dimensions.Measure> GetMeasure_ { get; set; }

        [Parameter]
        public Func<long?, Bcephal.Models.Base.HierarchicalData> GetAttribute_ { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        [Parameter]
        public Func<long?, Nameable> GetNameable { get; set; }


        [Parameter]
        public Func<Action<Bcephal.Models.Base.HierarchicalData>, string, RenderFragment> GetAttributTypeRender { get; set; }


        bool IsXSmallScreen;
        string labelLg = "d-flex justify-content-end mx-1 text-right";
        string labelSm = "d-flex justify-content-start mx-1 text-right";

        [Parameter]
        public string Item1Length { get; set; } = "30%";
        [Parameter]
        public string Item2Length { get; set; } = "70%";

        [Parameter]
        public string ItemSpacing { get; set; } = "10px";
        

        //[Parameter]
        public string CssClass = "mt-auto mb-auto w-100";

        private RenderFragment attributTypeRender()
        {
            return GetAttributTypeRender?.Invoke(RecoAttributeChanged, RecoAttributeName);
        }
        public ReconciliationModelEditorData GetEditorData
        {
            get { return (ReconciliationModelEditorData)EditorData; }
        }

        private void ResetRecoAttribute()
        {
            RecoAttribute = null;
        }

        public void RecoAttributeChanged(HierarchicalData Attribute)
        {
            RecoAttribute = Attribute;
        }


        public void LeftSelectFilterItemCallback(Measure newValue)
        {
            LeftMeasure = newValue;
        }

        public void RightSelectFilterItemCallback(Measure newValue)
        {
            RigthMeasure = newValue;
        }


        string RecoAttributeName => RecoAttribute != null ? RecoAttribute.Name : "";

        public long? RecoAttributeId
        {
            get { return EditorData.Item.RecoAttributeId; }
            set
            {
                EditorData.Item.RecoAttributeId = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public HierarchicalData RecoAttribute
        {
            get
            {
                return GetAttribute_?.Invoke(RecoAttributeId);
            }
            set
            {
                if (value != null)
                {
                    EditorData.Item.RecoAttributeId = value.Id;
                }
                else
                {
                    EditorData.Item.RecoAttributeId = null;
                }
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public Nameable RecoSequenceId
        {
            get { return GetNameable.Invoke(EditorData.Item.RecoSequenceId); }
            set
            {
                EditorData.Item.RecoSequenceId = value.Id;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public long? LeftMeasureId
        {
            get { return EditorData.Item.LeftMeasureId; }
            set
            {
                EditorData.Item.LeftMeasureId = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public Models.Dimensions.Measure LeftMeasure
        {
            get { return GetMeasure_?.Invoke(LeftMeasureId); }
            set
            {
                LeftMeasureId = value.Id;
            }
        }

        public long? RigthMeasureId
        {
            get { return EditorData.Item.RigthMeasureId; }
            set
            {
                EditorData.Item.RigthMeasureId = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public Models.Dimensions.Measure RigthMeasure
        {
            get
            {
                return GetMeasure_?.Invoke(RigthMeasureId);
            }
            set
            {
                RigthMeasureId = value.Id;
            }
        }

        public ReconciliationModelBalanceFormula BalanceFormula_ { get; set; }
        public ReconciliationModelBalanceFormula BalanceFormula
        {
            get
            {
                if (EditorData.Item != null && EditorData.Item.Id.HasValue)
                {
                    BalanceFormula_ = ReconciliationModelBalanceFormula.GetByCode(EditorData.Item.BalanceFormula);
                }
                return BalanceFormula_;
            }
            set
            {
                BalanceFormula_ = value;
                EditorData.Item.BalanceFormula = BalanceFormula_.code;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }


        public bool UseDebitCredit
        {
            get
            {
                return EditorData.Item.UseDebitCredit;
            }
            set
            {
                EditorData.Item.UseDebitCredit = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public bool AllowDebitCreditLineColor
        {
            get
            {
                return EditorData.Item.AllowDebitCreditLineColor;
            }
            set
            {
                EditorData.Item.AllowDebitCreditLineColor = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }
    }
}

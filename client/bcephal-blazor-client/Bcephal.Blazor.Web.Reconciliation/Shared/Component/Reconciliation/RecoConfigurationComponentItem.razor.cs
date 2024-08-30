using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Reconciliation;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation
{
  public partial  class RecoConfigurationComponentItem
    {
        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public EditorData<ReconciliationModel> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<ReconciliationModel>> EditorDataChanged { get; set; }
        
        [Parameter]
        public bool Editable { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> Entities { get; set; }

        public string Item1Length { get; set; } = "40%";
        public string Item2Length { get; set; } = "60%";
        public string ItemSpacing { get; set; } = "10px";

        public EditorData<ReconciliationModel> EditorDataBinding
        {
            get { return EditorData; }
            set
            {
                EditorData = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        

        HierarchicalData GetAttribute(long? id)
        {
            if (id.HasValue)
            {
                foreach (var item in Entities)
                {
                    foreach (var attib in ((Entity)item).Attributes)
                    {
                        if (attib.Id.HasValue && attib.Id.Value == id.Value)
                        {
                            return attib;
                        }
                    }
                }
            }
            return null;
        }

        Nameable GetNameable(long? id)
        {
            if (id.HasValue)
            {
                foreach (var item in GetEditorData.Sequences)
                {
                    if (item.Id.HasValue && item.Id.Value == id.Value)
                    {
                        return item;
                    }
                }
            }
            return null;
        }
        
        public ReconciliationModelEditorData GetEditorData
        {
            get { return (ReconciliationModelEditorData)EditorData; }
        }

        public HierarchicalData FreezeAttribute
        {
            get
            {
                return GetAttribute(EditorData.Item.FreezeAttributeId);
            }
            set
            {
                if (value != null)
                {
                    EditorData.Item.FreezeAttributeId = value.Id;
                }
                else
                {
                    EditorData.Item.FreezeAttributeId = null;
                }
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public bool UseDebitCredit
        {
            get { return EditorData.Item.UseDebitCredit; }
            set
            {
                EditorData.Item.UseDebitCredit = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }


        public bool AllowDebitCreditLineColor
        {
            get { return EditorData.Item.AllowDebitCreditLineColor; }
            set
            {
                EditorData.Item.AllowDebitCreditLineColor = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

       


        public Nameable FreezeSequenceId
        {
            get { return GetNameable(EditorData.Item.FreezeSequenceId); }
            set
            {
                EditorData.Item.FreezeSequenceId = value.Id;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }



        public void FreezeAttributeChanged(HierarchicalData Attribute)
        {
            FreezeAttribute = Attribute;
            StateHasChanged();
        }
        

        private void ResetFreezeAttribute()
        {
            FreezeAttribute = null;
        }

        string FreezeAttributeName => FreezeAttribute != null ? FreezeAttribute.Name : "";

        

        public bool AllowFreeze
        {
            get { return EditorData.Item.AllowFreeze; }
            set
            {
                EditorData.Item.AllowFreeze = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }
        

       private Models.Dimensions.Measure GetMeasure(long? id)
        {
            if (id.HasValue)
            {
                foreach (var item in EditorData.Measures)
                {
                    if (item.Id.HasValue && item.Id.Value == id.Value)
                    {
                        return item;
                    }
                }
            }
            return null;
        }

    }
}

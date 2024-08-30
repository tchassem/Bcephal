using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
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
    public partial class RecoConditionsComponent : ComponentBase
    {
        [Inject]
        public AutoRecoService AutoReconciliationService { get; set; }

        [Parameter]

        public EditorData<ReconciliationModel> EditorData { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        [Parameter]

        public EventCallback<EditorData<ReconciliationModel>> EditorDataChanged { get; set; }


        [Parameter]

        public EditorData<ReconciliationModel> EditorDataBindingLeft { get; set; }


        [Parameter]

        public EventCallback<EditorData<ReconciliationModel>> EditorDataBindingChanged { get; set; }


        [Parameter]

        public EditorData<ReconciliationModel> EditorDataBindingRight { get; set; }


        [Parameter]

        public EventCallback<EditorData<ReconciliationModel>> EditorDataBindingRightChanged { get; set; }


        ReconciliationModelColumns ReconciliationModelColumns { get; set; } = new ReconciliationModelColumns()
        {
            LeftColumns = new ObservableCollection<GrilleColumn>(),
            RightColumns = new ObservableCollection<GrilleColumn>()
        };
        ReconciliationCondition tempReco = new ReconciliationCondition() { };

        protected override async Task OnInitializedAsync()
        {
            if (EditorData.Item.Id.HasValue)
            {
                ReconciliationModelColumns = await AutoReconciliationService.GetModelColumns(EditorData.Item.Id.Value);
            }
            await base.OnInitializedAsync();
            
        }


        protected void OnConditionDeleted(ReconciliationCondition condition)
        {
            EditorData.Item.DeleteOrForgetCondition(condition);
            EditorDataChanged.InvokeAsync(EditorData);
        }

        protected void OnConditionChanged(ReconciliationCondition condition)
        {
            if (!EditorData.Item.ConditionListChangeHandler.GetItems().Contains(condition))
            {
                if (condition != null && condition.ColumnId1.HasValue && condition.ColumnId2.HasValue)
                {
                    EditorData.Item.AddCondition(condition);
                    tempReco = new ReconciliationCondition() { };
                }

            }
            else
            {
                EditorData.Item.UpdateCondition(condition);
            }
            EditorDataChanged.InvokeAsync(EditorData);
        }
    }
}

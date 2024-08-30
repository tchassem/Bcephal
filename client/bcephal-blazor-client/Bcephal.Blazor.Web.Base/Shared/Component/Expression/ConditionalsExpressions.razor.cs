using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Alarms;
using Bcephal.Models.Base;
using Bcephal.Models.Conditions;
using Bcephal.Models.Filters;
using Bcephal.Models.Spot;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component.Expression
{
    public partial class ConditionalsExpressions : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public string Title { get; set; }

        [Parameter]
        public bool ShowDialog { get; set; } = false;

        [Parameter]
        public EventCallback<bool> ShowDialogChanged { get; set; }

        [Parameter]
        public bool MustSaved { get; set; } = false;

        [Parameter]
        public EventCallback<bool> MustSavedChanged { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public EditorData<Alarm> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Alarm>> EditorDataChanged { get; set; }

        private List<Nameable> SpotData { get; set; }

        public Alarm CurrentAlarm { get; set; } = new();

        BaseModalComponent ModalCond { get; set; }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();

            SpotData = EditorData.Spots.ToList();
            if(EditorData.Item.condition == null)
            {
                EditorData.Item.condition = new ConditionalExpression();
            }
            ResetFields();
        }

        protected override void OnAfterRender(bool firstRender)
        {
            if (MustSaved)
            {
                CurrentAlarm = EditorData.Item.Copy();
                MustSaved = false;
                MustSavedChanged.InvokeAsync(MustSaved);
            }
        }

        private void AddItem(ConditionalExpressionItem Item)
        {
            CurrentAlarm.condition.AddItem(Item);
            StateHasChanged();
        }

        private void UpdateItem(ConditionalExpressionItem Item)
        {
            CurrentAlarm.condition.UpdateItem(Item);
            StateHasChanged();
        }

        private void RemoveItem(ConditionalExpressionItem Item)
        {
            CurrentAlarm.condition.DeleteOrForgetItem(Item);
            StateHasChanged();
        }

        protected void OkHandler()
        {
            if ((CurrentAlarm.condition.ItemListChangeHandler.NewItems.Count() > 0) || (CurrentAlarm.condition.ItemListChangeHandler.UpdatedItems.Count() > 0) || 
                (CurrentAlarm.condition.ItemListChangeHandler.DeletedItems.Count() > 0) || (CurrentAlarm.condition.ItemListChangeHandler.Items != EditorData.Item.condition.ItemListChangeHandler.Items))
            {
                AppState.Update = true;
            }

            EditorData.Item = CurrentAlarm.Copy();
            EditorDataChanged.InvokeAsync(EditorData);
            Close();
        }

        public void CancelHandler()
        {
            ResetFields();
            Close();
        }

        public void Close()
        {
            ModalCond.CanClose = true;
            ModalCond.Dispose();
            ShowDialogChanged.InvokeAsync(false);
        }

        private void ResetFields()
        {
            CurrentAlarm = EditorData.Item.Copy();
        }
    }
}

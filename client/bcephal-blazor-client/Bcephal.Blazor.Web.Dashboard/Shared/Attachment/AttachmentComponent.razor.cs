using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Alarms;
using Bcephal.Models.Base;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Attachment
{
   public partial class AttachmentComponent : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public string Title { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public bool ShowModal { get; set; } = false;

        [Parameter]
        public EventCallback<bool> ShowModalChanged { get; set; }

        [Parameter]
        public AlarmModelEditorData AlarmModelEditorData { get; set; }

        [Parameter]
        public bool MustSaved { get; set; } = false;

        [Parameter]
        public EventCallback<bool> MustSavedChanged { get; set; }

        [Parameter]
        public EventCallback<AlarmModelEditorData> AlarmModelEditorDataChanged { get; set; }

        BaseModalComponent ModalAttach { get; set; }

        public Alarm CurrentAlarm { get; set; } = new();

        public ObservableCollection<Nameable> Spreadsheets { get; set; }

        public ObservableCollection<Nameable> Grids { get; set; }

        public ObservableCollection<Nameable> Graphs { get; set; }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();

            Grids = AlarmModelEditorData.Grids;
            Graphs = AlarmModelEditorData.Graphs;
            Spreadsheets = AlarmModelEditorData.Spreadsheets;
            ResetFields();
        }

        protected override void OnAfterRender(bool firstRender)
        {
            if (MustSaved)
            {
                CurrentAlarm = AlarmModelEditorData.Item.Copy();
                MustSaved = false;
                MustSavedChanged.InvokeAsync(MustSaved);
            }
        }

        private void AddAttachmentItem(AlarmAttachment AlarmAttachment)
        {
            CurrentAlarm.AddAlarmAttachmentItem(AlarmAttachment);
            StateHasChanged();
        }

        private void UpdateAttachmentItem(AlarmAttachment AlarmAttachment)
        {
            CurrentAlarm.UpdateAlarmAttachmentItem(AlarmAttachment);
            StateHasChanged();
        }

        private void DeleteAttachmentItem(AlarmAttachment AlarmAttachment)
        {
            CurrentAlarm.DeleteOrForgetAlarmAttachmentItem(AlarmAttachment);
            StateHasChanged();
        }

        protected void OkHandler()
        {
            if ((CurrentAlarm.attachmentListChangeHandler.NewItems.Count() > 0) || (CurrentAlarm.attachmentListChangeHandler.UpdatedItems.Count() > 0) ||
                (CurrentAlarm.attachmentListChangeHandler.DeletedItems.Count() > 0) || (CurrentAlarm.attachmentListChangeHandler.Items != AlarmModelEditorData.Item.attachmentListChangeHandler.Items))
            {
                AppState.Update = true;
            }

            AlarmModelEditorData.Item = CurrentAlarm.Copy();
            AlarmModelEditorDataChanged.InvokeAsync(AlarmModelEditorData);
            Close();
        }

        public void CancelHandler()
        {
            ResetFields();
            Close();
        }

        public void Close()
        {
            ModalAttach.CanClose = true;
            ModalAttach.Dispose();
            ShowModalChanged.InvokeAsync(false);
        }

        private void ResetFields()
        {
            CurrentAlarm = AlarmModelEditorData.Item.Copy();
        }
    }
}

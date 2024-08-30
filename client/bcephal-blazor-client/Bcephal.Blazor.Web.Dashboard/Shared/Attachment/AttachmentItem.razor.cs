using Bcephal.Blazor.Web.Base.Services;
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
  public partial  class AttachmentItem : ComponentBase
    {

        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public AlarmAttachment AlarmAttachment { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public bool IsAdded { get; set; } = false;

        [Parameter]
        public ObservableCollection<Nameable> Graphs { get; set; }

        [Parameter]
        public ObservableCollection<Nameable> Grids { get; set; }

        [Parameter]
        public ObservableCollection<Nameable> Spreadsheets { get; set; }

        [Parameter]
        public EventCallback<AlarmAttachment> AddItemCallback { get; set; }

        [Parameter]
        public EventCallback<AlarmAttachment> UpdateItemCallback { get; set; }

        [Parameter]
        public EventCallback<AlarmAttachment> DeleteItemCallback { get; set; }

        public string ItemSpacing { get; set; } = "1px";

        public bool IsXSmallScreen { get; set; }

        AlarmAttachmentType? alarmAttachmentType => AlarmAttachmentType.REPORT_GRID;
        private ObservableCollection<string> AlarmAttachmentTypeItems { get => alarmAttachmentType.GetAll(text => AppState[text]); }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();

            if (AlarmAttachment == null)
            {
                AlarmAttachment = new();
            }
        }

        public string AttachmentType
        {
            get
            {
                return IsAdded ? AlarmAttachment.AttachmentType.GetText(text => AppState[text]) : null;
            }
            set
            {
                AlarmAttachment.AttachmentType = AlarmAttachment.AttachmentType.GetAlarmAttachmentType(value, text => AppState[text]);
                if (!IsAdded)
                {
                    AddAttachmentItem();
                }
                else
                {
                    UpdateAttachmentItem(AlarmAttachment);
                }
            }
        }

        public Nameable TemplateId
        {
            get
            {
                if (AlarmAttachment.AttachmentType.Equals(AlarmAttachmentType.REPORT_GRID) && AlarmAttachment.TemplateId != 0)
                {
                    return Grids.Where(u => u.Id == AlarmAttachment.TemplateId).FirstOrDefault();
                }
                else if (AlarmAttachment.AttachmentType.Equals(AlarmAttachmentType.REPORT_SPREADSHEET) && AlarmAttachment.TemplateId != 0)
                {
                    return Spreadsheets.Where(u => u.Id == AlarmAttachment.TemplateId).FirstOrDefault();
                }
                else if (AlarmAttachment.AttachmentType.Equals(AlarmAttachmentType.GRAPH) && AlarmAttachment.TemplateId != 0)
                {
                    return Graphs.Where(u => u.Id == AlarmAttachment.TemplateId).FirstOrDefault();
                }
                return null;
            }
            set
            {
                AlarmAttachment.TemplateId = value.Id.Value;
                UpdateAttachmentItem(AlarmAttachment);
            }
        }

        public string Name
        {
            get
            {
                if (AlarmAttachment.Name != null)
                {
                    return AlarmAttachment.Name;
                }
                return "";
            }
            set
            {
                AlarmAttachment.Name = value;
                UpdateAttachmentItem(AlarmAttachment);
            }
        }

        private async void AddAttachmentItem()
        {
            if (!IsAdded)
            {
                await AddItemCallback.InvokeAsync(AlarmAttachment);
                resetFields();
                StateHasChanged();
            }
        }

        private async void RemoveAttachmentItem(AlarmAttachment AlarmAttachment)
        {
            await DeleteItemCallback.InvokeAsync(AlarmAttachment);
        }

        private async void UpdateAttachmentItem(AlarmAttachment AlarmAttachment)
        {
            if (AlarmAttachment.IsPersistent)
            {
                await UpdateItemCallback.InvokeAsync(AlarmAttachment);
            }
        }

        private void resetFields()
        {
            AlarmAttachment = new();
        }
    }
}

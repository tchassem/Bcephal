using Bcephal.Blazor.Web.Administration.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Dashboard
{
    public partial class AlarmAudienceComponent : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        [Inject]
        public UsersService UsersService { get; set; }

        [Inject]
        public ProfileService ProfileService { get; set; }

        [Parameter]
        public string Title { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public bool ShowModal { get; set; } = false;

        [Parameter]
        public EventCallback<bool> ShowModalChanged { get; set; }

        [Parameter]
        public bool MustSaved { get; set; } = false;

        [Parameter]
        public EventCallback<bool> MustSavedChanged { get; set; }

        [Parameter]
        public EditorData<Models.Alarms.Alarm> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Models.Alarms.Alarm>> EditorDataChanged { get; set; }

        public BaseModalComponent ModalAud { get; set; }

        public Models.Alarms.Alarm CurrentAlarm { get; set; } = new();

        public List<Nameable> Users { get; set; }

        public List<Nameable> Profiles { get; set; } = new();

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();

            Users = (await UsersService.Search(new Models.Grids.Filters.BrowserDataFilter())).Items.Select(u => { Nameable us = new Nameable() { Id = u.Id, Name = u.Name }; return us; }).ToList();
            Profiles = (await ProfileService.Search(new Models.Grids.Filters.BrowserDataFilter())).Items.Select(p => { Nameable r = new Nameable() { Id = p.Id, Name = p.Name }; return r; }).ToList();
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

        private void AddAudienceItem(Models.Alarms.AlarmAudience AlarmAudience)
        {
            CurrentAlarm.AddAlarmAudienceItem(AlarmAudience);
            StateHasChanged();
        }

        private void UpdateAudienceItem(Models.Alarms.AlarmAudience alarmAudience)
        {
            CurrentAlarm.UpdateAlarmAudienceItem(alarmAudience);
            StateHasChanged();
        }

        private void DeleteAudienceItem(Models.Alarms.AlarmAudience AlarmAudience)
        {
            CurrentAlarm.DeleteOrForgetAlarmAudienceItem(AlarmAudience);
            StateHasChanged();
        }

        protected void OkHandler()
        {
            if ((CurrentAlarm.audienceListChangeHandler.NewItems.Count() > 0) || (CurrentAlarm.audienceListChangeHandler.UpdatedItems.Count() > 0) ||
                (CurrentAlarm.audienceListChangeHandler.DeletedItems.Count() > 0) || (CurrentAlarm.audienceListChangeHandler.Items != EditorData.Item.audienceListChangeHandler.Items))
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
            ModalAud.CanClose = true;
            ModalAud.Dispose();
            ShowModalChanged.InvokeAsync(false);
        }

        private void ResetFields()
        {
            CurrentAlarm = EditorData.Item.Copy();
        }
    }
}

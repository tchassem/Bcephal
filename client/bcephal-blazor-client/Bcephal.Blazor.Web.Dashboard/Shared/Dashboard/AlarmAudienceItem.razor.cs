using Bcephal.Blazor.Web.Administration.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Alarms;
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
    public partial class AlarmAudienceItem : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public Models.Alarms.AlarmAudience AlarmAudience { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public bool IsAdded { get; set; } = false;

        [Parameter]
        public EventCallback<Models.Alarms.AlarmAudience> AddItemCallback { get; set; }

        [Parameter]
        public EventCallback<Models.Alarms.AlarmAudience> UpdateItemCallback { get; set; }

        [Parameter]
        public EventCallback<Models.Alarms.AlarmAudience> DeleteItemCallback { get; set; }

        [Parameter]
        public List<Nameable> Users { get; set; }

        [Parameter]
        public List<Nameable> Profiles { get; set; }

        public string ItemSpacing { get; set; } = "1px";

        public bool IsXSmallScreen { get; set; }

        AlarmAudienceType? alarmAudienceType => AlarmAudienceType.PROFILE;
        private ObservableCollection<string> AlarmAudienceTypeItems { get => alarmAudienceType.GetAll(text => AppState[text]); }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();

            if (AlarmAudience == null)
            {
                AlarmAudience = new();
            }
        }

        public string AudienceType
        {
            get
            {
                return IsAdded ? AlarmAudience.AudienceType.GetText(text => AppState[text]) : null;
            }
            set
            {
                AlarmAudience.AudienceType = AlarmAudience.AudienceType.GetAlarmAudienceType(value, text => AppState[text]);
                if (!IsAdded)
                {
                    AddAudienceItem();
                }
                else
                {
                    UpdateAudienceItem(AlarmAudience);
                }
            }
        }

        public Nameable UserOrProfilId
        {
            get
            {
                if (AlarmAudience.AudienceType.Equals(AlarmAudienceType.USER) && AlarmAudience.UserOrProfilId != 0)
                {
                    return Users.Where(u => u.Id == AlarmAudience.UserOrProfilId).FirstOrDefault();
                }
                else if (AlarmAudience.AudienceType.Equals(AlarmAudienceType.PROFILE) && AlarmAudience.UserOrProfilId != 0)
                {
                    return Profiles.Where(u => u.Id == AlarmAudience.UserOrProfilId).FirstOrDefault();
                }
                return null;
            }
            set
            {
                AlarmAudience.UserOrProfilId = value.Id.Value;
                UpdateAudienceItem(AlarmAudience);
            }
        }

        public string Name
        {
            get
            {
                if (AlarmAudience.Name != null)
                {
                    return AlarmAudience.Name;
                }
                return "";
            }
            set
            {
                AlarmAudience.Name = value;
                UpdateAudienceItem(AlarmAudience);
            }
        }

        public string Email
        {
            get
            {
                if (AlarmAudience.Email != null)
                {
                    return AlarmAudience.Email;
                }
                return "";
            }
            set
            {
                AlarmAudience.Email = value;
                UpdateAudienceItem(AlarmAudience);
            }
        }

        public string Phone
        {
            get
            {
                if (AlarmAudience.Phone != null)
                {
                    return AlarmAudience.Phone;
                }
                return "";
            }
            set
            {
                AlarmAudience.Phone = value;
                UpdateAudienceItem(AlarmAudience);
            }
        }

        public bool SendSmS
        {
            get
            {
                return AlarmAudience.SendSms;
            }
            set
            {
                AlarmAudience.SendSms = value;
                UpdateAudienceItem(AlarmAudience);
            }
        }
        
        public bool SendChat
        {
            get
            {
                return AlarmAudience.SendChat;
            }
            set
            {
                AlarmAudience.SendChat = value;
                UpdateAudienceItem(AlarmAudience);
            }
        }
        
        public bool SendEmail
        {
            get
            {
                return AlarmAudience.SendEmail;
            }
            set
            {
                AlarmAudience.SendEmail = value;
                UpdateAudienceItem(AlarmAudience);
            }
        }

        private async void AddAudienceItem()
        {
            if (!IsAdded)
            {
                await AddItemCallback.InvokeAsync(AlarmAudience);
                resetFields();
                StateHasChanged();
            }
        }

        private async void RemoveAudienceItem(Models.Alarms.AlarmAudience AlarmAudience)
        {
            await DeleteItemCallback.InvokeAsync(AlarmAudience);
        }

        private async void UpdateAudienceItem(Models.Alarms.AlarmAudience AlarmAudience)
        {
            if (AlarmAudience.IsPersistent)
            {
                await UpdateItemCallback.InvokeAsync(AlarmAudience);
            }
        }

        private void resetFields()
        {
            AlarmAudience = new();
        }
    }
}

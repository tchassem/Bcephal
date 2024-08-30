using Bcephal.Models.Base;
using Bcephal.Models.Conditions;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Alarms
{
   public class Alarm : SchedulableObject
    {
		public bool SendEmail { get; set; }

		public string EmailTitle { get; set; }

		public string Email { get; set; }

        public bool SendSms { get; set; } 

		public string Sms { get; set; }

        public bool SendChat { get; set; }  

		public string Chat { get; set; }

		public ConditionalExpression condition;

		public ListChangeHandler<AlarmAudience> audienceListChangeHandler;

	    public ListChangeHandler<AlarmAttachment> attachmentListChangeHandler;

		public Alarm()
		{
			this.condition = new ConditionalExpression();
			this.audienceListChangeHandler = new ListChangeHandler<AlarmAudience>();
			this.attachmentListChangeHandler = new ListChangeHandler<AlarmAttachment>();
		}
        #region Audience

        public void AddAlarmAudienceItem(AlarmAudience item)
        {
            item.Position = audienceListChangeHandler.Items.Count;
            audienceListChangeHandler.AddNew(item, true);
        }

        public void DeleteAlarmAudienceItem(AlarmAudience item)
        {
            audienceListChangeHandler.AddDeleted(item, true);
            foreach (AlarmAudience child in audienceListChangeHandler.Items)
            {
                if (child.Position > item.Position) child.Position = child.Position - 1;
            }
        }

        public void UpdateAlarmAudienceItem(AlarmAudience item)
        {
            audienceListChangeHandler.AddUpdated(item);
        }

        public void ForgetAlarmAudienceItem(AlarmAudience item)
        {
            audienceListChangeHandler.forget(item, true);
            foreach (AlarmAudience child in audienceListChangeHandler.Items)
            {
                if (child.Position > item.Position) child.Position = child.Position - 1;
            }
        }

        public void DeleteOrForgetAlarmAudienceItem(AlarmAudience item)
        {
            if (item.IsPersistent)
            {
                DeleteAlarmAudienceItem(item);
            }
            else
            {
                ForgetAlarmAudienceItem(item);
            }
        }
        #endregion
        #region Attachment
        public void AddAlarmAttachmentItem(AlarmAttachment item)
        {
            item.Position = attachmentListChangeHandler.Items.Count;
            attachmentListChangeHandler.AddNew(item, true);
        }

        public void DeleteAlarmAttachmentItem(AlarmAttachment item)
        {
            attachmentListChangeHandler.AddDeleted(item, true);
            foreach (AlarmAttachment child in attachmentListChangeHandler.Items)
            {
                if (child.Position > item.Position) child.Position = child.Position - 1;
            }
        }

        public void UpdateAlarmAttachmentItem(AlarmAttachment item)
        {
            attachmentListChangeHandler.AddUpdated(item);
        }

        public void ForgetAlarmAttachmentItem(AlarmAttachment item)
        {
            attachmentListChangeHandler.forget(item, true);
            foreach (AlarmAttachment child in attachmentListChangeHandler.Items)
            {
                if (child.Position > item.Position) child.Position = child.Position - 1;
            }
        }

        public void DeleteOrForgetAlarmAttachmentItem(AlarmAttachment item)
        {
            if (item.IsPersistent)
            {
                DeleteAlarmAttachmentItem(item);
            }
            else
            {
                ForgetAlarmAttachmentItem(item);
            }
        }
        #endregion


        public Alarm Copy()
        {
            Alarm copy = new Alarm();
            copy.Id = this.Id;
            copy.Name = this.Name;
            copy.Scheduled = this.Scheduled;
            copy.Active = this.Active;
            copy.CronExpression = this.CronExpression;
            copy.InitStatus = this.InitStatus;
            copy.Modified = this.Modified;
            copy.group = this.group;
            copy.VisibleInShortcut = this.VisibleInShortcut;
            copy.CreationDate = this.CreationDate;
            copy.ModificationDate = this.ModificationDate;
            copy.Email = this.Email;
            copy.EmailTitle = this.EmailTitle;
            copy.Sms = this.Sms;
            copy.Chat = this.Chat;
            copy.Email = this.Email;
            copy.SendEmail = this.SendEmail;
            copy.SendSms = this.SendSms;
            copy.SendChat = this.SendChat;

            copy.condition = this.condition.Copy();

            copy.audienceListChangeHandler.Items = new ObservableCollection<AlarmAudience>(audienceListChangeHandler.Items.Select(p => p.Copy()).ToList());
            copy.audienceListChangeHandler.OriginalList = new ObservableCollection<AlarmAudience>(audienceListChangeHandler.OriginalList.Select(p => p.Copy()).ToList());
            copy.audienceListChangeHandler.NewItems = new ObservableCollection<AlarmAudience>(audienceListChangeHandler.NewItems.Select(p => p.Copy()).ToList());
            copy.audienceListChangeHandler.UpdatedItems = new ObservableCollection<AlarmAudience>(audienceListChangeHandler.UpdatedItems.Select(p => p.Copy()).ToList());
            copy.audienceListChangeHandler.DeletedItems = new ObservableCollection<AlarmAudience>(audienceListChangeHandler.DeletedItems.Select(p => p.Copy()).ToList());
            
            copy.attachmentListChangeHandler.Items = new ObservableCollection<AlarmAttachment>(attachmentListChangeHandler.Items.Select(p => p.Copy()).ToList());
            copy.attachmentListChangeHandler.OriginalList = new ObservableCollection<AlarmAttachment>(attachmentListChangeHandler.OriginalList.Select(p => p.Copy()).ToList());
            copy.attachmentListChangeHandler.NewItems = new ObservableCollection<AlarmAttachment>(attachmentListChangeHandler.NewItems.Select(p => p.Copy()).ToList());
            copy.attachmentListChangeHandler.UpdatedItems = new ObservableCollection<AlarmAttachment>(attachmentListChangeHandler.UpdatedItems.Select(p => p.Copy()).ToList());
            copy.attachmentListChangeHandler.DeletedItems = new ObservableCollection<AlarmAttachment>(attachmentListChangeHandler.DeletedItems.Select(p => p.Copy()).ToList());
            return copy;
        }
    }
}

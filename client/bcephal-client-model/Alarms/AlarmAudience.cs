using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Alarms
{
   public class AlarmAudience : Persistent
    {
        public int Position { get; set; }

        public bool Active { get; set; }

        [JsonIgnore]
        public long? AlarmId { get; set; }

        public long UserOrProfilId { get; set; }

        public string Name { get; set; }

        public string Email { get; set; }

        public string Phone { get; set; }

        public bool SendEmail { get; set; }

        public bool SendSms { get; set; }

        public bool SendChat { get; set; }

        public AlarmAudienceType AudienceType { get; set; }

        public AlarmAudience()
        {
            Position = -1;
        }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is AlarmAudience)) return 1;
            if (this == obj) return 0;
            if (this.Id.HasValue && this.Id.Equals(((AlarmAudience)obj).Id)) return 0;
            int c = this.Position.CompareTo(((AlarmAudience)obj).Position);
            if (c != 0) return c;
            if (this.AlarmId.HasValue && ((AlarmAudience)obj).AlarmId.HasValue) return this.AlarmId.Value.CompareTo(((AlarmAudience)obj).AlarmId);
            return 1;
        }

        public AlarmAudience Copy()
        {
            AlarmAudience copy = new AlarmAudience();
            copy.Id = this.Id;
            copy.Position = this.Position;
            copy.Active = this.Active;
            copy.AlarmId = this.AlarmId;
            copy.UserOrProfilId = this.UserOrProfilId;
            copy.Name = this.Name;
            copy.Email = this.Email;
            copy.Phone = this.Phone;
            copy.SendEmail = this.SendEmail;
            copy.SendSms = this.SendSms;
            copy.SendChat = this.SendChat;
            copy.AudienceType = this.AudienceType;
            return copy;
        }

    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Alarms
{
    public class AlarmAttachment : Persistent
    {

        public string Name { get; set; }

        public int Position { get; set; }

        public AlarmAttachmentType AttachmentType { get; set; }

        public long TemplateId { get; set; }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is AlarmAttachment)) return 1;
            int c = this.Position.CompareTo(((AlarmAttachment)obj).Position);
            if (c != 0) return c;
            if (this.TemplateId != 0) return this.TemplateId.CompareTo(((AlarmAttachment)obj).TemplateId);
            return 1;
        }

        public AlarmAttachment Copy()
        {
            AlarmAttachment copy = new AlarmAttachment();
            copy.Id = this.Id;
            copy.Position = this.Position;
            copy.Name = this.Name;
            copy.AttachmentType = this.AttachmentType;
            return copy;
        }

    }
}

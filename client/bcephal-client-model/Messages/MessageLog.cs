using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Messages
{
    public class MessageLog: Persistent
    {
        public MessageType MessageLogType { get; set; }

        public string Title { get; set; }

        public string Sender { get; set; }

        public string Destinator { get; set; }
        
        public List<string> Destinators { get; set; }

        public string MailOrigin { get; set; }

        public List<string> filesId { get; set; }

        public string Status { get; set; }

        public string ErrorCode { get; set; }

        public string Resend { get; set; }

        public long ReferenceMail { get; set; }

        public long ReferenceRun{ get; set; }

        [JsonIgnore]
        public MessageLogStatus MessageLogStatus
        {
            get { return !string.IsNullOrEmpty(Status) ? MessageLogStatus.GetByCode(Status) : null; }
            set { this.Status = value != null ? value.code : null; }
        }
    }
}

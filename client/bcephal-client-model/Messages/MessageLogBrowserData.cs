using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Messages
{
    public class MessageLogBrowserData : BrowserData
    {
        public string FirstSendDate { get; set; }

        [JsonIgnore]
        public DateTime? FirstSendDateTime
        {
            get
            {
                try
                {
                    return DateUtils.ParseDateTime(FirstSendDate);
                }
                catch (Exception)
                {
                    return null;
                }
            }
        }

        public string MessageType { get; set; }

        public string MessageLogStatus { get; set; }
        
        public string Subject { get; set; }

        public string Username { get; set; }

        public string Content { get; set; }

        public string Audience { get; set; }

        public long? MaxSendAttempts { get; set; }

        public long? SendAttempts { get; set; }

        public string Log { get; set; }

        public string Mode { get; set; }
    }
}

using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Messages
{
    public class Message : Persistent
    {
        public string Message_ { get; set; }

        public string MessageTitle { get; set; }

        public List<Address> Recipient { get; set; }

        public string Operator { get; set; }

        public string Type { get; set; }

        public string Status { get; set; }

        [JsonIgnore]
        public MessageType MessageLogType
        {
            get { return !string.IsNullOrEmpty(Type) ? MessageType.GetByCode(Type) : null; }
            set { this.Type = value != null ? value.code : null; }
        }

        [JsonIgnore]
        public MessageStatut MessageLogStatut
        {
            get { return !string.IsNullOrEmpty(Status) ? MessageStatut.GetByCode(Status) : null; }
            set { this.Status = value != null ? value.code : null; }
        }



    }
}

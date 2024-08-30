using Bcephal.Models.Grids.Filters;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Messages
{
    public class AlarmMessageBrowserDataFilter : BrowserDataFilter
    {

        public string Status { get; set; }
        [JsonIgnore]
        public MessageLogStatus Statut
        {
            get { return MessageLogStatus.GetByCode(this.Status); }
            set { this.Status = value != null ? value.code : null; }
        }


        public string Type { get; set; }
        [JsonIgnore]
        public MessageType MessageType
        {
            get { return MessageType.GetByCode(this.Type); }
            set { this.Type = value != null ? value.code : null; }
        }

        public string Mode { get; set; }
    }
}

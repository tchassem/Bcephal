using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Archives
{
    public class ArchiveBrowserData : BrowserData
    {

        public string Code { get; set; }

        public string Username { get; set; }

        public string Description { get; set; }

        public long LineCount { get; set; }

        public string Status { get; set; }
        [JsonIgnore]
        public ArchiveStatus ArchiveStatus
        {
            get { return !string.IsNullOrEmpty(Status) ? ArchiveStatus.GetByCode(Status) : null; }
            set { this.Status = value != null ? value.code : null; }
        }

    }
}

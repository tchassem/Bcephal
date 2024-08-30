using Bcephal.Models.Base;
using Newtonsoft.Json;

namespace Bcephal.Models.Archives
{
    public class ArchiveLogBrowserData : BrowserData
    {

        public string Username { get; set; }

        public string Message { get; set; }

        public long LineCount { get; set; }

        public string Status { get; set; }
        [JsonIgnore]
        public ArchiveLogStatus LogStatus
        {
            get { return !string.IsNullOrEmpty(Status) ? ArchiveLogStatus.GetByCode(Status) : null; }
            set { this.Status = value != null ? value.code : null; }
        }

        public string Action { get; set; }
        [JsonIgnore]
        public ArchiveLogAction LogAction
        {
            get { return !string.IsNullOrEmpty(Action) ? ArchiveLogAction.GetByCode(Action) : null; }
            set { this.Action = value != null ? value.code : null; }
        }

    }
}

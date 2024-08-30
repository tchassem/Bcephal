using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Scripts
{
    public class ScriptRunnerLog 
    {
        public string Username { get; set; }

        public string Mode { get; set; }

        [JsonIgnore]
        public RunModes RunModes
        {
            get { return !string.IsNullOrEmpty(Mode) ? RunModes.GetByCode(Mode) : null; }
            set { this.Mode = value != null ? value.code : null; }
        }

        public string Status { get; set; }

        [JsonIgnore]
        public RunStatus RunStatus
        {
            get { return !string.IsNullOrEmpty(Status) ? RunStatus.GetByCode(Status) : null; }
            set { this.Status = value != null ? value.code : null; }
        }

        public string Message { get; set; }

        public DateTime? StartDate { get; set; }

        public DateTime? EndDate { get; set; }
    }
}

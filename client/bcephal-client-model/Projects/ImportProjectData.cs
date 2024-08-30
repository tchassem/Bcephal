using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Projects
{
   public class ImportProjectData
    {
        public string FilePath { get; set; }

        public string NewProjectName { get; set; }

        public bool OverwriteExistingProject { get; set; }

        public bool RenameExistingProject { get; set; }

        public bool RenameProjectToImport { get; set; }

        public long ClientId { get; set; }
        public long ProfileId { get; set; }

        public string Locale { get; set; }

        [JsonIgnore]
        public ImportProjectDecision Decision { get; set; }

        public ImportProjectData()
        {
            this.OverwriteExistingProject = false;
            this.RenameExistingProject = false;
            this.RenameProjectToImport = false;
        }
    }

    public enum ImportProjectDecision
    {
        OverwriteExistingProject,
        RenameExistingProject,
        RenameProjectToImport
    }
}

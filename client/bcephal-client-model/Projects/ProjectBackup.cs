using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Projects
{
    public class ProjectBackup : BrowserData
    {

        public string ProjectCode { get; set; }

        public string ProjectName { get; set; }

        public string Username { get; set; }

        public string Description { get; set; }

    }
}

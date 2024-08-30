using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Projects
{
    public class ProjectBackupData : Persistent
    {

        public string Name { get; set; }

        public string Description { get; set; }

    }
}

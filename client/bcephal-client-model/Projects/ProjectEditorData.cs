using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Projects
{
    public class ProjectEditorData : EditorData<Project>
    {

        public ObservableCollection<Nameable> Clients { get; set; }

    }
}

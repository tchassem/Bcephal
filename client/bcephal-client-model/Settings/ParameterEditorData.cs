using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Settings
{
    public class ParameterEditorData : EditorData<Parameter>
    {

        public ObservableCollection<Nameable> Grids { get; set; }

        public ObservableCollection<Nameable> Sequences { get; set; }

        public ObservableCollection<Nameable> Billtemplates { get; set; }

        public ObservableCollection<ParameterGroup> ParameterGroups { get; set; }
    }
}

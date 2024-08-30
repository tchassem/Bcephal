using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Spot
{
    public class SpotEditorData : EditorData<Spot>
    {
        public ObservableCollection<Nameable> Grids { get; set; }
    }
}

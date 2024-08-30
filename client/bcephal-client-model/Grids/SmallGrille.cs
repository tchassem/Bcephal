using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Grids
{
    public class SmallGrille : Persistent
    {
        public string Name { get; set; }

        public bool Published { get; set; }

        public ObservableCollection<SmallGrilleColumn> Columns { get; set; }
    }
}

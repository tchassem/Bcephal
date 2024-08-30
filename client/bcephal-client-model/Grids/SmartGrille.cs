using Bcephal.Models.Base;
using System.Collections.ObjectModel;

namespace Bcephal.Models.Grids
{
    public class SmartGrille : Persistent
    {
        public GrilleType Type { get; set; }

        public string Name { get; set; }

        public bool Published { get; set; }

        public ObservableCollection<SmallGrilleColumn> Columns { get; set; }
    }
}

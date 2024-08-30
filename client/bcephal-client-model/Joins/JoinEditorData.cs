using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using System.Collections.ObjectModel;

namespace Bcephal.Models.Joins
{
    public class JoinEditorData : EditorData<Join>
    {
        public ObservableCollection<Nameable> Routines;

        public ObservableCollection<Nameable> Sequences;

        public ObservableCollection<SmartGrille> Grids;

        public JoinEditorData()
        {
            Routines = new ObservableCollection<Nameable>();
            Sequences = new ObservableCollection<Nameable>();
            Grids = new ObservableCollection<SmartGrille>();
        }

    }
}

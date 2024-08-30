using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Planners
{
    public class SchedulerPlannerEditorData : EditorData<SchedulerPlanner>
    {

		public ObservableCollection<Nameable> Routines { get; set; }

        public ObservableCollection<Nameable> Joins { get; set; }

        public ObservableCollection<Nameable> Recos { get; set; }

        public ObservableCollection<Nameable> Trees { get; set; }

        public ObservableCollection<Nameable> Billing { get; set; }

        public ObservableCollection<Nameable> Alarms { get; set; }

        public SchedulerPlannerEditorData()
        {
            Routines = new ObservableCollection<Nameable>();
            Joins = new ObservableCollection<Nameable>();
            Recos = new ObservableCollection<Nameable>();
            Trees = new ObservableCollection<Nameable>();
            Billing = new ObservableCollection<Nameable>();
            Alarms = new ObservableCollection<Nameable>();
        }

    }
}

using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Alarms
{
    public class AlarmModelEditorData : EditorData<Alarm>
    {
        public ObservableCollection<string> Variables { get; set; }

        public ObservableCollection<Nameable> Grids { get; set; } 

        public ObservableCollection<Nameable> Graphs { get; set; }

        public ObservableCollection<Nameable> Spreadsheets { get; set; }

       


}
    
      
}

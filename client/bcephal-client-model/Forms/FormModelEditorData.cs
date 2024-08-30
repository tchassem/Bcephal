using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class FormModelEditorData : EditorData<FormModel>
    {

        public ObservableCollection<Nameable> Grids { get; set; }

    }
}

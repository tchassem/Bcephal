using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Clients
{
    public class ClientEditorData : EditorData<Client>
    {

        public ObservableCollection<string> Functionalities { get; set; }

        public ClientEditorData()
        {
            Functionalities = new ObservableCollection<string>();
        }

    }
}

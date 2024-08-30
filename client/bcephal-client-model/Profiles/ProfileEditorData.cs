using Bcephal.Models.Base;
using Bcephal.Models.Clients;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Profiles
{
    public class ProfileEditorData : EditorData<Profile>
    {

        public ObservableCollection<ClientFunctionality> Functionalities { get; set; }

        public ObservableCollection<Nameable> Users { get; set; }


        public ObservableCollection<Nameable> Roles { get; set; }

    }
}

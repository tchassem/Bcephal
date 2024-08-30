using Bcephal.Models.Base;
using Bcephal.Models.Security;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
   public  class AppStateStore
    {
        public string ProjectName { get; set; }
        public long? ProjectId { get; set; }

        public User CurrentUser { get; set; }
        public string ProjectCode { get; set; }
        public long? ClientId { get; set; }
        public long? ProfilId { get; set; }
        public string lastUri { get; set; }
        public ObservableCollection<Nameable> ClientsBinding { get; set; }
        public ObservableCollection<Nameable> ProfilsBinding { get; set; }
        public Nameable ProfilBinding { get; set; }
        public Nameable ClientBinding { get; set; }
        public bool CanRenderClientBinding { get; set; }
        public bool CanRenderProfilBinding { get; set; }
        public Models.Users.PrivilegeObserver PrivilegeObserver_ { get; set; }
    }
}

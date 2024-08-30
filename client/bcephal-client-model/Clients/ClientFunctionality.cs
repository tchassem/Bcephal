using Bcephal.Models.Base;
using Bcephal.Models.Profiles;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Clients
{
    public class ClientFunctionality : Persistent
    {

        public string Code { get; set; }

        public bool Active { get; set; }

        public int Position { get; set; }

        [JsonIgnore]
        public ObservableCollection<RightLevel> Levels {
            get {
                return new ObservableCollection<RightLevel>(Actions.Select(r => { RightLevel res = RightLevel.GetByCode(r); return res; }).AsEnumerable());
                
            }
            set { }
        }

        public ObservableCollection<string> Actions { get; set; }
        
        
        public ClientFunctionality()
        {
            Actions = new ObservableCollection<string>();
        }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is ClientFunctionality)) return 1;
            if (this == obj) return 0;
            if (this.Id.HasValue && this.Id.Equals(((ClientFunctionality)obj).Id)) return 0;
            if (this.Position.Equals(((ClientFunctionality)obj).Position))
            {
                return this.Code.CompareTo(((ClientFunctionality)obj).Code);
            }
            return this.Position.CompareTo(((ClientFunctionality)obj).Position);
        }

    }
}

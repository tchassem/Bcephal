using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dimensions
{
    public class Model : Dimension
    {
        public ObservableCollection<Entity> Entities { get; set; }

        //[JsonIgnore]
        //public override string ParentId { get { return null; } }

        //[JsonIgnore]
        //public string FieldId { get { return "MODEL_" + Id; } }
    }
}

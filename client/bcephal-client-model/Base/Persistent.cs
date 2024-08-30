using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Base
{
    public abstract class Persistent : DataForEdit,IPersistent
    {
        public long? Id { get; set; }

        [JsonIgnore]
        public bool IsPersistent { get { return this.Id.HasValue; } }

        public virtual int CompareTo(object obj)
        {
            if (obj == null || !(obj is Persistent)) return 1;
            if (this == obj || !this.Id.HasValue) return 0;
            if (this.Id.HasValue && ((Persistent)obj).Id.HasValue) return this.Id.Value.CompareTo(((Persistent)obj).Id.Value);
            return 0;
        }

    }


}

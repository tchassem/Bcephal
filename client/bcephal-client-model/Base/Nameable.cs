using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Base
{
    public class Nameable : Persistent
    {

        public string Name { get; set; }

        public override string ToString()
        {
            return this.Name;
        }
        public Nameable (string Name)
        {
            this.Name = Name;
        } public Nameable ()
        {
            this.Name = Name;
        }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is Nameable)) return 1;
            if (this == obj) return 0;
            if (this.Id.HasValue && this.Id.Equals(((Nameable)obj).Id)) return 0;
            if(!string.IsNullOrEmpty(this.Name)) return this.Name.CompareTo(((Nameable)obj).Name);
            return base.CompareTo(obj);
        }

    }
}

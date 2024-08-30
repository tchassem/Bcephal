using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Base
{
    public abstract class HierarchicalData : Nameable
    {        
        public virtual string ParentId { get; set; }
                

        public int Position { get; set; }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is HierarchicalData)) return 1;
            if (this == obj) return 0;
            if (this.Id.HasValue && this.Id.Equals(((HierarchicalData)obj).Id)) return 0;
            if (this.Position.Equals(((HierarchicalData)obj).Position))
            {
                if (!string.IsNullOrEmpty(this.ParentId))
                {
                    return this.ParentId.CompareTo(((HierarchicalData)obj).ParentId);
                }
                if (!string.IsNullOrEmpty(this.Name))
                {
                    return this.Name.CompareTo(((HierarchicalData)obj).Name);
                }                
            }            
            return this.Position.CompareTo(((HierarchicalData)obj).Position);
        }
    }
}

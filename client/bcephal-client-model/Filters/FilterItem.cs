using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Filters
{
    public abstract class FilterItem : IPersistent
    {

        public long? Id { get; set; }

        public DimensionType DimensionType { get; set; }

        public long? DimensionId { get; set; }

        public string DimensionName { get; set; }

        public FilterVerb FilterVerb { get; set; }

        public string OpenBrackets { get; set; }

        public string CloseBrackets { get; set; }

        public int Position { get; set; }

        public string Formula { get; set; }


        public FilterItem()
        {
            this.FilterVerb = FilterVerb.AND;
        }

        public int CompareTo(object obj)
        {
            if (this == obj) return 0;
            if (!DimensionId.HasValue) return - 1;
            if(obj is FilterItem)
            {
                return this.DimensionId.Value.CompareTo(((FilterItem)obj).DimensionId);
            }
            return 1;
        }
    }
}

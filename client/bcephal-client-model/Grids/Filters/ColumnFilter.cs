using Bcephal.Models.Filters;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Grids.Filters
{
    public class ColumnFilter
    {

        public string Name { get; set; }

        //public GrilleColumn Column { get; set; }
        public DimensionType dimensionType { get; set; }

        public long? dimensionId { get; set; }

        public string Link { get; set; }

        public string Value { get; set; }

        public string Operation { get; set; }

        public List<ColumnFilter> Items { get; set; }

        public bool Grouped { get; set; }

        public long? JoinColumnId;

        public ColumnFilter()
        {
            this.Items = new List<ColumnFilter>(0);
            this.Grouped = false;
        }

    }
}

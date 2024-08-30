using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Grids
{
    public class UniverseFilter : Persistent
    {

        public MeasureFilter MeasureFilter { get; set; }

        public AttributeFilter AttributeFilter { get; set; }

        public PeriodFilter PeriodFilter { get; set; }

        public SpotFilter SpotFilter { get; set; }

        public UniverseFilter() 
        {
            MeasureFilter = new MeasureFilter();
            AttributeFilter = new AttributeFilter();
            PeriodFilter = new PeriodFilter();
            SpotFilter = new SpotFilter();
        }

    }
}

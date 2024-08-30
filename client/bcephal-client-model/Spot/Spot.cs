using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Spot
{
  public class Spot : MainObject
    {
        public UniverseFilter Filter { get; set; }

        public string Description { get; set; }

        public long? MeasureId { get; set; }

        public long? GridId { get; set; }

        public string MeasureFunction { get; set; }

        [JsonIgnore]
        public MeasureFunctions Function
        {
            get { return MeasureFunctions.GetByCode(this.MeasureFunction); }
            set { this.MeasureFunction = value != null ? value.code : null; }
        }
    }
}

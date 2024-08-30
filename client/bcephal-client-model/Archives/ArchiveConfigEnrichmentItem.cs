using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Archives
{
    public class ArchiveConfigEnrichmentItem : Persistent
    {

        public long? SourceId { get; set; }

        public decimal? DecimalValue { get; set; }

        public string StringValue { get; set; }

        [JsonIgnore]
        public string DimensionName { get; set; }

        public PeriodValue PeriodValue { get; set; }

        public int Position { get; set; }

        public DimensionType? Type { get; set; }

        [JsonIgnore]
        public bool IsAttribute { get { return this.Type.HasValue && this.Type.Value.IsAttribute(); } }

        [JsonIgnore]
        public bool IsMeasure { get { return this.Type.HasValue && this.Type.Value.IsMeasure(); } }

        [JsonIgnore]
        public bool IsPeriod { get { return this.Type.HasValue && this.Type.Value.IsPeriod(); } }

        [JsonIgnore]
        public bool IsBillingEvent { get { return this.Type.HasValue && this.Type.Value.IsBillingEvent(); } }



        public ArchiveConfigEnrichmentItem()
        {
            Position = -1;
        }


        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is ArchiveConfigEnrichmentItem)) return 1;
            int c = this.Position.CompareTo(((ArchiveConfigEnrichmentItem)obj).Position);
            if (c != 0) return c;
            if (this.SourceId.HasValue) return this.SourceId.Value.CompareTo(((ArchiveConfigEnrichmentItem)obj).SourceId);
            return 1;
        }

    }
}

using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class ReconciliationModelEnrichment : Persistent
    {
		public int Position { get; set; }

		public DimensionType DimensionType { get; set; }
        


        public string TargetSide { get; set; }
        [JsonIgnore]
        public ReconciliationModelSide TargetModelSide
        {
            get
            {
                return string.IsNullOrEmpty(TargetSide) ? null : ReconciliationModelSide.GetByCode(TargetSide);
            }
            set
            {
                this.TargetSide = value != null ? value.getCode() : null;
            }
        }

        public long? TargetColumnId { get; set; }


        public string SourceSide { get; set; }
        public ReconciliationModelSide SourceModelSide
        {
            get
            {
                return string.IsNullOrEmpty(SourceSide) ? null : ReconciliationModelSide.GetByCode(SourceSide);
            }
            set
            {
                this.SourceSide = value != null ? value.getCode() : null;
            }
        }

        public long? SourceColumnId { get; set; }

		public string StringValue { get; set; }

		public decimal? DecimalValue { get; set; }
		
		public PeriodValue DateValue { get; set; }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is ReconciliationModelEnrichment)) return 1;
            if (this == obj) return 0;
            if (this.Id.HasValue && this.Id.Equals(((ReconciliationModelEnrichment)obj).Id)) return 0;
            if (this.Position.Equals(((ReconciliationModelEnrichment)obj).Position))
            {
                return this.DimensionType.CompareTo(((ReconciliationModelEnrichment)obj).DimensionType);
            }
            return this.Position.CompareTo(((ReconciliationModelEnrichment)obj).Position);
        }


        [JsonIgnore]
        public string Key
        {
            get
            {
                if (string.IsNullOrWhiteSpace(Key_))
                {
                    Key_ = Guid.NewGuid().ToString("d");
                }
                return Key_;
            }
            set
            {
                Key_ = value;
            }
        }

        private string Key_ { get; set; }
    }
}

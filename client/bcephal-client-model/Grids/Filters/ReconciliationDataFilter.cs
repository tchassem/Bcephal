using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Grids.Filters
{
    public class ReconciliationDataFilter
    {

        public long? RecoAttributeId { get; set; }
        public long? RecoSequenceId { get; set; }

        public long? AmountMeasureId { get; set; }

        public bool AllowPartialReco { get; set; }
        public long? PartialRecoAttributeId { get; set; }
        public long? PartialRecoSequenceId { get; set; }
        public long? ReconciliatedMeasureId { get; set; }
        public long? RemainningMeasureId { get; set; }

        public bool AllowFreeze { get; set; }
        public long? FreezeAttributeId { get; set; }
        public long? FreezeSequenceId { get; set; }

        public bool AllowNeutralization { get; set; }
        public long? NeutralizationAttributeId { get; set; }
        public long? NeutralizationSequenceId { get; set; }
        public bool NeutralizationRequestSelectValue { get; set; }
        public bool NeutralizationAllowCreateNewValue { get; set; }
        public bool NeutralizationInsertNote { get; set; }
        public bool NeutralizationMandatoryNote { get; set; }
        public string NeutralizationMessage { get; set; }

        public long? NoteAttributeId { get; set; }

        public bool Conterpart { get; set; }

        public bool credit { get; set; }

        public bool debit { get; set; }

    }
}

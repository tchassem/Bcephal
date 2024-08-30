using Bcephal.Models.Dimensions;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class ReconciliationData
    {
        public long? ReconciliationId { get; set; }
        public ObservableCollection<long?> Leftids { get; set; }
        public ObservableCollection<long?> Rightids { get; set; }

        public long? RecoTypeId { get; set; }
        public long? RecoSequenceId { get; set; }

        public long? LeftMeasureId { get; set; }
        public long? RigthMeasureId { get; set; }

        public bool PerformPartialReco { get; set; }
        public bool AllowPartialReco { get; set; }
        public long? PartialRecoAttributeId { get; set; }
        public long? PartialRecoSequenceId { get; set; }
        public long? ReconciliatedMeasureId { get; set; }
        public long? RemainningMeasureId { get; set; }

        public ObservableCollection<PartialRecoItem> PartialRecoItems { get; set; }

        public bool AllowFreeze { get; set; }
        public long? FreezeAttributeId { get; set; }
        public long? FreezeSequenceId { get; set; }

        public bool AllowNeutralization { get; set; }
        public long? NeutralizationAttributeId { get; set; }
        public long? NeutralizationSequenceId { get; set; }
        public string NeutralizationValue { get; set; }

        public long? WriteOffMeasureId { get; set; }
        public decimal WriteOffAmount { get; set; }
        public decimal LeftAmount { get; set; }
        public decimal RigthAmount { get; set; }
        public decimal BalanceAmount { get; set; }
        public ObservableCollection<WriteOffField> WriteOffFields { get; set; }

        public ObservableCollection<ReconciliationModelEnrichment> EnrichmentItemDatas { get; set; }

        public bool AddRecoDate { get; set; }
        public bool AddUser { get; set; }
        public bool AddNote { get; set; }
        public bool MandatoryNote { get; set; }
        public string Note { get; set; }
        public bool AddAutomaticManual { get; set; }
        public long? RecoDateId { get; set; }

        public ReconciliationData()
        {
            Leftids = new ObservableCollection<long?>();
            Rightids = new ObservableCollection<long?>();
            WriteOffAmount = 0;
            LeftAmount = 0;
            RigthAmount = 0;
            BalanceAmount = 0;
            EnrichmentItemDatas = new ObservableCollection<ReconciliationModelEnrichment>();
        }

    }
}

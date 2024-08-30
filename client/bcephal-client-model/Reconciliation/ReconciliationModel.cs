using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class ReconciliationModel : RecoConditianable
    {

		public Grille LeftGrid { get; set; }
		public long? LeftMeasureId { get; set; }

		public Grille RigthGrid { get; set; }
		public long? RigthMeasureId { get; set; }

		public Grille BottomGrid { get; set; }

		public long? RecoAttributeId { get; set; }
        public long? RecoSequenceId { get; set; }

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

        public bool AllowWriteOff { get; set; }

        public WriteOffModel WriteOffModel { get; set; }

        public string RigthGridPosition { get; set; }

        [JsonIgnore]
        public RecoGridPosition RigthGridPositions
        {
            get
            {
                return !string.IsNullOrEmpty(RigthGridPosition) ? RecoGridPosition.getByCode(RigthGridPosition) : RecoGridPosition.COLUMN;
            }
            set { this.RigthGridPosition = value.code; }
        }


        public string BalanceFormula { get; set; }
		[JsonIgnore]
		public ReconciliationModelBalanceFormula BalanceFormulas
		{
			get
			{
				return string.IsNullOrEmpty(BalanceFormula) ? ReconciliationModelBalanceFormula.LEFT_MINUS_RIGHT : ReconciliationModelBalanceFormula.GetByCode(BalanceFormula);
			}
			set
			{
				this.BalanceFormula = value != null ? value.getCode() : null;
			}
		}

		public bool UseDebitCredit { get; set; }
		public bool AllowDebitCreditLineColor { get; set; }
		public int? DebitLineColor { get; set; }
		public int? CreditLineColor { get; set; }

		public bool AddRecoDate { get; set; }

		public bool AddUser { get; set; }

		public bool AddAutomaticManual { get; set; }

        public bool AddNote { get; set; }

        public bool MandatoryNote { get; set; }

        public long? RecoPeriodId { get; set; }
        public bool Published { get; set; }

        public ListChangeHandler<ReconciliationModelEnrichment> EnrichmentListChangeHandler { get; set; }

        
        public ReconciliationModel() : base()
        {
			EnrichmentListChangeHandler = new ListChangeHandler<ReconciliationModelEnrichment>();
        }

		public void AddEnrichment(ReconciliationModelEnrichment enrichment, bool sort = true)
		{
			enrichment.Position = EnrichmentListChangeHandler.Items.Count;
			EnrichmentListChangeHandler.AddNew(enrichment, sort);
		}

		public void UpdateEnrichment(ReconciliationModelEnrichment enrichment, bool sort = true)
		{
			EnrichmentListChangeHandler.AddUpdated(enrichment, sort);
		}

        public void InsertEnrichment(int position, ReconciliationModelEnrichment enrichment)
        {
            enrichment.Position = position;
            foreach (ReconciliationModelEnrichment child in EnrichmentListChangeHandler.Items)
            {
                if (child.Position >= enrichment.Position)
                {
                    child.Position = child.Position + 1;
                    EnrichmentListChangeHandler.AddUpdated(child, false);
                }
            }
            EnrichmentListChangeHandler.AddNew(enrichment);
        }


        public void DeleteOrForgetEnrichment(ReconciliationModelEnrichment enrichment)
        {
            if (enrichment.IsPersistent)
            {
                DeleteEnrichment(enrichment);
            }
            else
            {
                ForgetEnrichment(enrichment);
            }
        }

        public void DeleteEnrichment(ReconciliationModelEnrichment enrichment)
        {
            EnrichmentListChangeHandler.AddDeleted(enrichment);
            foreach (ReconciliationModelEnrichment child in EnrichmentListChangeHandler.Items)
            {
                if (child.Position > enrichment.Position)
                {
                    child.Position = child.Position - 1;
                    EnrichmentListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetEnrichment(ReconciliationModelEnrichment enrichment)
        {
            EnrichmentListChangeHandler.forget(enrichment);
            foreach (ReconciliationModelEnrichment child in EnrichmentListChangeHandler.Items)
            {
                if (child.Position > enrichment.Position)
                {
                    child.Position = child.Position - 1;
                    EnrichmentListChangeHandler.AddUpdated(child, false);
                }
            }
        }


    }
}

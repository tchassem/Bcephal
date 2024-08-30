using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Archives
{
    public class ArchiveConfig : SchedulableObject
    {

        public string ArchiveName { get; set; }

        public string Description { get; set; }

        public Grille BackupGrid { get; set; }

        public Grille ReplacementGrid { get; set; }

        public ListChangeHandler<ArchiveConfigEnrichmentItem> EnrichmentItemListChangeHandler { get; set; }


        public ArchiveConfig()
        {
            EnrichmentItemListChangeHandler = new ListChangeHandler<ArchiveConfigEnrichmentItem>();
            this.BackupGrid = new Grille();
            this.BackupGrid.Name = "Backup";
            this.BackupGrid.Type = GrilleType.ARCHIVE_BACKUP;
            this.BackupGrid.UseLink = true;
            this.BackupGrid.Consolidated = false;

            this.ReplacementGrid = new Grille();
            this.ReplacementGrid.Name = "Replacement";
            this.ReplacementGrid.Type = GrilleType.ARCHIVE_REPLACEMENT;
            this.ReplacementGrid.UseLink = true;
            this.ReplacementGrid.Consolidated = true;
        }



        public void AddEnrichmentItem(ArchiveConfigEnrichmentItem item)
        {
            item.Position = EnrichmentItemListChangeHandler.Items.Count;
            EnrichmentItemListChangeHandler.AddNew(item, true);
        }

        public void DeleteEnrichmentItem(ArchiveConfigEnrichmentItem item)
        {
            EnrichmentItemListChangeHandler.AddDeleted(item, true);
            foreach (ArchiveConfigEnrichmentItem child in EnrichmentItemListChangeHandler.Items)
            {
                if (child.Position > item.Position) child.Position = child.Position - 1;
            }
        }

        public void UpdateEnrichmentItem(ArchiveConfigEnrichmentItem item)
        {
            EnrichmentItemListChangeHandler.AddUpdated(item);
        }

        public void ForgetEnrichmentItem(ArchiveConfigEnrichmentItem item)
        {
            EnrichmentItemListChangeHandler.forget(item, true);
            foreach (ArchiveConfigEnrichmentItem child in EnrichmentItemListChangeHandler.Items)
            {
                if (child.Position > item.Position) child.Position = child.Position - 1;
            }
        }

        public void DeleteOrForgetEnrichmentItem(ArchiveConfigEnrichmentItem item)
        {
            if (item.IsPersistent)
            {
                DeleteEnrichmentItem(item);
            }
            else
            {
                ForgetEnrichmentItem(item);
            }
        }


        public override string ToString()
        {
            return this.Name != null ? this.Name : base.ToString();
        }


    }
}

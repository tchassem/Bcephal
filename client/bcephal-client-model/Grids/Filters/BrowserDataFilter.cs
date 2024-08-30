using Bcephal.Models.Joins;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

namespace Bcephal.Models.Grids.Filters
{
    public class BrowserDataFilter
    {

        public static int DEFAULT_PAGE_SIZE = 25;
        
        public int Page { get; set; }

        public int PageSize { get; set; }

        public long? GroupId { get; set; }

        public long? UserId { get; set; }

        public string Criteria { get; set; }

        public bool OrderAsc { get; set; }

        public bool ShowAll { get; set; }

        public bool AllowRowCounting { get; set; }

        public long? ClientId { get; set; }

        public long? ProfileId { get; set; }

        public Grille Grid { get; set; }

        public Join Join { get; set; }

        public ObservableCollection<long?> Ids { get; set; }

        public ReconciliationDataFilter RecoData { get; set; }

        public long? RecoAttributeId { get; set; }

        public long? FreezeAttributeId { get; set; }

        public long? NoteAttributeId { get; set; }

        public bool Conterpart { get; set; }

        public string RowType { get; set; }

        public bool credit { get; set; }

        public bool debit { get; set; }

        public ColumnFilter ColumnFilters { get; set; }

        public string ReportType { get; set; }

        public bool? published;


        public BrowserDataFilter()
        {
            this.PageSize = DEFAULT_PAGE_SIZE;
            //this.Items = new List<BrowserDataFilterItem>(0);
            this.OrderAsc = true;
            this.ShowAll = false;
            this.AllowRowCounting = true;

        }
    }
}

using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Links;
using Bcephal.Models.Utils;
using Newtonsoft.Json;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Models.Grids
{
	[Serializable]
	public class Grille : MainObject
	{
        public GrilleType Type { get; set; }

        public GrilleCategory Category { get; set; }

        public GrilleStatus Status { get; set; }

        public GrilleSource SourceType { get; set; }

        public long? SourceId { get; set; }

        public bool Editable { get; set; }

        public bool Consolidated { get; set; }

        public bool ShowAllRowsByDefault { get; set; }

        public bool AllowLineCounting { get; set; }

        public bool UseLink { get; set; }

        public bool? Debit { get; set; }

        public bool? Credit { get; set; }
        public bool Published { get; set; }

        public string RowType { get; set; }
        [JsonIgnore]
        public GrilleRowType GrilleRowType
        {
            get
            {
                return string.IsNullOrEmpty(RowType) ? GrilleRowType.ALL : GrilleRowType.GetByCode(RowType);
            }
            set
            {
                this.RowType = value != null ? value.getCode() : null;
            }
        }

        public UniverseFilter UserFilter { get; set; }

        public UniverseFilter AdminFilter { get; set; }

        public ListChangeHandler<GrilleDimension> DimensionListChangeHandler { get; set; }

        public ListChangeHandler<GrilleColumn> ColumnListChangeHandler { get; set; }

        public ListChangeHandler<Link> LinkListChangeHandler { get; set; }


        [JsonIgnore]
        public bool IsLoaded
        {
            get
            {
                return this.Status == GrilleStatus.LOADED;
            }
            set
            {
                this.Status = value ? GrilleStatus.LOADED : GrilleStatus.UNLOADED;
            }
        }

        [JsonIgnore]
        public bool IsUnLoaded
        {
            get
            {
                return this.Status == GrilleStatus.UNLOADED;
            }
            set
            {
                this.Status = value ? GrilleStatus.UNLOADED : GrilleStatus.LOADED;
            }
        }

        [JsonIgnore] public bool IsReconciliation
        {
            get { return this.Type == GrilleType.RECONCILIATION; }
        }

        [JsonIgnore] public bool IsReport
        {
            get { return this.Type == GrilleType.REPORT; }
        }

        [JsonIgnore]  public bool IsInput
        {
            get { return this.Type == GrilleType.INPUT; }
        }

        [JsonIgnore]
        public bool IsBillingEventRepo
        {
            get { return this.Type == GrilleType.BILLING_EVENT_REPOSITORY; }
        }

        [JsonIgnore]
        public bool IsClientRepo
        {
            get { return this.Type == GrilleType.CLIENT_REPOSITORY; }
        }

        [JsonIgnore]
        public bool IsBookingRepo
        {
            get { return this.Type == GrilleType.BOOKING_REPOSITORY; }
        }

        [JsonIgnore]
        public bool IsPostingEntryRepo
        {
            get { return this.Type == GrilleType.POSTING_ENTRY_REPOSITORY; }
        }

        [JsonIgnore]
        public bool IsForReporting
        {
            get { return this.IsReport || this.IsReconciliation || this.IsBillingEventRepo || this.IsBookingRepo || this.IsPostingEntryRepo; }
        }

        public Grille()
        {
            this.ColumnListChangeHandler = new ListChangeHandler<GrilleColumn>();
            this.LinkListChangeHandler = new ListChangeHandler<Link>();
            this.DimensionListChangeHandler = new ListChangeHandler<GrilleDimension>();
        }

        public Grille(Grille grid, bool copyFilter = false) : this()
        {
            this.Id = grid.Id;
            this.Name = grid.Name;
            this.ColumnListChangeHandler.SetOriginalList(grid.ColumnListChangeHandler.Items);
            this.UseLink = grid.UseLink;
            this.Consolidated = grid.Consolidated;
            this.Type = grid.Type;
            this.Status = grid.Status;
            this.Debit = grid.Debit;
            this.Credit = grid.Credit;
            this.GrilleRowType = grid.GrilleRowType;
            if (copyFilter)
            {
                this.UserFilter = grid.UserFilter;
                this.AdminFilter = grid.AdminFilter;
            }
        }


        public void AddColumn(GrilleColumn column, bool sort = true)
        {
            column.Position = ColumnListChangeHandler.Items.Count;
            ColumnListChangeHandler.AddNew(column, sort);
        }

        public void UpdateColumn(GrilleColumn column, bool sort = true)
        {
            ColumnListChangeHandler.AddUpdated(column, sort);
        }

        public void InsertColumn(int position, GrilleColumn column)
        {
            column.Position = position;
            foreach (GrilleColumn child in ColumnListChangeHandler.Items)
            {
                if (child.Position >= column.Position)
                {
                    child.Position = child.Position + 1;
                    ColumnListChangeHandler.AddUpdated(child, false);
                }
            }
            ColumnListChangeHandler.AddNew(column);
        }


        public void DeleteOrForgetColumn(GrilleColumn column)
        {
            if (column.IsPersistent)
            {
                DeleteColumn(column);
            }
            else
            {
                ForgetColumn(column);
            }
        }

        public void DeleteColumn(GrilleColumn column)
        {
            ColumnListChangeHandler.AddDeleted(column);
            foreach (GrilleColumn child in ColumnListChangeHandler.Items)
            {
                if (child.Position > column.Position)
                {
                    child.Position = child.Position - 1;
                    ColumnListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetColumn(GrilleColumn column)
        {
            ColumnListChangeHandler.forget(column);
            foreach (GrilleColumn child in ColumnListChangeHandler.Items)
            {
                if (child.Position > column.Position)
                {
                    child.Position = child.Position - 1;
                    ColumnListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public GrilleColumn GetColumnByDimensionId(long? dimensionId, DimensionType type)
        {
            foreach (GrilleColumn column in ColumnListChangeHandler.Items)
            {
                if (column.DimensionId.Equals(dimensionId) && type == column.Type)
                {
                    return column;
                }
            }
            return null;
        }

        public List<GrilleColumn> GetPersistentColumns(string type = null)
        {
            List<GrilleColumn> columns = new List<GrilleColumn>();
            foreach (GrilleColumn column in ColumnListChangeHandler.Items)
            {
                if (column.IsPersistent)
                {
                    if (type != null)
                    {
                        if (type.Equals(column.Type.ToString()))
                        {
                            columns.Add(column);
                        }
                    }
                    else
                    {
                        columns.Add(column);
                    }
                }
            }
            return columns;
        }




        public void AddLink(Link link, bool sort = true)
        {
            link.Position = ColumnListChangeHandler.Items.Count;
            LinkListChangeHandler.AddNew(link, sort);
        }
        public void UpdateLink(Link link, bool sort = true)
        {
            LinkListChangeHandler.AddUpdated(link, sort);
        }

        public void DeleteOrForgetLink(Link link)
        {
            if (link.IsPersistent)
            {
                DeleteLink(link);
            }
            else
            {
                ForgetLink(link);
            }
        }

        public void DeleteLink(Link link)
        {
            LinkListChangeHandler.AddDeleted(link);
            foreach (Link child in LinkListChangeHandler.Items)
            {
                if (child.Position > link.Position)
                {
                    child.Position = child.Position - 1;
                    LinkListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetLink(Link link)
        {
            LinkListChangeHandler.forget(link);
            foreach (Link child in LinkListChangeHandler.Items)
            {
                if (child.Position > link.Position)
                {
                    child.Position = child.Position - 1;
                    LinkListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public void AddDimension(GrilleDimension dimension, bool sort = true)
        {
            dimension.Position = DimensionListChangeHandler.Items.Count;
            DimensionListChangeHandler.AddNew(dimension, sort);
        }

        public void UpdateDimension(GrilleDimension dimension, bool sort = true)
        {
            DimensionListChangeHandler.AddUpdated(dimension, sort);
        }


        public void DeleteOrForgetDimension(GrilleDimension dimension)
        {
            if (dimension.IsPersistent)
            {
                DeleteDimension(dimension);
            }
            else
            {
                ForgetDimension(dimension);
            }
        }

        public void DeleteDimension(GrilleDimension dimension)
        {
            DimensionListChangeHandler.AddDeleted(dimension);
            foreach (GrilleDimension child in DimensionListChangeHandler.Items)
            {
                if (child.Position > dimension.Position)
                {
                    child.Position = child.Position - 1;
                    DimensionListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetDimension(GrilleDimension dimension)
        {
            DimensionListChangeHandler.forget(dimension);
            foreach (GrilleDimension child in DimensionListChangeHandler.Items)
            {
                if (child.Position > dimension.Position)
                {
                    child.Position = child.Position - 1;
                    DimensionListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public GrilleDimension GetDimensionByDimensionId(long? dimensionId, DimensionType type)
        {
            foreach (GrilleDimension dimension in DimensionListChangeHandler.Items)
            {
                if (dimension.DimensionId.Equals(dimensionId) && type == dimension.Type)
                {
                    return dimension;
                }
            }
            return null;
        }









        public GrilleColumn GetColumn(string fieldName)
        {
            foreach (GrilleColumn column in this.ColumnListChangeHandler.Items)
            {
                if (column.Name.Equals(fieldName))
                {
                    return column;
                }
            }
            return null;
        }

        public GrilleColumn GetColumn(DimensionType type, long? id)
        {
            foreach (GrilleColumn column in this.ColumnListChangeHandler.Items)
            {
                if (column.Type == type && column.DimensionId == id) return column;
            }
            return null;
        }


        public void  reverseColumn(int previewPosition, int NewPosition, GrilleColumn column)
        {
            bool isUp =  previewPosition > NewPosition;
            ObservableCollection<GrilleColumn> its_ = ColumnListChangeHandler.Items;
            its_.BubbleSort();
            IList<GrilleColumn> its = its_;
            if (NewPosition >= 0 && NewPosition < its.Count && previewPosition >= 0 && previewPosition < its.Count)
            {
                if (isUp)
                {
                    GrilleColumn child = its[NewPosition];
                    column.Position = NewPosition;
                    ColumnListChangeHandler.AddUpdated(column, false);
                    its[NewPosition] = column;
                    int position_ = previewPosition;
                    while (position_ - 1 > NewPosition)
                    {
                        its[position_] = its[position_ - 1];
                        its[position_].Position = position_;
                        ColumnListChangeHandler.AddUpdated(its[position_], false);
                        position_--;
                    }
                    its[position_] = child;
                    child.Position = position_;
                    ColumnListChangeHandler.AddUpdated(child, false);
                }
                else
                {
                    GrilleColumn child = its[NewPosition];
                    column.Position = NewPosition;
                    ColumnListChangeHandler.AddUpdated(column, false);
                    its[NewPosition] = column;
                    int position_ = previewPosition;
                    while (position_ + 1 < NewPosition)
                    {
                        its[position_] = its[position_ + 1];
                        its[position_].Position = position_;
                        ColumnListChangeHandler.AddUpdated(its[position_], false);
                        position_++;
                    }
                    its[position_] = child;
                    child.Position = position_;
                    ColumnListChangeHandler.AddUpdated(child, false);
                }
            }
        }
    }
}

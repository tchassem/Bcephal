using Bcephal.Models.Base;
using Bcephal.Models.Sheets;
using Bcephal.Models.Utils;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Filters
{
    public class PeriodFilter : Persistent
    {

        public ListChangeHandler<PeriodFilterItem> ItemListChangeHandler { get; set; }

        public PeriodFilter()
        {
            ItemListChangeHandler = new ListChangeHandler<PeriodFilterItem>();
        }

        public void Synchronize(SpreadSheetCell cell, SpreadSheetCell referenceCell, PeriodFilter filter)
        {
            foreach (PeriodFilterItem item in filter.ItemListChangeHandler.NewItems)
            {
                string formula = item.Formula;
                formula = RangeUtil.BuildRefFormula(cell, referenceCell, formula);
                PeriodFilterItem reference = GetItemAtPosition(item.Position);
                if (reference != null)
                {
                    reference.Synchronize(item, formula);
                    UpdateItem(reference);
                }
                else
                {
                    reference = new PeriodFilterItem();
                    reference.Synchronize(item, formula);
                    AddItem(reference);
                }
            }
            foreach (PeriodFilterItem item in filter.ItemListChangeHandler.UpdatedItems)
            {
                string formula = item.Formula;
                formula = RangeUtil.BuildRefFormula(cell, referenceCell, formula);
                PeriodFilterItem reference = GetItemAtPosition(item.Position);
                if (reference != null)
                {
                    reference.Synchronize(item, formula);
                    UpdateItem(reference);
                }
                else
                {
                    reference = new PeriodFilterItem();
                    reference.Synchronize(item, formula);
                    AddItem(reference);
                }
            }
            foreach (PeriodFilterItem item in filter.ItemListChangeHandler.DeletedItems)
            {
                PeriodFilterItem reference = GetItemAtPosition(item.Position);
                if (reference != null)
                {
                    DeleteOrForgetItem(reference);
                }
            }
        }

        public void AddItem(PeriodFilterItem item, bool sort = true)
        {
            item.Position = ItemListChangeHandler.Items.Count;
            ItemListChangeHandler.AddNew(item, sort);
        }

        public void UpdateItem(PeriodFilterItem item, bool sort = true)
        {
            ItemListChangeHandler.AddUpdated(item, sort);
        }

        public void DeleteOrForgetItem(PeriodFilterItem item)
        {
            if (item.Id.HasValue)
            {
                DeleteItem(item);
            }
            else
            {
                ForgetItem(item);
            }
        }

        public void DeleteItem(PeriodFilterItem item)
        {
            ItemListChangeHandler.AddDeleted(item);
            foreach (PeriodFilterItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public void ForgetItem(PeriodFilterItem item)
        {
            ItemListChangeHandler.forget(item);
            foreach (PeriodFilterItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public PeriodFilterItem GetItemAtPosition(int position)
        {
            foreach (PeriodFilterItem item in ItemListChangeHandler.Items)
            {
                if (item.Position == item.Position)
                {
                    return item;
                }
            }
            return null;
        }

    }
}

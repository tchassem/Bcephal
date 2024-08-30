using Bcephal.Models.Base;
using Bcephal.Models.Sheets;
using Bcephal.Models.Utils;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Filters
{
    public class MeasureFilter : Persistent
    {

        public ListChangeHandler<MeasureFilterItem> ItemListChangeHandler { get; set; }

        public MeasureFilter()
        {
            ItemListChangeHandler = new ListChangeHandler<MeasureFilterItem>();
        }

        public void Synchronize(SpreadSheetCell cell, SpreadSheetCell referenceCell, MeasureFilter filter)
        {
            foreach (MeasureFilterItem item in filter.ItemListChangeHandler.NewItems){
                string formula = item.Formula;
                formula = RangeUtil.BuildRefFormula(cell, referenceCell, formula);
                MeasureFilterItem reference = GetItemAtPosition(item.Position);
                if (reference != null)
                {
                    reference.Synchronize(item, formula);
                    UpdateItem(reference);
                }
                else
                {
                    reference = new MeasureFilterItem();
                    reference.Synchronize(item, formula);
                    AddItem(reference);
                }
            }
            foreach (MeasureFilterItem item in filter.ItemListChangeHandler.UpdatedItems)
            {
                string formula = item.Formula;
                formula = RangeUtil.BuildRefFormula(cell, referenceCell, formula);
                MeasureFilterItem reference = GetItemAtPosition(item.Position);
                if (reference != null)
                {
                    reference.Synchronize(item, formula);
                    UpdateItem(reference);
                }
                else
                {
                    reference = new MeasureFilterItem();
                    reference.Synchronize(item, formula);
                    AddItem(reference);
                }
            }
            foreach (MeasureFilterItem item in filter.ItemListChangeHandler.DeletedItems)
            {
                MeasureFilterItem reference = GetItemAtPosition(item.Position);
                if (reference != null)
                {
                    DeleteOrForgetItem(reference);
                }
            }
        }

        public void AddItem(MeasureFilterItem item, bool sort = true)
        {
            item.Position = ItemListChangeHandler.Items.Count;
            ItemListChangeHandler.AddNew(item, sort);
        }

        public void UpdateItem(MeasureFilterItem item, bool sort = true)
        {
            ItemListChangeHandler.AddUpdated(item, sort);
        }

        public void DeleteOrForgetItem(MeasureFilterItem item)
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

        public void DeleteItem(MeasureFilterItem item)
        {
            ItemListChangeHandler.AddDeleted(item);
            foreach (MeasureFilterItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public void ForgetItem(MeasureFilterItem item)
        {
            ItemListChangeHandler.forget(item);
            foreach (MeasureFilterItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public MeasureFilterItem GetItemAtPosition(int position)
        {
            foreach (MeasureFilterItem item in ItemListChangeHandler.Items)
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

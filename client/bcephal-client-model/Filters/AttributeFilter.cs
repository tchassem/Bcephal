using Bcephal.Models.Base;
using Bcephal.Models.Sheets;
using Bcephal.Models.Utils;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Filters
{
    public class AttributeFilter : Persistent
    {

        public ListChangeHandler<AttributeFilterItem> ItemListChangeHandler { get; set; }

        public AttributeFilter()
        {
            ItemListChangeHandler = new ListChangeHandler<AttributeFilterItem>();
        }

        public void Synchronize(SpreadSheetCell cell, SpreadSheetCell referenceCell, AttributeFilter filter)
        {
            foreach (AttributeFilterItem item in filter.ItemListChangeHandler.NewItems)
            {
                string formula = item.Formula;
                formula = RangeUtil.BuildRefFormula(cell, referenceCell, formula);
                AttributeFilterItem reference = GetItemAtPosition(item.Position);
                if (reference != null)
                {
                    reference.Synchronize(item, formula);
                    UpdateItem(reference);
                }
                else
                {
                    reference = new AttributeFilterItem();
                    reference.Synchronize(item, formula);
                    AddItem(reference);
                }
            }
            foreach (AttributeFilterItem item in filter.ItemListChangeHandler.UpdatedItems)
            {
                string formula = item.Formula;
                formula = RangeUtil.BuildRefFormula(cell, referenceCell, formula);
                AttributeFilterItem reference = GetItemAtPosition(item.Position);
                if (reference != null)
                {
                    reference.Synchronize(item, formula);
                    UpdateItem(reference);
                }
                else
                {
                    reference = new AttributeFilterItem();
                    reference.Synchronize(item, formula);
                    AddItem(reference);
                }
            }
            foreach (AttributeFilterItem item in filter.ItemListChangeHandler.DeletedItems)
            {
                AttributeFilterItem reference = GetItemAtPosition(item.Position);
                if (reference != null)
                {
                    DeleteOrForgetItem(reference);
                }
            }
        }
        public void AddItem(AttributeFilterItem item, bool sort = true)
        {
            item.Position = ItemListChangeHandler.Items.Count;
            ItemListChangeHandler.AddNew(item, sort);
        }

        public void UpdateItem(AttributeFilterItem item, bool sort = true)
        {
            ItemListChangeHandler.AddUpdated(item, sort);
        }

        public void DeleteOrForgetItem(AttributeFilterItem item)
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

        public void DeleteItem(AttributeFilterItem item)
        {
            ItemListChangeHandler.AddDeleted(item);
            foreach (AttributeFilterItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public void ForgetItem(AttributeFilterItem item)
        {
            ItemListChangeHandler.forget(item);
            foreach (AttributeFilterItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public AttributeFilterItem GetItemAtPosition(int position)
        {
            foreach (AttributeFilterItem item in ItemListChangeHandler.Items)
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

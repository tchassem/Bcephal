using Bcephal.Models.Base;
using Bcephal.Models.Sheets;
using Bcephal.Models.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Filters
{
    public class SpotFilter : Persistent
    {
        public ListChangeHandler<SpotFilterItem> ItemListChangeHandler { get; set; }

        public SpotFilter()
        {
            ItemListChangeHandler = new ListChangeHandler<SpotFilterItem>();
        }

        public void Synchronize(SpreadSheetCell cell, SpreadSheetCell referenceCell, SpotFilter filter)
        {
            foreach (SpotFilterItem item in filter.ItemListChangeHandler.NewItems)
            {
                string formula = item.Formula;
                formula = RangeUtil.BuildRefFormula(cell, referenceCell, formula);
                SpotFilterItem reference = GetItemAtPosition(item.Position);
                if (reference != null)
                {
                    reference.Synchronize(item, formula);
                    UpdateItem(reference);
                }
                else
                {
                    reference = new SpotFilterItem();
                    reference.Synchronize(item, formula);
                    AddItem(reference);
                }
            }
            foreach (SpotFilterItem item in filter.ItemListChangeHandler.UpdatedItems)
            {
                string formula = item.Formula;
                formula = RangeUtil.BuildRefFormula(cell, referenceCell, formula);
                SpotFilterItem reference = GetItemAtPosition(item.Position);
                if (reference != null)
                {
                    reference.Synchronize(item, formula);
                    UpdateItem(reference);
                }
                else
                {
                    reference = new SpotFilterItem();
                    reference.Synchronize(item, formula);
                    AddItem(reference);
                }
            }
            foreach (SpotFilterItem item in filter.ItemListChangeHandler.DeletedItems)
            {
                SpotFilterItem reference = GetItemAtPosition(item.Position);
                if (reference != null)
                {
                    DeleteOrForgetItem(reference);
                }
            }
        }
        public void AddItem(SpotFilterItem item, bool sort = true)
        {
            item.Position = ItemListChangeHandler.Items.Count;
            ItemListChangeHandler.AddNew(item, sort);
        }

        public void UpdateItem(SpotFilterItem item, bool sort = true)
        {
            ItemListChangeHandler.AddUpdated(item, sort);
        }

        public void DeleteOrForgetItem(SpotFilterItem item)
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

        public void DeleteItem(SpotFilterItem item)
        {
            ItemListChangeHandler.AddDeleted(item);
            foreach (SpotFilterItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public void ForgetItem(SpotFilterItem item)
        {
            ItemListChangeHandler.forget(item);
            foreach (SpotFilterItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public SpotFilterItem GetItemAtPosition(int position)
        {
            foreach (SpotFilterItem item in ItemListChangeHandler.Items)
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

using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Planners
{
    public class SchedulerPlanner : SchedulableObject
    {

        public ListChangeHandler<SchedulerPlannerItem> ItemListChangeHandler { get; set; }

        public SchedulerPlanner()
        {
            this.ItemListChangeHandler = new ListChangeHandler<SchedulerPlannerItem>();
        }

        public void AddItem(SchedulerPlannerItem item, bool sort = true)
        {
            item.Position = ItemListChangeHandler.Items.Count;
            ItemListChangeHandler.AddNew(item, sort);
        }

        public void UpdateItem(SchedulerPlannerItem item, bool sort = true)
        {
            ItemListChangeHandler.AddUpdated(item, sort);
        }


        public void DeleteOrForgetItem(SchedulerPlannerItem item, bool sort = true)
        {
            if (item.IsPersistent)
            {
                DeleteItem(item, sort);
            }
            else
            {
                ForgetItem(item, sort);
            }
        }

        public void DeleteItem(SchedulerPlannerItem item, bool sort = true)
        {
            ItemListChangeHandler.AddDeleted(item, sort);
            foreach (SchedulerPlannerItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetItem(SchedulerPlannerItem item, bool sort = true)
        {
            ItemListChangeHandler.forget(item, sort);
            foreach (SchedulerPlannerItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }


    }
}

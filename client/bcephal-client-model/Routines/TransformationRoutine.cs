using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Routines
{
    public class TransformationRoutine : SchedulableObject
    {

        public string Description { get; set; }

        public UniverseFilter Filter { get; set; }

        public ListChangeHandler<TransformationRoutineItem> ItemListChangeHandler { get; set; }

        public TransformationRoutine()
        {
            ItemListChangeHandler = new ListChangeHandler<TransformationRoutineItem>();
        }

        public void AddItem(TransformationRoutineItem item, bool sort = true)
        {
            item.Position = ItemListChangeHandler.Items.Count;
            ItemListChangeHandler.AddNew(item, sort);
        }

        public void UpdateItem(TransformationRoutineItem item, bool sort = true)
        {
            ItemListChangeHandler.AddUpdated(item, sort);
        }

        public void DeleteOrForgetItem(TransformationRoutineItem item)
        {
            if (item.IsPersistent)
            {
                DeleteItem(item);
            }
            else
            {
                ForgetItem(item);
            }
        }

        public void DeleteItem(TransformationRoutineItem item)
        {
            ItemListChangeHandler.AddDeleted(item);
        }

        public void ForgetItem(TransformationRoutineItem item)
        {
            ItemListChangeHandler.forget(item);
        }


    }
}

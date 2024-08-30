using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Expressions
{
    public class ExpressionAction
    {

        public ListChangeHandler<ExpressionActionItem> ItemListChangeHandler { get; set; }

        public ExpressionAction()
        {
            ItemListChangeHandler = new ListChangeHandler<ExpressionActionItem>();
        }


        public void AddItem(ExpressionActionItem item, bool sort = true)
        {
            item.Position = ItemListChangeHandler.Items.Count;
            ItemListChangeHandler.AddNew(item, sort);
        }

        public void UpdateItem(ExpressionActionItem item, bool sort = true)
        {
            ItemListChangeHandler.AddUpdated(item, sort);
        }

        public void DeleteOrForgetItem(ExpressionActionItem item)
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

        public void DeleteItem(ExpressionActionItem item)
        {
            ItemListChangeHandler.AddDeleted(item);
            foreach (ExpressionActionItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetItem(ExpressionActionItem item)
        {
            ItemListChangeHandler.forget(item);
            foreach (ExpressionActionItem child in ItemListChangeHandler.Items)
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

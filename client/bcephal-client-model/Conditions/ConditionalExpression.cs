using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Conditions
{
    public class ConditionalExpression : Persistent
    {
		public ListChangeHandler<ConditionalExpressionItem> ItemListChangeHandler { get; set; }

		public ConditionalExpression()
		{
			ItemListChangeHandler = new ListChangeHandler<ConditionalExpressionItem>();
		}

        public void AddItem(ConditionalExpressionItem item, bool sort = true)
        {
            item.Position = ItemListChangeHandler.Items.Count;
            ItemListChangeHandler.AddNew(item, sort);
        }

        public void UpdateItem(ConditionalExpressionItem item, bool sort = true)
        {
            ItemListChangeHandler.AddUpdated(item, sort);
        }

        public void DeleteOrForgetItem(ConditionalExpressionItem item)
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

        public void DeleteItem(ConditionalExpressionItem item)
        {
            ItemListChangeHandler.AddDeleted(item);
            foreach (ConditionalExpressionItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetItem(ConditionalExpressionItem item)
        {
            ItemListChangeHandler.forget(item);
            foreach (ConditionalExpressionItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public ConditionalExpression Copy()
        {
            ConditionalExpression copy = new ConditionalExpression();
            copy.Id = this.Id;
            copy.ItemListChangeHandler.Items = new ObservableCollection<ConditionalExpressionItem>(ItemListChangeHandler.Items.Select(p => p.Copy()).ToList());
            copy.ItemListChangeHandler.OriginalList = new ObservableCollection<ConditionalExpressionItem>(ItemListChangeHandler.OriginalList.Select(p => p.Copy()).ToList());
            copy.ItemListChangeHandler.NewItems = new ObservableCollection<ConditionalExpressionItem>(ItemListChangeHandler.NewItems.Select(p => p.Copy()).ToList());
            copy.ItemListChangeHandler.UpdatedItems = new ObservableCollection<ConditionalExpressionItem>(ItemListChangeHandler.UpdatedItems.Select(p => p.Copy()).ToList());
            copy.ItemListChangeHandler.DeletedItems = new ObservableCollection<ConditionalExpressionItem>(ItemListChangeHandler.DeletedItems.Select(p => p.Copy()).ToList());
            return copy;
        }
    }
}

using Bcephal.Models.Base;
using System.Collections.ObjectModel;


namespace Bcephal.Models.Joins
{
	public class JoinColumnProperties: Persistent
    {
		public long? ColumnId { get; set; }

		public JoinColumnField Field { get; set; }

		public ObservableCollection<JoinColumnConcatenateItem> ConcatenateItems { get; set; }

        public ListChangeHandler<JoinColumnConcatenateItem> ConcatenateItemListChangeHandler { get; set; }

        public ObservableCollection<JoinColumnCalculateItem> CalculateItems { get; set; }

        public ListChangeHandler<JoinColumnCalculateItem> CalculateItemListChangeHandler;

        public void AddCalculateItem(JoinColumnCalculateItem item, bool sort = true)
        {
            item.Position = CalculateItemListChangeHandler.Items.Count;
            CalculateItemListChangeHandler.AddNew(item, sort);
        }

        public void UpdateCalculateItem(JoinColumnCalculateItem item, bool sort = true)
        {
            CalculateItemListChangeHandler.AddUpdated(item, sort);
        }


        public void DeleteOrForgetCalculateItem(JoinColumnCalculateItem item, bool sort = true)
        {
            if (item.IsPersistent)
            {
                DeleteCalculateItem(item, sort);
            }
            else
            {
                ForgetCalculateItem(item, sort);
            }
        }

        public void DeleteCalculateItem(JoinColumnCalculateItem item, bool sort = true)
        {
            CalculateItemListChangeHandler.AddDeleted(item, sort);
            foreach (JoinColumnCalculateItem child in CalculateItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    CalculateItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetCalculateItem(JoinColumnCalculateItem item, bool sort = true)
        {
            CalculateItemListChangeHandler.forget(item, sort);
            foreach (JoinColumnCalculateItem child in CalculateItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    CalculateItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void AddConcatenateItem(JoinColumnConcatenateItem item, bool sort = true)
        {
            item.Position = ConcatenateItemListChangeHandler.Items.Count;
            ConcatenateItemListChangeHandler.AddNew(item, sort);
        }

        public void UpdateConcatenateItem(JoinColumnConcatenateItem item, bool sort = true)
        {
            ConcatenateItemListChangeHandler.AddUpdated(item, sort);
        }


        public void DeleteOrForgetConcatenateItem(JoinColumnConcatenateItem item, bool sort = true)
        {
            if (item.IsPersistent)
            {
                DeleteConcatenateItem(item, sort);
            }
            else
            {
                ForgetConcatenateItem(item, sort);
            }
        }

        public void DeleteConcatenateItem(JoinColumnConcatenateItem item, bool sort = true)
        {
            ConcatenateItemListChangeHandler.AddDeleted(item, sort);
            foreach (JoinColumnConcatenateItem child in ConcatenateItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ConcatenateItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetConcatenateItem(JoinColumnConcatenateItem item, bool sort = true)
        {
            ConcatenateItemListChangeHandler.forget(item, sort);
            foreach (JoinColumnConcatenateItem child in ConcatenateItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ConcatenateItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public JoinColumnProperties()
        {
            this.ConcatenateItems = new ObservableCollection<JoinColumnConcatenateItem>();
            this.CalculateItems = new ObservableCollection<JoinColumnCalculateItem>();
            this.ConcatenateItemListChangeHandler = new ListChangeHandler<JoinColumnConcatenateItem>();
            this.CalculateItemListChangeHandler = new ListChangeHandler<JoinColumnCalculateItem>();
            this.Field = new JoinColumnField();
        }

        public void SetConcatenateItems(ObservableCollection<JoinColumnConcatenateItem> ConcatenateItems)
        {
            this.ConcatenateItems = ConcatenateItems;
            ConcatenateItemListChangeHandler.SetOriginalList(ConcatenateItems);
        }

        public void SetCalculateItems(ObservableCollection<JoinColumnCalculateItem> CalculateItems)
        {
            this.CalculateItems = CalculateItems;
            CalculateItemListChangeHandler.SetOriginalList(CalculateItems);
        }
    }
}

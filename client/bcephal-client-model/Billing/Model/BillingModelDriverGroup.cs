using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelDriverGroup : Persistent
    {

        public long? GroupId { get; set; }

        public string GroupName { get; set; }

        public int Position { get; set; }

		public ListChangeHandler<BillingModelDriverGroupItem> ItemListChangeHandler { get; set; }


		public BillingModelDriverGroup()
        {
			this.ItemListChangeHandler = new ListChangeHandler<BillingModelDriverGroupItem>();
		}


        public void AddItem(BillingModelDriverGroupItem item, bool sort = true)
        {
            item.Position = ItemListChangeHandler.Items.Count;
            ItemListChangeHandler.AddNew(item, sort);
        }

        public void UpdateItem(BillingModelDriverGroupItem item, bool sort = true)
        {
            ItemListChangeHandler.AddUpdated(item, sort);
        }

        public void DeleteOrForgetItem(BillingModelDriverGroupItem item)
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

        public void DeleteItem(BillingModelDriverGroupItem item)
        {
            ItemListChangeHandler.AddDeleted(item);
            foreach (BillingModelDriverGroupItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetItem(BillingModelDriverGroupItem item)
        {
            ItemListChangeHandler.forget(item);
            foreach (BillingModelDriverGroupItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }




        public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is BillingModelDriverGroup)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((BillingModelDriverGroup)obj).Id)) return 0;
			if (this.Position.Equals(((BillingModelDriverGroup)obj).Position))
			{
				return this.GroupName.CompareTo(((BillingModelDriverGroup)obj).GroupName);
			}
			return this.Position.CompareTo(((BillingModelDriverGroup)obj).Position);
		}

		public override string ToString()
		{
			return this.GroupName;
		}

	}
}

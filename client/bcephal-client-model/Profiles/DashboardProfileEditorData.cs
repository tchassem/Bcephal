using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Profiles
{
    public class DashboardProfileEditorData
    {
        public long? ProfileId { get; set; }

        public ListChangeHandler<ProfileDashboard> ItemListChangeHandler { get; set; }

        public ObservableCollection<Nameable> Profiles { get; set; }

        public DashboardProfileEditorData()
        {
            this.ItemListChangeHandler = new ListChangeHandler<ProfileDashboard>();
            this.Profiles = new ObservableCollection<Nameable>();
        }

        public void AddItem(ProfileDashboard item, bool sort = true)
        {
            item.Position = ItemListChangeHandler.Items.Count;
            ItemListChangeHandler.AddNew(item, sort);
        }

        public void UpdateItem(ProfileDashboard item, bool sort = true)
        {
            ItemListChangeHandler.AddUpdated(item, sort);
        }

        public void InsertItem(int position, ProfileDashboard item)
        {
            item.Position = position;
            foreach (ProfileDashboard child in ItemListChangeHandler.Items)
            {
                if (child.Position >= item.Position)
                {
                    child.Position = child.Position + 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
            ItemListChangeHandler.AddNew(item);
        }


        public void DeleteOrForgetItem(ProfileDashboard item)
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

        public void DeleteItem(ProfileDashboard item)
        {
            ItemListChangeHandler.AddDeleted(item);
            foreach (ProfileDashboard child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetItem(ProfileDashboard item)
        {
            ItemListChangeHandler.forget(item);
            foreach (ProfileDashboard child in ItemListChangeHandler.Items)
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

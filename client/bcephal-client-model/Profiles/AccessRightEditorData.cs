using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Profiles
{
    public class AccessRightEditorData : EditorData<ProfileProject>
    {

        public string ProjectCode { get; set; }

        public ListChangeHandler<ProfileProject> ItemListChangeHandler { get; set; }

        public ObservableCollection<Nameable> Profiles { get; set; }

        public AccessRightEditorData()
        {
            this.ItemListChangeHandler = new ListChangeHandler<ProfileProject>();
            this.Profiles = new ObservableCollection<Nameable>();
        }

        public void AddItem(ProfileProject item, bool sort = true)
        {
            item.Position = ItemListChangeHandler.Items.Count;
            ItemListChangeHandler.AddNew(item, sort);
        }

        public void UpdateItem(ProfileProject item, bool sort = true)
        {
            ItemListChangeHandler.AddUpdated(item, sort);
        }

        public void InsertItem(int position, ProfileProject item)
        {
            item.Position = position;
            foreach (ProfileProject child in ItemListChangeHandler.Items)
            {
                if (child.Position >= item.Position)
                {
                    child.Position = child.Position + 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
            ItemListChangeHandler.AddNew(item);
        }


        public void DeleteOrForgetItem(ProfileProject item)
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

        public void DeleteItem(ProfileProject item)
        {
            ItemListChangeHandler.AddDeleted(item);
            foreach (ProfileProject child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetItem(ProfileProject item)
        {
            ItemListChangeHandler.forget(item);
            foreach (ProfileProject child in ItemListChangeHandler.Items)
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

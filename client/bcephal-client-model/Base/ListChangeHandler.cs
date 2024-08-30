using Bcephal.Models.Utils;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Runtime.Serialization;
using System.Text;

namespace Bcephal.Models.Base
{
    public class ListChangeHandler<P> where P : IPersistent
    {

        #region Properties

        /** List of original items */
        public ObservableCollection<P> OriginalList { get; set; }

        /** List of new items */
        public ObservableCollection<P> NewItems { get; set; }

        /** List of deleted items */
        public ObservableCollection<P> DeletedItems { get; set; }

        /** List of updated items */
        public ObservableCollection<P> UpdatedItems { get; set; }

        [JsonIgnore]
        public ObservableCollection<P> Items { get; set; }

        #endregion


        #region Constructors

        /**
         * Initialize with an original list
         *
         * @param list original list
         */
        public ListChangeHandler(ObservableCollection<P> list)
        {
            SetOriginalList(list);
        }

        /**
         * Initialize with no original list
         *
         */
        public ListChangeHandler()
        {
            reset();
            Items = new ObservableCollection<P>();
            OriginalList = new ObservableCollection<P>();
        }

        #endregion


        #region Operations

        /**
         * List of items that are currenlty in the list it is equal to:
         * original - deleted + newItems
         */
        public ObservableCollection<P> GetItems()
        {
            ObservableCollection<P> result = new ObservableCollection<P>();
            if (OriginalList != null)
            {
                foreach (P item in OriginalList)
                {
                    bool found = false;
                    foreach (P deleted in DeletedItems)
                    {
                        if (sameItems(item, deleted))
                        {
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                    {
                        result.Add(item);
                    }
                }
            }
            foreach (P item in NewItems)
            {
                result.Add(item);
            }
            return result;
        }


        /**
         * Add an item to the list of deleted items
         *
         * @param deleted Deleted item
         *
         * @pre deleted must be in getItems
         */
        public void AddDeleted(P deleted, bool sort = true)
        {
            // if item is new we just remove it from the list of new and there is no
            // need to keep it in the list of deleted
            bool found = false;
            foreach (P item in NewItems)
            {
                if (sameItems(item, deleted))
                {
                    found = true;
                    break;
                }
            }


            if (!found)
            {
                DeletedItems.Add(deleted);
                UpdatedItems.Remove(deleted);
            }
            else
            {
                DeletedItems.Add(deleted);
                NewItems.Remove(deleted);
            }

            Items.Remove(deleted);
            if (sort) Items.BubbleSort();
        }

        /**
         * Add an item to the list of new items
         *
         * @param newItem New item
         *
         * @pre newItem must be in getItems
         */
        public void AddNew(P newItem, bool sort = true)
        {
            NewItems.Add(newItem);
            Items.Add(newItem);
            if (sort) Items.BubbleSort();      
        }

        public void AddNew(IEnumerable<P> items, bool sort = true)
        {
            foreach (var item in items)
            {
                NewItems.Add(item);
                Items.Add(item);
            }
            if (sort) Items.BubbleSort();
        }



        /**
         * Add an item to the list of updated items
         *
         * @param updated Updated item
         *
         * @pre updated must be in getItems
         */
        public void AddUpdated(P updated, bool sort = true)
        {
            // if item is new we just do nothing as it will any how be persisted
            // with the list of new items
            bool found = false;
            foreach (P item in NewItems)
            {
                if (sameItems(item, updated))
                {
                    found = true;
                    break;
                }
            }
            // if item is was alway updated we just do nothing
            foreach (P item in UpdatedItems)
            {
                if (sameItems(item, updated))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                UpdatedItems.Add(updated);
            }
            if (sort) Items.BubbleSort();
        }

        /**
         * Remove item for list of new, deleted, or updated
         * @param item item to forget
         */
        public void forget(P item, bool sort = true)
        {
            bool found = NewItems.Remove(item);
            if (!found)
            {
                found = DeletedItems.Remove(item);
            }
            if (!found)
            {
                found = UpdatedItems.Remove(item);
            }
            if (!found)
            {
                found = OriginalList.Remove(item);
            }
            Items.Remove(item);
            if (sort) Items.BubbleSort();
        }

        /**
         * Do item and other have the same id?
         * or do other and item refer to the same object?
         * @param item
         * @param other
         * @return True if both ids are non null and are equal
         */

        private bool sameItems(P item, P other)
        {
            bool result = false;
            if (item != null)
            {
                if (other != null)
                {
                    if (item.Equals(other))
                    {
                        result = true;
                    }
                    else
                    {
                        if (item.Id > 0 && item.Id.Equals(other.Id))
                        {
                            result = true;
                        }
                        else
                        {
                            result = false;
                        }
                    }
                }
            }
            else
            {
                result = false;
            }
            return result;
        }

        /**
         * Set all internal lists to empty lists
         */
        private void reset()
        {
            NewItems = new ObservableCollection<P>();
            DeletedItems = new ObservableCollection<P>();
            UpdatedItems = new ObservableCollection<P>();
        }

        /**
         * Set all internal lists to empty lists
         */
        public void resetOriginalList()
        {
            reset();
            OriginalList = new ObservableCollection<P>(Items);
        }



        public void SetOriginalList(ObservableCollection<P> list)
        {
            reset();
            this.OriginalList = new ObservableCollection<P>(list);
            if (this.OriginalList == null)
            {
                this.OriginalList = new ObservableCollection<P>();
            }

            if (this.Items != null)
            {
                this.Items.Clear();
            }
            else
            {
                this.Items = new ObservableCollection<P>();
            }
            foreach (P item in GetItems())
            {
                this.Items.Add(item);
            }
            this.Items.BubbleSort();
        }

        #endregion


        #region Utils

        [OnDeserialized]
        internal void OnDeserializedMethod(StreamingContext context)
        {
            Items.Clear();            
            Items = GetItems();
            Items.BubbleSort();
        }

        #endregion

    }
}

using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dashboards
{
    public class Dashboard : MainObject
    {

        public bool Published { get; set; }

        public long? ProfilId { get; set; }

        public string Layout { get; set; }
        [JsonIgnore] public DashboardLayout DashboardLayout
        {
            get { return DashboardLayout.GetByCode(this.Layout); }
            set { this.Layout = value != null ? value.Code : null; }
        }

        [JsonIgnore]
        public bool ItemDimensionSet
        {
            get
            {
                if(ItemsListChangeHandler != null 
                    && ItemsListChangeHandler.Items.Any() 
                    && ItemsListChangeHandler.Items.ElementAt(0).Width > 0 
                    && ItemsListChangeHandler.Items.ElementAt(0).Height > 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
           
        }
       
        public bool AllowRefreshFrequency { get; set; }

        public int RefreshFrequency { get; set; }

        public string RefreshFrequencyUnit { get; set; }
        [JsonIgnore] public TimeUnit RefreshFrequencyTimeUnit
        {
            get { return TimeUnit.GetByCode(this.RefreshFrequencyUnit); }
            set { this.RefreshFrequencyUnit = value != null ? value.Code : null; }
        }

        public ListChangeHandler<DashboardItem> ItemsListChangeHandler { get; set; }


        public Dashboard()
        {
            this.ItemsListChangeHandler = new ListChangeHandler<DashboardItem>();
        }


        public void AddItem(DashboardItem item, bool sort = true)
        {            
            ItemsListChangeHandler.AddNew(item, sort);
        }


        /// <summary>
        /// Met à jour un Column
        /// </summary>
        /// <param name="cell"></param>
        public void UpdateItem(DashboardItem item, bool sort = true)
        {
            ItemsListChangeHandler.AddUpdated(item, sort);
        }

        /// <summary>
        /// Retire un Column
        /// </summary>
        /// <param name="cell"></param>
        public void RemoveItem(DashboardItem item, bool sort = true)
        {
            ItemsListChangeHandler.AddDeleted(item, sort);
        }

        /// <summary>
        /// Oublier un DashboardReportField
        /// </summary>
        /// <param name="cell"></param>
        public void ForgetItem(DashboardItem item, bool sort = true)
        {
            ItemsListChangeHandler.forget(item, sort);
        }

        public void RemoveOrForgetItem(DashboardItem item, bool sort = true)
        {
            if (item.IsPersistent)
            {
                RemoveItem(item, sort);
            }
            else
            {
                ForgetItem(item, sort);
            }
        }


        public Dictionary<int?, DashboardItem> GetItemsDimension()
        {
            Dictionary<int?, DashboardItem> ItemsPosition_ = new Dictionary<int?, DashboardItem>();

            foreach (DashboardItem item in ItemsListChangeHandler.GetItems())
            {
                if (item.Width.HasValue && item.Height.HasValue)
                {
                    ItemsPosition_.Add(item.Position, item);
                }
            }
            return ItemsPosition_;
        }

    }
}

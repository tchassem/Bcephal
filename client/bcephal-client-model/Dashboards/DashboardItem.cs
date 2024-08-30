using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dashboards
{
    public class DashboardItem : Nameable, DimensionPanel
    {

        public int Position { get; set; }

        public string ItemType { get; set; }

        public string Uid { get; set; }
        [JsonIgnore] public DashboardItemType DashboardItemType
        {
            get { return DashboardItemType.GetByCode(this.ItemType); }
            set { this.ItemType = value != null ? value.Code : null; }
        }

        public string ItemName { get; set; }

        public string Description { get; set; }

        // Id du report
        public long? ItemId { get; set; }
        
        public string Background { get; set; }

        public string Foreground { get; set; }

        public bool ShowTitleBar { get; set; }

        public bool ShowBorder { get; set; }

        public string BackgroundTitle { get; set; }

        public string ForegroundTitle { get; set; }

        public bool Visible { get; set; }

        public int? Height { get; set; }

        public int? Width { get; set; }


        public DashboardItem()
        {
            this.Visible = true;
            this.ShowTitleBar = true;
            this.ShowBorder = true;
        }


        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is DashboardItem)) return 1;
            return this.Position.CompareTo(((DashboardItem)obj).Position);
        }

        public int? GetHeight()
        {
            return Height;
        }
        public int? Getwidth()
        {
            return Width;
        }

        [JsonIgnore]
        public string ItemKey { get; set; }
    }
}

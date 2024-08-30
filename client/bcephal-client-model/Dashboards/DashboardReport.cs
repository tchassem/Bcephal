using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dashboards
{
    public class DashboardReport : MainObject
    {

        public string Description { get; set; }

        public string ReportType { get; set; }
        [JsonIgnore] public DashboardItemType DashboardReportType
        {
            get { return DashboardItemType.GetByCode(this.ReportType); }
            set { this.ReportType = value != null ? value.Code : null; }
        }

        public bool Published { get; set; }

        public UniverseFilter UserFilter { get; set; }

        public UniverseFilter AdminFilter { get; set; }

        public ListChangeHandler<DashboardReportField> FieldListChangeHandler { get; set; }


        public ChartProperties ChartProperties { get; set; }

        public PivotTableProperties PivotTableProperties { get; set; }

        public void AddField(DashboardReportField Field, bool sort = true)
        {
            Field.Position = FieldListChangeHandler.Items.Count;
            FieldListChangeHandler.AddNew(Field, sort);
        }

        public void UpdateField(DashboardReportField Field, bool sort = true)
        {
            FieldListChangeHandler.AddUpdated(Field, sort);
        }

        public void InsertField(DashboardReportField Field,int position)
        {
            Field.Position = position;
            foreach (DashboardReportField child in FieldListChangeHandler.Items)
            {
                if (child.Position >= Field.Position)
                {
                    child.Position = child.Position + 1;
                    FieldListChangeHandler.AddUpdated(child, false);
                }
            }
            FieldListChangeHandler.AddNew(Field);
        }


        public void DeleteOrForgetField(DashboardReportField Field)
        {
            if (Field.IsPersistent)
            {
                DeleteField(Field);
            }
            else
            {
                ForgetField(Field);
            }
        }

        public void DeleteField(DashboardReportField Field)
        {
            FieldListChangeHandler.AddDeleted(Field);
            foreach (DashboardReportField child in FieldListChangeHandler.Items)
            {
                if (child.Position > Field.Position)
                {
                    child.Position = child.Position - 1;
                    FieldListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetField(DashboardReportField Field)
        {
            FieldListChangeHandler.forget(Field);
            foreach (DashboardReportField child in FieldListChangeHandler.Items)
            {
                if (child.Position > Field.Position)
                {
                    child.Position = child.Position - 1;
                    FieldListChangeHandler.AddUpdated(child, false);
                }
            }
        }

    }
}

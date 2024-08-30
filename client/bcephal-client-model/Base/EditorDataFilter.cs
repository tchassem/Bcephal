using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Base
{
    public class EditorDataFilter
    {
        public long? Id { get; set; }

        public long? SecondId { get; set; }

        public string SubjectType { get; set; }
        public bool ShowAllMeasureTypes { get; set; }
        public bool ShowPeriodWithIntervalls { get; set; }
        public bool ShowModelWithAttributes { get; set; }
        public bool ShowModelWithTargetCustom { get; set; }

        public bool NewData { get; set; }

        public EditorDataFilter()
        {
            //this.projectCode = ApplicationManager.Instance.Project.code;
            this.ShowAllMeasureTypes = false;
            this.ShowPeriodWithIntervalls = false;
            this.ShowModelWithAttributes = true;
            this.ShowModelWithTargetCustom = true;
            this.NewData = false;
        }

        public EditorDataFilter(long? id, string subjectType, bool newData = false) : this()
        {
            this.Id = id;
            this.SubjectType = subjectType;
            this.NewData = newData;
        }

    }
}

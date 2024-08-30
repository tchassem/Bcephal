using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Initiation.Domain
{
    public class PeriodName : HierarchicalData
    {
        public string DefaultValue { get; set; }

        public bool VisibleByAdminOnly { get; set; }

        public ListChangeHandler<PeriodName> ChildrenListChangeHandler { get; set; }

        [JsonIgnore]
        public PeriodName Parent { get; set; }

        [JsonIgnore]
        public override string ParentId { get { return Parent != null ? Parent.Name : null; } }

        public PeriodName(Nameable m)
        {
            this.Id = m.Id;
            this.Name = m.Name;
            this.ChildrenListChangeHandler = new ListChangeHandler<PeriodName>();
        }

        public PeriodName()
        {
            this.ChildrenListChangeHandler = new ListChangeHandler<PeriodName>();
        }

        [JsonIgnore]
        public List<PeriodName> Descendents
        {
            get
            {
                List<PeriodName> periods = new List<PeriodName>();
                foreach (PeriodName period in ChildrenListChangeHandler.Items)
                {
                    period.Parent = this;
                    periods.Add(period);
                }
                return periods;
            }
        }

        public void AddChildrens(List<PeriodName> periodList, bool sort = true)
        {
            periodList.ForEach(p =>
            {
                p.Position = ChildrenListChangeHandler.Items.Count;
                p.Parent = this;
                ChildrenListChangeHandler.AddNew(p, sort);
            });
            UpdateParents();
        }

        public void AddChild(PeriodName period, bool sort = true)
        {
            period.Position = ChildrenListChangeHandler.Items.Count;
            period.Parent = this;
            ChildrenListChangeHandler.AddNew(period, sort);
            UpdateParents();
        }

        public void UpdateChild(PeriodName period, bool sort = true)
        {
            ChildrenListChangeHandler.AddUpdated(period, sort);
            UpdateParents();
        }

        public void InsertChild(int position, PeriodName period)
        {
            period.Position = position;
            period.Parent = this;
            foreach (PeriodName child in ChildrenListChangeHandler.Items)
            {
                if (child.Position >= period.Position)
                {
                    child.Position = child.Position + 1;
                    ChildrenListChangeHandler.AddUpdated(child, false);
                }
            }
            ChildrenListChangeHandler.AddNew(period);
            UpdateParents();
        }

        public void DeleteOrForgetChild(PeriodName period)
        {
            if (period.IsPersistent)
            {
                DeleteChild(period);
            }
            else
            {
                ForgetChild(period);
            }
            foreach (PeriodName child in new List<PeriodName>(period.ChildrenListChangeHandler.Items))
            {
                period.DeleteOrForgetChild(child);
            }
        }

        public void DeleteChild(PeriodName period)
        {
            UpdateParents();
            ChildrenListChangeHandler.AddDeleted(period);
            period.Parent = null;
            foreach (PeriodName child in ChildrenListChangeHandler.Items)
            {
                if (child.Position > period.Position)
                {
                    child.Position = child.Position - 1;
                    ChildrenListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public void ForgetChild(PeriodName period)
        {
            UpdateParents();
            ChildrenListChangeHandler.forget(period);
            period.Parent = null;
            foreach (PeriodName child in ChildrenListChangeHandler.Items)
            {
                if (child.Position > period.Position)
                {
                    child.Position = child.Position - 1;
                    ChildrenListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        private void UpdateParents()
        {
            if (this.Parent != null)
            {
                this.Parent.UpdateChild(this);
            }
        }

        public PeriodName GetChildByPosition(int position)
        {
            foreach (PeriodName child in ChildrenListChangeHandler.Items)
            {
                if (child.Position == position)
                {
                    return child;
                }
            }
            return null;
        }

    }
}

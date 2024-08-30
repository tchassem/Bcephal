using Bcephal.Models.Base;
using Newtonsoft.Json;
using System.Collections.Generic;

namespace Bcephal.Blazor.Web.Initiation.Domain
{
    public class Measure : HierarchicalData
    {
        public decimal? DefaultValue { get; set; }

        public bool VisibleByAdminOnly { get; set; }

        public ListChangeHandler<Measure> ChildrenListChangeHandler { get; set; }

        [JsonIgnore]
        public Measure Parent { get; set; }

        [JsonIgnore]
        public override string ParentId { get { return Parent != null ? Parent.Name : null; } }

        public Measure(Nameable m)
        {
            this.Id = m.Id;
            this.Name = m.Name;
            this.ChildrenListChangeHandler = new ListChangeHandler<Measure>();
        }

        public Measure()
        {
            this.ChildrenListChangeHandler = new ListChangeHandler<Measure>();
        }

        [JsonIgnore]
        public List<Measure> Descendents
        {
            get
            {
                List<Measure> measures = new List<Measure>();
                foreach (Measure measure in ChildrenListChangeHandler.Items)
                {
                    measure.Parent = this;
                    measures.Add(measure);
                }
                return measures;
            }
        }

        public void AddChildrens(List<Measure> measureList, bool sort = true)
        {
            measureList.ForEach(m =>
            {
                m.Position = ChildrenListChangeHandler.Items.Count;
                m.Parent = this;
                ChildrenListChangeHandler.AddNew(m, sort);
            });
            UpdateParents();
        }

        public void AddChild(Measure measure, bool sort = true)
        {
            measure.Position = ChildrenListChangeHandler.Items.Count;
            measure.Parent = this;
            ChildrenListChangeHandler.AddNew(measure, sort);
            UpdateParents();
        }

        public void UpdateChild(Measure measure, bool sort = true)
        {
            ChildrenListChangeHandler.AddUpdated(measure, sort);
            UpdateParents();
        }

        public void InsertChild(int position, Measure measure)
        {
            measure.Position = position;
            measure.Parent = this;
            foreach (Measure child in ChildrenListChangeHandler.Items)
            {
                if (child.Position >= measure.Position)
                {
                    child.Position = child.Position + 1;
                    ChildrenListChangeHandler.AddUpdated(child, false);
                }
            }
            ChildrenListChangeHandler.AddNew(measure);
            UpdateParents();
        }

        public void DeleteOrForgetChild(Measure measure)
        {
            if (measure.IsPersistent)
            {
                DeleteChild(measure);
            }
            else
            {
                ForgetChild(measure);
            }
            foreach (Measure child in new List<Measure>(measure.ChildrenListChangeHandler.Items))
            {
                measure.DeleteOrForgetChild(child);
            }
        }

        public void DeleteChild(Measure measure)
        {
            UpdateParents();
            ChildrenListChangeHandler.AddDeleted(measure);
            measure.Parent = null;
            foreach (Measure child in ChildrenListChangeHandler.Items)
            {
                if (child.Position > measure.Position)
                {
                    child.Position = child.Position - 1;
                    ChildrenListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public void ForgetChild(Measure measure)
        {
            UpdateParents();
            ChildrenListChangeHandler.forget(measure);
            measure.Parent = null;
            foreach (Measure child in ChildrenListChangeHandler.Items)
            {
                if (child.Position > measure.Position)
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

        public Measure GetChildByPosition(int position)
        {
            foreach (Measure child in ChildrenListChangeHandler.Items)
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

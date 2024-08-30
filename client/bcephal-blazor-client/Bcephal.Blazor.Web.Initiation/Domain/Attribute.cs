using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

namespace Bcephal.Blazor.Web.Initiation.Domain
{
    public class Attribute : HierarchicalData
    {

        public string DefaultValue { get; set; }

        public bool Declared { get; set; }

        public bool VisibleByAdminOnly { get; set; }

        public ListChangeHandler<Attribute> ChildrenListChangeHandler { get; set; }

        public ListChangeHandler<AttributeValue> ValueListChangeHandler { get; set; }

        public Attribute()
        {
            this.ChildrenListChangeHandler = new ListChangeHandler<Attribute>();
            this.ValueListChangeHandler = new ListChangeHandler<AttributeValue>();
        }

        public Attribute(Nameable m)
        {
            this.Id = m.Id;
            this.Name = m.Name;
            this.ChildrenListChangeHandler = new ListChangeHandler<Attribute>();
            this.ValueListChangeHandler = new ListChangeHandler<AttributeValue>();
        }

        [JsonIgnore]
        public Attribute Parent { get; set; }

        [JsonIgnore]
        public Entity Entity { get; set; }

        [JsonIgnore]
        public override string ParentId { get { return Parent != null ? Parent.Name : null; } }

        [JsonIgnore]
        public List<Attribute> Descendents
        {
            get
            {
                List<Attribute> attributes = new List<Attribute>();
                foreach (Attribute attribute in ChildrenListChangeHandler.Items)
                {
                    attribute.Parent = this;
                    attributes.Add(attribute);
                }
                return attributes;
            }
        }

        public void AddChildrens(List<Attribute> attributes, bool sort = true)
        {
            attributes.ForEach(attribute =>
            {
                attribute.Position = ChildrenListChangeHandler.Items.Count;
                attribute.Parent = this;
                ChildrenListChangeHandler.AddNew(attribute, sort);
            });            
            UpdateParents();
        }

        public void AddChildren(Attribute attribute, bool sort = true)
        {
            attribute.Position = ChildrenListChangeHandler.Items.Count;
            attribute.Parent = this;
            ChildrenListChangeHandler.AddNew(attribute, sort);
            UpdateParents();
        }

        public void UpdateChildren(Attribute attribute, bool sort = true)
        {
            ChildrenListChangeHandler.AddUpdated(attribute, sort);
            UpdateParents();
        }

        public void InsertChild(int position, Attribute attribute)
        {
            attribute.Position = position;            
            attribute.Parent = this;
            foreach (Attribute child in ChildrenListChangeHandler.Items)
            {
                if (child.Position >= attribute.Position)
                {
                    child.Position = child.Position + 1;
                    ChildrenListChangeHandler.AddUpdated(child, false);
                }
            }
            ChildrenListChangeHandler.AddNew(attribute);
            UpdateParents();
        }

        public void DeleteOrForgetChildren(Attribute attribute)
        {            
            if (attribute.IsPersistent)
            {
                DeleteChildren(attribute);
            }
            else
            {
                ForgetChildren(attribute);
            }
            foreach(Attribute child in new List<Attribute>(attribute.ChildrenListChangeHandler.Items))
            {
                attribute.DeleteOrForgetChildren(child);
            }            
        }

        public void DeleteChildren(Attribute attribute)
        {
            UpdateParents();
            ChildrenListChangeHandler.AddDeleted(attribute);
            attribute.Parent = null;
            foreach (Attribute child in ChildrenListChangeHandler.Items)
            {
                if (child.Position > attribute.Position)
                {
                    child.Position = child.Position - 1;
                    ChildrenListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public void ForgetChildren(Attribute attribute)
        {
            UpdateParents();
            ChildrenListChangeHandler.forget(attribute);
            attribute.Parent = null;
            foreach (Attribute child in ChildrenListChangeHandler.Items)
            {
                if (child.Position > attribute.Position)
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
                this.Parent.UpdateChildren(this);
            }
            else if (this.Entity != null)
            {
                this.Entity.UpdateAttribute(this);
            }
        }

        public Attribute GetChildByPosition(int position)
        {
            foreach (Attribute child in ChildrenListChangeHandler.Items)
            {
                if (child.Position == position)
                {
                    return child;
                }
            }
            return null;
        }


        public void AddValue(AttributeValue value, bool sort = true)
        {
            value.Position = ValueListChangeHandler.Items.Count;
            ValueListChangeHandler.AddNew(value, sort);
        }

        public void UpdateValue(AttributeValue value, bool sort = true)
        {
            ValueListChangeHandler.AddUpdated(value, sort);
        }

        public void DeleteOrForgetValue(AttributeValue value)
        {
            if (value.IsPersistent)
            {
                DeleteValue(value);
            }
            else
            {
                ForgetValue(value);
            }
        }

        public void DeleteValue(AttributeValue value)
        {
            ValueListChangeHandler.AddDeleted(value);
            foreach (AttributeValue child in ValueListChangeHandler.Items)
            {
                if (child.Position > value.Position)
                {
                    child.Position = child.Position - 1;
                    ValueListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public void ForgetValue(AttributeValue value)
        {
            ValueListChangeHandler.forget(value);
            foreach (AttributeValue child in ValueListChangeHandler.Items)
            {
                if (child.Position > value.Position)
                {
                    child.Position = child.Position - 1;
                    ValueListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public Attribute GetCopy()
        {
            Attribute attribute = new Attribute();
            attribute.Name = "Copy Of " + this.Name;
            attribute.Position = -1;
            attribute.DefaultValue = DefaultValue;
            attribute.Declared = this.Declared;
            foreach (Attribute child in this.ChildrenListChangeHandler.Items)
            {
                Attribute copy = (Attribute)child.GetCopy();
                attribute.AddChildren(copy);
            }
            foreach (AttributeValue value in this.ValueListChangeHandler.Items)
            {
                AttributeValue copy = (AttributeValue)value.GetCopy();
                copy.Position = value.Position;
                attribute.ValueListChangeHandler.AddNew(copy);
            }
            return attribute;
        }



    }
}

using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

namespace Bcephal.Blazor.Web.Initiation.Domain
{
    public class Entity : HierarchicalData
    {

        public bool VisibleByAdminOnly { get; set; }


        public ListChangeHandler<Attribute> AttributeListChangeHandler { get; set; }

        public Entity()
        {
            this.AttributeListChangeHandler = new ListChangeHandler<Attribute>();
        }

        public Entity(Nameable m)
        {
            this.Id = m.Id;
            this.Name = m.Name;
            this.AttributeListChangeHandler = new ListChangeHandler<Attribute>();
        }

        [JsonIgnore]
        public Model Model { get; set; }

        [JsonIgnore]
        public override string ParentId { get; set; }


        [JsonIgnore]
        public ObservableCollection<Attribute> Attributes 
        { 
            get
            {
                ObservableCollection<Attribute> attributes = new ObservableCollection<Attribute>();
                foreach(Attribute attribute in AttributeListChangeHandler.Items)
                {
                    attribute.Entity = this;
                    attributes.Add(attribute);
                    foreach(Attribute child in attribute.Descendents)
                    {
                        attributes.Add(child);
                    }
                }
                return attributes;
            }
        }

        public void AddAttribute(Attribute attribute, bool sort = true)
        {
            attribute.Entity = this;
            attribute.Position = AttributeListChangeHandler.Items.Count;
            AttributeListChangeHandler.AddNew(attribute, sort);
            this.Model.UpdateEntity(this);
        }

        public void AddAttributes(List<Attribute> attributes, bool sort = true)
        {
            attributes.ForEach(attribute =>
            {
                attribute.Entity = this;
                attribute.Position = AttributeListChangeHandler.Items.Count;
                AttributeListChangeHandler.AddNew(attribute, sort);
            });
            this.Model.UpdateEntity(this);
        }

        public void UpdateAttribute(Attribute attribute, bool sort = true)
        {
            attribute.Entity = this;
            AttributeListChangeHandler.AddUpdated(attribute, sort);
            this.Model.UpdateEntity(this);
        }

        public void InsertAttribute(int position, Attribute attribute)
        {
            attribute.Position = position;
            attribute.Entity = this;
            attribute.Parent = null;
            foreach (Attribute child in AttributeListChangeHandler.Items)
            {
                if (child.Position >= attribute.Position)
                {
                    child.Position = child.Position + 1;
                    AttributeListChangeHandler.AddUpdated(child, false);
                }
            }
            AttributeListChangeHandler.AddNew(attribute);
            this.Model.UpdateEntity(this);
        }

        public void DeleteOrForgetAttribute(Attribute attribute)
        {
            if (attribute.IsPersistent)
            {
                DeleteAttribute(attribute);
            }
            else
            {
                ForgetAttribute(attribute);
            }
            foreach (Attribute child in new List<Attribute>(attribute.ChildrenListChangeHandler.Items))
            {
                attribute.DeleteOrForgetChildren(child);
            }
        }

        public void DeleteAttribute(Attribute attribute)
        {
            AttributeListChangeHandler.AddDeleted(attribute);
            foreach (Attribute child in AttributeListChangeHandler.Items)
            {
                if (child.Position > attribute.Position)
                {
                    child.Position = child.Position - 1;
                    AttributeListChangeHandler.AddUpdated(child, false);
                }
            }
            this.Model.UpdateEntity(this);
        }


        public void ForgetAttribute(Attribute attribute)
        {
            AttributeListChangeHandler.forget(attribute);
            foreach (Attribute child in AttributeListChangeHandler.Items)
            {
                if (child.Position > attribute.Position)
                {
                    child.Position = child.Position - 1;
                    AttributeListChangeHandler.AddUpdated(child, false);
                }
            }
            this.Model.UpdateEntity(this);
        }

        public Attribute GetAttributeByPosition(int position)
        {
            foreach (Attribute child in AttributeListChangeHandler.Items)
            {
                if (child.Position == position)
                {
                    return child;
                }
            }
            return null;
        }



        public Entity GetCopy()
        {
            Entity entity = new Entity();
            entity.Name = "Copy Of " + this.Name;
            foreach (Attribute attribute in AttributeListChangeHandler.Items)
            {
                Attribute copy = (Attribute)attribute.GetCopy();
                copy.Position = attribute.Position;
                entity.AttributeListChangeHandler.AddNew(copy);
            }
            return entity;
        }

        [JsonIgnore]
        public List<Attribute> Descendents
        {
            get
            {
                List<Attribute> Attributes = new List<Attribute>();
                foreach (Attribute attribute in AttributeListChangeHandler.Items)
                {
                    attribute.Entity = this;
                    Attributes.Add(attribute);
                }
                return Attributes;
            }
        }

    }
}

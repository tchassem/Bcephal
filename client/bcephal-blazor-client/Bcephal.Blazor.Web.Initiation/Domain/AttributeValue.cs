using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Blazor.Web.Initiation.Domain
{
    public class AttributeValue : Nameable
    {              

        public int Position { get; set; }

        public ListChangeHandler<AttributeValue> ChildrenListChangeHandler { get; set; }

        public AttributeValue()
        {
            this.ChildrenListChangeHandler = new ListChangeHandler<AttributeValue>();
        }

        public void AddChildren(AttributeValue value, bool sort = true)
        {
            value.Position = ChildrenListChangeHandler.Items.Count;
            ChildrenListChangeHandler.AddNew(value, sort);
        }

        public void UpdateChildren(AttributeValue value, bool sort = true)
        {
            ChildrenListChangeHandler.AddUpdated(value, sort);
        }

        public void DeleteOrForgetChildren(AttributeValue value)
        {
            if (value.IsPersistent)
            {
                DeleteChildren(value);
            }
            else
            {
                ForgetChildren(value);
            }
        }

        public void DeleteChildren(AttributeValue value)
        {
            ChildrenListChangeHandler.AddDeleted(value);
            foreach (AttributeValue child in ChildrenListChangeHandler.Items)
            {
                if (child.Position > value.Position)
                {
                    child.Position = child.Position - 1;
                    ChildrenListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public void ForgetChildren(AttributeValue value)
        {
            ChildrenListChangeHandler.forget(value);
            foreach (AttributeValue child in ChildrenListChangeHandler.Items)
            {
                if (child.Position > value.Position)
                {
                    child.Position = child.Position - 1;
                    ChildrenListChangeHandler.AddUpdated(child, false);
                }
            }
        }


        public AttributeValue GetCopy()
        {
            AttributeValue value = new AttributeValue();
            value.Name = "Copy Of " + this.Name;
            value.Position = -1;
            foreach (AttributeValue child in this.ChildrenListChangeHandler.Items)
            {
                AttributeValue copy = child.GetCopy();
                value.AddChildren(copy);
            }
            return value;
        }


    }
}

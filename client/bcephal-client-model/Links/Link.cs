using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Links
{
	[Serializable]
	public class Link : MainObject
	{
		public int Position { get; set; }

        
		public ListChangeHandler<LinkedAttribute> LinkedAttributeListChangeHandler { get; set; }

        public Link()
        {
            this.LinkedAttributeListChangeHandler = new ListChangeHandler<LinkedAttribute>();
        }

        public void AddLinkedAttribute(LinkedAttribute linkedAttribute, bool sort = true)
        {
            linkedAttribute.Position = LinkedAttributeListChangeHandler.Items.Count;
            LinkedAttributeListChangeHandler.AddNew(linkedAttribute, sort);
        }
        public void UpdateLinkedAttribute(LinkedAttribute linkedAttribute, bool sort = true)
        {
            LinkedAttributeListChangeHandler.AddUpdated(linkedAttribute, sort);
        }

        public void DeleteOrForgetLinkedAttribute(LinkedAttribute linkedAttribute)
        {
            if (linkedAttribute.IsPersistent)
            {
                DeleteLinkedAttribute(linkedAttribute);
            }
            else
            {
                ForgetLinkedAttribute(linkedAttribute);
            }
        }

        public void DeleteLinkedAttribute(LinkedAttribute linkedAttribute)
        {
            LinkedAttributeListChangeHandler.AddDeleted(linkedAttribute);
            foreach (LinkedAttribute child in LinkedAttributeListChangeHandler.Items)
            {
                if (child.Position > linkedAttribute.Position)
                {
                    child.Position = child.Position - 1;
                    LinkedAttributeListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetLinkedAttribute(LinkedAttribute linkedAttribute)
        {
            LinkedAttributeListChangeHandler.forget(linkedAttribute);
            foreach (LinkedAttribute child in LinkedAttributeListChangeHandler.Items)
            {
                if (child.Position > linkedAttribute.Position)
                {
                    child.Position = child.Position - 1;
                    LinkedAttributeListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is Link)) return 1;
            if (this == obj) return 0;
            if (this.Id.HasValue && this.Id.Equals(((Link)obj).Id)) return 0;
            if (this.Position.Equals(((Link)obj).Position) && !string.IsNullOrEmpty(this.Name))
            {
                return this.Name.CompareTo(((Link)obj).Name);
            }
            return this.Position.CompareTo(((Link)obj).Position);
        }

        public List<LinkedAttribute> GetLinkedAttributes(bool iskey)
        {
            List<LinkedAttribute> attributes = new List<LinkedAttribute>(0);
            foreach (LinkedAttribute attribute in this.LinkedAttributeListChangeHandler.Items)
            {
                if (attribute.Iskey == iskey)
                {
                    attributes.Add(attribute);
                }
            }
            return attributes;
        }

        [JsonIgnore]
        private string Key_ { get; set; }

        [JsonIgnore]
        public string Key
        {
            get
            {
                if (string.IsNullOrWhiteSpace(Key_))
                {
                    Key_ = new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds() + "";
                }
                return Key_;
            }
            set
            {
                Key_ = value;
            }
        }
    }
}

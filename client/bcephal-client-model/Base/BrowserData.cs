using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Base
{
    public class BrowserData : DataForEdit,IComparable
    {
        public long? Id { get; set; }

        public string Name { get; set; }

        public string Group { get; set; }

        public bool VisibleInShortcut { get; set; }
        
        public string CreationDate { get; set; }

        public string ModificationDate { get; set; }
                
        [JsonIgnore]
        public DateTime CreationDateTime
        {
            get
            {
                try
                {
                    return DateUtils.ParseDateTime(CreationDate);
                }
                catch (Exception)
                {
                    return DateTime.Now;
                }
            }
        }

        [JsonIgnore]
        public DateTime ModificationDateTime
        {
            get
            {
                try
                {
                    return DateUtils.ParseDateTime(ModificationDate);
                }
                catch (Exception)
                {
                    return DateTime.Now;
                }
            }
        }

        public virtual int CompareTo(object obj)
        {
            if (obj == null || !(obj is BrowserData)) return 1;
            if (this == obj) return 0;
            if (this.Id.HasValue && this.Id.Equals(((BrowserData)obj).Id)) return 0;
            if (!string.IsNullOrEmpty(this.Name)) return this.Name.CompareTo(((BrowserData)obj).Name);
            return -1;
        }

        public override string ToString()
        {
            return Name;
        }

    }
}

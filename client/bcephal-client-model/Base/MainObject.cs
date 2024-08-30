using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Text;

namespace Bcephal.Models.Base
{
    public class MainObject : Nameable
    {
        public BGroup group { get; set; }
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
    }
}

using Bcephal.Models.Base;
using Bcephal.Models.Reconciliation;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dimensions
{
    public class Attribute : Dimension
    {

        public ObservableCollection<Attribute> Children { get; set; }


        public Attribute()
        {
            
        }


        [JsonIgnore]
        public List<Attribute> Descendents
        {
            get
            {
                List<Attribute> attributes = new List<Attribute>();
                if (Children != null)
                {
                    foreach (Attribute attribute in Children)
                    {
                        attributes.Add(attribute);
                        if (attribute.Children != null)
                        {
                            attributes.AddRange(attribute.Descendents);
                        }
                    }
                }
                return attributes;
            }
        }


    }
}

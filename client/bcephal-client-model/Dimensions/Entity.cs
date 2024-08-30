using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dimensions
{
    public class Entity : Dimension
    {

        public ObservableCollection<Attribute> Attributes { get; set; }

        [JsonIgnore]
        public ObservableCollection<Attribute> Descendents
        {
            get
            {
                ObservableCollection<Attribute> attributes = new ObservableCollection<Attribute>();
                if (Attributes != null)
                {
                    foreach (Attribute attribute in Attributes)
                    {
                        attributes.Add(attribute);
                        if (attribute.Children != null)
                        {
                            foreach (Attribute child in attribute.Descendents)
                            {
                                attributes.Add(child);
                            }
                        }

                    }
                }
                return attributes;
            }
        }

    }
}

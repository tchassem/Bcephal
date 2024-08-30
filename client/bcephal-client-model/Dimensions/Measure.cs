using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dimensions
{
    public class Measure : Dimension
    {

        public ObservableCollection<Measure> Children { get; set; }

        [JsonIgnore]
        public List<Measure> Descendents
        {
            get
            {
                List<Measure> measures = new List<Measure>();
                if (Children != null)
                {
                    foreach (Measure measure in Children)
                    {
                        measures.Add(measure);
                        if (measure.Children != null)
                        {
                            measures.AddRange(measure.Descendents);
                        }
                    }
                }
                return measures;
            }
        }

    }
}

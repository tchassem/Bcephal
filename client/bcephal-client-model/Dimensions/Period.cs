using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dimensions
{
    public class Period : Dimension
    {

        public ObservableCollection<Period> Children { get; set; }

        [JsonIgnore]
        public List<Period> Descendents
        {
            get
            {
                List<Period> periods = new List<Period>();
                if (Children != null)
                {
                    foreach (Period period in Children)
                    {
                        periods.Add(period);
                        if (period.Children != null)
                        {
                            periods.AddRange(period.Descendents);
                        }
                    }
                }
                return periods;
            }
        }

    }
}

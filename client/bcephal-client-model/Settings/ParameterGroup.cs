using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Settings
{
    public class ParameterGroup
    {

        public string Code { get; set; }

        public string Name { get; set; }

        public bool CanBeCreateAutomatically { get; set; }

        public ObservableCollection<ParameterGroupItem> ParameterGroupItems { get; set; }

        public ObservableCollection<ParameterGroup> SubGroups { get; set; }


        public ParameterGroup()
        {
            this.ParameterGroupItems = new ObservableCollection<ParameterGroupItem>();
            this.SubGroups = new ObservableCollection<ParameterGroup>();
            this.CanBeCreateAutomatically = false;
        }

        public ParameterGroup(String code) : this()
        {
            this.Code = code;
        }

        public ParameterGroup(String code, string name)
            : this(code)
        {
            this.Name = name;
        }

        public ParameterGroup(String code, string name, bool canBeCreateAutomatically)
            : this(code, name)
        {
            this.CanBeCreateAutomatically = canBeCreateAutomatically;
        }


        public override string ToString()
        {
            return this.Name;
        }

    }
}

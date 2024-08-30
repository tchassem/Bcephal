using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Sheets
{
    public class Sheet
    {

        public int Index { get; set; }
        public string Name { get; set; }

        public Sheet() { }

        public Sheet(int index, string name) : this()
        {
            this.Index = index;
            this.Name = name;
        }

        public override string ToString()
        {
            return this.Name;
        }

    }
}

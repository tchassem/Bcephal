using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Functionalities
{
    public class Functionality : Persistent
    {

        public string Code { get; set; }

        public string Name { get; set; }

        public string Parentcode { get; set; }

        public bool CanView { get; set; }

        public bool CanEdit { get; set; }

        public bool IsTilable { get { return CanView || CanEdit; } }



        public override string ToString()
        {
            return Name;
        }
    }
}

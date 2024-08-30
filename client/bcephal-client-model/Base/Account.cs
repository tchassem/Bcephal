﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Base
{
    public class Account
    {
        public String number { get; set; }

        public String name { get; set; }

        public override string ToString()
        {
            return number + (String.IsNullOrWhiteSpace(name) ? "" : " - " + name);
        }

    }
}

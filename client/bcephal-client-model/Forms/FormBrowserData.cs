﻿using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class FormBrowserData : BrowserData
    {

        public Dictionary<int?, FormDataValue> Datas { set; get; }

    }
}
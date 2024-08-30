using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Projects
{
    public class ProjectBrowserData : BrowserData
    {

        public string Code { get; set; }

        public bool DefaultProject { get; set; }

        public long? SubscriptionId { get; set; }

    }
}

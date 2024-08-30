using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

namespace Bcephal.Models.Projects
{
    public class UserWorkspace
    {

        public ObservableCollection<BrowserData> Clients { get; set; }

        public BrowserData DefaultClient { get; set; }

        public ObservableCollection<ProjectBrowserData> AvailableProjects { get; set; }

        public ObservableCollection<ProjectBrowserData> DefaultProjects { get; set; }

    }
}

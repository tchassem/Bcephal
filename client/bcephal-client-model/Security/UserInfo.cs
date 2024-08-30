using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Security
{
    public class UserInfo
    {
        public string Login { get; set; }
        public string FirstName { get; set; }
        public string Name { get; set; }
        public string DefaultLanguage { get; set; }

        public string FullDescription { get { return Login + " (" + FirstName + " " + Name + ")"; } }

    }
}

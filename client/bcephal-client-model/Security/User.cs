using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Security
{
    public class User
    {
        public string Login { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public string Name { get; set; }
        public string DefaultLanguage { get; set; }
    }
}

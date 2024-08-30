
using Bcephal.Models.Base;

namespace Bcephal.Models.Projects
{
    public class Project : Nameable
    {

        public string Code { get; set; }

        public string Description { get; set; }

        public bool DefaultProject { get; set; }

    }
}

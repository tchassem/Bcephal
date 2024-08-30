using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Base
{
    public interface IPersistent : IComparable
    {
        long? Id { get; set; }
    }
}

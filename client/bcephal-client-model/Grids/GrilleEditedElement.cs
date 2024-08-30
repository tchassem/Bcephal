using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Grids
{
    public class GrilleEditedElement
    {

        public long? Id { get; set; }

        public GrilleColumn Column { get; set; }

        public string StringValue { get; set; }

        public decimal? DecimalValue { get; set; }

        public DateTime? DateValue { get; set; }

        public Grille Grid { get; set; }

    }
}

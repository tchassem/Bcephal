using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class FormModelMenu : Persistent
    {

		public bool Active { get; set; }

		public string Parent { get; set; }

		public string Caption { get; set; }

		public bool AllowNewMenu { get; set; }
		public string NewMenuCaption { get; set; }

		public bool AllowListMenu { get; set; }
		public string ListMenuCaption { get; set; }

		public int Position { get; set; }

		public long? FormId { get; set; }

		public FormModelMenu()
        {
			this.Active = true;
			this.AllowNewMenu = true;
			this.AllowListMenu = true;
		}

		public FormModelMenu(string modelName) :this()
		{
			this.Caption = modelName;
			this.NewMenuCaption = "New " + modelName;
			this.ListMenuCaption = "List " + modelName;
		}

	}
}

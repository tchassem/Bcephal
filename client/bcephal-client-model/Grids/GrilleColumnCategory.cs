using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Grids
{
    public enum GrilleColumnCategory
    {
		/**
	 * System column can't be delete by user
	 */
		SYSTEM,

		/**
		 * User columns are created by user. They are deletable.
		 */
		USER,
	}
}

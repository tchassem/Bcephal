using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Grids
{
    public enum GrilleCategory
    {
		/**
	 * Grid column can't be delete by user
	 */
		SYSTEM,

		/**
		 * User grids are created by user. They are deletable.
		 */
		USER,
	}
}

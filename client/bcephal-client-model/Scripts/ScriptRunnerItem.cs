using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Scripts
{
    public class ScriptRunnerItem : Persistent
    {
        public string Description { get; set; }

        public string Script { get; set; }

        public int Position { get; set; }

		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is ScriptRunnerItem)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((ScriptRunnerItem)obj).Id)) return 0;
			if (this.Position.Equals(((ScriptRunnerItem)obj).Position))
			{
				return this.Description.CompareTo(((ScriptRunnerItem)obj).Description);
			}
			return this.Position.CompareTo(((ScriptRunnerItem)obj).Position);
		}


	}
}

using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class SubFormModel : AbstractFormModel
    {

		public long? JoinFormFieldId { get; set; }

		public long? JoinSubFormFieldId { get; set; }

		public bool DisplayInSeparatedTab { get; set; }

		public string SubFormType { get; set; }

		[JsonIgnore]
		public SubFormModelType SubFormModelType
		{
			get { return SubFormModelType.GetByCode(this.SubFormType); }
			set { this.SubFormType = value != null ? value.code : null; }
		}

		public int Position { get; set; }

		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is SubFormModel)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((SubFormModel)obj).Id)) return 0;
			if (this.Position.Equals(((SubFormModel)obj).Position))
			{
				return this.Name.CompareTo(((SubFormModel)obj).Name);
			}
			return this.Position.CompareTo(((SubFormModel)obj).Position);
		}

	}
}

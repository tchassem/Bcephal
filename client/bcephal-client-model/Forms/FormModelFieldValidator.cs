using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class FormModelFieldValidator : Persistent
    {

		public string Type { get; set; }
		[JsonIgnore]
		public FormModelFieldValidatorType ValidatorType
		{
			get { return FormModelFieldValidatorType.GetByCode(this.Type); }
			set { this.Type = value != null ? value.code : null; }
		}

		[JsonIgnore]
		public ObservableCollection<FormModelFieldValidatorType> ValidatorTypes
		{
			get { return FormModelFieldValidatorType.GetAll(); }
			set {  }
		}

		public int? IntegerValue { get; set; }

		public decimal? DecimalValue { get; set; }

		public string StringValue { get; set; }

		public PeriodValue DateValue { get; set; }

		public int Position { get; set; }

		public string Message { get; set; }



		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is FormModelFieldValidator)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((FormModelFieldValidator)obj).Id)) return 0;
			if (this.Position.Equals(((FormModelFieldValidator)obj).Position))
			{
				return this.Type.CompareTo(((FormModelFieldValidator)obj).Type);
			}
			return this.Position.CompareTo(((FormModelFieldValidator)obj).Position);
		}


	}
}

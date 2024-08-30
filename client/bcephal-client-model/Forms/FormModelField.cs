using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class FormModelField : Persistent
    {

        [JsonIgnore]
        public FormModelField Parent { get; set; }

        public bool Group { get; set; }

        public string Orientation { get; set; }
        [JsonIgnore]
        public FormModelFieldOrientaton FieldOrientaton
        {
            get { return FormModelFieldOrientaton.GetByCode(this.Orientation); }
            set { this.Orientation = value != null ? value.code : null; }
        }

        public string LabelPosition { get; set; }
        [JsonIgnore]
        public FormModelFieldLabelPosition FieldLabelPosition
        {
            get { return FormModelFieldLabelPosition.GetByCode(this.LabelPosition); }
            set { this.LabelPosition = value != null ? value.code : null; }
        }

        public string Label { get; set; }

		public string Description { get; set; }

		public int Position { get; set; }

		public long? ColumnId { get; set; }
        [JsonIgnore]
        public GrilleColumn Column { get; set; }

        public DimensionType? DimensionType { get; set; }

		public long? DimensionId { get; set; }

		public string DimensionName { get; set; }
			
		public DimensionFormat Format { get; set; }

		public long? LinkedAttributeId { get; set; }

		public long? ReferenceFieldId { get; set; }

		public string Nature { get; set; }
        [JsonIgnore]
        public FormModelFieldNature FieldNature
        {
            get { return FormModelFieldNature.GetByCode(this.Nature); }
            set { this.Nature = value != null ? value.code : null; }
        }

        public string Type { get; set; }
        [JsonIgnore]
        public FormModelFieldType FieldType
        {
            get { return FormModelFieldType.GetByCode(this.Type); }
            set { this.Type = value != null ? value.code : null; }
        }

        [JsonIgnore]
        public ObservableCollection<FormModelFieldType> FieldTypes
        {
            get { return FormModelFieldType.GetAll(); }
            set {  }
        }

        public bool Key { get; set; }

		public bool VisibleInEditor { get; set; }

		public bool VisibleInBrowser { get; set; }

		public bool AllowValidation { get; set; }

		public bool Mandatory { get; set; }

		public string AttachmentName { get; set; }

		public int? BackgroundColor { get; set; }

		public int? ForegroundColor { get; set; }

		public string FontFamilly { get; set; }

		public int? FontSize { get; set; }

		public ListChangeHandler<FormModelFieldValidator> ValidatorListChangeHandler { get; set; }

        public ListChangeHandler<FormModelField> ChildrenListChangeHandler { get; set; }


        [JsonIgnore] public bool IsAttribute { get { return this.DimensionType.HasValue && this.DimensionType.Value.IsAttribute(); } }

        [JsonIgnore] public bool IsMeasure { get { return this.DimensionType.HasValue && this.DimensionType.Value.IsMeasure(); } }

        [JsonIgnore] public bool IsPeriod { get { return this.DimensionType.HasValue && this.DimensionType.Value.IsPeriod(); } }

        public FormModelField()
        {
            this.ValidatorListChangeHandler = new ListChangeHandler<FormModelFieldValidator>();
            this.ChildrenListChangeHandler = new ListChangeHandler<FormModelField>();
            this.VisibleInBrowser = true;
            this.VisibleInEditor = true;
            this.AllowValidation = true;
        }

        public FormModelField(GrilleColumn column) : this()
        {
            this.Column = column;
            this.ColumnId = column.Id;
            this.DimensionId = column.DimensionId;
            this.DimensionName = column.DimensionName;
            this.DimensionType = column.Type;
            this.Label = column.Name;
            this.FieldType = FormModelFieldType.EDITION;
        }

        public void AddValidator(FormModelFieldValidator validator, bool sort = true)
        {
            validator.Position = ValidatorListChangeHandler.Items.Count;
            ValidatorListChangeHandler.AddNew(validator, sort);
        }

        public void UpdateValidator(FormModelFieldValidator validator, bool sort = true)
        {
            ValidatorListChangeHandler.AddUpdated(validator, sort);
        }

        public void InsertValidator(int position, FormModelFieldValidator validator)
        {
            validator.Position = position;
            foreach (FormModelFieldValidator child in ValidatorListChangeHandler.Items)
            {
                if (child.Position >= validator.Position)
                {
                    child.Position = child.Position + 1;
                    ValidatorListChangeHandler.AddUpdated(child, false);
                }
            }
            ValidatorListChangeHandler.AddNew(validator);
        }

        public void DeleteOrForgetValidator(FormModelFieldValidator validator)
        {
            if (validator.IsPersistent)
            {
                DeleteValidator(validator);
            }
            else
            {
                ForgetValidator(validator);
            }
        }

        public void DeleteValidator(FormModelFieldValidator validator)
        {
            ValidatorListChangeHandler.AddDeleted(validator);
            foreach (FormModelFieldValidator child in ValidatorListChangeHandler.Items)
            {
                if (child.Position > validator.Position)
                {
                    child.Position = child.Position - 1;
                    ValidatorListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetValidator(FormModelFieldValidator validator)
        {
            ValidatorListChangeHandler.forget(validator);
            foreach (FormModelFieldValidator child in ValidatorListChangeHandler.Items)
            {
                if (child.Position > validator.Position)
                {
                    child.Position = child.Position - 1;
                    ValidatorListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is FormModelField)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((FormModelField)obj).Id)) return 0;
			if (this.Position.Equals(((FormModelField)obj).Position))
			{
				return this.Label.CompareTo(((FormModelField)obj).Label);
			}
			return this.Position.CompareTo(((FormModelField)obj).Position);
		}

        public override string ToString()
        {
            return this.Label;
        }


    }
}

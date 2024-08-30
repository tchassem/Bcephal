using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Newtonsoft.Json;
using System;


namespace Bcephal.Models.Reconciliation
{
    public class WriteOffField : Persistent
    {

		public int Position { get; set; }

		public string DefaultValueType { get; set; }

		public WriteOffFieldValueType DefaultValueTypes
		{
			get
			{
				return string.IsNullOrEmpty(DefaultValueType) ? WriteOffFieldValueType.LEFT_SIDE : WriteOffFieldValueType.GetByCode(DefaultValueType);
			}
			set
			{
				this.DefaultValueType = value != null ? value.getCode() : null;
			}
		}

		public DimensionType DimensionType { get; set; }

		public long? DimensionId { get; set; }

		public bool Mandatory { get; set; }

		public bool AllowNewValue { get; set; }

		public bool DefaultValue { get; set; }

		public string StringValue { get; set; }

		public decimal? DecimalValue { get; set; }
		
		public PeriodValue DateValue { get; set; }

		public ListChangeHandler<WriteOffFieldValue> ValueListChangeHandler { get; set; }


		[JsonIgnore] public bool IsAttribute { get { return this.DimensionType == DimensionType.ATTRIBUTE; } }

		[JsonIgnore] public bool IsMeasure { get { return this.DimensionType == DimensionType.MEASURE; } }

		[JsonIgnore] public bool IsPeriod { get { return this.DimensionType == DimensionType.PERIOD; } }

		public WriteOffField()
		{
			this.ValueListChangeHandler = new ListChangeHandler<WriteOffFieldValue>();
		}

		[JsonIgnore]
		public string Key
		{
			get
			{
				if (string.IsNullOrWhiteSpace(Key_))
				{
					Key_ = Guid.NewGuid().ToString("d");
				}
				return Key_;
			}
			set
			{
				Key_ = value;
			}
		}
		[JsonIgnore]
		private string Key_ { get; set; }

		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is WriteOffField)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((WriteOffField)obj).Id)) return 0;
			if (this.Position.Equals(((WriteOffField)obj).Position))
			{
				return this.DimensionType.CompareTo(((WriteOffField)obj).DimensionType);
			}
			return this.Position.CompareTo(((WriteOffField)obj).Position);
		}

		public void AddWriteOffFieldValue(WriteOffFieldValue writeOffFieldValue, bool sort = true)
		{
			writeOffFieldValue.Position = ValueListChangeHandler.Items.Count;
			ValueListChangeHandler.AddNew(writeOffFieldValue, sort);
		}

		public void UpdateWriteOffFieldValue(WriteOffFieldValue writeOffFieldValue, bool sort = true)
		{
			ValueListChangeHandler.AddUpdated(writeOffFieldValue, sort);
		}

		public void InsertWriteOffFieldValue(int position, WriteOffFieldValue writeOffFieldValue)
		{
			writeOffFieldValue.Position = position;
			foreach (WriteOffFieldValue child in ValueListChangeHandler.Items)
			{
				if (child.Position >= writeOffFieldValue.Position)
				{
					child.Position = child.Position + 1;
					ValueListChangeHandler.AddUpdated(child, false);
				}
			}
			ValueListChangeHandler.AddNew(writeOffFieldValue);
		}


		public void DeleteOrForgetWriteOffFieldValue(WriteOffFieldValue writeOffFieldValue)
		{
			if (writeOffFieldValue.IsPersistent)
			{
				DeleteWriteOffFieldValue(writeOffFieldValue);
			}
			else
			{
				ForgetWriteOffFieldValue(writeOffFieldValue);
			}
		}

		public void DeleteWriteOffFieldValue(WriteOffFieldValue writeOffFieldValue)
		{
			ValueListChangeHandler.AddDeleted(writeOffFieldValue);
			foreach (WriteOffFieldValue child in ValueListChangeHandler.Items)
			{
				if (child.Position > writeOffFieldValue.Position)
				{
					child.Position = child.Position - 1;
					ValueListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void ForgetWriteOffFieldValue(WriteOffFieldValue writeOffFieldValue)
		{
			ValueListChangeHandler.forget(writeOffFieldValue);
			foreach (WriteOffFieldValue child in ValueListChangeHandler.Items)
			{
				if (child.Position > writeOffFieldValue.Position)
				{
					child.Position = child.Position - 1;
					ValueListChangeHandler.AddUpdated(child, false);
				}
			}
		}

	}
}

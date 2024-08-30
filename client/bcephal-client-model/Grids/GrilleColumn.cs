using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Filters;
using Bcephal.Models.Utils;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Media;

namespace Bcephal.Models.Grids
{
	[Serializable]
	public class GrilleColumn : Persistent
	{

		public String Name { get; set; }

		public DimensionType Type { get; set; }

		public long? DimensionId { get; set; }

		public string DimensionName { get; set; }

		public string DimensionFunction { get; set; }

		public string DimensionFormat { get; set; }

		[JsonIgnore]
		public MeasureFunctions MeasureFunction
		{
			get { return MeasureFunctions.GetByCode(this.DimensionFunction); }
			set { this.DimensionFunction = value != null ? value.code : null; }
		}


		[JsonIgnore]
		public Grille Grid { get; set; }

		public GrilleColumnCategory Category;

		public bool Mandatory { get; set; }

		public bool Editable { get; set; }

		public bool ShowValuesInDropList { get; set; }

		public int Position { get; set; }

		public bool Show { get; set; }

		public int? BackgroundColor { get; set; }

		public int? ForegroundColor { get; set; }

		public int? Width { get; set; }

		public string FixedType { get; set; }

		[JsonIgnore]
		public GrilleColumnFixedStyle ColumnFixedStyle
		{
			get { return GrilleColumnFixedStyle.GetByCode(this.FixedType); }
			set { this.FixedType = value != null ? value.code : null; }
		}

		public string DefaultStringValue { get; set; }

		public decimal? DefaultDecimalValue { get; set; }

		public DateTime? DefaultDateValue { get; set; }

		public bool ApplyDefaultValueIfCellEmpty { get; set; }

		public bool ApplyDefaultValueToFutureLine { get; set; }

		public DimensionFormat Format { get; set; }

		public bool? OrderAsc { get; set; }

		public string GroupBy { get; set; }

		//[JsonIgnore]
		//public PeriodGrouping PeriodGroupBy
		//{
		//	get { return PeriodGrouping.GetByCode(this.GroupBy); }
		//	set { this.GroupBy = value != null ? value.code : null; }
		//}

		[JsonIgnore]
		public object DefaultValue
		{
			get
			{
				if (this.IsMeasure)
				{
					return this.DefaultDecimalValue;
				}
				if (this.IsPeriod)
				{
					return this.DefaultDateValue;
				}
				return this.DefaultStringValue;
			}
			set
			{
				this.DefaultDateValue = null;
				this.DefaultDecimalValue = null;
				this.DefaultStringValue = null;
				if (this.IsMeasure)
				{
					this.DefaultDecimalValue = (decimal?)value;
				}
				else if (this.IsPeriod)
				{
					this.DefaultDateValue = (DateTime?)value;
				}
				else
				{
					this.DefaultStringValue = (string)value;
				}
			}
		}

		[JsonIgnore]
		public string TypeAsString { get { return this.Type.ToString(); } set { } }

		[JsonIgnore]
		public string CategoryAsString { get { return this.Category.ToString(); } set { } }

		[JsonIgnore]
		public string ColumnName { get { return RangeUtil.GetColumnName(this.Position + 1); } set { } }

   //     [JsonIgnore]
   //     public Color Background
   //     {
   //         get
   //         {
   //             return this.BackgroundColor.HasValue ?
   //             new SolidColorBrush(ColorToLongConverter.DecimalToColor(this.BackgroundColor.Value)).Color
   //                 : GrilleColumnStyle.DEFAULT_BACKGROUND.Color;
   //         }
   //         set 
			//{ 
			//	this.BackgroundColor = ColorToLongConverter.ColorToDecimal(value);
			//}
   //     }

   //     [JsonIgnore]
   //     public Color Foreground
   //     {
   //         get
   //         {
   //             return this.ForegroundColor.HasValue ?
   //                 new SolidColorBrush(ColorToLongConverter.DecimalToColor(this.ForegroundColor.Value)).Color
   //               : GrilleColumnStyle.DEFAULT_FOREGROUND.Color;
   //         }
   //         set 
			//{
			//	this.ForegroundColor = ColorToLongConverter.ColorToDecimal(value);
			//}
   //     }


		[JsonIgnore]
		public string DefaultStringColorB = "#adb9ca";
		[JsonIgnore]
		public string DefaultStringColorF = "#ffffff";

		[JsonIgnore]
		public string Backgrounds
		{
			get
			{
				return BackgroundColor.HasValue ? convertToHex(BackgroundColor.Value) : DefaultStringColorB;
			}
			set
			{
				BackgroundColor = Convert.ToInt32(value.Substring(1), 16);
			}
		}
		[JsonIgnore]
		public string Foregrounds
		{
			get
			{
				return ForegroundColor.HasValue ? convertToHex(ForegroundColor.Value) : DefaultStringColorF;
			}
			set
			{
				ForegroundColor = Convert.ToInt32(value.Substring(1), 16);
			}
		}

		private string convertToHex(int color)
		{
			string hex = color.ToString("X");
			while (hex.Length < 6)
			{
				hex = "0" + hex;
			}
			return "#" + hex;
		}

		[JsonIgnore] public bool IsAttribute { get { return this.Type == DimensionType.ATTRIBUTE; } }

		[JsonIgnore] public bool IsMeasure { get { return this.Type == DimensionType.MEASURE; } }

		[JsonIgnore] public bool IsPeriod { get { return this.Type == DimensionType.PERIOD; } }


		public GrilleColumn()
        {
			this.Type = DimensionType.ATTRIBUTE;
			this.Category = GrilleColumnCategory.USER;
			this.Show = true;
			this.Editable = true;
			this.ShowValuesInDropList = false;
			this.Format = new DimensionFormat();
		}


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is GrilleColumn)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((GrilleColumn)obj).Id)) return 0;
			if (this.Position.Equals(((GrilleColumn)obj).Position))
			{
				return this.Name.CompareTo(((GrilleColumn)obj).Name);
			}
			return this.Position.CompareTo(((GrilleColumn)obj).Position);
		}

		public override string ToString()
		{
			return this.Name;
		}


	}
}

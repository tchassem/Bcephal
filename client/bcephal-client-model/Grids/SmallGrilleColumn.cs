using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Filters;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Grids
{
    public class SmallGrilleColumn : Persistent
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

		public long? GridId { get; set; }

		public int Position { get; set; }

		public bool Show { get; set; }


		public int? BackgroundColor { get; set; }

		public int? ForegroundColor { get; set; }

		public int? Width { get; set; }

		public string FixedType { get; set; }

		public PeriodGrouping? GroupBy { get; set; }

		[JsonIgnore]
		public GrilleColumnFixedStyle ColumnFixedStyle
		{
			get { return GrilleColumnFixedStyle.GetByCode(this.FixedType); }
			set { this.FixedType = value != null ? value.code : null; }
		}

		public DimensionFormat Format { get; set; }

		[JsonIgnore]
		public string TypeAsString { get { return this.Type.ToString(); } set { } }

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


		public SmallGrilleColumn()
		{
			this.Type = DimensionType.ATTRIBUTE;
			this.Show = true;
			this.Format = new DimensionFormat();
		}

		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is GrilleColumn)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((GrilleColumn)obj).Id)) return 0;
			if (this.Name.Equals(((GrilleColumn)obj).Name))
			{
				return this.Name.CompareTo(((GrilleColumn)obj).Name);
			}
			return this.Name.CompareTo(((GrilleColumn)obj).Name);
		}

		public override string ToString()
		{
			return this.Name;
		}
	}
}

using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Joins
{
	[Serializable]
	public class JoinColumn : Persistent
	{
		public string Name { get; set; }

		public JoinColumnCategory Category { get; set; }

		public DimensionType Type { get; set; }

		public long? DimensionId { get; set; }

		public string DimensionName { get; set; }

		public string DimensionFunction { get; set; }

		public string DimensionFormat { get; set; }

		public long? GridId { get; set; }

		public long? ColumnId { get; set; }

		public string ColumnName { get; set; }

		public bool Show { get; set; }

		public int? BackgroundColor { get; set; }

		public int? ForegroundColor { get; set; }

		[JsonIgnore]
		public GrilleColumnFixedStyle ColumnFixedStyle
		{
			get { return GrilleColumnFixedStyle.GetByCode(this.FixedType); }
			set { this.FixedType = value != null ? value.code : null; }
		}
		public int? Width { get; set; }

		public string FixedType { get; set; }

		public int Position { get; set; }

		public DimensionFormat Format { get; set; }

		public Grids.PeriodGrouping? GroupBy { get; set; }
        public string TypeAsString { get; }

        public bool UsedForPublication { get; set; }

        public bool IsAttribute { get; }

        public bool IsMeasure { get; }

        public bool IsPeriod { get; }

		public long? PublicationDimensionId { get; set; }

		public string PublicationDimensionName { get; set; }

		public JoinColumnProperties Properties { get; set; }


		public JoinColumn()
		{
			this.Show = true;
			this.Format = new DimensionFormat();
			this.Category = JoinColumnCategory.STANDARD;
		}

		public JoinColumn(SmallGrilleColumn smallGrilleColumn )
        {
			this.Name = smallGrilleColumn.Name;
			this.Category = JoinColumnCategory.STANDARD;
			this.Type = smallGrilleColumn.Type;
			this.DimensionId = smallGrilleColumn.DimensionId;
			this.DimensionName = smallGrilleColumn.DimensionName;
			this.DimensionFunction = smallGrilleColumn.DimensionFunction;
			this.DimensionFormat = smallGrilleColumn.DimensionFormat;
			this.GridId = smallGrilleColumn.GridId;
			this.Show = smallGrilleColumn.Show;
			this.BackgroundColor = smallGrilleColumn.BackgroundColor;
			this.ForegroundColor = smallGrilleColumn.ForegroundColor;
            this.ColumnFixedStyle = smallGrilleColumn.ColumnFixedStyle;
			this.Width = smallGrilleColumn.Width;
			this.FixedType = smallGrilleColumn.FixedType;
			this.Position = smallGrilleColumn.Position;
			this.Format = smallGrilleColumn.Format;
			this.GroupBy = smallGrilleColumn.GroupBy;
			this.TypeAsString = smallGrilleColumn.TypeAsString;
			this.IsAttribute = smallGrilleColumn.IsAttribute;
			this.IsMeasure = smallGrilleColumn.IsMeasure;
			this.IsPeriod = smallGrilleColumn.IsPeriod;
			this.UsedForPublication = true;
            this.ColumnId = smallGrilleColumn.Id;
			this.ColumnName = smallGrilleColumn.Name;

        }

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

		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is JoinColumn)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((JoinColumn)obj).Id)) return 0;
			if (this.Position.Equals(((JoinColumn)obj).Position))
			{
				return this.Name.CompareTo(((JoinColumn)obj).Name);
			}
			return this.Position.CompareTo(((JoinColumn)obj).Position);
		}
	}
}

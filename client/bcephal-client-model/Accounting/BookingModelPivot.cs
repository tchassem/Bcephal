using System;

namespace Bcephal.Models.Base.Accounting
{
    public class BookingModelPivot : Persistent
    {
        public string Name { get; set; }
        public long? DimensionId  { get; set; }
        public int Position { get; set; }
        public bool? Show { get; set; }

        public BookingModelPivot()
        {
            this.Position = -1;
            this.Show = true;
        }


        public override String ToString()
        {
            return Name;
        }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is BookingModelPivot)) return 1;
            return this.Position.CompareTo(((BookingModelPivot)obj).Position);
        }

    }
}

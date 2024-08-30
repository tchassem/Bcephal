using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dashboards
{
    public class DashboardLayout
    {

        public static DashboardLayout ONE = new DashboardLayout("ONE", "1 panel");

        public static DashboardLayout VERTICAL_2x1 = new DashboardLayout("VERTICAL_2x1", "2 x 1 Vertical panels", 2, 1, "Vertical");
        public static DashboardLayout VERTICAL_2x2 = new DashboardLayout("VERTICAL_2x2", "2 x 2 Vertical panels", 2, 2, "Vertical");
        public static DashboardLayout VERTICAL_3x3 = new DashboardLayout("VERTICAL_3x3", "3 x 3 Vertical panels", 3, 3, "Vertical");
        public static DashboardLayout VERTICAL_3x4 = new DashboardLayout("VERTICAL_3x4", "3 x 4 Vertical panels", 3, 4, "Vertical");
        public static DashboardLayout VERTICAL_4x3 = new DashboardLayout("VERTICAL_4x3", "4 x 3 Vertical panels", 4, 3, "Vertical");

        public static DashboardLayout HORIZONTAL_1x2 = new DashboardLayout("HORIZONTAL_1x2", "1 x 2 Horizontal panels", 1, 2, "Horizontal");
        public static DashboardLayout HORIZONTAL_2x2 = new DashboardLayout("HORIZONTAL_2x2", "2 x 2 Horizontal panels", 2, 2, "Horizontal");
        public static DashboardLayout HORIZONTAL_3x3 = new DashboardLayout("HORIZONTAL_3x3", "3 x 3 Horizontal panels", 3, 3, "Horizontal");
        public static DashboardLayout HORIZONTAL_3x4 = new DashboardLayout("HORIZONTAL_3x4", "3 x 4 Horizontal panels", 3, 4, "Horizontal");
        public static DashboardLayout HORIZONTAL_4x3 = new DashboardLayout("HORIZONTAL_4x3", "4 x 3 Horizontal panels", 4, 3, "Horizontal");



        public string Code { get; protected set; }
        public string Label { get; protected set; }
        public string Group { get; protected set; }
        public int GroupCount { get; protected set; }
        public int BoxCount { get; protected set; }

        public DashboardLayout(string code, string label, int groupCount = 0, int boxCount = 0, string group = null)
        {
            this.Label = label;
            this.Code = code;
            this.Group = group;
            this.GroupCount = groupCount;
            this.BoxCount = boxCount;
        }

        public override string ToString()
        {
            return this.Label;
        }

        public bool IsOne()
        {
            return this == DashboardLayout.ONE;
        }

        public bool IsVertical2x1()
        {
            return this == DashboardLayout.VERTICAL_2x1;
        }
        public bool IsVertical2x2()
        {
            return this == DashboardLayout.VERTICAL_2x2;
        }


        public bool IsVertical3x3()
        {
            return this == DashboardLayout.VERTICAL_3x3;
        }

        public bool IsVertical3x4()
        {
            return this == DashboardLayout.VERTICAL_3x4;
        }

        public bool IsVertical4x3()
        {
            return this == DashboardLayout.VERTICAL_4x3;
        }

        public bool IsHorizontal1x2()
        {
            return DashboardLayout.HORIZONTAL_1x2.Code.Equals(this.Code);
        }

        public bool IsHorizontal2x2()
        {
            return DashboardLayout.HORIZONTAL_2x2.Code.Equals(this.Code);
        }

        public bool IsHorizontal3x3()
        {
            return this == DashboardLayout.HORIZONTAL_3x3;
        }

        public bool IsHorizontal3x4()
        {
            return this == DashboardLayout.HORIZONTAL_3x4;
        }

        public bool IsHorizontal4x3()
        {
            return this == DashboardLayout.HORIZONTAL_4x3;
        }



        public bool IsHorizontal()
        {
            return this.IsHorizontal1x2() ||   this.IsHorizontal2x2() || this.IsHorizontal3x3() || this.IsHorizontal3x4() || this.IsHorizontal4x3();
        }

        public bool IsVertical()
        {
            return this.IsVertical2x1() ||  this.IsVertical2x2() || this.IsVertical3x3() || this.IsVertical3x4() || this.IsVertical4x3();
        }

        public static DashboardLayout GetByCode(string code)
        {
            if (DashboardLayout.ONE.Code.Equals(code)) return DashboardLayout.ONE;
            if (DashboardLayout.VERTICAL_2x1.Code.Equals(code)) return DashboardLayout.VERTICAL_2x1;
            if (DashboardLayout.VERTICAL_2x2.Code.Equals(code)) return DashboardLayout.VERTICAL_2x2;
            if (DashboardLayout.VERTICAL_3x3.Code.Equals(code)) return DashboardLayout.VERTICAL_3x3;
            if (DashboardLayout.VERTICAL_3x4.Code.Equals(code)) return DashboardLayout.VERTICAL_3x4;
            if (DashboardLayout.VERTICAL_4x3.Code.Equals(code)) return DashboardLayout.VERTICAL_4x3;
            if (DashboardLayout.HORIZONTAL_2x2.Code.Equals(code)) return DashboardLayout.HORIZONTAL_2x2;
            if (DashboardLayout.HORIZONTAL_1x2.Code.Equals(code)) return DashboardLayout.HORIZONTAL_1x2;
            if (DashboardLayout.HORIZONTAL_3x3.Code.Equals(code)) return DashboardLayout.HORIZONTAL_3x3;
            if (DashboardLayout.HORIZONTAL_3x4.Code.Equals(code)) return DashboardLayout.HORIZONTAL_3x4;
            if (DashboardLayout.HORIZONTAL_4x3.Code.Equals(code)) return DashboardLayout.HORIZONTAL_4x3;
            return DashboardLayout.VERTICAL_2x2;
        }


    }
}


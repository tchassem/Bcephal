using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dashboards
{
    public class DashboardReportChartType
    {

        public static DashboardReportChartType SIDE_BY_SIDE_BAR = new DashboardReportChartType("SIDE_BY_SIDE_BAR", "Side by side bar");
        public static DashboardReportChartType STACKED_BAR = new DashboardReportChartType("STACKED_BAR", "Stacked bar");
        public static DashboardReportChartType FULL_STACKED_BAR = new DashboardReportChartType("FULL_STACKED_BAR", "Full stacked bar");

        public static DashboardReportChartType LINE = new DashboardReportChartType("LINE", "Line");

        public static DashboardReportChartType POINT = new DashboardReportChartType("POINT", "Point");
        public static DashboardReportChartType BUBBLE = new DashboardReportChartType("BUBBLE", "Bubble");


        public static DashboardReportChartType AREA = new DashboardReportChartType("AREA", "Area");
        public static DashboardReportChartType STACKED_AREA = new DashboardReportChartType("STACKED_AREA", "Stacked area");
        public static DashboardReportChartType FULL_STACKED_AREA = new DashboardReportChartType("FULL_STACKED_AREA", "Full stacked area");


        public String Code { get; protected set; }
        public String Label { get; protected set; }

        public DashboardReportChartType(String code, String Label)
        {
            this.Label = Label;
            this.Code = code;
        }

        public override string ToString()
        {
            return this.Label;
        }

        public bool IsSideBySideBar()
        {
            return this == DashboardReportChartType.SIDE_BY_SIDE_BAR;
        }

        public bool IsStackedBar()
        {
            return this == DashboardReportChartType.STACKED_BAR;
        }

        public bool IsFullStackedBar()
        {
            return this == DashboardReportChartType.FULL_STACKED_BAR;
        }

        public bool IsLine()
        {
            return this == DashboardReportChartType.LINE;
        }

        public bool IsPoint()
        {
            return this == DashboardReportChartType.POINT;
        }

        public bool IsBubble()
        {
            return this == DashboardReportChartType.BUBBLE;
        }

        public bool IsArea()
        {
            return this == DashboardReportChartType.AREA;
        }

        public bool IsStackedArea()
        {
            return this == DashboardReportChartType.STACKED_AREA;
        }

        public bool IsFullStackedArea()
        {
            return this == DashboardReportChartType.FULL_STACKED_AREA;
        }



        public static DashboardReportChartType GetByCode(String code)
        {
            if (DashboardReportChartType.SIDE_BY_SIDE_BAR.Code.Equals(code)) return DashboardReportChartType.SIDE_BY_SIDE_BAR;
            if (DashboardReportChartType.STACKED_BAR.Code.Equals(code)) return DashboardReportChartType.STACKED_BAR;
            if (DashboardReportChartType.FULL_STACKED_BAR.Code.Equals(code)) return DashboardReportChartType.FULL_STACKED_BAR;
            if (DashboardReportChartType.LINE.Code.Equals(code)) return DashboardReportChartType.LINE;
            if (DashboardReportChartType.POINT.Code.Equals(code)) return DashboardReportChartType.POINT;
            if (DashboardReportChartType.BUBBLE.Code.Equals(code)) return DashboardReportChartType.BUBBLE;
            if (DashboardReportChartType.AREA.Code.Equals(code)) return DashboardReportChartType.AREA;
            if (DashboardReportChartType.STACKED_AREA.Code.Equals(code)) return DashboardReportChartType.STACKED_AREA;
            if (DashboardReportChartType.FULL_STACKED_AREA.Code.Equals(code)) return DashboardReportChartType.FULL_STACKED_AREA;
            return DashboardReportChartType.SIDE_BY_SIDE_BAR;
        }

        public static DashboardReportChartType[] Types()
        {
            return new[] {
                DashboardReportChartType.AREA,
                DashboardReportChartType.BUBBLE,
                DashboardReportChartType.FULL_STACKED_AREA,
                DashboardReportChartType.FULL_STACKED_BAR,
                DashboardReportChartType.LINE,
                DashboardReportChartType.POINT,
                DashboardReportChartType.SIDE_BY_SIDE_BAR,
                DashboardReportChartType.STACKED_AREA,
                DashboardReportChartType.STACKED_BAR};
        }

    }
}

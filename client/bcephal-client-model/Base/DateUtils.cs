
using System;
using System.Globalization;

namespace Bcephal.Models.Base
{
    public static class DateUtils
    {

        public static string SHORT_DATE_FORMAT = "dd/MM/yyyy";

        public static string SHORT_DATE_FORMAT2 = "yyyy-MM-dd";

        public static string SHORT_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

        public static DateTime? GetDate(string dynamicPeriodType)
        {
            //if (DynamicPeriodType.IsToday(dynamicPeriodType))
            //{
            //    return DateTime.Today;
            //}
            //else if (DynamicPeriodType.IsBeginWeek(dynamicPeriodType))
            //{
            //    return DateTime.Today.StartOfWeek();
            //}
            //else if (DynamicPeriodType.IsEndWeek(dynamicPeriodType))
            //{
            //    return DateTime.Today.EndOfWeek();
            //}
            //else if (DynamicPeriodType.IsBeginMonth(dynamicPeriodType))
            //{
            //    return DateTime.Today.StartOfMonth();
            //}
            //else if (DynamicPeriodType.IsEndMonth(dynamicPeriodType))
            //{
            //    return DateTime.Today.EndOfMonth();
            //}
            //else if (DynamicPeriodType.IsBeginYear(dynamicPeriodType))
            //{
            //    return DateTime.Today.StartOfYear();
            //}
            //else if (DynamicPeriodType.IsEndYear(dynamicPeriodType))
            //{
            //    return DateTime.Today.EndOfYear();
            //}
            return null;
        }

        public static DateTime Parse(string dateString)
        {
            try
            {
                return DateTime.ParseExact(dateString, SHORT_DATE_FORMAT, CultureInfo.CurrentCulture);
            }
            catch
            {
                return DateTime.ParseExact(dateString, SHORT_DATE_FORMAT2, CultureInfo.CurrentCulture);
            }            
        }

        public static DateTime ParseDateTime(string dateTimeString)
        {
            return DateTime.ParseExact(dateTimeString, SHORT_DATE_TIME_FORMAT, CultureInfo.CurrentCulture);
        }

        public static string Format(DateTime? date)
        {
            return date.HasValue ? Format(date.Value) : null;
        }

        public static string Format(DateTime date)
        {
            return date.ToString(SHORT_DATE_FORMAT, CultureInfo.InvariantCulture);
        }

        public static DateTime StartOfWeek(this DateTime date)
        {
            int diff = (7 + (date.DayOfWeek - DayOfWeek.Monday)) % 7;
            return date.AddDays(-1 * diff).Date;
        }

        public static DateTime EndOfWeek(this DateTime date)
        {
            DateTime first = StartOfWeek(date);
            return first.AddDays(6);
        }

        public static DateTime StartOfMonth(this DateTime date)
        {
            return new DateTime(date.Year, date.Month, 1);
        }

        public static DateTime EndOfMonth(this DateTime date)
        {
            DateTime first = new DateTime(date.Year, date.Month, 1);
            return first.AddMonths(1).AddDays(-1);
        }

        public static DateTime StartOfYear(this DateTime date)
        {
            return new DateTime(date.Year, 1, 1);
        }

        public static DateTime EndOfYear(this DateTime date)
        {
            DateTime first = StartOfYear(date);
            return first.AddYears(1).AddDays(-1);
        }

    }
}

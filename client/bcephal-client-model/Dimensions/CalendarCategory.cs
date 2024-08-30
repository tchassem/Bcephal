using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dimensions
{
    public class CalendarCategory : Persistent
    {

        public string Name { get; set; }

        public int Position { get; set; }

        public ListChangeHandler<CalendarDay> DayListChangeHandler { get; set; }

        public ListChangeHandler<CalendarCategory> Children { get; set; }

        public CalendarCategory()
        {
            DayListChangeHandler = new ListChangeHandler<CalendarDay>();
        }


        public void AddDay(CalendarDay day, bool sort = true)
        {
            day.Position = DayListChangeHandler.Items.Count;
            DayListChangeHandler.AddNew(day, sort);
        }

        public void UpdateDay(CalendarDay day, bool sort = true)
        {
            DayListChangeHandler.AddUpdated(day, sort);
        }

        public void InsertDay(int position, CalendarDay day)
        {
            day.Position = position;
            foreach (CalendarDay child in DayListChangeHandler.Items)
            {
                if (child.Position >= day.Position)
                {
                    child.Position = child.Position + 1;
                    DayListChangeHandler.AddUpdated(child, false);
                }
            }
            DayListChangeHandler.AddNew(day);
        }


        public void DeleteOrForgetDay(CalendarDay day)
        {
            if (day.Id.HasValue)
            {
                DeleteDay(day);
            }
            else
            {
                ForgetDay(day);
            }
        }

        public void DeleteDay(CalendarDay day)
        {
            DayListChangeHandler.AddDeleted(day);
            foreach (CalendarDay child in DayListChangeHandler.Items)
            {
                if (child.Position > day.Position)
                {
                    child.Position = child.Position - 1;
                    DayListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetDay(CalendarDay day)
        {
            DayListChangeHandler.forget(day);
            foreach (CalendarDay child in DayListChangeHandler.Items)
            {
                if (child.Position > day.Position)
                {
                    child.Position = child.Position - 1;
                    DayListChangeHandler.AddUpdated(child, false);
                }
            }
        }





        public void AddChild(CalendarCategory category, bool sort = true)
        {
            category.Position = Children.Items.Count;
            Children.AddNew(category, sort);
        }

        public void UpdateChild(CalendarCategory category, bool sort = true)
        {
            Children.AddUpdated(category, sort);
        }

        public void InsertChild(int position, CalendarCategory category)
        {
            category.Position = position;
            foreach (CalendarCategory child in Children.Items)
            {
                if (child.Position >= category.Position)
                {
                    child.Position = child.Position + 1;
                    Children.AddUpdated(child, false);
                }
            }
            Children.AddNew(category);
        }


        public void DeleteOrForgetChild(CalendarCategory category)
        {
            if (category.Id.HasValue)
            {
                DeleteChild(category);
            }
            else
            {
                ForgetChild(category);
            }
        }

        public void DeleteChild(CalendarCategory category)
        {
            Children.AddDeleted(category);
            foreach (CalendarCategory child in Children.Items)
            {
                if (child.Position > category.Position)
                {
                    child.Position = child.Position - 1;
                    Children.AddUpdated(child, false);
                }
            }
        }

        public void ForgetChild(CalendarCategory category)
        {
            Children.forget(category);
            foreach (CalendarCategory child in Children.Items)
            {
                if (child.Position > category.Position)
                {
                    child.Position = child.Position - 1;
                    Children.AddUpdated(child, false);
                }
            }
        }


        public CalendarCategory GetCategoryByName(string name, CalendarCategory excluded)
        {
            foreach (CalendarCategory category in Children.Items)
            {
                if (category.Name.Equals(name, StringComparison.OrdinalIgnoreCase) && category != excluded)
                {
                    return category;
                }
            }
            return null;
        }


        public override string ToString()
        {
            return this.Name != null ? this.Name : base.ToString();
        }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is CalendarCategory)) return 1;
            int c = this.Position.CompareTo(((CalendarCategory)obj).Position);
            if (c != 0) return c;
            if (this.Name != null) return this.Name.CompareTo(((CalendarCategory)obj).Name);
            return 1;
        }

    }
}


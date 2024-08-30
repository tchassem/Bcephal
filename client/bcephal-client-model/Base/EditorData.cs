using Bcephal.Models.Dimensions;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

namespace Bcephal.Models.Base
{
    public class EditorData<T>
    {

        //public int? clientOid { get; set; }

        public T Item { get; set; }

        public ObservableCollection<Model> Models { get; set; }

        public ObservableCollection<Measure> Measures { get; set; }

        public ObservableCollection<Period> Periods { get; set; }

        public ObservableCollection<Nameable> CalendarCategories { get; set; }


        public ObservableCollection<Nameable> Spots { get; set; }

    }
}

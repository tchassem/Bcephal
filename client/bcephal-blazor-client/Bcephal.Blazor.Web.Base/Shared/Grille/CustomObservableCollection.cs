using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Grille
{
   public  class CustomObservableCollection<P> : /*ObservableCollection<P>*/BindingList<P>
    {
        private Func<Action<P>,Task> HttpRequestFunc { get; set; }

        private event Action NotificationHanler;


        public CustomObservableCollection(Func<Action<P>, Task> httpRequestFunc, Action notificationHanler) : base()
        {
            HttpRequestFunc = httpRequestFunc;
            NotificationHanler += notificationHanler;
            Load();
        }

        public void notify() => NotificationHanler.Invoke();

        protected override void OnListChanged(ListChangedEventArgs e)
        {
            switch (e.ListChangedType)
            {
                case ListChangedType.ItemAdded:
                    //e.NewItems.Cast<P>().ToList().ForEach(OnAdded);
                    notify();
                    break;
                case ListChangedType.ItemDeleted:
                    //e.OldItems.Cast<P>().ToList().ForEach(OnRemoved);
                    notify();
                    break;
                case ListChangedType.ItemMoved:
                    // TODO: Handle this case
                    break;
                case ListChangedType.Reset:
                    //_count.Clear();
                    //this.ToList().ForEach(OnAdded);
                    notify();
                    break;
            }
            base.OnListChanged(e);
        }

        //protected override void OnCollectionChanged(NotifyCollectionChangedEventArgs e)
        //{
        //    switch (e.Action)
        //    {
        //        case NotifyCollectionChangedAction.Add:
        //            //e.NewItems.Cast<P>().ToList().ForEach(OnAdded);
        //            notify();
        //            break;
        //        case NotifyCollectionChangedAction.Remove:
        //            //e.OldItems.Cast<P>().ToList().ForEach(OnRemoved);
        //            notify();
        //            break;
        //        case NotifyCollectionChangedAction.Replace:
        //            // TODO: Handle this case
        //            break;
        //        case NotifyCollectionChangedAction.Reset:
        //            //_count.Clear();
        //            //this.ToList().ForEach(OnAdded);
        //            notify();
        //            break;
        //    }
        //    base.OnCollectionChanged(e);
        //}

        private void OnAdded(P o)
        {
            //_count[o.GetType()] += 1;
        }

        private void OnRemoved(P o)
        {
            //_count[o.GetType()] -= 1;
        }

        public async void Load()
        {
          await  HttpRequestFunc?.Invoke(Add);
        }
    }
}

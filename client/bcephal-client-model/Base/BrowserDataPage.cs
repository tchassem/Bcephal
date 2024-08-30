using Bcephal.Models.Billing.Invoices;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

namespace Bcephal.Models.Base
{
    public class BrowserDataPage<T>
    {

        public int PageSize { get; set; }

        public int PageFirstItem { get; set; }

        public int PageLastItem { get; set; }

        public int TotalItemCount { get; set; }

        public int PageCount { get; set; }

        public int CurrentPage { get; set; }

        public ObservableCollection<T> Items { get; set; }

        public BrowserDataPage()
        {
            Items = new ObservableCollection<T>();
        }

        public static implicit operator BrowserDataPage<T>(BrowserDataPage<BillingTemplateBrowserData> v)
        {
            throw new NotImplementedException();
        }
    }
}

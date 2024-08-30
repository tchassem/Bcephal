using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base.Accounting;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Accounting.Services
{
    public class BookingModelLogService: Service<BookingModelLog, BookingModelLogBrowserData>
    {
        public BookingModelLogService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {

            ResourcePath = "accounting/";

        }
    
    }
}

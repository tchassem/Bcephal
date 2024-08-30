using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component.Splitter
{
    public static class SplitterCJsInterop
    {
        internal static ValueTask<bool> SetPointerCapture(IJSRuntime jsRuntime, string elementID, long pointerID)
        {
            return jsRuntime.InvokeAsync<bool>("SplitterCJsFunctions.setPCapture", elementID, pointerID);
        }
        internal static ValueTask<bool> releasePointerCapture(IJSRuntime jsRuntime, string elementID, long pointerID)
        {
            return jsRuntime.InvokeAsync<bool>("SplitterCJsFunctions.releasePCapture", elementID, pointerID);
        }
        internal static ValueTask<BoundingClientRect> roundingClientRect(IJSRuntime jsRuntime, string elementID, ElementReference ElementReference)
        {
            return jsRuntime.InvokeAsync<BoundingClientRect>("SplitterCJsFunctions.roundingClientRect", elementID, ElementReference);
        }
        internal static ValueTask<BoundingClientRect> roundingClientRect2(IJSRuntime jsRuntime, string elementID)
        {
            return jsRuntime.InvokeAsync<BoundingClientRect>("SplitterCJsFunctions.roundingClientRect2", elementID);
        }
        public static ValueTask<BoundingClientRect> RoundingClientRectDashboardItem(IJSRuntime jsRuntime, string elementID)
        {
            return jsRuntime.InvokeAsync<BoundingClientRect>("SplitterCJsFunctions.roundingClientRectDashboardItem", elementID);
        }
        internal static ValueTask<BoundingClientRect> roundingClientRect3(IJSRuntime jsRuntime, string elementID)
        {
            return jsRuntime.InvokeAsync<BoundingClientRect>("SplitterCJsFunctions.roundingClientRect3", elementID);
        }

        internal static ValueTask<BoundingClientRect> DimansionClientRect(IJSRuntime jsRuntime, string elementID)
        {
            return jsRuntime.InvokeAsync<BoundingClientRect>("SplitterCJsFunctions.DimansionClientRect", elementID);
        }

        internal static ValueTask GetId(IJSRuntime jsRuntime, object elementID, object dotNet, string openCallBack)
        {
            return jsRuntime.InvokeVoidAsync("GetIdObjectReference", elementID, dotNet, openCallBack);
        }
    }
}

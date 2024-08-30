using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Utils
{
    public static class JsInterop
    {
        public static async void SetFocus(IJSRuntime jsRuntime, string elementID )
        {
            await jsRuntime.InvokeVoidAsync("bcephal.focusById", elementID );
        }

        internal static async void LogMessage(IJSRuntime jsRuntime, string message)
        {
            await jsRuntime.InvokeVoidAsync("console.log", message);
        }

        internal static async Task<bool> CallConfirmationDialog(IJSRuntime jsRuntime,string message) 
        {
            bool IsConfirmed = false;
            if (jsRuntime != null)
            {
                IsConfirmed = await jsRuntime.InvokeAsync<bool>("ConfirmationDialog.confirmDelete", message);
            }
            return IsConfirmed;
          
        }

        internal static async Task ClickTrigger(IJSRuntime jsRuntime, ElementReference Elt)
        {
          await jsRuntime.InvokeAsync<object>("ClickTriggerFunction.ClickTrigger", Elt);
        }

        internal static ValueTask SetEventJs(IJSRuntime jSRuntime, object element, object dotNet, string callbackName1, string callbackName2, string eventJsName)
        {
            return jSRuntime.InvokeVoidAsync("EnterKeyEvent", element, dotNet, callbackName1, callbackName2, eventJsName);
        }
        internal static ValueTask SetEventJsArgv(IJSRuntime jSRuntime, object element, object dotNet, string callbackName, string eventJsName, bool isArgv = false)
        {
            return jSRuntime.InvokeVoidAsync("EnterKeyEventArg", element, dotNet, callbackName, eventJsName, isArgv);
        }
        internal static ValueTask SetFocusById(IJSRuntime jsRuntime, string elementID)
        {
            return jsRuntime.InvokeVoidAsync("bcephal.focusById", elementID);
        }

    }
}

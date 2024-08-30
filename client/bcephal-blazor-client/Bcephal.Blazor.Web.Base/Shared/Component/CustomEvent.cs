using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    [EventHandler("oncustompaste", typeof(CustomPasteEventArgs), enableStopPropagation: true, enablePreventDefault: true)]
    public static class EventHandlers
    {
        // This static class doesn't need to contain any members. It's just a place where we can put
        // [EventHandler] attributes to configure event types on the Razor compiler. This affects the
        // compiler output as well as code completions in the editor.
    }

    public class CustomPasteEventArgs : EventArgs
    {
        // Data for these properties will be supplied by custom JavaScript logic
        public DateTime EventTimestamp { get; set; }
        public string PastedData { get; set; }
    }
    class CustomEvent
    {
    }
}

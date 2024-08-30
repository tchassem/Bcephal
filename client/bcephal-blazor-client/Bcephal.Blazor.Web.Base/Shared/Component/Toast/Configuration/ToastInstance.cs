using System;

namespace Bcephal.Blazor.Web.Base.Shared.Component.Toast
{
    internal class ToastInstance
    {
        public Guid Id { get; set; }
        public DateTime TimeStamp { get; set; }
        public ToastSettings ToastSettings { get; set; }
    }
}

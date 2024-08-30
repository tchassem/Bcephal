using Bcephal.Blazor.Web.Base.Services;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;


namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class RenameComponent<B> : ComponentBase, IDisposable 
    {
        [CascadingParameter]
        public Error Error { get; set; }
        [Inject]
        private AppState AppState{ get; set; }

        [Parameter]
        public Action<B, string> RenameCallback { get; set; }

        [Parameter]
        public B Item { get; set; }
        
        [Parameter]
        public bool RenameVisible { get; set; } = false;

        [Parameter]
        public EventCallback<bool> RenameVisibleChanged { get; set; }

        DxPopup DxPopupRef;

        [Parameter]
        public string IncomingName { get; set; }

        public string Name 
        {
            get 
            {
                return IncomingName;
            } 
            set 
            {
                IncomingName = value;
            }
        }

        public void Dispose()
        {
            GC.SuppressFinalize(DxPopupRef);
            GC.SuppressFinalize(this);
        }

        public void Close()
        {
            RenameVisible = false;
            RenameVisibleChanged.InvokeAsync(RenameVisible);
            Dispose();
        }

        public void Rename()
        {
            RenameVisible = false;
            RenameCallback?.Invoke(Item, IncomingName);
            RenameVisibleChanged.InvokeAsync(RenameVisible);
            Dispose();
        }
    }
}

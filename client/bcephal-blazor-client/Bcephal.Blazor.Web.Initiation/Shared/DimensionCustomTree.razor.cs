using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Initiation.Shared
{
    public partial class DimensionCustomTree<T> where T : Nameable
    {
        public DxTreeView treeView;

        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public ObservableCollection<T> DimensionDataSource { get; set; }

        [Parameter]
        public Func<MouseEventArgs, T, Task> ItemHandler { get; set; }

        [Parameter]
        public Func<MouseEventArgs, T, Task> SelectHandler { get; set; }

        [Parameter]
        public Func<ClipboardEventArgs, Task> ClipboardHandler { get; set; }

        protected async void OnContextMenu(MouseEventArgs e, T item)
        {
            await ItemHandler?.Invoke(e, item);
        }

        protected async void SelectionChanged(TreeViewNodeEventArgs e)
        {
            try
            {
                T item = (T)e.NodeInfo.DataItem;
                if (item != null)
                {
                    await SelectHandler?.Invoke(new MouseEventArgs(), item);
                }
            }
            catch (NullReferenceException ne)
            {

            }
            catch (Exception)
            {

            }
        }

        protected void ClipboardPressed(ClipboardEventArgs e)
        {
            ClipboardHandler?.Invoke(e);
        }

        
        private Dictionary<string, object> GetAttribute()
        {
             Task onkeydown_(KeyboardEventArgs ee)
            {
                onkeydown(ee);
                return Task.CompletedTask;
            }
            return new() { { "onkeydown", EventCallback.Factory.Create(this, (Func<KeyboardEventArgs, Task>)onkeydown_)} };
        }

        List<string> ElementReferenceRefs { get; set; }

        private void onkeydown(KeyboardEventArgs args)
        {
            var key = args.Code; // Detecting keyCode

            // Detecting Ctrl
            var ctrl = args.CtrlKey ? args.CtrlKey : key.Equals("17");

            // If key pressed is V and if ctrl is true.
            if (key.Equals("86") && ctrl)
            {
                // print in console.
                //console.log("Ctrl+V is pressed.");
                ClipboardPressed(new ClipboardEventArgs() { Type = "paste"});
            }
            else if (key.Equals("67") && ctrl)
            {

                // If key pressed is C and if ctrl is true.
                // print in console.
                //console.log("Ctrl+C is pressed.");
            }
        }
    }
}


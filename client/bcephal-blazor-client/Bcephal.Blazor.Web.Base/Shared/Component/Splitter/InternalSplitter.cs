using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component.Splitter
{
    internal class InternalSplitter
    {
        internal Action PropertyChanged;

        internal SplitterSettings SplitterSettings { get; set; }


        internal int PreviousPosition { get; set; } = 0;
        internal int PreviousPosition2 { get; set; } = 0;

        internal int Position { get; set; } = 0;

        internal int Step { get; set; } = 0;

        internal void InvokePropertyChanged()
        {
            PropertyChanged?.Invoke();
        }
    }
}

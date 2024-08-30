using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Loaders
{
    public class FileLoaderEditorData : EditorData<FileLoader>
    {

        public ObservableCollection<BrowserData> Grids { get; set; }

        public ObservableCollection<BrowserData> Tables { get; set; }

        public ObservableCollection<Nameable> Routines { get; set; }

    }
}

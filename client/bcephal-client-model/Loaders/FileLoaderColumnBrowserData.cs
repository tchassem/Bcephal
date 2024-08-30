using Bcephal.Models.Base;
using Bcephal.Models.Grids;


namespace Bcephal.Models.Loaders
{
    public class FileLoaderColumnBrowserData : BrowserData
    {
        public string Type { get; set; }

        public GrilleColumn GrilleColumn { get; set; }

        public string FileColumn { get; set; }

        public int Position { get; set; }

    }
}

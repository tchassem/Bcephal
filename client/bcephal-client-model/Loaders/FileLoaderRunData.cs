using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Loaders
{
    public class FileLoaderRunData
    {

		public long? Id { get; set; }

        public FileLoader Loader { get; set; }

        public string Repository { get; set; }

        public ObservableCollection<string> Files { get; set; }

        public string Mode { get; set; }

        [JsonIgnore]
        public RunModes RunModes
        {
            get { return !string.IsNullOrEmpty(Mode) ? RunModes.GetByCode(Mode) : null; }
            set { this.Mode = value != null ? value.code : null; }
        }

        public FileLoaderRunData()
        {
            this.RunModes = RunModes.M;
            this.Files = new ObservableCollection<string>();
        }

    }
}

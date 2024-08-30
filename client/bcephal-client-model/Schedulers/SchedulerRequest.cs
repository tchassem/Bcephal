using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Schedulers
{
    public class SchedulerRequest
    {
        public string ProjectCode { get; set; }

        public ObservableCollection<long?> ObjectIds { get; set; }

        public string ObjectType { get; set; }


        public SchedulerRequest()
        {
            this.ObjectIds = new ObservableCollection<long?>();
        }
    }
}

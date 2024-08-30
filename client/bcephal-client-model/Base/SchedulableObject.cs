using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Base
{
    public class SchedulableObject : MainObject
    {

        public bool Active { get; set; }

        public bool Scheduled { get; set; }

        public string CronExpression {
            get; 
            set;
        }

        [JsonIgnore]
        public bool InitStatus { get; set; }

        [JsonIgnore]
        public bool Modified { get; set; }

        [JsonIgnore]
        public bool CanStop { get => InitStatus && (!Active || !Scheduled || String.IsNullOrEmpty(CronExpression)); }

        [JsonIgnore]
        public bool CanRestart { get => Active && Scheduled && !String.IsNullOrEmpty(CronExpression); }

        public void Init()
        {
            InitStatus = CanRestart;
            Modified = false;
        }

    }
}

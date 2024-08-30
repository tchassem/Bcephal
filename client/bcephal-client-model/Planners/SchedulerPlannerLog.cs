﻿using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Planners
{
    public class SchedulerPlannerLog : Persistent
    {
        public string SchedulerName { get; set; }

        public string Message { get; set; }

        public string Status { get; set; }

        [JsonIgnore]
        public RunStatus RunStatus
        {
            get
            {
                return string.IsNullOrEmpty(Status) ? RunStatus.ENDED : RunStatus.GetByCode(Status);
            }
            set
            {
                this.Status = value != null ? value.getCode() : null;
            }
        }

        public string Mode { get; set; }

        [JsonIgnore]
        public RunModes RunModes
        {
            get
            {
                return string.IsNullOrEmpty(Mode) ? RunModes.M : RunModes.GetByCode(Mode);
            }
            set
            {
                this.Mode = value != null ? value.getCode() : null;
            }
        }

        public string Username { get; set; }

        public int Steps { get; set; }

        public int RunnedStep { get; set; }

        public long CurrentItemId { get; set; }

        public DateTime? StartDate { get; set; }

        public DateTime? EndDate { get; set; }

        public string ProjectCode { get; set; }
    }
}

using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Routines
{
    public class RoutineExecutor : Persistent
    {

        public long? RoutineId { get; set; }

        public int Position { get; set; }

        public string Type { get; set; }

        [JsonIgnore]
        public RoutineExecutorType RoutineExecutorType
        {
            get
            {
                return string.IsNullOrEmpty(Type) ? RoutineExecutorType.POST : RoutineExecutorType.GetByCode(Type);
            }
            set
            {
                this.Type = value != null ? value.getCode() : null;
            }
        }


        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is RoutineExecutor)) return 1;
            if (obj == this) return 0;
            return this.Position.CompareTo(((RoutineExecutor)obj).Position);
        }

    }
}

using Bcephal.Models.Base;
using Bcephal.Models.Security;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Bcephal.Models.Profiles
{
    public class Right : Persistent
    {

        public string Functionality { get; set; }

        public string Level { get; set; }

        [JsonIgnore] public RightLevel RightLevel
        {
            get { return RightLevel.GetByCode(this.Level); }
            set { this.Level = value != null ? value.code : null; }
        }

        public long? ObjectId { get; set; }

        public string ObjectType { get; set; }
                

        public Right() { }

        public Right(String function)
        {
            this.Functionality = function;
        }

        public Right(String function, RightLevel rightLevel) : this(function)
        {
            this.RightLevel = rightLevel;
        }

        public override string ToString()
        {
            return Functionality;
        }

        /// <summary>
        /// compare
        /// </summary>
        /// <param name="obj"></param>
        /// <returns></returns>
        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is Right)) return 1;
            if (!String.IsNullOrWhiteSpace(this.Functionality))
                return this.Functionality.CompareTo(((Right)obj).Functionality);
            return 1;
        }
    }
}

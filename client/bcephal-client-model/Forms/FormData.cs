using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class FormData : Persistent
    {

        public long? ModelOid { set; get; }

        public long? FormModelId { set; get; }

        public long? GridId { set; get; }

        public int Position { set; get; }

        public bool Validated { set; get; }

        public Dictionary<long?, FormDataValue> Datas { set; get; }

        public Dictionary<long?, ListChangeHandler<FormData>> SubGridDatas { set; get; }

        public FormData()
        {
            this.Datas = new Dictionary<long?, FormDataValue>(0);
            this.SubGridDatas = new Dictionary<long?, ListChangeHandler<FormData>>(0);
            this.Validated = false;
        }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is FormData)) return 1;
            if (this == obj) return 0;
            return this.Position.CompareTo(((FormData)obj).Position);
        }


    }
}

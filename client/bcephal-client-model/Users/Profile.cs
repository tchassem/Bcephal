using Bcephal.Models.Base;
using Bcephal.Models.Clients;
using Newtonsoft.Json;

namespace Bcephal.Models.Users
{
  public  class Profile : MainObject
    {
        public string Code { get; set; }
        public string Description { get; set; }
        public string Client { get; set; }
        public string Type { get; set; }
        [JsonIgnore]
        public UserType UserType
        {
            get { return UserType.GetByCode(this.Type); }
            set { this.Type = value != null ? value.code : null; }
        }
    }
}

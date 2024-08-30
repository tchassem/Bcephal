using Bcephal.Models.Base;
using Bcephal.Models.Clients;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;

namespace Bcephal.Models.Users
{
  public  class User : MainObject
	{
        public Dictionary<string, List<string>> attributes { get; set; }
		public List<string> requiredActions { get; set; }
		public List<string> realmRoles { get; set; }
		public Dictionary<string, List<string>> clientRoles { get; set; }
		public List<String> groups;
		public Dictionary<string, bool> access { get; set; }
		public string UserId { get; set; }
		public string FirstName { get; set; }
		public string username { get; set; }
		public string lastName { get; set; }
		public bool Enabled { get; set; } = true;
		public bool emailVerified { get; set; }
	    public string password { get; set; }
		public string email { get; set; }
		public string DefaultLanguage { get; set; }
		public string Type { get; set; }
		[JsonIgnore]
		public UserType UserType
		{
			get { return UserType.GetByCode(this.Type); }
			set { this.Type = value != null ? value.code : null; }
		}
	}
}

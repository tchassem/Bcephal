
using Bcephal.Models.Base;
using Newtonsoft.Json;

namespace Bcephal.Models.Clients
{
    public class Client : MainObject
    {

		public string Code { get; set; }
		public string FirstName { get; set; }		
		public string ClientId { get; set; }
		public string Description { get; set; }
		public bool Enabled { get; set; } = true;
		public string Secret { get; set; }
		public bool DefaultClient { get; set; }
		public string DefaultLanguage { get; set; }
		public int MaxUser { get; set; }
		public string OwnerUser { get; set; }
		public Address Address { get; set; }
		public string Nature { get; set; }	
		[JsonIgnore]
		public ClientNature ClientNature
		{
			get { return ClientNature.GetByCode(this.Nature); }
			set { this.Nature = value != null ? value.code : null; }
		}

		public string Type { get; set; }
		[JsonIgnore]
		public ClientType ClientType
		{
			get { return ClientType.GetByCode(this.Type); }
			set { this.Type = value != null ? value.code : null; }
		}

		public string Status { get; set; }
		[JsonIgnore]
		public ClientStatus ClientStatus
		{
			get { return ClientStatus.GetByCode(this.Status); }
			set { this.Status = value != null ? value.code : null; }
		}


		public bool DefaultSubscription { get; set; }


		public ListChangeHandler<ClientFunctionality> FunctionalityListChangeHandler { get; set; }

		public ListChangeHandler<Nameable> ProfileListChangeHandler { get; set; }


		public Client()
		{
			this.FunctionalityListChangeHandler = new ListChangeHandler<ClientFunctionality>();
			this.ProfileListChangeHandler = new ListChangeHandler<Nameable>();
		}


        public void AddProfile(Nameable Profile, bool sort = true)
        {
            ProfileListChangeHandler.AddNew(Profile, sort);
        }

        public void UpdateProfile(Nameable Profile, bool sort = true)
        {
            ProfileListChangeHandler.AddUpdated(Profile, sort);
        }

        public void DeleteOrForgetProfile(Nameable Profile)
        {
            if (Profile.IsPersistent)
            {
                DeleteProfile(Profile);
            }
            else
            {
                ForgetProfile(Profile);
            }
        }

        public void DeleteProfile(Nameable Profile)
        {
            ProfileListChangeHandler.AddDeleted(Profile);
        }

        public void ForgetProfile(Nameable Profile)
        {
            ProfileListChangeHandler.forget(Profile);
        }


        public Nameable GetProfileById(long? id)
        {
            foreach (Nameable Profile in ProfileListChangeHandler.Items)
            {
                if (Profile.Id.Equals(id))
                {
                    return Profile;
                }
            }
            return null;
        }

        public void AddClientFunctionality(ClientFunctionality ClientFunctionality, bool sort = true)
        {
            FunctionalityListChangeHandler.AddNew(ClientFunctionality, sort);
        }

        public void UpdateClientFunctionality(ClientFunctionality ClientFunctionality, bool sort = true)
        {
            FunctionalityListChangeHandler.AddUpdated(ClientFunctionality, sort);
        }

        public void DeleteOrForgetClientFunctionality(ClientFunctionality ClientFunctionality)
        {
            if (ClientFunctionality.IsPersistent)
            {
                DeleteClientFunctionality(ClientFunctionality);
            }
            else
            {
                ForgetClientFunctionality(ClientFunctionality);
            }
        }

        public void DeleteClientFunctionality(ClientFunctionality ClientFunctionality)
        {
            FunctionalityListChangeHandler.AddDeleted(ClientFunctionality);
        }

        public void ForgetClientFunctionality(ClientFunctionality ClientFunctionality)
        {
            FunctionalityListChangeHandler.forget(ClientFunctionality);
        }


    }
}

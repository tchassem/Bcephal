using Bcephal.Models.Base;
using Bcephal.Models.Clients;
using Newtonsoft.Json;

namespace Bcephal.Models.Profiles
{
  public  class Profile : MainObject
    {
        public object FunctionalityListChangeHandler;

        public string Description { get; set; }
		public string Type { get; set; }

		[JsonIgnore]
		public UserType ProfileType
		{
			get { return UserType.GetByCode(this.Type); }
			set { this.Type = value != null ? value.code : null; }
		}

		public ListChangeHandler<Nameable> UserListChangeHandler { get; set; }

		public ListChangeHandler<Right> RightListChangeHandler { get; set; }

		public Profile()
		{
			this.UserListChangeHandler = new ListChangeHandler<Nameable>();
			this.RightListChangeHandler = new ListChangeHandler<Right>();
		}


        public void AddUser(Nameable user, bool sort = true)
        {
            UserListChangeHandler.AddNew(user, sort);
        }

        public void UpdateUser(Nameable User, bool sort = true)
        {
            UserListChangeHandler.AddUpdated(User, sort);
        }

        public void DeleteOrForgetUser(Nameable User)
        {
            if (User.IsPersistent)
            {
                DeleteUser(User);
            }
            else
            {
                ForgetUser(User);
            }
        }

        public void DeleteUser(Nameable User)
        {
            UserListChangeHandler.AddDeleted(User);
        }

        public void ForgetUser(Nameable User)
        {
            UserListChangeHandler.forget(User);
        }


        public Nameable GetUserById(long? id)
        {
            foreach (Nameable User in UserListChangeHandler.Items)
            {
                if (User.Id.Equals(id))
                {
                    return User;
                }
            }
            return null;
        }

        public void AddRight(Right Right, bool sort = true)
        {
            RightListChangeHandler.AddNew(Right, sort);
        }

        public void UpdateRight(Right Right, bool sort = true)
        {
            RightListChangeHandler.AddUpdated(Right, sort);
        }

        public void DeleteOrForgetRight(Right Right)
        {
            if (Right.IsPersistent)
            {
                DeleteRight(Right);
            }
            else
            {
                ForgetRight(Right);
            }
        }

        public void DeleteRight(Right Right)
        {
            RightListChangeHandler.AddDeleted(Right);
        }

        public void ForgetRight(Right Right)
        {
            RightListChangeHandler.forget(Right);
        }



    }
}

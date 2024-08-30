using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Clients
{
    public class UserType
    {
        public static UserType ADMINISTRATOR = new UserType("ADMINISTRATOR", "Administrator");
        public static UserType SUPERUSER = new UserType("SUPERUSER", "Super user");
        public static UserType USER = new UserType("USER", "User");
        public static UserType GUEST = new UserType("GUEST", "Guest");


        public String label;
        public String code;

        public UserType(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public bool IsADMINISTRATOR()
        {
            return this == ADMINISTRATOR;
        }

        public bool IsSUPERUSER()
        {
            return this == SUPERUSER;
        }

        public bool IsUSER()
        {
            return this == USER;
        }

        public bool IsGUEST()
        {
            return this == GUEST;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static UserType GetByCode(string code)
        {
            if (code == null) return null;
            if (ADMINISTRATOR.code.Equals(code)) return ADMINISTRATOR;
            if (SUPERUSER.code.Equals(code)) return SUPERUSER;
            if (USER.code.Equals(code)) return USER;
            if (GUEST.code.Equals(code)) return GUEST;
            return null;
        }

        public static ObservableCollection<UserType> GetAll()
        {
            ObservableCollection<UserType> operators = new ObservableCollection<UserType>();
            operators.Add(UserType.ADMINISTRATOR);
            operators.Add(UserType.SUPERUSER);
            operators.Add(UserType.USER);
            operators.Add(UserType.GUEST);
            return operators;
        }

    }

    public static class UserTypeExtension
    {


        public static ObservableCollection<string> GetAll(this UserType userType, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            // --- operators.Add(null);
            operators.Add(Localize?.Invoke("ADMINISTRATOR"));
            operators.Add(Localize?.Invoke("SUPERUSER"));
            operators.Add(Localize?.Invoke("USER"));
            operators.Add(Localize?.Invoke("GUEST"));
            return operators;
        }

        public static string GetText(this UserType userType, Func<string, string> Localize)
        {
            if (UserType.ADMINISTRATOR.code.Equals(userType.code))
            {
                return Localize?.Invoke("ADMINISTRATOR");
            }
            if (UserType.SUPERUSER.code.Equals(userType.code))
            {
                return Localize?.Invoke("SUPERUSER");
            }
            if (UserType.USER.code.Equals(userType.code))
            {
                return Localize?.Invoke("USER");
            }
            if (UserType.GUEST.code.Equals(userType.code))
            {
                return Localize?.Invoke("GUEST");
            }
            return null;
        }

        public static UserType GetSchedulerOption(this UserType userType, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("ADMINISTRATOR")))
                {
                    return UserType.ADMINISTRATOR;
                }
                if (text.Equals(Localize?.Invoke("SUPERUSER")))
                {
                    return UserType.SUPERUSER;
                }
                if (text.Equals(Localize?.Invoke("USER")))
                {
                    return UserType.USER;
                }
                if (text.Equals(Localize?.Invoke("GUEST")))
                {
                    return UserType.GUEST;
                }
            }
            return null;
        }
    }

    }

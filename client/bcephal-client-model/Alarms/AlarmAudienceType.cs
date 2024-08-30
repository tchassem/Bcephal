using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Alarms
{
    [JsonConverter(typeof(StringEnumConverter))]
    public enum AlarmAudienceType
    {
        PROFILE,
        USER,
        FREE
    }
    public static class AlarmAudienceTypeExtensionMethods
    {
        public static ObservableCollection<AlarmAudienceType> GetAll()
        {
            ObservableCollection<AlarmAudienceType> audiences = new ObservableCollection<AlarmAudienceType>();
            audiences.Add(AlarmAudienceType.FREE);
            audiences.Add(AlarmAudienceType.PROFILE);
            audiences.Add(AlarmAudienceType.USER);
            return audiences;
        }

        public static bool IsPROFILE(this AlarmAudienceType audience)
        {
            return audience == AlarmAudienceType.PROFILE;
        }

        public static bool IsUSER(this AlarmAudienceType audience)
        {
            return audience == AlarmAudienceType.USER;
        }

        public static bool IsFREE(this AlarmAudienceType audience)
        {
            return audience == AlarmAudienceType.FREE;
        }

        public static ObservableCollection<string> GetAll(this AlarmAudienceType? audience, Func<string, string> Localize)
        {
            ObservableCollection<string> audiences = new ObservableCollection<string>
            {
                null,
                Localize?.Invoke("Profile"),
                Localize?.Invoke("User"),
                Localize?.Invoke("FREE")
            };
            return audiences;
        }

        public static ObservableCollection<string> GetAllAudiences(this AlarmAudienceType? audience, Func<string, string> Localize)
        {
            ObservableCollection<string> audiences = new ObservableCollection<string>
            {
                null,
                Localize?.Invoke("Profile"),
                Localize?.Invoke("User"),
                Localize?.Invoke("FREE")
            };
            return audiences;
        }

        public static string GetText(this AlarmAudienceType audience, Func<string, string> Localize)
        {
            if (AlarmAudienceType.PROFILE.Equals(audience))
            {
                return Localize?.Invoke("Profile");
            }
            if (AlarmAudienceType.USER.Equals(audience))
            {
                return Localize?.Invoke("User");
            }
            if (AlarmAudienceType.FREE.Equals(audience))
            {
                return Localize?.Invoke("FREE");
            }
            return null;
        }

        public static AlarmAudienceType GetAlarmAudienceType(this AlarmAudienceType audience, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("Profile")))
                {
                    return AlarmAudienceType.PROFILE;
                }
                if (text.Equals(Localize?.Invoke("User")))
                {
                    return AlarmAudienceType.USER;
                }
                if (text.Equals(Localize?.Invoke("FREE")))
                {
                    return AlarmAudienceType.FREE;
                }
            }
            return AlarmAudienceType.PROFILE;
        }
    }
}

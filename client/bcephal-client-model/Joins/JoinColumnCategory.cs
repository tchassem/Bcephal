using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System;
using System.Collections.ObjectModel;

namespace Bcephal.Models.Joins
{
    [JsonConverter(typeof(StringEnumConverter))]
    public enum JoinColumnCategory
    {
        CUSTOM,
        STANDARD
    }
    public static class JoinColumnCategoryExtensionMethods
    {

        public static ObservableCollection<JoinColumnCategory> GetAll(this JoinColumnCategory joinColumnCategory)
        {
            ObservableCollection<JoinColumnCategory> joinColumnCategories = new ObservableCollection<JoinColumnCategory>
            {
                JoinColumnCategory.CUSTOM,
                JoinColumnCategory.STANDARD
            };
            return joinColumnCategories;
        }
     
        public static bool IsCustom(this JoinColumnCategory periodOperator)
        {
            return periodOperator == JoinColumnCategory.CUSTOM;
        }

        public static bool IsStandard(this JoinColumnCategory periodOperator)
        {
            return periodOperator == JoinColumnCategory.STANDARD;
        }

        public static ObservableCollection<string> GetAll(this JoinColumnCategory joinColumnCategory, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>
            {
                null,
                Localize?.Invoke("CUSTOM"),
                Localize?.Invoke("STANDARD")
            };
            return operators;
        }

        public static string GetText(this JoinColumnCategory joinColumnCategory, Func<string, string> Localize)
        {
            if (JoinColumnCategory.CUSTOM.Equals(joinColumnCategory))
            {
                return Localize?.Invoke("CUSTOM");
            }
            if (JoinColumnCategory.STANDARD.Equals(joinColumnCategory))
            {
                return Localize?.Invoke("STANDARD");
            }
            return null;
        }


        public static JoinColumnCategory GetJoinColumnCategory(this JoinColumnCategory joinColumnCategory, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("CUSTOM")))
                {
                    return JoinColumnCategory.CUSTOM;
                }
                if (text.Equals(Localize?.Invoke("STANDARD")))
                {
                    return JoinColumnCategory.STANDARD;
                }

            }
            return JoinColumnCategory.STANDARD;
        }

    }
}

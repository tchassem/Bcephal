//using Bcephal.Blazor.Web.Base.Services;
//using Microsoft.AspNetCore.Components;
//using System;
//using System.Collections.Generic;
//using System.Globalization;
//using System.Linq;
//using System.Text;
//using System.Text.RegularExpressions;
//using System.Threading;
//using System.Threading.Tasks;

//namespace Bcephal.Blazor.Web.Setting.Pages
//{
//    public static class LocalizeHelper
//    {

//        public static string GetTitle(string key, AppState AppState)
//        {
//            if (string.IsNullOrWhiteSpace(key))
//            {
//                return key;
//            }
//            string code = key;
//            code = Regex.Replace(code, @"\s+", "");
//            if (!code.Contains('.'))
//            {
//                if (!string.IsNullOrWhiteSpace(AppState.GetString(key)))
//                {
//                    return AppState.GetString(key);
//                }
//                else
//                {
//                    if (!string.IsNullOrWhiteSpace(AppState.GetString(key.ToLower())))
//                    {
//                        return AppState.GetString(key.ToLower());
//                    }
//                    return AppState.GetString(Capitalize(key));
//                }
//            }

//            string code_ = AppState.GetString(code);
//            if (!string.IsNullOrWhiteSpace(code_))
//            {
//                return code_;
//            }
//            else
//            {
//                string[] str = code.Split('.');
//                string code2 = Capitalize(str[0]) + code[code.IndexOf('.')..];
//                string title_ = AppState.GetString(code2);
//                return title_;
//            }

//        }

//        private static string Capitalize(string text)
//        {
//            CultureInfo cultureInfo = Thread.CurrentThread.CurrentCulture;
//            TextInfo textInfo = cultureInfo.TextInfo;
//            return textInfo.ToTitleCase(text);
//        }



//    }
//}

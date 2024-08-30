using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Data;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared
{

    /// <summary>
    /// Helper class for common conversion
    /// </summary>
    public static class Converter
    {

        public static string DefaultHexColor = "ffffff";
        public static int DefaultIntColor = 16777215;

        /// <summary>
        /// this method convert int to hexadecimal
        /// </summary>
        /// <param name="color"></param>
        /// <returns>converted value to string</returns>
        public static string ConvertInToHex(int? color)
        {
            string hex;
            if (color.HasValue)
            {
                hex = "#" + color.Value.ToString("X");
            }
            else
            {
                hex = "#" + DefaultHexColor;
            }
            return hex;
        }


        /// <summary>
        /// this method convert hexadecimal to int
        /// </summary>
        /// <param name="hex"></param>
        /// <returns>converted value to int </returns>
        public static int ConvertHexToInt(string hex)
        {
            return hex != default ? Convert.ToInt32(hex.Substring(1), 16) : DefaultIntColor;
        }


        /// <summary>
        /// this method convert IEnumerable to ObservableCollection
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="myIEnumerable"></param>
        /// <returns>ObservableCollection</returns>
        public static ObservableCollection<T> ToObservableCollection<T>(this IEnumerable<T> myIEnumerable)
        {
            return new ObservableCollection<T>(myIEnumerable);
        }



        /// <summary>
        /// This method creates a list of limit 
        /// element for a given list
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="Source"></param>
        /// <param name="Remainder"></param>
        /// <param name="Limit"></param>
        /// <returns>List<List<T>></returns>
        public static List<List<T>> GetListOfList<T>(List<T> Source, int Remainder, int Limit)
        {
            List<List<T>> Resultat = new List<List<T>>();
            int tail = Source.Count();
            int quotient = tail / Limit;
            if (quotient == 0) Resultat.Add(Source);
            if (quotient != 0)
            {
                for (int i = 0; i < quotient; i++)
                {
                    Resultat.Add(Source.GetRange(i * Limit, Limit));

                }
                if (Remainder != 0) Resultat.Add(Source.GetRange(quotient * Limit, Remainder));
            }

            return Resultat;
        }
    }
}

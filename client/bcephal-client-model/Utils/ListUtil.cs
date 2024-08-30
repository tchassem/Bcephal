using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Utils
{
    public static class ListUtil
    {

        public static void BubbleSort(this IList list)
        {
            int tailleTotale = list.Count;

            for (int i = tailleTotale - 1; i >= 0; i--)
            {
                for (int j = 1; j <= i; j++)
                {
                    object o1 = list[j - 1];
                    object o2 = list[j];
                    if (((IComparable)o1).CompareTo(o2) > 0)
                    {
                        list.Remove(o1);
                        list.Insert(j, o1);
                    }
                }
            }
        }
    }
}

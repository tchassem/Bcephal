using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared
{
    public class Helpers
    {


        public static bool IsJsonValid(string ToTest)
        {
            try { 
                return JsonDocument.Parse(ToTest) != null; 
            }catch { 
            
            }

            return false;
        }
    }
}

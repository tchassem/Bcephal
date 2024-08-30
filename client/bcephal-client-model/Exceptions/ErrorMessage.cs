using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Exceptions
{
    public class ErrorMessage
    {

        //contains the same HTTP Status code returned by the server
        public int? status { get; set; }

        //application specific error code
        public int? code { get; set; }

        //message describing the error
        public String message { get; set; }

        //link point to page where the error message is documented
        public String link { get; set; }

        //extra information that might useful for developers
        public String developerMessage { get; set; }
               

    }
}

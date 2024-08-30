using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Exceptions
{
    public class BcephalException : Exception
    {

        //contains the same HTTP Status code returned by the server
        public int? status { get; set; }

        //application specific error code
        public int? code { get; set; }

        //link point to page where the error message is documented
        public string link { get; set; }

        //extra information that might useful for developers
        public string developerMessage { get; set; }


        public BcephalException() : base() { }

        public BcephalException(string message)
            : base(message) { }
        public BcephalException(string message, int? status)
            : base(message) {
            this.status = status;
        }

        public BcephalException(string message, Exception innerException)
            : base(message, innerException) { }


        public override string ToString()
        {
            return Message;
        }

    }

}


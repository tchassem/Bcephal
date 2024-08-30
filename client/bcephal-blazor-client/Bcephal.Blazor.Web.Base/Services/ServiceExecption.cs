using Bcephal.Models.Exceptions;
using System;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class ServiceExecption : BcephalException
    {
        public static string MessageError  { get; set; }

        public ServiceExecption() 
            : base() { }
    
        public ServiceExecption(string message) 
            : base(message) { }

        public ServiceExecption(string message, Exception innerException)
            : base(message, innerException) { }

    }
}

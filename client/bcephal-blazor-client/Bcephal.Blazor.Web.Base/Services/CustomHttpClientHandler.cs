using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.WebAssembly.Http;
using Microsoft.JSInterop;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading;
using System.Threading.Tasks;
using System;
using System.Security.Cryptography.X509Certificates;
using System.Net.Security;
using System.Net;
using System.Collections.Generic;
using Bcephal.Models.Exceptions;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class CustomHttpClientHandler : HttpClientHandler
    {

        private readonly IJSRuntime JSRuntime;
        public static string SessionId { get; set; }

        public static string TENANT_HTTP_HEADER = "Bcephal-Project";
        public static string CLIENT_HTTP_HEADER = "BC-CLIENT";
        public static string PROFILE_HTTP_HEADER = "BC-PROFILE";
        public static string CUSTOM_HTTP_HEADER_SOCKET = "http-header";

        public static string TENANT_ID { get; set; }

        public static string CLIENT_ID { get; set; }
        public static string PROFILE_ID { get; set; }

        public static string BASE_URL { get; set; }

        public CustomHttpClientHandler(IJSRuntime jSRuntime) : base()
        {
            JSRuntime = jSRuntime;
        }

        public void SetHttpsConfigClientHandler()
        {
            //ServicePointManager.ServerCertificateValidationCallback = (sender, cert, chain, sslPolicyErrors) => true;
            //ClientCertificateOptions = ClientCertificateOption.Manual;
        }

        protected override async Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken cancellationToken)
        {
            //await JSRuntime.InvokeVoidAsync("console.log", "Call CustomDelegatingHandler === request :", TENANT_ID);
            request.SetBrowserRequestCredentials(BrowserRequestCredentials.Include);
            if (!string.IsNullOrWhiteSpace(TENANT_ID) && request.Headers != null)
            {
                request.Headers.Add(TENANT_HTTP_HEADER, TENANT_ID);
               // await JSRuntime.InvokeVoidAsync("console.log", "Call CustomDelegatingHandler === set tenant I :", TENANT_ID);
            }

            if (request.Headers != null)
            {
                if (request.Headers.Contains(CLIENT_HTTP_HEADER))
                {
                    request.Headers.Remove(CLIENT_HTTP_HEADER);
                }
                request.Headers.Add(CLIENT_HTTP_HEADER, CLIENT_ID);
            }

            if (request.Headers != null)
            {
                if (request.Headers.Contains(PROFILE_HTTP_HEADER))
                {
                    request.Headers.Remove(PROFILE_HTTP_HEADER);
                }
                request.Headers.Add(PROFILE_HTTP_HEADER, PROFILE_ID);
            }

            if (!string.IsNullOrWhiteSpace(SessionId) && request.Headers != null)
            {
                request.Headers.Add("Cookie", "SESSION=" + SessionId);
                //await JSRuntime.InvokeVoidAsync("console.log", "Call CustomDelegatingHandler === set session I :", SessionId);
            }


            //await JSRuntime.InvokeVoidAsync("console.log", "Call CustomDelegatingHandler === endequest :", request);
            if (!request.Headers.Contains("Accept-Language"))
            {
                request.Headers.Add("Accept-Language", UserService.DefaultLanguage);
            }

            //await JSRuntime.InvokeVoidAsync("console.log","body request:::::",request.Content.ToString() );
            //if(request.Content != null){
            //    Console.WriteLine("console.log","body request:::::",request.Content.ToString() );
            //}
            string url = request.RequestUri.AbsoluteUri;
                //AllowAutoRedirect = false;
                //if (AllowUrlToRedirec.Contains(url))
                //{
                //    AllowAutoRedirect = true;
                //}
            return await base.SendAsync(request, cancellationToken);            
        }

        //List<string> AllowUrlToRedirec => new() { $"{BASE_URL}/signout", $"{BASE_URL}/edit-user", $"{BASE_URL}/edit-user" };


        private bool VerifyServerCertificate(object sender, X509Certificate certificate, X509Chain chain,SslPolicyErrors sslPolicyErrors)
        {
            // If the certificate is a valid, signed certificate, return true.
            if (sslPolicyErrors == SslPolicyErrors.None)
            {
                return true;
            }

            //// If there are errors in the certificate chain, look at each error to determine the cause.
            //if ((sslPolicyErrors & SslPolicyErrors.RemoteCertificateChainErrors) != 0)
            //{
            //    chain.ChainPolicy.RevocationMode = X509RevocationMode.NoCheck;

            //    // add all your extra certificate chain
            //    foreach (var rootCert in this.rootCerts)
            //    {
            //        chain.ChainPolicy.ExtraStore.Add(rootCert);
            //    }

            //    chain.ChainPolicy.VerificationFlags = X509VerificationFlags.AllowUnknownCertificateAuthority;
            //    var isValid = chain.Build((X509Certificate2)certificate);

            //    var rootCertActual = chain.ChainElements[chain.ChainElements.Count - 1].Certificate;
            //    var rootCertExpected = this.rootCerts[this.rootCerts.Count - 1];
            //    isValid = isValid && rootCertActual.RawData.SequenceEqual(rootCertExpected.RawData);

            //    return isValid;
            //}

            // In all other cases, return false.
            return false;
        }
    }
}

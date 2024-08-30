using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Messages;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Messenger.Services
{
    public class MessageLogService: Service<Persistent, MessageLogBrowserData>
    {
        public MessageLogService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "messenger/logs";
        }

        public async Task<bool> Reset(List<long> ids)
        {
            return bool.Parse(await this.ExecutePost(ResourcePath + "/reset-items", ids));

        } 
        
        public async Task<bool> Cancel(List<long> ids)
        {
            return bool.Parse(await this.ExecutePost(ResourcePath + "/cancel-items", ids));

        }

        public async Task<bool> Send(List<long> ids)
        {
            return bool.Parse(await this.ExecutePost(ResourcePath + "/send-items", ids));

            
        }

    }

    


    
    
}

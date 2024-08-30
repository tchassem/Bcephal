using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Messages;
using Microsoft.AspNetCore.Components;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Messenger.Services
{
    public class MessageService : Service<Message, Message>
    {
        public IEnumerable<Message> MessageLogs { get; set; }
        NavigationManager navigation;

        public MessageService(HttpClient RestClient, IJSRuntime JSRuntime, NavigationManager NavigationManager) : base(RestClient, JSRuntime)
        {
            ResourcePath = "messenger/";
            navigation = NavigationManager;
        }

        public async Task<BrowserDataPage<Message>> searchAll()
        {
            BrowserDataPage<Message> MyBrowserDataPage = new BrowserDataPage<Message>();
            string result = await ExecutePost(ResourcePath + "/all-messages/" + MessageType.EMAIL);
            List<Message> rows = JsonConvert.DeserializeObject<List<Message>>(result, getJsonSerializerSettings());
            MyBrowserDataPage.Items = new ObservableCollection<Message>(rows);
            MyBrowserDataPage.TotalItemCount = MyBrowserDataPage.Items.Count;

            return MyBrowserDataPage;
        }

        public BrowserDataPage<Message> Mock()
        {
            navigation.Uri.Contains("SMS");

            Message M1 = new Message
            {
                Id = 1,
                Message_ = "message1",
               // Address = new Address { Email = "Ludo@gmail.com", Phone = "+237323453", City = "", Country = "", Street = "" },
                Operator = "user",
                Type = "SMS",
                Status = "SENDED",
                Recipient = new List<Address>()  
            };
            Message M2 = new Message
            {
                Id=2,
                Message_ = "message1",
                //Address = new Address { Email = "Ludo@gmail.com", Phone = "+237323453", City = "", Country = "", Street = "" },
                Operator = "user",
                Type = "EMAIL",
                Status = "SENDED",
                Recipient = new List<Address>()
            };
            Message M3 = new Message
            {
                Id = 3,
                Message_ = "message1",
                //Address = new Address { Email = "Ludo@gmail.com", Phone = "+237323453", City = "", Country = "", Street = "" },
                Operator = "user",
                Type = "EMAIL",
                Status = "SENDED",
                Recipient = new List<Address>()              
            };
            Message M4 = new Message
            {
                Id = 3,
                Message_ = "message52454",
                //Address = new Address { Email = "Ludo@gmail.com", Phone = "+237323453", City = "", Country = "", Street = "" },
                Operator = "user",
                Type = "SMS",
                Status = "SENDED",
                Recipient = new List<Address>()
            };
            Message M5 = new Message
            {
                Id = 3,
                Message_ = "message754",
                //Address = new Address { Email = "Ludo@gmail.com", Phone = "+237323453", City = "", Country = "", Street = "" },
                Operator = "user",
                Type = "EMAIL",
                Status = "SENDED",
                Recipient = new List<Address>()
            };
            Message M6 = new Message
            {               
                Id = 3,
                Message_ = "message247",
                //Address = new Address { Email = "Ludo@gmail.com", Phone = "+237323453", City = "", Country = "", Street = "" },
                Operator = "user",
                Type = "SMS",
                Status = "SENDED",
                Recipient = new List<Address>()

            };

            // return (List<MessageLog>)(MessageLogs = new List<MessageLog> { M1, M2, M3 });
            BrowserDataPage<Message> myBrowserDataPage = new BrowserDataPage<Message>();
            List<Message>  MyList = new List<Message> { M1, M2, M3, M4, M5, M6 };
            myBrowserDataPage.TotalItemCount = myBrowserDataPage.Items.Count;
            myBrowserDataPage.Items = new ObservableCollection<Message>(MyList);
            List<Message> trie = new();
            if (navigation.Uri.Contains("sms"))
            {
                trie = myBrowserDataPage.Items.Where(t => t.Type.Equals("SMS")).ToList();               
            }
            else if (navigation.Uri.Contains("email"))
            {
                trie = myBrowserDataPage.Items.Where(t => t.Type.Equals("EMAIL")).ToList();               
            }
            myBrowserDataPage.Items = new ObservableCollection<Message>(trie);
            //return (BrowserDataPage<MessageLog>)trie;
            return myBrowserDataPage;
        }
    }

    public static class ExtensionMethods
    {
        public static void AddMessengerServices(this IServiceCollection service)
        {
            service.AddSingleton<MessageService>();
            service.AddSingleton<MessageLogService>();
        }
    }
}

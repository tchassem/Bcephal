using Bcephal.Models.Base;
using Bcephal.Models.Clients;
using Bcephal.Models.Users;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System.Collections.ObjectModel;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Services
{
    public class ClientService: Base.Services.Service<Client, Client>
    {

        public ClientService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "clients";
        }

        public async Task<ObservableCollection<Nameable>> getConnectedUserClients()
        {
            string response = await this.ExecuteGet(ResourcePath + "/connected-user-clients");
            ObservableCollection<Nameable> page =
                JsonConvert.DeserializeObject<ObservableCollection<Nameable>>(response, getJsonSerializerSettings());
            return page;
        }

        public async Task<ObservableCollection<Nameable>> getConnectedUserProfiles()
        {
            string response = await this.ExecuteGet(ResourcePath + "/connected-user-profiles");
            ObservableCollection<Nameable> page =
                JsonConvert.DeserializeObject<ObservableCollection<Nameable>>(response, getJsonSerializerSettings());
            return page;
        }

        public async Task<PrivilegeObserver> getPrivilegeObservers()
        {
            string response = await this.ExecuteGet(ResourcePath + "/privileges");
            PrivilegeObserver page =
                JsonConvert.DeserializeObject<PrivilegeObserver>(response, getJsonSerializerSettings());
            return page;
        }
    }
}

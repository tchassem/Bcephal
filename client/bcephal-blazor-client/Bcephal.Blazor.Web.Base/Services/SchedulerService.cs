using Bcephal.Models.Base;
using Bcephal.Models.Planners;
using Bcephal.Models.Schedulers;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System.Collections.ObjectModel;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class SchedulerService : Service<SchedulableObject, BrowserData>
    {
        public SchedulerService(HttpClient RestClient, IJSRuntime JSRuntime):base(RestClient, JSRuntime)
        {
            ResourcePath = "scheduler";
        }

        public async Task<bool> start(string projectCode,SchedulerType type, ObservableCollection<long?>  ids)
        {
            var requestFilter = new SchedulerRequest()
            {
                ObjectIds = ids,
                ObjectType = type.ToString(),
                ProjectCode = projectCode,
            };
            string response = await this.ExecutePost($"scheduler/start", requestFilter);
            bool page = JsonConvert.DeserializeObject<bool>(response, getJsonSerializerSettings());
            return page;
        }

        public async Task<bool> restart(string projectCode, SchedulerType type, ObservableCollection<long?> ids)
        {
            var requestFilter = new SchedulerRequest()
            {
                ObjectIds = ids,
                ObjectType = type.ToString(),
                ProjectCode = projectCode,
            };
            string response = await this.ExecutePost($"scheduler/restart", requestFilter);
            bool page = JsonConvert.DeserializeObject<bool>(response, getJsonSerializerSettings());
            return page;
        }

        public async Task<bool> stop(string projectCode, SchedulerType type, ObservableCollection<long?> ids)
        {
            var requestFilter = new SchedulerRequest()
            {
                ObjectIds = ids,
                ObjectType = type.ToString(),
                ProjectCode = projectCode,
            };
            string response = await this.ExecutePost($"scheduler/stop", requestFilter);
            bool page = JsonConvert.DeserializeObject<bool>(response, getJsonSerializerSettings());
            return page;
        }

    }

    public enum SchedulerType
    {
        ALL,
        AUTORECO,
        FILELOADER,
        ALARM,
        ARCHIVE,
        ROUTINE,
        TRANSFORMATIONTREE,
        BILLING,
        BOOKING,
        JOIN
    }
}

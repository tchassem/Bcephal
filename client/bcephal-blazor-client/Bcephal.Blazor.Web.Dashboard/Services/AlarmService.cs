
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Services.Mocks;
using Bcephal.Models.Alarms;
using Bcephal.Models.Base;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Services
{
    public class AlarmService : Service<Alarm, BrowserData>
    {

        public AlarmService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "/dashboarding/alarm";
        }

        protected override EditorData<Alarm> DeserialiazeEditorData(string response)
        {
            return JsonConvert.DeserializeObject<AlarmModelEditorData>(response, getJsonSerializerSettings());
        }

        public async Task<bool> sendAlertMessage(Alarm alarm)
        {
            string result = await ExecutePost(ResourcePath + "/send-message", alarm);
            return result == "true" ? true : false;
        }

        protected override async Task<bool> CheckDuplicateObject(Alarm item)
        {
            Alarm alarm = await getByName(item.Name);
            return alarm == null || !(item.Id.HasValue && alarm.Id.Value == item.Id.Value) ? false : true;
        }
    }
}

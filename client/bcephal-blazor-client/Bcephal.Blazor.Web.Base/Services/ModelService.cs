using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids.Filters;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class ModelService : Service<Model, object>
    {
        public ModelService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "initiation/api";
        }

        public async Task<BrowserDataPage<T>> SearchAttributeValues<T>(BrowserDataFilter filter)
        {
            string ResourcePath_ = "initiation/model";
            string response = await this.ExecutePost(ResourcePath_ + "/search-attribute-values", filter);
            BrowserDataPage<T> page = JsonConvert.DeserializeObject<BrowserDataPage<T>>(response, getJsonSerializerSettings());
            return page;
        }
        public async Task<BrowserDataPage<T>> SearchMeasureValues<T>(BrowserDataFilter filter)
        {
            string ResourcePath_ = "initiation/model";
            string response = await this.ExecutePost(ResourcePath_ + "/search-measure-values", filter);
            BrowserDataPage<T> page = JsonConvert.DeserializeObject<BrowserDataPage<T>>(response, getJsonSerializerSettings());
            return page;
        }
        public async Task<BrowserDataPage<T>> SearchPeriodValues<T>(BrowserDataFilter filter)
        {
            string ResourcePath_ = "initiation/model";
            string response = await this.ExecutePost(ResourcePath_ + "/search-period-values", filter);
            BrowserDataPage<T> page = JsonConvert.DeserializeObject<BrowserDataPage<T>>(response, getJsonSerializerSettings());
            return page;
        }

        public async Task<Dimension> createDimension(DimensionType dimensionType, DimensionApi data)
        {
            string subPath = "create-attribute";
            if (dimensionType.IsPeriod())
            {
                subPath = "create-period";
            }else
                if (dimensionType.IsMeasure())
            {
                subPath = "create-measure";
            }
            string response = await this.ExecutePost(ResourcePath + $"/{subPath}", data);
            Dimension page = null;
            if (dimensionType.IsPeriod())
            {
               page = JsonConvert.DeserializeObject<Models.Dimensions.Period>(response, getJsonSerializerSettings());
            }
            if (dimensionType.IsMeasure())
            {
               page = JsonConvert.DeserializeObject<Models.Dimensions.Measure>(response, getJsonSerializerSettings());
            }
            if (dimensionType.IsAttribute())
            {
                page = JsonConvert.DeserializeObject<Models.Dimensions.Attribute>(response, getJsonSerializerSettings());
            }
            return page;
        }
    }

    public class DimensionApi
    {

        public string Name { get; set; }

        public long? Parent { get; set; }

        public long? Entity { get; set; }

    }
}

using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Reconciliation;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Services
{
    public class ReconciliationModelService: Service<ReconciliationModel,BrowserData>, IGridItemService
    {
        public ReconciliationModelService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "reconciliation/model";
        }

        public async Task<bool> DeleteRows(List<long> id)
        {
            string response = await this.ExecutePost(ResourcePath + "/delete-rows", id);
            bool result = false;
            bool.TryParse(response, out result);
            return result;
        }

        protected override async Task<bool> CheckDuplicateObject(ReconciliationModel item)
        {
            ReconciliationModel reconciliationModel = await getByName(item.Name);
            return reconciliationModel == null || !(item.Id.HasValue && reconciliationModel.Id.Value == item.Id.Value) ? false : true;
        }

        protected override  ReconciliationModelEditorData DeserialiazeEditorData(string response)
        {
            return JsonConvert.DeserializeObject<ReconciliationModelEditorData>(response, getJsonSerializerSettings());
        }

        public async Task<GrilleEditedResult> EditCell(GrilleEditedElement grilleEditedElement)
        {
            string response = await this.ExecutePost(ResourcePath + "/edit-cell", grilleEditedElement);
            GrilleEditedResult page = JsonConvert.DeserializeObject<GrilleEditedResult>(response);
            return page;
        }

        public async Task<EditorData<ReconciliationModel>> Publish(long id)
        {
            string Uri = ResourcePath + "/publish/" + id;
            string responseMessage = await ExecuteGet(Uri);
            EditorData<ReconciliationModel> result = JsonConvert.DeserializeObject<EditorData<ReconciliationModel>>(responseMessage);
            return result;
        }

        public async Task<bool> RefreshPublication(long id)
        {
            string Uri = ResourcePath + "/refresh-publication/" + id;
            string responseMessage = await ExecuteGet(Uri);
            bool result = JsonConvert.DeserializeObject<bool>(responseMessage);
            return result;
        }

        public async Task<EditorData<ReconciliationModel>> ResetPublication(long id)
        {
            string Uri = ResourcePath + "/reset-publication/" + id;
            string responseMessage = await ExecuteGet(Uri);
            EditorData<ReconciliationModel> result = JsonConvert.DeserializeObject<EditorData<ReconciliationModel>>(responseMessage);
            return result;
        }

        public async Task<GridItem> EditCells(List<GrilleEditedElement> grilleEditedElements)
        {
            GrilleEditedResult page = null;
            GridItem gridItem = null;
            foreach (var el in grilleEditedElements)
            {
                if (gridItem != null)
                {
                    el.Id = gridItem.GetId();
                    page = await EditCell(el);
                    gridItem = new GridItem(page.Datas);
                }
                else
                {
                    page = await EditCell(el);
                    gridItem = new GridItem(page.Datas);
                }
            }
            return gridItem;
        }


        public async Task<bool> Reconciliate(ReconciliationData ReconciliationData_)
        {
            string response = await this.ExecutePost(ResourcePath + "/reconciliate", ReconciliationData_);
            bool result = false;
            bool.TryParse(response, out result);
            return result;
        }


        public async Task<bool> ResetReconciliation(ReconciliationData ReconciliationData_)
        {
            string response = await this.ExecutePost(ResourcePath + "/reset-reconciliation", ReconciliationData_);
            bool result = false;
            bool.TryParse(response, out result);
            return result;
        }

        public async Task<bool> ContainsReconciliation(long recoId, List<long> ids)
        {
            string response = await this.ExecutePost(ResourcePath + $"/contains-reconciliated-items/{recoId}", ids);
            bool result = false;
            bool.TryParse(response, out result);
            return result;
        }

        public async Task<bool> RunFreeze(ReconciliationData reconciliationData)
        {
            string response = await this.ExecutePost(ResourcePath + "/freeze", reconciliationData);
            bool result = false;
            bool.TryParse(response, out result);
            return result;
        }

        public async Task<bool> RunUnFreeze(ReconciliationData reconciliationData)
        {
            string response = await this.ExecutePost(ResourcePath + "/unfreeze", reconciliationData);
            bool result = false;
            bool.TryParse(response, out result);
            return result;
        }

        public async Task<bool> RefreshPartialReconciliation()
        {
            string response = await this.ExecutePost(ResourcePath + "/unfreeze");
            bool result = false;
            bool.TryParse(response, out result);
            return result;
        }


        public async Task<bool> RunNeutralization(ReconciliationData reconciliationData)
        {
            string response = await this.ExecutePost(ResourcePath + "/neutralize", reconciliationData);
            bool result = false;
            bool.TryParse(response, out result);
            return result;
        }

        public async Task<bool> RunUnNeutralization(ReconciliationData reconciliationData)
        {
            string response = await this.ExecutePost(ResourcePath + "/unneutralize", reconciliationData);
            bool result = false;
            bool.TryParse(response, out result);
            return result;
        }

        public async Task<BrowserDataPage<object[]>> SearchRowsPartial(BrowserDataFilter filter)
        {
            string response = await this.ExecutePost(ResourcePath + "/refresh-reconciliated-amounts", filter);
            BrowserDataPage<object[]> page =
                JsonConvert.DeserializeObject<BrowserDataPage<object[]>>(response, getJsonSerializerSettings());
            return page;
        }

        public async Task<BrowserDataPage<P>> SearchRowsPartial<P>(BrowserDataFilter filter)
        {
            string response = await this.ExecutePost(ResourcePath + "/refresh-reconciliated-amounts", filter);
            BrowserDataPage<P> page =
                JsonConvert.DeserializeObject<BrowserDataPage<P>>(response, getJsonSerializerSettings());
            return page;
        }

    }

    public static class ExtensionMethods
    {
        public static void AddReconciliationServices(this IServiceCollection service)
        {
            service.AddSingleton<ReconciliationModelService>();
            service.AddSingleton<ReconciliationLogService>();
            service.AddSingleton<AutoRecoLogService>();
            service.AddSingleton<ScheduledAutoRecoService>();
        }
    }
}

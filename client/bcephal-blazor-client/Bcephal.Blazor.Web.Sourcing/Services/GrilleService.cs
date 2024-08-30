using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Grids.Filters;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Services
{

    public class GrilleService : Service<Grille, object>, IGridItemService
    {
        public GrilleService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "sourcing/grid";
            SocketResourcePath = "ws/sourcing";
        }

        protected override async Task<bool> CheckDuplicateObject(Grille item)
        {
            Grille grid = await getByName(item.Name);
            return grid == null || !(item.Id.HasValue && grid.Id.Value == item.Id.Value) ?  false :  true;
        }
        public async Task<bool> DeleteRows(List<long> id)
        {
            string response = await this.ExecutePost(ResourcePath + "/delete-rows", id);
            bool result = false;
            try
            {
                bool.TryParse(response, out result);
            }catch(Exception )
            {

            }
            return result;
        }
        public async Task<bool> SetEditable(long id, bool editable)
        {
            string response = await this.ExecutePost(ResourcePath + $"/set-editable/{id}/{editable}");
            bool result = false;
            try
            {
                bool.TryParse(response, out result);
            }catch(Exception )
            {

            }
            return result;
        }

        public async Task<bool> Duplicate(IEnumerable<long> id)
        {
            string response = await this.ExecutePost(ResourcePath + "/duplicate-rows", id);
            bool result = false;
            try
            {
                bool.TryParse(response, out result);
            }
            catch (Exception)
            {

            }
            return result;
        }

        public async Task<GrilleEditedResult> EditCell(GrilleEditedElement grilleEditedElement)
        {
            string response = await this.ExecutePost(ResourcePath + "/edit-cell", grilleEditedElement);
            GrilleEditedResult page = JsonConvert.DeserializeObject<GrilleEditedResult>(response);
            return page;
        }

        public async Task<GridItem> EditCells(List<GrilleEditedElement> grilleEditedElements)
        {
            GrilleEditedResult page = null;
            GridItem gridItem = null;
            foreach (var el in grilleEditedElements)
            {
                if(gridItem != null)
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

        public async Task<ObservableCollection<GrilleColumn>> GetColumns(long id)
        {
            string Uri = ResourcePath + "/columns/" + id;
            string responseMessage = await ExecuteGet(Uri);
            ObservableCollection<GrilleColumn> resul = JsonConvert.DeserializeObject<ObservableCollection<GrilleColumn>>(responseMessage);
            return resul;
        }

        public async Task<EditorData<Grille>> Publish(long id)
        {
            string Uri = ResourcePath + "/publish/" + id;
            string responseMessage = await ExecuteGet(Uri);
            EditorData<Grille> result = JsonConvert.DeserializeObject<EditorData<Grille>>(responseMessage);
            return result;
        }

        public async Task<bool> Publish(List<long> ids)
        {
            string Uri = ResourcePath + "/publish";
            string responseMessage = await ExecutePost(Uri, ids);
            bool result = JsonConvert.DeserializeObject<bool>(responseMessage);
            return result;
        }

        public async Task<bool> RefreshPublication(long id)
        {
            string Uri = ResourcePath + "/refresh-publication/" + id;
            string responseMessage = await ExecuteGet(Uri);
            bool result = JsonConvert.DeserializeObject<bool>(responseMessage);
            return result;
        }

        public async Task<bool> RefreshPublication(List<long> ids)
        {
            string Uri = ResourcePath + "/refresh-publication";
            string responseMessage = await ExecutePost(Uri, ids);
            bool result = JsonConvert.DeserializeObject<bool>(responseMessage);
            return result;
        }

        public async Task<EditorData<Grille>> ResetPublication(long id)
        {
            string Uri = ResourcePath + "/reset-publication/" + id;
            string responseMessage = await ExecuteGet(Uri);
            EditorData<Grille> result = JsonConvert.DeserializeObject<EditorData<Grille>>(responseMessage);
            return result;
        }


        public async Task<bool> ResetPublication(List<long> ids)
        {
            string Uri = ResourcePath + "/reset-publication";
            string responseMessage = await ExecutePost(Uri, ids);
            bool result = JsonConvert.DeserializeObject<bool>(responseMessage);
            return result;
        }


        public Task<BrowserDataPage<object[]>> SearchRowsPartial(BrowserDataFilter filter)
        {
            return Task.FromResult(new BrowserDataPage<object[]>());
        }

        public Task<BrowserDataPage<P>> SearchRowsPartial<P>(BrowserDataFilter filter)
        {
            return Task.FromResult(new BrowserDataPage<P>());
        }
    }

    public class ExportDataTransfert
    {
        public byte[] Data { get; set; }
        public Decision? decision { get; set; }

        public string Type { get; set; }
                
        [JsonIgnore]
        public GrilleExportDataType DataType
        {
            get { return GrilleExportDataType.GetByCode(this.Type); }
            set { this.Type = value != null ? value.code : null; }
        }

        [JsonIgnore]
        public bool IsExcel
        {
            get { return DataType == GrilleExportDataType.EXCEL; }
        }

        [JsonIgnore]
        public bool IsCsv
        {
            get { return DataType == GrilleExportDataType.CSV; }
        }

        [JsonIgnore]
        public bool IsJson
        {
            get { return DataType == GrilleExportDataType.JSON; }
        }

    }

    public enum Decision
    {
        END, STOP, CONTINUE, NEW

    }

    public static class ExtensionMethods
    {
        public static void AddSourcingServices(this IServiceCollection service)
        {
            service.AddSingleton<FileLoaderService>();
            service.AddSingleton<SpotService>();
            service.AddSingleton<AutoRecoService>();
            service.AddSingleton<GrilleService>();
            service.AddSingleton<ScheduledFileLoaderService>();
            service.AddSingleton<FileLoaderLogService>();
            service.AddSingleton<FileLoaderLogItemService>();
            service.AddSingleton<InputSpreadSheetService>();
        }
    }
}

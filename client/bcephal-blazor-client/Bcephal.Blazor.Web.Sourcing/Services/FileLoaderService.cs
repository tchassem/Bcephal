using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Exceptions;
using Bcephal.Models.Loaders;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.ObjectModel;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Services
{
    public class FileLoaderService : Service<FileLoader, FileLoaderBrowserData>
    {
        public FileLoaderService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "sourcing/file-loader";
            this.SocketResourcePath = "/ws/sourcing/file-loader";
        }

        protected override EditorData<FileLoader> DeserialiazeEditorData(string response)
        {
            return JsonConvert.DeserializeObject<FileLoaderEditorData>(response, getJsonSerializerSettings());
        }

        protected override async Task<bool> CheckDuplicateObject(FileLoader item)
        {
            FileLoader fileLoader = await getByName(item.Name);
            return fileLoader == null || !(item.Id.HasValue && fileLoader.Id.Value == item.Id.Value) ? false : true;
        }

        public async Task<ObservableCollection<Models.Loaders.FileLoaderColumn>> LoaderFileLoaderColumn(FileLoaderColumnDataBuilder item, string fileName, byte[] file)
        {
            if (item == null)
            {
                return new ObservableCollection<Models.Loaders.FileLoaderColumn>();
            }
            if (file.Length <= 0)
            {
                return new ObservableCollection<Models.Loaders.FileLoaderColumn>();
            }
            HttpRequestMessage httpRequest;            

            using var form = new MultipartFormDataContent();
            using var fileContent = new ByteArrayContent(file);
            string json = Serialize(item);
            using var fileContentString = new StringContent(json);
            fileContent.Headers.ContentType = MediaTypeHeaderValue.Parse("multipart/form-data");
            fileContentString.Headers.ContentType = MediaTypeHeaderValue.Parse("application/json");
            form.Add(fileContent, "file", fileName);
            form.Add(fileContentString, "data");
            httpRequest = new HttpRequestMessage() { 
                Content = form, 
                Method = HttpMethod.Post, 
                RequestUri = new Uri(RestClient.BaseAddress + ResourcePath + "/build-columns") 
            };
            try
            {
                if (RestClient.DefaultRequestHeaders != null)
                {
                    RestClient.DefaultRequestHeaders.Remove("Accept-Language");
                    RestClient.DefaultRequestHeaders.Add("Accept-Language", UserService.DefaultLanguage);
                }
                HttpResponseMessage queryResult = await RestClient.SendAsync(httpRequest);
                await ValidateResponse(queryResult);
                String response =  await queryResult.Content.ReadAsStringAsync();
                ObservableCollection<Models.Loaders.FileLoaderColumn> result = JsonConvert.DeserializeObject<ObservableCollection<Models.Loaders.FileLoaderColumn>>(response);
                return result;
            }
            catch (Exception e)
            {
                if (e is BcephalException)
                {
                    throw e;
                }
                throw new BcephalException(e.Message, e);
            }
            
        }


        public async Task<ObservableCollection<Models.Loaders.FileLoaderColumn>> LoaderFileLoaderColumn(FileLoaderColumnDataBuilder item, string remotePath)
        {
            if (RestClient.DefaultRequestHeaders != null)
            {
                RestClient.DefaultRequestHeaders.Remove("remote-path-__");
                RestClient.DefaultRequestHeaders.Add("remote-path-__", remotePath);
            }
            string response = await this.ExecutePost(ResourcePath + "/build-columns-by-remote-path", item);
            ObservableCollection<Models.Loaders.FileLoaderColumn> result = JsonConvert.DeserializeObject<ObservableCollection<Models.Loaders.FileLoaderColumn>>(response);
            return result;
            
        }
        public async Task<SpreadSheetData> GetSpreadSheetData(string remotePath)
        {
            if (RestClient.DefaultRequestHeaders != null)
            {
                RestClient.DefaultRequestHeaders.Remove("remote-path-__");
                RestClient.DefaultRequestHeaders.Add("remote-path-__", remotePath);
            }
            string response = await this.ExecutePost(ResourcePath + "/spreadsheet/info-by-path");
            return JsonConvert.DeserializeObject<SpreadSheetData>(response);
        }
    }

    public class SheetData
    {
        public int Index { get; set; }
        public string Name { get; set; }
    }
    public class SpreadSheetData
    {
        public ObservableCollection<SheetData> SheetDatas { get; set; }
        public string RepositoryOnServer { get; set; }
    }
}

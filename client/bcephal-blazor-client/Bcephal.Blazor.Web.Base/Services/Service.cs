using System;
using System.Net;
using System.Net.Http;
using Newtonsoft.Json;
using System.Threading.Tasks;
using System.Text;
using System.Net.Http.Headers;
using Microsoft.JSInterop;
using Microsoft.AspNetCore.Components.WebAssembly.Http;
using Bcephal.Models.Exceptions;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Base;
using System.Collections.Generic;
using System.IO;
using System.IO.Compression;
using Microsoft.AspNetCore.Components;
using System.Globalization;
using Newtonsoft.Json.Converters;
using Bcephal.Models.Grids;
using Microsoft.AspNetCore.Components.Forms;
using System.Collections.ObjectModel;
using System.Linq;
using Bcephal.Models.socket;
using Bcephal.Models.Filters;

namespace Bcephal.Blazor.Web.Base.Services
{
    /// <summary>
    /// Cette classe encapsule les propriétés communues des services.
    /// </summary>
    /// <typeparam name="T"></typeparam>
    public abstract class Service<T, B>
    {

        #region Properties


        /// <summary>
        /// RestClient
        /// </summary>
        public HttpClient RestClient { get; set; }

        public IJSRuntime JSRuntime;

        /// <summary>
        /// RestClient
        /// </summary>
        public string ResourcePath { get; set; }

        /// <summary>
        /// RestClient
        /// </summary>
        public string SocketResourcePath { get; set; }

        [Inject]
        protected AppState AppState { get; set; }

        #endregion


        #region Constructors

        /// <summary>
        /// Build new instance of Service
        /// </summary>
        public Service(HttpClient RestClient, IJSRuntime JSRuntime)
        {
            this.JSRuntime = JSRuntime;
            this.RestClient = RestClient;
            this.ResourcePath = RestClient.BaseAddress.AbsoluteUri;
        }

        #endregion


        #region Save Operations

        /// <summary>
        /// Save the given item.
        /// </summary>
        /// <param name="item">Item to save</param>
        /// <returns>Saved item</returns>
        public async virtual Task<T> Save(T item)
        {
            if (item == null)
            {
                return item;
            }
            //if(await CheckDuplicateObject(item))
            //{
            //    throw new BcephalException(AppState["duplicate.data"]);
            //}
            String response = await this.ExecutePost(ResourcePath + "/save", item);
            item = JsonConvert.DeserializeObject<T>(response);
            return item;
        }

        protected virtual Task<bool>  CheckDuplicateObject(T item)
        {
            return Task.FromResult(false);
        }

        public async virtual Task<ListChangeHandler<P>> SaveAll<P>(ListChangeHandler<P> items) where P : IPersistent
        {
            if (items == null)
            {
                return items;
            }
            String response = await this.ExecutePost(ResourcePath + "/save-all", items);
            items = JsonConvert.DeserializeObject<ListChangeHandler<P>>(response);
            return items;
        }

        public async virtual Task<T> update(T item)
        {
            if (item == null)
            {
                return item;
            }
            String response = await this.ExecutePost(ResourcePath + "/update", item);
            item = JsonConvert.DeserializeObject<T>(response);
            return item;
        }
        #endregion


        #region Get Object Operations

        /// <summary>
        /// 
        /// </summary>
        /// <param name="id">Oid of the object to return.</param>
        /// <returns>Object such that object.id == id.</returns>
        public async virtual Task<T> getById(long id)
        {
            String response = await this.ExecuteGet(ResourcePath + "/" + id);
            T item = JsonConvert.DeserializeObject<T>(response);
            return item;
        }

        /// <summary>
        ///     Find Object of type T by his name
        /// </summary>
        /// <param name="name">Name of the object to return.</param>
        /// <returns>Object such that object.name == name.</returns>
        public async virtual Task<T> getByName(string name)
        {
            String response = await this.ExecuteGet(ResourcePath + "/by-name/" + name);
            T item = JsonConvert.DeserializeObject<T>(response);
            return item;
        }

        /// <summary>
        /// Delete item with the given oid
        /// </summary>
        /// <param name="oid"></param>
        /// <returns></returns>
        public async Task<bool> Delete(long id)
        {
            HttpResponseMessage response = await this.ExecuteDelete(ResourcePath + "/delete/" + id);
            bool ok = JsonConvert.DeserializeObject<bool>(await response.Content.ReadAsStringAsync());
            return ok;
        }

        public async Task<bool> Delete(List<long> ids)
        {
            string response = await this.ExecutePost(ResourcePath + "/delete-items", ids);
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

        public async Task<BrowserDataPage<B>> Search(BrowserDataFilter filter)
        {
            string response = await this.ExecutePost(ResourcePath + "/search", filter);
            BrowserDataPage<B> page = JsonConvert.DeserializeObject<BrowserDataPage<B>>(response, getJsonSerializerSettings());
            return page;
        }

        public async Task<BrowserDataPage<P>> Search<P>(BrowserDataFilter filter)
        {
            string response = await this.ExecutePost(ResourcePath + "/search", filter);
            BrowserDataPage<P> page = JsonConvert.DeserializeObject<BrowserDataPage<P>>(response, getJsonSerializerSettings());
            return page;
        }

        public async Task<BrowserDataPage<object[]>> SearchRows(BrowserDataFilter filter)
        {
            string response = await this.ExecutePost(ResourcePath + "/rows", filter);
            BrowserDataPage<object[]> page =
                JsonConvert.DeserializeObject<BrowserDataPage<object[]>>(response, getJsonSerializerSettings());
            return page;
        }

        public async Task<BrowserDataPage<P>> SearchRows<P>(BrowserDataFilter filter)
        {
            string response = await this.ExecutePost(ResourcePath + "/rows", filter);
            BrowserDataPage<P> page = JsonConvert.DeserializeObject<BrowserDataPage<P>>(response, getJsonSerializerSettings());
            return page;
        }

        public async Task<EditorData<T>> GetEditorData(EditorDataFilter filter)
        {
            string response = await this.ExecutePost(ResourcePath + "/editor-data", filter);
            EditorData<T> page = DeserialiazeEditorData(response);
            return page;
        }

        protected virtual EditorData<T> DeserialiazeEditorData(string response)
        {
            return JsonConvert.DeserializeObject<EditorData<T>>(response, getJsonSerializerSettings());
        }


        protected JsonSerializerSettings getJsonSerializerSettings()
        {
            return new JsonSerializerSettings()
            {
                ReferenceLoopHandling = ReferenceLoopHandling.Ignore,
            };
        }


        /// <summary>
        /// Delete item with the given uri
        /// </summary>
        /// <param name="oid"></param>
        /// <returns></returns>
        public async Task<bool> DeleteString(String uri)
        {
            HttpResponseMessage response = await this.ExecuteDelete(uri);
            string Result = await response.Content.ReadAsStringAsync();
            bool ok = JsonConvert.DeserializeObject<bool>(Result);
            return ok;

        }

        /// <summary>
        /// Execute DELETE request
        /// </summary>
        /// <param name="path">Request local path</param>
        /// <returns>The response content (generally as JSON string)</returns>
        protected Task<HttpResponseMessage> ExecuteDelete(String path)
        {
            return RestClient.DeleteAsync(path);
        }
        #endregion



        /// <summary>
        /// Execute request
        /// </summary>
        /// <param name="path">Request local path</param>
        /// <param name="method">Request method</param>
        /// <param name="body">Request body</param>
        /// <returns>The response content (generally as JSON string)</returns>
        protected Task<String> ExecutePost(String path, Object body = null)
        {
            return Execute(path, HttpMethod.Post, body);
        }

        public async Task<String> Execute(String path, HttpMethod method, Object body = null)
        {
            HttpRequestMessage httpRequest;
            if (!path.StartsWith("/"))
            {
                path = RestClient.BaseAddress + path;
            }
            else
            {
                path = RestClient.BaseAddress + path.Substring(1);
            }
            if (body != null)
            {
                var Content_ = new System.Net.Http.StringContent(Serialize(body), Encoding.UTF8, "application/json");
                httpRequest = new HttpRequestMessage { Content = Content_, Method = method, RequestUri = new Uri(path) };
                Content_.Headers.ContentType = new MediaTypeHeaderValue("application/json");
            }
            else
            {
                var Content_ = new System.Net.Http.StringContent("", Encoding.UTF8, "application/json");
                httpRequest = new HttpRequestMessage(method, path);
                if (method.Equals(HttpMethod.Post))
                {
                    httpRequest.Content = Content_;
                    Content_.Headers.ContentType = new MediaTypeHeaderValue("application/json");
                }
                else
                {

                    httpRequest = new HttpRequestMessage
                    {
                        RequestUri = new Uri(path),
                        Method = method,
                        Headers = {
                                    { "X-Version", "1" }, // HERE IS HOW TO ADD HEADERS,
                                    //{ HttpRequestHeader.Authorization.ToString(), "[your authorization token]" },
                                    { HttpRequestHeader.ContentType.ToString(), "application/json" },//use this content type if you want to send more than one content type
                                }
                    };

                }
            }
            try
            {
                if (RestClient.DefaultRequestHeaders != null)
                {
                    RestClient.DefaultRequestHeaders.Remove("Accept-Language");
                    RestClient.DefaultRequestHeaders.Add("Accept-Language", UserService.DefaultLanguage);
                }
                httpRequest.SetBrowserRequestCredentials(BrowserRequestCredentials.Include);
                HttpResponseMessage queryResult = await RestClient.SendAsync(httpRequest);
                await ValidateResponse(queryResult);
                return await queryResult.Content.ReadAsStringAsync();
            }
            catch (HttpRequestException e)
            {
                BuildHttpRequestException(e);
                throw new BcephalException(e.Message, e);
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

        private void BuildHttpRequestException(HttpRequestException e)
        {
            Console.WriteLine("\nException Caught!");
            Console.WriteLine("Message :{0} {1} {2}", e.Message, e.StatusCode, e.Data);
            bool test1 = !string.IsNullOrWhiteSpace(e.Message) && e.Message.Contains("Failed to fetch");
            bool test2 = e.InnerException != null && !string.IsNullOrWhiteSpace(e.InnerException.Message) && e.InnerException.Message.Contains("Failed to fetch");           
            if (test1 || test2 || HttpStatusCode.MovedPermanently.Equals(e.StatusCode) || HttpStatusCode.PermanentRedirect.Equals(e.StatusCode))
            {
                throw new BcephalException("!!==== ResetSession ====!!", e);
            }
            else
            {
                throw new BcephalException(e.Message, e);
            }
        }
        public async Task<HttpResponseMessage> ExecuteRequest(String path, HttpMethod method, Object body = null)
        {
            HttpRequestMessage httpRequest;
            if (!path.StartsWith("/"))
            {
                path = RestClient.BaseAddress + path;
            }
            else
            {
                path = RestClient.BaseAddress + path.Substring(1);
            }
            if (body != null)
            {
                var Content_ = new System.Net.Http.StringContent(Serialize(body), Encoding.UTF8, "application/json");
                httpRequest = new HttpRequestMessage { Content = Content_, Method = method, RequestUri = new Uri(path) };
                Content_.Headers.ContentType = new MediaTypeHeaderValue("application/json");
            }
            else
            {
                var Content_ = new System.Net.Http.StringContent("", Encoding.UTF8, "application/json");
                httpRequest = new HttpRequestMessage(method, path);
                if (method.Equals(HttpMethod.Post))
                {
                    httpRequest.Content = Content_;
                    Content_.Headers.ContentType = new MediaTypeHeaderValue("application/json");
                }
                else
                {

                    httpRequest = new HttpRequestMessage
                    {
                        RequestUri = new Uri(path),
                        Method = method,
                        Headers = {
                                    { "X-Version", "1" }, // HERE IS HOW TO ADD HEADERS,
                                    { HttpRequestHeader.ContentType.ToString(), "application/json" },//use this content type if you want to send more than one content type
                                }
                    };

                }
            }
            try
            {
                if (RestClient.DefaultRequestHeaders != null)
                {
                    RestClient.DefaultRequestHeaders.Remove("Accept-Language");
                    RestClient.DefaultRequestHeaders.Add("Accept-Language", UserService.DefaultLanguage);
                }
                httpRequest.SetBrowserRequestCredentials(BrowserRequestCredentials.Include);
                HttpResponseMessage queryResult = await RestClient.SendAsync(httpRequest);
                await ValidateResponse(queryResult);
                return queryResult;
            }
            catch (HttpRequestException e)
            {
                BuildHttpRequestException(e);
                throw new BcephalException(e.Message, e);
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


        protected Task<String> ExecutePostString(String path, string body = null)
        {
            return ExecuteS(path, HttpMethod.Post, body);
        }
        private async Task<String> ExecuteS(String path, HttpMethod method, string body = null)
        {
            HttpRequestMessage httpRequest;
            if (!path.StartsWith("/"))
            {
                path = RestClient.BaseAddress + path;
            }
            else
            {
                path = RestClient.BaseAddress + path.Substring(1);
            }
            if (!string.IsNullOrWhiteSpace(body))
            {
                var Content_ = new System.Net.Http.StringContent(body, Encoding.UTF8, "application/json");
                httpRequest = new HttpRequestMessage() { Content = Content_, Method = method, RequestUri = new Uri(path) };
                Content_.Headers.ContentType = new MediaTypeHeaderValue("application/json");
            }
            else
            {
                var Content_ = new System.Net.Http.StringContent("", Encoding.UTF8, "application/json");
                httpRequest = new HttpRequestMessage(method, path);
                if (method.Equals(HttpMethod.Post))
                {
                    httpRequest.Content = Content_;
                    Content_.Headers.ContentType = new MediaTypeHeaderValue("application/json");
                }
                else
                {
                    httpRequest = new HttpRequestMessage()
                    {
                        RequestUri = new Uri(path),
                        Method = method,
                        Headers = {
                                { "X-Version", "1" }, // HERE IS HOW TO ADD HEADERS,
                                //{ HttpRequestHeader.Authorization.ToString(), "[your authorization token]" },
                                { HttpRequestHeader.ContentType.ToString(), "application/json" },//use this content type if you want to send more than one content type
                        }
                    };
                }
            }
            try
            {
                if (RestClient.DefaultRequestHeaders != null)
                {
                    RestClient.DefaultRequestHeaders.Remove("Accept-Language");
                    RestClient.DefaultRequestHeaders.Add("Accept-Language", UserService.DefaultLanguage);
                }
                HttpResponseMessage queryResult = await RestClient.SendAsync(httpRequest);
                await ValidateResponse(queryResult);
                return await queryResult.Content.ReadAsStringAsync();
            }
            catch (HttpRequestException e)
            {
                BuildHttpRequestException(e);
                throw new BcephalException(e.Message, e);
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

        protected Task<String> ExecuteGet(String path)
        {
            return Execute(path, HttpMethod.Get);
        }
        public String Serialize(Object item)
        {
            try
            {
                JsonSerializerSettings settings = new JsonSerializerSettings()
                {
                    ReferenceLoopHandling = Newtonsoft.Json.ReferenceLoopHandling.Serialize
                };
                return JsonConvert.SerializeObject(item, settings);
            }
            catch (Exception e)
            {
                throw new BcephalException("Unable to serialize : " + item, e);
            }
        }


        #region Validation

        /// <summary>
        /// Validate request response
        /// </summary>
        /// <param name="response"></param>
        /// <returns></returns>
        public async Task<bool> ValidateResponse(HttpResponseMessage response)
        {
            if (response == null)
            {
                throw new BcephalException("Request response is null!");
            }

            switch (response.StatusCode)
            {
                case HttpStatusCode.Moved: throw new BcephalException("Request aborted!", ((int)HttpStatusCode.Moved));
                case HttpStatusCode.RequestTimeout: throw new BcephalException("Request Time out!", ((int)HttpStatusCode.RequestTimeout));
                case HttpStatusCode.Ambiguous: throw new BcephalException(response.ReasonPhrase);
                case HttpStatusCode.ServiceUnavailable: throw new BcephalException(response.ReasonPhrase, ((int)HttpStatusCode.ServiceUnavailable));
                case HttpStatusCode.NotFound: throw new BcephalException(AppState["HttpError.NotFound"], ((int)HttpStatusCode.NotFound));
                case HttpStatusCode.BadRequest: throw new BcephalException(AppState["HttpError.BadRequest"], ((int)HttpStatusCode.BadRequest));
                default: break;
            }

            HttpStatusCode statusCode = response.StatusCode;
            if (statusCode == HttpStatusCode.OK)
            {
                return true;
            }
            else
            {
                string error = null;
                try
                {
                    error = JsonConvert.DeserializeObject<string>(await response.Content.ReadAsStringAsync());
                }
                catch { }
                if (error != null)
                {
                    throw new BcephalException(error);
                }
                if (statusCode == HttpStatusCode.InternalServerError)
                {
                    var internalerrormessage = await response.Content.ReadAsStringAsync();
                    throw new BcephalException(internalerrormessage);
                }
            }
            return false;
        }

        public async Task<String> Upload(String path, string fileName, byte[] file, String folder)
        {
            if (file.Length <= 0)
            {
                return null;
            }
            HttpRequestMessage httpRequest;
            if (!path.StartsWith("/"))
            {
                path = RestClient.BaseAddress + path;
            }
            else
            {
                path = RestClient.BaseAddress + path.Substring(1);
            }



            if (!path.EndsWith("/"))
            {
                path = path + "/upload/" + folder;
            }
            else
            {
                path = path + "upload/" + folder;
            }

            using var form = new MultipartFormDataContent();
            using var fileContent = new ByteArrayContent(file);
            fileContent.Headers.ContentType = MediaTypeHeaderValue.Parse("multipart/form-data");
            form.Add(fileContent, "file", fileName);
            httpRequest = new HttpRequestMessage() { Content = form, Method = HttpMethod.Post, RequestUri = new Uri(path) };
            try
            {
                if (RestClient.DefaultRequestHeaders != null)
                {
                    RestClient.DefaultRequestHeaders.Remove("Accept-Language");
                    RestClient.DefaultRequestHeaders.Add("Accept-Language", UserService.DefaultLanguage);
                }
                HttpResponseMessage queryResult = await RestClient.SendAsync(httpRequest);
                await ValidateResponse(queryResult);
                return await queryResult.Content.ReadAsStringAsync();
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


        public async Task<DataTransfert> Upload(DataTransfert data)
        {
            string path = $"{ResourcePath}/upload-by-data/resume";
            HttpRequestMessage httpRequest;
            if (!path.StartsWith("/"))
            {
                path = RestClient.BaseAddress + path;
            }
            else
            {
                path = RestClient.BaseAddress + path.Substring(1);
            }
            using var form = new MultipartFormDataContent();
            using var fileContent = new ByteArrayContent(System.Text.Encoding.UTF8.GetBytes(Serialize(data)));
            fileContent.Headers.ContentType = MediaTypeHeaderValue.Parse("multipart/form-data");
            form.Add(fileContent, "file", data.Name);
            httpRequest = new HttpRequestMessage() { Content = form, Method = HttpMethod.Post, RequestUri = new Uri(path) };
            try
            {
                if (RestClient.DefaultRequestHeaders != null)
                {
                    RestClient.DefaultRequestHeaders.Remove("Accept-Language");
                    RestClient.DefaultRequestHeaders.Add("Accept-Language", UserService.DefaultLanguage);
                }
                HttpResponseMessage queryResult = await RestClient.SendAsync(httpRequest);
                await ValidateResponse(queryResult);
                string response = await queryResult.Content.ReadAsStringAsync();
                return JsonConvert.DeserializeObject<DataTransfert>(response, getJsonSerializerSettings());
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

        public async Task ConnectSocket(CustomWebSocket Socket_, string path)
        {
            string uri = SocketResourcePath + path;
            await Socket_.ConnectAsync(uri);
        }

        public async Task ConnectSocketJS(SocketJS Socket_, string path, bool addHeader = false)
        {
            string Header = "";
            if (addHeader)
            {
                //Header = await GetHeader();
            }
            string uri = SocketResourcePath + path;
            await Socket_.ConnectAsync(uri, Header);
        }

        private Task<string> GetHeader()
        {
            return ExecuteGet("http-header");
        }

        public static byte[] ZipFiles(Dictionary<string, byte[]> files)
        {
            using (MemoryStream ms = new MemoryStream())
            {
                using (ZipArchive archive = new ZipArchive(ms, ZipArchiveMode.Update))
                {
                    foreach (var file in files)
                    {
                        ZipArchiveEntry archiveEntry = archive.CreateEntry(file.Key, CompressionLevel.Optimal); //create a file with this name
                        using (BinaryWriter writer = new BinaryWriter(archiveEntry.Open()))
                        {
                            writer.Write(file.Value); //write the binary data
                        }
                    }
                }
                //ZipArchive must be disposed before the MemoryStream has data
                return ms.ToArray();
            }
        }

        public async Task<byte[]> ZipFiles_(Dictionary<string, byte[]> files)
        {
            string DestFile = Path.GetTempPath() + "template.zip";
            // string DestFile = Path.GetTempPath() + "templates" + Path.DirectorySeparatorChar + AppState.ProjectCode + Path.DirectorySeparatorChar + "template.zip";

            if (File.Exists(DestFile))
            {
                File.Delete(DestFile);
            }

            using (FileStream zipFile = new FileStream(DestFile, FileMode.OpenOrCreate, FileAccess.ReadWrite, FileShare.ReadWrite))
            {
                using (ZipArchive archive = new ZipArchive(zipFile, ZipArchiveMode.Create))
                {
                    foreach (var file in files)
                    {
                        ZipArchiveEntry archiveEntry = archive.CreateEntry(file.Key, CompressionLevel.Optimal);
                        using (BinaryWriter writer = new BinaryWriter(archiveEntry.Open()))
                        {
                            writer.Write(file.Value);
                            Console.WriteLine($"{archiveEntry.FullName} was compressed and added to zip file.");
                        }
                    }
                    // archive.Dispose();
                }
                // Or put the instructions below after previous comment dispose to avoid the close stream.
                MemoryStream ms = new MemoryStream();
                await zipFile.CopyToAsync(ms);
                // zipFile.Dispose();
                return ms.ToArray();
            }
        }


        #endregion



    }


    public class CustomDateTimeConverter : DateTimeConverterBase
    {

        private const string Format = "yyyy-MM-dd";

        public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer)
        {
            writer.WriteValue(((DateTime)value).ToString(Format));
        }

        public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            if (reader.Value == null)
            {
                return null;
            }

            var s = reader.Value.ToString();
            DateTime result;
            if (DateTime.TryParseExact(s, Format, CultureInfo.CurrentCulture, DateTimeStyles.None, out result))
            {
                return result;
            }
            return null;
        }
    }

    public interface IGridItemService
    {
        public Task<GridItem> EditCells(List<GrilleEditedElement> grilleEditedElements);
        public Task<GrilleEditedResult> EditCell(GrilleEditedElement grilleEditedElement);
        public Task<bool> DeleteRows(List<long> id);
        public Task<BrowserDataPage<object[]>> SearchRows(BrowserDataFilter filter);
        public Task<BrowserDataPage<P>> SearchRows<P>(BrowserDataFilter filter);
        public Task<BrowserDataPage<object[]>> SearchRowsPartial(BrowserDataFilter filter);
        public Task<BrowserDataPage<P>> SearchRowsPartial<P>(BrowserDataFilter filter);
    }


}

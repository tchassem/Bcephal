using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.IO;
using Microsoft.AspNetCore.Components.Forms;
using Microsoft.Extensions.Logging;
using Bcephal.Models.Loaders;
using System.Text.RegularExpressions;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.socket;
using Bcephal.Models.Base;
using Newtonsoft.Json;
using Bcephal.Models.Exceptions;
using Bcephal.Models.Grids.Filters;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Collections.ObjectModel;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class CustomInputFile : ComponentBase, IAsyncDisposable
    {
        [Inject]
        private IJSRuntime JSRuntime { get; set; }
        [Inject]
        private AppState AppState { get; set; }

        IDictionary<string, object> attrib = new Dictionary<string, object>();

        [Parameter]
        public bool Editable { get; set; } = true;

        [CascadingParameter]
        public Error Error { get; set; }
        public List<IBrowserFile> LoadedFiles { get; set; } = new();

        [Parameter]
        public Func<List<IBrowserFile>, Task> LoadedFilesChanged { get; set; }

        [Parameter]
        public Func<Dictionary<string, string>, Task> LoadedFilesChanged_ { get; set; }

        private long maxFileSizeKo = 1024 * 1024;
        private long maxFileSize { get; set; } = 1024 * 1024 * 1024;

        private long maxFileSize_ { get; set; } = 1024;

        [Parameter]
        public long MaxFileSize
        {
            get { return maxFileSize_; }
            set
            {
                if (value > 0 && value < 1024)
                {
                    maxFileSize_ = value;
                    maxFileSize = maxFileSizeKo * value;
                }
            }
        }


        private int maxAllowedFiles = 100;
        [Parameter]
        public int MaxAllowedFiles
        {
            get { return maxAllowedFiles; }
            set
            {
                if (value > 0 && value < 1000)
                {
                    maxAllowedFiles = value;
                }
            }
        }

        [Parameter]
        public string NameZipFile { get; set; }
        [Parameter]
        public List<string> Extension { get; set; }

        [Parameter]
        public Dictionary<string, List<string>> filters { get; set; }

        [Parameter]
        public bool ToZip { get; set; }

        [Parameter]
        public bool IsSingle { get; set; } = false;

        [Parameter]
        public bool IsTemplate { get; set; } = false;

        [Parameter]
        public bool IsFile { get; set; } = true;


        private FullyProgressbar FullyProgressbarRef { get; set; }

        private FullyProgressbar FullyProgressbarRef_ { get => FullyProgressbarRef;
            set {
                FullyProgressbarRef = value;
                if (FullyProgressbarRef != null)
                {
                    FullyProgressbarRef.FullBase = FullBase;
                }
            }
        }

        private string FileSelected { get; set; }

        private string webkitdirectory => IsFile ? "" : "webkitdirectory";

        private string Parttern => @".*(\.[xX]{1}[lL]{1}[sS]{1}[xX]{0,1})$|.*(\.[cC]{1}[sS]{1}[vV]{1})$";



        bool HowProgressbar { get; set; }

        bool FullBase { get; set; } = false;


        private async Task setFullyProgressbar(bool state, bool full = false)
        {
            HowProgressbar = state;
            FullBase = full;
            await InvokeAsync(StateHasChanged);
        }

        private Task setParentPosition(long count, long currentPosition)
        {
            return FullyProgressbarRef_.setParentPosition(count, currentPosition);
        }


        private Task setChildrenPosition(long count, long currentPosition)
        {
            return FullyProgressbarRef_.setChildrenPosition(count, currentPosition);
        }

        protected override Task OnInitializedAsync()
        {
            AppState.SetParentProgressbarHander += setParentPosition;
            AppState.SetChildrenProgressbarHander += setChildrenPosition;
            AppState.ChangedStateUploadProgressbarHander += setFullyProgressbar;
            return base.OnInitializedAsync();
        }

        public ValueTask DisposeAsync()
        {
            AppState.SetParentProgressbarHander -= setParentPosition;
            AppState.SetChildrenProgressbarHander -= setChildrenPosition;
            AppState.ChangedStateUploadProgressbarHander -= setFullyProgressbar;
            return ValueTask.CompletedTask;
        }

        private async Task LoadFiles(InputFileChangeEventArgs e)
        {
            LoadedFiles.Clear();
            long currentParentPosition = 0;
            long filecount = e.GetMultipleFiles(maxAllowedFiles).Count;
            long Maxposition = 100;
            long percentStep = maxAllowedFiles != 0 ? Maxposition / filecount : 100;
            foreach (var file in e.GetMultipleFiles(maxAllowedFiles))
            {
                currentParentPosition++;
                long currentParentPosition_ = currentParentPosition * percentStep;
                LoadedFiles.Add(file);
            }
            await LoadedFilesChanged.Invoke(LoadedFiles);
        }

        [JSInvokable("LoadFiles_2")]
        public async void LoadFiles_2(object result)
        {
            ResData[] res = JsonConvert.DeserializeObject<ResData[]> (result.ToString());
            
            Dictionary<string, string> _res = new();
            if(res.Length > 0)
            {
                foreach (var obj in res)
                {
                    _res.Add(obj.Path, obj.Data);
                }
            }
            await LoadedFilesChanged_.Invoke(_res);
        }
        private async Task LoadFiles_(ChangeEventArgs e)
        {
            await JSRuntime.InvokeAsync<string>("OpenFile", DotNetObjectReference.Create(this), "LoadFiles_2");
        }

        private bool CheckFiltersRegex(IBrowserFile file)
        {            
            if (filters == null || filters.Count == 0)
            {
                return true;
            }
            bool result = true;
            foreach (var filter in filters)
            {
                if (!string.IsNullOrWhiteSpace(filter.Key) && filter.Key.Equals(FileNameCondition.BEGINS_WITH.code))
                {
                    if (!string.IsNullOrWhiteSpace(file.Name))
                    {
                        string pattern = "";
                        foreach (var value in filter.Value)
                        {
                            pattern += string.IsNullOrWhiteSpace(pattern) ? @"^(" : @"|^(" ;
                            pattern += value;
                            pattern += @").*";
                        }
                        Regex rgx = new Regex(pattern);
                        result = result && rgx.IsMatch(file.Name);
                    }
                }
                else
                if (!string.IsNullOrWhiteSpace(filter.Key) && filter.Key.Equals(FileNameCondition.CONTAINS.code))
                {
                    if (!string.IsNullOrWhiteSpace(file.Name))
                    {
                        string pattern = "";
                        foreach (var value in filter.Value)
                        {
                            pattern += string.IsNullOrWhiteSpace(pattern) ? @".*(" : @"|.*(";
                            pattern += value;
                            pattern += @").*";
                        }
                        Regex rgx = new Regex(pattern);
                        result = result && rgx.IsMatch(file.Name);
                    }
                }
                else
                if (!string.IsNullOrWhiteSpace(filter.Key) && filter.Key.Equals(FileNameCondition.DO_NOT_CONTAINS.code))
                {
                    if (!string.IsNullOrWhiteSpace(file.Name))
                    {
                        string pattern = "";
                        foreach (var value in filter.Value)
                        {
                            pattern += string.IsNullOrWhiteSpace(pattern) ? @".*(" : @"|.*(";
                            pattern += value;
                            pattern += @").*";
                        }
                        Regex rgx = new Regex(pattern);
                        result = result && !rgx.IsMatch(file.Name);
                    }
                }
                else
                     if (!string.IsNullOrWhiteSpace(filter.Key) && filter.Key.Equals(FileNameCondition.ENDS_WITH.code))
                {
                    if (!string.IsNullOrWhiteSpace(file.Name))
                    {
                        string pattern = "";
                        foreach (var value in filter.Value)
                        {
                            pattern += string.IsNullOrWhiteSpace(pattern) ? @".*(" : @"|.*(";
                            pattern += value;
                            pattern += @")$";
                        }
                        Regex rgx = new Regex(pattern);
                        result = result && rgx.IsMatch(file.Name);
                    }
                }
            } 
            return result;
        }

        private string AllExtensions()
        {
            string extensions = "";
            if (Extension != null && Extension.Any())
            {
                foreach (var ext in Extension)
                {
                    if (!string.IsNullOrWhiteSpace(ext))
                    {
                        extensions += "," + ext;
                    }
                    else
                    {
                        extensions = ext;
                    }
                }
            }
            if (extensions.Length > 1 && extensions.StartsWith(','))
            {
                extensions = extensions[1..];
            }
            return extensions;
        }

        
    }

    public static class IBrowserFileExtension
    {
        public static async Task UplaodBySocketLists(this IEnumerable<IBrowserFile> files, Func<DataTransfert, Task> AddFileItemCallback, Func<SocketJS, Task> connectiooSocketJs,
            IJSRuntime JSRuntime, AppState AppState, WebSocketAddress WebSocketAddress, IToastService toastService)
        {
            Action<object, object> CallBackUpload = async (sender, message) =>
            {
                await JSRuntime.InvokeVoidAsync("console.log", "call of upload data");
                if (message != null && !string.IsNullOrWhiteSpace(message.ToString()))
                {
                    TaskProgressInfo info = JsonConvert.DeserializeObject<TaskProgressInfo>(message.ToString());
                    if (info != null && !string.IsNullOrWhiteSpace(info.Message))
                    {
                        DataTransfert contentFile = JsonConvert.DeserializeObject<DataTransfert>(info.Message);
                        if (contentFile != null)
                        {
                            await  AddFileItemCallback?.Invoke(contentFile);
                            if (Decision.CLOSE.Equals(contentFile.decision))
                            {
                                await AppState.ChangedStateUploadProgressBar(false);
                            }
                        }
                    }
                }
            };

            Func<string, byte[], string, Models.socket.Decision, Task<string >> GetDataTransfert =  async  (fileName, file, folder, decision) =>
            {
                DataTransfert dataTra = new DataTransfert() { Data = file, DataType = DataType.EXCEL, Name = fileName, Folder = folder, decision = decision };
                try
                {
                    JsonSerializerSettings settings = new JsonSerializerSettings()
                    {
                        ReferenceLoopHandling = Newtonsoft.Json.ReferenceLoopHandling.Serialize
                    };
                    string data_ = JsonConvert.SerializeObject(dataTra, settings);
                    return Convert.ToBase64String(System.Text.Encoding.UTF8.GetBytes(data_));
                }
                catch (Exception e)
                {
                    await AppState.ChangedStateUploadProgressBar(false);
                    throw new BcephalException("Unable to serialize : " + dataTra, e);
                }
            };

            AppState.ShowLoadingStatus();
            await AppState.ChangedStateUploadProgressBar(true);
            await JSRuntime.InvokeVoidAsync("console.log", "try to upload data");
            SocketJS Socket = new SocketJS(WebSocketAddress, CallBackUpload, JSRuntime, AppState);
            string folder = "upload_data_" + new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds();
            Socket.BinaryTpe = "arraybuffer";
            Socket.SendHandler += async () =>
            {
                AppState.HideLoadingStatus();
                Socket.setBinaryTpe(null);
                long currentPerentPosition = 0;
                foreach (var file in files)
                {
                    double maxreader = 5 * 1024;
                    long maxFileSize = 1024 * 1024 * 1024;
                    byte[] bytes_ = new byte[((long)maxreader)];
                    bool first = true;
                    ulong index = 0;
                    currentPerentPosition++;
                    await AppState.SetParentProgressbar(files.Count(), currentPerentPosition);
                    Stream stream = file.OpenReadStream(maxFileSize);
                    long currentChildrenPosition = 0;
                    long ChildrenCount = file.Size / (long)maxreader;
                    long interValValue = ChildrenCount / 100;
                    while (maxreader > 0)
                    {
                        maxreader = await stream.ReadAsync(bytes_);
                        await Socket.sendBinary(GetDataTransfert.Invoke(file.Name, bytes_, folder, first ? Models.socket.Decision.NEW : Models.socket.Decision.CONTINUE));
                        
                            currentChildrenPosition++;
                            if (interValValue == 0)
                            {
                                interValValue = 1;
                            }
                            if (currentChildrenPosition % interValValue == 0)
                            {
                            await AppState.SetChildrenProgressbar(ChildrenCount, currentChildrenPosition);
                            }   
                        first = false;
                        index++;
                        bytes_ = new byte[((long)maxreader)];
                    }
                    stream.Close();
                    await Socket.sendBinary(GetDataTransfert.Invoke(file.Name, new byte[0], folder, Models.socket.Decision.END));
                }
                await Socket.sendBinary(GetDataTransfert.Invoke("clos", new byte[0], folder, Models.socket.Decision.CLOSE));
            };
            if (files != null && files.Count() > 0)
            {
                bool valueClose = false;
                bool valueError = false;
                Socket.CloseHandler += async () =>
                {
                    if (!valueClose && !valueError)
                    {
                        AppState.HideLoadingStatus();
                        // toastService.ShowSuccess(AppState["Upload.data.error.message"]);                       
                    }
                    await AppState.ChangedStateUploadProgressBar(false);
                };

                Socket.ErrorHandler += async (errorMessage) =>
                {
                    if (!valueError)
                    {
                        AppState.HideLoadingStatus();
                        toastService.ShowError((string)errorMessage, AppState["Upload.data.error.message"]);
                        valueError = true;
                    }
                    await AppState.ChangedStateUploadProgressBar(false);
                };
                await connectiooSocketJs.Invoke(Socket);
            }
            else
            {
                toastService.ShowWarning(AppState["empty.input.files"], AppState["warning"]);
            }
        }
        public static async Task UplaodBySocket(this IBrowserFile file, Func<DataTransfert, Task> AddFileItemCallback, Func<SocketJS, Task> connectiooSocketJs,
            IJSRuntime JSRuntime, AppState AppState, WebSocketAddress WebSocketAddress, IToastService toastService)
        {
            Action<object, object> CallBackUpload = async (sender, message) =>
         {
             await JSRuntime.InvokeVoidAsync("console.log", "call of upload data");
             if (message != null && !string.IsNullOrWhiteSpace(message.ToString()))
             {
                 TaskProgressInfo info = JsonConvert.DeserializeObject<TaskProgressInfo>(message.ToString());
                 if (info != null && !string.IsNullOrWhiteSpace(info.Message))
                 {
                     DataTransfert contentFile = JsonConvert.DeserializeObject<DataTransfert>(info.Message);
                     if (contentFile != null)
                     {
                          await AddFileItemCallback?.Invoke(contentFile);
                         if (Decision.CLOSE.Equals(contentFile.decision))
                         {
                             await AppState.ChangedStateUploadProgressBar(false);
                         }
                     }
                 }
             }
         };

            Func<string, byte[], string, Models.socket.Decision, Task<string>> GetDataTransfert = async (fileName, file, folder, decision) =>
             {
                 DataTransfert dataTra = new DataTransfert() { Data = file, DataType = DataType.EXCEL, Name = fileName, Folder = folder, decision = decision };
                 try
                 {
                     JsonSerializerSettings settings = new JsonSerializerSettings()
                     {
                         ReferenceLoopHandling = Newtonsoft.Json.ReferenceLoopHandling.Serialize
                     };
                     string data_ = JsonConvert.SerializeObject(dataTra, settings);
                     return Convert.ToBase64String(System.Text.Encoding.UTF8.GetBytes(data_));
                 }
                 catch (Exception e)
                 {
                     await AppState.ChangedStateUploadProgressBar(false);
                     throw new BcephalException("Unable to serialize : " + dataTra, e);
                 }
             };

            AppState.ShowLoadingStatus();
            await AppState.ChangedStateUploadProgressBar(true);
            await JSRuntime.InvokeVoidAsync("console.log", "try to upload data");
            SocketJS Socket = new SocketJS(WebSocketAddress, CallBackUpload, JSRuntime, AppState);
            string folder = "upload_data_" + new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds();
            Socket.BinaryTpe = "arraybuffer";
            Socket.SendHandler += async () =>
            {
                AppState.HideLoadingStatus();
                Socket.setBinaryTpe(null);

                double maxreader = 5 * 1024;
                long maxFileSize = 1024 * 1024 * 1024;
                byte[] bytes_ = new byte[((long)maxreader)];
                bool first = true;
                ulong index = 0;
                Stream stream = file.OpenReadStream(maxFileSize);
                long currentParentPosition = 0;
                long parentCount = file.Size / (long)maxreader;
                long interValValue = parentCount / 100;
                while (maxreader > 0)
                {
                    maxreader = await stream.ReadAsync(bytes_);
                    await Socket.sendBinary(GetDataTransfert(file.Name, bytes_, folder, first ? Models.socket.Decision.NEW : Models.socket.Decision.CONTINUE));
                    
                        currentParentPosition++;
                        if(interValValue == 0)
                        {
                            interValValue = 1;
                        }
                        if (currentParentPosition % interValValue == 0)
                        {
                        await AppState.SetParentProgressbar(parentCount, currentParentPosition);
                        }                        
                   
                    index++;
                    first = false;
                    bytes_ = new byte[((long)maxreader)];
                }
                stream.Close();
                await Socket.sendBinary(GetDataTransfert(file.Name, new byte[0], folder, Models.socket.Decision.END));
                await Socket.sendBinary(GetDataTransfert("clos", new byte[0], folder, Models.socket.Decision.CLOSE));
            };
            if (file != null && file.Size > 0)
            {
                bool valueClose = false;
                bool valueError = false;
                Socket.CloseHandler += async () =>
                {
                    if (!valueClose && !valueError)
                    {
                        AppState.HideLoadingStatus();
                        // toastService.ShowSuccess(AppState["Upload.data.error.message"]);                       
                    }
                    await AppState.ChangedStateUploadProgressBar(false);
                };

                Socket.ErrorHandler +=  async (errorMessage) =>
                {
                    if (!valueError)
                    {
                        AppState.HideLoadingStatus();
                        toastService.ShowError((string)errorMessage, AppState["Upload.data.error.message"]);
                        valueError = true;
                    }
                    await AppState.ChangedStateUploadProgressBar(false);
                };
                await connectiooSocketJs.Invoke(Socket);
            }
            else
            {
                toastService.ShowWarning(AppState["empty.input.files"], AppState["warning"]);
            }
        }

        public static async Task<string> Uplaod<P, B>(this IBrowserFile file, Service<P, B> service, AppState AppState)
        {
            string folder_ = "upload_data_" + new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds();
            string itemsLoaded = null;
            await AppState.ChangedStateUploadProgressBar(true);
            Stream stream = file.OpenReadStream(file.Size);
            double maxreader = 5 * 1024 * 1024;
            if (file.Size < maxreader)
            {
                maxreader = file.Size;
            }
            byte[] bytes_ = new byte[((long)maxreader)];
            DataTransfert response = null;            
            long currentParentPosition = 0;
            long parentCount = (file.Size / (long)maxreader) + 1;
            while (maxreader > 0)
            {           
                maxreader = await stream.ReadAsync(bytes_).AsTask();
                if (maxreader > 0)
                {
                    Decision decision = response == null ? Decision.NEW : Decision.CONTINUE;
                    string path = response == null ? null : response.RemotePath;
                    response = await service.Upload(new DataTransfert() { Data = bytes_, DataType = DataType.EXCEL, Name = file.Name, Folder = folder_, decision = decision, RemotePath = path });
                    currentParentPosition++;
                    await AppState.SetParentProgressbar(parentCount, currentParentPosition);
                    if (response != null && itemsLoaded == null)
                    {
                        itemsLoaded = response.RemotePath;
                    }
                }
                bytes_ = new byte[((long)maxreader)];
            }
            stream.Close();
            await AppState.ChangedStateUploadProgressBar(false);
            return itemsLoaded;
        }

        public static async Task<ObservableCollection<Dictionary<string, string>>> UplaodAll<P, B>(this IEnumerable<IBrowserFile> files, Service<P, B> service, AppState AppState)
        {
            string folder = "upload_data_" + new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds();
            ObservableCollection<Dictionary<string, string>> itemsLoadeds = new ObservableCollection<Dictionary<string, string>>();
            long currentParentPosition = 0;
            long parentCount = files.Count();
            await AppState.ChangedStateUploadProgressBar(true, true);
            foreach (var file in files)
            {
                currentParentPosition++;
                await AppState.SetParentProgressbar(parentCount, currentParentPosition);
                Dictionary<string, string> disc = new Dictionary<string, string>();                
                Stream stream = file.OpenReadStream(file.Size);
                double maxreader = 5 * 1024 * 1024;
                if(file.Size < maxreader)
                {
                    maxreader = file.Size;
                }
                byte[] bytes_ = new byte[((long)maxreader)];
                DataTransfert response = null;
                long currentChildrenPosition = 0;
                long ChildrenCount = (file.Size / (long)maxreader) + 1;
                string itemsLoaded = null;
                while (maxreader > 0)
                {
                    maxreader = await stream.ReadAsync(bytes_).AsTask();
                    if (maxreader > 0)
                    {
                        Decision decision = response == null ? Decision.NEW : Decision.CONTINUE;
                        string path = response == null ? null : response.RemotePath;
                        response = await service.Upload(new DataTransfert() { Data = bytes_, DataType = DataType.EXCEL, Name = file.Name, Folder = folder, decision = decision, RemotePath = path });
                        currentChildrenPosition++;
                        await AppState.SetChildrenProgressbar(ChildrenCount, currentChildrenPosition);
                        if (response != null && itemsLoaded == null)
                        {
                            await AppState.SetParentProgressbar(parentCount, currentParentPosition);
                            itemsLoaded = response.RemotePath;
                        }
                    }
                    bytes_ = new byte[((long)maxreader)];
                }
                stream.Close();
                disc.Add(file.Name, itemsLoaded);
                itemsLoadeds.Add(disc);
            }
            await AppState.ChangedStateUploadProgressBar(false);
            return itemsLoadeds;
        }


        public static async Task<string> Uplaod<P, B>(this IBrowserFile file, Service<P, B> service, string uri)
        {
            Func<byte[], string, string, double, Task<string>> UploadHandler = async (data, folder, fileName, length) =>
            {
                HttpRequestMessage httpRequest;
                var form = new MultipartFormDataContent();
                var fileContent = new ByteArrayContent(data);
                var fileContentString = new StringContent(service.Serialize(fileName));
                var fileContentFolder = new StringContent(service.Serialize(folder));
                var fileContentLength = new StringContent(service.Serialize(length));
                form.Headers.ContentType.MediaType = "multipart/form-data";
                fileContent.Headers.ContentType = MediaTypeHeaderValue.Parse("application/octet-stream");
                fileContentString.Headers.ContentType = MediaTypeHeaderValue.Parse("application/json");
                fileContentFolder.Headers.ContentType = MediaTypeHeaderValue.Parse("application/json");
                fileContentLength.Headers.ContentType = MediaTypeHeaderValue.Parse("application/json");

                fileContentString.Headers.ContentDisposition = new ContentDispositionHeaderValue("form-data")
                {
                    Name = "fileName_",
                };

                fileContentFolder.Headers.ContentDisposition = new ContentDispositionHeaderValue("form-data")
                {
                    Name = "folder",
                };

                fileContentLength.Headers.ContentDisposition = new ContentDispositionHeaderValue("form-data")
                {
                    Name = "length",
                };

                fileContent.Headers.ContentDisposition = new ContentDispositionHeaderValue("form-data")
                {
                    FileName = fileName,
                    Name = "chunk",
                };

                form.Add(fileContent, "chunk");
                form.Add(fileContentString, "fileName_");
                form.Add(fileContentFolder, "folder");
                form.Add(fileContentLength, "length");


                httpRequest = new HttpRequestMessage()
                {
                    Content = form,
                    Method = HttpMethod.Post,
                    RequestUri = new Uri(service.RestClient.BaseAddress + service.ResourcePath + uri)
                };
                try
                {
                    if (service.RestClient.DefaultRequestHeaders != null)
                    {
                        service.RestClient.DefaultRequestHeaders.Remove("Accept-Language");
                        service.RestClient.DefaultRequestHeaders.Add("Accept-Language", UserService.DefaultLanguage);
                    }
                    HttpResponseMessage queryResult = await service.RestClient.SendAsync(httpRequest);
                    await service.ValidateResponse(queryResult);
                    String response = await queryResult.Content.ReadAsStringAsync();
                    return response;
                }
                catch (Exception e)
                {
                    if (e is BcephalException)
                    {
                        throw e;
                    }
                    throw new BcephalException(e.Message, e);
                }
            };
            string folder = "upload_data_" + new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds();
            string itemsLoaded = null;
            double maxreader = 5 * 1024;
            long maxFileSize = 1024 * 1024 * 1024;
            byte[] bytes_ = new byte[((long)maxreader)];
            Stream stream = file.OpenReadStream(maxFileSize);
            while (maxreader > 0)
            {
                maxreader = await stream.ReadAsync(bytes_);
                itemsLoaded = await UploadHandler.Invoke(bytes_, folder, file.Name, maxreader);
                bytes_ = new byte[((long)maxreader)];
            }
            stream.Close();
            return itemsLoaded;
        }



        public static  async Task<string> BuildSpreadSheetInfo<P, B>(this IBrowserFile file, Service<P, B> service, string uri)
        {
            Func<byte[], string, string, double, Task<string>> UploadHandler = async (data, folder, fileName, length) =>
            {
                HttpRequestMessage httpRequest;
                var form = new MultipartFormDataContent();
                var fileContent = new ByteArrayContent(data);
                var fileContentString = new StringContent(service.Serialize(fileName));
                var fileContentFolder = new StringContent(service.Serialize(folder));
                var fileContentLength = new StringContent(service.Serialize(length));
                form.Headers.ContentType.MediaType = "multipart/form-data";
                fileContent.Headers.ContentType = MediaTypeHeaderValue.Parse("application/octet-stream");
                fileContentString.Headers.ContentType = MediaTypeHeaderValue.Parse("application/json");
                fileContentFolder.Headers.ContentType = MediaTypeHeaderValue.Parse("application/json");
                fileContentLength.Headers.ContentType = MediaTypeHeaderValue.Parse("application/json");

                fileContentString.Headers.ContentDisposition = new ContentDispositionHeaderValue("form-data")
                {
                    Name = "fileName_",
                };

                fileContentFolder.Headers.ContentDisposition = new ContentDispositionHeaderValue("form-data")
                {
                    Name = "folder",
                };

                fileContentLength.Headers.ContentDisposition = new ContentDispositionHeaderValue("form-data")
                {
                    Name = "length",
                };

                fileContent.Headers.ContentDisposition = new ContentDispositionHeaderValue("form-data")
                {
                    FileName = fileName,
                    Name = "chunk",
                };

                form.Add(fileContent, "chunk");
                form.Add(fileContentString, "fileName_");
                form.Add(fileContentFolder, "folder");
                form.Add(fileContentLength, "length");


                httpRequest = new HttpRequestMessage()
                {
                    Content = form,
                    Method = HttpMethod.Post,
                    RequestUri = new Uri(service.RestClient.BaseAddress + service.ResourcePath + uri)
                };
                try
                {
                    if (service.RestClient.DefaultRequestHeaders != null)
                    {
                        service.RestClient.DefaultRequestHeaders.Remove("Accept-Language");
                        service.RestClient.DefaultRequestHeaders.Add("Accept-Language", UserService.DefaultLanguage);
                    }
                    HttpResponseMessage queryResult = await service.RestClient.SendAsync(httpRequest);
                    await service.ValidateResponse(queryResult);
                    String response = await queryResult.Content.ReadAsStringAsync();
                    return response;
                }
                catch (Exception e)
                {
                    if (e is BcephalException)
                    {
                        throw e;
                    }
                    throw new BcephalException(e.Message, e);
                }
            };
            string folder = "upload_data_" + new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds();
            Stream stream = file.OpenReadStream(file.Size);
            var ms = new MemoryStream();
            await stream.CopyToAsync(ms);
            string response = await UploadHandler.Invoke(ms.ToArray(), folder, file.Name, file.Size);
            return response;
        }
        public static async Task<ObservableCollection<string>> UplaodAll<P, B>(this IEnumerable<IBrowserFile> files, Service<P, B> service, string uri)
        {
            Func<byte[], string, string, double, Task<string>> UploadHandler = async (data, folder, fileName, length) =>
            {
                HttpRequestMessage httpRequest;
                var form = new MultipartFormDataContent();
                var fileContent = new ByteArrayContent(data);
                var fileContentString = new StringContent(service.Serialize(fileName));
                var fileContentFolder = new StringContent(service.Serialize(folder));
                var fileContentLength = new StringContent(service.Serialize(length));
                form.Headers.ContentType.MediaType = "multipart/form-data";
                fileContent.Headers.ContentType = MediaTypeHeaderValue.Parse("application/octet-stream");
                fileContentString.Headers.ContentType = MediaTypeHeaderValue.Parse("application/json");
                fileContentFolder.Headers.ContentType = MediaTypeHeaderValue.Parse("application/json");
                fileContentLength.Headers.ContentType = MediaTypeHeaderValue.Parse("application/json");

                fileContentString.Headers.ContentDisposition = new ContentDispositionHeaderValue("form-data")
                {
                    Name = "fileName_",
                };

                fileContentFolder.Headers.ContentDisposition = new ContentDispositionHeaderValue("form-data")
                {
                    Name = "folder",
                };

                fileContentLength.Headers.ContentDisposition = new ContentDispositionHeaderValue("form-data")
                {
                    Name = "length",
                };

                fileContent.Headers.ContentDisposition = new ContentDispositionHeaderValue("form-data")
                {
                    FileName = fileName,
                    Name = "chunk",
                };

                form.Add(fileContent, "chunk");
                form.Add(fileContentString, "fileName_");
                form.Add(fileContentFolder, "folder");
                form.Add(fileContentLength, "length");
                httpRequest = new HttpRequestMessage()
                {
                    Content = form,
                    Method = HttpMethod.Post,
                    RequestUri = new Uri(service.RestClient.BaseAddress + service.ResourcePath + uri)
                };
                try
                {
                    if (service.RestClient.DefaultRequestHeaders != null)
                    {
                        service.RestClient.DefaultRequestHeaders.Remove("Accept-Language");
                        service.RestClient.DefaultRequestHeaders.Add("Accept-Language", UserService.DefaultLanguage);
                    }
                    HttpResponseMessage queryResult = await service.RestClient.SendAsync(httpRequest);
                    await service.ValidateResponse(queryResult);
                    String response = await queryResult.Content.ReadAsStringAsync();
                    return response;
                }
                catch (Exception e)
                {
                    if (e is BcephalException)
                    {
                        throw e;
                    }
                    throw new BcephalException(e.Message, e);
                }
            };
            string folder = "upload_data_" + new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds();
            ObservableCollection<string> itemsLoadeds = new ObservableCollection<string>();
            foreach (var file in files)
            {
                double maxreader = 5 * 1024;
                long maxFileSize = 1024 * 1024 * 1024;
                byte[] bytes_ = new byte[((long)maxreader)];
                Stream stream = file.OpenReadStream(maxFileSize);
                while (maxreader > 0)
                {
                    maxreader = await stream.ReadAsync(bytes_);
                    itemsLoadeds.Add(await UploadHandler.Invoke(bytes_, folder, file.Name, maxreader));
                    bytes_ = new byte[((long)maxreader)];
                }
                stream.Close();
            }
            return itemsLoadeds;
        }
    }

    public class ResData
    {
        public ResData()
        {
        }

        public ResData(string path, string data)
        {
            Path = path;
            Data = data;
        }

        public string Path { get; set; }
        public string Data { get; set; }
    }
}

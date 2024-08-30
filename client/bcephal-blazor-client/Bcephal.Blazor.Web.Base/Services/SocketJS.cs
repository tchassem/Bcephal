using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Bcephal.Models.Exceptions;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Threading;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class SocketJS
    {
        private readonly Uri BaseAddress;
        private Uri wsRequestPath;
        private Action<object, object> Handler { get; set; }
        public Action SendHandler { get; set; }
        public Action<object> ErrorHandler { get; set; }
        public Action CloseHandler { get; set; }
        private AppState AppState { get; set; }
        public IJSRuntime JSRuntime { get; set; }
        public string BinaryTpe { get; set; }
        public string Id { get; private set; } = Guid.NewGuid().ToString("d");
        public FullyProgressbar FullyProgressbar { get; set; }
        DotNetObjectReference<SocketJS> dotNetReference;

        private bool createdCallBackEvent = false;
        private bool CreatedCallBackEventAndProgressbar = false;
        private bool Stop = false;
        private bool DisplayProgressBar { get; set; }

        public string  CallBackJsFunction { get; set; }
        public string CallBackJsFunctionKey { get; set; }

        public SocketJS(WebSocketAddress WebSocketAddress, 
            Action<object, object> Handler,IJSRuntime JSRuntime_, AppState appState = null, 
            bool displayProgressBar = false, bool createdCallBackEventAndProgressbar = false)
        {
            BaseAddress = WebSocketAddress.BaseAddress;
            AppState = appState;
            JSRuntime = JSRuntime_;
            DisplayProgressBar = displayProgressBar;
            CreatedCallBackEventAndProgressbar = createdCallBackEventAndProgressbar;
            if (Handler != null)
            {
                this.Handler += Handler;
                if (!createdCallBackEventAndProgressbar)
                {
                    createdCallBackEvent = true;
                }                
            }
            dotNetReference = DotNetObjectReference.Create(this);
        }
        

        public async Task ConnectAsync(string path, string header = null)
        {
            path += "?" + CustomHttpClientHandler.TENANT_HTTP_HEADER + "=" + CustomHttpClientHandler.TENANT_ID;
            if (!string.IsNullOrWhiteSpace(header))
            {
                path += "&" + CustomHttpClientHandler.CUSTOM_HTTP_HEADER_SOCKET + "=" + header;
            }            

            Uri.TryCreate(BaseAddress, path, out wsRequestPath);
            await JSRuntime.InvokeVoidAsync("WebSocket_.new", dotNetReference, Id, wsRequestPath, "AfterOpen", BinaryTpe);
            await JSRuntime.InvokeVoidAsync("WebSocket_.closeEvent", dotNetReference, Id, "closeEvent");
            await JSRuntime.InvokeVoidAsync("WebSocket_.errorEvent", dotNetReference, Id, "ErrorEvent");
            if (DisplayProgressBar)
            {
                await AppState.AddProgressBars(this);
                await JSRuntime.InvokeVoidAsync("WebSocket_.message", dotNetReference, Id, "message", createdCallBackEvent,
                        FullyProgressbar.progressbarDivId, FullyProgressbar.progressbarDivSubId, FullyProgressbar.dotNetReference,
                        CreatedCallBackEventAndProgressbar, CallBackJsFunction, CallBackJsFunctionKey);
                FullyProgressbar.StopSucces = CloseHandler;
                FullyProgressbar.StopError = ErrorHandler;
            }
            else
            {
                await JSRuntime.InvokeVoidAsync("WebSocket_.message", dotNetReference, Id, "message", createdCallBackEvent, null, null, null, false,CallBackJsFunction, CallBackJsFunctionKey);
            }
        }

        [JSInvokable("AfterOpen")]
        public void AfterOpen()
        {
            SendHandler?.Invoke();
        }

        public void send(object data)
        {
            JSRuntime.InvokeVoidAsync("WebSocket_.send", dotNetReference, Id, data);
        }

        public async Task sendBinary(object data)
        {
          await  JSRuntime.InvokeVoidAsync("WebSocket_.sendBinary", dotNetReference, Id, data);
        }

        public void close(object data)
        {
            JSRuntime.InvokeVoidAsync("WebSocket_.close", dotNetReference, Id);
        }

        public void setBinaryTpe(string type)
        {
            JSRuntime.InvokeVoidAsync("WebSocket_.setBinaryType", Id, type);
        }

        [JSInvokable("closeEvent")]
        public async void closeEvent()
        {
            await JSRuntime.InvokeVoidAsync("console.log", "call closeEvent");            
            CloseHandler?.Invoke();
            if (DisplayProgressBar)
            {
                await FullyProgressbar.stop(true);
            }
        }

        [JSInvokable("ErrorEvent")]
        public async void ErrorEvent(string error)
        {
            await JSRuntime.InvokeVoidAsync("console.log", "call ErrorEvent");
            ErrorHandler?.Invoke(error);
            if (DisplayProgressBar)
            {
                await FullyProgressbar.stop(false, error);
            }
        }


        [JSInvokable("message")]
        public Task message(object data)
        {
            try
            {
                Handler?.Invoke(this, data.ToString());
            }
            catch (Exception)
            {
                try
                {
                    JsonElement json = (JsonElement)data;
                    var socketObj = JsonConvert.DeserializeObject<byte[]>(json.ToString());
                    //JSRuntime.InvokeVoidAsync("console.log", "bytes => ", socketObj.Data);
                    Handler?.Invoke(this, socketObj);
                }
                catch (Exception)
                {
                    Handler?.Invoke(this, data);
                }
            }

            return Task.CompletedTask;
        }

        public  string Base64Encode(string plainText)
        {
            var plainTextBytes = System.Text.Encoding.UTF8.GetBytes(plainText);
            return System.Convert.ToBase64String(plainTextBytes);
        }

        public  string Base64Decode(string base64EncodedData)
        {
            var base64EncodedBytes = System.Convert.FromBase64String(base64EncodedData);
            return System.Text.Encoding.UTF8.GetString(base64EncodedBytes);
        }
    }

    public class SocketObj<T>
    {
        public T Data { get; set; }

    }
}

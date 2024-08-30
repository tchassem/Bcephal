using System;
using System.IO;
using System.Net.WebSockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class CustomWebSocket
    {
        private readonly ClientWebSocket webSocket;

        private readonly Uri BaseAddress;
        private readonly CancellationTokenSource disposalTokenSource;
        private Uri wsRequestPath;
        bool stopped;
        private Action<object,object> Handler { get; set; }

        public CustomWebSocket(WebSocketAddress WebSocketAddress, ClientWebSocket webSocket,
            CancellationTokenSource disposalTokenSource,
            Action<object, object> Handler)
        {
            BaseAddress = WebSocketAddress.BaseAddress;
            this.webSocket = webSocket;
            this.disposalTokenSource = disposalTokenSource;
            if (Handler != null)
            {
                this.Handler += Handler;
            }
        }
        public async Task ReceiveAsync()
        {
            try
            {
                bool closed = false;
                while (!closed /*webSocket.State == WebSocketState.Open*/)
                {
                    WebSocketReceiveResult response;
                    String response_ = "";
                    do
                    {
                        var content = new byte[1024];
                        var buffer = new ArraySegment<byte>(content);
                        response = await webSocket.ReceiveAsync(buffer, disposalTokenSource.Token);
                        response_ += Encoding.UTF8.GetString(content, 0, response.Count);
                        closed = response.CloseStatus != null;
                    }while (!response.EndOfMessage);
                     Handler?.Invoke(this, response_);
                   // Console.WriteLine(response_);
                    if (stopped)
                    {
                        var sendBody = Encoding.UTF8.GetBytes("STOP");
                        await webSocket.SendAsync(new ArraySegment<byte>(sendBody), WebSocketMessageType.Text, true, disposalTokenSource.Token);                        
                    }
                }
            }
            catch { }
            finally
            {
                // Clean up by disposing the WebSocket.
                if (webSocket != null)
                {
                    webSocket.Dispose();
                }

            }
        }

        public void Stop()
        {
            stopped = true;
        }


        public async Task ReceiveByteAsync()
        {
            try
            {
                bool closed = false;
                while (!closed)
                {
                    WebSocketReceiveResult response;
                    var ms = new MemoryStream();
                    do
                    {
                        var buffer = new ArraySegment<byte>(new byte[2024]);
                        response = await webSocket.ReceiveAsync(buffer, disposalTokenSource.Token);
                        ms.Write(buffer.Array, 0, response.Count);
                        closed = response.CloseStatus != null;
                    } while (!response.EndOfMessage);
                    ms.Seek(0, SeekOrigin.Begin);
                    Handler?.Invoke(this, ms.ToArray());
                    ms.Close();
                    if (stopped)
                    {
                        var sendBody = Encoding.UTF8.GetBytes("STOP");
                        await webSocket.SendAsync(new ArraySegment<byte>(sendBody), WebSocketMessageType.Text, true, disposalTokenSource.Token);
                    }
                }
            }
            catch { }
            finally
            {
                // Clean up by disposing the WebSocket.
                if (webSocket != null)
                {
                    webSocket.Dispose();
                }

            }
        }
        public Task ConnectAsync(string path)
        {
            path += "?" + CustomHttpClientHandler.TENANT_HTTP_HEADER + "=" + CustomHttpClientHandler.TENANT_ID;
            Uri.TryCreate(BaseAddress, path, out wsRequestPath);
            return webSocket.ConnectAsync(wsRequestPath, disposalTokenSource.Token);
        }
        public Task SendStringAsync_(string data, bool endOfMessage = true)
        {
            var encoded = Encoding.UTF8.GetBytes(data);
            var buffer = new ArraySegment<byte>(encoded, 0, encoded.Length);
            return webSocket.SendAsync(buffer, WebSocketMessageType.Text, endOfMessage: endOfMessage, disposalTokenSource.Token);
        }

        public Task SendbyteAsync_(byte[] data, bool endOfMessage = true)
        {
            var buffer = new ArraySegment<byte>(data, 0, data.Length);
            return webSocket.SendAsync(buffer, WebSocketMessageType.Binary, endOfMessage: endOfMessage, disposalTokenSource.Token);
        }

        //public async Task SendbyteAsync(Action<object> action, byte[] data,bool endOfMessage = true)
        //{

        //     SendbyteAsync_(data, endOfMessage);
        //    if (action != null)
        //    {
        //        await Receive(action);
        //    }
        //}
        public async Task SendStringAsync(string data, string path, bool canOpen = true, bool endOfMessage = true)
        {
            if (canOpen)
            {
                await ConnectAsync(path);
            }
            await SendStringAsync_(data, endOfMessage);
            await ReceiveAsync();
        }
    }
}

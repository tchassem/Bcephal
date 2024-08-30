window.WebSocket_ = {
    new: function (dotNetObject, key, url, callBackOpen, type) {
        // created web socket
        const ws = new WebSocket(url);
        //console.log('Call ws key ', key);
        //console.log('Call ws ', ws);
        if (type) {
            ws.binaryType = type;
           // console.log('Call after set type ', ws);
        }
        // created open listner
        ws.onopen = function (event) {
           // console.log('Open connection ', event);
            if (dotNetObject) {
                //console.log('Call collBack handler ', event);
                dotNetObject.invokeMethodAsync(callBackOpen);
            }
        };
        $.data[key] = ws;
        return;
    }
    ,// set binary type
    setBinaryType: function (key, type) {
       // console.log('Call before set type ', $.data[key]);
        if ($.data[key] && type) {
            $.data[key].binaryType = type;
         //   console.log('Call after set type ', $.data[key]);
        }
    },
    sendBinary: function (dotNetObject, key, message) {
        if ($.data[key]) {
            // send bytes message
            $.data[key].send(Uint8Array.from(atob(message), c => c.charCodeAt(0)));
        }
    },// created send method
    send: function (dotNetObject, key, message) {
        if ($.data[key]) {
            // send text message
            $.data[key].send(message);
        }
    },// created close websocket method
    close: function (dotNetObject, key) {
        var open = 1;
        if ($.data[key] && $.data[key].readyState == open) {
            $.data[key].close();
            $.data[key] = null;
        }
        return;
    },// created closed event from server 
    closeEvent: function (dotNetObject, key, callBackName) {
        if ($.data[key]) {
            $.data[key].onclose = function (event) {
                dotNetObject.invokeMethodAsync(callBackName);
            }
        }
        return;
    },// error event from server
    errorEvent: function (dotNetObject, key, callBackName) {
        if ($.data[key]) {
            $.data[key].onerror = function (event) {
                dotNetObject.invokeMethodAsync(callBackName, event);
            }
        }
        return;
    },// created listner to receive message
    message: function (dotNetObject, key, callBackName, canDotCallBackEvent, parentId, childId, progressBardotNetObject, canDotCallBackEventProgressbar, CallBackJsFunction, CallBackJsFunctionKey, fileName) {
        if ($.data[key]) {
            $.data[key].onmessage = function (event) {
                /*console.log("event.data=>", event.data);*/
                if (dotNetObject) {
                    if (canDotCallBackEvent && !canDotCallBackEventProgressbar) {//if callback dotnet method
                        dotNetObject.invokeMethodAsync(callBackName, event.data);
                    } else {
                        window.WebSocket_.buildProgressBar(dotNetObject, parentId, childId, progressBardotNetObject, event.data, callBackName, canDotCallBackEventProgressbar, CallBackJsFunction, CallBackJsFunctionKey, fileName)
                    }
                }
                return;
            };
        }
        return;
    },
    buildProgressBar: async function (dotNetObject, parentId, childId, progressBardotNetObject, data_, methodName_, canDotCallBackEventProgressbar, CallBackJsFunction, CallBackJsFunctionKey, fileName) {
        const data = JSON.parse(data_);
        //console.log("parentId=>", parentId);
        if (parentId && data) {// call progress bar
            const executeF = async function (id_, dat, isEndParent, canDotCallBackEventProg, dotNetObject, CallBackJsFunction_, CallBackJsFunctionKey_) {
                if (progressBardotNetObject && (dat.taskInError || dat.taskEnded)) {
                    window.progressBar__(id_, dat, progressBardotNetObject, "stop", true && isEndParent,null);
                    if (dat.taskInError) {
                        dotNetObject.invokeMethodAsync("ErrorEvent", dat.message);
                    }
                } else {
                    window.progressBar__(id_, dat, progressBardotNetObject, methodName_, false, canDotCallBackEventProg, dotNetObject, CallBackJsFunction_, CallBackJsFunctionKey_, fileName);
                }
            };
            if (childId && data.currentSubTask) {
               // console.log("childId=>", data.currentSubTask);
                executeF(parentId, data, false, canDotCallBackEventProgressbar, dotNetObject, CallBackJsFunction, CallBackJsFunctionKey, fileName);
                executeF(childId, data.currentSubTask, (data.taskInError || data.taskEnded), canDotCallBackEventProgressbar, dotNetObject, CallBackJsFunction, CallBackJsFunctionKey, fileName);
            } else {
                executeF(parentId, data, true, canDotCallBackEventProgressbar, dotNetObject, CallBackJsFunction, CallBackJsFunctionKey, fileName);
            }
        }
        return;
    },
};
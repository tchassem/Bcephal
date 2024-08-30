window.bcephal = {
    focusElement: function (element) {
        if (element instanceof HTMLElement) {
           setTimeout(function () {
                element.focus();
                element.select();
            }, 100);
        } else
            console.log("Cannot focus this element !!");
    },
    focusById: function (id) {
        var element = document.getElementById(id);
        if (element) {
            setTimeout(function () {
                element.focus();
                element.select();
            }, 100);
            
        } else
            console.log("Cannot focus this because it's not a HTML element !!");
    },

    focusByRef: function (element) {
            element.focus();
    },
};

window.saveProjectAsFile = function (filename, fileContent) {
  var link = document.createElement("a");
    link.download = filename;
  link.href =
    "data:text/plain;charset=utf-8," + encodeURIComponent(fileContent);
    //
    document.body.appendChild(link); // Needed for Firefox
    link.click();
    document.body.removeChild(link);
};

window.saveAsFileBytes = function (filename, fileContent) {
    var link = document.createElement("a");
    // Convert base64 string to numbers array.
    const numArray = atob(fileContent).split('').map(c => c.charCodeAt(0));
    // Convert numbers array to Uint8Array object.
    const uint8Array = new Uint8Array(numArray);
    // Wrap it by Blob object.
     const blob = new Blob([uint8Array], { type: 'application/octetstream' });
    // Create "object URL" that is linked to the Blob object.
    const url = URL.createObjectURL(blob);    
    link.setAttribute("download", filename);
    link.setAttribute("href", url);
    document.body.appendChild(link); // Needed for Firefox
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
};
window.resumeFileAfterDownload = function resumeFileAfterDownload(data_, key,filename) {
    const data = JSON.parse(data_);
    console.log("Call Back Save Data", key, filename);
    if (!$.data[key] && data) {
        $.data[key] = data.data;
        console.log("Data init ");
    } else
        if ($.data[key] && data) {
            $.data[key] += data.data;
            console.log("Data ++");
    }
    if (data && data.decision && data.decision === "END") {
        console.log("Call saveAsFileBytes -- ");
        saveAsFileBytes(filename, $.data[key]);
        $.data[key] = null;
    }
};
//window.saveAsFile = function downloadUrl2(url) {
//    window.open(url, '_self');
//};

//window.saveAsFile2 = function downloadURI3(uri, filename) {
//    var link = document.createElement("a");
//    link.setAttribute("download", filename);
//    link.setAttribute("href", uri);
//    document.body.appendChild(link); // Needed for Firefox
//    link.click();
//    document.body.removeChild(link);
//};

window.blazorCulture = {
    WriteCookie: function setCookie(cname, cvalue, exdays) {
        const d = new Date();
        d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
        let expires = "expires=" + d.toUTCString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
        //console.log("From Set document.cookie =>", document.cookie);
    },
    ReadCookie: function (cname) {
        let name = cname + "=";
        let ca = document.cookie.split(';');
        for (let i = 0; i < ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
               // console.log("From Get document.cookie =>", c.substring(name.length, c.length));
                return c.substring(name.length, c.length);
            }
        }
        //console.log("From Get document.cookie =>");
        return "";
    },
};

window.stateManager = {
    save: function (key, str) {
        localStorage[key] = str;
    },
    load: function (key) {
        return localStorage[key];
    },
    delete: function (key) {
    return (localStorage[key] = null);
    },
    saveAppState_: function (dotNetObject, confirmationMessage) {
        var unloadEvent = function (e) {
            dotNetObject.invokeMethodAsync("saveAppState");
            (e || window.event).returnValue = confirmationMessage; 
            return confirmationMessage;
        };
        window.addEventListener("beforeunload", unloadEvent);
  },
};

window.ChangeIconFilterPeriod = function () {
    var iconclass = $("#expand");

    if (iconclass.hasClass("fa fa-expand")) {
        iconclass.removeClass("fa fa-expand");
        iconclass.addClass("fa fa-compress");
    } else {
        iconclass.removeClass("fa fa-compress");
        iconclass.addClass("fa fa-expand");
    }
};

window.ChangeIconFilterMeasure = function () {
    var iconclass = $("#expandmeasure");

    if (iconclass.hasClass("fa fa-expand")) {
        iconclass.removeClass("fa fa-expand");
        iconclass.addClass("fa fa-compress");
    } else {
        iconclass.removeClass("fa fa-compress");
        iconclass.addClass("fa fa-expand");
    }
};

window.ChangeIconFilterAttribute = function () {
    var iconclass = $("#expandattribute");

    if (iconclass.hasClass("fa fa-expand")) {
        iconclass.removeClass("fa fa-expand");
        iconclass.addClass("fa fa-compress");
    } else {
        iconclass.removeClass("fa fa-compress");
        iconclass.addClass("fa fa-expand");
    }
};

window.openTag = function(className,divId) {
    var offset;
    var elements = document.getElementsByClassName(className);
    for (offset = 0; offset < elements.length; offset++) {
        elements[offset].style.display = "none";
        }
    document.getElementById(divId).style.display = "block";
};

window.ReadFileUpload = async function readText() {
  const file = document.getElementById("fileItem").files[0];
    const text = await file.text();
    let reader = new FileReader();
    reader.readAsArrayBuffer(file);
    reader.onload = function() {
        console.log(reader.result);
        document.getElementById("output").innerText = reader.result;
    };
  
    reader.onerror = function() {
      console.log(reader.error);
    };
  //  document.getElementById("output").innerText = text
        };

window.OpenFileUpload = async function openText() {
  // Selected folder's absolute path:
  let zip = new JSZip();
  let fileInput = document.getElementById("tabpageTemplate");
  let files = [];

  // get all selected file

  $.each(fileInput.files, function (i, file) {
      let reader = new FileReader();
    reader.readAsArrayBuffer(file);
    reader.onload = function () {
        files.push(file);
        console.log(reader.result);
        zip.file(file.name, reader.result);      

     };
    reader.onerror = function () {
      console.log(reader.error);
    };
  });
    console.log(zip.files);
  zip.generateAsync({ type: "base64" }).then(function (content) {
    location.href = "data:application/zip;base64," + content;
  });
 
};

window.OpenFile = async (dotnet, method) => {

    var filesWidget = document.getElementById('selectedFiles');
    var res = [];
    
    $.each(filesWidget.files, function (i, file) {

        var reader = new FileReader();
        reader.readAsDataURL(file);

        reader.onload = function () {

            var obj = {};
            obj.path = file.webkitRelativePath;
            obj.data = reader.result;

            res.push(obj);

            if ((filesWidget.files.length - 1) == i && dotnet) {
                dotnet.invokeMethodAsync(method, JSON.stringify(res));
            }

        };
        reader.onerror = function (error) {
            console.log('Error: ', error);
        };
    });
}

window.ChangeIconDragDrop = (id) => {

    var item = "#" + id + " " + "i:first-child";

    var iconchild = $(item);

    if (iconchild != undefined) {
        if (iconchild.hasClass("bi-chevron-right")) {
            iconchild.removeClass("bi-chevron-right");
            iconchild.addClass("bi-chevron-down");
        }
        else {
            iconchild.removeClass("bi-chevron-down");
            iconchild.addClass("bi-chevron-right");
        }
    }
};

window.progressBar = async function (progressbar, total, ResourceCount, dotNetObject_,methodName_, state) {
    var id = setInterval(frame, 10);
    var offset = 0;
    var width = 0;
    var methodName = methodName_;
    var dotNetObject = dotNetObject_;
    function frame() {
        if (offset == 1 && width == 99) {
       //     console.log("call stop", progressbar.style.width, width, methodName, dotNetObject);
        }
        if (progressbar && width >= 99 && dotNetObject) {
            console.debug("call stop");
            console.log("call stop000");
            dotNetObject.invokeMethodAsync(methodName, state,null);
            clearInterval(id);
        } else {
            if (progressbar && offset == 0) {
                const value = parseInt((ResourceCount * 100.0) / total);
                const pct = value + '%';
                var label = "" + value + " % (" + ResourceCount + "/" + total + ")";
                width = value;
                progressbar.style.width = pct;
                progressbar.innerHTML = label;
                offset++;
            } else {
                clearInterval(id);
               // console.log("Clear Interval", progressbar.style.width);
            }
        }
    }
};

window.progressBar__2 = async function (progressbarId, total, ResourceCount, dotNetObject_, methodName_, canStop) {
    var id = setInterval(frame, 10);
    var offset = 0;
    var methodName = methodName_;
    var dotNetObject = dotNetObject_;
    var progressbar = document.getElementById(progressbarId);
    function frame() {
        if ((progressbar && canStop && methodName && dotNetObject)) {
            //console.log("callback stop progressBar from js");
            dotNetObject.invokeMethodAsync(methodName, canStop, null);
            clearInterval(id);
        } else {
            if (progressbar && offset == 0) {

                const value = parseInt((ResourceCount * 100.0) / total);
                const pct = value + '%';
                //console.log("ProgressBar from js value", pct);
                var label = "" + value + " % (" + ResourceCount + "/" + total + ")";
                progressbar.style.width = pct;
                progressbar.innerHTML = label;
                offset++;
            } else {
                clearInterval(id);
            }
        }
    }
};
window.progressBar__ = async function (progressbarId, data, dotNetObject_, methodName_, canStop, canDotCallBackEventProgressbar_, dotNetObjectSockeJs_, CallBackJsFunction, CallBackJsFunctionKey, fileName) {
    var id = setInterval(frame, 10);
    var offset = 0;
    var methodName = methodName_;
    var message = data;
    var total = data.stepCount;
    var ResourceCount = data.currentStep;
    var canDotCallBackEventProgressbar = canDotCallBackEventProgressbar_;
    var dotNetObject = dotNetObject_;
    var dotNetObjectSockeJs = dotNetObjectSockeJs_;
    var progressbar = document.getElementById(progressbarId);
    function frame() {
        if ((progressbar && canStop && methodName && dotNetObject)) {
            //console.log("callback stop progressBar from js");
            dotNetObject.invokeMethodAsync(methodName, canStop, null);
            clearInterval(id);
        } else {
            if (progressbar && offset == 0) {
                if (canDotCallBackEventProgressbar && message && dotNetObjectSockeJs && methodName_) {
                    //console.log("callback dotNetObjectSockeJs 0 progressBar from js");
                    if (CallBackJsFunction && CallBackJsFunctionKey) {
                        if (CallBackJsFunction === "resumeFileAfterDownload") {
                            resumeFileAfterDownload(data.message, CallBackJsFunctionKey, fileName);
                        }
                    } else {
                        //console.log("callback dotNetObjectSockeJs 1 progressBar from js");
                        dotNetObjectSockeJs.invokeMethodAsync(methodName, JSON.stringify(message));
                    }
                }
                const value = parseInt((ResourceCount * 100.0) / total);
                const pct = value + '%';
                //console.log("ProgressBar from js value", pct);
                var label = "" + value + " % (" + ResourceCount + "/" + total + ")";
                progressbar.style.width = pct;
                progressbar.innerHTML = label;
                if (progressbar.parentNode) {
                    if (progressbar.parentNode.children.length > 1) {
                        progressbar.parentNode.children[1].innerHTML = label;
                    }
                }
                offset++;
            } else {
                clearInterval(id);
            }
        }
    }
};

//window.TalkToPreventDefault = function () {
//    event.preventDefault();
//};

//window.ContextMenuEvent = {
//    preventDefault: function (dotNetObject, targetId, eventName, callBackName) {
//        window.ContextMenuEvent = {
//            ContextEvent: function (event) {
//                //console.log("Call unloadEvent preventDefault");
//                event.preventDefault();
//                event.stopPropagation();
//                dotNetObject.invokeMethodAsync(callBackName, event);
//            }
//        };
//        //console.log("Call ContextMenuEvent targetId", targetId);
//        var targets = document.getElementById(targetId);
//        if (targets) {
//            targets.addEventListener(eventName, ContextMenuEvent.ContextEvent);
//        }
//    }
//};

var ResourceCount = 0;
window.ResourceCount = ResourceCount;

window.loadResourceCallback = (total, name, response) => {
    console.log("loadResourceCallback");
    if (name.endsWith('.dll')) {
        ResourceCount++;
        const value = parseInt((ResourceCount * 100.0) / total);
        const pct = value + '%';
        const progressbar = document.getElementById('progressbar_');
        progressbar.style.width = pct;
    }
};

window.SplitterCJsFunctions = {
    setPCapture: function (el, p) {
        if (document.getElementById(el) !== null) {
            document.getElementById(el).setPointerCapture(p);
            return true;
        }
        else {
            return false;
        }
    },
    releasePCapture: function (el, p) {
        if (document.getElementById(el) !== null) {
            document.getElementById(el).releasePointerCapture(p);
            GetItemBoundingRectByPosition();
            return true;
        }
        else {
            return false;
        }
    },
    roundingClientRect: function (element_, ElementReference) {
        var element = document.getElementById(element_);
        if (element !== null && element.parentElement !== null) {
            var item = element.parentElement.getBoundingClientRect();
            if (item.width === 0) {                                   
                var elementComponent = document.querySelector("div[BaseModalComponent=\"Item\"]");
                console.log(elementComponent);
                if (elementComponent) {
                    console.log(elementComponent.getBoundingClientRect());
                    console.log(elementComponent.style);
                    console.log(window.getComputedStyle(elementComponent).getPropertyValue("height"));
                    console.log(window.getComputedStyle(elementComponent).getPropertyValue("width"));

                    return elementComponent.getBoundingClientRect();
                }                   
                if (element.parentElement !== null && element.parentElement.parentElement !== null) {
                    return element.parentElement.parentElement.getBoundingClientRect();
                }
            }
            return item;
        }
        else {
            return null;
        }
    },
    roundingClientRect2: function (element_, param) {
        var element = document.getElementById(element_);
        if (element !== null
            && element.parentElement !== null
            && element.parentElement.parentElement !== null) {
            return element.parentElement.parentElement.getBoundingClientRect();
        }
        else {
            return null;
        }
    },
    roundingClientRect3: function (element_, param) {
        var element = document.getElementById(element_);
        if (element !== null
            && element.parentElement !== null
            && element.parentElement.parentElement !== null
            && element.parentElement.parentElement.parentElement !== null) {
            return element.parentElement.parentElement.parentElement.getBoundingClientRect();
        }
        else {
            return null;
        }
    },
    dimension: function (element_, param) {
        var element = document.getElementById(element_);
        if (element !== null && element.parentElement !== null) {
            return element.parentElement.clientHeight;
        }
        else {
            return null;
        }
    },
    DimansionClientRect: function (element_) {
        var element = document.getElementById(element_);
        if (element !== null && element.parentElement !== null) {
            var child = element.getBoundingClientRect();
            var perent = element.parentElement.getBoundingClientRect();
            child.top = child.top - perent.top;
            child.bottom = perent.bottom - child.bottom;
            return child;
        }
        else {
            return null;
        }
    },
    dimension_max_height: function (element_, param) {
        var element = document.getElementById(element_);
        if (element !== null && element.parentElement !== null) {
            //return document.getElementById(element).parentElement.offsetHeight;
            return window.screen.availHeight;
        }
        else {
            return null;
        }
    },
    roundingClientRectDashboardItem: function (ElementReference) {
        var element = document.getElementById(ElementReference);
        if (element !== null && element.parentElement !== null) {
            return element.parentElement.getBoundingClientRect();
        }
        else {
            return null;
        }
    }
};

window.SetCurrentOpentProject = (title) => {
    document.title = title;
};

window.HideEmptyData2 = function () {
    var emptyElement = document.querySelector(".custom-grid .dxbs-empty-data-row > td");
    if (emptyElement)
    {
        emptyElement.innerText = "";
    }
    
};

window.HideEmptyData = function changeNoDataText(gridCss) {
    console.log("gridCss =>" + gridCss);
    var emptyRow = document.querySelector('.' + gridCss).querySelector(".dxbs-empty-data-row");
    emptyRow.querySelector("td").innerText = "Custom Text";
}

window.addCSSSheeteStyleToGrid = async function addCSSSheeteStyleToGridSD(cssText, CustomStylesheetGrid) {
    if (!CustomStylesheetGrid) {
        CustomStylesheetGrid = 'CustomStylesheetGrid';
    }
    //let GridIdsheet = document.querySelector("div[grid-bcephal-id='" + GridId + "']");
    let sheet = document.getElementById(CustomStylesheetGrid);
    if (sheet === null){
        var element = document.createElement('style');
        element.id = 'CustomStylesheetGrid';
        element.type = 'text/css';
        document.head.appendChild(element);
        sheet = element.sheet;
    }
    const separetor = "#;;#";
    var cssTextVec = cssText.split(separetor);
    for (var offset = 0; offset < cssTextVec.length; offset++) {
        if (cssTextVec[offset]) {
            sheet.insertRule(cssTextVec[offset], sheet.cssRules.length);
        }
    }    
    return;
};

window.removeCSSSheeteStyleToGrid = async function removeCSSSheeteStyleToGridSD(cssClass, CustomStylesheetGrid) {
    if (!CustomStylesheetGrid) {
        CustomStylesheetGrid = 'CustomStylesheetGrid';
    }
   // let GridIdsheet = document.querySelector("div[grid-bcephal-id='" + GridId + "']");
    let element = document.getElementById(CustomStylesheetGrid);
    if (element) {
        let sheet = element.sheet;
        if (sheet) {
            if (sheet.cssRules) { // all browsers, except IE before version 9
                //console.log(" cssRules 1");
                for (var i = 0; i < sheet.cssRules.length; i++) {
                   // console.log(sheet.cssRules[i].selectorText);
                    if (sheet.cssRules[i] && sheet.cssRules[i].selectorText && sheet.cssRules[i].selectorText.startsWith(cssClass)) {
                        //console.log(i, sheet.cssRules[i].selectorText);
                        sheet.deleteRule(i);
                    }
                }
            }
            else {  // Internet Explorer before version 9
                //console.log(" cssRules 2");
                for (var i = 0; i < sheet.rules.length; i++) {
                    if (sheet.rules[i] && sheet.rules[i].selectorText && sheet.rules[i].selectorText.startsWith(cssClass)) {
                        //console.log(i, sheet.cssRules[i].selectorText);
                        sheet.removeRule(i);
                    }
                }
            }
        }
        element.parentNode.removeChild(element);
    }
    return;
};

window.ConfirmationDialog = {
    confirmDelete: function (message) {
        return confirm(message);
    }
};

window.ClickTriggerFunction = {
    ClickTrigger: function (elt) {
        (elt) => elt.click();
    }
};

window.GetIdObjectReference = function GetIdObjectReference(element, dotNetObject, openCallback) {
    if (element && element instanceof HTMLElement) {
        element.id = Date.now().toString(36) + Math.random().toString(36).substr(2) + Math.random().toString(36).substr(2);
        if (dotNetObject) {
            dotNetObject.invokeMethodAsync(openCallback, element.id);
        }
    } else
        if (element) {
            var id = element;
            element = document.getElementById(id);
            if (!element) {
                element = document.querySelector("div[bcephal-id='" + id + "']");
            }
            if (dotNetObject && element) {
                element.id = Date.now().toString(36) + Math.random().toString(36).substr(2) + Math.random().toString(36).substr(2);
                dotNetObject.invokeMethodAsync(openCallback, element.id);
            }
        }
};

window.EnterKeyEvent = function EnterKeyEvent(element, dotNetObject, EnterCallback, TextCallback, eventName) {
    const timeOut = 500;
    if (element && element instanceof HTMLElement) {
        if (dotNetObject) {
            element.addEventListener(eventName, function (event) {                
                //console.log(event);
                if (event.keyCode === 13) {
                    event.preventDefault();
                    event.stopPropagation();
                    if (EnterCallback) {
                       // console.log(event);
                        setTimeout(function () {
                            dotNetObject.invokeMethodAsync(EnterCallback, event, element.value);
                        }, timeOut);
                    }
                } else {
                    event.preventDefault();
                    if (TextCallback) {
                        setTimeout(function () {
                            dotNetObject.invokeMethodAsync(TextCallback, element.value);
                        }, timeOut);
                    }
                }
            });
        }
    } else
        if (element) {
            element = document.getElementById(element);
            //console.log(element);            
            if (dotNetObject && element) {
                element.addEventListener(eventName, function (event) {
                    event.preventDefault();
                    //console.log(event);
                    if (event.keyCode === 13) {
                       // console.log(event);
                        if (EnterCallback) {
                            setTimeout(function () {
                                dotNetObject.invokeMethodAsync(EnterCallback, event, element.value);
                            }, timeOut);
                        }
                    } else {
                        if (TextCallback) {
                            setTimeout(function () {
                                dotNetObject.invokeMethodAsync(TextCallback, element.value);
                            }, timeOut);
                        }
                    }
                });
            }
        }
};

window.EnterKeyEventArg = function EnterKeyEventArg(element, dotNetObject, Callback, eventName, isArgv) {
    var handler = function (event) {
        event.preventDefault();
        //     console.log(event);
        if (isArgv && (isArgv === 'true' || isArgv === true)) {
            //         console.log("call 2 true");
            setTimeout(function () { dotNetObject.invokeMethodAsync(Callback, event); }, 0);
        } else {
            //         console.log("call 2 false");
            setTimeout(function () { dotNetObject.invokeMethodAsync(Callback); }, 0);
        }
    };
    if (element && element instanceof HTMLElement) {
        if (dotNetObject) {
            element.addEventListener(eventName, handler);
        }
    } else
        if (element) {
           // console.log(element);
            element = document.getElementById(element);
          //  console.log(element);
            if (dotNetObject && element) {
                element.addEventListener(eventName, handler);
            }
        }
};

window.dragstart_handler = function (ev, dat) {
    //console.log("dragStart ==> ", dat);
    ev.dataTransfer.setData("text", JSON.stringify(dat));
};

window.drop_handler = function (ev, id) {

    var data = ev.dataTransfer.getData("text");
    //console.log("Drop data ==> ", data);
    if ($.data[id + 'dotNetObject'] && $.data[id + 'Callback']) {
        setTimeout(function () { $.data[id + 'dotNetObject'].invokeMethodAsync($.data[id + 'Callback'], data); }, 0);
    }
};

window.drop_handler_Callback = function (id, dotNetObject, Callback) {
    $.data[id + 'dotNetObject'] = dotNetObject;
    $.data[id + 'Callback'] = Callback;
};

window.drop_handler_Callback_delete = function (id) {
    $.data[id + 'dotNetObject'] = null;
    $.data[id + 'Callback'] = null;
};

window.dragstart_target = function (items) {
    //console.log("dragstart_target listner", items);
    items = JSON.parse(items);
    if (dragstart_target) {
        for (var el = 0; el < items.length; el++) {
            var attribute = items[el];            
            //element = document.querySelector("div[bcephal-drag='" + attribute[0] + "']");
            element = document.getElementById(attribute[0]);
            if (element) {
                //console.log("dragstart_target item", attribute[0]);
                element.addEventListener("dragstart", function (event) {
                    dragstart_handler(event, attribute[1]);
                });
            }
        }
    }
};

window.gradEventConfigInit = function (items) {
    console.log("gradEventConfigInit", items);
    document.addEventListener("drag", function (event) {
    });

    document.addEventListener("dragstart", function (event) {
        // store a ref. on the dragged elem
        //console.log("event.target", event.target);
        console.log("event", event);
    });

    document.addEventListener("drop", function (event) {
        // store a ref. on the dragged elem
        console.log("drop", event.target);
    });

    //items = JSON.parse(items);
    //if (items) {
    //    for (var el = 0; el < items.length; el++) {
    //        var attribute = items[el];
    //        element = document.getElementById(attribute[0]);
    //        if (element) {
    //            console.log("item", attribute[0]);
    //           // console.log("item value", attribute[1]);
    //            element.addEventListener('drag', function (event) {}, false);
    //            element.addEventListener('dragstart', function (event) {
    //                console.log("item value", attribute[1]);
    //                dragstart_handler(event, attribute[1]);
    //            },false);
    //        }
    //    }
    //}

    window.gradEventConfigInitDragstart = function (event) {
        console.log("item", items);
        stop = false;
    };

    window.gradEventConfigInitDragend = function (event) {
        console.log("item", items);
        //var toDrag = document.getElementById(items);
        //if (toDrag) {
        //    toDrag.addEventListener('dragstart', gradEventConfigInitDragstart);
        //    toDrag.addEventListener('dragend', gradEventConfigInitDragend);
        //}
    }
};


//window.GetItemBoundingRectByPosition = function () {
//    var items = document.querySelectorAll('[data-position]');
//    console.log("tems.length =>");
//    if (items) {
//        console.log("tems.length =>" + items.length);
//        for (i = 0; i < items.length; i++) {
//            console.log("i =>" + items[i].getBoundingClientRect().width);
//        }
//    }

//};


window.GetItemDimensiongByPosition = function (position) {
   // console.log("position in getting height =>" + position);
    var item = document.querySelector(`[data-position="${position}"]`)
    var dim = "";
    if (item) {
        var height = item.getBoundingClientRect().height;
        var width = item.getBoundingClientRect().width;
        dim = "width:" + width + "_" + "height:" + height;
        return dim;
    }
    return '';
};

window.GetContainerDimension = function () {
    var item = document.querySelector(`[class="d-flex flex-column grid-bc-two"`)
    var dim = "";
    if (item) {
        var height = item.getBoundingClientRect().height;
        var width = item.getBoundingClientRect().width;
        dim = "width:" + width + ";" + "height:" + height;
        return dim;
    }
    return '';
};

window.InsertVariableInTextArea = function (id, myValue) {
    var myField = document.getElementById(id);
    //IE support
    if (document.selection) {
        myField.focus();
        sel = document.selection.createRange();
        sel.text = myValue;
    }
    //MOZILLA and others
    else if (myField.selectionStart || myField.selectionStart == '0') {
        var startPos = myField.selectionStart;
        var endPos = myField.selectionEnd;
        myField.value = myField.value.substring(0, startPos)
            + myValue
            + myField.value.substring(endPos, myField.value.length);
    } else {
        myField.value += myValue;
    }
};

window.setRowsCountOfGrid = function (id, CheckBoxTotalPageId, CheckBoxTotalPageControl, dotNetObject) {
    //console.log("Call setRowsCountOfGrid", id);
    var functionInit = function (id, dotNetObject, CheckBoxTotalPageId, CheckBoxTotalPageControl) {
        const idString = `div[grid-bcephal-id="${id}"]`;
        //console.log("functionInit querySelector", idString);
        var grid0 = document.querySelector(idString);
        //console.log("functionInit grid0", grid0);
        if (grid0) {
            $(grid0).ready(function () {
                //console.log("call callback");
                if (dotNetObject) {
                    var val = CheckBoxTotalPageControl
                    //console.log("call reverse ", val);
                    if (val == null) {
                        val = grid0.querySelector(`div[id="${CheckBoxTotalPageId}"]`);
                        //console.log("call reverse ", val);
                    }
                    if (val) {
                        //console.log("Result GetPagerRender", val);
                        var input = val.querySelectorAll('div[class="custom-control custom-checkbox d-inline-block"] input'); // correct
                        //console.log("Result input checked", input.checked);
                        //console.log("input ", input);
                        input[0].onclick = function () {
                            dotNetObject.invokeMethodAsync('RefreshGirdFromJSRow', input[0].checked);
                        }
                        input[1].onclick = function () {
                            dotNetObject.invokeMethodAsync('RefreshGirdFromJS', input[1].checked);
                        }
                        setTimeout(funct, 200, id, val);
                    }
               }
            });
        }
    };
    var funct = function(id,CheckBoxTotalPageControl) {
        
        const idString = `div[grid-bcephal-id="${id}"]`;
        //console.log("querySelector", idString);
        var grid = document.querySelector(idString);
        if (grid) {
            //console.log("grid", grid);
            var pager = document.querySelector(idString + ' div.card > div[class="card-body dxbs-grid-pager dx-resize"] > div[class="ml-auto"]'); // correct
           // var IsInit = document.querySelector(`div[class="m-0 p-0 content-bc"] > div[id="${chexboxTotaPageId}"]`);
           // var IsInit = document.querySelector(`div[class="m-0 p-0 content-bc"] > div[class="${CheckBoxTotalPageControl.className}"]`);

            if (pager) {
               // console.log("pager", pager);
               // console.log("chexboxTotaPageId", CheckBoxTotalPageControl);
                //console.log("IsInit", IsInit);
                // const lastElem = pager.lastElementChild;
                const lastElem = pager;
                if (CheckBoxTotalPageControl && CheckBoxTotalPageControl.parentNode) {
                    CheckBoxTotalPageControl.parentNode.removeChild(CheckBoxTotalPageControl);
                    lastElem.parentNode.insertBefore(CheckBoxTotalPageControl, lastElem.nextSibling);
                    CheckBoxTotalPageControl.parentNode = lastElem.parentNode;
                }
            } 
        }
    };
    setTimeout(functionInit, 200, id, dotNetObject, CheckBoxTotalPageId, CheckBoxTotalPageControl);
};


window.setRowsCountOfGrid2 = function (id, CheckBoxTotalPageId, CheckBoxTotalPageControl, dotNetObject) {
    //console.log("Call setRowsCountOfGrid2", id);
    var functionInit2 = function (id, dotNetObject, CheckBoxTotalPageId, CheckBoxTotalPageControl) {
        const idString = `dxbl-grid[grid-bcephal-id="${id}"]`;
        //console.log("functionInit2 querySelector", idString);
        var grid0 = document.querySelector(idString);
        //console.log("functionInit grid0", grid0);
        if (grid0) {
            $(grid0).ready(function () {
                console.log("call callback");
                if (dotNetObject) {
                    var val = CheckBoxTotalPageControl
                    //console.log("call reverse ", val);
                    if (val == null) {
                        val = grid0.querySelector(`div[id="${CheckBoxTotalPageId}"]`);
                        //console.log("call reverse ", val);
                    }
                    if (val) {
                        //console.log("Result GetPagerRender", val);
                        var input = val.querySelectorAll('div[class="custom-control custom-checkbox d-inline-block"] input'); // correct
                        //console.log("Result input checked", input.checked);
                        //console.log("input ", input);
                        input[0].onclick = function () {
                            dotNetObject.invokeMethodAsync('RefreshGirdFromJSRow', input[0].checked);
                        }
                        input[1].onclick = function () {
                            dotNetObject.invokeMethodAsync('RefreshGirdFromJS', input[1].checked);
                        }
                        setTimeout(funct2, 200, id, val);
                    }
                }
            });
        }
    };
    var funct2 = function (id, CheckBoxTotalPageControl) {

        const idString = `dxbl-grid[grid-bcephal-id="${id}"]`;
        //console.log("querySelector", idString);
        var grid = document.querySelector(idString);
        if (grid) {
            //console.log("grid", grid);
            var pager = document.querySelector(idString + ' div.card  div[class="card-body dxbs-grid-pager-container"]   div[class="dxbs-grid-page-size-selector"]'); // correct
            // var IsInit = document.querySelector(`div[class="m-0 p-0 content-bc"] > div[id="${chexboxTotaPageId}"]`);
            // var IsInit = document.querySelector(`div[class="m-0 p-0 content-bc"] > div[class="${CheckBoxTotalPageControl.className}"]`);
            if (pager) {
                 //console.log("pager", pager);
                // console.log("chexboxTotaPageId", CheckBoxTotalPageControl);
                //console.log("IsInit", IsInit);
                // const lastElem = pager.lastElementChild;
                if (pager.style && pager.style !== "margin-left:auto;") {
                    pager.style = "margin-left:auto;";
                }
                const lastElem = pager;
                if (CheckBoxTotalPageControl && CheckBoxTotalPageControl.parentNode) {
                    CheckBoxTotalPageControl.parentNode.removeChild(CheckBoxTotalPageControl);
                    lastElem.parentNode.insertBefore(CheckBoxTotalPageControl, lastElem.nextSibling);
                    CheckBoxTotalPageControl.parentNode = lastElem.parentNode;
                }
            }
        }
    };
    setTimeout(functionInit2, 200, id, dotNetObject, CheckBoxTotalPageId, CheckBoxTotalPageControl);
};



window.setStateReadyFirst = function (id, dotNetObject, method_) {
    var functionInit2 = function (id, dotNetObject, method_) {
        const idString = `dxbl-grid[grid-bcephal-id="${id}"]`;
        var grid0 = document.querySelector(idString);
        if (grid0) {
            $(grid0).ready(function () {
                if (dotNetObject && method_) {
                    dotNetObject.invokeMethodAsync(method_, true);
                }
            });
        }
    };
    setTimeout(functionInit2, 200, id, dotNetObject, method_);
};

//Blazor.registerCustomEventType('custompaste', {
//    browserEventName: 'paste',
//    createEventArgs: event => {
//        return {
//            eventTimestamp: new Date(),
//            pastedData: event.clipboardData.getData('text')
//        };
//    }
//});



window.fileManager = {
   
    downloadFromUrl: function (options) {
        var _a;
        var anchorElement = document.createElement('a');
        anchorElement.href = options.url;
        anchorElement.target = "_blank";
 
        anchorElement.download = (_a = options.fileName) !== null && _a !== void 0 ? _a : '';
        anchorElement.click();
        anchorElement.remove();
    },
  
    downloadFromByteArray: function (options) {
       
        var url = typeof (options.byteArray) === 'string' ?
            // .NET 5 or earlier, the byte array in .NET is encoded to base64 string when it passes to JavaScript.
            // In that case, that base64 encoded string can be pass to the browser as a "data URL" directly.
            "data:" + options.contentType + ";base64," + options.byteArray :
            // .NET 6 or later, the byte array in .NET is passed to JavaScript as an UInt8Array efficiently.
            // - https://docs.microsoft.com/en-us/dotnet/core/compatibility/aspnet-core/6.0/byte-array-interop
            // In that case, the UInt8Array can be converted to an object URL and passed to the browser.
            // But don't forget to revoke the object URL when it is no longer needed.
            URL.createObjectURL(new Blob([options.byteArray], { type: options.contentType }));
       
        fileManager.downloadFromUrl({ url: url, fileName:options.fileName });
        if (typeof (options.byteArray) !== 'string')
            URL.revokeObjectURL(url);
    },
};

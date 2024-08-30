
var ref = null;
var htmleditor = null;
var selectedContent = '';

window.initHtmlEditor = function () {
    if (document.getElementsByClassName('htmleditor').length > 0) {
        htmleditor = document.getElementsByClassName('htmleditor')[0];
        htmleditor.addEventListener('selectionchange', getSelectedText);
        htmleditor.addEventListener('paste', pasteEvent);
        htmleditor.addEventListener('keypress', addParagraphTag);
    }
};


window.contenteditor = {
    
    getHtml: function () {
        if (htmleditor) {
            return htmleditor.innerHTML;
        } else {
            return "";
        }

    },
    setHtml: function (htmlcontent) {
        if (htmleditor) {
            htmleditor.innerHTML = htmlcontent;
           /* htmleditor.contentEditable = false;*/
        }
    },
};

function getSelectedText()
{
    selectedContent = window.getSelection();

}


function boldtify()
{
    var sel = window.getSelection();
    var html;
    if (sel)
    {
        if (sel.getRangeAt && sel.rangeCount)
        {
            range = sel.getRangeAt(0);
            document.execCommand('bold');
           //document.execCommand('removeFormat');
        }
    } else if (document.selection && document.selection.createRange) {
        range = document.selection.createRange();
        range.collapse(false);
        range.pasteHTML(html);

    }
}

function underline()
{
    var sel = window.getSelection();
    var html;
    if (sel) {
        if (sel.getRangeAt && sel.rangeCount) {
            range = sel.getRangeAt(0);
            document.execCommand('underline');
        }
    } else if (document.selection && document.selection.createRange) {
        range = document.selection.createRange();
        range.collapse(true);
        range.pasteHTML(html);

    }
}

function italicize()
{
    var sel = window.getSelection();
    var html
    if (sel) {
        if (sel.getRangeAt && sel.rangeCount) {
            range = sel.getRangeAt(0);
            document.execCommand('italic', true);
        }
    } else if (document.selection && document.selection.createRange) {
        range = document.selection.createRange();
        range.collapse(true);
        range.pasteHTML(html);

    }
}


function changePolicySize(dimension)
{
    var sel = window.getSelection();

    if (sel)
    {
        if (sel.getRangeAt && sel.rangeCount)
        {
            range = sel.getRangeAt(0);
            var html = '' + range + '';
            range.deleteContents();
            var el = document.createElement("span");
            el.style.fontSize = dimension;
            el.innerHTML = html;
            range.insertNode(el);

        }
    } else if (document.selection && document.selection.createRange)
    {
        range = document.selection.createRange();
        range.collapse(true);
        range.pasteHTML(html);  
    }
}

function changeColor(color)
{
    var sel = window.getSelection();

    if (sel) {
        if (sel.getRangeAt && sel.rangeCount) {
            range = sel.getRangeAt(0);
            document.execCommand('foreColor',true, color);
        }
    } else if (document.selection && document.selection.createRange) {
        range = document.selection.createRange();
        range.collapse(true);
        range.pasteHTML(html);
        insertText();
    }
}

function addParagraphTag(evt) {
    if (evt.keyCode == '13') {
        if (window.getSelection().anchorNode.parentNode.tagName === 'LI') return;
        document.execCommand('formatBlock', false, 'p');
    }
}


function pasteEvent(e) {
    e.preventDefault();

    let text = (e.originalEvent || e).clipboardData.getData('text/plain');
    document.execCommand('insertHTML', false, text);
}



function horizontalAlignment(action) {
    if (htmleditor) {
        document.execCommand(action, false, htmleditor.innerHTML);
    }
        
}

function insertText() {
    htmleditor.innerHTML = htmleditor.innerHTML + '&nbsp';
}




// https://stackoverflow.com/questions/6910278/how-to-return-focus-to-the-parent-window-using-javascript

function register() {
    if(!window.opener){ alert('missing opener'); }

    window.opener.document.getElementById('name').value = document.getElementById('name').value;
	window.opener.register();
}

function call() {
    if (document.getElementById('peer').value == '') {
        window.alert('You must specify the peer name');
        return;
    }
    window.opener.document.getElementById('peer').value = document.getElementById('peer').value;
    window.opener.call();
}

function stop(message) {
    window.opener.stop();
}

$(document).ready(function() {
    enableButton('#register', 'register()');
    enableButton('#call', 'call()');
    enableButton('#terminate', 'stop()');
});

function enableButton(id, functionName) {
    $(id).attr('disabled', false);
    $(id).attr('onclick', functionName);
}

function openNewBackgroundTab(){
    var a = document.createElement("a");
    a.href = "http://www.google.com/";
    var evt = document.createEvent("MouseEvents");
    //the tenth parameter of initMouseEvent sets ctrl key
    evt.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0,
        true, false, false, false, 0, null);
    a.dispatchEvent(evt);
}

function newTab(url)
{
    var tab=window.open("");
    tab.document.write("<!DOCTYPE html><html>"+document.getElementsByTagName("html")[0].innerHTML+"</html>");
    tab.document.close();
    window.location.href=url;
}
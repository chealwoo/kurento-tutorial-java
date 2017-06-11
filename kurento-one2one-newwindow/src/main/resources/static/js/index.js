
// https://stackoverflow.com/questions/6910278/how-to-return-focus-to-the-parent-window-using-javascript

window.name = "parent";

function register() {
	window.open("webcall.html")
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
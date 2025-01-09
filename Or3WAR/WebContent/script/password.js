function changePassword() {
  var url = location.pathname + "?trg=chp&old="+
            document.forms[0].oldpass.value+"&new="+
            document.forms[0].newpass.value+"&confirm="+
            document.forms[0].confirm.value;
  if (url.indexOf("/") > 0) url = "/" + url;
  url += "&noCache=" + (new Date).getTime();

  window.document.location.replace(url);
}
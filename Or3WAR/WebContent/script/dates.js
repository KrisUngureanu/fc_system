function openReportWithDates(id) {
  var addr = "trg=frm&cmd=grpt&id="+id;
  if (document.forms[0].fd != null) {
    addr += "&fd=" + document.forms[0].fd.value;
  }
  if (document.forms[0].ld != null) {
    addr += "&ld=" + document.forms[0].ld.value;
  }
  if (document.forms[0].cd != null) {
    addr += "&cd=" + document.forms[0].cd.value;
  }
  opener.getXmlByParams(addr);
}

function nextStep(id) {
  var inputs = document.getElementsByTagName('input');
  var opt = "-1";
  if (inputs != null) {
    for (i = 0; i<inputs.length; i++) {
      var inp = inputs[i];
      if (inp.checked) {
        opt = inp.value;
        break;
      }
    }
  }
  opener.nextStep2(id, opt);
}

function moveStart() 
{
  var range = document.selection.createRange();
  range.move('textedit', -1);
  range.select();
}

function formatDate (obj, e) {
  if (obj == null || obj.disabled == true) return false;
  var code = e.keyCode; 
  if (code == 9) return true;
  if (code >= 35 && code <= 40) return true;

  var str = obj.value;
  var range = document.selection.createRange();
  if (str.length == 10) {
    var d = -range.moveStart('character',-11);
  
    var cur = String.fromCharCode(e.keyCode);

    if (((code >= 48 && code <= 57) || (code >= 96 && code <= 105)) && d<10) {
      if (d==2 || d==5) d++;
    
      var c = 0;
      if (code <=57) c = code - 48;
      else c = code - 96;
    
      str = str.substring(0,d) + c + str.substring(d+1, str.length);
      if (d==1 || d==4) d++;
      obj.value = str;
      range.move('character', d+1);
      range.select();
    }
         
    if (code==8 && d>0) {
      if (d==3 || d==6) d--;
      var f = "г";
      if (d<3) f="д";
      else if (d<6) f = "м";
      str = str.substring(0,d-1) + f + str.substring(d, str.length);
      if (d==4 || d==7) d--;
    
      obj.value = str;
      range.move('character', d-1);
      range.select();
    }

  } else if (str.length == 16) {
    var d = -range.moveStart('character',-17);
  
    var cur = String.fromCharCode(e.keyCode);

    if (((code >= 48 && code <= 57) || (code >= 96 && code <= 105)) && d<16) {
      if (d==2 || d==5 || d == 10 || d == 13) d++;
      var c = 0;
      if (code <=57) c = code - 48;
      else c = code - 96;
    
      str = str.substring(0,d) + c + str.substring(d+1, str.length);
      if (d==1 || d==4 || d == 9 || d == 12) d++;
      obj.value = str;
      range.move('character', d+1);
      range.select();
    }
         
    if (code==8 && d>0) {
      if (d==3 || d==6 || d == 11 || d == 14) d--;
      var f = "М";
      if (d<3) f="д";
      else if (d<6) f = "м";
      else if (d<11) f = "г";
      else if (d<14) f = "ч";
      str = str.substring(0,d-1) + f + str.substring(d, str.length);
      if (d==4 || d==7 || d == 12 || d == 15) d--;
    
      obj.value = str;
      range.move('character', d-1);
      range.select();
    }
  }  

  return false;
}

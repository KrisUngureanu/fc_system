function allGetElementById(id)
{
	return document.all[id];
}
function evalGetElementById(id)
{
	eval("var e=self."+id+";");
	return e;
}
function execGetElementById(id)
{
	window.execScript("var e=self."+id+";","JavaScript");
	return e;
}
function nullGetElementById(id)
{
	return null;
}
if (document.getElementById){;}
else if(document.all)
{	document.getElementById=allGetElementById;}
else if(eval && self)
{document.getElementById=evalGetElementById;}
else if(window.execScript && self)
{document.getElementById=execGetElementById;}
else
{document.getElementById=nullGetElementById;}

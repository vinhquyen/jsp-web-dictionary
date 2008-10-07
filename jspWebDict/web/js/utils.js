
/** Creates a new definition OR example text box into the formulary */
var numEx = 0;
var numDef = 0;
function addNode(type) {
	var node; 
	var div		= document.createElement("div");
	var txtArea = document.createElement("textarea");
	txtArea.cols = 32;
	txtArea.rows = 4;

	if(type == "def") {
		node = document.getElementById("definitions");
		txtArea.name = "def"+numDef;
		div.id = "def"+numDef;
		numDef += 1;
	}
	 else {
		node = document.getElementById("examples");
		txtArea.name = "ex"+numEx;
		div.id = "def"+numEx;
		numEx += 1;
	}
	//<textarea cols="32" rows="4" name="ex"></textarea>
	var a = document.createElement("a");
	a.href = "#";
	var fDel = "delNode('" + node.id + "', '" + div.id + "')";
	a.setAttribute("onclick", fDel);
	a.appendChild(document.createTextNode("[x]"));
	a.style.position = "relative";
	a.style.left = "10px";
	a.style.top	= "-60px";

	//div.style.border = "1px solid red";
	
	div.appendChild(txtArea);
	div.appendChild(a);
	node.appendChild(div);
}

function delNode(idParent, id) {
	var parent = document.getElementById(idParent);
	var node = document.getElementById(id);
	parent.removeChild(node);
	
	 // TODO: TEST --> fix number of new nodes created (num--)
	if(id.contains("def"))
		 numDef -= 1;
	else if(id.cotains("ex"))
		 numEx -= 1;
}

/** Change the language of the page */
function changeLang(lang) {
	var pag, host, params;
	var url = location.href;
	
	var j = url.lastIndexOf("/");
	var i = url.indexOf("?"); 
	host = url.substring(0, j+1); //get the host		
	
	if(i >= 0) {	// Some POST parameters exist
		pag = url.substring(j+1, i); //get the page
		params = url.substr(i); //get the parameters
		if(params.indexOf("lang=") > -1) { //any parameter is lang and we will override it
			var args = params.split("&"); 
			var k;
			for(k=0; k<args.length; k++) {
				if(args[k].indexOf("lang=") > -1) {
					i = args[k].indexOf("=");
					args[k] = args[k].substring(0,i+1)+lang;
				}
				if(k == 0) {params = args[k];}
				else {params = params.concat("&"+args[k]);}
			}
		}
		else {params = params.concat("&lang="+lang);}
	}
	else {	// Do not exist any parameter
		pag = url.substr(j+1).replace("#", ""); //get the page
		params = "?lang="+lang;
	}
	location.href = host+pag+params;
}

/** Set CSS styles for IE compability */
function cssIE() {
	var browser		= navigator.appName;
	var b_version	= navigator.appVersion;
	var version		= parseFloat(b_version);
	
	if(browser == "Microsoft Internet Explorer") {
		var head = document.getElementsByTagName("head")[0];         
		var cssNode = document.createElement("link");
		cssNode.type = 'text/css';
		cssNode.rel = 'stylesheet';
		cssNode.href = 'css/ie.css';
		head.appendChild(cssNode);
		 
		var body = document.getElementsByTagName("body")[0];
		var div = document.createElement("div")
		div.id = "ie";
		div.style.background = "black";
		div.style.color = "red";
		div.style.opacity = "0.7";
		div.style.position = "fixed";
		div.style.textAlign = "center";
		div.style.paddingTop = "33%";
		div.style.top = "0";
		div.style.left = "0";
		div.style.height = "100%";
		div.style.width = "100%";
		div.setAttribute("onclick", "showDiv('ie', false)");
		
		var msg = "This website has NOT been tested for IE compability."
		var txt = document.createTextNode(msg);
		div.appendChild(txt);
		div.appendChild(document.createElement("br"));
		msg = "Click to continue";
		txt = document.createTextNode(msg);
		div.appendChild(txt);
		 
		body.appendChild(div);
	}
}


function displayVerb(morf) {
	if(morf == "v.") {
		showDiv("verb", true);
	}
	else {
		showDiv("verb", false); 
	}
}

/** This function do the initialization of the web onLoad event */
function init() {
	document.forms[0].input_search.focus();
	cssIE();
}

function setVisibility(id) {
	var obj = document.getElementById(id);
	if(obj.style.display == "block" || obj.style.visibility == "visible") {
		 showDiv(id, false);
	}
	else {
		 showDiv(id, true);
	}
}

function showDiv(id, visible) {
	var obj = document.getElementById(id);
	if(!visible) {
		obj.style.display = "none";
		obj.style.visibility = "hidden"; 
	}
	else {
		obj.style.display = "block";
		obj.style.visibility = "visible";
	}
}
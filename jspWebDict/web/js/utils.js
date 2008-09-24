
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
		pag = url.substr(j+1); //get the page
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
		var headID = document.getElementsByTagName("head")[0];         
		var cssNode = document.createElement('link');
		cssNode.type = 'text/css';
		cssNode.rel = 'stylesheet';
		cssNode.href = 'css/ie.css';
		headID.appendChild(cssNode);
		 
		var body = document.getElementsByTagName("body")[0];
		var txt = document.createTextNode("This website has NOT been tested for IE compability.");
		body.appendChild(txt);
	}
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
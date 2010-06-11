

/** Change the language of the page
 *  @param lang : the new locale */
function changeLang(lang) {
    var pag, host, params;
    var url = location.href.replace("#", "");

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
                if(k == 0) {
                    params = args[k];
                }
                else {
                    params = params.concat("&"+args[k]);
                }
            }
        }
        else {
            params = params.concat("&lang="+lang);
        }
    }
    else {	// Do not exist any parameter
        pag = url.substr(j+1); //get the page
        params = "?lang="+lang;
    }
    location.href = host+pag+params;
}

/** TODO: funcion que iguale la altura de las dos columnas de
 *  definiciones (aproximadamente) :-)
 */
function equalHeight(group) {
    tallest = 0;
    group.each(function() {
        thisHeight = $(this).height();
        if(thisHeight > tallest) {
            tallest = thisHeight;
        }
    });
    group.height(tallest);
}

/** External links --> open new window XHTML compliance */
function externalLinks() {
    if (!document.getElementsByTagName) return;
    var anchors = document.getElementsByTagName("a");
    for (var i=0; i<anchors.length; i++) {
        var anchor = anchors[i];
        if (anchor.getAttribute("href") &&
            anchor.getAttribute("rel") == "external") {
            anchor.target = "_blank";
            anchor.innerHTML += "<img src='img/new_window.gif' alt='new window' />";
        }
    }
}

/** This function do the initialization of the web onLoad event */
function init() {
    var f = document.getElementById("s_word");
    if(f) f.input_search.focus();

    jqueryInit()
    externalLinks();
}

/** JQuery library inits
 *  Developer NOTE: .hide uses the css.display property (set to none)
 *                  combine it with main.css.hidden style to initialite
 *                  a unshowed element
 */
function jqueryInit() {
    $(document).ready(function() {
        //$('.hidden').hide();

        $('a#tog-contact').click(function() {
            $('#contact').toggle(400);
            return false;
        });
        $('a#tog-license').click(function() {
            $('#license').toggle(400);
            return false;
        });
        $('a#tog-help').click(function() {
            $('#help').toggle(400);
            return false;
        });
        /** Hardcoded inline ********
        $('#help').click(function() {
            $('#help').hide(400);
            return false;
        });
        $('#license').click(function() {
            $('#license').hide(400);
            return false;
        }); *************************/

        //equalHeight($(".definition"));

        /* Form Validation */
        $("#addForm").validate(/*{debug: true}*/);
    });
}

/** Creates a new definition textarea into the formulary */
var iDef = 1;
var definitionBoxDefault = "Otra definición";
function addDefinition() {
    $('#definitions').append('<div style="margin-top:5px;" id="'+iDef+'"><label>&nbsp;</label>'+
    '<textarea style="color:grey" onfocus="emptyText(this)" onblur="setDefaultText(this)"'+
    ' cols="24" rows="6" name="def" minlength="10">'+definitionBoxDefault+'</textarea>'+
    '<a style="color:red; margin-left:2px;" href="#" onclick="delDefinition('+iDef+')">[x]</a>'+
    '</div>');
    iDef = iDef + 1;
}

/** Removes the definition textarea with identifier = id */
function delDefinition(id) {
    $("#" + id).remove();
}

/** Form.definitions behaviour: modifies CSS and help text */
function emptyText(obj) {
    if(obj.value == definitionBoxDefault) {
        obj.value = "";
        obj.style.color = "black";
    }
}

/** Form.definitions behaviour: modifies CSS and help text */
function setDefaultText(obj) {
    //alert(obj.value);
    if(obj.value == "") {
        obj.value = definitionBoxDefault;
        obj.style.color = "grey";
    }
}

/** Replace no ascii chars for its HTML code
 *  @param str string to replace no ascii chars
 *  @return string with no ascii html-coded  */
function replaceHTML(str)
{
    str = str.replace(/&agrave;/g,'à');
    str = str.replace(/&aacute;/g,'á');
    str = str.replace(/&egrave;/g,'è');
    str = str.replace(/&eacute;/g,'é');
    str = str.replace(/&igrave;/g,'ì');
    str = str.replace(/&iacute;/g,'í');
    str = str.replace(/&iuml;/g,'ï');
    str = str.replace(/&ograve;/g,'ò');
    str = str.replace(/&oacute;/g,'ó');
    str = str.replace(/&ugrave;/g,'ù');
    str = str.replace(/&uacute;/g,'ú');
    str = str.replace(/&uuml;/g,'ü');
    str = str.replace(/&ccedil;/g,'ç');
    str = str.replace(/&middot;/g,'·');


    str = str.replace(/&Agrave;/g,'à');
    str = str.replace(/&Aacute;/g,'á');
    str = str.replace(/&Egrave;/g,'è');
    str = str.replace(/&Eacute;/g,'é');
    str = str.replace(/&Igrave;/g,'ì');
    str = str.replace(/&Iacute;/g,'í');
    str = str.replace(/&Iuml;/g,'ï');
    str = str.replace(/&Ograve;/g,'ò');
    str = str.replace(/&Oacute;/g,'ó');
    str = str.replace(/&Ugrave;/g,'ù');
    str = str.replace(/&Uacute;/g,'ú');
    str = str.replace(/&Uuml;/g,'ü');
    str = str.replace(/&Ccedil;/g,'ç');
    return str;
}

/** DEPRECATED: Use JQuery instead
 *  Hidde or show and element
 *  @param id : identificator of the element */
function setVisibility(id) {
    var obj = document.getElementById(id);
    if(obj.style.visibility == "visible") {
        obj.style.visibility = "hidden";
        obj.style.display = "none";
    }
    else {
        obj.style.visibility = "visible";
        obj.style.display = "block";
    }
}


$.extend({
    /** @return an associative array with the uri params */
    getUrlVars: function(){
        var vars = [], hash;
        var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
        for(var i = 0; i < hashes.length; i++)
        {
            hash = hashes[i].split('=');
            vars.push(hash[0]);
            vars[hash[0]] = hash[1];
        }
        return vars;
    },
    /** @param name: the param identifier
     *  @return value 'name' identified parameter  */
    getUrlVar: function(name){
        return $.getUrlVars()[name];
    },
    /** @return string with de uri params */
    getUrlParams: function() {
        var url_param = "";
        var aParam = $.getUrlVars();
        for(var i=0; i < aParam.length; i++) {
            url_param += aParam[i]+"="+$.getUrlVar(aParam[i])+"&";
        }
        return url_param;
    }
});

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

/** Iguala la altura de las dos columnas de definiciones moviendo def:
 *      def = col_left.last   , si col_left > col_right
 *      def = col_right.first , si col_left < col_right
 *      def = null            , si dif_heights < def.height()
 *      
 *  Perfila el ajustado repartiendo pixeles de relleno entre cada elemento de
 *  la columna de menor altura
 *  TODO: ¿crear func auxiliares para código más limpio?
 */
var MIN_DIF = 25;
var MAX_NUM_ITER = 5; //Chapuza para fijar bug de bucle infinito...
function adjustColsHeigh() {
    MAX_NUM_ITER = MAX_NUM_ITER - 1;
    var aux, def;
    var cleft_height = $('#col_left').height() - $('#col_left').children("div.definition").length * 10;
    var cright_height = $('#col_right').height() - $('#col_right').children("div.definition").length * 10;

    if(cright_height == 0 || MAX_NUM_ITER == 0)  {
		if(!MAX_NUM_ITER)alert("Posible bucle infinito:"+MAX_NUM_ITER);
		return;
	}

    var dif_height = (cleft_height - cright_height);

    if(dif_height < 0) {
        def = $('#col_right .definition:first-child');
        if(def.height()+MIN_DIF < -dif_height) {
            def.detach();
            $('#col_left').append(def);
            adjustColsHeigh();
        } else { // Perfilar con vertical-padding
            aux = -dif_height/$('#col_left .definition').length;
            $('#col_left .definition').each(function() {
               $(this).height( $(this).height() + aux);
            });
        }
    } else if (dif_height > 0) {
        def = $('#col_left .definition:last-child');
        if(def.height()+MIN_DIF < dif_height) {
            def.detach();
            $('#col_right .definition:first-child').before(def);
            adjustColsHeigh();
        } else { // Perfilar con vertical-padding
            aux = dif_height/$('#col_right .definition').length;
            $('#col_right .definition').each(function() {
               $(this).height( $(this).height() + aux);
            });
        }
    }
    //alert(cleft_height+"||"+cright_height+"||"+def.height());//DEBUG
    //else {return;}
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

        /** ModifyWord Contextual Help Labels */
        $('#morfology').blur(function() {
            $('#info_morf').hide();
            return false;
        });
        $('#morfology').focus(function() {
            $('#info_morf').show();
            return false;
        });
        $('#info_morf').mouseover(function() {
           this.style.maxHeight = "10em";
           this.style.overflow = "visible";
        });
        $('#info_morf').mouseout(function() {
           this.style.maxHeight = "2.5em";
           this.style.overflow = "hidden";
        });
        $('#word').blur(function() {
            $('#info_word').hide();
            return false;
        });
        $('#word').focus(function() {
            $('#info_word').show();
            return false;
        });
        /*** end contextual help ***/

        /** Message fadding out */
        setTimeout("$('p.error').fadeOut(800)",5000);
        setTimeout("$('#msg').fadeOut(800)",5000);
        
        /* Form Validation */
        $("#addForm").validate({
            event: "blur",
            rules: {
                    'word': "required",
                    'def': "required"
            },
            messages: {
                    'word': "Por favor ingrese la palabra a definir",
                    'def': "Por favor, ingrese la definición"
            },
            errorElement: "label",
            errorPlacement: function(error, element) {
                 element.after(error);
            },
            submitHandler: function(form) {
               // Handle empty definitions
               $(".ta_def").each(function() {
                    this.onblur = null;
                    this.onfocus = null;
                    emptyText(this);
               });
               // Check UNIQUE(word+term) restriction
               var w_id = jQuery.trim($("#id").val());
               var w_morf = jQuery.trim($('#morfology').val());
               var w_word = jQuery.trim($('#word').val());
               
               $.get('ajax_handler.jsp?id='+w_id+'&word='+w_word+'&morfology='+w_morf,null,
                    function(response){
                        var res_id = jQuery.trim(response);
                        if(res_id == 0) { form.submit() }
                        else {
                            $('#word').attr("class", "error");
                            $('#morfology').attr("class", "error");
                            $('#validate').append("<label class='error'>Ya existe una definición con el par {término, morfología} introducidos.</label>");
                            $('#validate').append("<p class='info'><a href='index.jsp?action=4&mod=modify&id="+res_id+"'>Modifica la palabra</a> si quieres añadir nuevas definiciones.</p>");
                        }
                    });
           }
        });
    });
}

/** Creates a new definition textarea into the formulary */
var iDef = 1;
var definitionBoxDefault = "Otra definición";
function addDefinition() {
    $('#definitions').append('<div style="margin-top:5px;" id="'+iDef+'"><label class="name">&nbsp;</label>'+
    '<textarea class="ta_def" style="color:grey" onfocus="emptyText(this)" onblur="setDefaultText(this)"'+
    ' cols="24" rows="6" name="def">'+definitionBoxDefault+'</textarea>'+
    '<a style="color:red; margin-left:2px;" href="#" onclick="delDefinition('+iDef+')">[x]</a>'+
    '</div>');
    iDef = iDef + 1;
}

/** Removes the definition textarea with identifier = id */
function delDefinition(id) {
    var def = $("#" + id);
    jConfirm('Are you sure about deleting this definition?', 'Delete confirmation', function(r) {
        if(r) {
            def.remove();
        }
    });
}

function deleteWord(id) {
    jConfirm('Are you sure about deleting this word?\n This action can not be undone', 'Delete word confirmation', function(r) {
        if(r) {
            //jAlert('TODO: Ajax JSP petition to DELETE id='+id, 'Confirmation Results');
            $(".result").load("delete.jsp?id="+id);
        }
    });
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

/** Set the checkbox 'id' to the complementary checked state
 *  @param id = the id of the checkbox */
function setChecked(id) {
    var chkBox = document.getElementById(id);
    chkBox.checked = !chkBox.checked;
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
        obj.style.display = "inline";
    }
}

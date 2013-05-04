/* 
 * s2dict javascript utils
 * @author Santiago Lamora
 * @copyright cbox
 */

/** This function do the initialization of the web onLoad event */
function init() {
    $("#mode").buttonset();
    $('label.ui-button.ui-widget', '#search_bar').tooltip();

    $('body').append("<div id='overlay' class='ui-helper-hidden'></div>");

    /* init helpers links */
    $('a.helpers').click(function() {
        
        $container = $('#' + $(this).data('id'));
        $container.detach();
        $('#container_960').append($container);
        
        $('#overlay').toggle(200);
        $container.toggle(400);

    });

    $('div.ui-helper-hidden').click(function() {
        $('div.ui-helper-hidden').hide(400);
    });
    
    /* beta examples markup */
    //highLight();

    /** TODO: revise this old staff **/
    jqueryInit()
    externalLinks();
}

function highLight() {
    searchTerm = "«";
    searchRegex  = new RegExp(searchTerm, 'g');
    $(".definition p").replaceText( searchRegex, '<span class="example">');

    searchTerm = "»";
    searchRegex  = new RegExp(searchTerm, 'g');
    $(".definition p").replaceText( searchRegex, '</span>');
}

// jQuery extension
$.fn.replaceText = function( search, replace, text_only ) {
    return this.each(function(){
      var node = this.firstChild,
        val,
        new_val,
        remove = [];
      if ( node ) {
        do {
          if ( node.nodeType === 3 ) {
            val = node.nodeValue;
            new_val = val.replace( search, replace );
            if ( new_val !== val ) {
              if ( !text_only && /</.test( new_val ) ) {
                $(node).before( new_val );
                remove.push( node );
              } else {
                node.nodeValue = new_val;
              }
            }
          }
        } while ( node = node.nextSibling );
      }
      remove.length && $(remove).remove();
    });
  };

  jQuery.fn.removeHighlight = function() {
 return this.find("span.highlight").each(function() {
  with (this.parentNode) {
   replaceChild(this.firstChild, this);
  }
 }).end();
};

/** External links --> open new window XHTML compliance */
function externalLinks() {
    if (!document.getElementsByTagName) return;
    var anchors = document.getElementsByTagName("a");
    for (var i=0; i<anchors.length; i++) {
        var anchor = anchors[i];
        if (anchor.getAttribute("href") &&
            anchor.getAttribute("rel") == "external") {
            //anchor.target = "_blank"; //The User decides where open the link
            anchor.innerHTML += "<img src='img/new_window.gif' alt='new window' />";
        }
    }
}



/** JQuery library inits
 *  Developer NOTE: .hide uses the css.display property (set to none)
 *                  combine it with main.css.hidden style to initialite
 *                  a unshowed element
 */
function jqueryInit() {
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
    //setTimeout("$('p.error').fadeOut(800)",5000);
    //setTimeout("$('#msg').fadeOut(800)",5000);

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
            //$(".result").load("delete.jsp?id="+id);
            //$(".result").load("index.jsp?action=5&id="+id);
            //location.href = "index.jsp?action=5&id="+id;
            $("#content").load("index.jsp?action=5&id="+id+" #content");
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

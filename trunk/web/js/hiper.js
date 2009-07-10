
if (self != top) top.location.href = self.location.href;

function Is ()
{   //convert all characters to lowercase to simplify testing
    var agt=navigator.userAgent.toLowerCase()

    // *** BROWSER VERSION ***
    this.major = parseInt(navigator.appVersion)
    this.minor = parseFloat(navigator.appVersion)

    this.nav  = ((agt.indexOf('mozilla')!=-1) && ((agt.indexOf('spoofer')==-1) && (agt.indexOf('compatible') == -1)))
    this.nav2 = (this.nav && (this.major == 2))
    this.nav3 = (this.nav && (this.major == 3))
    this.nav4 = (this.nav && (this.major == 4))
    this.navonly = (this.nav && (agt.indexOf(";nav") != -1))

    this.ie  = (agt.indexOf("msie") != -1)
    this.ie3 = (this.ie && (this.major == 2))
    this.ie4 = (this.ie && (this.major == 4))

    this.opera = (agt.indexOf("opera") != -1)
     
    // *** JAVASCRIPT VERSION CHECK *** 
    // Useful to workaround Nav3 bug in which Nav3 
    // loads <SCRIPT LANGUAGE="JavaScript1.2">.
    if (this.nav2 || this.ie3) this.js = 1.0
    else if (this.nav3 || this.opera) this.js = 1.1
    else if (this.nav4 || this.ie4) this.js = 1.2
    
	// NOTE: In the future, update this code when newer versions of JS 
    // are released. For now, we try to provide some upward compatibility 
    // so that future versions of Nav and IE will show they are at 
    // *least* JS 1.2 capable. Always check for JS version compatibility 
    // with > or >=.
    else if ((this.nav && (this.minor > 4.05)) || (this.ie && (this.major > 4))) 
         this.js = 1.2
    else this.js = 0.0 // HACK: always check for JS version with > or >=
}

var nav = new Is()
if (nav.js >= 1.2) captura();
seleccionat= false;

function captura()
{
	if (nav.nav)
	{
		if (document.getElementById)
		{
	 	  window.addEventListener("dblclick",engega_doubleclick,false);
	 	  window.addEventListener("mouseup",engega_mouseup,false);
		}
		else
		{
	  	  // Netscape 
	 	  window.captureEvents(Event.DBLCLICK | Event.MOUSEUP);
	 	  window.onDblClick = engega_doubleclick;	
		  window.onMouseUp = engega_mouseup;
		}
	}
	else
	{
	  // IE
	  document.ondblclick = engega_doubleclick;
	  document.onmouseup = engega_mouseup;
	}
}

function engega_doubleclick(e)
{
	if (seleccionat && textSeleccionat()!="") 
	{
		seleccionat=false;

		if (!e)
		{
			var e = new emula_event();
		}

		var obj = top.document.getElementById('hiperList');
		if (obj)
		{
			//obj.innerHTML = "";
			mouObjA(e.pageX,e.pageY,obj);
		}
		
		if (nav.nav) cercaf(textSeleccionat());
		else cercad(textSeleccionat());
	}
	
	return true;
}

function engega_mouseup(e)
{
   //Si hi ha text seleccionat
   seleccionat=true;	
   return true;
} 

function textSeleccionat()
{
	str = "";
	if (nav.nav) //Netscape 
	{
	   str = ""+window.getSelection(); //+document.getSelection(); DEPRECATED
  	} 
	else if (document.selection &&  document.selection.createRange) 
 	{ 
   	   var rang = document.selection.createRange(); 
  	   if (rang)   str = rang.text; 
	} 
 	
	if (str=="")  {return "";}

   	patro=/\cM/g; //Separador del Navigator
 	str = str.replace(patro,' ');
 	patro=/[.,;:?!��=\(\)\[\]\t\n]/g;
 	str = str.replace(patro,'');
  
	// Treure els espais de m�s
	patro=/\s\s/g;
	lon=str.length;
	lon2=lon+1;
	while (lon != lon2)
	{
	   str = str.replace(patro,' ');
	   lon2 = lon;
	   lon = str.length;
	}

	str=str.substring(0,100);

	return str;
}

function cercaf(vtext)
{
	if ((vtext != " ") && (vtext != ""))
	{
		HiperText(trim(vtext));
	}
}

function cercad(vtext)
{
	if ((vtext != " ") && (vtext != ""))
	{
		if (document.selection.createRange())
		{
			var rang  = document.selection.createRange(); 
			var rang2 = document.selection.createRange().duplicate(); 

			var Ok = true;
			while ((rang2.moveStart('character', -1) != 0) && (Ok) && (rang2.text.length != rang.text.length))
			{
			   if (EsSeparador(rang2.text.charAt(0))) 
			   {
				  rang2.moveStart('character', 1);
				  rang2.select();
				  Ok = false;
			   }
		    }
			
			rang = document.selection.createRange(); 
			rang2 = document.selection.createRange().duplicate(); 
			
			Ok = true;
			if (!EsSeparador(rang.text.charAt(rang.text.length-1)))
			{		
				while ((rang.moveEnd('character', 1) != 0) && (Ok) && (rang2.text.length != rang.text.length))
				{
					if (EsSeparador(rang.text.charAt(rang.text.length-1))) 
					{	
						rang.moveEnd('character', -1);
						rang.select();
						Ok = false;
					}
				}			
			}
			
			rang = document.selection.createRange(); 
			
			while ((rang.text.length > 0) && ((rang.text.charCodeAt(rang.text.length-1) == 32) || ((rang.text.charCodeAt(rang.text.length-1) >=48) && (rang.text.charCodeAt(rang.text.length-1) <= 57))))
			{
				rang.moveEnd('character', -1);
				rang.select();
			}	
			
			rang = document.selection.createRange(); 
			
			var i;		
			var str = rang.text;
			var spl = new Array();
					
			patro=/["']/g;
			str = str.replace(patro,' ');
			
			spl = str.split(" ");
			str = " " + str + " ";

			for (i=0; i < spl.length; i++)
			{
				if (spl[i].length == 0) 
				{
					spl.splice(i,1);
					i--;
				}
			}

			var posi, posf;
			switch (spl.length)
			{
				case 1:
				{
					posi = str.indexOf(" " + spl[0]);
					posf = posi + spl[0].length-1;
				}
				break;
				
				case 2:
				{
					if (spl[0].length > spl[1].length)
					{	
						posi = str.indexOf(" " + spl[0]);
						posf = posi + spl[0].length-1;
					}
					else
					{
						posi = str.indexOf(" " + spl[1]);
						posf = posi + spl[1].length-1;
					}
				}
				break;
					
				case 3:
				{
					posi = str.indexOf(" " + spl[1]);
					posf = posi + spl[1].length-1;
				}
				break;
				
				default:{ posi = 0; posf = rang.text.length-1; }
			}
			
			rang.moveStart('character', posi);
			rang.select();
			rang.moveEnd('character', -(rang.text.length - ((posf - posi)+1)));
			rang.select();
			
			rang = document.selection.createRange(); 
			vtext = rang.text;
		}	
		HiperText(trim(vtext));
 	}
}

function EsSeparador(c)
{
	return (c == '\'' || c == '-' || c == '/' || c == '+'  || c == ',' || c == '.' || c == '$' || c == '%' || c == '&'  ||
			c == '<'  || c == '>' || c == '_' || c == '\\' || c == ';' || c == ')' || c == '=' || c == '+' || c == ':'  || 
			c == '�'  || c == '!' || c == '?' || c == '�'  || c == '�' || c == '(' || c == ' ' || c == '[' || c == ']'  ||
			c == '*'  || c.charCodeAt(0) == 13 || c.charCodeAt(0) == 10 || c.charCodeAt(0) == 11); 
}

function ltrim(str) 
{ 
	for(var k = 0; k < str.length && isWhitespace(str.charAt(k)); k++);
	return str.substring(k, str.length);
}

function rtrim(str) 
{
	for(var j=str.length-1; j>=0 && isWhitespace(str.charAt(j)) ; j--) ;
	return str.substring(0,j+1);
}

function trim(str) 
{
	return ltrim(rtrim(str));
}

function isWhitespace(charToCheck) 
{
	var whitespaceChars = " \t\n\r\f";
	return (whitespaceChars.indexOf(charToCheck) != -1);
}

function emula_event()
{	
	this.which=window.event.button;
	this.pageX=window.event.clientX+document.body.scrollLeft;
	this.pageY=window.event.clientY+document.body.scrollTop;
	this.target=window.event.srcElement;
	this.target.name=window.event.srcElement.name;
}

/* Process the double-clicked text and DO an ACTION */
function HiperText(gecart)
{
  // alert("Selected text is: [" + gecart + "]");
  window.location = "index.jsp?word=" + gecart;
}

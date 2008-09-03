		<h2>Add a new word:</h2>
		<form action="insert.jsp" method="post">
			<p> <label for="word">Word: </label>
				<% 
						String szWord = request.getParameter("word");
						String szMorf = request.getParameter("morfology");
						String szDef  = request.getParameter("def");
						
						if(szWord == null)
						    szWord = "";
						if(szMorf == null)
						    szMorf = "";
						if(szDef == null)
						    szDef = "";
				 %>
				<input type="text" name="word" value="<% out.print(szWord); %>"/>
			</p>
			<p> <label>Morfology:</label><br/>		<!-- location.href is an adress that allows to keep form values -->
				<select name="morfology" onchange="location.href='index.jsp?action=1&word='+word.value+'&morfology='+this.value+'&def='+def.value;">
					 <!-- TODO: use an ArrayList to print it -->
					 <% 
						String aMorf[] = {"adj.", "adv.", "interfj.", "f.", "m.", "prep.", "pron.", "v."};
						for(String m : aMorf) {
						    String szValue = "<option value=\"" + m + "\"";
						    if(szMorf.compareTo(m)==0)
								szValue+=("selected=\"selected\"");
						    szValue+=">" + m + "</option>";
						    out.println(szValue);
						}
					  %>
				</select>
			<% if(szMorf.compareTo("v.") == 0) { %>
				<div id="verb">
					<p>Select propierties of the verb: <br/>
						<input type='checkbox' name="tr." /><label>Transitive</label>
						<input type='checkbox' name="int."/><label>Intransitive</label>
						<input type='checkbox' name="r."/><label>Reflexive</label>
					</p>
				</div>
			<% } %>
			</p>
			<p>
				<label for="Definition">Definition:</label><br/>
				<textarea cols="25" rows="4" name="def"><% out.print(szDef); %></textarea>
			</p>
			<p>
				<input type="submit" value="Add word"/>
				<input type="reset" value="Clear form"/>
			</p>
		</form>
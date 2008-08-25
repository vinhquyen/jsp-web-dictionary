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
				<select name="morfology" onchange="location.href='index.jsp?action=add&word='+word.value+'&morfology='+this.value+'&def='+def.value;">
					<option value="adj." <% if(szMorf.compareTo("adj.") == 0) out.print("selected='selected'");%>>Adjetive</option>
					<option value="adv." <% if(szMorf.compareTo("adv.") == 0) out.print("selected='selected'");%>>Adverb</option>
					<option value="interj." <% if(szMorf.compareTo("interj.") == 0) out.print("selected='selected'");%>>Interjection</option>
					<option value="f." <% if(szMorf.compareTo("f.") == 0) out.print("selected='selected'");%>>Femenine Name</option>
					<option value="m." <% if(szMorf.compareTo("m.") == 0) out.print("selected='selected'");%>>Male Name</option>
					<option value="prep." <% if(szMorf.compareTo("prep.") == 0) out.print("selected='selected'");%>>Preposition</option>
					<option value="pron." <% if(szMorf.compareTo("pron.") == 0) out.print("selected='selected'");%>>Pronom</option>
					<option value="v." <% if(szMorf.compareTo("v.") == 0) out.print("selected='selected'"); %>>Verb</option>
				</select>
				<div id="verb" <% if(szMorf.compareTo("v.") != 0) out.print("class='hidden'"); %>>
					<p>Select propierties of the verb: <br/>
						<input type='checkbox' name="tr." /><label>Transitive</label>
						<input type='checkbox' name="int."/><label>Intransitive</label>
						<input type='checkbox' name="r."/><label>Reflexive</label>
					</p>
				</div>
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
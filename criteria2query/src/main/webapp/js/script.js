// define variable
var ohdsi_json_orig;
var ohdsi_json;
var ohdsi_counts;
var selectAlls = [];
var nct_eli = {};
var xml_text = "";

function findCount(counts,id){
	for(var i = 0; i < counts.length; i++){
		if(counts[i]['key'] == id){
			return counts[i]['value'];
		}
	}
	return [0,0];
}

function getCT(){
	var ct_num = document.getElementById("ct_num").value;
	console.log("the ct_num is: "+ct_num);
	if(ct_num.substring(0,3) != "NCT" && ct_num.substring(0,3) != "nct"){
		// the input string is not valid
		alert("please enter valid ClinicalTrials.gov Identifier! \n e.g. NCT01136369");
	}
	else{
		window.location.href = "/EliIE/"+ct_num;
	}
}

function onLoadEliIE(){
		// set the ClinicalTrial.gov free text if provided
	var freetxt_area = document.getElementById("eliieinput");
    if ('text' in nct_eli){
      freetxt_area.value = nct_eli['text'];   
    }
    else{
      freetxt_area.placeholder="e.g. Has a known history of HIV, multiple or severe drug allergies, or severe post-treatment hypersensitivity reactions.";
    }
}

// on load funciton for the EliIEx, initiate the input text when loading the index page
function onLoadEvent(){
		var xmltext = document.getElementById("xmlinput");
		if(xml_text == ""){
			xmltext.placeholder='e.g. \n \
		<root>\n\
			<sent>\n\
				<text>Has a known history of HIV , multiple or severe drug allergies , or severe post-treatment hypersensitivity reactions .</text>\n\
				<entity class="Condition" index="T1" negated="N" relation="T3:modified_by|T2:modified_by" start="5"> HIV </entity>\n\
				 <attribute class="Qualifier" index="T2" start="7"> multiple </attribute>\n\
				 <attribute class="Qualifier" index="T3" start="9"> severe </attribute>\n\
				 <entity class="Condition" index="T4" negated="N" relation="T5:modified_by|T2:modified_by|T3:modified_by" start="10"> drug allergies </entity>\n\
				 <attribute class="Qualifier" index="T5" start="14"> severe </attribute>\n\
				 <entity class="Condition" index="T6" negated="N" relation="T5:modified_by" start="15"> post-treatment hypersensitivity reactions </entity>\n\
			</sent>\n\
		</root>';
		}
}

// load the concept matched list
function onLoadConceptEvent(){
	onChangeConcept();
}

// sort the concept list via count array order
function sortConcept(concept,count){
	// change the items order according to the sorted counts array
	for(var k = count.length-1; k >= 0; k--){
			for(var l = 0; l < concept.length; l++){
				if(concept[l]['concept']['CONCEPT_ID'] == count[k]['key']){
					var temp = concept[l];
					concept.splice(l,1);
					concept.unshift(temp);
					break;
				}
			}
	}
}

function selectAll(idx){
	var ancestor = document.getElementById("collapseExample"+idx);
	var tbody = ancestor.childNodes[0].childNodes[0].childNodes[0].childNodes;
	var checked = true;
	var checklabel = document.getElementById("sel"+idx);
	if (selectAlls[idx] == 1){
		checked = false;
		selectAlls[idx] = 0;
		checklabel.innerHTML = "Select All";
	}
	else{
		selectAlls[idx] = 1;
		checklabel.innerHTML = "Unselect All";
	}
	// the first row is heading of table, start from 2nd row
	for(var j = 1; j < tbody.length; ++j){
			var tmp = tbody[j].childNodes[0].childNodes[0];
			tmp.checked = checked;
	}

}

function onChangeConcept(){
	var ohdsi_div = document.getElementById("transform");
	ohdsi_div.innerHTML = "";
	var ohdsi_div = document.getElementById("transform");
	ohdsi_div.innerHTML = "";

	ohdsi_json = JSON.parse(JSON.stringify(ohdsi_json_orig));
	var conceptsets  = ohdsi_json['ConceptSets'];
	var form = document.createElement("div");
	form.id = "concept_form";		
	
	var conceptclass = document.createElement("div");
	// conceptclass.className = "";
	for(var i = 0; i < conceptsets.length; i++){
		var items = conceptsets[i]['expression']['items'];
		// change the order of conceptsets items according to the count array
		sortConcept(items,ohdsi_counts['count'][i]);

		console.log("coming to the conceptset loop "+i+" item length"+items.length);

		var conceptname = '<a class="btn btn-primary btn-block" role="button" data-toggle="collapse" \
		href="#collapseExample'+i+'" aria-expanded="false" aria-controls="collapseExample">'+
  		conceptsets[i]['name']+" [length "+items.length+']</a>';
  		// var parser = new DOMParser();
  		// var doc = parser.parseFromString(conceptname, "text/xml");
  		var stat = document.getElementById("stat");
  		if(items.length == 0){
  			var tr = '<tr><td>'+conceptsets[i]['name']+'</td><td>'+conceptsets[i]['domain']+'</td>'+'</tr>';
  			stat.insertAdjacentHTML('beforeend',tr);
  		}

		conceptclass.insertAdjacentHTML('beforeend',conceptname);

		var conceptdiv = document.createElement("div");
		conceptdiv.className = "collapse";
		conceptdiv.id = "collapseExample"+i;
		var conceptwell = document.createElement("div");
		conceptwell.className = "well";
		var concepttbl = document.createElement("table");
		concepttbl.className = "table table-bordered .table-striped";
		var th = '<tr><th><button id="sel'+i+'" class="btn btn-success pull-right" onclick="selectAll('+i+')">Unselect All</button></th><th>ID</th><th>Code</th><th>Name</th><th>Class</th><th>RC</th><th>DRC</th><th>Domain</th><th>Vocabulary</th></tr>';
		// selectAll array save the state of select/unselect all of each concept sets
		selectAlls.push(1);

		concepttbl.insertAdjacentHTML('beforeend',th); 


		for(var j = 0; j < items.length; j++){
			// var concept = "test for items";
			var input = document.createElement("input");
			input.type = "checkbox";
			input.name = "conceptsets";
			input.value = conceptsets[i]['name'];
			input.checked = "checked";

			// show the conceptsets information via table
			var td2 = document.createTextNode(items[j]['concept']['CONCEPT_ID']);
			// td2.innerHTML = ;
			var td3 = document.createTextNode(items[j]['concept']['CONCEPT_CODE']);
			// td3.innerHTML = ;
			var td4 = document.createTextNode(items[j]['concept']['CONCEPT_NAME']);
			// td4.innerHTML = ;
			var td5 = document.createTextNode(items[j]['concept']['CONCEPT_CLASS_ID']);
			// td5.innerHTML = ;
			// console.log("DC is: "+getCountsByKey(ohdsi_counts['count'][i],items[j]['concept']['CONCEPT_ID']));
			// var td6 = document.createTextNode(getCountsByKey(ohdsi_counts['count'][i],items[j]['concept']['CONCEPT_ID'])["value"][0]);
			
			var DC = findCount(ohdsi_counts['count'][i],items[j]['concept']['CONCEPT_ID']);
			var td6 = document.createTextNode(DC[0]);

			// td6.innerHTML = ;
			// var td7 = document.createTextNode(getCountsByKey(ohdsi_counts['count'][i],items[j]['concept']['CONCEPT_ID'])["value"][1]);
			var td7 = document.createTextNode(DC[1]);

			// td7.innerHTML = ;
			var td8 = document.createTextNode(items[j]['concept']['DOMAIN_ID']);
			
			// td8.innerHTML = ;
			var td9 = document.createTextNode(items[j]['concept']['VOCABULARY_ID']);
			// td9.innerHTML = ;

			// var concept = JSON.stringify(items[j]['concept']);
			// var concept_txt = document.createTextNode(concept);
 
			var tblrow = concepttbl.getElementsByTagName('tbody')[0];
			var newrow = tblrow.insertRow(tblrow.rows.length);
			var newcell = newrow.insertCell(0);
			newcell.appendChild(input);
			var newcell2 = newrow.insertCell(1);
			newcell2.appendChild(td2);
			var newcell3 = newrow.insertCell(2);
			newcell3.appendChild(td3);
			var newcell4 = newrow.insertCell(3);
			newcell4.appendChild(td4);
			var newcell5 = newrow.insertCell(4);
			newcell5.appendChild(td5);
			var newcell6 = newrow.insertCell(5);
			newcell6.appendChild(td6);
			var newcell7 = newrow.insertCell(6);
			newcell7.appendChild(td7);
			var newcell8 = newrow.insertCell(7);
			newcell8.appendChild(td8);
			var newcell9 = newrow.insertCell(8);
			newcell9.appendChild(td9);

		}
		conceptwell.appendChild(concepttbl);
		conceptdiv.appendChild(conceptwell);

		conceptclass.appendChild(conceptdiv);	
	}
	form.appendChild(conceptclass);

	var submit = '<button class="btn btn-default pull-right" onclick="onSubmitConcept()">Apply</button>';

	var prev = '<button class="btn btn-default pull-left" action="" onclick="history.go(-1)">Previous</button>';

	var ohdsi_form = document.getElementById("ohdsi");
	ohdsi_form.innerHTML = "";
	ohdsi_form.appendChild(form);
	ohdsi_form.insertAdjacentHTML('beforeend',prev);
	ohdsi_form.insertAdjacentHTML('beforeend',submit);
}

function onSubmitConcept(){
	console.log("come in onSubmitConcept");
	var ohdsi = {"ConceptSets":[]};

	var ancestor = document.getElementById("concept_form");
    descendents0 = ancestor.childNodes[0];
    descendents  = descendents0.childNodes;
    var i, a, div1;
	for(i = 0; i < descendents.length; ++i){
		// the 'a' tagname, get the concept name
		a = descendents[i];
		var conceptsets = {"expression":{"item":[]},"id":i/2,"name":a.value};
		// the 'div' of collapse class
		div0 = descendents[++i];
		// the 'div' of well class, the table, the tbody,    the tr
		div1 = div0.childNodes[0].childNodes[0].childNodes[0].childNodes;
		
		// div2 = div1.getElementsByTagName('div');
		var remove = [];
		var j;
		// the first row is heading of table, start from 2nd row
		for(j = 1; j < div1.length; ++j){
			var div2 = div1[j].childNodes[0].childNodes[0];
			if(!div2.checked){
				// console.log("deleting "+j+ "'s childnode");
				remove.push(j-1);
			}
		}
		for(j = remove.length-1; j >=0; j--){
			// console.log("deleting (i-1)/2= "+(i-1)/2+" j= "+j);
			ohdsi_json["ConceptSets"][(i-1)/2]["expression"]["items"].splice(remove[j],1);
		}
	}
	var ohdsi_submit = document.createElement("div");
	ohdsi_submit.className = "well";
	var json_pretty = JSON.stringify(ohdsi_json,null,2);

	ohdsi_submit.appendChild(document.createTextNode(json_pretty));
	
	var copy = '<button id="copy_txt" class="btn btn-success btn-block">Click to Copy</button>'
	var prev = '<button class="btn btn-default btn-block" action="" onclick="onChangeConcept()">Previous</button>';

	var ohdsi_form = document.getElementById("ohdsi");
	ohdsi_form.innerHTML = "";
	var ohdsi_div = document.getElementById("transform");
	var textarea = document.createElement("textarea");
	textarea.rows = "20";
	textarea.className = "form-control span6";
	var pre = '<pre id="json_txt">'+json_pretty+'</pre>';
	var title = '<h3>OHDSI json format of eligibility criteria text</h3><p>please copy and paste to <a href="http://www.ohdsi.org/web/atlas/#/cohortdefinition/0" target="_blanket">OHDSI ATLAS</a> platform\'s json text field and generate the cohort for further research.</p>'
	ohdsi_div.insertAdjacentHTML('beforeend',title);
	// ohdsi_div.insertAdjacentHTML('beforeend',copy);
	// textarea.insertAdjacentHTML('beforeend',pre);
	textarea.innerHTML = json_pretty;
	ohdsi_div.appendChild(textarea);	
	ohdsi_div.insertAdjacentHTML('beforeend',prev);
	
}





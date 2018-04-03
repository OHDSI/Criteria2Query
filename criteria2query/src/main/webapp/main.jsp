<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<html>
<head>
<meta charset="utf-8">
<title>EliIE APP | CUMC-DBMI</title>
<meta name="description" content="">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width">
<link rel="icon"
	href="http://www.columbia.edu/sites/all/themes/base/columbia2/images/favicon-crown.png">
<link
	href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
	rel="stylesheet">

<link
	href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<link rel="stylesheet" href="../css/styles.css">
<link rel="stylesheet" href="../css/ner.css">
<style>
.btn {
	margin: 5px;
}

.well {
	overflow: scroll;
}
</style>






<script src="//code.jquery.com/jquery.min.js"></script>
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script type="text/javascript" src="http://malsup.github.io/min/jquery.blockUI.min.js"></script>
<script>
            $( function() {
                $( "#accordion" ).accordion({
                  collapsible: true
                });
              } );
        </script>
<script src="../js/script.js"></script>

<script
	src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>





</head>
<body>
	<header class="banner"> <nav class="navbar navbar-inverse">
	<div class="container-fluid">
		<div class="navbar-header">
			<a class="navbar-brand" rel="home" href="/" title="Freesources">
				EliIE APP </a>
		</div>
		<ul class="nav navbar-nav navbar-right">
			<li><a href="/EliIE">EliIE</a></li>
			<li><a href="/json-transform">EliIEx</a></li>
		</ul>
	</div>
	</nav> </header>

	<div class="container">
		<h2>
			<a href="https://github.com/Tian312/EliIE" target="_blanket">EliIE</a>
		</h2>
		<p>- a tool that transforms clinical trial eligibility criteria to
			xml file EliIE.</p>
		<p>
			<i>Note: you can enter the ClinicalTrials.gov Identifier to
				automatically fetch the eligibility criteria or enter the free text
				by your self.</i>
		</p>

		<div class="accordion">
			<h3>Get eligibility criteria free text from the
				ClinicalTrials.gov</h3>
			<div>
				<label for="ct_num"
					class="col-sm-3 col-form-label col-form-label-lg">ClinicalTrials.gov
					Identifier:</label>
				<div class="col-sm-6">
					<input type="text" class="form-control form-control-lg" id="nctid"
						placeholder="e.g. NCT01136369">
				</div>
				<div class="col-sm-3">
					<button id="getct" class="btn btn-primary btn-block">Get
						From ClinicalTrials.gov</button>
				</div>
			</div>
		</div>

		<hr>
		<form id="freetxt_input" class="form-group"
			action="<%=basePath%>ohdsi/eliresult" method="POST">
			<div class="form-group">
				<label class="control-label" for="eliieinput">Eliie input
					free text</label>
			<div id="inin">
				<textarea class="form-control span6" cols="40" id="eliieinput"
					name="eliie_input_free_text" placeholder="Eliie input free text"
					rows="12" required>
				</textarea>
				</div>
			</div>
			<button type="button" id="start" class="btn btn-primary pull-right">Start
				EliIE</button>
			<button id="format" type="button" class="btn btn-success pull-right">Format</button>
			<button id="show" type="button" class="btn btn-success pull-right">Show</button>

		</form>

	</div>
	<script type="text/javascript">  
	$(document).ajaxStop($.unblockUI);
  var basePath = "<%=basePath%>";
		$(function() {
			$("#eliieinput").val('');
			$("#getct").click(function() {
				if ($("#nctid").val() == '') {
					alert('Please input a nctid!');
					return;
				}
				getCTinfo();
			});
			$("#format").click(function() {
				formattext();
			});
			$("#start").click(function() {
				startparser();
			});
			$("#show").click(function(){
				 // $("#result").toggle();
				  $("#inin").replaceWith("When <mark data-entity=\"person\">Sebastian Thrun</mark>started working on self-driving cars at<mark data-entity=\"org\">Google</mark> in<mark data-entity=\"date\">2007</mark>");
							
				});
		})
		function getCTinfo() {
			var nctid = $("#nctid").val();
			$('body').block({
				message : '<h3>Data loading……</h3>',
				css : {
					border : '1px solid khaki'
				}
			});
			$.ajax({
				type : 'POST',
				url : basePath + "ohdsi/getct",
				data : {
					'nctid' : nctid
				},
				dataType : "json",
				success : function(data) {
					/* alert("Success!"); */
					$("#eliieinput").val(data);
				}
			});
		}
		function formattext() {
			var freetextinput = $("#eliieinput").val();
			$('body').block({
				message : '<h3>Data Formatting……</h3>',
				css : {
					border : '1px solid khaki'
				}
			});
			$.ajax({
				type : 'POST',
				url : basePath + "ohdsi/format",
				data : {
					'freetextinput' : freetextinput
				},
				dataType : "json",
				success : function(data) {
					$("#eliieinput").val(data);
				}
			});
		}
		function startparser() {
			var freetextinput = $("#eliieinput").val();
			$.ajax({
				type : 'POST',
				url : basePath + "ohdsi/execeli",
				data : {
					'freetextinput' : freetextinput
				},
				dataType : "json",
				success : function(data) {
					alert('!');
				}
			});
		}
		
	</script>


	<footer class="footer" style="background-color: white">
	<div class="container">
		<hr>
		<h4 class="text-center">
			2016
			<script>
				new Date().getFullYear() > 2010
						&& document.write("- " + new Date().getFullYear())
			</script>
			&copy Yixuan Guo
		</h4>
		<div class="text-center center-block">
			<p class="txt-railway">
				This is a web application developed for transforming eligibility
				criteria free text to OHDSI recepted JSON format,<br> a
				graduation project by Yixuan Guo at <a
					href="https://www.dbmi.columbia.edu/" target="_blanket"> DMBI</a>
				from <a href="www.columbia.edu">Columbia University</a>. <br> <strong>
					Author: <a href="http://www.columbia.edu/~yg2430" target="_blanket">Yixuan
						Guo</a>, <a href="http://www.tiankangnlp.com/" target="_blanket">Tian
						Kang</a> (EliIE author), <a
					href="http://people.dbmi.columbia.edu/~chw7007/" target="_blanket">Chunhua
						Weng</a>.
				</strong>
			</p>
			<a href="https://github.com/YixuanAshleyGuo/OHDSImatcher"
				target="_blanket"><i id="social-gh"
				class="fa fa-github-square fa-3x social"></i></a> <a
				href="mailto:cw2384@cumc.columbia.edu"><i id="social-em"
				class="fa fa-envelope-square fa-3x social"></i></a>
		</div>
		<hr>
	</footer>
</body>
</html>
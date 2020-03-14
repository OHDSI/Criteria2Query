<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<html>

<head>

<meta charset="utf-8">
<title>Criteria2Query</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="keywords" content="Clinical trial,">
<meta name="author" content="Chi Yuan, Chunhua Weng">
<meta name="robots" content="index,follow">

<!-- Site CSS -->
<!--  <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet"> -->
<link href="../css/bootstrap.min.css" rel="stylesheet">
<link
	href="https://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css"
	rel="stylesheet">
<link href="https://static.bootcss.com/www/assets/css/site.min.css?v5"
	rel="stylesheet">
<style>
.job-hot {
	position: absolute;
	color: #d9534f;
	right: 0;
	top: 15px;
}
.col-center-block {  
    float: none;  
    display: block;  
    margin-left: auto;  
    margin-right: auto;  
}  
.eliback {
	color: white;
	background-color: #1E90FF;
	position: relative;
}

.navback {
	background-color: #428bca;
	color: white;
}

.conceptsetbody {
	top: 25px;
}
</style>

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://cdn.bootcss.com/html5shiv/3.7.3/html5shiv.min.js"></script>
      <script src="https://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
<!-- Custom styles for this template -->
<link href="../css/dashboard.css" rel="stylesheet">
<link href="../css/ner.css" rel="stylesheet">
<link
	href="https://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css"
	rel="stylesheet">
<link href="https://static.bootcss.com/www/assets/css/site.min.css?v5"
	rel="stylesheet">
<link rel="stylesheet" href="../css/bootstrap-table.min.css">
<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
<!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
<script src="../js/ie-emulation-modes-warning.js"></script>
<script src="../js/jquery.min.js"></script>
<script src="../js/bootstrap.min.js"></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="../js/ie10-viewport-bug-workaround.js"></script>
<script src="../js/bootstrap-table.min.js"></script>
<script type="text/javascript"
	src="http://malsup.github.io/min/jquery.blockUI.min.js"></script>
</head>
<body>

	<div class="navbar navbar-inverse navbar-fixed-top navback">
		<div class="container">
			<div class="navbar-header">
				<button class="navbar-toggle collapsed" type="button"
					data-toggle="collapse" data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand hidden-sm" href="<%=basePath%>"
					style="color: white"
					onclick="_hmt.push(['_trackEvent', 'navbar', 'click', 'navbar-mainpage'])">Criteria2Query</a>
			</div>
			<div class="navbar-collapse collapse" role="navigation">
				<ul class="nav navbar-nav">
				</ul>
			</div>
		</div>
	</div>
	<div class="container projects">
		<div class="page-header projects-header">
			<h4>JSON Result </h4>
			
		</div>
		
		<!--  <div class="col-sm-12 col-md-12 col-lg-12">	
       
       
        <label class="col-sm-3 col-md-3 col-lg-3">Please input a name for that cohort:</label> 
        <div class="col-sm-4 col-md-4 col-lg-4" style="margin-bottom: 15px">
        <input type="text" class="form-control form-control-lg" id="cohortname" placeholder="e.g. T2DM">
        </div>
        <button id="atlas" type="button" class="btn btn-primary">Check it On ATLAS</button>
        <button id="trans2sql" type="button" class="btn btn-info">Translate to SQL</button>
        </div> -->
		<div class="row">


			<div id="candidates" class="col-sm-12 col-md-12 col-lg-12">
				<!-- <table id="conceptsettable"></table> -->
				 <!--  <label class="col-sm-3 col-md-3 col-lg-3">JSON Result:</label> -->
				<textarea id="jsonresult" rows="50" class="form-control" style="margin-top: 20px"></textarea>
				
			</div>
			
		</div>
	</div>

	<footer class="footer ">
	<div class="container">
		<div class="row footer-top">
			<div class="col-sm-6 col-lg-6">
 				<p><strong>Criteria2query v0.8.6.0</strong></p>
				<p>This website was developed by ChiYuan, Chunhua Weng</p>
			</div>

		</div>


	</div>
	</footer>
	<script type="text/javascript">
  var basePath = "<%=basePath%>";
  var concepturl="http://api.ohdsi.org/WebAPI/conceptset/";
 
  var count;
  var conceptsetname;
  var wholeindex;
  $(function() {
	  initJSON();
	  $("#atlas").click(function(){
			gotoATLAS();
		});
	  $("#trans2sql").click(function(){
		  translate2sql();
		});
	  })
	function gotoATLAS(){
	  //var cohortname = $("#cohortname").val();
	  var timestamp=new Date().getTime();
	  var senddata = {};
	  senddata.name="criteria2query-"+timestamp;;
	  senddata.expressionType="SIMPLE_EXPRESSION";
	  senddata.expression=$("#jsonresult").val();
	  $.ajax({
			type : 'POST',
			url : 'http://api.ohdsi.org/WebAPI/cohortdefinition/',
			contentType: "application/json",
			data :JSON.stringify(senddata),
			dataType : "json",
			async : false,
			success : function(data) {
				//alert(data['id']);
				openNewWin("http://www.ohdsi.org/web/atlas/#/cohortdefinition/"+data['id']);
			}
	  });
	  //window.location.href=basePath + "nlpmethod/jsonpage";
  	}
  function openNewWin(url, title)  
	{  
	    window.open(url);  
	}  
	function initJSON(){
	  $.ajax({
			type : 'GET',
			url : basePath + "nlpmethod/jsonresult",
			data :{},
			dataType : "json",
			success : function(data) {
				$("#jsonresult").val(data['jsonstr']);
				
				//window.location.href=basePath + "nlpmethod/jsonresult";
			}
		});
  }
	function translate2sql(){
		//window.location.href=basePath + "nlpmethod/sqlpage";
		var timestamp=new Date().getTime();
		var cohortname = "criteria2query-"+timestamp;
		  var senddata = {};
		  senddata.name=cohortname;
		  senddata.expressionType="SIMPLE_EXPRESSION";
		  senddata.expression=$("#jsonresult").val();
		  alert('!success!');
		  $.ajax({
				type : 'POST',
				url : 'http://api.ohdsi.org/WebAPI/cohortdefinition/',
				contentType: "application/json",
				data :JSON.stringify(senddata),
				dataType : "json",
				async : false,
				success : function(data) {
					//alert(data['id']);
					//openNewWin("http://www.ohdsi.org/web/atlas/#/cohortdefinition/"+data['id']);
					 $.ajax({
							type : 'PUT',
							url : 'http://api.ohdsi.org/WebAPI/cohortdefinition/'+data['id'],
							contentType: "application/json",
							data :JSON.stringify(senddata),
							dataType : "json",
							success : function(data) {
								
								window.location.href=basePath + "nlpmethod/sqlpage?id="+data['id'];
							}
						});
				}
		  });
	  	}
	</script>
</body>


</html>

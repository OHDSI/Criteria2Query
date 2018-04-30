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
	<!-- Global site tag (gtag.js) - Google Analytics -->
<script async src="https://www.googletagmanager.com/gtag/js?id=UA-118282453-1"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());
  gtag('config', 'UA-118282453-1');
</script>
</head>
	<div class="navbar navbar-inverse navbar-fixed-top navback">
		<div class="container">
			<div class="navbar-header">
				<button class="navbar-toggle collapsed" type="button"
					data-toggle="collapse" data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand hidden-sm" href="../"
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
		<!-- <div class="page-header projects-header">
			
		</div> -->
		<!-- <form role="form">
			<div class="col-sm-12 col-md-12 col-lg-12">
				<label class="col-sm-3 col-md-3 col-lg-3">Please select your database implementation:</label>


				<div class="form-group col-sm-4 col-md-4 col-lg-4">
					<select id="dbtype" class="form-control">
						<option>PostgreSQL</option>
						<option>MSSQL</option>
						<option>Oracle</option>
					</select>
				</div>
				<button id="checkonATLAS" type="button" class="btn btn-success">Check on ATLAS</button>
				<button id="trans2sql" type="button" class="btn btn-primary">Generate SQL</button> 
		</form> -->



			<div id="candidates" class="col-sm-12 col-md-12 col-lg-12">
				<!-- <table id="conceptsettable"></table> -->
				<div class="projects-header page-header">
					<!--   <h2>Critera2Query</h2> -->
					<h4>JSON</h4>
				</div>
				<button id="checkonATLAS" type="button" class="btn btn-success">Check on ATLAS</button>
				<!-- <button id="trans2sql" type="button" class="btn btn-primary">Generate SQL</button>  -->
				<textarea id="jsonresult" rows="25" class="form-control"></textarea>

			</div>

		</div>
	</div>

	<!-- /.container -->
	<footer class="footer foot-wrap">
	<div class="container">
		<div class="row footer-top">
			<div class="col-sm-6 col-lg-6">
			    <p><strong>Criteria2query v0.8.2.1</strong></p>
				<p>This website was developed by Chi Yuan,Patrick Ryan, Yixuan Guo, Chunhua Weng</p>
			</div>
		</div>
	</div>
	</footer>
	<script type="text/javascript">
  var basePath = "../";
  var concepturl="http://api.ohdsi.org/WebAPI/conceptset/";
 
  var count;
  var conceptsetname;
  var wholeindex;
  $(function() {
	  //initPage();
	  init();
	  $("#trans2sql").click(function(){
		  translate2sql();
		  //execsql();
		});
	  $("#checkonATLAS").click(function(){
		  //translate2sql();
		  gotoATLAS();
		});
	  })
	function translate2sql(){
	 var dbtype=$("#dbtype").val();
	  $.ajax({
			type : 'POST',
			url : basePath + "queryformulate/setDBtype",
			data :{
				'dbtype' : dbtype,
			},
			dataType : "json",
			success : function(data) {
				 window.location.href=basePath + "main/sqlpage";	
			},
			error: function(e) { 
				
			} 
		});
	 
  	}
  
    function execsql(){
    	$.blockUI({
			message : '<h3><img src="../img/squares.gif" />Executing SQL...</h3>',
			css : {
				border : '1px solid khaki'
			}
		});
    	//window.location.href=basePath + "nlpmethod/sqlpage";
	   $.ajax({
			type : 'POST',
			url : basePath + "parse/execSQL",
			data :{},
			dataType : "json",
			success : function(data) {
				$(document).ajaxStop($.unblockUI);
				//$("#conceptsetindex0").html(data['patientcount']); 
				//window.location.href=basePath + "nlpmethod/sqlpage";
				
			},
			error: function(e) { 
				
			} 
		});
  	}
    function gotoATLAS(){
  	  $.ajax({
  			type : 'GET',
  			url : basePath + "queryformulate/storeInATLAS",
  			contentType: "application/json",
  			dataType : "json",
  			async : false,
  			success : function(data) {
  				openNewWin("http://www.ohdsi.org/web/atlas/#/cohortdefinition/"+data['id']);
  			}
  	  });
  	  //window.location.href=basePath + "nlpmethod/jsonpage";
    	}
  function init(){
	  $.blockUI({
			message : '<h3><img src="../img/gears.gif" /> Query Formulating...</h3>',
			css : {
				border : '1px solid khaki'
			}
		});
		 $.ajax({
				type : 'GET',
				url : basePath + "queryformulate/formulateCohort",
				data :{},
				dataType : "json",
				success : function(data) {
					$(document).ajaxStop($.unblockUI);
					$("#jsonresult").val(data['jsonResult']);
				},
				error: function(e) { 
					
				} 
			});
		 }
	
	 function openNewWin(url, title)  
		{  
		    window.open(url);  
		}  
	</script>
</body>


</html>

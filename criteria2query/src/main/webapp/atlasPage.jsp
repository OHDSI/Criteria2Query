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
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="../../favicon.ico">

<title>OHDSI Matcher</title>

<!-- Bootstrap core CSS -->
<link href="../css/bootstrap.min.css" rel="stylesheet">

<!-- Custom styles for this template -->
<link href="../css/dashboard.css" rel="stylesheet">
<link rel="stylesheet" href="../css/bootstrap-table.min.css">
<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
<!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
<script src="../js/ie-emulation-modes-warning.js"></script>
<script src="../js/jquery.min.js"></script>
<script src="../js/bootstrap.min.js"></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="../js/ie10-viewport-bug-workaround.js"></script>
<script src="../js/bootstrap-table.min.js"></script>
<script src="../js/bootstrap-table-zh-CN.min.js"></script>
<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="http://cdn.bootcss.com/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body>

	<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="#">OHDSI Matcher</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav navbar-right">
				<li><a href="#">About</a></li>
			</ul>
		</div>
	</div>
	</nav>

	<div class="container-fluid">
		<div class="row">
			<div class="col-sm-3 col-md-2 sidebar">
				<ul class="nav">
					<li>
						<div align="center">
							<span>Input Free-text</span>
						</div>
					</li>
				</ul>
				<ul class="nav">
					<li>&nbsp;</li>
					<div align="center">
						<li><span class="glyphicon glyphicon-arrow-down"></span></li>
					</div>
					<li>&nbsp;</li>
				</ul>
				<ul class="nav">
					<li class="active" >
						<div align="center">
							<span>Generate XML </span>
						</div>
					</li>
				</ul>
				<ul class="nav">
					<li>&nbsp;</li>
					<div align="center">
						<li><span class="glyphicon glyphicon-arrow-down"></span></li>
					</div>
					<li>&nbsp;</li>
				</ul>
				<ul class="nav">
					<li class="active" >
						<div align="center">
							<span>Concept Selection </span>
						</div>
					</li>
				</ul>
				<ul class="nav">
					<li>&nbsp;</li>
					<div align="center">
						<li><span class="glyphicon glyphicon-arrow-down"></span></li>
					</div>
					<li>&nbsp;</li>
				</ul>
				<ul class="nav">
					<li class="active" style="color: blue">
						<div align="center">
							<span>ATLAS Results </span>
						</div>
					</li>
				</ul>
			</div>
			<div id="mainBody" class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
			<input id="123" type="text" class="form-control" value="Search..."/>
			<input id="456" type="text" class="form-control" value="1233"/>
			<button type="button" id="btn_query" class="btn btn-primary" onclick="test()">NextStep</button>
			<iframe id="atlas" name="atlas" src="http://www.ohdsi.org/web/atlas/#/search" width="100%" height="960px"  frameborder="0"  scrolling="No" ></iframe>
				<!-- <h3>Concept Selection</h3>
				<h6>TEst</h6>
				<form id="formSearch" class="form-horizontal">
					<div class="form-group" style="margin-top: 15px">

						<div class="col-sm-4" style="text-align: left;">
							<button type="button" id="btn_query" class="btn btn-primary">Next
								Step</button>
						</div>
					</div>

				</form>
				<table id="tb_departments"></table> -->
				<br><br><br><br><br><br><br><br><br>
				<input>
				</div>
				</div>
			</div>

			<!-- Bootstrap core JavaScript
    ================================================== -->
			<!-- Placed at the end of the document so the pages load faster -->
			<script type="text/javascript">
				$(function() {
					
				});
				
				function test(){
					/* var result4 = $("input:eq(0)").val();  
	                alert("result4 = " + result4);  */ 
	                var f = $("#atlas").contents().find("#querytext").val();
	                alert(f);
				}
				

				
			</script>
</body>
</html>

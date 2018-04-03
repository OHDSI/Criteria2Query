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
						<div align="center" style="color: blue">
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
					<li class="active" >
						<div align="center">
							<span>ATLAS Results </span>
						</div>
					</li>
				</ul>

				
			</div>
			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
				<h4>Please input the NCTID or Criteria</h4>
							<form id="formSearch" class="form-horizontal" action="<%=basePath%>ohdsi/xmlresult">
								<div class="form-group" style="margin-top: 15px">
									<label class="control-label col-sm-1"
										for="txt_search_departmentname">NCTID</label>
									<div class="col-sm-3">
										<input type="text" class="form-control"
											id="txt_search_departmentname">
									</div>
									<div class="col-sm-4" style="text-align: left;">
										<button type="submit" style="margin-left: 50px" id="btn_query"
											class="btn btn-primary">Start Transform</button>
									</div>
								</div>
								
								
								<h5>criteria</h5>		
								<textarea class="form-control" rows="10"></textarea>
							</form>
		
	</div>

	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script type="text/javascript">
  var basePath = "<%=basePath%>";

</script>
</body>
</html>
